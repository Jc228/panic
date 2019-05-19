package group.jedai.panic.activitys;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import group.jedai.panic.R;
import group.jedai.panic.dto.Usuario;
import group.jedai.panic.srv.UsuarioSrv;
import group.jedai.panic.utils.Constantes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VerificacionActivity extends AppCompatActivity {
    private String idUser;
    private String mail;
    private String activity;
    private TextView email;
    private EditText codigo;
    private ProgressDialog progressDialog;
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificacion);

        idUser = getIntent().getStringExtra("idUser").toString();
        mail = getIntent().getStringExtra("mail").toString();
        activity = getIntent().getStringExtra("activity".toString());

        progressDialog = new ProgressDialog(this);

        email = (TextView) findViewById(R.id.txtVeri2);
        email.setText(mail);
        codigo = (EditText) findViewById(R.id.txtCodigo);
        retrofit = new Retrofit.Builder()
                .baseUrl(Constantes.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void verificarCodigo(){
        String cod = codigo.getText().toString();
        UsuarioSrv usuarioSrv = retrofit.create(UsuarioSrv.class);
        progressDialog.setMessage("Verificando...");
        progressDialog.show();
        Call<Usuario> user = usuarioSrv.findUserByMailAndCod(mail,cod);
        user.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if(response.isSuccessful()){
                    if(activity.equals("foto")){
                        Toast.makeText(VerificacionActivity.this, "Verificación exitosa, inicie sesión", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplication(), MainActivity.class);
                        startActivity(intent);
                        progressDialog.dismiss();
                    }else if(activity.equals("menu")){
                        Toast.makeText(VerificacionActivity.this, "Verificación exitosa, cuenta activa", Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }

                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(VerificacionActivity.this, "Ocurrio un error", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });

    }

    private void reenviarCodigo() {
        UsuarioSrv usuarioSrv = retrofit.create(UsuarioSrv.class);
        Call<Usuario> usuarioCall = usuarioSrv.reenviarCod(mail);
        usuarioCall.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if(response.isSuccessful()){
                    Usuario user = response.body();
                    if (user!=null){
                        Toast.makeText(VerificacionActivity.this, "Se envio un nuevo codigo a su correo", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(VerificacionActivity.this, "Ocurrio un error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void clicVeri(View view) {
        switch (view.getId()) {
            case R.id.btnVeri:
                verificarCodigo();
                break;
            case R.id.btnReenviarCod:
                reenviarCodigo();
                break;
        }
    }
}
