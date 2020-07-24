package my_manage.tool.http;

import android.app.Activity;

import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.io.File;
import java.util.Optional;
import java.util.function.BiConsumer;

import my_manage.tool.PageUtils;
import okhttp3.Headers;
import web.WebResult;

/**
 * @author inview
 * @Date 2020/7/23 11:11
 * @Description :
 */
public class MyFileCallback extends FileCallback {
    private static final String                   destFileName = "download.dat";
    private              BiConsumer<File, String> doSomething;
    private              Activity                 activity;

    public MyFileCallback(Activity activity, BiConsumer<File, String> doSomething) {
        super(destFileName);
        this.doSomething = doSomething;
        this.activity = activity;
    }

    @Override
    public void onSuccess(Response<File> response) {
        try {
            Headers headers     = response.headers();
            String  stateString = headers.get("state");
            String  msg         = headers.get("msg");
            PageUtils.Log("state:" + stateString + ",msg:" + msg);

            int resultCode = Integer.parseInt(Optional.ofNullable(stateString).orElse("0"));
            if (resultCode == WebResult.OK) {
                if (response.body().exists())
                    doSomething.accept(response.body(), msg);
            }else {
                PageUtils.showMessage(activity,msg);
            }
        } catch (NumberFormatException e) {
            PageUtils.Error(activity.getLocalClassName() + " " + e.getLocalizedMessage());
        }
    }

    @Override
    public void onError(Response<File> response) {
//        super.onError(response);
        PageUtils.showMessage(activity, "出错了");
    }
}
