package my_manage.password_box.iface;

import android.app.Activity;

public interface IActivityMenu<T extends Activity> {
    void run(T activity, int position);
    int getIndex();
    String getName();

}

