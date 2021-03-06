package com.example.personal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {
    private static final String DATABASE_NAME = "Personal";
    private Context context;
    static OpenHelper openHelper;
    private SQLiteDatabase database;
    static final String tblReceiptPayment = "tblReceiptPayment";
    static final String caID = "caID";
    static final String caAccount = "caAccount";
    static final String caType = "caType";
    static final String caAmount = "caAmount";
    static final String caReason = "caReason";
    static final String caGroup = "caGroup";
    static final String caDate = "caDate";

    public DatabaseHandler(Context context) {
        this.context = context;
    }

    public DatabaseHandler open() {
        try {
            this.openHelper = new OpenHelper(context);
            this.database = openHelper.getWritableDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public void close() {
        openHelper.close();
    }

    public boolean addCA(String account, String type, String amount,
                         String reason, String group, String date) {
        ContentValues cv = new ContentValues();
        cv.put(caAccount,account);
        cv.put(caType, type);
        cv.put(caAmount, amount);
        cv.put(caGroup, group);
        cv.put(caReason, reason);
        cv.put(caDate, date);

        long result = -1;
        try {
            result = database.insert(tblReceiptPayment, null, cv);
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return result == -1 ? false : true;
    }

    public List<ReceiptPayment> getReceiptPayment() {
        ArrayList<ReceiptPayment> receiptPayments = new ArrayList<ReceiptPayment>();
        String sql = "select " + caAccount + ", " + caReason + ", "
                + caAmount + ", " + caDate + " from " + tblReceiptPayment;
        Cursor cursor = database.rawQuery(sql, null);
        if(cursor.moveToFirst()) {
            ReceiptPayment receiptPayment = null;
            do {
                receiptPayment = new ReceiptPayment();
                receiptPayment.setAccountCA(cursor.getString(0));
                receiptPayment.setReasonCA(cursor.getString(1));
                receiptPayment.setAmountCA(cursor.getString(2));
                receiptPayment.setDateCA(cursor.getString(3));
                receiptPayments.add(receiptPayment);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return receiptPayments;
    }

    // ham lay so tien theo tai khoan truyen vao nhu tien mat, tien tiet kiem, the tin dung
    public String getAmountByAccount(String account) {
        String amount = "0";
        String sql = "select sum(" + caAmount + ") from " + tblReceiptPayment + " where " + caAccount + "='" + account+"'";
        Cursor cursor = database.rawQuery(sql, null);
        if(cursor.moveToFirst()) {
            amount = cursor.getString(0);
        }
        return amount == null ? "0" : amount;
    }

    static class OpenHelper extends SQLiteOpenHelper{
        public OpenHelper(Context context) {
            super(context,DATABASE_NAME,null,1);
        }
        @Override
        public void onCreate(SQLiteDatabase database) {
            String sql = "create table "+tblReceiptPayment +"("
                    + caID + " integer primary key autoincrement not null,"
                    + caAccount + " text,"
                    + caType + " text,"
                    + caAmount +" text,"
                    + caReason + " text,"
                    + caGroup + " text,"
                    + caDate + " text );";
            database.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase database, int olVersion, int newVersion) {
            String sql = "drop table if exists tblReceiptPayment";
            onCreate(database);
        }

    }

}
