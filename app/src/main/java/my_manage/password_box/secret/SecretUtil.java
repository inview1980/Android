package my_manage.password_box.secret;

import com.litesuits.orm.db.assit.Encrypt;

import org.jasypt.util.text.BasicTextEncryptor;

/**
 * 加密算法util
 * Created on 2020/3/23.
 */
public final class SecretUtil {
    public static final String getEncryptString(String pwd, String word) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        //设置加密密钥
        textEncryptor.setPassword(pwd);

        //加密信息
        return textEncryptor.encrypt(word);
    }

    public static final String getDecryptSting(String pwd, String word) {
            BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
            //设置解密密钥
            textEncryptor.setPassword(pwd);
            //解密
        return textEncryptor.decrypt(word);
    }


}
