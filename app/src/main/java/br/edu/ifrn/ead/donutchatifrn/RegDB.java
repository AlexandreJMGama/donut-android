package br.edu.ifrn.ead.donutchatifrn;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Ale on 22/08/2017.
 */

class RegDB {
    private SQLiteDatabase db;
    private Banco meuBanco;

    public RegDB (Context context){
        meuBanco = new Banco(context);
    }

    public void inserir(String user, String dados, String token){
        ContentValues cv = new ContentValues();
        long resultado;

        db = meuBanco.getWritableDatabase();
        cv.put(meuBanco.ID, 1);
        cv.put(meuBanco.USERDATA, dados);
        cv.put(meuBanco.USER, user);
        cv.put(meuBanco.USERLIST, "");
        cv.put(meuBanco.ROOMLIST, "");
        cv.put(meuBanco.TOKEN, token);

        meuBanco.onUpgrade(db, 1, 2);
        resultado = db.insert(meuBanco.TAB, null, cv);
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

        db = meuBanco.getWritableDatabase();

        where = meuBanco.ID + "=" + 1;

        if (dados != null){
            cv.put(meuBanco.USERDATA, dados);
        }else if (userList != null){
            cv.put(meuBanco.USERLIST, userList);
        }else if (roomList != null){
            cv.put(meuBanco.ROOMLIST, roomList);
        }else
            return;

        db.update(meuBanco.TAB, cv, where,null);
        db.close();
    }

    public Cursor carregar(){
        Cursor cursor;
        db = meuBanco.getReadableDatabase();
        String[] campos = {meuBanco.USER, meuBanco.USERDATA, meuBanco.TOKEN, meuBanco.USERLIST, meuBanco.ROOMLIST};
        cursor = db.query(meuBanco.TAB, campos, null, null, null, null, null);

        if (cursor != null){
            cursor.moveToNext();
        }
        db.close();

        return cursor;
    }

    public void delete(){
        db = meuBanco.getWritableDatabase();
        meuBanco.onUpgrade(db, 1, 2);
        db.close();
    }
}

class controlRoom {
    private SQLiteDatabase db;
    private BancoRoom bancoRoom;

    public controlRoom (Context context){
        bancoRoom = new BancoRoom(context);
    }

    public void inserir(int smsID, String texto, int userID, int roomID, String data){
        ContentValues cv = new ContentValues();
        long resultado;

        db = bancoRoom.getWritableDatabase();
        cv.put(bancoRoom.SMS_ID, smsID);
        cv.put(bancoRoom.MENSAGEM, texto);
        cv.put(bancoRoom.USER_ID, userID);
        cv.put(bancoRoom.ROOM_ID, roomID);
        cv.put(bancoRoom.SMS_TIME, data);

        resultado = db.insert(bancoRoom.TAB, null, cv);
        db.close();

        if (resultado == -1){
            Log.i("::CHECK", "Erro no BD");
        }else {
            Log.i("::CHECK", "Ok no BD");
        }
    }

    public Cursor carregar(){
        Cursor cursor;
        db = bancoRoom.getReadableDatabase();
        String[] campos = {bancoRoom.SMS_ID, bancoRoom.MENSAGEM, bancoRoom.USER_ID,bancoRoom.ROOM_ID, bancoRoom.SMS_TIME};
        cursor = db.query(bancoRoom.TAB, campos, null, null, null, null, null);

        if (cursor != null){
            cursor.moveToNext();
        }
        db.close();

        return cursor;
    }

    public void delete(){
        db = bancoRoom.getWritableDatabase();
        bancoRoom.onUpgrade(db, 1, 2);
        db.close();
    }
}

class controlEtag {
    private SQLiteDatabase db;
    private BancoListRoom bancoListRoom;

    public controlEtag (Context context){
        bancoListRoom = new BancoListRoom(context);
    }

    public void inserir(int id, String etag){
        ContentValues cv = new ContentValues();
        long resultado;

        db = bancoListRoom.getWritableDatabase();
        cv.put(bancoListRoom.ID, id);
        cv.put(bancoListRoom.eTAG, etag);

        resultado = db.insert(bancoListRoom.TAB, null, cv);
        db.close();

        if (resultado == -1){
            Log.i("::CHECK", "Erro no BD");
        }else {
            Log.i("::CHECK", "Ok no BD");
        }
    }

    public void atualizar(int id, String etag){
        ContentValues cv = new ContentValues();
        String where;

        db = bancoListRoom.getWritableDatabase();

        where = bancoListRoom.ID + "=" + id;

        cv.put(bancoListRoom.eTAG, etag);

        db.update(bancoListRoom.TAB, cv, where,null);
        db.close();
    }

    public Cursor carregar(){
        Cursor cursor;
        db = bancoListRoom.getReadableDatabase();
        String[] campos = {bancoListRoom.ID, bancoListRoom.eTAG};
        cursor = db.query(bancoListRoom.TAB, campos, null, null, null, null, null);

        if (cursor != null){
            cursor.moveToNext();
        }
        db.close();

        return cursor;
    }

    public void delete(){
        db = bancoListRoom.getWritableDatabase();
        bancoListRoom.onUpgrade(db, 1, 2);
        db.close();
    }
}