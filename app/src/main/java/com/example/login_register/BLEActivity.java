package com.example.login_register;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.login_register.Adapter.BleAdapter;
import com.example.login_register.LitePalDatabase.DailyRecord;
import com.example.login_register.LitePalDatabase.UserInfo;
import com.example.login_register.Service.FloatWindowService;
import com.example.login_register.Utils.BaseActivity;
import com.example.login_register.Utils.HexUtil;
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
    ProgressBar pbSearchBle;
    ImageView ivSerBleStatus;
    TextView tvSerBleStatus;
    TextView tvSerBindStatus;
    ListView bleListView;
    private LinearLayout operaView;
    private Button btnWrite;
    private Button btnRead;
    private EditText etWriteContent;
    private TextView tvResponse;
    private List<BluetoothDevice> mDatas;
    private List<Integer> mRssis;
    private BleAdapter mAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    private boolean isScanning=false;
    private boolean isConnecting=false;
    private BluetoothGatt mBluetoothGatt;
    //SharedPreferences
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    //服务和特征值
    private UUID notify_UUID_service;
    private UUID notify_UUID_chara;
    private String AddressMAC;
//    private UUID write_UUID_service;
//    private UUID write_UUID_chara;
//    private UUID read_UUID_service;
//    private UUID read_UUID_chara;
//    private UUID indicate_UUID_service;
//    private UUID indicate_UUID_chara;
//    private String hex="7B46363941373237323532443741397D";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_device);

        LitePal.initialize(this);
        initView();
        initData();
        mBluetoothManager = (BluetoothManager)getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent,0);
        }
    }

    private void initData(){
        mDatas=new ArrayList<>();
        mRssis=new ArrayList<>();
        mAdapter=new BleAdapter(BLEActivity.this,mDatas,mRssis);
        bleListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void initView(){
        pbSearchBle=findViewById(R.id.progress_ser_bluetooth);
        ivSerBleStatus=findViewById(R.id.iv_ser_ble_status);
        tvSerBindStatus=findViewById(R.id.tv_ser_bind_status);
        tvSerBleStatus=findViewById(R.id.tv_ser_ble_status);
        bleListView=findViewById(R.id.ble_list_view);
        operaView=findViewById(R.id.opera_view);
        btnWrite=findViewById(R.id.btnWrite);
        btnRead=findViewById(R.id.btnRead);
        etWriteContent=findViewById(R.id.et_write);
        tvResponse=findViewById(R.id.tv_response);

        mSharedPreferences = getSharedPreferences("User",MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                //执行读取操作
//                readData();
                Intent intent = new Intent(BLEActivity.this,ForgetPsdActivity.class);
                startActivity(intent);
            }
        });

        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                //执行写入操作
//                writeData();
                Intent intent = new Intent(BLEActivity.this, FloatWindowService.class);
                startService(intent);
                finish();
            }
        });

        ivSerBleStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isScanning){
                    tvSerBindStatus.setText("停止搜索");
                    stopScanDevice();
                }else{
                    checkPermissions();
                }
            }
        });

        /**
         * 连接设备
         **/
        bleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                connectBLE(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(BLEActivity.this);
                builder.setTitle("蓝牙").setMessage("是否记住蓝牙，以便下次直接连接")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mEditor.putString("MACAddress",AddressMAC);
                                mEditor.apply();
                            }
                        }).setNeutralButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddressMAC = " ";
                        mEditor.putString("MACAddress",AddressMAC);
                        mEditor.apply();
                    }
                }).show();
            }
        });
    }

    private void connectBLE(int pst){
        if (isScanning){
            stopScanDevice();
        }
        if (!isConnecting){
            isConnecting = true;
            BluetoothDevice bluetoothDevice = mDatas.get(pst);
            //连接设备
            tvSerBindStatus.setText("连接中");
            Log.d(TAG,"连接"+pst);
            AddressMAC = bluetoothDevice.getAddress();
            Log.d(TAG,bluetoothDevice.getAddress());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //6.0以上
                mBluetoothGatt = bluetoothDevice.connectGatt(BLEActivity.this,
                        true, gattCallback, TRANSPORT_LE);
                //TRANSPORT_LE参数是设置传输层模式
                //bluetoothDevice.connectGatt（）方法返回的对象BluetoothGatt,用于读写订阅操作
            } else {
                mBluetoothGatt = bluetoothDevice.connectGatt(BLEActivity.this,
                        true, gattCallback);
            }
        }
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
     * 开始扫描 10秒后自动停止
     * */
    private void scanDevice(){
        tvSerBindStatus.setText("正在搜索");
        isScanning = true;
        pbSearchBle.setVisibility(View.VISIBLE);
        //设置进度条可见
        mBluetoothAdapter.startLeScan(scanCallback);
        //startLeScan和stopLeScan必须有参数，开始结束扫描
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //结束扫描
                mBluetoothAdapter.stopLeScan(scanCallback);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isScanning = false;
                        pbSearchBle.setVisibility(View.GONE);
                        tvSerBindStatus.setText("搜索已结束");
                    }
                });
            }
        },5000);
    }

    /**
     * 停止扫描
     * */
    private void stopScanDevice(){
        isScanning=false;
        pbSearchBle.setVisibility(View.GONE);
        mBluetoothAdapter.stopLeScan(scanCallback);
    }

    /**
     *扫描后显示设备
     **/
    BluetoothAdapter.LeScanCallback scanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            String RememberMAC = mSharedPreferences.getString("MACAddress","");
            Log.e(TAG, "run: scanning...");
            Log.e(TAG,"SCANNING" + RememberMAC);
            if (!mDatas.contains(device)){
                mDatas.add(device);
                mRssis.add(rssi);
                mAdapter.notifyDataSetChanged();
                //notifyDataSetChanged()可以在修改适配器绑定的数组后，不用重新刷新Activity，通知Activity更新ListView
            }
                if(device.getAddress().equals(RememberMAC)){
                    connectBLE(device);
                }

        }
    };
    private void connectBLE(BluetoothDevice bluetoothDevice){
        if (isScanning){
            stopScanDevice();
        }
        if (!isConnecting){
            isConnecting = true;
            //连接设备
            tvSerBindStatus.setText("连接中");
            Log.d(TAG,bluetoothDevice.getAddress());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //6.0以上
                mBluetoothGatt = bluetoothDevice.connectGatt(BLEActivity.this,
                        true, gattCallback, TRANSPORT_LE);
                //TRANSPORT_LE参数是设置传输层模式
                //bluetoothDevice.connectGatt（）方法返回的对象BluetoothGatt,用于读写订阅操作
            } else {
                mBluetoothGatt = bluetoothDevice.connectGatt(BLEActivity.this,
                        true, gattCallback);
            }
        }
    }

    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        /**
         * 断开或连接 状态发生变化时调用
         * */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.e(TAG,"onConnectionStateChange()" + status + "  " + newState);
            if (status == BluetoothGatt.GATT_SUCCESS){
                //连接成功
                if (newState == BluetoothGatt.STATE_CONNECTED){
                    Log.e(TAG,"连接成功");
                    //发现服务
                    gatt.discoverServices();
                }
            }else{
                //连接失败
                Log.e(TAG,"失败=="+status);
                mBluetoothGatt.close();
                //否则重复连接会报133连接失败
                isConnecting = false;
            }
        }
        /**
         * 发现服务（真正建立连接）
         * */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            //直到这里才是真正建立了可通信的连接
            isConnecting = false;
            Log.e(TAG,"onServicesDiscovered()---建立连接");
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
                    bleListView.setVisibility(View.GONE);
                    operaView.setVisibility(View.VISIBLE);
                    tvSerBindStatus.setText("已连接");
                }
            });
        }

        /**
         * 接收到硬件返回的数据
         * */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
//            Log.e(TAG,"onCharacteristicChanged()"+ HexUtil.encodeHexStr(characteristic.getValue()));
            Log.e(TAG,"onCharacteristicChanged()"+ bytes2hex(characteristic.getValue()));
            final byte[] data = characteristic.getValue();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());

            UserInfo userInfo = LitePal.findLast(UserInfo.class);
            DailyRecord dailyRecord = new DailyRecord();
            dailyRecord.setDate(simpleDateFormat.format(date));
            dailyRecord.setStep(Integer.valueOf(bytes2hex(data)));
            dailyRecord.setUserInfo(userInfo);
            dailyRecord.save();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvResponse.setText(bytes2hex(data));
//                    addText(tvResponse,bytes2hex(data));
                }
            });

        }

        /**
         * 读操作的回调
         * */
//        @Override
//        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//            super.onCharacteristicRead(gatt, characteristic, status);
//            Log.e(TAG,"onCharacteristicRead()");
//            //HexUtil.encodeHexStr(characteristic.getValue());
//        }

        /**
         * 写操作的回调
         * */
//        @Override
//        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//            super.onCharacteristicWrite(gatt, characteristic, status);
//            Log.e(TAG,"onCharacteristicWrite()  status="+status+",value="+ HexUtil.encodeHexStr(characteristic.getValue()));
//        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothGatt.disconnect();
    }


    /**
     * 获得蓝牙的UUID
     * */
//    private void initServiceAndChara(){
//        List<BluetoothGattService> bluetoothGattServices= mBluetoothGatt.getServices();
//        for (BluetoothGattService bluetoothGattService:bluetoothGattServices){
//            List<BluetoothGattCharacteristic> characteristics=bluetoothGattService.getCharacteristics();
//            for (BluetoothGattCharacteristic characteristic:characteristics){
//                int charaProp = characteristic.getProperties();
//                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
//                    read_UUID_chara=characteristic.getUuid();
//                    read_UUID_service=bluetoothGattService.getUuid();
//                    Log.e(TAG,"read_chara="+read_UUID_chara+"----read_service="+read_UUID_service);
//                }
//                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
//                    write_UUID_chara=characteristic.getUuid();
//                    write_UUID_service=bluetoothGattService.getUuid();
//                    Log.e(TAG,"write_chara="+write_UUID_chara+"----write_service="+write_UUID_service);
//                }
//                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
//                    write_UUID_chara=characteristic.getUuid();
//                    write_UUID_service=bluetoothGattService.getUuid();
//                    Log.e(TAG,"write_chara_2="+write_UUID_chara+"----write_service_2="+write_UUID_service);
//
//                }
//                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
//                    notify_UUID_chara=characteristic.getUuid();
//                    notify_UUID_service=bluetoothGattService.getUuid();
//                    Log.e(TAG,"notify_chara="+notify_UUID_chara+"----notify_service="+notify_UUID_service);
//                }
//                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
//                    indicate_UUID_chara=characteristic.getUuid();
//                    indicate_UUID_service=bluetoothGattService.getUuid();
//                    Log.e(TAG,"indicate_chara="+indicate_UUID_chara+"----indicate_service="+indicate_UUID_service);
//
//                }
//            }
//        }
//    }

    /**
     * 读取数据
     * */
//    private void readData() {
//        BluetoothGattCharacteristic characteristic=mBluetoothGatt.getService(read_UUID_service)
//                .getCharacteristic(read_UUID_chara);
//        mBluetoothGatt.readCharacteristic(characteristic);
//    }

    /**
     * 写人数据
     * */
//    private void writeData(){
//        //write_UUID_service和write_UUID_chara是硬件工程师告诉我们的
//        BluetoothGattService service = mBluetoothGatt.getService(write_UUID_service);
//        BluetoothGattCharacteristic charaWrite = service.getCharacteristic(write_UUID_chara);
//        byte[] data;
//        String content = etWriteContent.getText().toString();
//        if (!TextUtils.isEmpty(content)){
//            data = HexUtil.hexStringToBytes(content);
//        }else{
//            data = HexUtil.hexStringToBytes(hex);
//        }
//        if (data.length>20){//数据大于个字节 分批次写入
//            Log.e(TAG, "writeData: length="+data.length);
//            int num = 0;
//            if (data.length%20!=0){
//                num = data.length/20+1;
//            }else{
//                num = data.length/20;
//            }
//            for (int i=0;i<num;i++){
//                byte[] tempArr;
//                if (i==num-1){
//                    tempArr=new byte[data.length-i*20];
//                    System.arraycopy(data,i*20,tempArr,0,data.length-i*20);
//                }else{
//                    tempArr=new byte[20];
//                    System.arraycopy(data,i*20,tempArr,0,20);
//                }
//                charaWrite.setValue(tempArr);
//                mBluetoothGatt.writeCharacteristic(charaWrite);
//            }
//        }else{
//            charaWrite.setValue(data);
//            mBluetoothGatt.writeCharacteristic(charaWrite);
//        }
//    }

    /**
     * 收到数据一列一列地显示
     * */
//    private void addText(TextView textView, String content) {
//        textView.append(content);
//        textView.append("\n");
//        int offset = textView.getLineCount() * textView.getLineHeight();
//        if (offset > textView.getHeight()) {
//            textView.scrollTo(0, offset - textView.getHeight());
//        }
//    }
}
