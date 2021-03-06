package com.example.personal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AddCAActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner spAccount, spTypeCA, spGroupCA, spStatusCA;
    Button btnSave;
    EditText edDateCA, edAmount, edReasonCA;
    ImageView imgDatePicker;
    DatabaseHandler database;
    private int mYear,mMonth,mDay;
    static List<String> accounts = new ArrayList<String>(Arrays.asList("Tiền mặt", "Tiền tiết kiệm","Thẻ tín dụng"));
    static List<String> types = new ArrayList<String>(Arrays.asList("Khoản thu", "Khoản chi"));
    static List<String> groups = new ArrayList<String>(Arrays.asList("Đồ ăn", "Trang phục","Đi lại","Học tâp", "Sức khỏe", "Nhà cửa", "Nhận lương", "Khác"));
    static List<String> status = new ArrayList<String>(Arrays.asList("Hoàn tất", "Chưa hoàn tất"));

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_addca);

        database = new DatabaseHandler(this);
        database.open();

        btnSave = (Button) findViewById(R.id.btnSave);
        edDateCA = (EditText) findViewById(R.id.edDateCA);
        edAmount = (EditText) findViewById(R.id.edAmount);
        edReasonCA = (EditText) findViewById(R.id.edReason);
        imgDatePicker = (ImageView) findViewById(R.id.imgDatePicker);

        ArrayAdapter<String> adapter = null;
        // thiết lập chọn select taif khoan
        spAccount = (Spinner) findViewById(R.id.spinAccount);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, accounts);
        spAccount.setAdapter(adapter);

        // thiet lap chon loai giao dich
        spTypeCA = (Spinner)  findViewById(R.id.spinTypeCA);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, types);
        spTypeCA.setAdapter(adapter);

        // thiet lap chon phan nhom
        spGroupCA = (Spinner)  findViewById(R.id.spinGroup);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, groups);
        spGroupCA.setAdapter(adapter);

        // thiet lap chon trang thai
        spStatusCA = (Spinner)  findViewById(R.id.spinStatus);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, status);
        spStatusCA.setAdapter(adapter);


        // thiết lập việc chọn ngày giao dịch là ngày hiện tại
        edDateCA.setEnabled(false);
        edDateCA.setFocusable(false);
        Calendar c = Calendar.getInstance();
        mDay = c.get(Calendar.DATE);
        mMonth = c.get(Calendar.MONTH);
        mYear = c.get(Calendar.YEAR);

        // định dạng theo dd/MM/yyyy
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        edDateCA.setText(sdf.format(c.getTime()));

        // su kien khi nhan vao img thi hien ra datepicker (co dung 2 ham onCreateDialog, onDateSet o duoi de set gia tri cho no
        imgDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(0);
            }
        });

        // su kien nhan nut luu
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edAmount.getText().length() < 1 ) {
                    Toast.makeText(getApplicationContext(), "Bạn cần điền số tiền chi tiêu!", Toast.LENGTH_SHORT).show();
                    edAmount.requestFocus();
                } else if(edReasonCA.getText().length() < 1) {
                    Toast.makeText(getApplicationContext(), "Bạn cần điền lý do chi tiêu!", Toast.LENGTH_SHORT).show();
                    edReasonCA.requestFocus();
                } else {
                    String amount = edAmount.getText().toString();
                    if(spTypeCA.getSelectedItem().equals("Khoản chi")) {
                        amount = "-" + edAmount.getText().toString();
                    }
                    boolean check = database.addCA(spAccount.getSelectedItem().toString(), spTypeCA.getSelectedItem().toString(),
                            amount, edReasonCA.getText().toString(), spGroupCA.getSelectedItem().toString(),
                            edDateCA.getText().toString());
                    database.close();
                    if(check == true) {
                        Toast.makeText(getApplicationContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Thêm thất bại", Toast.LENGTH_SHORT).show();
                    }
                    Intent i = new Intent(AddCAActivity.this, CAActivity.class);
                    startActivity(i);
                }

            }
        });
    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 0:
                return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);

        }
        return null;

    }
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear+1;
            mDay = dayOfMonth;
            String day = mDay + "", month = mMonth+"";
            if(mDay < 10) day = "0"+mDay;
            if(mMonth < 10) month = "0"+mMonth;
            edDateCA.setText(new StringBuilder().append(day).append("/").append(month).append("/").append(mYear));

        }

    };

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
