package group.jedai.panic.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import group.jedai.panic.activitys.MainActivity;
import group.jedai.panic.activitys.MenuActivity;
import group.jedai.panic.activitys.MenuMapActivity;
import group.jedai.panic.dto.Usuario;


public class AdmSession {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editPref;
    private Context context;

    public AdmSession() {
    }

    public AdmSession(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("datos", Context.MODE_PRIVATE);
        editPref = sharedPreferences.edit();
    }

    public void guardarSesion(boolean login, Usuario user) {
        Class red;
        if (user.getTipo().equalsIgnoreCase("guardia")) {
//            red = MapsActivity.class;
            red = MenuMapActivity.class;
        } else {
//            red = BienvenidaActivity.class;
            red = MenuActivity.class;
        }

        Intent intent = new Intent(context, red);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//para versiones menores a android 6
        intent.putExtra("nombre", user.getNombre());
        intent.putExtra("tipo", user.getTipo());
        intent.putExtra("idUser", user.getId());
        intent.putExtra("email", user.getMail());
        intent.putExtra("activo", user.isActivo());


        editPref.putBoolean("login", true);
        editPref.putString("email", user.getMail());
        editPref.putString("nombre", user.getNombre());
        editPref.putString("tipo", user.getTipo());
        editPref.putString("idUser", user.getId());
        editPref.putBoolean("activo", user.isActivo());
        editPref.commit();
        Log.i("UserRegistrado", sharedPreferences.getString("nombre", null));
        context.startActivity(intent);
    }

    public void redireccionarInicio() {
        if (sharedPreferences.getString("tipo", null).equalsIgnoreCase("guardia")) {
//            Intent intent = new Intent(context, MapsActivity.class);
            Intent intent = new Intent(context, MenuMapActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//para versiones menores a android 6
            intent.putExtra("nombre", sharedPreferences.getString("nombre", null));
            intent.putExtra("tipo", sharedPreferences.getString("tipo", null));
            intent.putExtra("idUser", sharedPreferences.getString("idUser", null));
            intent.putExtra("email", sharedPreferences.getString("email", null));
            intent.putExtra("activo", sharedPreferences.getBoolean("activo", false));
            Log.i("UserLogueado", sharedPreferences.getString("nombre", null));
            context.startActivity(intent);
        } else {
//            Intent intent = new Intent(context, BienvenidaActivity.class);
            Intent intent = new Intent(context, MenuActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//para versiones menores a android 6
            intent.putExtra("nombre", sharedPreferences.getString("nombre", null));
            intent.putExtra("tipo", sharedPreferences.getString("tipo", null));
            intent.putExtra("idUser", sharedPreferences.getString("idUser", null));
            intent.putExtra("email", sharedPreferences.getString("email", null));
            intent.putExtra("activo", sharedPreferences.getBoolean("activo", false));
            Log.i("UserLogueado", sharedPreferences.getString("nombre", null));
            context.startActivity(intent);
        }
    }

    public void borrarSesion() {
        editPref.clear();
        editPref.commit();
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//para versiones menores a android 6
//        intent.putExtra("nombre", sharedPreferences.getString("nombre", null));
//        intent.putExtra("tipo", sharedPreferences.getString("tipo", null));
//        intent.putExtra("idUser", sharedPreferences.getString("idUser", null));

        context.startActivity(intent);
    }

    public boolean logueado() {
        return sharedPreferences.getBoolean("login", false);
    }
}
