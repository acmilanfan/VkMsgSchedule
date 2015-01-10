package com.acmilanfan.vkmsgschedule;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity {

    private EditTextPreference userId;
    private ListPreference msgType;
    private EditTextPreference msgText;
    private EditTextPreference schedulePeriod;
    private CheckBoxPreference isOnilneSend;
    private CheckBoxPreference isUnreadMsg;
    private EditTextPreference msgLoadNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceScreen rootScreen = getPreferenceManager().createPreferenceScreen(this);
        setPreferenceScreen(rootScreen);

        userId = new EditTextPreference(this);
        userId.setKey("userId");
        userId.setTitle("Id пользователя");
        userId.setSummary("Введите id пользователя");
        userId.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue.toString().contains("vk.com")) {
                    Toast.makeText(getApplicationContext(), "Вводите только id или короткое имя пользователя", Toast.LENGTH_LONG).show();
                    userId.setText("");
                } else {
                    userId.setText(newValue.toString());
                }
                return false;
            }
        });

        rootScreen.addPreference(userId);

        schedulePeriod = new EditTextPreference(this);
        schedulePeriod.setKey("schedulePeriod");
        schedulePeriod.setTitle("Период отправки");
        schedulePeriod.setSummary("Введите период отправки сообщений");
        schedulePeriod.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean valid = isNumber(newValue.toString());
                if (!valid) {
                    Toast.makeText(getApplicationContext(), "Неверное значение поля, попробуйте ввесли целое число", Toast.LENGTH_LONG).show();
                    schedulePeriod.setText("");
                } else {
                    schedulePeriod.setText(newValue.toString());
                }
                return false;
            }
        });

        rootScreen.addPreference(schedulePeriod);

        msgType = new ListPreference(this);
        msgType.setKey("msgType");
        msgType.setTitle("Тип сообщения");
        msgType.setSummary("Выберите тип сообщения");
        msgType.setEntries(R.array.msgType);
        msgType.setEntryValues(R.array.msgType_values);
        msgType.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                textDepend(newValue.toString());
                return true;
            }
        });

        rootScreen.addPreference(msgType);

        msgText = new EditTextPreference(this);
        msgText.setKey("msgText");
        msgText.setTitle("Текст сообщения");
        msgText.setSummary("Введите текст сообщения");
        textDepend(msgType.getValue());

        rootScreen.addPreference(msgText);

        isOnilneSend = new CheckBoxPreference(this);
        isOnilneSend.setKey("isOnline");
        isOnilneSend.setTitle("Оптравка в онлайн");
        isOnilneSend.setSummary("Отправка сообщений тольк тогда, когда пользователь находится онлайн");

        rootScreen.addPreference(isOnilneSend);

        isUnreadMsg = new CheckBoxPreference(this);
        isUnreadMsg.setKey("isUnreadMsg");
        isUnreadMsg.setTitle("Отправка при непрочитанных сообщениях");
        isUnreadMsg.setSummary("Отправлять сообщение только тогда, когда есть непрочитанные пользователем сообщения");

        rootScreen.addPreference(isUnreadMsg);

        msgLoadNum = new EditTextPreference(this);
        msgLoadNum.setKey("msgLoadNum");
        msgLoadNum.setTitle("Количество загружаемых сообщений");
        msgLoadNum.setSummary("Количество сообщений, которые будут загружаться для поиска непрочитанных");
        msgLoadNum.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean valid = isNumber(newValue.toString());
                if (!valid) {
                    Toast.makeText(getApplicationContext(), "Неверное значение поля, попробуйте ввесли целое число", Toast.LENGTH_LONG).show();
                    msgLoadNum.setText("");
                } else {
                    msgLoadNum.setText(newValue.toString());
                }
                return false;
            }
        });

        rootScreen.addPreference(msgLoadNum);

        msgLoadNum.setDependency("isUnreadMsg");
    }

    private void textDepend(final String value) {

        int index = msgType.findIndexOfValue(value);

        if (index == 0) {
            msgText.setEnabled(true);
        } else {
            msgText.setEnabled(false);
        }
    }

    private boolean isNumber(final String value) {
        for (char c : value.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
}
