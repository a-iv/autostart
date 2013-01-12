package info.a_iv.autostart;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class NotificationActivity extends Activity {

	private static final int NOTIFICATION_ID = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(NOTIFICATION_ID);
		finish();
	}

	public static void addNotification(Context context, String message) {
		Notification notification = new Notification(R.drawable.ic_launcher,
				message, System.currentTimeMillis());
		notification.setLatestEventInfo(context, message, context
				.getText(R.string.app_name), PendingIntent.getActivity(context,
				0, new Intent(context, NotificationActivity.class),
				PendingIntent.FLAG_UPDATE_CURRENT));
		notification.defaults = Notification.DEFAULT_ALL;
		notification.flags = Notification.FLAG_SHOW_LIGHTS;
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		try {
			notificationManager.notify(NOTIFICATION_ID, notification);
		} catch (SecurityException e) {
		}
	}

}
