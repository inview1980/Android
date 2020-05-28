//package my_manage.tool.database;
//
//import android.content.Context;
//import android.net.Uri;
//import android.util.Log;
//
//import androidx.core.content.FileProvider;
//import androidx.test.InstrumentationRegistry;
//
//import org.junit.Test;
//
//import java.io.File;
//import java.util.List;
//
//import lombok.val;
//import my_manage.password_box.BuildConfig;
//import my_manage.password_box.R;
//import my_manage.password_box.pojo.UserItem;
//import my_manage.rent_manage.pojo.show.ShowRoomDetails;
//import my_manage.tool.PageUtils;
//import my_manage.tool.StrUtils;
//
//import static org.junit.Assert.*;
//
//public class DbHelperTest {
//
//    //    @Test
//    public void getRoomForHouse() {
////        DbBase.createCascadeDB(InstrumentationRegistry.getTargetContext(), "/storage/emulated/0/Android/data/my_manage.password_box/files/rental.db");
////        List<RoomDetails> roomDetailsList = DbBase.getQueryAll(RoomDetails.class);
////        List<ShowRoomForHouse> sh = DbHelper.getInstance().getRoomForHouse(roomDetailsList.get(4).getCommunityName());
////        assertNotNull(sh);
////        System.out.println(sh.get(0));
//////        System.out.println(sh.get(0).getRentalEndDate());
////        System.out.println(sh.get(new Random().nextInt(sh.size())));
//    }
//
//    @Test
//    public void getShowRoomDesForPerson() {
//        DbBase.createCascadeDB(InstrumentationRegistry.getTargetContext(), "/storage/emulated/0/Android/data/my_manage.password_box/files/rental.db");
//        List<ShowRoomDetails> lst = DbHelper.getInstance().getShowRoomDesForPerson(3);
//
//        assertTrue(lst.size() > 0);
//    }
//
//    @Test
//    public void rebuilding() {
//        DbBase.createCascadeDB(InstrumentationRegistry.getTargetContext(), "/storage/emulated/0/Android/data/my_manage.password_box/files/rental.db");
//        DbHelper.getInstance().rebuilding(InstrumentationRegistry.getTargetContext());
//    }
//
//    @Test
//    public void toExcel() {
//        DbHelper.getInstance().dbInit(InstrumentationRegistry.getTargetContext(), "/storage/emulated/0/Android/data/my_manage.password_box/files/rental.db");
//        Context context = InstrumentationRegistry.getTargetContext();
//        String path = context.getApplicationContext().getExternalFilesDir(null).getAbsolutePath()
//                + "/" + context.getResources().getString(R.string.rentalFileNameBackup);
//        DbHelper.getInstance().toExcel(path);
//    }
//
//    @Test
//    public void readFile() {
//        Context appContext = InstrumentationRegistry.getTargetContext();
//        String  filePath   = appContext.getExternalFilesDir(null) + "/files/" + System.currentTimeMillis() + ".jpg";
//        String  f2         = appContext.getExternalFilesDir(null).getAbsolutePath() + "/" + appContext.getString(R.string.rentalFileName);
//        Log.i(PageUtils.Tag, filePath);
//        Log.i(PageUtils.Tag, f2);
//
//        File outputFile = new File(f2);
//        if (!outputFile.getParentFile().exists()) {
//            outputFile.getParentFile().mkdir();
//        }
//        Uri contentUri = FileProvider.getUriForFile(appContext,
//                BuildConfig.APPLICATION_ID + ".fileprovider", outputFile);
//        Log.i(PageUtils.Tag, contentUri.toString());
//        assertNotNull(contentUri);
//    }
//
//    @Test
//    public void saveUserItem() {
//        Context appContext = InstrumentationRegistry.getTargetContext();
//        initDB(appContext);
//        UserItem item = new UserItem(0, "123", "234", "inview", "123456", null, StrUtils.getRandomString(18));
//       assertTrue( DbHelper.getInstance().saveUserItem(item));
//        val lst= DbBase.getQueryAll(UserItem.class);
//        lst.forEach(s->Log.i(PageUtils.Tag,s.toString()));
//        val lst2=DbHelper.getInstance().getItemsByAfter(appContext);
//        lst2.forEach(s->Log.i(PageUtils.Tag,s.toString()));
//    }
//
//    @Test
//    public void loadIn() {
//        Context appContext = InstrumentationRegistry.getTargetContext();
//        initDB(appContext);
//        assertTrue(DbHelper.getInstance().loadIn(appContext,"123456"));
//    }
//
//    @Test
//    public void getItemsByAfter() {
//        Context appContext = InstrumentationRegistry.getTargetContext();
//        initDB(appContext);
//        DbHelper.getInstance().resetPassword(appContext,null);
//
//        List<UserItem> tmp = DbHelper.getInstance().getItemsByAfter(appContext);
//        assertNotNull(tmp);
//    }
//
//    private void initDB(Context appContext) {
//        //初始化数据库
//        String DBFilePath = appContext.getExternalFilesDir(null).getAbsolutePath() + "/" + appContext.getString(R.string.rentalFileName);
//        DbHelper.getInstance().dbInit(appContext, DBFilePath);
//    }
//}