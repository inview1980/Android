//package com.example.passwordsavingcabinet.tool;
//
//import android.content.Context;
//
//import androidx.test.ext.junit.runners.AndroidJUnit4;
//import androidx.test.platform.app.InstrumentationRegistry;
//
//import com.example.passwordsavingcabinet.secret.User;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//
//@RunWith(AndroidJUnit4.class)
//public class FileUtilsTest {
//
//    @Test
//    public void readUserInfoFormFile() {
//        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        User user=FileUtils.readUserInfoFormFile(appContext.getExternalCacheDir().getPath());
//        assertNotNull(user);
//        assertNotNull(user.getPassword());
//        assertNotNull(user.getSalt());
////        assertEquals(12,user.getSalt().length());
////        assertEquals(18,user.getPassword().length());
//        System.out.println(user);
//    }
//
//    @Test
//    public void writeUserInfoToFile() {
//        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        User user=new User();
//        user.setPassword("987654321");
//        user.setSalt("123456");
//        boolean b = FileUtils.writeUserInfoToFile(user,appContext.getExternalCacheDir().getPath());
//        assertTrue(b);
//    }
//}