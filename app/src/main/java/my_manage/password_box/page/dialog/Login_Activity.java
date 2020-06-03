package my_manage.password_box.page.dialog;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import lombok.val;
import my_manage.iface.IShowList;
import my_manage.password_box.R;
import my_manage.password_box.listener.PasswordManageActivityListener;
import my_manage.password_box.page.PasswordManageActivity;
import my_manage.password_box.page.PasswordManageTotalActivity;
import my_manage.password_box.pojo.UserItem;
import my_manage.tool.database.DbBase;
import my_manage.tool.database.DbHelper;

@SuppressLint("Registered")
public final class Login_Activity extends AppCompatActivity implements IShowList {
    private static final String DEFAULT_KEY_NAME = "default_key";
private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        setTitle("登录");
        editText= findViewById(R.id.dialog_loginPwd);
        editText.setOnKeyListener((view, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_ENTER) {
                // 监听到回车键，会执行2次该方法。按下与松开
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    //按下事件
                    okBtn_onClick(null);
                }
            }
            return false;
        });
//        onAuthenticated();
        //指纹识别
        if (isNotLoginFirst() && supportFingerprint()) {
            initKey();
        }
    }

    private boolean isNotLoginFirst() {
        val item = DbBase.getInfoById(1, UserItem.class);
        if (item != null && item.getItemName() != null && "logged in".equals(item.getItemName()))
            return true;
        return false;
    }

    /**
     * 检测指纹的运行环境
     */
    public boolean supportFingerprint() {
        if (Build.VERSION.SDK_INT < 23) {
            Toast.makeText(this, "您的系统版本过低，不支持指纹功能", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            KeyguardManager    keyguardManager    = getSystemService(KeyguardManager.class);
            FingerprintManager fingerprintManager = getSystemService(FingerprintManager.class);
            if (!fingerprintManager.isHardwareDetected()) {
                Toast.makeText(this, "您的手机不支持指纹功能", Toast.LENGTH_SHORT).show();
                return false;
            } else if (!keyguardManager.isKeyguardSecure()) {
                Toast.makeText(this, "您还未设置锁屏，请先设置锁屏并添加一个指纹", Toast.LENGTH_SHORT).show();
                return false;
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                Toast.makeText(this, "您至少需要在系统设置中添加一个指纹", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    @TargetApi(23)
    private void initKey() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(DEFAULT_KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);
            keyGenerator.init(builder.build());
            keyGenerator.generateKey();
            initCipher(keyStore);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @TargetApi(23)
    private void initCipher(KeyStore keyStore) {
        try {
            SecretKey key = (SecretKey) keyStore.getKey(DEFAULT_KEY_NAME, null);
            Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            //显示指纹界面
            FingerprintDialogFragment fragment = new FingerprintDialogFragment(cipher);
            fragment.show(getSupportFragmentManager(), "fingerprint");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 调用PasswordManageActivity密码显示窗口
     */
    public void onAuthenticated() {
        Intent intent = new Intent(this, PasswordManageTotalActivity.class);
        startActivity(intent);
        finish();
    }

    public void cancel_OnClick(View view) {
        finish();
    }

    public void okBtn_onClick(View view) {
        if (!DbHelper.getInstance().loadIn(this, editText.getText().toString())) {
            Toast.makeText(this, "密码不正确", Toast.LENGTH_SHORT).show();
//            finish();//密码不正确，退出
        } else {
            onAuthenticated();
        }
    }


    public void resetPasswordBtn_onClick(View view) {
        PasswordManageActivityListener.resetDatabaseAndPassword(this);
    }

    @Override
    public void showList() {

    }
}
