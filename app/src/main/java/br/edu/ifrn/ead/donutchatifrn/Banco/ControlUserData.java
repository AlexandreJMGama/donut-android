package br.edu.ifrn.ead.donutchatifrn.Banco;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Ale on 22/08/2017.
 */

public class ControlUserData {
    private SQLiteDatabase db;
    private DBUserData meuDBUserData;

    public ControlUserData(Context context){
        meuDBUserData = new DBUserData(context);
    }

    public void inserir(String user, String dados, String token){
        ContentValues cv = new ContentValues();
        long resultado;

        db = meuDBUserData.getWritableDatabase();
        cv.put(meuDBUserData.ID, 1);
        cv.put(meuDBUserData.USERDATA, dados);
        cv.put(meuDBUserData.USER, user);
        cv.put(meuDBUserData.USERLIST, "");
        cv.put(meuDBUserData.ROOMLIST, "");
        cv.put(meuDBUserData.TOKEN, token);

        meuDBUserData.onUpgrade(db, 1, 2);
        resultado = db.insert(meuDBUserData.TAB, null, cv);
        db.close();

        if (resultado == -1){
            Log.i("::CHECK", "Erro no BD");
        }else {
            Log.i("::CHECK", "Ok no BD");
        }
    }

    public void atualizar(String dados, String userList, String roomList){
        ContentValues cv = new ContentValues();
        String where;

        db = meuDBUserData.getWritableDatabase();

        where = meuDBUserData.ID + "=" + 1;

        if (dados != null){
            cv.put(meuDBUserData.USERDATA, dados);
        }else if (userList != null){
            cv.put(meuDBUserData.USERLIST, userList);
        }else if (roomList != null){
            cv.put(meuDBUserData.ROOMLIST, roomList);
        }else
            return;

        db.update(meuDBUserData.TAB, cv, where,null);
        db.close();
    }

    public Cursor carregar(){
        Cursor cursor;
        db = meuDBUserData.getReadableDatabase();
        String[] campos = {meuDBUserData.USER, meuDBUserData.USERDATA, meuDBUserData.TOKEN, meuDBUserData.USERLIST, meuDBUserData.ROOMLIST};
        cursor = db.query(meuDBUserData.TAB, campos, null, null, null, null, null);

        if (cursor != null){
            cursor.moveToNext();
        }
        db.close();

        return cursor;
    }

    public void delete(){
        db = meuDBUserData.getWritableDatabase();
        meuDBUserData.onUpgrade(db, 1, 2);
        db.close();
    }

    public String currentUser(int idUser){
        Cursor cursor = carregar();
        String userName = "";
        try {
            String json = cursor.getString(cursor.getColumnIndex(DBUserData.USERLIST));

            JSONArray roomArray = new JSONArray(json);
            for (int i = 0; i < roomArray.length(); i++) {
                JSONObject jsonObj = roomArray.getJSONObject(i);
                int jsonID = jsonObj.getInt("id");
                String jsonName = jsonObj.getString("name");
                if (idUser == jsonID){
                    userName = jsonName;
                    break;
                }
            }
        }catch (Exception e){

        }
        return userName;
    }
}