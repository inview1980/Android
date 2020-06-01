package com.inview;


import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

}