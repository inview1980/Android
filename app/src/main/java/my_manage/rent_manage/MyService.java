package my_manage.rent_manage;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.List;

import my_manage.password_box.R;
import my_manage.receiver.AlarmReceiver;
import my_manage.rent_manage.database.DbHelper;
import my_manage.rent_manage.database.RentDB;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.NotificationChannels;

public class MyService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    /**
     * 定时任务 https://blog.csdn.net/qq_17798399/article/details/94345175
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String path = intent.getStringExtra("path");

        new Thread(() -> {
            checkAndSendNotification(path);
        }).start();
        AlarmManager manager       = (AlarmManager) getSystemService(ALARM_SERVICE);
        int          anHour        = 8 * 60 * 60 * 1000; // 这是8小时的毫秒数
        long         triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent       i             = new Intent(this, AlarmReceiver.class);
        i.putExtra("path", path);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    public void sendSimpleNotification(Context context, String msg) {
        //创建点击通知时发送的广播
        Intent intent = new Intent(context, RentalMainActivity.class);
        intent.setAction("RentalMainActivity");
        PendingIntent pi = PendingIntent.getActivity(context, R.string.app_name, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        //创建删除通知时发送的广播
//        Intent deleteIntent = new Intent(context,RentalMainActivity.class);
//        deleteIntent.setAction("RentalMainActivity");
//        PendingIntent deletePendingIntent = PendingIntent.getService(context,0,deleteIntent,0);
        //创建通知
        Notification.Builder nb = new Notification.Builder(context, NotificationChannels.LOW)
                //设置通知左侧的小图标
                .setSmallIcon(R.drawable.ic_home_black_24dp)
                //设置通知标题
                .setContentTitle("房租到期")
                .setTicker("提示消息来啦").setWhen(System.currentTimeMillis())
                //设置通知内容
                .setContentText(msg)
                //设置点击通知后自动删除通知
                .setAutoCancel(true)
                //设置显示通知时间
                .setShowWhen(true)
                //设置通知右侧的大图标
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                //设置点击通知时的响应事件
                .setContentIntent(pi);
//                //设置删除通知时的响应事件
//                .setDeleteIntent(deletePendingIntent);
        //发送通知
        NotificationManager nm = NotificationChannels.createAllNotificationChannels(this);
        nm.notify(R.string.app_name, nb.build());
    }

    private void checkAndSendNotification(String path) {
        RentDB.getLiteOrm(MyService.this, path);
        //获取已超时但未交租的房子的数量
        List<ShowRoomDetails> lst   = DbHelper.getInstance().getRoomForHouse(null);
        long                  count = lst.stream().map(sr -> sr.getRentalEndDate()).filter(sr -> sr != null && sr.before(Calendar.getInstance())).count();
        sendSimpleNotification(this, "未按时交房租数量：" + count);
    }
}
