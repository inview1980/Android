package my_manage.rent_manage.pojo;

import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class RentalRecordTest {
    @Test
    public void testRoom() {
        RentalRecord rentalRecord =new RentalRecord();
        LocalDate now=LocalDate.now();
//        rentalRecord.setStartDate(now.minusMonths(3));
//        rentalRecord.setPaymentDate(now.minusDays(45));
//        rentalRecord.setIsContainRealty(true);
//        rentalRecord.setPropertyCosts(444.56);
//        rentalRecord.setTotalMoney(12345.67);
//        rentalRecord.setRoomNumber("7-1-801");
//        System.out.println(rentalRecord);
//        assertNotNull(rentalRecord);
    }

    @Test
    public void tt(){
        List<RoomDetails> t1 = getRoomDes(100);
        String[] names = t1.stream().map(rd->rd.getCommunityName()).distinct().toArray(String[]::new);
        for (String name : names) {
            System.out.println(name);
        }
        assertTrue(names!=null);
    }
    private List<RoomDetails> getRoomDes(int i) {
        Random random = new Random();
        List<RoomDetails> rList=new ArrayList<>();
        RoomDetails rd;
        for (int j = 0; j < i; j++) {
            rd = new RoomDetails();
            rd.setCommunityName("小区名称：" + random.nextInt(10));
            rd.setRoomArea(random.nextDouble() * 100);
            rd.setRoomNumber(random.nextInt(10) + 1 + "-" + random.nextInt(50));
            rList.add(rd);
        }
        return rList;
    }
}