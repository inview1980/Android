package my_manage.tool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import my_manage.password_box.pojo.MyTime;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public final class FileUtils {
    public static String PATH;
    public static String userFileName;
    public static String passwordManageDataFileName;
    public static String timeManageFileName;


    /**
     * 读取时间管理的项目列表
     */
    public static List<MyTime> readTimeManageFromFile() {
        List<MyTime> results = new ArrayList<>();
        if (StrUtils.isAnyBlank(PATH, timeManageFileName)) return results;
        try {
            File file = new File(PATH, timeManageFileName);
            if (file.exists()) {
                BufferedReader er = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                results = JSON.parseArray(readStringFromFile(er), MyTime.class);
            }
            return results;
        } catch (IOException e) {
            e.printStackTrace();
            return results;
        }
    }

    /**
     * 从文件中读取所有文本
     *
     * @param er BufferedReader的实例
     * @return String
     * @throws IOException
     */
    private static String readStringFromFile(BufferedReader er) throws IOException {
        StringBuilder sb = new StringBuilder();
        String tmp = null;
        while ((tmp = er.readLine()) != null) {
            sb.append(tmp);
        }
        return sb.toString();
    }

    /**
     * 保存时间管理的项目列表
     */
    public static boolean writeTimeManageToFile(Set<MyTime> myTimes) {
        if (myTimes == null || myTimes.size() == 0 || StrUtils.isAnyBlank(PATH, timeManageFileName))
            return false;
        try {
            File file = new File(PATH, timeManageFileName);
            if (file.exists()) file.delete();
            if (!file.createNewFile()) return false;
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            String str = JSONArray.toJSONString(myTimes);
            System.out.println(str);
            bw.write(str);
            bw.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String readString(String path, String filename) {
        File file = new File(path, filename);
        StringBuilder sb = new StringBuilder();
        if (!file.exists()) return null;
        try {
            BufferedReader er = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String tmp = null;
            while ((tmp = er.readLine()) != null) {
                sb.append(tmp);
            }
            er.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean writeStringToFile(String path, String filename, Object object) {
        if(null==object)return false;
        try {
            File file = new File(path, filename);
            if (file.exists() && !file.delete()) throw new IOException("删除数据库文件失败");
            if (file.createNewFile()) {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
                bw.write(JSON.toJSONString(object));
                bw.close();
                return true;
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
