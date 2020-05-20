package my_manage.tool.database;



import androidx.test.InstrumentationRegistry;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import my_manage.rent_manage.pojo.RoomDetails;


public class DbBaseTest {
@Test
public void showDB(){
    DbBase.createCascadeDB( InstrumentationRegistry.getTargetContext(),"/storage/emulated/0/Android/data/my_manage.password_box/files/rental.db");

    List<RoomDetails> rd = DbHelper.getInstance().getRoomDetailsByDelete();
//    List<RentalRecord> rList = DbBase.getQueryByWhere(RentalRecord.class, "roomDesID", new Object[]{new Random().nextInt(10)});
//    List<RentalRecord> r2=DbBase.getRentalRecordAll(rList);
//    List<ShowRoomForMain> rList=DbHelper.getInstance().getShowRoomDesList();
//    boolean t1 = DbHelper.getInstance().toExcel("/storage/emulated/0/Android/data/my_manage.password_box/files/db.xlsx");
    System.out.println(rd);//F:\program\android\Password saving cabinet\app\db.xlsx

}
    @Test
    public void insert() throws IOException {
//        File file=new File("/1.txt");
//        file.createNewFile();
//        assertTrue(file.exists());
        int roomCount=10;
//        DbBase.createCascadeDB( InstrumentationRegistry.getTargetContext(),null);

//        DbBase.insertAll(getRoomDes(roomCount));
//        DbBase.insertAll(getRentalRecord(100, roomCount));
//
//        List<RentalRecord> rList = DbBase.getQueryByWhere(RentalRecord.class, "roomDesID", new Object[]{new Random().nextInt(10)});
//        assertTrue(rList.size() > 0);
//        System.out.println(rList);

//        List<RentalRecord> rList2 = DbBase.getQueryAll(RentalRecord.class);
//        System.out.println(rList2);
    }

}