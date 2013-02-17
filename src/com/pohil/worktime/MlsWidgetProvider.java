package com.pohil.worktime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.SimpleTimeZone;
import java.util.Timer;
import java.util.TimerTask;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.text.method.TimeKeyListener;
import android.util.Log;
import android.widget.RemoteViews;

public class MlsWidgetProvider extends AppWidgetProvider{
	
	public static final String TIME_KEY = "com.pohil.worktime.MlsWidgetProvider.TIME_KEY";
	private static final long TIME_DELAY = 1000;
	
	private static HashMap<Integer, BackTimer> widgetTimer = new HashMap<Integer, MlsWidgetProvider.BackTimer>(); 
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);	
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context); 
		String time1 = pref.getString(SettingsActivity.LUNCH_TIME_KEY, context.getResources().getString(R.string.default_lunch_time));
		String time2 = pref.getString(SettingsActivity.DAY_TIME_KEY, context.getResources().getString(R.string.default_day_time));
		String time = TimeUtils.getEarlierTime(time1, time2);
		for (int id : widgetTimer.keySet()) {
			widgetTimer.get(id).setTargetTime(time);
		}
		setAlarmNotify(time, context);
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String time1 = pref.getString(SettingsActivity.LUNCH_TIME_KEY, context.getResources().getString(R.string.default_lunch_time));
        String time2 = pref.getString(SettingsActivity.DAY_TIME_KEY, context.getResources().getString(R.string.default_day_time));
        String time = TimeUtils.getEarlierTime(time1, time2);
		Timer timer = new Timer();
		for (int id : appWidgetIds) {
			BackTimer backTimer = new BackTimer(context, appWidgetManager, id);
			backTimer.setTargetTime(time);
			widgetTimer.put(id, backTimer);
			timer.schedule(backTimer, 0, TIME_DELAY);
		}
		setAlarmNotify(time, context);
	}
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		for (int id : appWidgetIds) {
			widgetTimer.get(id).cancel();
			widgetTimer.remove(id);
		}
	}
	
	/*
	public static void sendBrodcast(Context context) {
		Intent intent = new Intent(context, MlsWidgetProvider.class);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context); 
		String time1 = pref.getString(SettingsActivity.LUNCH_TIME_KEY, TimePreference.DEFAULT_TIME);
		String time2 = pref.getString(SettingsActivity.DAY_TIME_KEY, TimePreference.DEFAULT_TIME);
		intent.putExtra(MlsWidgetProvider.TIME_KEY, TimeUtils.getEarlierTime(time1, time2));
		context.sendBroadcast(intent);
	} */
	
	public static void notifyTimeChanged(Context context) {
		Intent intent = new Intent(context, MlsWidgetProvider.class);
		context.sendBroadcast(intent);
	}
	
	protected void onTimeChanged(Context context) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context); 
		String time1 = pref.getString(SettingsActivity.LUNCH_TIME_KEY, context.getResources().getString(R.string.default_lunch_time));
		String time2 = pref.getString(SettingsActivity.DAY_TIME_KEY, context.getResources().getString(R.string.default_day_time));
		String time = TimeUtils.getEarlierTime(time1, time2);
		for (int id : widgetTimer.keySet()) {
			widgetTimer.get(id).setTargetTime(time);
		}
		setAlarmNotify(time1, context);
		setAlarmNotify(time2, context);
	}
	
	private void setAlarmNotify(String time, Context context) {
		AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, TimeUtils.getHour(time));
		calendar.set(Calendar.MINUTE, TimeUtils.getMinute(time));
		calendar.set(Calendar.SECOND, 0);
		if (calendar.getTimeInMillis() > System.currentTimeMillis()) {
			Intent intent = new Intent(context, MlsWidgetProvider.class);
			PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			am.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmProvider.DAY_DURATION ,pi);
		}
	}
	
	private class BackTimer extends TimerTask {

        RemoteViews remoteViews;
        AppWidgetManager appWidgetManager;
        int widgetId;
        int targetHour, targetMinute;
        Time prevTime;
        Time currTime;
        SimpleDateFormat df;
        boolean isFistRunning = true;

		public BackTimer(Context context, AppWidgetManager appWidgetManager, int widgetId) {
	        this.appWidgetManager = appWidgetManager;
	        this.widgetId = widgetId;
	        remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget);
	        prevTime = new Time();
	        currTime = new Time();
	        prevTime.setToNow();
	        df = new SimpleDateFormat("HH:mm");
			df.setTimeZone(new SimpleTimeZone(0, ""));
		}
		
		public void setTargetTime(String targetTime) {
	        targetHour = TimeUtils.getHour(targetTime);
	        targetMinute = TimeUtils.getMinute(targetTime);
	        isFistRunning = true;
		}

		@Override
		public void run() {
			currTime.setToNow();
			if (currTime.minute != prevTime.minute || currTime.hour != prevTime.hour || isFistRunning) {
				long remainTime = TimeUtils.getRemainTime(targetHour, targetMinute);
				if (remainTime < 0) {
					remainTime = 0;
				}
				remoteViews.setTextViewText(R.id.timer ,df.format(remainTime));
				appWidgetManager.updateAppWidget(widgetId, remoteViews);
				prevTime.setToNow();
			}
			isFistRunning = false;
		}
	}

}
