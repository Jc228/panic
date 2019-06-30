package group.jedai.panic.activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import group.jedai.panic.R;
import group.jedai.panic.dto.EstadoAlerta;
import group.jedai.panic.dto.MotivoAlerta;
import group.jedai.panic.dto.NivelServicio;
import group.jedai.panic.srv.EstadoAlertaSrv;
import group.jedai.panic.srv.MotivoAlertaSrv;
import group.jedai.panic.srv.NivelServicioSrv;
import group.jedai.panic.utils.Constantes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.widget.Toast.LENGTH_LONG;

public class NivelServicioActivity extends AppCompatActivity {
    private Retrofit retrofit;
    String idA;
    RadioGroup radioGroup;
    RadioButton radioButton;
    RadioGroup radioGroup1;
    RadioButton radioButton1;
    String motivo = null;
    String niv = null;
    List<String> estados;
    List<String> motivos;

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

//        estados = getEstados();
//        motivos = getMotivos();
//
//        RadioGroup radEstados = (RadioGroup) findViewById(R.id.radioGroup);
//        RadioGroup radMotivos = (RadioGroup) findViewById(R.id.radioGroup1);
//        for (String est : estados) {
//            RadioButton radEstado = crearRadioButton(est);
//            radioGroup.addView(radEstado);
//        }
//        RadioButton primerRadio = (RadioButton) radioGroup.getChildAt(0);
//        primerRadio.setChecked(true);
//
//        for (String mot : motivos) {
//            RadioButton radMotivo = crearRadioButton(mot);
//            radioGroup1.addView(radMotivo);
//        }
//        RadioButton primerRadio1 = (RadioButton) radioGroup1.getChildAt(0);
//        primerRadio1.setChecked(true);


    }

    public List<String> getEstados() {
        final ArrayList<String> lista = new ArrayList<>();
        int i = 0;
        while (lista.size() == 0) {
            EstadoAlertaSrv estadoAlertaSrv = retrofit.create(EstadoAlertaSrv.class);
            Call<List<EstadoAlerta>> listCall = estadoAlertaSrv.findAllEstado();
            listCall.enqueue(new Callback<List<EstadoAlerta>>() {
                @Override
                public void onResponse(Call<List<EstadoAlerta>> call, Response<List<EstadoAlerta>> response) {
                    if (response.isSuccessful()) {
                        List<EstadoAlerta> list = response.body();
                        for (EstadoAlerta est : list) {
                            lista.add(est.getNm());
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<EstadoAlerta>> call, Throwable t) {

                }
            });

            i++;
        }
        return lista;
    }

    public List<String> getMotivos() {
        final ArrayList<String> lista = new ArrayList<>();
        int i = 0;
        while (lista.size() == 0) {
            MotivoAlertaSrv motivoAlertaSrv = retrofit.create(MotivoAlertaSrv.class);
            Call<List<MotivoAlerta>> motListCall = motivoAlertaSrv.findAllMotivo();
            motListCall.enqueue(new Callback<List<MotivoAlerta>>() {
                @Override
                public void onResponse(Call<List<MotivoAlerta>> call, Response<List<MotivoAlerta>> response) {
                    if (response.isSuccessful()) {
                        List<MotivoAlerta> list = response.body();
                        for (MotivoAlerta mot : list) {
                            lista.add(mot.getNm());
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<MotivoAlerta>> call, Throwable t) {
                    Toast.makeText(NivelServicioActivity.this, "No hay motivos", Toast.LENGTH_SHORT).show();
                }
            });
            i++;
        }
        return lista;
    }

    private RadioButton crearRadioButton(String marca) {
        RadioButton nuevoRadio = new RadioButton(this);
        LinearLayout.LayoutParams params = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.WRAP_CONTENT);
        nuevoRadio.setLayoutParams(params);
        nuevoRadio.setText(marca);
        nuevoRadio.setTag(marca);
        return nuevoRadio;
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
                    Toast.makeText(getApplicationContext(), "Enviado...", LENGTH_LONG).show();
                    NivelServicio niv = response.body();
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<NivelServicio> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Ocurrio un problema", LENGTH_LONG).show();
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
