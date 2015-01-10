package com.acmilanfan.vkmsgschedule;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;
import com.vk.sdk.dialogs.VKCaptchaDialog;


public class MainActivity extends ActionBarActivity {

    //todo 8. Сменить иконку приложения

    private static final String VK_APP_ID = "4719094";
    private TextView userId;
    private TextView schedulePeriod;
    private TextView isOnline;
    private TextView isUnreadMsg;
    private TextView msgLoadNum;
    private TextView msgType;
    private TextView msgText;
    private SharedPreferences sp;

    private final VKSdkListener sdkListener = new VKSdkListener() {

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            startService(new Intent(getApplicationContext(), ScheduleService.class));
        }

        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {
            startService(new Intent(getApplicationContext(), ScheduleService.class));
        }

        @Override
        public void onRenewAccessToken(VKAccessToken token) {
            startService(new Intent(getApplicationContext(), ScheduleService.class));
        }

        @Override
        public void onCaptchaError(VKError captchaError) {
            new VKCaptchaDialog(captchaError).show();
        }

        @Override
        public void onTokenExpired(VKAccessToken expiredToken) {
            VKSdk.authorize(VKScope.MESSAGES, VKScope.FRIENDS);
        }

        @Override
        public void onAccessDenied(VKError authorizationError) {
            Toast.makeText(getApplicationContext(), "Ошибка авторизации " + authorizationError, Toast.LENGTH_LONG).show();
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VKSdk.initialize(sdkListener, VK_APP_ID);
        userId = (TextView) findViewById(R.id.userId);
        schedulePeriod = (TextView) findViewById(R.id.schedulePeriod);
        isOnline = (TextView) findViewById(R.id.isOnline);
        isUnreadMsg = (TextView) findViewById(R.id.isUnreadMsg);
        msgLoadNum = (TextView) findViewById(R.id.loadMsgNum);
        msgType = (TextView) findViewById(R.id.msgType);
        msgText = (TextView) findViewById(R.id.msgText);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        VKUIHelper.onCreate(this);
    }

    @Override
    protected void onResume() {
        String userIdNum = sp.getString("userId", "");
        String schedPeriod = sp.getString("schedulePeriod", "");
        boolean isOnlineB = sp.getBoolean("isOnline", false);
        boolean isUnreadMsgB = sp.getBoolean("isUnreadMsg", false);
        String loadMsgNum = sp.getString("msgLoadNum", "");
        String messageType = sp.getString("msgType", "");
        String messageText = sp.getString("msgText", "");

        userId.setText(userIdNum);
        schedulePeriod.setText(schedPeriod + " мин");
        if (isOnlineB) {
            isOnline.setText("да");
        } else {
            isOnline.setText("нет");
        }
        if (isUnreadMsgB) {
            isUnreadMsg.setText("да");
        } else {
            isUnreadMsg.setText("нет");
        }
        msgLoadNum.setText(loadMsgNum);
        if (messageType.equals("text")) {
            msgType.setText("текст");
            msgText.setText(messageText);
        } else {
            msgType.setText("случайный набор чисел");
            msgText.setText("случайное целое число");
        }

        VKUIHelper.onResume(this);
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VKUIHelper.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onSettingsMenu(MenuItem item) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void onStartMenu(MenuItem item) {
        if (VKSdk.wakeUpSession()) {
            startService(new Intent(this, ScheduleService.class));
        } else {
            VKSdk.authorize(VKScope.MESSAGES, VKScope.FRIENDS);
        }
    }

    public void onStopMenu(MenuItem item) {
        stopService(new Intent(this, ScheduleService.class));
    }

    public void onRestartMenu(MenuItem item) {
        stopService(new Intent(this, ScheduleService.class));
        if (VKSdk.wakeUpSession()) {
            startService(new Intent(this, ScheduleService.class));
        } else {
            VKSdk.authorize(VKScope.MESSAGES, VKScope.FRIENDS);
        }
    }

    public void onAboutMenu(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("О программе")
                .setMessage("Программа для отправки сообщений vk.com с определенной периодичностью.\n" +
                        "Версия 0.1.1 от 9.01.2015 \n\n\n" +
                        "Шумайлов Андрей, 2015 год \n" +
                        "Just for fun ;)")
                .setCancelable(true)
                .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog about = builder.create();
        about.show();
    }

    public void onSettingsBtn(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void onClickStart(View view) {
        if (VKSdk.wakeUpSession()) {
            startService(new Intent(this, ScheduleService.class));
        } else {
            VKSdk.authorize(VKScope.MESSAGES, VKScope.FRIENDS);
        }
    }

    public void onClickStop(View view) {
        stopService(new Intent(this, ScheduleService.class));
    }

    public void onClickRestart(View view) {
        stopService(new Intent(this, ScheduleService.class));
        if (VKSdk.wakeUpSession()) {
            startService(new Intent(this, ScheduleService.class));
        } else {
            VKSdk.authorize(VKScope.MESSAGES, VKScope.FRIENDS);
        }
    }
}
