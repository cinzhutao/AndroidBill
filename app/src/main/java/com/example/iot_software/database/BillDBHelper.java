package com.example.iot_software.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.iot_software.entity.BillInfo;

import java.util.ArrayList;
import java.util.List;

public class BillDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "bill.db";
    // 账单信息表
    private static final String TABLE_BILLS_INFO = "bill_info";
    private static final int DB_VERSION = 1;
    private static BillDBHelper mHelper = null;
    private SQLiteDatabase mRDB = null;
    private SQLiteDatabase mWDB = null;

    private BillDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // 利用单例模式获取数据库帮助器的唯一实例
    public static BillDBHelper getInstance(Context context) {
        if (mHelper == null) {
            mHelper = new BillDBHelper(context);
        }
        return mHelper;
    }

    // 打开数据库的读连接
    public SQLiteDatabase openReadLink() {
        if (mRDB == null || !mRDB.isOpen()) {
            mRDB = mHelper.getReadableDatabase();
        }
        return mRDB;
    }

    // 打开数据库的写连接
    public SQLiteDatabase openWriteLink() {
        if (mWDB == null || !mWDB.isOpen()) {
            mWDB = mHelper.getWritableDatabase();
        }
        return mWDB;
    }

    // 关闭数据库连接
    public void closeLink() {
        if (mRDB != null && mRDB.isOpen()) {
            mRDB.close();
            mRDB = null;
        }

        if (mWDB != null && mWDB.isOpen()) {
            mWDB.close();
            mWDB = null;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建账单信息表
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_BILLS_INFO +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " date VARCHAR NOT NULL," +
                " type INTEGER NOT NULL," +
                " amount DOUBLE NOT NULL," +
                " remark VARCHAR NOT NULL);";
        db.execSQL(sql);

    }

    public void delete_all_db() {
        mWDB.delete(TABLE_BILLS_INFO, null, null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // 保存一条订单信息
    public long save_bill_item(BillInfo billInfo) {
        ContentValues values = new ContentValues();
        values.put("date", billInfo.date);
        values.put("type", billInfo.type);
        values.put("amount", billInfo.amount);
        values.put("remark", billInfo.remark);
        return mWDB.insert(TABLE_BILLS_INFO, null, values);
    }

    // 保存初始化账单数据
    public void insertBill(int id, String date, String type, double amount, String remark) {
        ContentValues values = new ContentValues();
        values.put("_id", id);
        values.put("date", date);
        values.put("type", type);
        values.put("amount", amount);
        values.put("remark", remark);

        mWDB.insert(TABLE_BILLS_INFO, null, values);
    }

    @SuppressLint("Range")
    public List<BillInfo> queryByMonth(String yearMonth) {
        List<BillInfo> list = new ArrayList<>();
        // 2035-09-12
        // select * from bill_info where date like '2035-09%'
        String sql = "select * from " + TABLE_BILLS_INFO + " where date like '" + yearMonth + "%'";
        Cursor cursor = mRDB.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            BillInfo bill = new BillInfo();
            bill.id = cursor.getInt(cursor.getColumnIndex("_id"));
            bill.date = cursor.getString(cursor.getColumnIndex("date"));
            bill.type = cursor.getInt(cursor.getColumnIndex("type"));
            bill.amount = cursor.getDouble(cursor.getColumnIndex("amount"));
            bill.remark = cursor.getString(cursor.getColumnIndex("remark"));
            list.add(bill);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return list;
    }

    public void delete_bill_item(BillInfo bill) {
        // 构造删除条件
        String whereClause = "_id=? AND date=?";
        String[] whereArgs = {String.valueOf(bill.id), bill.date};

        // 执行删除操作
        mWDB.delete(TABLE_BILLS_INFO, whereClause, whereArgs);
    }

    @SuppressLint("Range")
    public BillInfo query_bill_by_date_remark(String date, String remark) {
        BillInfo billInfo = null; // 修改为 null 以便区分未查询到记录的情况
        String sql = "SELECT * FROM " + TABLE_BILLS_INFO + " WHERE date = ? AND remark = ?";
        Cursor cursor = mRDB.rawQuery(sql, new String[]{date, remark});
        if (cursor.moveToNext()) {
            billInfo = new BillInfo();
            billInfo.id = cursor.getInt(cursor.getColumnIndex("_id"));
            billInfo.date = cursor.getString(cursor.getColumnIndex("date"));
            billInfo.type = cursor.getInt(cursor.getColumnIndex("type"));
            billInfo.amount = cursor.getDouble(cursor.getColumnIndex("amount"));
            billInfo.remark = cursor.getString(cursor.getColumnIndex("remark"));
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return billInfo;
    }

}
