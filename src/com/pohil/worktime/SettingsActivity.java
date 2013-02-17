package com.pohil.worktime;

import java.util.Calendar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.util.Log;
import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class SettingsActivity extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener, OnPreferenceChangeListener{
	
	public static final String LUNCH_TIME_KEY = "lunch_time";
	public static final String LUNCH_RINGTON_KEY = "lunch_rington";
	public static final String LUNCH_NOTIFY_ENEBLE = "lunch_notification_enable";
	public static final String LUNCH_NOTIFY_VOICE_ENEBLE = "lunch_voice_notification_enable";
	public static final String LUNCH_NOTIFY_VIBRATE_ENEBLE = "lunch_vibrate_notification_enable";
	public static final String LUNCH_NOTIFY_SETTINGS_KEY = "lunch_notification_settings";

	public static final String DAY_TIME_KEY = "day_time";
	public static final String DAY_RINGTON_KEY = "day_rington";
	public static final String DAY_NOTIFY_ENEBLE = "day_notification_enable";
	public static final String DAY_NOTIFY_VOICE_ENEBLE = "day_voice_notification_enable";
	public static final String DAY_NOTIFY_VIBRATE_ENEBLE = "day_vibrate_notification_enable";
	public static final String DAY_NOTIFY_SETTINGS_KEY = "day_notification_settings";
	
	RingtonePreference lunchRingPref;
	RingtonePreference dayRingPref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock);
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		lunchRingPref = (RingtonePreference) findPreference(LUNCH_RINGTON_KEY);
		dayRingPref = (RingtonePreference) findPreference(DAY_RINGTON_KEY);
		updateRingtoneSummary(lunchRingPref);
		updateRingtoneSummary(dayRingPref);
		updateNotificationSummury(new KeyMeneger(KeyMeneger.LUNCH_MODE));
		updateNotificationSummury(new KeyMeneger(KeyMeneger.DAY_MODE));
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    getPreferenceScreen().getSharedPreferences()
	            .registerOnSharedPreferenceChangeListener(this);
	    
	    lunchRingPref.setOnPreferenceChangeListener(this);
	    dayRingPref.setOnPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
	    super.onPause();
	    getPreferenceScreen().getSharedPreferences()
	            .unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		
		if (key.equals(LUNCH_TIME_KEY) || (key.equals(LUNCH_NOTIFY_ENEBLE))) {
			onAlarmConigChanges(sharedPreferences, new KeyMeneger(KeyMeneger.LUNCH_MODE));
		}
		if (key.equals(DAY_TIME_KEY) || (key.equals(DAY_NOTIFY_ENEBLE))) {
			onAlarmConigChanges(sharedPreferences, new KeyMeneger(KeyMeneger.DAY_MODE));
		}
		
		if (key.equals(LUNCH_TIME_KEY) || key.equals(DAY_TIME_KEY)) {
			//MlsWidgetProvider.sendBrodcast(this);
			//createWidgetAlarm(sharedPreferences, key);
			MlsWidgetProvider.notifyTimeChanged(this);
		}
		if (key.equals(LUNCH_NOTIFY_VOICE_ENEBLE) || key.equals(LUNCH_NOTIFY_VIBRATE_ENEBLE) || key.equals(LUNCH_RINGTON_KEY)) {
			updateNotificationSummury(new KeyMeneger(KeyMeneger.LUNCH_MODE));
		}
		if (key.equals(DAY_NOTIFY_VOICE_ENEBLE) || key.equals(DAY_NOTIFY_VIBRATE_ENEBLE) || key.equals(DAY_RINGTON_KEY)) {
			updateNotificationSummury(new KeyMeneger(KeyMeneger.DAY_MODE));
		}
		sharedPreferences.edit().commit();
	}
	
	
	protected void createWidgetAlarm(SharedPreferences sharedPreferences, String key) {
		//TODO Refactor this method. To many cope paste
		AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
		String time = sharedPreferences.getString(key, TimePreference.DEFAULT_TIME);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, TimeUtils.getHour(time));
		calendar.set(Calendar.MINUTE, TimeUtils.getMinute(time));
		calendar.set(Calendar.SECOND, 0);
		if (calendar.getTimeInMillis() > System.currentTimeMillis()) {
			Log.d("TEST", "createWidgetAlarm");
			Intent intent = new Intent(this, MlsWidgetProvider.class);
			String timeKey;
			if (key.equals(LUNCH_TIME_KEY)) {
				timeKey = sharedPreferences.getString(SettingsActivity.DAY_TIME_KEY, TimePreference.DEFAULT_TIME);
			} else {
				timeKey = sharedPreferences.getString(SettingsActivity.LUNCH_TIME_KEY, TimePreference.DEFAULT_TIME);
			}
			intent.putExtra(MlsWidgetProvider.TIME_KEY, timeKey);
			PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			am.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmProvider.DAY_DURATION ,pi);
		}
	}
	
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
	
	protected void updateNotificationSummury(KeyMeneger km) {
		String summary = "";
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (sharedPreferences.getBoolean(km.getVibrateEnebleKey(), false)) {
			summary = "Vibrate,  ";
		} else {
			summary = "No vibrate,  ";
		}
		if (sharedPreferences.getBoolean(km.getVoiceEnebleKey(), false)) {
			String ringtonUri = sharedPreferences.getString(km.getRingtonKey(), ""); 
			String ringtonName = RingtoneManager.getRingtone(this, Uri.parse(ringtonUri)).getTitle(this);
			summary += "Rington - " + ringtonName;
		} else {
			summary += "No rington";
		}
		findPreference(km.getNotifySettingsKey()).setSummary(summary);
		onContentChanged();
	}
	
	protected void onAlarmConigChanges(SharedPreferences sharedPreferences, KeyMeneger km) {
		AlarmProvider alarmProvider = new AlarmProvider(this, km.mode);
		if (sharedPreferences.getBoolean(km.getNotifyEnebleKey(), false)) {
			String time = sharedPreferences.getString(km.getTimeKey(), TimePreference.DEFAULT_TIME);
			alarmProvider.create(time);
		} else {
			alarmProvider.cancel();
		}
	}
	
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference.getKey().equals(LUNCH_RINGTON_KEY) || preference.getKey().equals(DAY_RINGTON_KEY)) {
			updateRingtoneSummary((RingtonePreference) preference, (String)newValue);
			if (preference.getKey().equals(LUNCH_RINGTON_KEY)){
				updateNotificationSummury(new KeyMeneger(KeyMeneger.LUNCH_MODE));
			} else {
				updateNotificationSummury(new KeyMeneger(KeyMeneger.DAY_MODE));
			}
		}
	    return true;
	}

	private void updateRingtoneSummary(RingtonePreference preference, String uri) {
		Uri ringtoneUri = Uri.parse((String) uri);
	    Ringtone ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
	    if (ringtone != null) {
	        preference.setSummary(ringtone.getTitle(this));
	    } else {
	        preference.setSummary("Silent");
	    }
	    onContentChanged();
	}
	
	protected void updateRingtoneSummary(RingtonePreference pref) {
		String uri = getPreferenceScreen().getSharedPreferences().getString(pref.getKey(), "");
		updateRingtoneSummary(pref, uri);
	}
	
	static class KeyMeneger {
		static public final int LUNCH_MODE = 0;
		static public final int DAY_MODE = 1;
		public int mode;
		
		public KeyMeneger(int mode) {
			this.mode = mode;
		}
		
		public String getTimeKey() {
			switch (mode) {
			case LUNCH_MODE:
				return LUNCH_TIME_KEY;
			case DAY_MODE:
				return DAY_TIME_KEY;
			}
			return null;
		}
		
		public String getRingtonKey() {
			switch (mode) {
			case LUNCH_MODE:
				return LUNCH_RINGTON_KEY;
			case DAY_MODE:
				return DAY_RINGTON_KEY;
			}
			return null;
		}
		
		public String getNotifyEnebleKey() {
			switch (mode) {
			case LUNCH_MODE:
				return LUNCH_NOTIFY_ENEBLE;
			case DAY_MODE:
				return DAY_NOTIFY_ENEBLE;
			}
			return null;
		}
		
		public String getVoiceEnebleKey() {
			switch (mode) {
			case LUNCH_MODE:
				return LUNCH_NOTIFY_VOICE_ENEBLE;
			case DAY_MODE:
				return DAY_NOTIFY_VOICE_ENEBLE;
			}
			return null;
		}
		
		public String getVibrateEnebleKey() {
			switch (mode) {
			case LUNCH_MODE:
				return LUNCH_NOTIFY_VIBRATE_ENEBLE;
			case DAY_MODE:
				return DAY_NOTIFY_VIBRATE_ENEBLE;
			}
			return null;
		}
		
		public String getNotifySettingsKey() {
			switch (mode) {
			case LUNCH_MODE:
				return LUNCH_NOTIFY_SETTINGS_KEY;
			case DAY_MODE:
				return DAY_NOTIFY_SETTINGS_KEY;
			}
			return null;
		}
		
	}
}