package my_manage.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import my_manage.rent_manage.MyService;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, MyService.class);
        i.putExtra("path", intent.getStringExtra("path"));
        context.startService(i);
    }
}
