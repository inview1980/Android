package my_manage.password_box.page.dialog;

import android.annotation.SuppressLint;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.os.CancellationSignal;
import androidx.fragment.app.DialogFragment;

import my_manage.password_box.R;
import my_manage.tool.PageUtils;

import javax.crypto.Cipher;

@SuppressLint("ValidFragment")
public final class FingerprintDialogFragment extends DialogFragment {
    private FingerprintManagerCompat fingerprintManager;
    private CancellationSignal mcancellationSignal;
    private Cipher cipher;
    private Login_Activity mActivity;
    private TextView errorMsg;
    /**
     * 标识是否是用户主动取消的认证。
     */
    private boolean isSelfCancelled;

    public FingerprintDialogFragment(Cipher cipher) {
        this.cipher = cipher;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = (Login_Activity) getActivity();
        fingerprintManager = FingerprintManagerCompat.from(mActivity.getBaseContext());
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fingerprint_dialog, container, false);
        errorMsg = v.findViewById(R.id.dialog_finger_error);
        TextView cancel = v.findViewById(R.id.dialog_finger_cancel);
        cancel.setOnClickListener(v1 -> {
            dismiss();
            stopListening();
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 开始指纹认证监听
        startListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        // 停止指纹认证监听
        stopListening();
    }

    private void startListening() {
        isSelfCancelled = false;
        mcancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(new FingerprintManagerCompat.CryptoObject(cipher), 0, mcancellationSignal, new FingerprintManagerCompat.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                if (!isSelfCancelled) {
                    errorMsg.setText(errString);
                    if (errorCode == FingerprintManager.FINGERPRINT_ERROR_LOCKOUT) {
                        Toast.makeText(mActivity, errString, Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                }
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                errorMsg.setText(helpString);
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                PageUtils.showMessage(mActivity,  "指纹认证成功");
                mActivity.onAuthenticated();
            }


            @Override
            public void onAuthenticationFailed() {
                errorMsg.setText("指纹认证失败，请再试一次");
            }
        }, null);
    }

    private void stopListening() {
        if (mcancellationSignal != null) {
            mcancellationSignal.cancel();
            mcancellationSignal = null;
            isSelfCancelled = true;
        }
    }
}
