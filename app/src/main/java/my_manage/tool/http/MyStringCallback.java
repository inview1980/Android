//package my_manage.tool.http;
//
//import android.app.Activity;
//import android.content.Intent;
//
//import androidx.core.util.Consumer;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.lzy.okgo.callback.StringCallback;
//import com.lzy.okgo.model.Response;
//import com.orhanobut.dialogplus.DialogPlus;
//
//import java.util.List;
//
//import my_manage.tool.PageUtils;
//import my_manage.ui.common.Login_Activity;
//import web.WebResult;
//
///**
// * @author inview
// * @Date 2020/7/23 10:33
// * @Description :
// */
//public class MyStringCallback extends StringCallback {
//    private Activity            activity;
//    private Consumer<WebResult> doSomething;
//    private Object              obj;
//    private DialogPlus          dialog;
//
//    public MyStringCallback(Activity activity, Consumer<WebResult> doSomething, Object obj, DialogPlus dialog) {
//        this.activity = activity;
//        this.doSomething = doSomething;
//        this.obj = obj;
//        this.dialog = dialog;
//    }
//
//    @Override
//    public void onSuccess(Response<String> response) {
//        try {
//            WebResult webResult = unzip2WebResult(response, obj);
//            if (webResult.getState() == WebResult.NEED_LOGIN) {
//                /** 登录，要求返回值 */
//                Intent intent = new Intent(activity, Login_Activity.class);
//                intent.putExtra("isLogin", false);
//                activity.startActivityForResult(intent, 8080);
//            } else {
//                doSomething.accept(webResult);
//            }
//            if (dialog != null) dialog.dismiss();
//        } catch (Exception e) {
//            PageUtils.Error(activity.getLocalClassName() + " " + e.getLocalizedMessage());
//        }
//    }
//
//    @Override
//    public void onError(Response<String> response) {
////        super.onError(response);
//        PageUtils.showMessage(activity, "出错了");
//        if (dialog != null) dialog.dismiss();
//    }
//
//    /**
//     * 将接收到的数据转换
//     *
//     * @param res
//     * @param t   当返回对象为 WebResult&lt;List&lt;T&gt;&gt;时，obj==T
//     * @param <T>
//     * @return
//     */
//    private <T> WebResult unzip2WebResult(Response<String> res, T t) {
//        if (t == null) {
//            WebResult wr = JSON.parseObject(res.body(), WebResult.class);
//            return wr;
//        }
//        WebResult<List<T>> webResult = new WebResult<>();
////        if (IconDetails.class == t.getClass())
////            return JSON.parseObject(str, new TypeReference<WebResult<List<IconDetails>>>() {});
//        JSONObject jsonObject = JSON.parseObject(res.body());
//        webResult.setState(jsonObject.getIntValue("state"));
//        List<T> ts = (List<T>) JSON.parseArray(jsonObject.getString("data"), t.getClass());
//        webResult.setData(ts);
//        webResult.setDetails(jsonObject.getString("details"));
//        return webResult;
//    }
//}
