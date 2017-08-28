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

class BancoRoom extends SQLiteOpenHelper {

    static String NOME_BANCO = "banco_room.db";
    static String TAB = "rooms";
    static String SMS_ID = "id";
    static String MENSAGEM = "content";
    static String USER_ID = "user_id";
    static String ROOM_ID = "room_id";
    static String SMS_TIME = "roomlist";
    static int VERSAO = 1;

    public BancoRoom(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE "+ TAB +" ("+
                SMS_ID +" INTEGER, "+
                MENSAGEM +" TEXT, "+
                USER_ID +" INTEGER, "+
                ROOM_ID +" INTEGER, "+
                SMS_TIME+" TEXT)";

        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

class BancoListRoom extends SQLiteOpenHelper {

    static String NOME_BANCO = "list_room.db";
    static String TAB = "list_rooms";
    static String ID = "_id";
    static String eTAG = "etag";
    static int VERSAO = 1;

    public BancoListRoom(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE "+ TAB +" ("+
                ID +" INTEGER, "+
                eTAG+" TEXT)";

        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ TAB);
        onCreate(sqLiteDatabase);
    }
}