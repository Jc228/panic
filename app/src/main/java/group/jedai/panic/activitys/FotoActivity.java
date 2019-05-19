package group.jedai.panic.activitys;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import group.jedai.panic.R;
import group.jedai.panic.srv.FotoSrv;
import group.jedai.panic.utils.Constantes;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FotoActivity extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_SELECT = 2;
    int codigo;

    String mCurrentPhotoPath;
    private String idUser;
    private String email;
    private String activity;
    private ImageView img;
    private Button btnSubir;
    private Button btnTomar;
    private Uri photoUri;
    private ProgressDialog progressDialog;

    private Retrofit retrofit;
    File imagenGaleria = null;
    File imagen = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto);

        img = (ImageView) findViewById(R.id.fotoView);
        idUser = getIntent().getStringExtra("idUser").toString();
        email = getIntent().getStringExtra("mail").toString();
        activity = getIntent().getStringExtra("activity").toString();
        btnSubir = (Button) findViewById(R.id.buttonSubir);
        btnTomar = (Button) findViewById(R.id.btnTomar);
        progressDialog = new ProgressDialog(this);

        if (activity.equals("menu")) {
            btnSubir.setText("Guardar");
        }

        if (ContextCompat.checkSelfPermission(FotoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FotoActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FotoActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1000);
        }
        retrofit = new Retrofit.Builder()
                .baseUrl(Constantes.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private void cargarImagen(final View view) {
        final CharSequence[] opciones = {"Tomar Foto", "Cargar Imagen"};
        final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(FotoActivity.this);
        alertOpciones.setTitle("Seleccionar una opcion:");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        tomarFoto(view);
                        break;
                    case 1:
                        subirFoto();
                        break;
                }
            }
        });
        alertOpciones.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertOpciones.show();
    }

    private File createImage() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "Backup_" + idUser + "_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        imagen = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = imagen.getAbsolutePath();
        return imagen;
    }

    public void tomarFoto(View view) {
        Intent takePictures = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictures.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                codigo = REQUEST_TAKE_PHOTO;
                takePictures.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictures, REQUEST_TAKE_PHOTO);
            }
        }
    }

    public void subirFoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        codigo = REQUEST_IMAGE_SELECT;
        startActivityForResult(intent.createChooser(intent, "Seleccione la Aplicacion"), REQUEST_IMAGE_SELECT);
    }

    public void clicAction(View view) {
        switch (view.getId()) {
            case R.id.btnTomar:
//                tomarFoto(view);
                cargarImagen(view);
                break;
            case R.id.buttonSubir:
                subirImg();
                break;
        }
    }

    public void grabImage(ImageView imageView) {
        this.getContentResolver().notifyChange(photoUri, null);
        ContentResolver cr = this.getContentResolver();
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(cr, photoUri);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar foto", Toast.LENGTH_SHORT).show();
            Log.d("Error", "No se pudo cargar foto", e);
        }
    }

    public void subirImg() {
//        if((imagen == null) | (imagenGaleria == null)) {
//            Toast.makeText(this, "Cargue una foto...", Toast.LENGTH_LONG).show();
//            return;
//        }else{
        RequestBody requestBody = null;
        MultipartBody.Part body = null;
        //id usuario
        RequestBody id = RequestBody.create(MultipartBody.FORM, idUser);
        if (codigo == 1) {
            //imagen
            requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), imagen);
            body = MultipartBody.Part.createFormData("foto", imagen.getName(), requestBody);
        } else if (codigo == 2) {
            //imagen
            requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), imagenGaleria);
            body = MultipartBody.Part.createFormData("foto", imagenGaleria.getName(), requestBody);
        }

        FotoSrv fotoSrv = retrofit.create(FotoSrv.class);
        progressDialog.setMessage("Subiendo foto...");
        progressDialog.show();
        Call<ResponseBody> datosFoto = fotoSrv.enviarFoto(body, id);
        datosFoto.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String resp = null;
                    try {
                        resp = response.body().string();
                        if (resp.equalsIgnoreCase("guardado")) {
                            Toast.makeText(FotoActivity.this, "Foto registrada", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        } else {
                            Toast.makeText(FotoActivity.this, "No se pudo registrar foto", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(FotoActivity.this, "Ocurrio un error", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });

        if (activity.equals("registro")) {
            Intent intent = new Intent(this, VerificacionActivity.class);
            intent.putExtra("activity","foto");
            intent.putExtra("idUser", idUser);
            intent.putExtra("mail", email);
            startActivity(intent);
        } else if (activity.equals("menu")) {
            onBackPressed();
        }

//        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && codigo == 1) {
            this.grabImage(img);
        } else if (resultCode == RESULT_OK && codigo == 2) {
            Uri selectedImage = data.getData();

            imagenGaleria = new File(getRealPathFromURI(selectedImage));
            img.setImageURI(selectedImage);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
