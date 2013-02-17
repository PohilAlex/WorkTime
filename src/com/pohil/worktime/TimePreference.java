package com.pohil.worktime;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

/**
 * Custom preference for time selection. Hour and minute are persistent and
 * stored separately as ints in the underlying shared preferences under keys
 * KEY.hour and KEY.minute, where KEY is the preference's key.
 */
public class TimePreference extends DialogPreference {
	
	public static final String DEFAULT_TIME = "00:00";
	
	private TimePicker timePicker;
	
	public TimePreference(Context context, AttributeSet attributes) {
		super(context, attributes);
	}
	
	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getString(index);
	}
	
	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
		String currTime;
		if (restorePersistedValue) {
			currTime = getPersistedString(DEFAULT_TIME);
		} else {
			currTime = (String) defaultValue;
			persistString(currTime);
		}
	}
	
	@Override
	protected View onCreateView(ViewGroup parent) {
		setSummary(getPersistedString(DEFAULT_TIME));
		return super.onCreateView(parent);
	}
	
	@Override
	public void onBindDialogView(View view) {
		super.onBindDialogView(view);
		
		String[] time = getPersistedString(DEFAULT_TIME).split(":");
		timePicker = (TimePicker) view.findViewById(R.id.prefTimePicker);
		timePicker.setCurrentHour(Integer.parseInt(time[0]));
		timePicker.setCurrentMinute(Integer.parseInt(time[1]));
		timePicker.setIs24HourView(true);
	}


	@Override
	protected void onDialogClosed(boolean okToSave) {
		super.onDialogClosed(okToSave);
		if (okToSave) {
			timePicker.clearFocus();
			String time = getCorrectTime(timePicker.getCurrentHour(), timePicker.getCurrentMinute());
			persistString(time);
			setSummary(time);
		}
	}
	
	protected String getCorrectTime(Integer hour, Integer minute) {
		String minStr = minute.toString();;
		if (minStr.length() == 1) {
			minStr = "0" + minStr; 
		} 
		return hour.toString() + ":" + minStr;
		
	}
}