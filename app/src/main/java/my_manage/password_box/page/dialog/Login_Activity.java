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
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import my_manage.password_box.R;
import my_manage.password_box.database.PasswordDB;
import my_manage.password_box.page.PasswordManageActivity;
import my_manage.tool.PageUtils;

@SuppressLint("Registered")
public final class Login_Activity extends AppCompatActivity {
    private static final String DEFAULT_KEY_NAME = "default_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        setTitle("登录");
//        onAuthenticated();
        //指纹识别
        if (supportFingerprint()) {
            initKey();
        }
    }

    /**
     * 检测指纹的运行环境
     */
    public boolean supportFingerprint() {
        if (Build.VERSION.SDK_INT < 23) {
            Toast.makeText(this, "您的系统版本过低，不支持指纹功能", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            KeyguardManager keyguardManager = getSystemService(KeyguardManager.class);
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
        initDatabase();

        Intent intent = new Intent(this, PasswordManageActivity.class);
        startActivity(intent);
        finish();
    }

    public void cancel_OnClick(View view) {
        finish();
    }

    public void okBtn_onClick(View view) {
        initDatabase();

        EditText editText = findViewById(R.id.dialog_loginPwd);
        if (!PasswordDB.init().checkPassword(editText.getText().toString())) {
            Toast.makeText(this, "密码不正确", Toast.LENGTH_SHORT).show();
            finish();//密码不正确，退出
        } else {
            onAuthenticated();
        }
    }

    private void initDatabase() {
        //初始化数据库
        String DBFilename = getString(R.string.passwordDBFileName);
        String password = getString(R.string.defaultPassword);
        PasswordDB.init(getApplicationContext().getExternalFilesDir(null).getAbsolutePath(),
                DBFilename, password);
    }

    public void resetPasswordBtn_onClick(View view) {
//        Intent intent = new Intent(".receiver.PwdDBChangeReceiver");
//        intent.setComponent(new ComponentName("com.example.passwordsavingcabinet",
//                "com.example.passwordsavingcabinet.receiver.PwdDBChangeReceiver"));
////            sendBroadcast(intent);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//        Log.i(this.getLocalClassName(), "发送广播");
        PageUtils.resetDatabaseAndPassword(this);
    }

}
