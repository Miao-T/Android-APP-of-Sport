package com.example.login_register;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;

import com.example.login_register.CloudSQL.DBConnection;

import com.example.login_register.Utils.BaseActivity;

public class AliSqlActivity extends BaseActivity{
    private EditText mEtId;
    private EditText mEtName;
    private Button mBtnSave,mBtnRead,mBtnDelete,mBtnCreateTable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ali_sql);

        mEtId = findViewById(R.id.et_id_cloud);
        mEtName = findViewById(R.id.et_name_cloud);
        mBtnSave = findViewById(R.id.btn_save_cloud);
        mBtnRead = findViewById(R.id.btn_read_cloud);
        mBtnDelete = findViewById(R.id.btn_delete_cloud);
        mBtnCreateTable = findViewById(R.id.btn_createTable_cloud);

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                @Override
                    public void run() {
                    String Name = mEtName.getText().toString();
                    int Id = Integer.parseInt(mEtId.getText().toString());
                    DBConnection.linkInsert(Id,Name);
                }
            }).start();
            }
        });

        mBtnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DBConnection.linkRead();
                    }
                }).start();
            }
        });

        mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String Name = mEtName.getText().toString();
                        DBConnection.linkDelete(Name);
                    }
                }).start();
            }
        });

        mBtnCreateTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DBConnection.linkCreateTable();
                    }
                }).start();
            }
        });
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String Name = mEtName.getText().toString();
//                int Id = Integer.parseInt(mEtId.getText().toString());
//                DBConnection.link(Id,Name);
//            }
//        }).start();
    }
}
