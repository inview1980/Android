package my_manage.tool.database;

import com.alibaba.fastjson.JSONObject;

import org.json.JSONArray;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import my_manage.password_box.pojo.UserItem;
import my_manage.rent_manage.pojo.show.ExcelData;
import my_manage.tool.ExcelUtils;
import my_manage.tool.StrUtils;

import static org.junit.Assert.assertNotNull;

public class DbHelperTest {

    @Test
    public void readExcel() {
//        Random             random    = new Random();
        ExcelData excelData = ExcelUtils.getInstance().readExcel("F:\\db.xls");
        assertNotNull(excelData.getRentalRecordList());
//        assertNotNull(excelData.getRoomDetailsList());
////        assertNotNull(excelData.getPersonDetailsList());
//
////        System.out.println(excelData.getRoomDetailsList().get(random.nextInt(excelData.getRoomDetailsList().size())));
////        System.out.println(excelData.getRoomDetailsList().get(random.nextInt(excelData.getRoomDetailsList().size())));
////        System.out.println(excelData.getRentalRecordList().get(11));
////        System.out.println(excelData.getRentalRecordList().get(15));
////        System.out.println(excelData.getRentalRecordList().get(18));
//
//        System.out.println(excelData.getPersonDetailsList().get(random.nextInt(excelData.getPersonDetailsList().size())));
//        System.out.println(excelData.getPersonDetailsList().get(random.nextInt(excelData.getPersonDetailsList().size())));
    }

    @Test
    public void dbInit() {
        //初始化数据库
//        DbHelper.ExcelData excelData = new DbHelper.ExcelData();
//        excelData.setRoomDetailsList(BuildData.getRoomDes(200));
//        excelData.setRentalRecordList(BuildData.getRentalRecord(excelData.getRoomDetailsList(), 500));
//        excelData.setPersonDetailsList(BuildData.getContact());
//        excelData.setUserItemList(BuildData.getUserItem());
//        ExcelUtils.getInstance().toExcel("f:/db.xls", excelData);

    }

    @Test
    public void date() {
//        val lst=ExcelUtils.getAnnotation(PersonDetails.class);
//        System.out.println(lst);
    }

    @Test
    public void readJson(){
        List<UserItem> lst = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            UserItem item = new UserItem();
            item.setUserName("userName:"+i);
            item.setItemName("itemName:"+i);
            item.setPassword(StrUtils.getRandomString(8));
            lst.add(item);

        }
//        System.out.println(json.toString());
    }
}