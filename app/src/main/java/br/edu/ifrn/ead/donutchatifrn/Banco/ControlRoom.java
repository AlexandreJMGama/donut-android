package br.edu.ifrn.ead.donutchatifrn.Banco;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Ale on 14/09/2017.
 */

public class ControlRoom {
    private SQLiteDatabase db;
    private DBRoomMesseges DBRoomMesseges;

    public ControlRoom (Context context){
        DBRoomMesseges = new DBRoomMesseges(context);
    }

    public void inserir(int smsID, String texto, int userID, int roomID, String data){
        ContentValues cv = new ContentValues();
        long resultado;

        db = DBRoomMesseges.getWritableDatabase();
        cv.put(DBRoomMesseges.SMS_ID, smsID);
        cv.put(DBRoomMesseges.MENSAGEM, texto);
        cv.put(DBRoomMesseges.USER_ID, userID);
        cv.put(DBRoomMesseges.ROOM_ID, roomID);
        cv.put(DBRoomMesseges.SMS_TIME, data);

        resultado = db.insert(DBRoomMesseges.TAB, null, cv);
        db.close();

        if (resultado == -1){
            Log.i("::CHECK", "Erro no BD");
        }else {
            Log.i("::CHECK", "Ok no BD");
        }
    }

    public Cursor carregar(){
        Cursor cursor;
        db = DBRoomMesseges.getReadableDatabase();
        String[] campos = {DBRoomMesseges.SMS_ID, DBRoomMesseges.MENSAGEM, DBRoomMesseges.USER_ID, DBRoomMesseges.ROOM_ID, DBRoomMesseges.SMS_TIME};
        cursor = db.query(DBRoomMesseges.TAB, campos, null, null, null, null, null);

        if (cursor != null){
            cursor.moveToNext();
        }
        db.close();

        return cursor;
    }

    public int carregarUltimoId(int idRoom){
        db = DBRoomMesseges.getReadableDatabase();
        int id = 1;
        Cursor cursor = db.rawQuery("SELECT "+ DBRoomMesseges.SMS_ID +" FROM "+ DBRoomMesseges.TAB +" WHERE " + DBRoomMesseges.ROOM_ID + " = " + idRoom, null);

        if (cursor != null){
            try {
                cursor.moveToLast();
                id = cursor.getInt(cursor.getColumnIndex(DBRoomMesseges.SMS_ID));
            }catch (Exception e){
            }
        }
        db.close();

        return id;
    }

    public void delete(){
        db = DBRoomMesseges.getWritableDatabase();
        DBRoomMesseges.onUpgrade(db, 1, 2);
        db.close();
    }
}
