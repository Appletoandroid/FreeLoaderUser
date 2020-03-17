package com.Appleto.FreeloaderUser.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;

import com.Appleto.FreeloaderUser.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


public class NotificationHelper {

    private Context mContext;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private String bitmapUrl = "";
    private Bitmap myBitmap;
    private Bitmap bm;
    private PendingIntent resultPendingIntent = null;

    public NotificationHelper(Context context) {
        mContext = context;
    }

    /**
     * Create and push the notification
     */
    public void createNotification(String title, String message, boolean isBigNotif, String imageUrl, Intent intent) {
        /**Creates an explicit intent for an Activity in your app**/

        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (intent != null) {
            resultPendingIntent = PendingIntent.getActivity(mContext,
                    0 /* Request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        mBuilder = new NotificationCompat.Builder(mContext, title);
        if (isBigNotif) {
            showBigNotification(imageUrl, title, message, resultPendingIntent);
        } else {
            showSmallNotification(title, message, resultPendingIntent);
        }

    }

    private void showSmallNotification(String title, String message, PendingIntent resultPendingIntent) {
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText(message);
        mBuilder.setSmallIcon(R.drawable.ic_launcher_background);
        mBuilder.setContentTitle(title)
                .setContentText(message)
                .setStyle(bigTextStyle)
                .setAutoCancel(true)
                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        if (resultPendingIntent != null) {
            mBuilder.setContentIntent(resultPendingIntent);
        }

        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }

    private void showBigNotification(String imageUrl, String title, String message, PendingIntent resultPendingIntent) {
        new sendNotification(mContext, resultPendingIntent).execute(imageUrl, title, message);
    }

    @SuppressLint("StaticFieldLeak")
    private class sendNotification extends AsyncTask<String, Void, Bitmap> {

        Context ctx;
        String message, title;
        PendingIntent resultPendingIntent;

        public sendNotification(Context context, PendingIntent resultPendingIntent) {
            super();
            this.ctx = context;
            this.resultPendingIntent = resultPendingIntent;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            InputStream in;
            title = params[1];
            message = params[2];
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                return BitmapFactory.decodeStream(in);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (result != null) {
                NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
                bigPictureStyle.setBigContentTitle(title);
                bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
                bigPictureStyle.bigPicture(result);
                mBuilder.setStyle(bigPictureStyle);
            }
            mBuilder.setSmallIcon(R.drawable.ic_launcher_background);
            mBuilder.setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
            if (resultPendingIntent != null) {
                mBuilder.setContentIntent(resultPendingIntent);
            }

            mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                assert mNotificationManager != null;
                mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
            assert mNotificationManager != null;
            mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
        }

    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public void playNotificationSound() {
        try {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
