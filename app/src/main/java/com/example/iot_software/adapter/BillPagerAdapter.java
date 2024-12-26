package com.example.iot_software.adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.iot_software.fragment.BillFragment;

import java.util.Calendar;

public class BillPagerAdapter extends FragmentPagerAdapter {

    private static final int START_YEAR = 1999; // 起始年份
    private int mEndYear; // 当前年
    private int mTotalMonths; // 总月份数

    public BillPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        Calendar calendar = Calendar.getInstance();
        mEndYear = calendar.get(Calendar.YEAR);
        mTotalMonths = (mEndYear - START_YEAR + 1) * 12; // 计算总月份数
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        int year = START_YEAR + position / 12; // 计算年份
        int month = position % 12 + 1; // 计算月份
        String zeroMonth = month < 10 ? "0" + month : String.valueOf(month);
        String yearMonth = year + "-" + zeroMonth;
        Log.e("zhu", "new BillFragment: " + yearMonth);
        return BillFragment.newInstance(yearMonth);
    }

    @Override
    public int getCount() {
        return mTotalMonths; // 总月份数
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        int year = START_YEAR + position / 12; // 计算年份
        int month = position % 12 + 1; // 计算月份
        return year + "-" + month;
    }

    public void updateEndYear() {
        Calendar calendar = Calendar.getInstance();
        mEndYear = calendar.get(Calendar.YEAR);
        mTotalMonths = (mEndYear - START_YEAR + 1) * 12; // 重新计算总月份数
        notifyDataSetChanged();
    }
}
