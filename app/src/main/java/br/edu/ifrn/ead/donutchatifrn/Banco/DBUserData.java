package br.edu.ifrn.ead.donutchatifrn.Banco;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ale on 21/08/2017.
 */

public class DBUserData extends SQLiteOpenHelper{

    public static String NOME_BANCO = "banco.db";
    public static String TAB = "usuario";
    public static String ID = "_id";
    public static String USERDATA = "userdata";
    public static String USER = "username";
    public static String USERLIST = "userlist";
    public static String ROOMLIST = "roomlist";
    public static String TOKEN = "token";
    static int VERSAO = 1;

    public DBUserData(Context context) {
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