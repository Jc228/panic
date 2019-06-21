package group.jedai.panic.activitys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.onesignal.OneSignal;

import java.util.HashMap;

import group.jedai.panic.R;
import group.jedai.panic.background.AdmAlerta;
import group.jedai.panic.background.AlertaActService;
import group.jedai.panic.srv.MessageService;
import group.jedai.panic.utils.AdmSession;
import group.jedai.panic.utils.MyReceiver;

public class MenuMapActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private CameraUpdate cameraUpdate;
    private LocationManager locationManager;
    private Location location;
    private double longitud;
    private double latitud;
    private double longitud1;
    private double latitud1;
    private double longitudG;
    private double latitudG;
    private String nombre;
    private String tipo;
    private String idUser;
    private String email;
    private TextView textViewMail;
    private AdmAlerta admAlerta;
    private AdmSession admSession;
    private static final String CHANNEL_ID = "canal1";
    private static final int ID = 51623;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editPref;
    private MessageService messageService = new MessageService();
    private HashMap<String, Marker> hashMapMarker = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_map);

        int permissions_code = 42;
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

        ActivityCompat.requestPermissions(this, permissions, permissions_code);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        admSession = new AdmSession(getApplicationContext());

        nombre = getIntent().getStringExtra("nombre");
        tipo = getIntent().getStringExtra("tipo");
        idUser = getIntent().getStringExtra("idUser");
        email = getIntent().getStringExtra("email");

        //OneSignal
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        OneSignal.sendTag("idUser", idUser);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Mostrar algun dato...", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                mMap.clear();
//            }
//        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        textViewMail = (TextView) header.findViewById(R.id.textViewMailG);
        textViewMail.setText(email);

        sharedPreferences = this.getSharedPreferences("datos", Context.MODE_PRIVATE);
        editPref = sharedPreferences.edit();

        if (!checkLocation()) {
            return;
        }
        start();

        latitud1 = getIntent().getDoubleExtra("latitud1", 0.0);
        longitud1 = getIntent().getDoubleExtra("longitud1", 0.0);
        latitudG = getIntent().getDoubleExtra("latitudG", 0.0);
        longitudG = getIntent().getDoubleExtra("longitudG", 0.0);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initServicioAct();
        } else {
            iniciarServicio();
        }

    }

    private void suscribeSocket(String idUser, GoogleMap mMap) {
        messageService.connect(idUser, mMap, new ICallback() {

            @Override
            public void run(final GoogleMap googleMap, final Double latitude, final Double longitude, final Double latitudeG, final Double longitudeG) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onMapActualizar(googleMap, latitud, longitude, latitudeG, longitudeG);
                    }
                });
            }

            public void onMapActualizar(GoogleMap map, double latitud, double longitud, double latitudG, double longitudG) {
                CameraUpdate camera;
                map.clear();
                LatLng ubicacion = new LatLng(latitud, longitud);
                LatLng ubicacionG = new LatLng(latitudG, longitudG);
                map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.hombre)).position(ubicacion).title("Alerta"));
                map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.policeman)).position(ubicacionG).title(nombre));
                camera = CameraUpdateFactory.newLatLngZoom(ubicacion, 16);
                map.animateCamera(camera);
            }
        });
        ;
        if (messageService.isConnected()) {
            Log.i("SOCKET:", "Conectado a socket");
        } else {
            Log.i("SOCKET:", "No se pudo conectar a socket");
        }
    }

    public void iniciarServicio() {

        Intent intent = new Intent(getApplicationContext(), MyReceiver.class);
        intent.putExtra("tipo", tipo);
        intent.putExtra("idUser", idUser);
        intent.putExtra("email", email);
        intent.putExtra("latitud", latitud);
        intent.putExtra("longitud", longitud);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, MyReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long millis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, millis, 1000, pIntent);
    }

    public void initServicioAct() {
        Intent intent = new Intent(getApplicationContext(), AlertaActService.class);
        intent.putExtra("tipo", tipo);
        intent.putExtra("idUser", idUser);
        intent.putExtra("email", email);
        intent.putExtra("latitud", latitud);
        intent.putExtra("longitud", longitud);
        ContextCompat.startForegroundService(getApplicationContext(), intent);
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Habilitar Localizacion")
                .setMessage("Su ubicación esta desactivada.\npor favor active su ubicación " +
                        "usa esta app")
                .setPositiveButton("Configuración de ubicación", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }


    public void toggleGPSUpdates() {

        locationManager.removeUpdates(locationListenerGPS);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 2 * 20 * 1000, 10, locationListenerGPS);

    }

    public void toggleNetworkUpdates() {

        locationManager.removeUpdates(locationListenerNetwork);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 20 * 1000, 10, locationListenerNetwork);
    }

    private LocationListener locationListenerNetwork = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            do {
                if (location != null) {
                    longitud = location.getLongitude();
                    latitud = location.getLatitude();

                    editPref.putString("longitud", String.valueOf(longitud));
                    editPref.putString("latitud", String.valueOf(latitud));
                    editPref.commit();
                }
            }
            while (location == null || location.getLongitude() == 0.0 && location.getLatitude() == 0.0);
            System.out.println("longtNet: " + longitud + "latiNet: " + latitud);
            mMap.clear();
            LatLng ubicacion = new LatLng(latitud, longitud);
            Marker marker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.policeman)).position(ubicacion).title(nombre));

            Marker old = hashMapMarker.get("guardia");
            if (old != null) {
                old.remove();
            }
            hashMapMarker.put("guardia", marker);

            cameraUpdate = CameraUpdateFactory.newLatLngZoom(ubicacion, 16);
            mMap.animateCamera(cameraUpdate);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            do {
                if (location != null) {
                    longitud = location.getLongitude();
                    latitud = location.getLatitude();

                    editPref.putString("longitud", String.valueOf(longitud));
                    editPref.putString("latitud", String.valueOf(latitud));
                    editPref.commit();
                }
            }
            while (location == null || location.getLongitude() == 0.0 && location.getLatitude() == 0.0);
            System.out.println("longt: " + longitud + "lati: " + latitud);

            LatLng ubicacion = new LatLng(latitud, longitud);
            mMap.clear();

            Marker marker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.policeman)).position(ubicacion).title(nombre));

            Marker old = hashMapMarker.get("guardia");
            if (old != null) {
                old.remove();
            }
            hashMapMarker.put("guardia", marker);

            cameraUpdate = CameraUpdateFactory.newLatLngZoom(ubicacion, 16);
            mMap.animateCamera(cameraUpdate);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @SuppressLint("MissingPermission")
    public void start() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "No se han definido los permisos necesarios.", Toast.LENGTH_LONG);
            return;
        } else {
            try {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location == null || location.getLatitude() == 0.0 || location.getLongitude() == 0.0) {
                    toggleGPSUpdates();
                } else {
                    longitud = location.getLongitude();
                    latitud = location.getLatitude();

                    editPref.putString("longitud", String.valueOf(longitud));
                    editPref.putString("latitud", String.valueOf(latitud));
                    editPref.commit();

                    emitirUbicacion();
                }


            } catch (Exception e) {
//                toggleNetworkUpdates();
                toggleGPSUpdates();
                System.out.println("Excepcion: " + e);
            }
        }
    }

    public void emitirUbicacion() {
        admAlerta.emitirUbicacionGuardia(idUser, tipo, latitud, longitud);
    }

    public void notificacion() {
        createNotificationChannel();

        Bundle bundle = new Bundle();
        bundle.putInt("accion", 1);
        Intent intent = new Intent(this, MenuMapActivity.class);
        intent.putExtra("nombre", nombre);
        intent.putExtra("tipo", tipo);
        intent.putExtra("idUser", idUser);
        intent.putExtra("email", email);
        intent.putExtra("activo", true);

        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //interfaz de notificacion
        RemoteViews view = new RemoteViews(getPackageName(), R.layout.activity_notificacion);
        view.setOnClickPendingIntent(R.id.btnNotificacion, pendingIntent);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        mBuilder.setAutoCancel(false);

        mBuilder.setSmallIcon(R.drawable.aguila);
        mBuilder.setWhen(System.currentTimeMillis());
        mBuilder.setPriority(Notification.PRIORITY_HIGH);
        mBuilder.setVisibility(Notification.VISIBILITY_PUBLIC);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setContent(view);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(ID, mBuilder.build());
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "canal";
            String description = "canal de notificacion";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//        }else
        if (id == R.id.nav_cambiar) {
            Intent intent = new Intent(this, CambiarPassActivity.class);
            intent.putExtra("idUser", idUser);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            admSession.borrarSesion();
//            admAlerta.stopAlerta();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        mMap.setMyLocationEnabled(true);
        nombre = getIntent().getStringExtra("nombre");
        tipo = getIntent().getStringExtra("tipo");
        System.out.println("long: " + longitud + "lat: " + latitud);
        if ((longitud == 0.0) || (latitud == 0.0)) {
            toggleGPSUpdates();
        }
        mMap.clear();
        LatLng ubicacion = new LatLng(latitud, longitud);
        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.policeman)).position(ubicacion).title(nombre));
        cameraUpdate = CameraUpdateFactory.newLatLngZoom(ubicacion, 16);
        mMap.animateCamera(cameraUpdate);

        suscribeSocket(idUser, mMap);

    }

    public void onMapActualizar(GoogleMap googleMap, double latitud, double longitud, double latitudG, double longitudG) {
        GoogleMap map = googleMap;
        CameraUpdate camera;
        map.clear();
        LatLng ubicacion = new LatLng(latitud, longitud);
        LatLng ubicacionG = new LatLng(latitudG, longitudG);
        map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.hombre)).position(ubicacion).title("Alerta"));
        map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.policeman)).position(ubicacionG).title(nombre));
        camera = CameraUpdateFactory.newLatLngZoom(ubicacion, 16);
        map.animateCamera(camera);
    }

    @Override
    protected void onPause() {
        super.onPause();
        notificacion();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        emitirUbicacion();
        notificacion();
    }

    public static interface ICallback {
        void run(GoogleMap googleMap, Double latitude, Double longitude, Double latitudeG, Double longitudeG);
    }
}
