package group.jedai.panic.activitys;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.support.v7.app.AlertDialog;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.onesignal.OneSignal;

import group.jedai.panic.R;
import group.jedai.panic.background.AdmAlerta;
import group.jedai.panic.utils.AdmSession;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private CameraUpdate cameraUpdate;

    //    private TextView textView;
    private TextView textViewMail;
    private String nombre;
    private String tipo;
    private String idUser;
    private String email;
    private Boolean activo;
    private LocationManager locationManager;
    private Location location;
    private double latitud;
    private double longitud;
    private AdmSession admSession;
    private AdmAlerta admAlerta;
    private static final String CHANNEL_ID = "canal1";
    private static final int ID = 51623;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapCli);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        admSession = new AdmSession(getApplicationContext());
        admAlerta = new AdmAlerta(getApplicationContext());

        nombre = getIntent().getStringExtra("nombre");
        tipo = getIntent().getStringExtra("tipo");
        idUser = getIntent().getStringExtra("idUser");
        email = getIntent().getStringExtra("email");
        activo = getIntent().getBooleanExtra("activo", false);

        //OneSignal
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        OneSignal.sendTag("idUser", idUser);

        if (activo) {
            Toast.makeText(this, "!!Bienvenido " + nombre + " !!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Su cuenta no esta acticada se le recomienda activarla", Toast.LENGTH_SHORT).show();

        }

        Toolbar toolbar = findViewById(R.id.toolbarCli);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layoutCli);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_viewCli);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        textViewMail = (TextView) header.findViewById(R.id.textViewMail);
        System.out.println(textViewMail.getText());
        textViewMail.setText(email);

        if (!checkLocation()) {
            return;
        }
        start();
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
        ActivityCompat.requestPermissions(MenuActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 2 * 20 * 1000, 10, locationListenerGPS);
    }

    public void toggleNetworkUpdates() {

        locationManager.removeUpdates(locationListenerNetwork);
        ActivityCompat.requestPermissions(MenuActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
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
                longitud = location.getLongitude();
                latitud = location.getLatitude();
            } while (longitud == 0.0 && latitud == 0.0);
            System.out.println("longtNet: " + longitud + "latiNet: " + latitud);
            mMap.clear();
            LatLng ubicacion = new LatLng(latitud, longitud);
            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.hombre)).position(ubicacion).title(nombre));
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(ubicacion, 14);
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
                longitud = location.getLongitude();
                latitud = location.getLatitude();
            } while (longitud == 0.0 && latitud == 0.0);
            System.out.println("longt: " + longitud + "lati: " + latitud);
            mMap.clear();
            LatLng ubicacion = new LatLng(latitud, longitud);
            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.hombre)).position(ubicacion).title(nombre));
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(ubicacion, 14);
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

    public void onMapActualizar(GoogleMap googleMap, double latitud, double longitud, double latitudG, double longitudG) {
        GoogleMap map = googleMap;
        CameraUpdate camera;
        map.clear();
        LatLng ubicacion = new LatLng(latitud, longitud);
        LatLng ubicacionG = new LatLng(latitudG, longitudG);
        map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.hombre)).position(ubicacion).title(nombre));
//        if(longitud !=0.0 && latitud != 0.0 ) {
        map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.policeman)).position(ubicacionG).title(nombre));
//        }
        camera = CameraUpdateFactory.newLatLngZoom(ubicacion, 16);
        map.animateCamera(camera);
    }

    public void start() {
        ActivityCompat.requestPermissions(MenuActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "No se han definido los permisos necesarios.", Toast.LENGTH_LONG);
            return;
        } else {
            try {
//                toggleGPSUpdates();
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                latitud = location.getLatitude();
                longitud = location.getLongitude();
                if (location == null || latitud == 0.0 || longitud == 0.0) {
                    toggleGPSUpdates();
                }
            } catch (Exception e) {
                toggleNetworkUpdates();
                System.out.println("Excepcion: " + e);
            }
        }
    }

    public void emitirUbicacion() {
//admAlerta.enviarAlerta();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (latitud == 0.0) {
                    start();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                admAlerta.emitirUbicacion(idUser, tipo, latitud, longitud, true, mMap);
            }
        }).start();
    }

    public void stopAlerta() {
        admAlerta.stopAlerta();
    }


    public void notificacion() {
        createNotificationChannel();

        Bundle bundle = new Bundle();
        bundle.putInt("accion", 1);
        Intent intent = new Intent(this, MenuActivity.class);
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
        //        view.setImageViewResource(R.drawable.ic_menu_camera, R.drawable.ic_menu_gallery);

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
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "canal";
            String description = "canal de notificacion";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    public void clicAction(View view) {
        switch (view.getId()) {
            case R.id.buttonPeligroF:
                emitirUbicacion();
                break;
            case R.id.button:
                stopAlerta();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layoutCli);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
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

        if (id == R.id.nav_foto) {
            Intent intent = new Intent(this, FotoActivity.class);
            intent.putExtra("activity", "menu");
            intent.putExtra("idUser", idUser);
            intent.putExtra("mail", email);
            startActivity(intent);
        } else if (id == R.id.nav_instrucciones) {
            Intent intent = new Intent(this, VerificacionActivity.class);
            intent.putExtra("activity", "menu");
            intent.putExtra("idUser", idUser);
            intent.putExtra("mail", email);
            startActivity(intent);
        } else if (id == R.id.nav_cambiar) {
            Intent intent = new Intent(this, CambiarPassActivity.class);
            intent.putExtra("idUser", idUser);
            startActivity(intent);
        } else if (id == R.id.nav_cerrar) {
            admSession.borrarSesion();
//            admAlerta.stopAlerta();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layoutCli);
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
        LatLng ubicacion = new LatLng(latitud, longitud);
        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.hombre)).position(ubicacion).title(nombre));
        cameraUpdate = CameraUpdateFactory.newLatLngZoom(ubicacion, 16);
        mMap.animateCamera(cameraUpdate);
    }


    @Override
    protected void onPause() {
        super.onPause();
        notificacion();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        emitirUbicacion();
        notificacion();
    }

}
