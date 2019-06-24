package group.jedai.panic.activitys;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ResetearActivity extends AppCompatActivity {
    private Retrofit retrofit;
    private EditText txtEmail;
    private Button btnEnviar;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetear);
        txtEmail = (EditText) findViewById(R.id.editTextMail);
        btnEnviar = (Button) findViewById(R.id.button2Mail);
        progressDialog = new ProgressDialog(this);

        retrofit = new Retrofit.Builder()
                .baseUrl(Constantes.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void resetPass(View view) {
        progressDialog.setMessage("Enviando...");
        progressDialog.show();
        final String email = txtEmail.getText().toString().trim();
        //Reset clave
        UsuarioSrv usuarioSrv = retrofit.create(UsuarioSrv.class);
        Call<Usuario> usuarioCall = usuarioSrv.resetPass(email);

        usuarioCall.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ResetearActivity.this, "Su nueva clave ha sido enviada a su correo", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(ResetearActivity.this, "Correo invalido", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }

}
