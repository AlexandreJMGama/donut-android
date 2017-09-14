package br.edu.ifrn.ead.donutchatifrn.Banco;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ale on 14/09/2017.
 */

public class DBListRoom extends SQLiteOpenHelper {

    public static String NOME_BANCO = "list_room.db";
    public static String TAB = "list_rooms";
    public static String ID = "_id";
    public static String eTAG = "etag";
    static int VERSAO = 2;

    public DBListRoom(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE "+ TAB +" ("+
                ID +" INTEGER PRIMARY KEY, "+
                eTAG+" TEXT)";

        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ TAB);
        onCreate(sqLiteDatabase);
    }
}
