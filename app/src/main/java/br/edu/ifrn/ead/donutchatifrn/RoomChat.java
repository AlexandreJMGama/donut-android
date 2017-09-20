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

    int idRoom, idUser;
    String titleRoom, accessToken = null;
    EditText textFromSend;
    Button btnSend;
    ListView listView;
    List<Chat> chat;
    AdapterChat adapterChat;
    ControlUserData userData;
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

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(titleRoom);
        }

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textFromSend.getText().toString().trim().length() > 0) {
                    chat.add(new Chat(textFromSend.getText().toString().trim(), idUser));
                    adapterChat.notifyDataSetChanged();
                    textFromSend.setText("");
                }
            }
        });
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
        Cursor cursor = userData.carregar();

        try {
            String dados = cursor.getString(cursor.getColumnIndex(DBUserData.USERDATA));
            JSONObject jsonData = new JSONObject(dados);
            idUser = jsonData.getInt("id");
            accessToken = jsonData.getString("token");
            Log.i("::CHECK", accessToken);
        } catch (Exception e) {
        }
    }
}