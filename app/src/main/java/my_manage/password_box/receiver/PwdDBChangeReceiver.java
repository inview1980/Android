package my_manage.password_box.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * 接收应用内的广播，当passwordDB内的数据变更时
 */
public final class PwdDBChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(this.getClass().getSimpleName(),"已收到广播");
        Toast.makeText(context, "检测到意图。", Toast.LENGTH_LONG).show();
        return;
//        if (intent != null) {
//            UserItem userItem = JSON.parseObject(intent.getStringExtra("userItem"), UserItem.class);
//            int currentItem = intent.getIntExtra("currentItem", -1);
//            if (currentItem == -1) {
//                PasswordDB.init().getItems().add(userItem);
//                PasswordManageActivity.content.setDBChanged(true);
//            } else if (PasswordDB.init().getItems().size() >= currentItem) {
//                PasswordDB.init().getItems().set(currentItem, userItem);
//                PasswordManageActivity.content.setDBChanged(true);
//            }
//            //重新载入列表
////            PasswordManageActivity.content.showLst();
//        }
    }
}
