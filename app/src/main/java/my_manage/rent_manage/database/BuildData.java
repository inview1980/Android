package my_manage.rent_manage.database;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import my_manage.rent_manage.pojo.PersonDetails;
import my_manage.rent_manage.pojo.RentalRecord;
import my_manage.rent_manage.pojo.RoomDetails;

public final class BuildData {
    private static int CompoundNumber = 10;//小区数量
    private static int ContactsNumber = 50;//租户数量

    public static List<RoomDetails> getRoomDes(int i) {
        Random random = new Random();
        List<RoomDetails> rList = new ArrayList<>();
        RoomDetails rd;
        for (int j = 0; j < i; j++) {
            rd = new RoomDetails();
            rd.setCommunityName("小区名称：" + random.nextInt(CompoundNumber));
            rd.setRoomArea(random.nextDouble() * 100);
            rd.setRoomNumber((random.nextInt(10) + 1) + "-" + random.nextInt(50) + "-" + random.nextInt(50));
            rd.setMeterNumber(getNumberForString(8));
            rd.setManId(random.nextInt(ContactsNumber));
            rList.add(rd);
        }
        return rList;
    }

    private static String getNumberForString(int i) {
        if (i < 2 || i > 8) i = 8;
        String tmp = String.valueOf(new Random().nextDouble()).substring(2);
        return tmp.substring(0, i);
    }

    public static List<RentalRecord> getRentalRecord(List<RoomDetails> roomDetailsList, int count) {
        if (roomDetailsList == null) return null;
        Random random = new Random();
        RentalRecord rentalRecord;
        List<RentalRecord> rList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            rentalRecord = new RentalRecord();
            Calendar now = Calendar.getInstance();
            rentalRecord.setPayMonth(random.nextInt(2) + 2);
            now.add(Calendar.MONTH, random.nextInt(10) - 5);
            rentalRecord.setStartDate(now);
            Calendar pay = Calendar.getInstance();
            pay.add(Calendar.DAY_OF_YEAR, random.nextInt(100) - 50);
            rentalRecord.setPaymentDate(pay);
            rentalRecord.setIsContainRealty(random.nextBoolean());
            rentalRecord.setRealtyMoney(random.nextDouble() * 1000);
            rentalRecord.setTotalMoney(random.nextDouble() * 10000);
            rentalRecord.setManID(random.nextInt(ContactsNumber));
            rentalRecord.setRoomNumber(roomDetailsList.get(random.nextInt(roomDetailsList.size())).getRoomNumber());
            rList.add(rentalRecord);
        }
        return rList;
    }

    public static List<PersonDetails> getContact() {
        PersonDetails personDetails;
        List<PersonDetails> resultLst = new ArrayList<>();
        for (int i = 0; i < ContactsNumber; i++) {
            personDetails = new PersonDetails();
            personDetails.setTel("139" + getNumberForString(8));
            personDetails.setName("name:" + i);
            personDetails.setCord("4201" + getNumberForString(8) + getNumberForString(6));
            resultLst.add(personDetails);
        }
        return resultLst;
    }
}
