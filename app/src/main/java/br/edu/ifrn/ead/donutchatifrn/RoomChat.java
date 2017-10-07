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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifrn.ead.donutchatifrn.Adapters.AdapterChat;
import br.edu.ifrn.ead.donutchatifrn.Adapters.Chat;
import br.edu.ifrn.ead.donutchatifrn.Banco.ControlEtag;
import br.edu.ifrn.ead.donutchatifrn.Banco.ControlRoom;
import br.edu.ifrn.ead.donutchatifrn.Banco.ControlUserData;
import br.edu.ifrn.ead.donutchatifrn.Banco.DBListRoom;
import br.edu.ifrn.ead.donutchatifrn.Banco.DBUserData;

public class RoomChat extends AppCompatActivity {

    int idRoom, myIdUser;
    String titleRoom, accessToken = null;
    EditText textFromSend;
    Button btnSend;
    ListView listView;
    List<Chat> chat;
    AdapterChat adapterChat;
    ControlUserData userData;
    ControlEtag controlEtag;
    ControlRoom controlRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_chat);

        Intent intent = getIntent();
        idRoom = intent.getIntExtra("id", -1);
        titleRoom = intent.getStringExtra("title");

        orgDados();

        textFromSend = (EditText) findViewById(R.id.edtfromsend);
        listView = (ListView) findViewById(R.id.lstMsg);
        btnSend = (Button) findViewById(R.id.send);

        controlRoom = new ControlRoom(getBaseContext());
        chat = controlRoom.carregar(idRoom);

        adapterChat = new AdapterChat(getBaseContext(), chat);
        listView.setAdapter(adapterChat);
        listView.setSelection(adapterChat.getCount() - 1);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(titleRoom);
        }

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textFromSend.getText().toString().trim().length() > 0) {
                    if(info() != null && info().isConnected()) {
                        chat.add(new Chat(textFromSend.getText().toString().trim(), myIdUser));
                        adapterChat.notifyDataSetChanged();
                        new postMessege().execute(textFromSend.getText().toString().trim());
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
        //Organizando dados
        userData = new ControlUserData(getBaseContext());
        controlEtag = new ControlEtag(getBaseContext());
        controlRoom = new ControlRoom(getBaseContext());
        userData = new ControlUserData(getBaseContext());
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

        String eTag = "", neweTag;
        Boolean ok = false;

        @Override
        protected String doInBackground(Void... obj) {

            Cursor cursorEtag = controlEtag.carregar(idRoom);

            try {
                eTag = cursorEtag.getString(cursorEtag.getColumnIndex(DBListRoom.eTAG));
            }catch (Exception e){
                //Sem dados
            }

            HttpRequest httpData = HttpRequest
                    .get("https://donutchat.herokuapp.com/api/rooms/"+idRoom+"/messages")
                    .header("Authorization", "Token "+accessToken)
                    .header("If-None-Match", eTag);

            ok = httpData.ok();
            neweTag = httpData.eTag();
            return httpData.body();

        }

        @Override
        protected void onPostExecute(String json) {
            if (ok && eTag.length() > 0){
                //Atualizando
                controlEtag.atualizar(idRoom, neweTag);
                inserirMensagem(json, idRoom, false);
            }else if (ok && eTag == ""){
                //Inserindo
                controlEtag.inserir(idRoom, neweTag);
                inserirMensagem(json, idRoom, true);
            }
        }
    }

    public void inserirMensagem (String json, int id, boolean isNew){
        if (isNew){
            try {
                JSONArray roomArray = new JSONArray(json);
                for (int i = 0; i < roomArray.length(); i++) {
                    JSONObject jsonObj = roomArray.getJSONObject(i);
                    int idMess = jsonObj.getInt("id");
                    String mensagem = jsonObj.getString("content");
                    int idUser = jsonObj.getInt("user_id");
                    int idRoom = jsonObj.getInt("room_id");
                    String data = jsonObj.getString("created_at");
                    controlRoom.inserir(idMess, mensagem, idUser, idRoom, data);
                    chat.add(new Chat(mensagem, idUser));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            //verificar(carregar) ate q mensagem foi adicionada e so adicionar as novas
            int lastId = controlRoom.carregarUltimoId(id);
            try {
                JSONArray roomArray = new JSONArray(json);
                for (int i = 0; i < roomArray.length(); i++) {
                    JSONObject jsonObj = roomArray.getJSONObject(i);
                    int idMess = jsonObj.getInt("id");
                    String mensagem = jsonObj.getString("content");
                    int idUser = jsonObj.getInt("user_id");
                    int idRoom = jsonObj.getInt("room_id");
                    String data = jsonObj.getString("created_at");

                    if (idMess > lastId) {
                        controlRoom.inserir(idMess, mensagem, idUser, idRoom, data);
                        chat.add(new Chat(mensagem, idUser));
                    }//senão a mensagem ja existe e n precisa ser adicionada
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adapterChat.notifyDataSetChanged();
    }

    private class postMessege extends AsyncTask<String, Void , String>{
        String idSala = String.valueOf(idRoom);

        @Override
        protected String doInBackground(String... strings) {
            String mensagem = strings[0];

            try {
                JSONObject userValues = new JSONObject();
                JSONObject jsonSend = new JSONObject();
                userValues.put("content", mensagem);
                userValues.put("room_id", idSala);
                jsonSend.put("message", userValues);
                String jsonStr = jsonSend.toString();

                HttpRequest httpData = HttpRequest
                        .post("https://donutchat.herokuapp.com/api/messages/create")
                        .header("Authorization", "Token "+accessToken)
                        .header("Content-Type", "application/json")
                        .send(jsonStr);

                return httpData.body();
            }catch (Exception e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("::CHECK", result);
            try{
                JSONObject jsonObj = new JSONObject(result);
                int idMess = jsonObj.getInt("id");
                String mensagem = jsonObj.getString("content");
                int idUser = jsonObj.getInt("user_id");
                int idRoom = jsonObj.getInt("room_id");
                String data = jsonObj.getString("created_at");
                controlRoom.inserir(idMess, mensagem, idUser, idRoom, data);
            }catch (Exception e){

            }
            new getMesseges().execute();
        }
    }

    private NetworkInfo info() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        //info.isConnected() && info != null;
        return info;
    }
}