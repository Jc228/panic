package group.jedai.panic.activitys;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class RegistroActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private EditText txtEmail;
    private EditText txtPassword;
    private EditText txtNombreApel;

    private Spinner spnTipo;
    private Spinner spnSexo;
    private Spinner spnFAcu;
    private ProgressDialog progressDialog;
    private Button btnRegistrar;
    private Boolean registrado;
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        txtEmail = (EditText) findViewById(R.id.txtEmailReg);
        txtPassword = (EditText) findViewById(R.id.txtPasswordReg);
        txtNombreApel = (EditText) findViewById(R.id.txtNombre);
        spnTipo = (Spinner) findViewById(R.id.spntipo);
        spnSexo = (Spinner) findViewById(R.id.spnsexo);
        spnFAcu = (Spinner) findViewById(R.id.spnfacu);
        btnRegistrar = (Button) findViewById(R.id.btnRegistrar);
        registrado = false;
        progressDialog = new ProgressDialog(this);


        String[] tipoPersona = {"Estudiante", "Profesor", "Administrativo", "Guardia"};
        String[] sexo = {"Masculino", "Femenino"};
        String[] facultades = {"Ingenieria", "Administracion", "Derecho", "Medicina"};

        ArrayAdapter<String> opcSexo = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, sexo);
        spnSexo.setAdapter(opcSexo);
        spnSexo.setPrompt("Sexo");

        ArrayAdapter<String> opciones = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, tipoPersona);
        spnTipo.setAdapter(opciones);
        spnTipo.setOnItemSelectedListener(this);
        spnTipo.setPrompt("Personal");

        ArrayAdapter<String> opcFacu = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, facultades);
        spnFAcu.setAdapter(opcFacu);
        spnFAcu.setPrompt("Facultad");
        retrofit = new Retrofit.Builder()
                .baseUrl(Constantes.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private void registrarMongo() {
        UsuarioSrv usuarioSrv = retrofit.create(UsuarioSrv.class);
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        String nombre = txtNombreApel.getText().toString().trim();
        String tipo = spnTipo.getSelectedItem().toString();
        String sexo = spnSexo.getSelectedItem().toString();
        String facu = spnFAcu.getSelectedItem().toString();
//
        if (tipo.equalsIgnoreCase("guardia")) {
//            spnFAcu.setEnabled(false);
            facu = "universidad";
        }
//        else{
//            spnFAcu.setEnabled(true);
//        }

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(nombre) || TextUtils.isEmpty(sexo) || TextUtils.isEmpty(tipo) || TextUtils.isEmpty(facu)) {
            Toast.makeText(this, "Complete todos los campos...", Toast.LENGTH_LONG).show();
            return;
        } else {
            Usuario user = new Usuario(nombre, email, password, "", tipo, sexo, false, facu);//usuario mongo
            progressDialog.setMessage("Registrando...");
            progressDialog.show();
            Call<Usuario> usuarioCall = usuarioSrv.addUser(user);//agregar a mongo
            usuarioCall.enqueue(new Callback<Usuario>() {
                @Override
                public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                    if (response.isSuccessful()) {
                        Usuario user = response.body();

                        Toast.makeText(RegistroActivity.this, "Usuario registrado", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplication(), FotoActivity.class);
                        intent.putExtra("activity","registro");
                        intent.putExtra("idUser", user.getId());
                        intent.putExtra("mail", user.getMail());
                        startActivity(intent);
                        progressDialog.dismiss();

                    }
                }

                @Override
                public void onFailure(Call<Usuario> call, Throwable t) {
                    Toast.makeText(RegistroActivity.this, "Este correo ya se encuentra registrado", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });
        }
    }

    public void clicAction(View view) {
        switch (view.getId()) {
            case R.id.btnRegistrar:
                registrarMongo();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String[] facultades = {"Ingenieria", "Administracion", "Derecho", "Medicina"};
        String[] guardias = {"Universidad"};

        String tipo = spnTipo.getSelectedItem().toString();

        if (tipo.equalsIgnoreCase("guardia")) {
            spnFAcu.setEnabled(false);
//            ArrayAdapter<String> opcFacu = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, guardias);
//            spnFAcu.setAdapter(opcFacu);
        } else {
            spnFAcu.setEnabled(true);
            ArrayAdapter<String> opcFacu = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, facultades);
            spnFAcu.setAdapter(opcFacu);
        }
//        ArrayAdapter<String> opcFacu = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, facultades);
//        spnFAcu.setAdapter(opcFacu);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
