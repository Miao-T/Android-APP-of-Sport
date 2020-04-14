package com.example.login_register.BLE;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.login_register.BLEActivity;
import com.example.login_register.MainActivity;
import com.example.login_register.R;
import com.example.login_register.Utils.BaseActivity;
import com.example.login_register.Utils.ToastUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;

public class ScanBLEActivity extends BaseActivity {

    private static final String TAG = "ble_tag" ;
    ListView bleListView;
    private Button mBtnUpdate;
    private Button mBtnConnect;
    private List<BluetoothDevice> mDatas;
    private List<Integer> mRssis;
    private BleAdapter mAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothDevice mBluetoothDevice;
    //SharedPreferences
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private String addressMAC;

    static Handler handler = new Handler();
    static Runnable runnable;

    //是否选择了List中的蓝牙设备
    private boolean flag_item = false;
    //判断是否点击了connect，否则闪退
    private boolean flag_gatt= false;
    //是否选择了记住密码
    private boolean flag_rememberMac = false;
    //自动连接时，是否找到相应蓝牙设备
    private boolean flag_scan_succeed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_ble);

        LitePal.initialize(this);
        initView();
        initData();

        mBluetoothManager = (BluetoothManager)getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent,0);
        }

        checkPermissions();
    }

    private void initData(){
        mDatas = new ArrayList<>();
        mRssis = new ArrayList<>();
        mAdapter = new BleAdapter(ScanBLEActivity.this,mDatas,mRssis);
        bleListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void initView(){
        bleListView = findViewById(R.id.ble_list_view);
        mBtnUpdate = findViewById(R.id.btn_update);
        mBtnConnect = findViewById(R.id.btn_connect);

        mSharedPreferences = getSharedPreferences("User",MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        mBtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BleAdapter.selectedPosition = -1;
                mDatas.clear();
                scanDevice();
            }
        });

        mBtnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag_item){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ScanBLEActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle("蓝牙").setMessage("是否绑定蓝牙，以便下次直接连接")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    flag_rememberMac = true;
                                    handler.removeCallbacks(runnable);
                                    mBluetoothAdapter.stopLeScan(scanCallback);
//                                    mEditor.putString("DeviceMac",mBluetoothDevice.getAddress());
//                                    mEditor.apply();
                                    Log.d(TAG,"connectBLE Remember" + mBluetoothDevice.getName());
                                    Intent intent = new Intent(ScanBLEActivity.this, MainActivity.class);
                                    intent.putExtra("FinishScan",true);
                                    intent.putExtra("DeviceMac",mBluetoothDevice.getAddress());
                                    intent.putExtra("RememberDevice",true);
                                    startActivity(intent);
                                    finish();
                                }
                            }).setNeutralButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            flag_rememberMac = false;
                            handler.removeCallbacks(runnable);
                            mBluetoothAdapter.stopLeScan(scanCallback);
//                            mEditor.putString("DeviceMac","");
//                            mEditor.apply();
                            Log.d(TAG,"connectBLE No Remember");
                            Intent intent = new Intent(ScanBLEActivity.this, MainActivity.class);
                            intent.putExtra("FinishScan",true);
                            intent.putExtra("DeviceMac",mBluetoothDevice.getAddress());
                            intent.putExtra("RememberDevice",false);
                            startActivity(intent);
                            finish();
                        }
                    }).show();

                }else{
                    ToastUtil.showMsg(ScanBLEActivity.this,"请先选择蓝牙设备");
                }
            }
        });

        bleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.selectedItemPosition(position);
                mAdapter.notifyDataSetChanged();
                mBluetoothDevice = mDatas.get(position);
                flag_item = true;
            }
        });
    }

    /**
     * 检查权限
     */
    private void checkPermissions() {
        RxPermissions rxPermissions = new RxPermissions(ScanBLEActivity.this);
        rxPermissions.request(android.Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(new io.reactivex.functions.Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            // 用户已经同意该权限
                            scanDevice();
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            ToastUtil.showMsg(ScanBLEActivity.this,"用户开启权限后才能使用");
                        }
                    }
                });
    }

    /**
     * 开始扫描5秒后自动停止
     * */
    private void scanDevice(){
        Log.d(TAG,"scanDevice");
        runnable = new Runnable() {
            @Override
            public void run() {
                mBluetoothAdapter.stopLeScan(scanCallback);
                Log.d(TAG,"10s");
            }
        };
        mBluetoothAdapter.startLeScan(scanCallback);
        handler.postDelayed(runnable,10000);
    }

    /**
     *扫描后显示设备
     **/
    BluetoothAdapter.LeScanCallback scanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (!mDatas.contains(device)){
                mDatas.add(device);
                mRssis.add(rssi);
                mAdapter.notifyDataSetChanged();
                //notifyDataSetChanged()可以在修改适配器绑定的数组后，不用重新刷新Activity，通知Activity更新ListView
                Log.d(TAG, "run: scanning...");
                Log.d(TAG,"SCANNING" + device.getAddress());
            }
        }
    };

}


