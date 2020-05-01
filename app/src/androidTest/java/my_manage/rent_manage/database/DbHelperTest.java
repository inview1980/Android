package my_manage.rent_manage.database;

import androidx.test.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import my_manage.rent_manage.pojo.show.ShowRoomDetails;

public class DbHelperTest {

    //    @Test
    public void getRoomForHouse() {
//        RentDB.createCascadeDB(InstrumentationRegistry.getTargetContext(), "/storage/emulated/0/Android/data/my_manage.password_box/files/rental.db");
//        List<RoomDetails> roomDetailsList = RentDB.getQueryAll(RoomDetails.class);
//        List<ShowRoomForHouse> sh = DbHelper.getInstance().getRoomForHouse(roomDetailsList.get(4).getCommunityName());
//        assertNotNull(sh);
//        System.out.println(sh.get(0));
////        System.out.println(sh.get(0).getRentalEndDate());
//        System.out.println(sh.get(new Random().nextInt(sh.size())));
    }

    @Test
    public void getShowRoomDesForPerson() {
        RentDB.createCascadeDB(InstrumentationRegistry.getTargetContext(), "/storage/emulated/0/Android/data/my_manage.password_box/files/rental.db");
        List<ShowRoomDetails> lst=DbHelper.getInstance().getShowRoomDesForPerson();

        Assert.assertTrue(lst.size()>0);
    }

//    @Test
//    public void readFile() {
//        Context appContext = InstrumentationRegistry.getTargetContext();
////        assertTrue(file.exists());
//    }
}