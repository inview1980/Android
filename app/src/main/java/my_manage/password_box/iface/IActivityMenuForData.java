package my_manage.password_box.iface;

import android.app.Activity;

import java.util.List;

public interface IActivityMenuForData<T extends Activity> {
    void run(T activity, List data, int position);
    int getIndex();
    String getName();
}
