package br.edu.ifrn.ead.donutchatifrn.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.edu.ifrn.ead.donutchatifrn.R;

/**
 * Created by Ale on 21/08/2017.
 */

public class AdapterRooms extends BaseAdapter {

    Context context;
    List<Room> rooms;

    public AdapterRooms(Context context, List<Room> rooms) {
        this.context = context;
        this.rooms = rooms;
    }

    @Override
    public int getCount() {
        return rooms.size();
    }

    @Override
    public Object getItem(int i) {
        return rooms.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Room room = rooms.get(i);
        View returnView = LayoutInflater.from(context).inflate(R.layout.room_list, null);

        ImageView imgRoom = (ImageView) returnView.findViewById(R.id.imgRoomList);
        TextView titleRoom = (TextView) returnView.findViewById(R.id.txtTitleRoomList);
        TextView yearRoom = (TextView) returnView.findViewById(R.id.txtYearRoomList);

        Resources res = context.getResources();
//        TypedArray imagens = res.obtainTypedArray(R.array.imageRoom);
//        imgRoom.setImageDrawable(imagens.getDrawable(0));
        titleRoom.setText(room.title);
        yearRoom.setText(room.year+"."+room.semestre);

        return returnView;
    }
}