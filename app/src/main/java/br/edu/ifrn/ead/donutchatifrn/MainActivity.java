package br.edu.ifrn.ead.donutchatifrn;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private EditText userInput, passInput;
    private Button passwordView;
    private String usuario = null, senha = null, accessToken = null;
    private AlertDialog login, alerta;
    Boolean passVisible = false;
    RegDB regDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);

        regDB = new RegDB(getBaseContext());
        Cursor cursor = regDB.carregar();

        try {
            usuario = cursor.getString(cursor.getColumnIndex(Banco.USER));
            accessToken = cursor.getString(cursor.getColumnIndex(Banco.TOKEN));
        }catch (Exception e){
            //Sem dados
        }

        if (usuario != null && accessToken != null){
            Intent intent = new Intent(this, InfoActivity.class);
            startActivity(intent);
            finish();
        }else {
            alertLogin();
        }
    }

    private void alertLogin(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final View layout = View.inflate(this, R.layout.login_layout, null);

        builder.setView(layout)
                // Add action buttons
                .setPositiveButton(R.string.login, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        hideKeyboard();
                        makeLogin();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });

        userInput = (EditText) layout.findViewById(R.id.username);
        passInput = (EditText) layout.findViewById(R.id.password);
        passwordView = (Button) layout.findViewById(R.id.passView);

        passwordView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                if (passVisible) {
                    passwordView.setBackground(getResources().getDrawable(R.drawable.ic_eye_black_24dp));
                    passInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passInput.setSelection(passInput.length());
                    passVisible = false;
                }else {
                    passwordView.setBackground(getResources().getDrawable(R.drawable.ic_eye_green_24dp));
                    passInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passInput.setSelection(passInput.length());
                    passVisible = true;
                }
            }
        });

        if (usuario != null && senha != null) {
            userInput.setText(usuario);
            passInput.setText(senha);
        }

        builder.setCancelable(false);
        login = builder.create();
        login.show();
    }

    private void makeLogin(){
        usuario = userInput.getText().toString().trim();
        senha = passInput.getText().toString();

        if (Conexao() != null && Conexao().isConnected()){
            new AutenticacaoTask().execute();
        }else {
            Toast.makeText(getApplicationContext(), "Sem conexão!", Toast.LENGTH_LONG).show();
            alertLogin();
        }
    }

    private class AutenticacaoTask extends AsyncTask<Object, Object, String> {

        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(MainActivity.this);
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

                if (accessToken == null){
                    return null;
                }else{
                    return jsonMe.body();
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
                alertLogin();
                alertText("Verifique seus dados!");
            }else {
                //Logado
                regDB.inserir(usuario, result, accessToken);
                Intent intent = new Intent(getApplicationContext(), IntroActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private NetworkInfo Conexao() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
//            NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//            NetworkInfo mb = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return info;
    }

    public void alertText(String dados) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Erro")
                .setMessage(dados)
                .setIcon(getResources().getDrawable(R.drawable.ic_error_outline_red_24dp))
                .setCancelable(true);
        alerta = builder.create();
        alerta.show();
    }

    protected void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager.isActive()) {
            inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}