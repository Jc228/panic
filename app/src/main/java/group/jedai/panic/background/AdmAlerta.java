package group.jedai.panic.background;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import org.joda.time.LocalDateTime;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import group.jedai.panic.activitys.MenuActivity;
import group.jedai.panic.activitys.MenuMapActivity;
import group.jedai.panic.dto.Alerta;
import group.jedai.panic.dto.Notificacion;
import group.jedai.panic.srv.AlertaSrv;
import group.jedai.panic.srv.MessageService;
import group.jedai.panic.srv.NotificacionSrv;
import group.jedai.panic.utils.AdmSession;
import group.jedai.panic.utils.Constantes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdmAlerta extends Service {

    TimerTask timerTaskguardia;
    TimerTaskAlerta timerTask;
    final Handler handler = new Handler();
    Timer t = new Timer();
    private int nCounter = 0;
    private Context context;
    private Retrofit retrofit;
    private static final String CHANNEL_ID = "canal2";
    private static final int ID = 51624;
    private MenuActivity menuActivity = new MenuActivity();
    private MenuMapActivity menuMapActivity = new MenuMapActivity();
    private AdmSession admSession;

    private MessageService messageService = new MessageService();

    public AdmAlerta() {
    }

    public AdmAlerta(Context context) {
        this.context = context;
        retrofit = new Retrofit.Builder()
                .baseUrl(Constantes.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        admSession = new AdmSession(context);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Servicio", "Servicio AlertasService Iniciado");
        Map<String, Object> map = new HashMap<>();
        admSession = new AdmSession(getApplicationContext());
        map = admSession.getDatos();

        String idUser = (String) map.get("idUser");
        String tipo = (String) map.get("tipo");
        Double latitud = (Double) map.get("latitud");
        Double longitud = (Double) map.get("longitud");
        //controlar si es guardia enviar notificacion sino enviar alerta
        emitirUbicacionGuardia(idUser, tipo, latitud, longitud);
        return START_STICKY;
    }

    public void emitirUbicacionGuardia(final String idUser, final String tipo, final double latitud, final double longitud) {
//        Intent intent = new Intent(context, AdmAlerta.class);
//        context.startService(intent);
        retrofit = new Retrofit.Builder()
                .baseUrl(Constantes.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        if (latitud != 0.0) {
            timerTaskguardia = new TimerTask() {

                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            nCounter++;
                            NotificacionSrv notificacionSrv = retrofit.create(NotificacionSrv.class);
                            final Notificacion notificacion = new Notificacion(idUser, LocalDateTime.now().toString(), latitud, longitud);
                            Call<Notificacion> notificacionCall = notificacionSrv.addNotificacion(notificacion);
                            notificacionCall.enqueue(new Callback<Notificacion>() {
                                @Override
                                public void onResponse(Call<Notificacion> call, Response<Notificacion> response) {
                                    if (response.isSuccessful()) {
                                        Log.i("Servicio", notificacion.getIdUsuario());
                                        Toast.makeText(context, "Ubicacion actualizada", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Notificacion> call, Throwable t) {
                                    Log.i("Servicio", notificacion.getIdUsuario());

                                    Toast.makeText(context, "Ocurrio un problema", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                }
            };
            t.purge();
            t.schedule(timerTaskguardia, 500, 100000);
        }
    }

    public void enviarAlerta() {
        messageService.connect();
        if (messageService.isConnected()) {
            sendNotification("");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @TargetApi(Build.VERSION_CODES.O)
    public double emitirUbicacion(final String idUser, final String tipo, final double latitud, final double longitud, final boolean activo, final GoogleMap googleMap) {
        if (latitud != 0.0) {
            timerTask = new TimerTaskAlerta() {

                private boolean emitir = true;
                private boolean enviar = true;
                private String alertaId = null;
                private TimerTaskAlerta yomismo = this;

                @Override
                public void detenme() {
                    this.emitir = false;
                }

                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            nCounter++;
                            if (!tipo.equalsIgnoreCase("guardia") && enviar) {
                                if (!emitir) {
                                    enviar = false;
                                    yomismo.cancel();
                                }
                                AlertaSrv alertaSrv = retrofit.create(AlertaSrv.class);
                                final Alerta alerta = new Alerta(idUser, LocalDateTime.now().toString(), latitud, longitud, null, null, null, null, null, emitir);
                                alerta.setId(alertaId);
                                Call<Alerta> alertaCall = alertaSrv.addAlerta(alerta);
                                alertaCall.enqueue(new Callback<Alerta>() {
                                    @Override
                                    public void onResponse(Call<Alerta> call, Response<Alerta> response) {
                                        if (response.isSuccessful()) {
                                            Toast.makeText(context, "Alerta enviada", Toast.LENGTH_LONG).show();
                                            Alerta alert = response.body();
                                            alertaId = alert.getId();
                                            if (alert.getIdGuardia() != null) {
//                                            crear notificacion a los guardias
                                                messageService.connect();
                                                if (messageService.isConnected()) {
                                                    sendNotification(alert.getIdGuardia());
                                                    Log.i("SOCKET:", "Conectado a socket");
                                                } else {
                                                    Log.i("SOCKET:", "No se pudo conectar a socket");
                                                }

                                                if ((alert.getLongitudeG() != null) || (alert.getLatitudeG() != null)) {
                                                    menuActivity.onMapActualizar(googleMap, alert.getLatitude(), alert.getLongitude(), alert.getLatitudeG(), alert.getLongitudeG());
                                                }
//                                                else if(emitir == false){
//                                                    menuActivity.onMapActualizar(googleMap, alert.getLatitude(), alert.getLongitude(), 0.0, 0.0);
//                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Alerta> call, Throwable t) {
                                        Toast.makeText(context, "Ocurrio un problema", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    });
                }
            };
            t.purge();
            t.schedule(timerTask, 500, 50000);
            return latitud;
        } else {
            return 0.0;
        }
    }

    public void stopAlerta() {
        if (timerTask != null) {
            timerTask.detenme();
            nCounter = 0;
        }
    }

    private void sendNotification(final String idGuardia) {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic YmE3MjhiYzMtMjQ4NS00MjZjLWI4ODktMjc2MDU0MjU2MDc1");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                + "\"app_id\": \"2ba7353e-2b75-4597-9e77-689f600c0b75\","

                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"idUser\", \"relation\": \"=\", \"value\": \"" + idGuardia + "\"}],"

                                + "\"data\": {\"foo\": \"bar\"},"
                                + "\"contents\": {\"en\": \"Alguien necesita tu ayuda...\"}"
                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        } else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
    }

    public static abstract class TimerTaskAlerta extends TimerTask {
        public abstract void detenme();
    }
}
