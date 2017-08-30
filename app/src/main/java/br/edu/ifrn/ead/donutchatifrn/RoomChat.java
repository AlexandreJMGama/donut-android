package br.edu.ifrn.ead.donutchatifrn;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

public class RoomChat extends AppCompatActivity implements View.OnClickListener {

    int idRoom;
    String titleRoom;
    AppBarLayout barLayout;
    CollapsingToolbarLayout toolbarLayout;
    Toolbar toolbar;
    boolean expanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_chat);

        Intent intent = getIntent();
        idRoom = intent.getIntExtra("id", -1);
        titleRoom = intent.getStringExtra("title");

        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collap);
        barLayout = (AppBarLayout) findViewById(R.id.appbarchat);
        toolbar = (Toolbar) findViewById(R.id.toolbarchat);

        toolbar.setOnClickListener(this);
        toolbarLayout.setOnClickListener(this);

        toolbar.setTitle(titleRoom);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public void onClick(View view) {
        if (expanded){
            expanded = false;
            barLayout.setExpanded(expanded);
        }else {
            expanded = true;
            barLayout.setExpanded(expanded);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
