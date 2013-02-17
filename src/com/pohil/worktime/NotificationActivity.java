package com.pohil.worktime;

import java.nio.channels.AlreadyConnectedException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.pohil.worktime.SettingsActivity.KeyMeneger;
import com.pohil.worktime.UnlockerView.onUnlockListener;

public class NotificationActivity extends SherlockActivity {
	
	public static final String MODE_KEY = "com.pohil.worktime.MODE_KEY";
	public static final long CANCEL_TIME = 1000 * 60;  
	
	long[] vibrate_pattern = {0, 500, 1000};
	Ringtone ringtone;
	Vibrator vibrator;
	UnlockerView unlocker;
	TextView description;
	PowerManager.WakeLock wl;
	Timer timer;
	
	boolean enebleRington;
	boolean enebleVibrate;
	String ringtionName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		//KeyguardManager km1 = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
		//km1.newKeyguardLock("NotificationActivity").disableKeyguard();
		
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK|PowerManager.ACQUIRE_CAUSES_WAKEUP, "");
		wl.acquire();
		
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | 
	    		WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD );
	    
		setTheme(R.style.Theme_Sherlock);
		super.onCreate(savedInstanceState);
		timer = new Timer(true);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				NotificationActivity.this.finish();
			}
		}, CANCEL_TIME);
		
		setContentView(R.layout.notification);
		int mode;

		Bundle extras =  getIntent().getExtras();
		if (extras != null) {
			mode = extras.getInt(MODE_KEY);
		} else {
			mode = getIntent().getIntExtra(MODE_KEY, KeyMeneger.LUNCH_MODE);
		}
		KeyMeneger km = new KeyMeneger(mode);
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		enebleRington = pref.getBoolean(km.getVoiceEnebleKey(), false);
		enebleVibrate = pref.getBoolean(km.getVibrateEnebleKey(), false);
		ringtionName = pref.getString(km.getRingtonKey(), "");
		String time = pref.getString(km.getTimeKey(), TimePreference.DEFAULT_TIME);
		//AlarmProvider alarmProvider = new AlarmProvider(this, mode);
		//alarmProvider.create(time);
		//MlsWidgetProvider.sendBrodcast(this);
		
		TextView description = (TextView) findViewById(R.id.notify_description);
		unlocker = (UnlockerView) findViewById(R.id.unlocker);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		if (enebleRington) {
			Uri ringtoneUri = Uri.parse(ringtionName);
			Log.d("ringtionName", "enebleRington=" + ringtionName);
		    ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
		}
	    
		description.setText(getNotificationText(km.mode));
		unlocker.setOnUnlockListener(new onUnlockListener() {
			@Override
			public void onUnlock() {
				if (enebleRington) {
					ringtone.stop();
				}
				vibrator.cancel();
				unlocker.setVisibility(View.INVISIBLE);
			}
		});
		if (!enebleRington && !enebleVibrate) {
			unlocker.setVisibility(View.GONE);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (enebleRington) {
			ringtone.play();
		}
		if (enebleVibrate) {
			vibrator.vibrate(vibrate_pattern, 0);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause(); 
		if (enebleRington) {
			ringtone.stop();
		}
		vibrator.cancel();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		timer.cancel();
		if (wl.isHeld()) {
			wl.release();
		}
	}
	
	public String getNotificationText(int mode) {
		switch (mode) {
		case KeyMeneger.LUNCH_MODE:
			return getResources().getString(R.string.lunch_time);
		case KeyMeneger.DAY_MODE:
			return getResources().getString(R.string.end_time);
		}
		return null;
	}
	
}
