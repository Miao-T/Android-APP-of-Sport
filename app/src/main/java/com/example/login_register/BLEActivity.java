package com.example.login_register;

import androidx.appcompat.app.AlertDialog;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.login_register.BLE.BleAdapter;
import com.example.login_register.LitePalDatabase.DailyRecord;
import com.example.login_register.LitePalDatabase.UserInfo;
import com.example.login_register.Service.FloatWindowService;
import com.example.login_register.Utils.ActivityCollector;
import com.example.login_register.Utils.BaseActivity;
import com.example.login_register.Utils.ToastUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;

public class BLEActivity extends BaseActivity {

    private static final String TAG = "ble_tag" ;
    ListView bleListView;
    private Button mBtnUpdate;
    private Button mBtnConnect;
    private Button mBtnFloatBle;
    private LinearLayout operaView;
    private LinearLayout searchView;
    private TextView mTvResponse;
    private TextView mTvNotFindBle;
    private List<BluetoothDevice> mDatas;
    private List<Integer> mRssis;
    private BleAdapter mAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    public static BluetoothGatt mBluetoothGatt;
    private BluetoothDevice mBluetoothDevice;
    //SharedPreferences
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private String addressMAC;
    private String remember_step;
    //服务和特征值
    private UUID notify_UUID_service;
    private UUID notify_UUID_chara;

    //是否选择了List中的蓝牙设备
    private boolean flag_item = false;
    //判断是否点击了connect，否则闪退
    private boolean flag_gatt= false;
    //是否选择了记住密码
    private boolean flag_rememberMac = false;
    //自动连接时，是否找到相应蓝牙设备
    private boolean flag_scan_succeed = false;

    public static String dataBle;
    private long mExitTime;
    public static int data = 0;

    static Handler handler = new Handler();
    static Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_device);
        Log.d(TAG,"onCreate");

        LitePal.initialize(this);
        initView();
        initData();

        mBluetoothManager = (BluetoothManager)getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent,0);
        }
        Log.d(TAG,String.valueOf(data));
        if(mSharedPreferences.contains("MACAddress")){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    searchView.setVisibility(View.GONE);
                    operaView.setVisibility(View.VISIBLE);
                    mTvNotFindBle.setVisibility(View.INVISIBLE);
                }
            });
        }

        if(data == 0){
            remember_step = mSharedPreferences.getString("StepLastTime","");
            mTvResponse.setText(remember_step);
            checkPermissions();
        }else if(data == 1 && mSharedPreferences.contains("MACAddress")){
            mTvResponse.setText(dataBle);
            //mBluetoothGatt.disconnect();
            String RememberMAC = mSharedPreferences.getString("MACAddress","");
            BluetoothDevice connectedDevice = mBluetoothAdapter.getRemoteDevice(RememberMAC);
            connectBLE(connectedDevice);
            data = 0;
        }
    }


    private void initData(){
        mDatas = new ArrayList<>();
        mRssis = new ArrayList<>();
        mAdapter = new BleAdapter(BLEActivity.this,mDatas,mRssis);
        bleListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void initView(){
        bleListView = findViewById(R.id.ble_list_view);
        mBtnUpdate = findViewById(R.id.btn_update);
        mBtnConnect = findViewById(R.id.btn_connect);
        mBtnFloatBle = findViewById(R.id.btn_float_ble);
        operaView = findViewById(R.id.opera_view);
        searchView = findViewById(R.id.search_view);
        mTvResponse = findViewById(R.id.tv_response);
        mTvNotFindBle = findViewById(R.id.tv_not_find);
        mTvNotFindBle.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        mSharedPreferences = getSharedPreferences("User",MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        //the final step account


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
//                handler.removeCallbacks(runnable);
//                mBluetoothAdapter.stopLeScan(scanCallback);
                if(flag_item){
                    AlertDialog.Builder builder = new AlertDialog.Builder(BLEActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle("蓝牙").setMessage("是否记住蓝牙，以便下次直接连接")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    flag_rememberMac = true;
                                    connectBLE(mBluetoothDevice);
                                }
                            }).setNeutralButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            flag_rememberMac = false;
                            mEditor.remove("MACAddress");
                            mEditor.apply();
                            connectBLE(mBluetoothDevice);
                        }
                    }).show();

                }else{
                    ToastUtil.showMsg(BLEActivity.this,"请先选择蓝牙设备");
                }

            }
        });

        /**
         * 连接设备
         **/
        bleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.selectedItemPosition(position);
                mAdapter.notifyDataSetChanged();
                mBluetoothDevice = mDatas.get(position);
                flag_item = true;
            }
        });

        mBtnFloatBle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BLEActivity.this,FloatWindowService.class);
                startService(intent);
                ActivityCollector.finishAll();

//                Intent intent = new Intent(BLEActivity.this,LitePalActivity.class);
//                startActivity(intent);
            }
        });

        mTvNotFindBle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothGatt.disconnect();
                mBluetoothGatt.close();
                scanDevice();
            }
        });
    }


    /**
     * 检查权限
     */
    private void checkPermissions() {
        RxPermissions rxPermissions = new RxPermissions(BLEActivity.this);
        rxPermissions.request(android.Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(new io.reactivex.functions.Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            // 用户已经同意该权限
                            scanDevice();
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            ToastUtil.showMsg(BLEActivity.this,"用户开启权限后才能使用");
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
                Log.d(TAG,"5s");
                if(!flag_scan_succeed){
                    mTvNotFindBle.setVisibility(View.VISIBLE);
                }
            }
        };
        mBluetoothAdapter.startLeScan(scanCallback);
        handler.postDelayed(runnable,5000);
    }


    /**
     *扫描后显示设备
     **/
    BluetoothAdapter.LeScanCallback scanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            String RememberMAC = mSharedPreferences.getString("MACAddress","");
            Log.d(TAG, "run: scanning...");
            Log.d(TAG,"SCANNING" + device.getAddress());
            if (!mDatas.contains(device)){
                mDatas.add(device);
                mRssis.add(rssi);
                mAdapter.notifyDataSetChanged();
                //notifyDataSetChanged()可以在修改适配器绑定的数组后，不用重新刷新Activity，通知Activity更新ListView
                for(BluetoothDevice mData:mDatas){
                    if(mData.getAddress().equals(RememberMAC)){
//                        handler.removeCallbacks(runnable);
//                        mBluetoothAdapter.stopLeScan(scanCallback);
                        flag_scan_succeed = true;
                        connectBLE(mData);
                        Log.d(TAG,"equal");
                        break;
                    }
                }
                Log.d(TAG, "run: scanning...");
                Log.d(TAG,"SCANNING" + device.getAddress());
            }
        }
    };


    private void connectBLE(BluetoothDevice bluetoothDevice){
        mTvNotFindBle.setVisibility(View.INVISIBLE);
        handler.removeCallbacks(runnable);
        mBluetoothAdapter.stopLeScan(scanCallback);
        addressMAC = bluetoothDevice.getAddress();
        if(flag_rememberMac){
            mEditor.putString("MACAddress",addressMAC);
            mEditor.apply();
        }
        Log.d(TAG,bluetoothDevice.getAddress());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //6.0以上
            mBluetoothGatt = bluetoothDevice.connectGatt(BLEActivity.this,
                    true, gattCallback, TRANSPORT_LE);
            Log.d(TAG,gattCallback + "  is here?");
            flag_gatt = true;
            //TRANSPORT_LE参数是设置传输层模式
            //bluetoothDevice.connectGatt（）方法返回的对象BluetoothGatt,用于读写订阅操作
        } else {
            mBluetoothGatt = bluetoothDevice.connectGatt(BLEActivity.this,
                    true, gattCallback);
        }
    }


    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        /**
         * 断开或连接 状态发生变化时调用
         * */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.d(TAG,"onConnectionStateChange()" + status + "  " + newState);
            Log.d(TAG, String.valueOf( BluetoothGatt.STATE_DISCONNECTED));
            if (status == BluetoothGatt.GATT_SUCCESS){
                //连接成功
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTvNotFindBle.setVisibility(View.INVISIBLE);
                    }
                });
                if (newState == BluetoothGatt.STATE_CONNECTED){
                    Log.d(TAG,"连接成功");
                    //发现服务
                    gatt.discoverServices();
                }else if(newState == BluetoothGatt.STATE_DISCONNECTED){
                    Log.d(TAG,"STATE_DISCONNECTED");
                    return;
                }
            }else{
                //连接失败
                Log.d(TAG,"失败=="+status);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTvNotFindBle.setVisibility(View.VISIBLE);
                    }
                });
                mBluetoothGatt.close();
                //否则重复连接会报133连接失败
            }
        }

        /**
         * 发现服务（真正建立连接）
         * */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            //直到这里才是真正建立了可通信的连接
            Log.d(TAG,"onServicesDiscovered()---建立连接"+status);
            //获取初始化服务和特征值
            //initServiceAndChara();
            //订阅通知
            notify_UUID_chara = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
            notify_UUID_service= UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
            mBluetoothGatt.setCharacteristicNotification(mBluetoothGatt
                    .getService(notify_UUID_service).getCharacteristic(notify_UUID_chara),true);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    searchView.setVisibility(View.GONE);
                    operaView.setVisibility(View.VISIBLE);
                    mTvNotFindBle.setVisibility(View.INVISIBLE);
                }
            });
            if(status != BluetoothGatt.GATT_SUCCESS){
                Log.d(TAG,"unsucceed"+ status);
                mTvNotFindBle.setVisibility(View.VISIBLE);
            }
        }


        /**
         * 接收到硬件返回的数据
         * */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
//            Log.e(TAG,"onCharacteristicChanged()"+ HexUtil.encodeHexStr(characteristic.getValue()));
            Log.d(TAG,"onCharacteristicChanged()"+ bytes2hex(characteristic.getValue()));
            final byte[] data = characteristic.getValue();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());

//            UserInfo userInfo = LitePal.findLast(UserInfo.class);
//            DailyRecord dailyRecord = new DailyRecord();
//            dailyRecord.setDate(simpleDateFormat.format(date));
//            dailyRecord.setStep(Integer.valueOf(bytes2hex(data)));
//            dailyRecord.setUserInfo(userInfo);
//            dailyRecord.save();

            dataBle = bytes2hex(data);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTvResponse.setText(dataBle);
//                    addText(tvResponse,bytes2hex(data));
                }
            });

        }
    };


    /**
     * 二进制转16进制ASCII码
     * */
    private static final String HEX = "0123456789abcdef";
    public static String bytes2hex(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes)
        {
            // 取出这个字节的高4位，然后与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
            //sb.append(HEX.charAt((b >> 4) & 0x0f));
            // 取出这个字节的低位，与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
            sb.append(HEX.charAt(b & 0x0f));
        }
        return sb.toString();
    }


//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        BleAdapter.selectedPosition = -1;
//        if(dataBle != null){
//            mEditor.putString("StepLastTime",dataBle);
//            mEditor.apply();
//        }
//        //if(flag_gatt){mBluetoothGatt.disconnect();}
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                //Object mHelperUtils;
                ToastUtil.showMsg(BLEActivity.this,"再按一次退出APP");
                //System.currentTimeMillis()系统当前时间
                mExitTime = System.currentTimeMillis();
            } else {
                BleAdapter.selectedPosition = -1;
                if(dataBle != null){
                    mEditor.putString("StepLastTime",dataBle);
                    mEditor.apply();
                }
//                if(flag_gatt){
//                    mBluetoothGatt.disconnect();
//                    mBluetoothGatt.close();
//                }
                data = 1;
                ActivityCollector.finishAll();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
