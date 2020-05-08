package my_manage.password_box;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import my_manage.rent_manage.pojo.RoomDetails;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

//        assertEquals("com.example.passwordsavingcabinet", appContext.getPackageName());
        writeExecel("f:/tttt.xlsx");
    }

    private void writeExecel(String filename) {
        List<RoomDetails> rLst = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
//            RoomDetails rr = new RoomDetails();
//            rr.setCommunityName("小区名" + i);
//            rr.setRoomNumber(i + "-" + i);
//            rLst.add(rr);

        }
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
