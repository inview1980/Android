package my_manage.rent_manage.database;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.FileProvider;
import androidx.test.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.util.List;

import my_manage.password_box.BuildConfig;
import my_manage.password_box.R;
import my_manage.rent_manage.listener.RentalMainActivityListener;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.PageUtils;

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
        List<ShowRoomDetails> lst = DbHelper.getInstance().getShowRoomDesForPerson(3);

        Assert.assertTrue(lst.size() > 0);
    }

    @Test
    public void rebuilding() {
        RentDB.createCascadeDB(InstrumentationRegistry.getTargetContext(), "/storage/emulated/0/Android/data/my_manage.password_box/files/rental.db");
        DbHelper.getInstance().rebuilding(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void toExcel() {
        DbHelper.getInstance().dbInit(InstrumentationRegistry.getTargetContext(), "/storage/emulated/0/Android/data/my_manage.password_box/files/rental.db");
        Context context = InstrumentationRegistry.getTargetContext();
        String path = context.getApplicationContext().getExternalFilesDir(null).getAbsolutePath()
                + "/" + context.getResources().getString(R.string.rentalFileNameBackup);
        DbHelper.getInstance().toExcel(path);
    }

    @Test
    public void readFile() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        String  filePath = appContext.getExternalFilesDir(null) + "/files/"+System.currentTimeMillis()+".jpg";
        String  f2 = appContext.getExternalFilesDir(null).getAbsolutePath() + "/" + appContext.getString(R.string.rentalFileName);
        Log.i(PageUtils.Tag,filePath);
        Log.i(PageUtils.Tag,f2);

        File    outputFile = new File(f2);
        if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdir();
        }
        Uri contentUri = FileProvider.getUriForFile(appContext,
                BuildConfig.APPLICATION_ID + ".fileprovider", outputFile);
        Log.i(PageUtils.Tag,contentUri.toString());
        Assert.assertNotNull(contentUri);
    }
}