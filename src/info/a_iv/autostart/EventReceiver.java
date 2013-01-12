package info.a_iv.autostart;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;

public class EventReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		String phone = sharedPreferences.getString(
				context.getString(R.string.phone_number), "");
		String message = sharedPreferences.getString(
				context.getString(R.string.message_value), "");
		Log.d("Autostart send", phone + ": " + message);
		SmsManager smsManager = SmsManager.getDefault();
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
				new Intent(context, SentReceiver.class), 0);
		smsManager.sendTextMessage(phone, null, message, pendingIntent, null);
	}

}
