package com.pohil.worktime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.SimpleTimeZone;

import android.text.format.Time;

public class TimeUtils {
	
	public static SimpleDateFormat df;
	
	public static int getHour(String time) {
		String hour = time.split(":")[0];
		return Integer.parseInt(hour);
	}
	
	public static int getMinute(String time) {
		String minute = time.split(":")[1];
		return Integer.parseInt(minute);
	}
	
	public static long getRemainTime(Time time, int targetHour, int targetMinute) {
		/*Time currTime = new Time();
		currTime.setToNow();*/
		Time nextTime = new Time(time);
		nextTime.hour = targetHour;
		nextTime.minute = targetMinute;
		nextTime.second = 0;
		/*if (nextTime.before(time)) {
			nextTime.monthDay++;
		}*/
		return nextTime.toMillis(false) - time.toMillis(true);
	}
	
	public static long getRemainTime(int targetHour, int targetMinute) {
		Time currTime = new Time();
		currTime.setToNow();
		return getRemainTime(currTime, targetHour, targetMinute);
	}
	
	protected static DateFormat getDateFormat() {
		if (df == null) {
			df = new SimpleDateFormat("HH:mm");
			df.setTimeZone(new SimpleTimeZone(0, "")); 
		}
		return df;
	}
	
	public static String getEarlierTime(String time1, String time2) {
		long remain1 = getRemainTime(getHour(time1), getMinute(time1));
		long remain2 = getRemainTime(getHour(time2), getMinute(time2));
		
		if (remain1 > 0 && remain2 > 0) {
			if (remain1 < remain2) {
				return time1;
			} else {
				return time2;
			}
		} /*else {
			if (remain1 > 0) {
				return time1;
			} 
			if (remain2 > 0) {
				return time2;
			}
		}*/
		
		if (remain1 <= 0 && remain2 <= 0) {
			if (remain1 < remain2) {
				return time1;
			} else {
				return time2;
			}
		}
		if (remain1 > 0) {
			return time1;
		} 
		if (remain2 > 0) {
			return time2;
		}
		return time1;
	}
	
	
	
	
}
