package group.jedai.panic.background;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.joda.time.LocalDateTime;

import java.util.Timer;
import java.util.TimerTask;

import group.jedai.panic.R;
import group.jedai.panic.dto.Notificacion;
import group.jedai.panic.srv.NotificacionSrv;
import group.jedai.panic.utils.Constantes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class AlertaActService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "group.jedai.panic.background.action.FOO";
    private static final String ACTION_BAZ = "group.jedai.panic.background.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "group.jedai.panic.background.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "group.jedai.panic.background.extra.PARAM2";

    private PowerManager.WakeLock wakeLock;
    public static final String CHANNEL_ID = "CanalServicio1";
    TimerTask timerTask;
    final Handler handler = new Handler();
    Timer t = new Timer();
    private int nCounter = 0;
    private Context context;
    private Retrofit retrofit;

    public AlertaActService() {
        super("AlertaActService");
        setIntentRedelivery(true);
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, AlertaActService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, AlertaActService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "ExampleApp:Wakelock");
        wakeLock.acquire();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Smart Alert Uce")
                    .setContentText("Accede rapidamente aqui...")
                    .setSmallIcon(R.drawable.common_full_open_on_phone)
                    .build();
            startForeground(1, notification);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }

        String tipo = intent.getStringExtra("tipo");
        String idUser = intent.getStringExtra("idUser");
        String email = intent.getStringExtra("email");

        SharedPreferences sharedPreferences= this.getSharedPreferences("datos", Context.MODE_PRIVATE);

        Double latitud = Double.parseDouble(sharedPreferences.getString("latitud", "0.0"));
        Double longitud = Double.parseDouble(sharedPreferences.getString("longitud", "0.0"));

        Log.i("Intent:", tipo + " " + idUser);
        emitirUbicacionGuardia(idUser, tipo, latitud, longitud);
    }

    public void emitirUbicacionGuardia(final String idUser, final String tipo, final double latitud, final double longitud) {
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
//                                            Toast.makeText(getApplicationContext(), "Ubicacion actualizada" + notificacion.getIdUsuario(), Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Notificacion> call, Throwable t) {
                                        Log.i("Servicio", notificacion.getIdUsuario());

                                        Toast.makeText(getApplicationContext(), "Ocurrio un problema", Toast.LENGTH_LONG).show();
                                    }
                                });
                                System.out.println("Counter:"+nCounter);
                        }
                    });
                }
            };
            t.purge();
            t.schedule(timerTask, 4000, 100000);
        }
    }
    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
