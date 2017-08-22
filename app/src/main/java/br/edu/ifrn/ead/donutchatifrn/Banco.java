package br.edu.ifrn.ead.donutchatifrn;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ale on 21/08/2017.
 */

public class Banco extends SQLiteOpenHelper{

    static String NOME_BANCO = "banco.db";
    static String TAB = "usuario";
    static String ID = "_id";
    static String USERDATA = "userdata";
    static String USER = "username";
    static String USERLIST = "userlist";
    static String ROOMLIST = "roomlist";
    static String TOKEN = "token";
    static int VERSAO = 1;

    public Banco(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE "+ TAB +" ("+
                ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                USERDATA +" TEXT, "+
                USER +" TEXT NOT NULL, "+
                USERLIST +" TEXT, "+
                ROOMLIST +" TEXT, "+
                TOKEN+" TEXT NOT NULL)";

        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ TAB);
        onCreate(sqLiteDatabase);
    }
}