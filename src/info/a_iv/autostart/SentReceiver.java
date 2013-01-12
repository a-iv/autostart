package info.a_iv.autostart;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class SentReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("Autostart sent", Integer.toString(getResultCode()));
		boolean success = getResultCode() == Activity.RESULT_OK;
		String message = context.getString(success ? R.string.sent
				: R.string.fail);
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		if (!success
				|| sharedPreferences
						.getBoolean(
								context.getString(R.string.show_notification),
								context.getResources().getBoolean(
										R.bool.default_notification))) {
			NotificationActivity.addNotification(context, message);
		} else {
			Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
		}
	}

}