package br.edu.ifrn.ead.donutchatifrn.Banco;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Ale on 14/09/2017.
 */

public class ControlEtag {
    private SQLiteDatabase db;
    private DBListRoom DBListRoom;

    public ControlEtag (Context context){
        DBListRoom = new DBListRoom(context);
    }

    public void inserir(int id, String etag){
        ContentValues cv = new ContentValues();
        long resultado;

        db = DBListRoom.getWritableDatabase();
        cv.put(DBListRoom.ID, id);
        cv.put(DBListRoom.eTAG, etag);

        resultado = db.insert(DBListRoom.TAB, null, cv);
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

        db = DBListRoom.getWritableDatabase();

        where = DBListRoom.ID + "=" + id;

        cv.put(DBListRoom.eTAG, etag);

        db.update(DBListRoom.TAB, cv, where,null);
        db.close();
    }

    public Cursor carregar(int id){
        Cursor cursor;
        db = DBListRoom.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM "+ DBListRoom.TAB +" WHERE " + DBListRoom.ID + " = " + id, null);

        if (cursor != null){
            try {
                cursor.moveToFirst();
            }catch (Exception e){
            }
        }

        db.close();

        return cursor;
    }

    public void delete(){
        db = DBListRoom.getWritableDatabase();
        DBListRoom.onUpgrade(db, 1, 2);
        db.close();
    }
}