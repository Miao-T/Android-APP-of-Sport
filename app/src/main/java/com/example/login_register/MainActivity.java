package com.example.login_register;

import android.app.AlertDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.login_register.BLE.BleAdapter;
import com.example.login_register.Fragments.HomeFragment;
import com.example.login_register.Fragments.MainFragment;
import com.example.login_register.Fragments.MineFragment;
import com.example.login_register.LitePalDatabase.DailyRecord;
import com.example.login_register.LitePalDatabase.UserInfo;
import com.example.login_register.Service.FloatWindowService;
import com.example.login_register.Utils.ActivityCollector;
import com.example.login_register.Utils.BaseActivity;
import com.example.login_register.Utils.DoubleClickToExitUtil;
import com.example.login_register.Utils.ToastUtil;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;

public class MainActivity extends BaseActivity {

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private static final String TAG = "DB_tag";
    private BottomBar mBottomBar;
    //private TextView mTvUpdateStep;
    private long mExitTime;

    private HomeFragment homeFragment;
    private MainFragment mainFragment;
    private MineFragment mineFragment;
    private Fragment mCurrentFragment;

    private List<BluetoothDevice> mDatas;
    private List<Integer> mRssis;
    private BleAdapter mAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothDevice mBluetoothDevice;
    private UUID notify_UUID_service;
    private UUID notify_UUID_chara;

    //判断是否点击了connect，否则闪退
    private boolean flag_gatt= false;
    //是否选择了记住密码
    private boolean flag_rememberMac = false;
    //自动连接时，是否找到相应蓝牙设备
    private boolean flag_scan_succeed = false;
    private boolean flag_finishScan = false;
    private boolean flag_rememberDevice = false;

    static Handler handler = new Handler();
    static Runnable runnable;
    //从ScanActivity传过来的MAC地址
    private String DeviceMacIntent;
    //SharedPreference里保存的
    private String DeviceMacSP;
    public static String dataBle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothManager = (BluetoothManager)getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent,0);
        }
        mDatas = new ArrayList<>();
        mRssis = new ArrayList<>();

        homeFragment = new HomeFragment();
        mainFragment = new MainFragment();
        mineFragment = new MineFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.contentContainer,homeFragment)
                .add(R.id.contentContainer,mainFragment).add(R.id.contentContainer,mineFragment).commit();
        mBottomBar = findViewById(R.id.bottomBar);

        mSharedPreferences = getSharedPreferences("User",MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        String LoginName = mSharedPreferences.getString("RememberName",null);
        DeviceMacSP = mSharedPreferences.getString("DeviceMac",null);

        Log.d(TAG,"activity" + LoginName);
        Log.d(TAG,"activity" + DeviceMacSP);
        Intent ScanIntent = getIntent();
        flag_finishScan = ScanIntent.getBooleanExtra("FinishScan",false);
        flag_rememberDevice = ScanIntent.getBooleanExtra("RememberDevice",false);
        DeviceMacIntent = ScanIntent.getStringExtra("DeviceMac");

        if(flag_finishScan){
            BluetoothDevice Device = mBluetoothAdapter.getRemoteDevice(DeviceMacIntent);
            connectBLE(Device);
        }else{
            scanDevice();
        }


        /**
         * activity-fragment传数据
        * */
//        Bundle bundle = new Bundle();
//        bundle.putString("LoginName", LoginName);
//        //首先有一个Fragment对象 调用这个对象的setArguments(bundle)传递数据
//        mineFragment.setArguments(bundle);

        //        Bundle bundle = getArguments();
//        LoginName = bundle.getString("LoginName");
//        Log.d(TAG,"fragment" + LoginName);

        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                switch (tabId){
                    case R.id.tab1:
                        hideFragment(fragmentTransaction);
                        fragmentTransaction.show(homeFragment).commit();
                        ToastUtil.showMsg(MainActivity.this,"tag1");
                        break;
                    case R.id.tab2:
                        hideFragment(fragmentTransaction);
                        fragmentTransaction.show(mainFragment).commit();
                        ToastUtil.showMsg(MainActivity.this,"tag2");
                        break;
                    case R.id.tab3:
                        hideFragment(fragmentTransaction);
                        fragmentTransaction.show(mineFragment).commit();
                        ToastUtil.showMsg(MainActivity.this,"tag3");
                        break;
                }
            }
        });

    }

    private void hideFragment(FragmentTransaction fragmentTransaction){
        if(homeFragment != null){
            fragmentTransaction.hide(homeFragment);
        }
        if(mainFragment != null){
            fragmentTransaction.hide(mainFragment);
        }
        if(mineFragment != null){
            fragmentTransaction.hide(mineFragment);
        }
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
                    Log.d(TAG,"scan fail");
                    //mTvNotFindBle.setVisibility(View.VISIBLE);
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
            if (!mDatas.contains(device)){
                mDatas.add(device);
                mRssis.add(rssi);
                for(BluetoothDevice mData:mDatas){
                    if(mData.getAddress().equals(DeviceMacSP)){
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
        //mTvNotFindBle.setVisibility(View.INVISIBLE);
        handler.removeCallbacks(runnable);
        mBluetoothAdapter.stopLeScan(scanCallback);

//        addressMAC = bluetoothDevice.getAddress();
//        if(flag_rememberMac){
//            mEditor.putString("MACAddress",addressMAC);
//            mEditor.apply();
//        }
        if(flag_finishScan && flag_rememberDevice){
            mEditor.putString("DeviceMac",DeviceMacIntent);
            mEditor.apply();
        }else if((!flag_rememberDevice) && flag_finishScan){
            mEditor.remove("DeviceMac");
            mEditor.apply();
        }
        Log.d(TAG,bluetoothDevice.getAddress());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //6.0以上
            mBluetoothGatt = bluetoothDevice.connectGatt(MainActivity.this,
                    true, gattCallback, TRANSPORT_LE);
            Log.d(TAG,gattCallback + "  is here?");
            flag_gatt = true;
            //TRANSPORT_LE参数是设置传输层模式
            //bluetoothDevice.connectGatt（）方法返回的对象BluetoothGatt,用于读写订阅操作
        } else {
            mBluetoothGatt = bluetoothDevice.connectGatt(MainActivity.this,
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
                        //mTvNotFindBle.setVisibility(View.INVISIBLE);
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
                        //mTvNotFindBle.setVisibility(View.VISIBLE);
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
                    //searchView.setVisibility(View.GONE);
                    //operaView.setVisibility(View.VISIBLE);
                    //mTvNotFindBle.setVisibility(View.INVISIBLE);
                }
            });
            if(status != BluetoothGatt.GATT_SUCCESS){
                Log.d(TAG,"unsucceed"+ status);
                //mTvNotFindBle.setVisibility(View.VISIBLE);
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
            dataBle = bytes2hex(characteristic.getValue());
            Log.d(TAG,dataBle);

//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
//            Date date = new Date(System.currentTimeMillis());

//            UserInfo userInfo = LitePal.findLast(UserInfo.class);
//            DailyRecord dailyRecord = new DailyRecord();
//            dailyRecord.setDate(simpleDateFormat.format(date));
//            dailyRecord.setStep(Integer.valueOf(bytes2hex(data)));
//            dailyRecord.setUserInfo(userInfo);
//            dailyRecord.save();

            TextView mTvUpdateStep = homeFragment.getView().findViewById(R.id.tv_updateStep);
            mTvUpdateStep.setText(dataBle);

//            if(mCurrentFragment == homeFragment){
//                mTvUpdateStep = homeFragment.getView().findViewById(R.id.tv_updateStep);
//                mTvUpdateStep.setText(dataBle);
//            }else{
//                //broadcast
//            }
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

    private void switchFragment(Fragment target){
        if(mCurrentFragment == target)
            return;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(mCurrentFragment != null){
            transaction.hide(mCurrentFragment);
        }
        if(target.isAdded()){
            transaction.show(target);
        }else{
            transaction.add(R.id.contentContainer,target,target.getClass().getName());
        }
        transaction.commit();
        mCurrentFragment = target;
    }

    @Override
    public void onBackPressed() {
        mCurrentFragment = getSupportFragmentManager().findFragmentById(R.id.content_container);
        Log.d("fragment",String.valueOf(mCurrentFragment));
        if(mCurrentFragment != homeFragment){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            hideFragment(fragmentTransaction);
            fragmentTransaction.show(homeFragment).commit();
            mBottomBar.selectTabWithId(R.id.tab1);
            return;
        }else{
            moveTaskToBack(true);
        }
        super.onBackPressed();
    }


//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            moveTaskToBack(true);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}
