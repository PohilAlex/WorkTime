package com.pohil.worktime;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;


import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.Menu;

public class MainActivity extends SherlockActivity {
	
	TimerView timerLunch;
	TimerView timerDay;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		timerLunch = (TimerView) findViewById(R.id.lunch_timer);
		timerDay = (TimerView) findViewById(R.id.day_timer);
		
		//Intent intent = new Intent(this, NotificationActivity.class);
		//startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
	   MenuInflater inflater = getSupportMenuInflater();
	   inflater.inflate(R.menu.main,  menu);
	   return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		default:
			return false;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setUpTimer(timerLunch, SettingsActivity.LUNCH_TIME_KEY, getResources().getString(R.string.default_lunch_time));
		timerLunch.start();
		
		setUpTimer(timerDay, SettingsActivity.DAY_TIME_KEY, getResources().getString(R.string.default_day_time));
		timerDay.start();
	}
	
	protected void setUpTimer(TimerView timer, String key, String defaultTime) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		String[] time = sharedPref.getString(key, defaultTime).split(":");
		int hour = Integer.parseInt(time[0]);
		int minute = Integer.parseInt(time[1]);
		timer.setEndTime(hour, minute);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		timerLunch.stop();
		timerDay.stop();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
           this.moveTaskToBack(true);
           return true;
        }
        return super.onKeyDown(keyCode, event);
	}
	
}
