package my_manage.password_box.database;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import my_manage.password_box.pojo.UserItem;
import my_manage.password_box.secret.SecretUtil;
import my_manage.tool.FileUtils;
import my_manage.tool.StrUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import lombok.Getter;

public final class PasswordDB {
    @Getter
    private String path;
    @Getter
    private String filename;
    @Getter
    private String salt;
    private String password;

    private List<UserItem> items;
    private static PasswordDB db;
    private int PASSWORD_LENGTH = 18;

    private PasswordDB() {
    }

    public static PasswordDB init(String path, String filename, String password) {
        db = new PasswordDB();
        db.salt = db.getRandomString(db.PASSWORD_LENGTH);
        db.path = path;
        db.filename = filename;
        db.password = password;

        return db;
    }

    public void setPassword_Length(int length) {
        if (length > 10 && length < 50)
            this.PASSWORD_LENGTH = length;
    }

    public static PasswordDB init() {
        if (null == db || StrUtils.isAnyBlank(db.filename, db.path, db.password)) {
            throw new NullPointerException("数据库未初始化");
        }
        return db;
    }

    public List<UserItem> getItems() {
        if (this.items != null)
            return this.items;
        this.items = new ArrayList<>();
        String readStr=FileUtils.readString(path,filename);
        if(StrUtils.isBlank(readStr))
            return this.items;

        SaveDB tmpDB = JSON.parseObject(readStr, SaveDB.class);
        if (null != tmpDB && StrUtils.isAllNotBlank(tmpDB.salt, tmpDB.password, tmpDB.content)) {
            this.salt = tmpDB.salt;
            this.password = SecretUtil.getDecryptSting(salt, tmpDB.password);
            String oldStr = SecretUtil.getDecryptSting(this.password, tmpDB.content);
            this.items = Optional.ofNullable(JSON.parseArray(oldStr, UserItem.class))
                    .orElse(new ArrayList<>());
        }
        return this.items;
    }

    public boolean checkPassword( String pwd) {
        return this.password.equals(pwd);
    }

    public boolean changePassword(String oldPWD, String newPWD) {
        if (!this.password.equals(oldPWD)) {
            System.out.println("密码错误");
            return false;
        }
        return save(newPWD);
    }

    public boolean save() {
        SaveDB tmpDB = new SaveDB();
        tmpDB.salt = this.salt;
        tmpDB.password = SecretUtil.getEncryptString(this.salt, this.password);
        String tmpStr = JSON.toJSONString(this.items);
        tmpDB.content = SecretUtil.getEncryptString(this.password, tmpStr);
        Log.i(this.getClass().getSimpleName(), "保存数据库成功");
        return FileUtils.writeStringToFile(path, filename, tmpDB);
    }

    public boolean save(String pwd) {
        this.password = pwd;
        return this.save();
    }

    public boolean clearAndSave(String password) {
        String pwd = Optional.ofNullable(password).orElse(this.password);
        this.items.clear();
        db = init(path, filename, pwd);
        return db.save();
    }


    /**
     * 生成指定长度的字符串
     *
     * @param length 用户要求产生字符串的长度
     */
    private String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

}
