package br.edu.ifrn.ead.donutchatifrn;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
            finish();
        }

        userInput = (EditText) findViewById(R.id.username);
        passInput = (EditText) findViewById(R.id.password);
        passwordView = (ImageButton) findViewById(R.id.passView);
        btnLogin = (ImageButton) findViewById(R.id.logar);
        Uri uri = Uri.parse("res:///"+ R.drawable.if_intro);
        SimpleDraweeView draweeView = (SimpleDraweeView) findViewById(R.id.introLogo);
        draweeView.setImageURI(uri);

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
                    hideKeyboard();
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

                if (accessToken == null) {
                    return null;
                }else {
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
                alertText("Verifique seus dados!");
            }else {
                //Logado
                userData.inserir(usuario, result, accessToken);
                Intent intent = new Intent(getApplicationContext(), IntroActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private NetworkInfo Conexao() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info;
    }

    public void alertText(String dados) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Erro")
                .setMessage(dados)
                .setIcon(getResources().getDrawable(R.drawable.ic_error_outline_red_24dp))
                .setCancelable(true);
        AlertDialog alerta = builder.create();
        alerta.show();
    }

    protected void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager.isActive()) {
            inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}