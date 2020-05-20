//package my_manage.password_box.database;
//
//import my_manage.password_box.pojo.UserItem;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//
//public class PasswordDBTest {
//    String path="F:\\program";
//    String filename = "database";
//    String password = "123456";
//
//    @Before
//    public void setUp() {
//        PasswordDB.init(path,filename,password );
//    }
//
//    @Test
//    public void save_getItems() {
//        List<UserItem> ite=PasswordDB.init().getItems();
//        System.out.println(ite);
//
//        boolean isOk=ite.addAll(getItemLst());
//        assertTrue(isOk);
//        isOk=PasswordDB.init().save();
//        assertTrue(isOk);
//
//        List<UserItem> item2=PasswordDB.init().getItems();
//        assertNotNull(item2);
//        System.out.println(item2);
//    }
//
//    @Test
//    public void clearAndSave()  {
////数据文件不存在时
//        PasswordDB.init().clearAndSave(null);
//        List<UserItem> items=PasswordDB.init().getItems();
//        assertEquals(0,items.size());
//
//    }
//
//    private Set<UserItem> getItemLst() {
//        Set<UserItem> itemList = new HashSet<>();
//        UserItem ui1 = new UserItem();
//        ui1.setItemName("123");
//        itemList.add(ui1);
//
//        UserItem u2 = new UserItem();
//        u2.setUserName("username");
//        u2.setPassword("jfddk");
//        u2.setItemName("jfdk");
//        itemList.add(u2);
//
//        UserItem u3 = new UserItem();
//        u3.setUserName("username");
//        itemList.add(u3);
//        return itemList;
//    }
//}