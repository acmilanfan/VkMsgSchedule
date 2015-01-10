package com.acmilanfan.vkmsgschedule;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class MsgService extends Service {

    private VKRequest currentRequest1;
    private VKRequest currentRequest2;
    private VKRequest currentRequest3;
    private VKRequest currentRequest4;
    private String userId;
    private boolean isOnline;
    private boolean isUnreadMsg;
    private String loadMsgNum;
    private String msgType;
    private String msgText;
    private int userIntId;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        userId = intent.getStringExtra("userId");
        isOnline = intent.getBooleanExtra("isOnline", false);
        isUnreadMsg = intent.getBooleanExtra("isUnreadMsg", false);
        loadMsgNum = intent.getStringExtra("msgLoadNum");
        msgType = intent.getStringExtra("msgType");
        msgText = intent.getStringExtra("msgText");
        getIntUserId();

        return Service.START_STICKY;
    }

    private void doRequests() {
        if (isOnline && isUnreadMsg) {
            doRequestFull(msgType, msgText, Integer.parseInt(loadMsgNum));
        } else if (isOnline) {
            doRequestOnline(msgType, msgText);
        } else if (isUnreadMsg) {
            doRequestUnreadMsg(msgType, msgText, Integer.parseInt(loadMsgNum));
        } else {
            doRequestJustMsg(msgType, msgText);
        }
    }

    private void getIntUserId() {
        if (currentRequest4 != null) {
            currentRequest4.cancel();
        }
        currentRequest4 = VKApi.users().get(VKParameters.from(
                VKApiConst.USER_IDS, userId,
                VKApiConst.FIELDS, "id"));

        currentRequest4.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) throws JSONException {
                JSONArray resp = response.json.getJSONArray("response");
                JSONObject jsonObject = resp.getJSONObject(0);
                userIntId = jsonObject.getInt("id");
                doRequests();
                super.onComplete(response);
            }

            @Override
            public void onError(VKError error) {
                Toast.makeText(getApplicationContext(), "Ошибка при получении числового Id " + error, Toast.LENGTH_LONG).show();
                super.onError(error);
            }
        });
    }

    private void doRequestFull(String msgType, String msgText, int loadMsgNum) {
        String message;

        if (currentRequest1 != null) {
            currentRequest1.cancel();
        }
        if (currentRequest2 != null) {
            currentRequest2.cancel();
        }
        if (currentRequest3 != null) {
            currentRequest3.cancel();
        }

        if (msgType.equals("int")) {
            Random rand = new Random();
            message = "" + rand.nextInt((999 - 1) + 1) + 1;
        } else {
            message = msgText;
        }

        currentRequest1 = new VKRequest("messages.get", VKParameters.from(
                "out", 1,
                VKApiConst.COUNT, loadMsgNum));
        currentRequest2 = VKApi.users().get(VKParameters.from(
                VKApiConst.USER_ID, userIntId,
                VKApiConst.FIELDS, "online"));
        currentRequest3 = new VKRequest("messages.send", VKParameters.from(
                VKApiConst.USER_ID, userIntId,
                VKApiConst.MESSAGE, message));

        currentRequest1.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) throws JSONException {
                super.onComplete(response);

                JSONObject json = response.json.getJSONObject("response");
                JSONArray resp = json.getJSONArray("items");

                for (int i = 0; i < resp.length(); i++) {
                    JSONObject jo = resp.getJSONObject(i);
                    if (jo.getInt("user_id") == userIntId && jo.getInt("read_state") == 0) {
                        currentRequest2.executeWithListener(new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(VKResponse response) throws JSONException {
                                super.onComplete(response);

                                JSONArray resp = response.json.getJSONArray("response");
                                JSONObject jsonObject = resp.getJSONObject(0);
                                if (jsonObject.getInt("online") == 1) {
                                    currentRequest3.executeWithListener(new VKRequest.VKRequestListener() {
                                        @Override
                                        public void onComplete(VKResponse response) throws JSONException {
                                            Toast.makeText(getApplicationContext(), "Сообщение отправлено", Toast.LENGTH_LONG).show();
                                            super.onComplete(response);
                                        }

                                        @Override
                                        public void onError(VKError error) {
                                            Toast.makeText(getApplicationContext(), "Ошибка при отправке сообщения: " + error, Toast.LENGTH_LONG).show();
                                            super.onError(error);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onError(VKError error) {
                                Toast.makeText(getApplicationContext(), "Ошибка при проверке онлайна: " + error, Toast.LENGTH_LONG).show();
                                super.onError(error);
                            }
                        });
                        break;
                    }
                }
            }

            @Override
            public void onError(VKError error) {
                Toast.makeText(getApplicationContext(), "Ошибка при получении сообщений: " + error, Toast.LENGTH_LONG).show();

                super.onError(error);
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
            }
        });
    }


    private void doRequestOnline(String msgType, String msgText) {
        String message;

        if (currentRequest2 != null) {
            currentRequest2.cancel();
        }
        if (currentRequest3 != null) {
            currentRequest3.cancel();
        }

        if (msgType.equals("int")) {
            Random rand = new Random();
            message = "" + rand.nextInt((999 - 1) + 1) + 1;
        } else {
            message = msgText;
        }

        currentRequest2 = VKApi.users().get(VKParameters.from(
                VKApiConst.USER_ID, userIntId,
                VKApiConst.FIELDS, "online"));
        currentRequest3 = new VKRequest("messages.send", VKParameters.from(
                VKApiConst.USER_ID, userIntId,
                VKApiConst.MESSAGE, message));

        currentRequest2.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) throws JSONException {
                super.onComplete(response);

                JSONArray resp = response.json.getJSONArray("response");
                JSONObject jsonObject = resp.getJSONObject(0);
                if (jsonObject.getInt("online") == 1) {
                    currentRequest3.executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) throws JSONException {
                            Toast.makeText(getApplicationContext(), "Сообщение отправлено", Toast.LENGTH_LONG).show();
                            super.onComplete(response);
                        }

                        @Override
                        public void onError(VKError error) {
                            Toast.makeText(getApplicationContext(), "Ошибка при отправке сообщения: " + error, Toast.LENGTH_LONG).show();
                            super.onError(error);
                        }
                    });
                }
            }

            @Override
            public void onError(VKError error) {
                Toast.makeText(getApplicationContext(), "Ошибка при проверке онлайна: " + error, Toast.LENGTH_LONG).show();
                super.onError(error);
            }
        });
    }

    private void doRequestUnreadMsg(String msgType, String msgText, int loadMsgNum) {

        String message;

        if (currentRequest1 != null) {
            currentRequest1.cancel();
        }
        if (currentRequest3 != null) {
            currentRequest3.cancel();
        }

        if (msgType.equals("int")) {
            Random rand = new Random();
            message = "" + rand.nextInt((999 - 1) + 1) + 1;
        } else {
            message = msgText;
        }

        currentRequest1 = new VKRequest("messages.get", VKParameters.from(
                "out", 1,
                VKApiConst.COUNT, loadMsgNum));
        currentRequest3 = new VKRequest("messages.send", VKParameters.from(
                VKApiConst.USER_ID, userIntId,
                VKApiConst.MESSAGE, message));

        currentRequest1.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) throws JSONException {
                super.onComplete(response);

                JSONObject json = response.json.getJSONObject("response");
                JSONArray resp = json.getJSONArray("items");

                for (int i = 0; i < resp.length(); i++) {
                    JSONObject jo = resp.getJSONObject(i);
                    if (jo.getInt("user_id") == userIntId && jo.getInt("read_state") == 0) {
                        currentRequest3.executeWithListener(new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(VKResponse response) throws JSONException {
                                Toast.makeText(getApplicationContext(), "Сообщение отправлено", Toast.LENGTH_LONG).show();
                                super.onComplete(response);
                            }

                            @Override
                            public void onError(VKError error) {
                                Toast.makeText(getApplicationContext(), "Ошибка при отправке сообщения: " + error, Toast.LENGTH_LONG).show();
                                super.onError(error);
                            }
                        });
                    }
                }
            }

            @Override
            public void onError(VKError error) {
                Toast.makeText(getApplicationContext(), "Ошибка при получении сообщений: " + error, Toast.LENGTH_LONG).show();

                super.onError(error);
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
            }
        });
    }

    private void doRequestJustMsg(String msgType, String msgText) {

        String message;

        if (currentRequest3 != null) {
            currentRequest3.cancel();
        }

        if (msgType.equals("int")) {
            Random rand = new Random();
            message = "" + rand.nextInt((999 - 1) + 1) + 1;
        } else {
            message = msgText;
        }

        currentRequest3 = new VKRequest("messages.send", VKParameters.from(
                VKApiConst.USER_ID, userIntId,
                VKApiConst.MESSAGE, message));

        currentRequest3.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) throws JSONException {
                Toast.makeText(getApplicationContext(), "Сообщение отправлено", Toast.LENGTH_LONG).show();
                super.onComplete(response);
                stopSelf();
            }

            @Override
            public void onError(VKError error) {
                Toast.makeText(getApplicationContext(), "Ошибка запроса 3: " + error, Toast.LENGTH_LONG).show();
                super.onError(error);
                stopSelf();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (currentRequest1 != null) {
            currentRequest1.cancel();
        }
        if (currentRequest2 != null) {
            currentRequest2.cancel();
        }
        if (currentRequest3 != null) {
            currentRequest3.cancel();
        }
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
