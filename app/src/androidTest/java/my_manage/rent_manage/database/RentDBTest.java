package my_manage.rent_manage.database;



import androidx.test.InstrumentationRegistry;

import org.junit.Test;

import java.io.IOException;


public class RentDBTest {
@Test
public void showDB(){
    RentDB.createCascadeDB( InstrumentationRegistry.getTargetContext(),"/storage/emulated/0/Android/data/my_manage.password_box/files/rental.db");
//    List<RentalRecord> rList = RentDB.getQueryByWhere(RentalRecord.class, "roomDesID", new Object[]{new Random().nextInt(10)});
//    List<RentalRecord> r2=RentDB.getRentalRecordAll(rList);
//    List<ShowRoomForMain> rList=DbHelper.getInstance().getShowRoomDesList();
    boolean t1 = DbHelper.getInstance().toExcel("/storage/emulated/0/Android/data/my_manage.password_box/files/db.xlsx");
    System.out.println(t1);//F:\program\android\Password saving cabinet\app\db.xlsx

}
    @Test
    public void insert() throws IOException {
//        File file=new File("/1.txt");
//        file.createNewFile();
//        assertTrue(file.exists());
        int roomCount=10;
//        RentDB.createCascadeDB( InstrumentationRegistry.getTargetContext(),null);

//        RentDB.insertAll(getRoomDes(roomCount));
//        RentDB.insertAll(getRentalRecord(100, roomCount));
//
//        List<RentalRecord> rList = RentDB.getQueryByWhere(RentalRecord.class, "roomDesID", new Object[]{new Random().nextInt(10)});
//        assertTrue(rList.size() > 0);
//        System.out.println(rList);

//        List<RentalRecord> rList2 = RentDB.getQueryAll(RentalRecord.class);
//        System.out.println(rList2);
    }

}