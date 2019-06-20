package group.jedai.panic.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import group.jedai.panic.background.AlertasService;

public class MyReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    public static final String CUSTOM_INTENT = "com.test.intent.action.ALARM";
    public static Context ctx;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AlertasService.class);
        i.putExtra("tipo",intent.getStringExtra("tipo"));
        i.putExtra("idUser",intent.getStringExtra("idUser"));
        i.putExtra("email",intent.getStringExtra("email"));
        i.putExtra("latitud",intent.getDoubleExtra("latitud",0.0));
        i.putExtra("longitud",intent.getDoubleExtra("longitud",0.0));

        final PendingIntent pIntent = PendingIntent.getBroadcast(context, MyReceiver.REQUEST_CODE, i, PendingIntent.FLAG_UPDATE_CURRENT);
        long millis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, millis, 5000, pIntent);
        context.startService(i);
    }


    public static void cancelAlarm() {
        AlarmManager alarm = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
    }

    public static void setAlarm(boolean force) {
        cancelAlarm();
        AlarmManager alarm = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        // EVERY X MINUTES
        long delay = 1000;
        long when = System.currentTimeMillis();
        if (!force) {
            when += delay;
        }
        alarm.set(AlarmManager.RTC_WAKEUP, when, getPendingIntent());
    }

    private static PendingIntent getPendingIntent() {
        Intent alarmIntent = new Intent(ctx, MyReceiver.class);
        alarmIntent.setAction(CUSTOM_INTENT);
        return PendingIntent.getBroadcast(ctx, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
