package br.edu.ifrn.ead.donutchatifrn;

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

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ale on 03/09/2017.
 */

public class RestService extends Service {

    int countExec = 0;
    RegDB regDB;
    ControlRoom controlRoom;
    ControlEtag controlEtag;
    String accessToken = null;
    String roomList = null;

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

        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

        try {
            accessToken = cursorUser.getString(cursorUser.getColumnIndex(Banco.TOKEN));
            roomList = cursorUser.getString(cursorUser.getColumnIndex(Banco.ROOMLIST));
        }catch (Exception e){
            //Sem dados
        }

        TimeUnit unit = TimeUnit.MINUTES;
        executor.scheduleAtFixedRate(new Worker(), 0, 5, unit);

        return START_STICKY;
    }

    private NetworkInfo Conexao() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info;
    }

    class Worker implements Runnable {

        @Override
        public void run() {

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

            countExec++;
            Log.i("::CHECK1", "Count exec"+ countExec);
        }
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

            Log.i("::CHECK", "Atual ETag:"+eTag);

            try {
                HttpRequest httpData = HttpRequest
                        .get("https://donutchat.herokuapp.com/api/rooms/"+id+"/messages")
                        .header("Authorization", "Token "+accessToken)
                        .header("If-None-Match", eTag);

                ok = httpData.ok();
                neweTag = httpData.eTag();
                Log.i("::CHECK", "length body - "+httpData.body().length());
                return httpData.body();

            }catch (Exception e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(String json) {

            if (ok && eTag.length() > 0){
                controlEtag.atualizar(id, neweTag);
                Log.i("::CHECK", "Atualizando:"+neweTag);
            }else if (ok && eTag == ""){
                controlEtag.inserir(id, neweTag);
                Log.i("::CHECK", "Inserindo:"+neweTag);
            }
        }
    }
}
