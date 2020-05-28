package com.inview;


import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtilsTest {
    String path = "f:\\test.xls";

    @Test
    public void toExcel() {
        List<MyItem> lst = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            MyItem item = new MyItem("name:" + i, "details:" + i, i);
            lst.add(item);
        }
        ExcelUtils.getInstance().toExcel(path, new HashMap<String, List<MyItem>>() {
            {put(MyItem.class.getSimpleName(), lst);}
        });
    }

    @Test
    public void readExcel(){
        List<MyItem3> lst=ExcelUtils.getInstance().readExcel(path,MyItem3.class);
        Assert.assertNotNull(lst);
        System.out.println(lst);
    }
    @Test
    public void readJson(){
        List<MyItem> lst = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            MyItem item = new MyItem("name:" + i, "details:" + i, i);
            lst.add(item);
        }
        JSONArray json=new JSONArray(lst);
        System.out.println(json.toString());
    }
}