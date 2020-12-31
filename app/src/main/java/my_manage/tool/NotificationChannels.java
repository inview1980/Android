package my_manage.tool;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import java.util.Arrays;

import my_manage.password_box.R;

public final class NotificationChannels {
    public final static String CRITICAL = "critical";
    public final static String IMPORTANCE = "importance";
    public final static String DEFAULT = "default";
    public final static String LOW = "low";
    public final static String MEDIA = "media";

    /**
     * A notification with no importance: does not show in the shade.
     */
    public static final int IMPORTANCE_NONE = 0;

    /**
     * Min notification importance: only shows in the shade, below the fold.
     */
    public static final int IMPORTANCE_MIN = 1;

    /**
     * Low notification importance: shows everywhere, but is not intrusive.
     */
    public static final int IMPORTANCE_LOW = 2;

    /**
     * Default notification importance: shows everywhere, makes noise, but does not visually
     * intrude.
     */
    public static final int IMPORTANCE_DEFAULT = 3;

    /**
     * Higher notification importance: shows everywhere, makes noise and peeks. May use full screen
     * intents.
     */
    public static final int IMPORTANCE_HIGH = 4;

    /**
     * Unused.
     */
    public static final int IMPORTANCE_MAX = 5;

    public static NotificationManager createAllNotificationChannels(Context context) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(nm == null) {
            return null;
        }

        NotificationChannel mediaChannel = new NotificationChannel(
                MEDIA,
                context.getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT);
        mediaChannel.setSound(null,null);
        mediaChannel.setVibrationPattern(null);

        nm.createNotificationChannels(Arrays.asList(
                new NotificationChannel(
                        CRITICAL,
                        "严重",
                        NotificationManager.IMPORTANCE_HIGH),
                new NotificationChannel(
                        IMPORTANCE,
                        "重要",
                        NotificationManager.IMPORTANCE_DEFAULT),
                new NotificationChannel(
                        DEFAULT,
                        "默认",
                        NotificationManager.IMPORTANCE_LOW),
                new NotificationChannel(
                        LOW,
                        "低",
                        NotificationManager.IMPORTANCE_MIN),
                //custom notification channel
                mediaChannel
        ));
        return nm;
    }
}
