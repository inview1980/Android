package my_manage.ui;

import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

import my_manage.MyApplication;
import my_manage.tool.PageUtils;
import my_manage.tool.http.HttpUtils;
import pojo.Info;
import tools.SecretUtils;
import web.WebResult;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author inview
 * @Date 2020/7/20 16:43
 * @Description :
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({PageUtils.class})
public class HttpUtilsTest {
private String data="{\"state\":200,\"data\":[{\"id\":8,\"path\":\"path:8\",\"create_time\":\"2373-10-03T11:34:31\",\"file_size\":0,\"user_Id\":4},{\"id\":17,\"path\":\"path:17\",\"create_time\":\"1771-05-14T21:02:32\",\"file_size\":0,\"user_Id\":4},{\"id\":21,\"path\":\"path:21\",\"create_time\":\"1885-11-10T17:01:32\",\"file_size\":0,\"user_Id\":4},{\"id\":22,\"path\":\"path:22\",\"create_time\":\"1551-04-29T05:07:32\",\"file_size\":0,\"user_Id\":4},{\"id\":28,\"path\":\"path:28\",\"create_time\":\"1818-03-16T05:43:32\",\"file_size\":0,\"user_Id\":4},{\"id\":30,\"path\":\"path:30\",\"create_time\":\"1621-03-12T15:17:32\",\"file_size\":0,\"user_Id\":4},{\"id\":34,\"path\":\"path:34\",\"create_time\":\"2389-04-29T20:18:32\",\"file_size\":0,\"user_Id\":4},{\"id\":35,\"path\":\"path:35\",\"create_time\":\"2430-10-29T02:12:32\",\"file_size\":0,\"user_Id\":4},{\"id\":43,\"path\":\"path:43\",\"create_time\":\"2294-05-10T14:10:33\",\"file_size\":0,\"user_Id\":4},{\"id\":51,\"path\":\"path:51\",\"create_time\":\"1969-05-12T01:22:33\",\"file_size\":0,\"user_Id\":4},{\"id\":61,\"path\":\"path:61\",\"create_time\":\"2239-12-31T21:40:33\",\"file_size\":0,\"user_Id\":4},{\"id\":71,\"path\":\"path:71\",\"create_time\":\"2116-07-02T00:05:34\",\"file_size\":0,\"user_Id\":4},{\"id\":74,\"path\":\"path:74\",\"create_time\":\"2365-06-03T13:45:34\",\"file_size\":0,\"user_Id\":4},{\"id\":76,\"path\":\"path:76\",\"create_time\":\"1676-05-30T19:13:34\",\"file_size\":0,\"user_Id\":4},{\"id\":98,\"path\":\"path:98\",\"create_time\":\"2218-10-07T17:34:35\",\"file_size\":0,\"user_Id\":4},{\"id\":110,\"path\":\"F:\\\\program\\\\java\\\\mymanageserver\\\\build\\\\libs\\\\data\\\\4_2020-07-16_01.myManage\",\"create_time\":\"2020-07-16T08:44:25\",\"file_size\":23552,\"user_Id\":4},{\"id\":111,\"path\":\"F:\\\\program\\\\java\\\\mymanageserver\\\\build\\\\libs\\\\data\\\\4_2020-07-16_02.myManage\",\"create_time\":\"2020-07-16T08:46:19\",\"file_size\":23552,\"user_Id\":4},{\"id\":112,\"path\":\"F:\\\\program\\\\java\\\\mymanageserver\\\\build\\\\libs\\\\data\\\\4_2020-07-16_03.myManage\",\"create_time\":\"2020-07-16T08:51:03\",\"file_size\":23552,\"user_Id\":4},{\"id\":115,\"path\":\"F:\\\\program\\\\java\\\\mymanageserver\\\\build\\\\libs\\\\data\\\\4_2020-07-16_04.myManage\",\"create_time\":\"2020-07-16T09:05:22\",\"file_size\":23552,\"user_Id\":4}],\"details\":\"成功\"}";

    @Test
    public void json() {
//        data = data.replace('T', ' ');
//        Gson              gson   = new Gson();
        Info       id=new Info();
        JSONObject jsonObject = JSON.parseObject(data);
////        webResult.setState(jsonObject.getIntValue("state"));
            //通过反射 得到UserBean.class
        List<Info> ts =JSON.parseArray(jsonObject.getString("data"),Info.class); //gson.fromJson(user, id.getClass());

        assertTrue(ts.size()>0);
    }

    @Test
    public void loginTest(){
////PowerMockito.mockStatic(PageUtils.class);
////        Mockito.doNothing().when(PowerMockito.mock(PageUtils.class));
//        String     userName   = "2";
//        String     pwd        = "2";
////        HttpParams httpParams = new HttpParams("username", userName);
////        httpParams.put("password", SecretUtils.getPasswordEncryptString(pwd));
////        HttpUtils.post(null, "/login", httpParams, webResult -> {
////            if (webResult.getState() == WebResult.OK) {
////                PageUtils.Log(String.valueOf(webResult.getDetails()));
////            } else if (webResult.getState() == WebResult.USER_NULL) {
////                PageUtils.showMessage(null,webResult.getDetails());
////            } else if (webResult.getState() == WebResult.PASSWORD_ERROR) {
////                PageUtils.showMessage(null,webResult.getDetails());
////            }
////        });
//        OkGo.<String>post(MyApplication.ConnectionUrl+"/login")
//                .params("username",userName)
//                .params("password",pwd)
//                .execute(new StringCallback() {
//                    @Override
//                    public void onSuccess(Response<String> response) {
//                        System.out.println(response.body());
//                    }
//                });
    }
}