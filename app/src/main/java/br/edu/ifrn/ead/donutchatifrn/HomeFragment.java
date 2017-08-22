package br.edu.ifrn.ead.donutchatifrn;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    RegDB regDB;
    Button sair;
    View view;
    TextView textView;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        textView = view.findViewById(R.id.txtView);
        textView.setText("Bem vindo!");

        sair = (Button) view.findViewById(R.id.logout);
        sair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regDB = new RegDB(getContext());
                regDB.delete();
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
                System.exit(0);
            }
        });

        return view;
    }

}
