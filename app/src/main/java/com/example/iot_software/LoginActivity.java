package com.example.iot_software;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.iot_software.database.BillDBHelper;
import com.example.iot_software.util.OkHttpUtil;
import com.example.iot_software.util.SharedUtil;
import com.example.iot_software.util.ToastUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private TextView textView;
    private int count = 0;
    private Button btn_sign_in;
    private Button btn_sign_up;
    private EditText et_sign_email;
    private EditText et_sign_password;
    private SharedUtil mShareUtil;
    @SuppressLint("StaticFieldLeak")
    private static OkHttpUtil okHttpUtil;
    private static BillDBHelper billDBHelper;
    private ActivityResultLauncher<Intent> register;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);

        // 轻量存储
        mShareUtil = SharedUtil.getInstance(this);
        // 绑定按键
        btn_sign_in = findViewById(R.id.btn_sign_in);
        btn_sign_up = findViewById(R.id.btn_sign_up);
        et_sign_email = findViewById(R.id.et_sign_email);
        et_sign_password = findViewById(R.id.et_sign_password);

        btn_sign_in.setOnClickListener(this);
        btn_sign_up.setOnClickListener(this);

        register = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>(){
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent intent = result.getData();
                if (intent != null && result.getResultCode() == Activity.RESULT_OK) {
                    ToastUtil.show(LoginActivity.this, "请输入密码重新登录!");
                    // 用户密码已改为新密码，故更新密码变量
                    String newSignEmail = intent.getStringExtra("signEmail");
                    et_sign_email.setText(newSignEmail);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        String sign_email = et_sign_email.getText().toString();
        String sign_password = et_sign_password.getText().toString();
        long view_id = v.getId();

        if (view_id == R.id.btn_sign_in) {
            if (sign_email.equals("") || sign_password.equals("")) {
                ToastUtil.show(this, "账号密码不能为空");
                return;
            }

            String url = "http://100.95.237.253:12345/api/user";/*在此处改变你的服务器地址*/
            getCheckFromServer(this,url,sign_email,sign_password);
        } else if (view_id == R.id.btn_sign_up) {
            //String url2 = "http://100.95.229.116:12345/api/register";/*在此处改变你的服务器地址*/
            //registerNameWordToServer(url2,sign_email,sign_password);
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            intent.putExtra("signEmail", sign_email);
            register.launch(intent);
        }
    }

    private void registerNameWordToServer(String url, String signEmail, String signPassword) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("signEmail", signEmail);
        formBuilder.add("signPassword", signPassword);
        Request request = new Request.Builder()
                .url(url)
                .post(formBuilder.build())
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback(){

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ToastUtil.show(LoginActivity.this, "服务器错误");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String res = response.body().string();
                runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        if (res.equals("exist!"))
                        {
                            ToastUtil.show(LoginActivity.this, "该用户名已被注册");
                        } else {
                            ToastUtil.show(LoginActivity.this, "该用户名已成功注册");
                            mShareUtil.writeUserIDAndPassword(LoginActivity.this, "UserIDAndPassword", "signEmail", signEmail);
                        }
                    }
                });
            }
        });
    }

    private void getCheckFromServer(Context context, String url, String signEmail, String signPassword) {

        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("signEmail", signEmail);
        formBuilder.add("signPassword", signPassword);
        Request request = new Request.Builder()
                .url(url)
                .post(formBuilder.build())
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback(){

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ToastUtil.show(LoginActivity.this, "服务器错误");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String res = response.body().string();
                runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        if (res.equals("exist!"))
                        {
                            ToastUtil.show(LoginActivity.this, "成功登录!");

                            // 设置当前登录用户
                            MyApplication.getInstance().setSign_email(signEmail);

                            // 删除原先数据库数据
                            billDBHelper = MyApplication.getInstance().getGmDBHelper();
                            billDBHelper.delete_all_db();

                            // 获取除数数据库数据
                            okHttpUtil = OkHttpUtil.getInstance(context);
                            okHttpUtil.fetchBillInfoFromServer(signEmail);

                            // 跳转到添加账单页面
                            mShareUtil.writeUserIDAndPassword(LoginActivity.this, "UserIDAndPassword", "signEmail", signEmail);

                            // 跳转到添加账单页面
                            Intent intent = new Intent(context, BillPagerActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            ToastUtil.show(LoginActivity.this, "登录失败!");
                        }
                    }
                });
            }
        });
    }
}