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