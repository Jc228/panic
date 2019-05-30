package group.jedai.panic.background;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import org.joda.time.LocalDateTime;

import java.util.HashMap;
import java.util.Map;
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

    private double longitud;
    private double latitud;
    private String nombre;
    private String tipo;
    private String idUser;

    TimerTask timerTask;
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

//    private SocketSrv socketSrv;

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
        Log.d("Servicio", "Servicio Alertas Iniciado");
        Map<String, Object> map = new HashMap<>();
        admSession = new AdmSession(getApplicationContext());
        map = admSession.getDatos();

        String idUser = (String) map.get("idUser");
        String tipo = (String) map.get("tipo");
        Double latitud = (Double) map.get("latitud");
        Double longitud = (Double) map.get("longitud");
        emitirUbicacionGuardia(idUser, tipo, latitud ,longitud);

//        NotificacionThread notificacionThread = new NotificacionThread(idUser,tipo,latitud,longitud, getApplicationContext());
//new Thread(notificacionThread).start();
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
                        timerTask = new TimerTask() {
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
                                                        Toast.makeText(getApplicationContext(), "Ubicacion actualizada", Toast.LENGTH_LONG).show();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Notificacion> call, Throwable t) {
                                                    Log.i("Servicio", notificacion.getIdUsuario());

                                                    Toast.makeText(getApplicationContext(), "Ocurrio un problema", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                    }
                                });
                            }
                        };
                        t.purge();
                            t.schedule(timerTask, 500, 1000);
        }
    }

    public void enviarAlerta(){
            messageService.connect();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        messageService.send(new Alerta("user","27/05/2019",123.024,1325.0212,0.154,213.0215,"123isad","21/06/2010","14/04/2020", true));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    String alertaId = null;

    @TargetApi(Build.VERSION_CODES.O)
    public double emitirUbicacion(final String idUser, final String tipo, final double latitud, final double longitud, final GoogleMap googleMap) {
//        Intent intent = new Intent(context, AdmAlerta.class);
//        context.startService(intent);
        if (latitud != 0.0) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            nCounter++;
                            if (!tipo.equalsIgnoreCase("guardia")) {
                                AlertaSrv alertaSrv = retrofit.create(AlertaSrv.class);
                                final Alerta alerta = new Alerta(idUser, LocalDateTime.now().toString(), latitud, longitud, null, null, null, null, null, true);
                                alerta.setId(alertaId);
                                Call<Alerta> alertaCall = alertaSrv.addAlerta(alerta);
                                alertaCall.enqueue(new Callback<Alerta>() {
                                    @Override
                                    public void onResponse(Call<Alerta> call, Response<Alerta> response) {
                                        if (response.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Alerta enviada", Toast.LENGTH_LONG).show();
                                            Alerta alert = response.body();
                                            alertaId = alert.getId();
//                                            if (alert.getIdGuardia() != null) {
                                            //crear notificacion a los guardias
//                                                notificacion(alert);
//                                                System.out.printf("notificar guadias");
//                                            socketSrv.connect();
//                                            if (socketSrv.isConnected()) {
//                                                socketSrv.send(alert);
//                                                Log.i("SOCKET:", alert.getId() + "; " + alert.getLatitude() + "; " + alert.getLongitude());
//                                            } else {
//                                                Log.i("SOCKET:", "No se pudo conectar a socket");
//                                            }

                                            if ((alert.getLongitudeG() != null) || (alert.getLatitudeG() != null)) {
                                                menuActivity.onMapActualizar(googleMap, alert.getLatitude(), alert.getLongitude(), alert.getLatitudeG(), alert.getLongitudeG());
                                                menuMapActivity.onMapActualizar(googleMap, alert.getLatitude(), alert.getLongitude(), alert.getLatitudeG(), alert.getLongitudeG());
//                                                    menuMapActivity.onMapActualizar(googleMap, alert.getLatitude(), alert.getLongitude(), alert.getLatitudeG() + (nCounter * 0.0000028884), alert.getLongitudeG() + (nCounter * 0.0000673232));
//                                                notificacion(alert);
//                                                    if(nCounter == 2) {
//                                                Intent service = new Intent(AdmAlerta.this, ListenerNotificacion.class);
//                                                    service.putExtra("latitud",alert.getLatitude());
//                                                    service.putExtra("longitud",alert.getLongitude());
//                                                    service.putExtra("latitudG",alert.getLatitudeG());
//                                                    service.putExtra("longitudG",alert.getLongitudeG());
                                                Toast.makeText(getApplicationContext(), "Va a servicio", Toast.LENGTH_LONG);
                                                //  startService(service);
//                                                    }
                                            }

//                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Alerta> call, Throwable t) {
                                        Toast.makeText(getApplicationContext(), "Ocurrio un problema", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                NotificacionSrv notificacionSrv = retrofit.create(NotificacionSrv.class);
                                final Notificacion notificacion = new Notificacion(idUser, LocalDateTime.now().toString(), latitud, longitud);
                                Call<Notificacion> notificacionCall = notificacionSrv.addNotificacion(notificacion);
                                notificacionCall.enqueue(new Callback<Notificacion>() {
                                    @Override
                                    public void onResponse(Call<Notificacion> call, Response<Notificacion> response) {
                                        if (response.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Ubicacion actualizada", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Notificacion> call, Throwable t) {
                                        Toast.makeText(getApplicationContext(), "Ocurrio un problema", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    });
                }
            };
            t.purge();
            if (!tipo.equalsIgnoreCase("guardia")) {
                t.schedule(timerTask, 500, 50000);
            } else {
                t.schedule(timerTask, 500, 100000);
//                t.schedule(timerTask, 500, 3000);
            }
            return latitud;
        } else {
            return 0.0;
        }
    }

    public void stopAlerta() {
        if (timerTask != null) {
            timerTask.cancel();
            t.purge();
            nCounter = 0;
        }
    }




}

class NotificacionThread extends Thread {
    String idUser;
    String tipo;
    Double latitud;
    Double longitud;
    TimerTask timerTask;
    final Handler handler = new Handler();
    Timer t = new Timer();
    private int nCounter = 0;
    private Retrofit retrofit;
    private Context context;

    NotificacionThread(String idUser, String tipo, double latitud, double longitud, Context context){
        this.idUser = idUser;
        this.tipo = tipo;
        this.latitud = latitud;
        this.longitud = longitud;
        this.context = context;
        retrofit = new Retrofit.Builder()
                .baseUrl(Constantes.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Override
    public void run() {
        if (latitud != 0.0) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            nCounter++;
                            if (tipo.equalsIgnoreCase("guardia")) {
                                NotificacionSrv notificacionSrv = retrofit.create(NotificacionSrv.class);
                                final Notificacion notificacion = new Notificacion(idUser, LocalDateTime.now().toString(), latitud, longitud);
                                Call<Notificacion> notificacionCall = notificacionSrv.addNotificacion(notificacion);
                                notificacionCall.enqueue(new Callback<Notificacion>() {
                                    @Override
                                    public void onResponse(Call<Notificacion> call, Response<Notificacion> response) {
                                        if (response.isSuccessful()) {
                                            Toast.makeText(context, "Ubicacion actualizada", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Notificacion> call, Throwable t) {
                                        Toast.makeText(context, "Ocurrio un problema", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    });
                }
            };
            t.purge();
            if (tipo.equalsIgnoreCase("guardia")) {
                t.schedule(timerTask, 500, 5000);
            }


        }
    }
}
