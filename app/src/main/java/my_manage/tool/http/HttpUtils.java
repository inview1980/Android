package my_manage.tool.http;

import android.annotation.SuppressLint;
import android.app.Activity;

import androidx.core.util.Consumer;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.orhanobut.dialogplus.DialogPlus;

import java.io.File;
import java.util.Optional;
import java.util.function.BiConsumer;

import my_manage.MyApplication;
import my_manage.tool.PageUtils;
import okhttp3.Headers;
import web.WebResult;

/**
 * @author inview
 * @Date 2020/6/29 10:24
 * @Description :
 */
public class HttpUtils {
    public static <T extends WebResult> void get(Activity activity, String urlString, HttpParams params, Consumer<WebResult> doSomething, Object t) {
        get(activity, urlString, params, doSomething, t, null);
    }

    /**
     * get文本数据
     * <p>
     * //     * @param obj 当返回对象为 WebResult&lt;List&lt;T&gt;&gt;时，obj==T，当obj==null时，返回无泛形的WebResult对象
     */
    @SuppressLint("CheckResult")
    public static void get(Activity activity, String urlString, HttpParams params, Consumer<WebResult> doSomething,
                           Object obj, DialogPlus dialog) {
        PageUtils.Log(activity.getLocalClassName() + " Get请求:" + urlString);
        OkGo.<String>get(MyApplication.ConnectionUrl + urlString).tag(activity).params(params)
                .execute(new MyStringCallback(activity, doSomething, obj, dialog));
    }

    @SuppressLint("CheckResult")
    public static void get(Activity activity, String urlString, HttpParams params, BiConsumer<File, String> doSomething) {
        PageUtils.Log(Optional.ofNullable(activity).map(Activity::getLocalClassName).orElse("") + " Get请求:" + urlString);
        OkGo.<File>get(MyApplication.ConnectionUrl + urlString).params(params).tag(activity)
                .execute(new MyFileCallback(activity, doSomething));
    }

    public static void post(Activity activity, String urlString, HttpParams params, Consumer<WebResult> doSomething) {
        post(activity, urlString, params, doSomething, null, null);
    }

    public static void post(Activity activity, String urlString, HttpParams params, Consumer<WebResult> doSomething, Object obj) {
        post(activity, urlString, params, doSomething, obj, null);
    }

    /**
     * get文本数据
     *
     * @param obj 当返回对象为 WebResult&lt;List&lt;T&gt;&gt;时，obj==T，当obj==null时，返回无泛形的WebResult对象
     */
    @SuppressLint("CheckResult")
    public static void post(Activity activity, String urlString, HttpParams params
            , Consumer<WebResult> doSomething, Object obj, DialogPlus dialog) {
        PageUtils.Log(Optional.ofNullable(activity).map(Activity::getLocalClassName).orElse("")+ " post请求:" + urlString);
        OkGo.<String>post(MyApplication.ConnectionUrl + urlString).params(params).tag(activity)
                .execute(new MyStringCallback(activity, doSomething, obj, dialog));
    }
}
