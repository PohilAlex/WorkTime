package com.pohil.worktime;

import java.util.Calendar;

import com.pohil.worktime.SettingsActivity.KeyMeneger;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AlarmProvider {
	
	private static final int LUNCH_NOTIFY_CODE = 4987131;
	private static final int DAY_NOTIFY_CODE = 6847345;
	public static final int DAY_DURATION = 1000 * 60 * 60 * 24;
	
	Context context;
	int mode;
	
	public AlarmProvider(Context context, int mode) {
		this.context = context;
		this.mode = mode;
	}

	public  void create(String time) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, TimeUtils.getHour(time));
		calendar.set(Calendar.MINUTE, TimeUtils.getMinute(time));
		calendar.set(Calendar.SECOND, 0);
		if (calendar.getTimeInMillis() > System.currentTimeMillis()) {
			Bundle bundle = new Bundle();
			bundle.putInt(NotificationActivity.MODE_KEY, mode);
			AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
			//am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), getPendingIntent(bundle));
			am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), DAY_DURATION ,getPendingIntent(bundle));
		}
	}
	
	public void cancel() {
		AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
		am.cancel(getPendingIntent());
	}
	
	
	private int getNotifyCode() {
		switch (mode) {
		case KeyMeneger.LUNCH_MODE:
			return LUNCH_NOTIFY_CODE;
		case KeyMeneger.DAY_MODE:
			return DAY_NOTIFY_CODE;
		}
		return 0;
	}
	
	private  PendingIntent getPendingIntent() {
		return getPendingIntent(null);
	}
	
	private  PendingIntent getPendingIntent(Bundle bundle) {
        Intent intent = new Intent(context, NotificationActivity.class);
		//Intent intent = new Intent(context, AlarmReceiver.class);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		return PendingIntent.getActivity(context, getNotifyCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //return PendingIntent.getBroadcast(context, getNotifyCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
}
