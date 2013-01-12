package info.a_iv.autostart;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;

public class MainActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	private static final int[] DAYS_OF_WEEK = new int[] { Calendar.MONDAY,
			Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY,
			Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY };
	private static final int[] ENABLED_IDS = new int[] {
			R.string.monday_enabled, R.string.tuesday_enabled,
			R.string.wednesday_enabled, R.string.thursday_enabled,
			R.string.friday_enabled, R.string.saturday_enabled,
			R.string.sunday_enabled };
	private static final int[] TIME_IDS = new int[] { R.string.monday_time,
			R.string.tuesday_time, R.string.wednesday_time,
			R.string.thursday_time, R.string.friday_time,
			R.string.saturday_time, R.string.sunday_time };
	private static PendingIntent[] PENDING_INTENTS = new PendingIntent[DAYS_OF_WEEK.length];

	private SharedPreferences sharedPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.main);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
		update();
	}

	@Override
	protected void onPause() {
		super.onPause();
		sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		update();
	}

	private void update() {
		updateAlarms(this);
		for (int index = 0; index < getPreferenceScreen().getPreferenceCount(); index++) {
			Preference preference = getPreferenceScreen().getPreference(index);
			if (preference instanceof TimePreference
					|| preference instanceof EditTextPreference)
				preference.setSummary(sharedPreferences.getString(
						preference.getKey(), ""));
		}
	}

	public static void updateAlarms(Context context) {
		for (int index = 0; index < DAYS_OF_WEEK.length; index++)
			schedule(context, index);
	}

	private static void schedule(Context context, int index) {
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pendingIntent = PENDING_INTENTS[index];
		if (pendingIntent == null) {
			Intent intent = new Intent(context, EventReceiver.class);
			intent.setData(Uri.parse(String.valueOf(index)));
			pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			PENDING_INTENTS[index] = pendingIntent;
		}
		if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
				context.getString(ENABLED_IDS[index]),
				context.getResources().getBoolean(R.bool.default_enabled))) {
			Log.d("Autostart canceled", Integer.toString(DAYS_OF_WEEK[index]));
			alarmManager.cancel(pendingIntent);
			return;
		}
		int minutes = getMinutes(context, TIME_IDS[index]);
		Calendar now = Calendar.getInstance();
		Calendar calendar = (Calendar) now.clone();
		calendar.set(Calendar.HOUR_OF_DAY, minutes / 60);
		calendar.set(Calendar.MINUTE, minutes % 60);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		if (calendar.get(Calendar.DAY_OF_WEEK) != DAYS_OF_WEEK[index]
				|| now.after(calendar)) {
			while (true) {
				calendar.add(Calendar.DAY_OF_YEAR, 1);
				if (calendar.get(Calendar.DAY_OF_WEEK) == DAYS_OF_WEEK[index])
					break;
			}
		}
		Log.d("Autostart scheduled",
				Integer.toString(DAYS_OF_WEEK[index])
						+ " "
						+ DateFormat
								.getDateFormat(context.getApplicationContext())
								.format(calendar.getTime()).toString()
						+ " "
						+ DateFormat
								.getTimeFormat(context.getApplicationContext())
								.format(calendar.getTime()).toString());
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7,
				pendingIntent);
	}

	private static int getMinutes(Context context, int key) {
		String value = PreferenceManager.getDefaultSharedPreferences(context)
				.getString(context.getString(key),
						context.getString(R.string.default_time));
		Integer minutes = parseMinutes(value);
		if (minutes != null)
			return minutes;
		minutes = parseMinutes(context.getString(R.string.default_time));
		if (minutes != null)
			return minutes;
		return 0;
	}

	private static Integer parseMinutes(String value) {
		String[] pieces = value.split(":");
		if (pieces.length != 2)
			return null;
		int hour;
		try {
			hour = Integer.parseInt(pieces[0]);
		} catch (NumberFormatException e) {
			return null;
		}
		int minute;
		try {
			minute = Integer.parseInt(pieces[1]);
		} catch (NumberFormatException e) {
			return null;
		}
		return hour * 60 + minute;
	}

}
