package com.example.iot_software;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.iot_software.util.OkHttpUtil;
import com.example.iot_software.util.ToastUtil;

import java.util.Random;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private String signUpEmail;
    private String mVerifyCode;
    private EditText et_sign_up_email;
    private EditText et_password_first;
    private EditText et_password_second;
    private EditText et_verifycode;
    private static OkHttpUtil okHttpUtil;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        et_sign_up_email = findViewById(R.id.et_sign_up_email);
        et_password_first = findViewById(R.id.et_password_first);
        et_password_second = findViewById(R.id.et_password_second);
        et_verifycode = findViewById(R.id.et_verifycode);
        // 从上一个页面获取要修改密码的手机号码
        signUpEmail = getIntent().getStringExtra("signEmail");
        et_sign_up_email.setText(signUpEmail);

        findViewById(R.id.btn_verifycode).setOnClickListener(this);
        findViewById(R.id.btn_confirm).setOnClickListener(this);

        okHttpUtil = OkHttpUtil.getInstance(this);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_verifycode) {
            if (!signUpEmail.equals("")) {
                mVerifyCode = String.format("%06d", new Random().nextInt(999999));
                signUpEmail = et_sign_up_email.getText().toString();
                // 以下弹出提醒对话框，提示用户记住六位验证码数字
                AlertDialog.Builder buider = new AlertDialog.Builder(this);
                buider.setTitle("请记住验证码");
                buider.setMessage("邮箱号" + signUpEmail + ",本次验证码是" + mVerifyCode + ",请输入验证码");
                buider.setPositiveButton("好的", null);
                AlertDialog dialog = buider.create();
                Window window = dialog.getWindow();
                if (window != null) {
                    //设置自身的底板透明
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    //设置dialog周围activity背景的透明度，[0f,1f]，0全透明，1不透明黑
                    window.setDimAmount(0.5f);
                }
                dialog.show();
            } else {
                ToastUtil.show(this, "请输入完整的邮箱地址!");
            }
        } else if (v.getId() == R.id.btn_confirm) {
            signUpEmail = et_sign_up_email.getText().toString();
            String password_first = et_password_first.getText().toString();
            String password_second = et_password_second.getText().toString();
            if (password_first.isEmpty()) {
                Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password_first.equals(password_second)) {
                Toast.makeText(this, "两次输入的新密码不一致", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!mVerifyCode.equals(et_verifycode.getText().toString())) {
                Toast.makeText(this, "请输入正确的验证码", Toast.LENGTH_SHORT).show();
                return;
            }

            //  向服务器注册账号
            String url = "http://100.95.229.116:12345/api/register";/*在此处改变你的服务器地址*/
            okHttpUtil.registerNameWordToServer(SignUpActivity.this, url, signUpEmail, password_second);

            // 以下把修改好的新密码返回给上一个页面
            Intent intent = new Intent();
            intent.putExtra("signEmail", signUpEmail);
            //intent.putExtra("signPassword", password_first);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }
}