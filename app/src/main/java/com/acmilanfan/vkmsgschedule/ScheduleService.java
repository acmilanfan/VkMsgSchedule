package com.acmilanfan.vkmsgschedule;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

public class ScheduleService extends Service {

    private AlarmManager am;
    private NotificationManager nm;
    private Intent intent;
    private PendingIntent pIntent;
    private SharedPreferences sp;
    private String userId;
    private String period;
    private boolean isOnline;
    private boolean isUnreadMsg;
    private String loadMsgNum;
    private String msgType;
    private String msgText;

    @Override
    public void onCreate() {
        super.onCreate();
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        am = (AlarmManager) getSystemService(ALARM_SERVICE);
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        userId = sp.getString("userId", "");
        period = sp.getString("schedulePeriod", "");
        isOnline = sp.getBoolean("isOnline", false);
        isUnreadMsg = sp.getBoolean("isUnreadMsg", false);
        loadMsgNum = sp.getString("msgLoadNum", "");
        msgType = sp.getString("msgType", "");
        msgText = sp.getString("msgText", "");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startSchedule();
        return Service.START_STICKY;
    }

    private void startSchedule() {
        sendNotif();
        intent = new Intent(this, MsgService.class);
        intent.putExtra("userId", userId);
        intent.putExtra("isOnline", isOnline);
        intent.putExtra("isUnreadMsg", isUnreadMsg);
        intent.putExtra("msgLoadNum", loadMsgNum);
        intent.putExtra("msgType", msgType);
        intent.putExtra("msgText", msgText);
        pIntent = PendingIntent.getService(this, 0, intent, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * Integer.parseInt(period), pIntent);
    }

    private void sendNotif() {
        Notification notif = new Notification(R.drawable.ic_launcher, "Сервис отправки сообщений работает",
                System.currentTimeMillis());

        intent = new Intent(this, MainActivity.class);
        pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        notif.setLatestEventInfo(this, "Сервис сообщений", "Сервис сообщений запущен", pIntent);

        notif.flags |= Notification.FLAG_NO_CLEAR;
        startForeground(1, notif);
        nm.notify(1, notif);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        am.cancel(pIntent);
        nm.cancel(1);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
