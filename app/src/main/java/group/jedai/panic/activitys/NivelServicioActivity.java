package group.jedai.panic.activitys;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import group.jedai.panic.R;
import group.jedai.panic.dto.NivelServicio;
import group.jedai.panic.srv.NivelServicioSrv;
import group.jedai.panic.utils.Constantes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NivelServicioActivity extends AppCompatActivity {
    private Retrofit retrofit;
    String idA;
    RadioGroup radioGroup;
    RadioButton radioButton;
    RadioGroup radioGroup1;
    RadioButton radioButton1;
    String motivo = null;
    String niv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nivel_servicio);
        retrofit = new Retrofit.Builder()
                .baseUrl(Constantes.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        idA = getIntent().getStringExtra("idA").toString();
        radioGroup = findViewById(R.id.radioGroup);
        radioGroup1 = findViewById(R.id.radioGroup1);

    }


    public void enviarNivServicio() {
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        motivo = radioButton.getText().toString();

        int radioId1 = radioGroup1.getCheckedRadioButtonId();
        radioButton1 = findViewById(radioId1);
        niv = radioButton1.getText().toString();

        NivelServicio nivelServicio = new NivelServicio(idA, motivo, niv);
        NivelServicioSrv nivelServicioSrv = retrofit.create(NivelServicioSrv.class);
        Call<NivelServicio> nivelServicioCall = nivelServicioSrv.saveNivServ(nivelServicio);
        nivelServicioCall.enqueue(new Callback<NivelServicio>() {
            @Override
            public void onResponse(Call<NivelServicio> call, Response<NivelServicio> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Enviado...", Toast.LENGTH_LONG).show();
                    NivelServicio niv = response.body();
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<NivelServicio> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Ocurrio un problema", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void clicAction(View view) {
        switch (view.getId()) {
            case R.id.enviarNiv:
                enviarNivServicio();
                break;
        }
    }

}
