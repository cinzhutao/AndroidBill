package com.example.iot_software.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.iot_software.MyApplication;
import com.example.iot_software.R;
import com.example.iot_software.adapter.BillListAdapter;
import com.example.iot_software.database.BillDBHelper;
import com.example.iot_software.entity.BillInfo;

import java.util.List;

public class BillFragment extends Fragment {

    public static BillFragment newInstance(String yearMonth) {
        BillFragment fragment = new BillFragment();
        Bundle args = new Bundle();
        args.putString("yearMonth", yearMonth);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bill, container, false);
        ListView lv_bill = view.findViewById(R.id.lv_bill);
        BillDBHelper mDBHelper = BillDBHelper.getInstance(getContext());

        // 获取年份和月份
        Bundle arguments = getArguments();
        String yearMonth = arguments != null ? arguments.getString("yearMonth") : "";
        assert yearMonth != null;

        // 查询对应月份的账单数据
        Log.e("zhu", "Fragment:"+yearMonth);
        List<BillInfo> billInfoList = mDBHelper.queryByMonth(yearMonth);

        // 构建并设置适配器
        BillListAdapter adapter = new BillListAdapter(getContext(), billInfoList);
        lv_bill.setAdapter(adapter);
        lv_bill.setOnItemLongClickListener(adapter); // 设置长按监听器

        return view;
    }
}