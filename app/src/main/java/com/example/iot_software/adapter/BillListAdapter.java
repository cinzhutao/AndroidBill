package com.example.iot_software.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.iot_software.MyApplication;
import com.example.iot_software.R;
import com.example.iot_software.entity.BillInfo;
import com.example.iot_software.util.OkHttpUtil;

import java.util.List;

import okio.Buffer;

public class BillListAdapter extends BaseAdapter implements AdapterView.OnItemLongClickListener{

    private final Context mContext;
    private final List<BillInfo> mBillList;

    public BillListAdapter(Context context, List<BillInfo> billInfoList) {
        this.mContext = context;
        this.mBillList = billInfoList;
    }

    @Override
    public int getCount() {
        return mBillList.size();
    }

    @Override
    public Object getItem(int position) {
        return mBillList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mBillList.get(position).id;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_bill, null);
            holder.tv_date = convertView.findViewById(R.id.tv_date);
            holder.tv_remark = convertView.findViewById(R.id.tv_remark);
            holder.tv_amount = convertView.findViewById(R.id.tv_amount);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BillInfo bill = mBillList.get(position);
        holder.tv_date.setText(bill.date);
        holder.tv_remark.setText(bill.remark);
        holder.tv_amount.setText(String.format("%s:%d元", bill.type == 0 ? "收入" : "支出", (int) bill.amount));
        return convertView;
    }

    private void deleteBill(int position) {
        // 删除本地数据库
        // MyApplication.getInstance().getGmDBHelper().delete_bill_item(mBillList.get(position));
        // 此方法在okHttpUtil.deleteBillInfoToServer里面调用

        // 删除云端账单
        OkHttpUtil okHttpUtil = OkHttpUtil.getInstance(mContext);
        okHttpUtil.deleteBillInfoToServer(mBillList.get(position));

        // 删除列表中的账单
        mBillList.remove(position);
        notifyDataSetChanged();
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        BillInfo bill = mBillList.get(position); // 获得当前位置的账单信息
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        @SuppressLint("DefaultLocale")
        String desc = String.format("是否删除以下账单？\n%s %s%d %s", bill.date,
                bill.type==0?"收入":"支出", (int) bill.amount,
                bill.remark);
        builder.setMessage(desc); // 设置提醒对话框的消息文本
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteBill(position); // 删除该账单
            }
        });
        builder.setNegativeButton("否", null);
        AlertDialog dialog = builder.create(); // 显示提醒对话框
        Window window = dialog.getWindow();
        if (window != null) {
            //设置自身的底板透明
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //设置dialog周围activity背景的透明度，[0f,1f]，0全透明，1不透明黑
            window.setDimAmount(0.5f);
        }
        dialog.show();
        return true;
    }

    public final class ViewHolder {
        public TextView tv_date;
        public TextView tv_remark;
        public TextView tv_amount;
    }
}
