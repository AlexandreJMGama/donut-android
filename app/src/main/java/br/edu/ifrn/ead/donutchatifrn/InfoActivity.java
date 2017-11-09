package br.edu.ifrn.ead.donutchatifrn;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifrn.ead.donutchatifrn.Banco.ControlUserData;

public class InfoActivity extends AppCompatActivity {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    private String accessToken;
    ControlUserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Intent intent = getIntent();
        accessToken = intent.getStringExtra("token");
        userData = new ControlUserData(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mViewPager = (ViewPager) findViewById(R.id.container);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        //setar os tabs aqui
        tabLayout.addTab(tabLayout.newTab().setText("Salas"));
        tabLayout.addTab(tabLayout.newTab().setText("Usuário"));

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        //setar os fragments aqui
        mSectionsPagerAdapter.addFragment(new RoomsFragment());
        mSectionsPagerAdapter.addFragment(new UserMeFragment());

        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        if (Conexao() != null && Conexao().isConnected()){
            new getUsers().execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.about){
            alertAbout();
        }

        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragments = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment){
            mFragments.add(fragment);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }

    public void alertAbout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("DonutChat")
                .setMessage("DonutChat - IFRN\nBy: Alexandre Jackson")
                .setCancelable(true);
        AlertDialog alerta = builder.create();
        alerta.show();
    }

    private class getUsers extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            HttpRequest jsonUsers = HttpRequest
                    .get("https://donutchat.herokuapp.com/api/users")
                    .header("Authorization", "Token "+accessToken);

            if (jsonUsers.ok())
                return jsonUsers.body();
            else
                return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null){
                //get ok
                try {
                    userData.atualizar(null, result, null);
                    Log.i("::CHECK", "Atualizando usuarios!");
                } catch (SQLiteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private NetworkInfo Conexao() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info;
    }
}