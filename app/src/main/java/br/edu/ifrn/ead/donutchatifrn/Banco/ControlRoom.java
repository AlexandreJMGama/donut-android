package br.edu.ifrn.ead.donutchatifrn.Banco;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.edu.ifrn.ead.donutchatifrn.Adapters.Chat;

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
            Log.i("::CHECK", "Erro no BD - inserir");
        }else {
            Log.i("::CHECK", "Ok no BD");
        }
    }

    public List carregar(int idRoom){
        db = DBRoomMesseges.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ DBRoomMesseges.TAB +" WHERE "+ DBRoomMesseges.ROOM_ID +" = "+ idRoom, null);

        List<Chat> lista = new ArrayList();

        if (cursor != null) {
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                String content = cursor.getString(cursor.getColumnIndex(DBRoomMesseges.MENSAGEM));
                int userId = cursor.getInt(cursor.getColumnIndex(DBRoomMesseges.USER_ID));
                lista.add(new Chat(content, userId));
                cursor.moveToNext();
            }
        }

        db.close();

        return lista;
    }

    public boolean hasMessege(int idRoom, int idMess){
        db = DBRoomMesseges.getReadableDatabase();
        boolean has = false;
        Cursor cursor = db.rawQuery("SELECT * FROM "+ DBRoomMesseges.TAB +" WHERE " + DBRoomMesseges.ROOM_ID + " = ?"  +" AND "+ DBRoomMesseges.SMS_ID + " = ?", new String[]{String.valueOf(idRoom), String.valueOf(idMess)});

        if (cursor != null){
            try {
                cursor.moveToFirst();
                //Log nessesario para verificação, se n tiver o item da erro e has n fica true
                int txt = cursor.getInt(cursor.getColumnIndex(DBRoomMesseges.ROOM_ID));
                Random r = new Random();
                Log.i("::CHECK", "true-"+txt*r.nextInt());
                has = true;
            }catch (Exception e){
            }
        }
        db.close();

        return has;
    }

    public void delete(){
        db = DBRoomMesseges.getWritableDatabase();
        DBRoomMesseges.onUpgrade(db, 1, 2);
        db.close();
    }


}
