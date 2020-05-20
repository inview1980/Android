package my_manage.rent_manage;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.List;

import my_manage.password_box.R;
import my_manage.receiver.AlarmReceiver;
import my_manage.tool.database.DbHelper;
import my_manage.tool.database.DbBase;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.NotificationChannels;
import my_manage.tool.PageUtils;

public class MyService extends IntentService {
    public MyService() {
        super("MyService");
    }

    /**
     * 定时任务 https://blog.csdn.net/qq_17798399/article/details/94345175
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String path = intent.getStringExtra("path");

        new Thread(() -> checkAndSendNotification(path)).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent i = new Intent(this, AlarmReceiver.class);
        i.putExtra("path", path);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        // 间隔时间的毫秒数
        final int anHour =this.getResources().getInteger(R.integer.Message_Interval_byHour)  * 60 * 60 *1000;//
        Log.i(PageUtils.Tag, "毫秒数：" + anHour);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + anHour, pi);
    }


    public void sendSimpleNotification(Context context, String title, String msg) {
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
                .setContentTitle(title)
                .setTicker("提示消息来啦").setWhen(System.currentTimeMillis())
//                .setSubText(msg+"-2")
                //设置通知内容
                .setContentText(msg)
                //设置点击通知后自动删除通知
                .setAutoCancel(false)
                //设置显示通知时间
                .setShowWhen(true)
                //设置通知右侧的大图标
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.home))
                //设置点击通知时的响应事件
                .setContentIntent(pi);
//                //设置删除通知时的响应事件
//                .setDeleteIntent(deletePendingIntent);
        //发送通知
        NotificationManager nm = NotificationChannels.createAllNotificationChannels(this);
        nm.notify(R.string.app_name, nb.build());
    }

    private void checkAndSendNotification(String path) {
        StringBuilder sendMsg = new StringBuilder();
        DbBase.getLiteOrm(MyService.this, path);
        //获取已超时但未交租的房子的数量
        List<ShowRoomDetails> lst   = DbHelper.getInstance().getRoomForHouse(null);
        long                  count = lst.stream().map(ShowRoomDetails::getRentalEndDate).filter(sr -> sr != null && sr.before(Calendar.getInstance())).count();
        if (count > 0) {
            sendMsg.append("未交房租数量：").append(count);
        }
        //获取未超时但快到期的房子的数量
        Calendar now3 = Calendar.getInstance();
        now3.add(Calendar.DATE, getResources().getInteger(R.integer.advanceDay));
        long count2 = lst.stream().map(ShowRoomDetails::getRentalEndDate).filter(sr ->
                sr != null && sr.after(Calendar.getInstance()) && sr.before(now3)).count();
        if (count2 > 0) {
            if (sendMsg.length() > 1)
                sendMsg.append("，");
            sendMsg.append("房租即将到期数量：").append(count2);
        }
        //如果需发送的消息不为空，则发送消息
        if (sendMsg.length() > 1) {
            sendSimpleNotification(this, "出租房收租啦", sendMsg.toString());
        }

    }
}
