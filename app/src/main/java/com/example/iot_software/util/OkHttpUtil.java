package com.example.iot_software.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iot_software.BillAddActivity;
import com.example.iot_software.LoginActivity;
import com.example.iot_software.MyApplication;
import com.example.iot_software.database.BillDBHelper;
import com.example.iot_software.entity.BillInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpUtil extends AppCompatActivity {

    private final Context context;
    @SuppressLint("StaticFieldLeak")
    private static OkHttpUtil okHttpUtil = null;

    public OkHttpUtil(Context context) {
        this.context = context;
    }

    public static OkHttpUtil getInstance(Context context) {
        if (okHttpUtil == null) {
            okHttpUtil = new OkHttpUtil(context);
        }
        return okHttpUtil;
    }

    // 向Server发送账单数据
    public void SendBillInfoToServer(BillInfo billInfo) {

        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("sign_email", MyApplication.getInstance().getSign_email());
        formBuilder.add("bill_id", String.valueOf(billInfo.id));
        formBuilder.add("bill_date", billInfo.date);
        formBuilder.add("bill_type", String.valueOf(billInfo.type));
        formBuilder.add("bill_remark", billInfo.remark);
        formBuilder.add("bill_amount", String.valueOf(billInfo.amount));
        Request request = new Request.Builder()
                .url("http://100.95.237.253:12345/api/rev_bill")
                .post(formBuilder.build())
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String res = response.body().string();
                runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        if (res.equals("Insert success!"))
                        {
                            ToastUtil.show(context, "该账单已成功添加到云端");
                        } else {
                            Log.e("zhu","改账单已存在");
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ToastUtil.show(context, "服务器错误");
                    }
                });
            }
        });
    }


    //  从Server获取账单数据
    public void fetchBillInfoFromServer(String signEmail) {
        String url = "http://100.95.237.253:12345/api/bills?sign_email=" + signEmail; // 为 Flask 服务器地址

        OkHttpClient okHttpClient = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder().
                url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String message = e != null ? e.getMessage() : "";
                Log.e("MyApplication", "Fetch bills failed: " + message);
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    String body = response.body().string();
                    Log.e("MyApplication", "Fetched bills: " + body);

                    // Parse the response and insert into local database
                    try {
                        JSONArray jsonArray = new JSONArray(body);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            // Extract bill info
                            int id = jsonObject.getInt("id");
                            String signEmail = jsonObject.getString("sign_email"); // 用不到
                            String date = jsonObject.getString("date");
                            String type = jsonObject.getString("type");
                            double amount = jsonObject.getDouble("amount");
                            String remark = jsonObject.getString("remark");

                            // Insert into local database
                            BillDBHelper GmDBHelper = MyApplication.getInstance().getGmDBHelper();
                            GmDBHelper.insertBill(id, date, type, amount, remark);
                        }
                    } catch (Exception e) {
                        Log.e("MyApplication", "JSON parse error: " + e.getMessage());
                    }
                } else {
                    Log.e("MyApplication", "Fetch bills failed: Response not successful");
                }
            }
        });
    }

    public void registerNameWordToServer(Context context, String url, String signEmail, String signPassword) {
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
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ToastUtil.show(context, "服务器错误");
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        assert response.body() != null;
                        final String res;
                        try {
                            res = response.body().string();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        if (res.equals("exist!"))
                        {
                            ToastUtil.show(context, "该用户名已被注册或者其他原因导致注册失败");
                        } else {
                            ToastUtil.show(context, "该用户名已成功注册");
                        }
                    }
                });
            }
        });
    }

    public void deleteBillInfoToServer(BillInfo billInfo) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("bill_id", String.valueOf(billInfo.id));  // 发送账单ID
        Request request = new Request.Builder()
                .url("http://100.95.237.253:12345/api/delete_bill")  // 云端的删除接口
                .post(formBuilder.build())
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String res = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ("Delete success!".equals(res)) {
                            // 本地删除账单
                            MyApplication.getInstance().getGmDBHelper().delete_bill_item(billInfo);
                            Log.d("OkHttpUtil", "Bill deleted from server and local database.");
                        } else {
                            Log.e("OkHttpUtil", "Failed to delete bill from server.");
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("OkHttpUtil", "Server error: " + e.getMessage());
                    }
                });
            }
        });
    }


}
