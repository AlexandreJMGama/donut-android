package br.edu.ifrn.ead.donutchatifrn;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RoomsFragment extends Fragment {

    View view;
    RegDB regDB;
    String roomList;
    List<Room> rooms;
    ListView listView;
    AdapterRooms adapterRooms;

    public RoomsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_rooms, container, false);

        listView = (ListView) view.findViewById(R.id.listMain);
        rooms = new ArrayList<Room>();

        getRoomList();

        /*
                regDB = new RegDB(getContext());
                regDB.delete();
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
                System.exit(0);
                ATUAL LOGOUT;
     */

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Room roomP = (Room) adapterView.getItemAtPosition(i);
//                Toast.makeText(getContext(), roomP.id+" - "+ roomP.title, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getContext(), RoomChat.class);
                intent.putExtra("id", roomP.id);
                intent.putExtra("title", roomP.title);
                startActivity(intent);

            }
        });

        return view;
    }

    public boolean getRoomList(){

        regDB = new RegDB(getContext());
        Cursor cursor = regDB.carregar();

        try {
            roomList = cursor.getString(cursor.getColumnIndex(Banco.ROOMLIST));
            Log.i("::CHECK", "isEmpty? "+roomList.isEmpty());
            if (roomList.isEmpty()){
                return false;
            }else {
                makeRoomList();
                return true;
            }
        }catch (Exception e){
            return false;
        }
    }

    public void makeRoomList(){

        try {
            JSONArray roomArray = new JSONArray(roomList);
            for(int i = 0; i < roomArray.length(); i++){
                JSONObject jsonObj = roomArray.getJSONObject(i);
                String title = jsonObj.getString("title");
                //Usar o id abaixo para pegar o id da sala do servidor diferente do id no introActivity que se incrementa so com o laço
                int id = jsonObj.getInt("id");
                int suap_id = jsonObj.getInt("suap_id");
                int year = jsonObj.getInt("year");
                int semestre = jsonObj.getInt("semester");
                rooms.add(new Room(id, suap_id, year, semestre, title));

                adapterRooms = new AdapterRooms(getContext(), rooms);
            }
            listView.setAdapter(adapterRooms);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
