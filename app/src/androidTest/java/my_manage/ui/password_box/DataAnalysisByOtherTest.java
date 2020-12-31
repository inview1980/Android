//package my_manage.ui.password_box;
//
//import androidx.test.InstrumentationRegistry;
//import androidx.test.ext.junit.runners.AndroidJUnit4;
//
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import lombok.val;
//import my_manage.pojo.FuelRecord;
//import my_manage.pojo.show.ShowRoomDetails;
//import my_manage.tool.database.DbBase;
//import my_manage.tool.database.DbHelper;
//import my_manage.ui.fuel.listener.DataAnalysisByOther;
//
///**
// * @author inview
// * @Date 2020/12/25 9:01
// * @Description :
// */
//@RunWith(AndroidJUnit4.class)
//public class DataAnalysisByOtherTest {
//    @Test
//    public void getKMData() {
//        DbBase.createCascadeDB(InstrumentationRegistry.getTargetContext(), "/storage/emulated/0/Android/data/my_manage.password_box/files/rental.db");
//        List<FuelRecord> lst     = DbHelper.getInstance().getFuelRecordList();
//        List<Integer> yearLst = lst.stream().map(fr -> fr.getTime().get(Calendar.YEAR)).distinct().sorted().collect(Collectors.toList());
//        ArrayList<ArrayList<String>>     data    = DataAnalysisByOther.getKMData(lst, yearLst);
//        Assert.assertNotNull(data);
//        Assert.assertTrue(data.size() > 0);
////        assertTrue(lst.size() > 0);
//    }
//}
