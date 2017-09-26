package br.edu.ifrn.ead.donutchatifrn;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONObject;

import br.edu.ifrn.ead.donutchatifrn.Banco.ControlUserData;
import br.edu.ifrn.ead.donutchatifrn.Banco.DBUserData;
import br.edu.ifrn.ead.donutchatifrn.Banco.ControlEtag;
import br.edu.ifrn.ead.donutchatifrn.Banco.ControlRoom;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserMeFragment extends Fragment {

    ControlUserData userData;
    private String dados;
    String  name, typeUser, url_pic;

    public UserMeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_userme, container, false);
        Fresco.initialize(getContext());

        SimpleDraweeView draweeView = (SimpleDraweeView) view.findViewById(R.id.sdvImage);
        TextView txtUser = (TextView) view.findViewById(R.id.tvname);
        Button btnLogout = (Button) view.findViewById(R.id.btnLogout);

        orgDados();

        txtUser.setText(typeUser+"\n"+name);
        Uri uri = Uri.parse("https://suap.ifrn.edu.br"+url_pic);
        draweeView.setImageURI(uri);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //deleta todos os dados dos bancos
                userData = new ControlUserData(getContext());
                ControlRoom controlRoom = new ControlRoom(getContext());
                ControlEtag controlEtag = new ControlEtag(getContext());
                userData.delete();
                controlRoom.delete();
                controlEtag.delete();

                //fim do delete
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
                System.exit(0);
            }
        });

        return view;
    }

    public void orgDados(){
        //Organizando dados
        userData = new ControlUserData(getContext());
        Cursor cursor = userData.carregar();

        try {
            dados = cursor.getString(cursor.getColumnIndex(DBUserData.USERDATA));
            JSONObject jsonData = new JSONObject(dados);
            name = jsonData.getString("name");
            typeUser = jsonData.getString("category");
            url_pic = jsonData.getString("url_profile_pic");
        }catch (Exception e){
        }
    }
}
