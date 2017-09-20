package br.edu.ifrn.ead.donutchatifrn.Adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.List;

import br.edu.ifrn.ead.donutchatifrn.Banco.ControlUserData;
import br.edu.ifrn.ead.donutchatifrn.Banco.DBUserData;
import br.edu.ifrn.ead.donutchatifrn.R;

/**
 * Created by Ale on 19/09/2017.
 */

public class AdapterChat extends BaseAdapter {

    int myIdUser;
    private Context context;
    private List<Chat> messages;

    public AdapterChat(Context context, List<Chat> messages) {
        this.context = context;
        this.messages = messages;
    }

    public int getCount() {
        return messages.size();
    }

    @Override
    public Chat getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        Chat chat = messages.get(i);
        View returnView;

        orgDados();

        Log.i("::CHECK", "List id:"+chat.userId);
        Log.i("::CHECK", "My id:"+myIdUser);

        if (chat.userId == myIdUser) {
            returnView = LayoutInflater.from(context).inflate(R.layout.item_chat_right, null);
        } else {
            returnView = LayoutInflater.from(context).inflate(R.layout.item_chat_left, null);
        }

        TextView message = (TextView) returnView.findViewById(R.id.txtMsg);
        message.setText(chat.content);

        return returnView;
    }

    public void orgDados(){
        //Organizando dados
        ControlUserData userData = new ControlUserData(context);
        Cursor cursor = userData.carregar();

        try {
            String dados = cursor.getString(cursor.getColumnIndex(DBUserData.USERDATA));
            JSONObject jsonData = new JSONObject(dados);
            myIdUser = jsonData.getInt("id");
        }catch (Exception e){
        }
    }
}
