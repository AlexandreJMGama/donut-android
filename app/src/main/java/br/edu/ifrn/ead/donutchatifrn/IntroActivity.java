package br.edu.ifrn.ead.donutchatifrn;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifrn.ead.donutchatifrn.Adapters.AdapterRooms;
import br.edu.ifrn.ead.donutchatifrn.Adapters.Room;
import br.edu.ifrn.ead.donutchatifrn.Banco.ControlUserData;
import br.edu.ifrn.ead.donutchatifrn.Banco.DBUserData;

public class IntroActivity extends AppCompatActivity {

    ControlUserData userData;
    TextView txtUser;
    List<Room> rooms;
    ListView listView;
    AdapterRooms adapterRooms;
    private String usuario, accessToken, dados, roomList;
    String donutID, name, fullname, email, typeUser, url_pic;
    FloatingActionButton btnFloat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        SimpleDraweeView draweeView = (SimpleDraweeView) findViewById(R.id.my_image_view);
        txtUser = (TextView) findViewById(R.id.txtname);
        btnFloat = (FloatingActionButton) findViewById(R.id.fab);

        orgDados();

        txtUser.setText("Olá "+typeUser+"!\nSeja bem vindo\n"+name);
        Uri uri = Uri.parse("https://suap.ifrn.edu.br"+url_pic);
        draweeView.setImageURI(uri);
        listView = (ListView) findViewById(R.id.lvIntro);
        rooms = new ArrayList<Room>();

        if (usuario != null && accessToken != null){
            if (getRoomList()){
                //
            }else {
                new getRooms().execute();
            }
        }

        btnFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public boolean orgDados(){
        //Organizando dados
        userData = new ControlUserData(getBaseContext());
        Cursor cursor = userData.carregar();

        try {
            usuario = cursor.getString(cursor.getColumnIndex(DBUserData.USER));
            dados = cursor.getString(cursor.getColumnIndex(DBUserData.USERDATA));
            accessToken = cursor.getString(cursor.getColumnIndex(DBUserData.TOKEN));
            //Sem dados
            JSONObject jsonData = new JSONObject(dados);
            donutID = String.valueOf(jsonData.getInt("id"));
            name = jsonData.getString("name");
            fullname = jsonData.getString("fullname");
            email = jsonData.getString("email");
            typeUser = jsonData.getString("category");
            url_pic = jsonData.getString("url_profile_pic");
            //Erro ao converter
            Log.i("::CHECK", "orgDados OK");
            return true;
        }catch (Exception e){
            Log.i("::CHECK", "orgDados Erro");
            return false;
        }
    }

    private class getRooms extends AsyncTask<Object, Object, String> {

        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(IntroActivity.this);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Object... params) {
            try {

                HttpRequest jsonRoom = HttpRequest
                        .get("https://donutchat.herokuapp.com/api/rooms")
                        .header("Authorization", "Token "+accessToken);

                if (jsonRoom.ok())
                    return jsonRoom.body();
                else
                    return null;

            }catch (Exception e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null){
                //get ok
                try {
                    userData.atualizar(null, null, result);
                    getRoomList();
                } catch (SQLiteException e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(getApplicationContext(), "Erro", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    public boolean getRoomList(){

        userData = new ControlUserData(getBaseContext());
        Cursor cursor = userData.carregar();

        try {
            roomList = cursor.getString(cursor.getColumnIndex(DBUserData.ROOMLIST));
            Log.i("::CHECK", "isEmpty? "+roomList.isEmpty());
            if (roomList.isEmpty()){
                return false;
            }else {
                makeRoomList();
                return true;
            }
        }catch (Exception e){
            return false;
        }
    }

    public void makeRoomList(){

        try {
            JSONArray roomArray = new JSONArray(roomList);
            for(int i = 0; i < roomArray.length(); i++){
                JSONObject jsonObj = roomArray.getJSONObject(i);
                String title = jsonObj.getString("title");
                int suap_id = jsonObj.getInt("suap_id");
                int year = jsonObj.getInt("year");
                int semestre = jsonObj.getInt("semester");;
                rooms.add(new Room(i, suap_id, year, semestre, title));

                adapterRooms = new AdapterRooms(getApplicationContext(), rooms);
            }
            listView.setAdapter(adapterRooms);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}