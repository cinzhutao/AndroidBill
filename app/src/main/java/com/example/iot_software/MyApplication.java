package com.example.iot_software;

import android.app.Application;

import com.example.iot_software.database.BillDBHelper;
import com.example.iot_software.util.OkHttpUtil;

public class MyApplication extends Application {

    private static MyApplication mApp;
    private static String sign_email;
    private static String yearMouth;

    public static MyApplication getInstance() {
        return mApp;
    }

    public static BillDBHelper GmDBHelper;

    public String getYearMouth() {
        return yearMouth;
    }

    public void setYearMouth(String yearMouth) {
        MyApplication.yearMouth = yearMouth;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;

        GmDBHelper = BillDBHelper.getInstance(this);
        GmDBHelper.openWriteLink();
        GmDBHelper.openReadLink();

    }

    public BillDBHelper getGmDBHelper(){
        return GmDBHelper;
    }

    public void setSign_email(String signEmail) {
        sign_email = signEmail;
    }

    public String getSign_email() {
        return sign_email;
    }
}