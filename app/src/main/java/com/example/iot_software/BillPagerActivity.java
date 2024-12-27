package com.example.iot_software;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager.widget.ViewPager;

import com.example.iot_software.adapter.BillPagerAdapter;
import com.example.iot_software.database.BillDBHelper;
import com.example.iot_software.util.DateUtil;

import java.util.Calendar;

public class BillPagerActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private TextView tv_month;
    private Calendar calendar;
    private ViewPager vp_bill;
    private BillDBHelper mDBHelper;
    private BillPagerAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bill_pager);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView tv_title = findViewById(R.id.tv_title);
        TextView tv_option = findViewById(R.id.tv_option);
        tv_title.setText("账单列表");
        tv_option.setText("添加账单");

        tv_month = findViewById(R.id.tv_month);
        // 显示当前日期
        calendar = Calendar.getInstance();
        tv_month.setText(DateUtil.getMonth(calendar));
        // 点击弹出日期对话框
        tv_month.setOnClickListener(this);

        tv_option.setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);

        mDBHelper = BillDBHelper.getInstance(this);
        mDBHelper.openReadLink();
        mDBHelper.openWriteLink();

        // 初始化翻页视图
        initViewPager();
    }

    private void initViewPager() {
        // 从布局视图中获取名叫pts_bill的翻页标签栏
        PagerTabStrip pts_bill = findViewById(R.id.pts_bill);
        // 设置翻页标签栏的文本大小
        pts_bill.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        vp_bill = findViewById(R.id.vp_bill);

        adapter = new BillPagerAdapter(getSupportFragmentManager());
        vp_bill.setAdapter(adapter);

        // 设置默认选中当前月份
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = (currentYear - 1999) * 12 + calendar.get(Calendar.MONTH);
        vp_bill.setCurrentItem(currentMonth);

        // 添加页面切换监听器
        vp_bill.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // 更新顶部显示的日期
                int year = 1999 + position / 12;
                int month = position % 12 + 1;
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month - 1);
                tv_month.setText(DateUtil.getMonth(calendar));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        int view_id = v.getId();
        if (view_id == R.id.tv_month) {
            // 弹出日期对话框
            DatePickerDialog dialog = new DatePickerDialog(this, DatePickerDialog.THEME_DEVICE_DEFAULT_LIGHT, this,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        } else if (view_id == R.id.tv_option) {
            // 跳转到添加账单页面
            Intent intent = new Intent(this, BillAddActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (view_id == R.id.iv_back) {
            // 关闭当前页面
            finish();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // 更新选择的日期
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        tv_month.setText(DateUtil.getMonth(calendar)); // 更新显示的日期

        // 更新 ViewPager 的选中项
        int targetPosition = (year - 1999) * 12 + month;
        vp_bill.setCurrentItem(targetPosition);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mDBHelper.closeLink();
    }
}
