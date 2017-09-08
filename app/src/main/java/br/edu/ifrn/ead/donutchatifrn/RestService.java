package br.edu.ifrn.ead.donutchatifrn;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ale on 03/09/2017.
 */

public class RestService extends Service {

    RegDB regDB;
    ControlRoom controlRoom;
    ControlEtag controlEtag;
    String accessToken = null;
    String roomList = null;
    ScheduledThreadPoolExecutor executor;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("::CHECK", "ONCREATE SERVICE");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("::CHECK", "onStartCommand SERVICE");
        regDB = new RegDB(getBaseContext());
        controlRoom = new ControlRoom(getApplicationContext());
        controlEtag = new ControlEtag(getApplicationContext());
        Cursor cursorUser = regDB.carregar();

        executor = new ScheduledThreadPoolExecutor(1);

        try {
            accessToken = cursorUser.getString(cursorUser.getColumnIndex(Banco.TOKEN));
            roomList = cursorUser.getString(cursorUser.getColumnIndex(Banco.ROOMLIST));
        } catch (Exception e) {
            //Sem dados
        }

        TimeUnit unit = TimeUnit.MINUTES;
        executor.scheduleAtFixedRate(new Worker(), 0, 5, unit);
        //chama um runnable sem delay a cada 1 minuto
        Log.i("::CHECK", "\nCount exec " + startId);

            return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.shutdown();
        stopSelf();
        Log.i("::CHECK","STOP SERVICE");
    }

    class Worker implements Runnable {
        @Override
        public void run() {

            Log.i("::CHECK", "Worker");
            if (accessToken != null && Conexao().isConnected()) {
                try {
                    JSONArray roomArray = new JSONArray(roomList);
                    for (int i = 0; i < roomArray.length(); i++) {
                        JSONObject jsonObj = roomArray.getJSONObject(i);
                        int id = jsonObj.getInt("id");
                        new restMessage().execute(id);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private NetworkInfo Conexao() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info;
    }

    private class restMessage extends AsyncTask<Integer, Void, String> {

        String eTag = "", neweTag;
        Boolean ok = false;
        int id;

        @Override
        protected String doInBackground(Integer... idRoom) {
            id = idRoom[0];
            Cursor cursorEtag = controlEtag.carregar(id);

            //pegar o etag
            try {
                eTag = cursorEtag.getString(cursorEtag.getColumnIndex(BancoListRoom.eTAG));
            }catch (Exception e){
                //Sem dados
            }

            try {
                HttpRequest httpData = HttpRequest
                        .get("https://donutchat.herokuapp.com/api/rooms/"+id+"/messages")
                        .header("Authorization", "Token "+accessToken)
                        .header("If-None-Match", eTag);

                ok = httpData.ok();
                neweTag = httpData.eTag();
                return httpData.body();

            }catch (Exception e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(String json) {

            if (ok && eTag.length() > 0){
                //Atualizando
                controlEtag.atualizar(id, neweTag);
                inserirMensagem(json, id, false);
            }else if (ok && eTag == ""){
                //Inserindo
                controlEtag.inserir(id, neweTag);
                inserirMensagem(json, id, true);
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

                    if (idMess > id) {
                        controlRoom.inserir(idMess, mensagem, idUser, idRoom, data);
                    }//senão a mensagem ja existe e n precisa ser adicionada
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
