package my_manage.rent_manage.database;

import org.junit.Test;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

import my_manage.rent_manage.pojo.PersonDetails;
import my_manage.rent_manage.pojo.RentalRecord;
import my_manage.rent_manage.pojo.RoomDetails;

import static org.junit.Assert.assertNotNull;

public class DbHelperTest {

    @Test
    public void readExcel() {
        Random random=new Random();
        DbHelper.ExcelData excelData = DbHelper.getInstance().readExcel("F:\\program\\android\\Password saving cabinet\\app\\src\\main\\res\\raw\\db.xlsx");
//        assertNotNull(excelData.getRentalRecordList());
        assertNotNull(excelData.getRoomDetailsList());
//        assertNotNull(excelData.getPersonDetailsList());

//        System.out.println(excelData.getRoomDetailsList().get(random.nextInt(excelData.getRoomDetailsList().size())));
//        System.out.println(excelData.getRoomDetailsList().get(random.nextInt(excelData.getRoomDetailsList().size())));
//        System.out.println(excelData.getRentalRecordList().get(11));
//        System.out.println(excelData.getRentalRecordList().get(15));
//        System.out.println(excelData.getRentalRecordList().get(18));

        System.out.println(excelData.getPersonDetailsList().get(random.nextInt(excelData.getPersonDetailsList().size())));
        System.out.println(excelData.getPersonDetailsList().get(random.nextInt(excelData.getPersonDetailsList().size())));
    }

    @Test
    public void dbInit() {
        //初始化数据库

        List<RoomDetails> rd =BuildData. getRoomDes(200);
        List<RentalRecord> rr =BuildData. getRentalRecord(rd,500);
        List<PersonDetails> cc=BuildData.getContact();
        DbHelper.getInstance().toExcel("f:/db.xlsx",rd,rr,cc);

    }
@Test
    public void date(){
        String str;
    Calendar calendar=Calendar.getInstance();
    str=calendar.get(Calendar.YEAR)+"-"+calendar.get(Calendar.MONTH)+"-"+calendar.get(Calendar.DAY_OF_MONTH);
    System.out.println(str);
    calendar.add(Calendar.MONTH, 3);
    str=calendar.get(Calendar.YEAR)+"-"+calendar.get(Calendar.MONTH)+"-"+calendar.get(Calendar.DAY_OF_MONTH);
    System.out.println(str);

}
}