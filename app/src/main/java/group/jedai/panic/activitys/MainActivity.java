package group.jedai.panic.activitys;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import group.jedai.panic.R;
import group.jedai.panic.dto.Usuario;
import group.jedai.panic.srv.UsuarioSrv;
import group.jedai.panic.utils.AdmSession;
import group.jedai.panic.utils.Constantes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private EditText txtEmail;
    private EditText txtPassword;
    private Button btnRegistrar;
    private Button btnLoguear;
    private Button btnOlvido;
    private ProgressDialog progressDialog;

    private AdmSession admSession;
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btnRegistrar = (Button) findViewById(R.id.btnRegistro);
        btnLoguear = (Button) findViewById(R.id.btnLoguear);
        btnOlvido = findViewById(R.id.btnOlvido);
        progressDialog = new ProgressDialog(this);
        admSession = new AdmSession(getApplicationContext());
        if (admSession.logueado()) {
            admSession.redireccionarInicio();
        }
        retrofit = new Retrofit.Builder()
                .baseUrl(Constantes.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    public void loginUsuario() {
        final String email = txtEmail.getText().toString().trim();
        final String password = txtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Complete todos los campos...", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("Ingresando...");
        progressDialog.show();

        //Login de usuario
        UsuarioSrv usuarioSrv = retrofit.create(UsuarioSrv.class);
        Call<Usuario> usuarioCall = usuarioSrv.findUserByLogin(email, password);

        usuarioCall.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Usuario user = response.body();
                    admSession.guardarSesion(true, user);
                    progressDialog.dismiss();
                }
            }
            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Usuario o clave invalidos", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });

    }

    public void crearUser() {
        Intent intent = new Intent(this, RegistroActivity.class);
        startActivity(intent);
    }

    public void olvidoClave() {
                        Toast.makeText(MainActivity.this, "Usuario o clave invalidos", Toast.LENGTH_LONG).show();

//        Intent intent = new Intent(this, ResetearActivity.class);
//        startActivity(intent);
    }


    public void miClicAction(View view) {
        switch (view.getId()) {
            case R.id.btnRegistro:
                crearUser();
                break;
            case R.id.btnLoguear:
                loginUsuario();
                break;
            case R.id.btnOlvido:
                Intent intent = new Intent(this, ResetearActivity.class);
                startActivity(intent);
                break;
        }
    }
}
