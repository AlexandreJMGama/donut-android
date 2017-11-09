package br.edu.ifrn.ead.donutchatifrn;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONObject;

import br.edu.ifrn.ead.donutchatifrn.Banco.ControlUserData;
import br.edu.ifrn.ead.donutchatifrn.Banco.DBUserData;

public class MainActivity extends AppCompatActivity {

    EditText userInput, passInput;
    ImageButton btnLogin, passwordView;
    String usuario = null, senha = null, accessToken = null;
    Boolean passVisible = false;
    ControlUserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "Roboto-Regular.ttf"); // font from assets: "assets/Roboto-Regular.ttf
        setContentView(R.layout.activity_main);

        userData = new ControlUserData(getBaseContext());
        Cursor cursor = userData.carregar();

        try {
            usuario = cursor.getString(cursor.getColumnIndex(DBUserData.USER));
            accessToken = cursor.getString(cursor.getColumnIndex(DBUserData.TOKEN));
        }catch (Exception e){
            //Sem dados
        }

        if (usuario != null && accessToken != null) {
            Intent intent = new Intent(this, InfoActivity.class);
            intent.putExtra("token", accessToken);
            startActivity(intent);
            finish();
        }

        userInput = (EditText) findViewById(R.id.username);
        passInput = (EditText) findViewById(R.id.password);
        passwordView = (ImageButton) findViewById(R.id.passView);
        btnLogin = (ImageButton) findViewById(R.id.logar);

        passwordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passVisible) {
                    passwordView.setImageResource(R.drawable.ic_visibility_off_black_24dp);
                    passInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passInput.setSelection(passInput.length());
                    passVisible = false;
                }else {
                    passwordView.setImageResource(R.drawable.ic_eye_black_24dp);
                    passInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passInput.setSelection(passInput.length());
                    passVisible = true;
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userInput.getText().toString().trim().length() > 0 && passInput.getText().toString().trim().length() > 0){
                    makeLogin();
                    passInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passInput.setSelection(passInput.length());
                    passVisible = false;
                }else {
                    if (!(userInput.getText().toString().trim().length() > 0)){
                        userInput.setError("Matricula");
                    }
                    if (!(passInput.getText().toString().trim().length() > 0)){
                        passInput.setError("Senha");
                    }
                }
            }
        });
    }

    public void makeLogin() {
        usuario = userInput.getText().toString().trim();
        senha = passInput.getText().toString();

        if (Conexao() != null && Conexao().isConnected()){
            new AutenticacaoTask().execute();
        }else {
            Toast.makeText(getApplicationContext(), "Sem conexão!", Toast.LENGTH_LONG).show();
        }
    }

    private class AutenticacaoTask extends AsyncTask<Object, Object, String> {

        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Object... params) {
            try {

                JSONObject userValues = new JSONObject();
                JSONObject jsonSend = new JSONObject();
                userValues.put("username", usuario);
                userValues.put("password", senha);
                jsonSend.put("user", userValues);
                String jsonStr = jsonSend.toString();

                HttpRequest json = HttpRequest
                        .post("https://donutchat.herokuapp.com/api/auth")
                        .header("Content-Type", "application/json")
                        .send(jsonStr);

                String jsonObject = json.body();

                JSONObject token = new JSONObject(jsonObject);
                accessToken = token.getString("token");

                HttpRequest jsonMe = HttpRequest
                        .get("https://donutchat.herokuapp.com/api/users/me")
                        .header("Authorization", "Token "+accessToken);

                if (jsonMe.ok()) {
                    return jsonMe.body();
                }else {
                    return null;
                }

            }catch (Exception e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            if (result == null){
                //Não logado
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("Erro")
                        .setMessage("Verifique seus dados!")
                        .setIcon(getResources().getDrawable(R.drawable.ic_error_outline_red_24dp))
                        .setCancelable(true);
                AlertDialog alerta = builder.create();
                alerta.show();
            }else {
                //Logado
                userData.inserir(usuario, result, accessToken);
                new getRooms().execute();
            }
        }
    }

    private class getRooms extends AsyncTask<Object, Object, String> {

        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Carregando salas!");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Object... params) {
            try {

                HttpRequest jsonRoom = HttpRequest
                        .get("https://donutchat.herokuapp.com/api/rooms")
                        .header("Authorization", "Token "+accessToken);

                if (jsonRoom.ok())
                    return jsonRoom.body();
                else
                    return null;

            }catch (Exception e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null){
                //get ok
                try {
                    userData.atualizar(null, null, result);
                    new getUsers().execute();
                } catch (SQLiteException e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(getApplicationContext(), "Erro", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    private class getUsers extends AsyncTask<Void, Void, String>{

        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Carregando usuários!");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

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
                    Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                    intent.putExtra("token", accessToken);
                    startActivity(intent);
                    finish();
                } catch (SQLiteException e) {
                    e.printStackTrace();
                }

            }else {
                Toast.makeText(getApplicationContext(), "Erro", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    private NetworkInfo Conexao() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info;
    }
}