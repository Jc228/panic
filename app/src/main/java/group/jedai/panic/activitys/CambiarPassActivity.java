package group.jedai.panic.activitys;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import group.jedai.panic.R;
import group.jedai.panic.srv.UsuarioSrv;
import group.jedai.panic.utils.Constantes;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CambiarPassActivity extends AppCompatActivity {
    private Retrofit retrofit;
    private EditText txtClvActual;
    private EditText txtClv1;
    private EditText txtClv2;
    private String idUser;
    private Button btnCambiar;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_pass);

        idUser = getIntent().getStringExtra("idUser").toString();
        txtClvActual = (EditText) findViewById(R.id.txtClvActual);
        txtClv1 = (EditText) findViewById(R.id.txtClv1);
        txtClv2 = (EditText) findViewById(R.id.txtClv2);
        btnCambiar = (Button) findViewById(R.id.btnCambiarClv);
        progressDialog = new ProgressDialog(this);

        retrofit = new Retrofit.Builder()
                .baseUrl(Constantes.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void cambiarPass(View view) {
        progressDialog.setMessage("Enviando...");
        progressDialog.show();
        List<String> params = new ArrayList<>();
        final String clvAct = txtClvActual.getText().toString().trim();
        final String clv1 = txtClv1.getText().toString().trim();
        final String clv2 = txtClv2.getText().toString().trim();
        if (TextUtils.isEmpty(clvAct) || TextUtils.isEmpty(clv1) || TextUtils.isEmpty(clv2)) {
            Toast.makeText(this, "Complete todos los campos...", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return;
        } else {
            params.add(idUser);
            params.add(clvAct);
            params.add(clv1);
            params.add(clv2);
            //Cambiar clave
            UsuarioSrv usuarioSrv = retrofit.create(UsuarioSrv.class);
            Call<ResponseBody> usuarioCall = usuarioSrv.cambiarPass(params);
            usuarioCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            Toast.makeText(CambiarPassActivity.this, response.body().string(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(CambiarPassActivity.this, "Ocurrio un error", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });

        }
    }

}
