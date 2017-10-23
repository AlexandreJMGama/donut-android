package br.edu.ifrn.ead.donutchatifrn;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hosopy.actioncable.ActionCable;
import com.hosopy.actioncable.ActionCableException;
import com.hosopy.actioncable.Channel;
import com.hosopy.actioncable.Consumer;
import com.hosopy.actioncable.Subscription;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.edu.ifrn.ead.donutchatifrn.Adapters.AdapterChat;
import br.edu.ifrn.ead.donutchatifrn.Adapters.Chat;
import br.edu.ifrn.ead.donutchatifrn.Banco.ControlRoom;
import br.edu.ifrn.ead.donutchatifrn.Banco.ControlUserData;
import br.edu.ifrn.ead.donutchatifrn.Banco.DBUserData;

public class RoomChat extends AppCompatActivity {

    int idRoom, myIdUser;
    String titleRoom, strIdRoom, accessToken = null;
    EditText textFromSend;
    Button btnSend;
    ListView listView;
    List<Chat> chat;
    AdapterChat adapterChat;
    ControlUserData userData;
    ControlRoom controlRoom;
    URI uri = null;
    Channel chatChannel;
    Subscription subscription;
    boolean isConnected = false;
    ImageView statusDraw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_chat);

        Intent intent = getIntent();
        idRoom = intent.getIntExtra("id", -1);
        strIdRoom = String.valueOf(idRoom);
        titleRoom = intent.getStringExtra("title");

        textFromSend = (EditText) findViewById(R.id.edtfromsend);
        listView = (ListView) findViewById(R.id.lstMsg);
        btnSend = (Button) findViewById(R.id.send);
        statusDraw = (ImageView) findViewById(R.id.statusdraw);

        orgDados();
        setupConection();
        mudarLista();

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(titleRoom);
        }

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textSend = textFromSend.getText().toString().trim();
                if (textSend.length() > 0) {
                    if(info() != null && info().isConnected() && isConnected) {

                        JsonObject userValues = new JsonObject();
                        userValues.addProperty("content", textSend);
                        userValues.addProperty("room_id", strIdRoom);

                        subscription.perform("send_message", userValues);
                        textFromSend.setText("");
                    }else {
                        Toast.makeText(RoomChat.this, "Verifique sua conexão!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        if(info() != null && info().isConnected()) {
            new getMesseges().execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.about_room) {

        } else if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public void orgDados() {
        userData = new ControlUserData(this);
        controlRoom = new ControlRoom(this);
        Cursor cursor = userData.carregar();

        try {
            String dados = cursor.getString(cursor.getColumnIndex(DBUserData.USERDATA));
            JSONObject jsonData = new JSONObject(dados);
            myIdUser = jsonData.getInt("id");
            accessToken = jsonData.getString("token");
            Log.i("::CHECK", accessToken);
        } catch (Exception e) {
        }
    }

    private class getMesseges extends AsyncTask<Void, Void, String>{

        Boolean ok = false;

        @Override
        protected String doInBackground(Void... obj) {

        HttpRequest httpData = HttpRequest
                .get("https://donutchat.herokuapp.com/api/rooms/"+idRoom+"/messages")
                .header("Authorization", "Token "+accessToken);

        ok = httpData.ok();
        return httpData.body();

        }

        @Override
        protected void onPostExecute(String json) {
            if (ok) {
                inserirMensagem(json);
                Log.i("::CHECK", "onPost");
            }
        }
    }

    public void inserirMensagem (String json){
        try {
            JSONArray roomArray = new JSONArray(json);
            for (int i = 0; i < roomArray.length(); i++) {
                JSONObject jsonObj = roomArray.getJSONObject(i);
                int idMess = jsonObj.getInt("id");
                String mensagem = jsonObj.getString("content");
                int idUser = jsonObj.getInt("user_id");
                int idRoom = jsonObj.getInt("room_id");
                String data = jsonObj.getString("created_at");
                if(!controlRoom.hasMessege(idRoom, idMess)) {
                    Log.i("::CHECK", "inserir AND chat add");
                    controlRoom.inserir(idMess, mensagem, idUser, idRoom, data);
                    chat.add(new Chat(mensagem, idUser));
                }
            }
            mudarLista();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private NetworkInfo info() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        //info.isConnected() && info != null;
        return info;
    }

    private void setupConection(){

        try {
            uri = new URI("https://donutchat.herokuapp.com/cable");
            Log.i("::CHECK", uri.toString());
        }catch (Exception e){
        }

        Consumer.Options options = new Consumer.Options();
        options.reconnection = true;

        Map<String, String> headers = new HashMap<>();
        headers.put("token", accessToken);
        options.headers = headers;

        Consumer consumer = ActionCable.createConsumer(uri, options);

        chatChannel = new Channel("ChatRoomsChannel");
        chatChannel.addParam("room_id", strIdRoom);
        subscription = consumer.getSubscriptions().create(chatChannel);

        subscription
            .onConnected(new Subscription.ConnectedCallback() {
                @Override
                public void call() {
                    Log.i("::CHECK", "onConnected");
                    altStatus(0);
                }
            }).onRejected(new Subscription.RejectedCallback() {
                @Override
                public void call() {
                    Log.i("::CHECK", "RejectedCallback");
                    altStatus(3);
                }
            }).onReceived(new Subscription.ReceivedCallback() {
                @Override
                public void call(JsonElement data) {
                    Log.i("::CHECK", "onReceived");
                    Log.i("::CHECK", data.toString());
                    novaMensagem(data.toString());
                    altStatus(4);
                }
            }).onDisconnected(new Subscription.DisconnectedCallback() {
                @Override
                public void call() {
                    Log.i("::CHECK", "onDisconnected");
                    altStatus(1);
                }
            }).onFailed(new Subscription.FailedCallback() {
                @Override
                public void call(ActionCableException e) {
                    Log.i("::CHECK", "onFailed");
                    Log.i("::CHECK", e.getMessage());
                    altStatus(2);
                }
            });

        consumer.connect();
    }

    public void novaMensagem(String result){
        try {
            JSONObject jsonAll = new JSONObject(result);
            String allMessage = jsonAll.getString("message");
            JSONObject jsonObj = new JSONObject(allMessage);
            int idMess = jsonObj.getInt("id");
            String mensagem = jsonObj.getString("content");
            int idUser = jsonObj.getInt("user_id");
            int idRoom = jsonObj.getInt("room_id");
            String sendData = jsonObj.getString("created_at");
            controlRoom.inserir(idMess, mensagem, idUser, idRoom, sendData);
            chat.add(new Chat(mensagem, idUser));
            mudarLista();
        }catch (Exception e){
        }
    }

    public void mudarLista(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (adapterChat == null){
                    chat = controlRoom.carregar(idRoom);
                    adapterChat = new AdapterChat(getBaseContext(), chat);
                    listView.setAdapter(adapterChat);
                    Log.i("::CHECK", "ADAPT NULL");
                }else {
                    adapterChat.notifyDataSetChanged();
                    Log.i("::CHECK", "ADAPT notify");
                }
                listView.setSelection(adapterChat.getCount() - 1);
                Log.i("::CHECK", "LIST SELECT");
            }
        });
    }

    public void altStatus(final int i){
        isConnected = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (i) {
                    case 0:
                        isConnected = true;
                        statusDraw.setImageDrawable(getResources().getDrawable(R.drawable.status_on));
                        break;
                    case 1:
                        statusDraw.setImageDrawable(getResources().getDrawable(R.drawable.status_off));
                        break;
                    case 2:
                        statusDraw.setImageDrawable(getResources().getDrawable(R.drawable.failed_animation));
                        break;
                    case 3:
                        statusDraw.setImageDrawable(getResources().getDrawable(R.drawable.status_rejected));
                        break;
                    case 4:
                        isConnected = true;
                        statusDraw.setImageDrawable(getResources().getDrawable(R.drawable.received_animation));
                        break;
                }
            }
        });
    }
}