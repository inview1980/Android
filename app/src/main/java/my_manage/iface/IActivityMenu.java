package my_manage.iface;

import android.app.Activity;

import java.util.List;

import my_manage.pojo.show.ShowRoomDetails;

public interface IActivityMenu {
    <T extends Activity & IShowList> void run(T activity, List<ShowRoomDetails> data, int position);
}

