package br.edu.ifrn.ead.donutchatifrn.Banco;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ale on 14/09/2017.
 */

public class DBRoomMesseges extends SQLiteOpenHelper {

    public static String NOME_BANCO = "banco_room.db";
    public static String TAB = "rooms";
    public static String SMS_ID = "id";
    public static String MENSAGEM = "content";
    public static String USER_ID = "user_id";
    public static String ROOM_ID = "room_id";
    public static String SMS_TIME = "roomlist";
    static int VERSAO = 1;

    public DBRoomMesseges(Context context) {
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
