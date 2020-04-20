package com.example.login_register;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.login_register.CloudSQL.DBConnection;
import com.example.login_register.LitePalDatabase.UserInfo;
import com.example.login_register.PickerView.JsonBean;
import com.example.login_register.PickerView.SingleOptionsPicker;
import com.example.login_register.Utils.AgeUtil;
import com.example.login_register.Utils.BaseActivity;
import com.example.login_register.Utils.GetJsonDataUtil;
import com.example.login_register.Utils.MonitorText;
import com.example.login_register.Utils.NetworkListener;
import com.example.login_register.Utils.ReadData;
import com.example.login_register.Utils.ToastUtil;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.litepal.LitePal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RegisterUserInfoActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "userInfo_tag";
    private RadioGroup mRgSex;
    private RadioButton mRbMale,mRbFemale;
    private TextView mTvHeight,mTvWeight,mTvBirthday,mTvAddress,mTvStep;
    private Button mBtnSave;
    private TextView mTvGap;
    private List<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private int yearBirth,monthBirth,dayBirth;
    private int yearNow,monthNow,dayNow;
    private String birthday;
    private double weight;
    private int sex,height,age,targetStep;
    private String province,city,area,location;
    private boolean flagSex = false,flagHeight = false,flagWeight = false,flagBirthday = false,flagLocation = false,flagStep = false;
    private String name;
    private NetworkListener networkListener;
    private boolean flagNetwork;
    private String flag;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private String sexSR,heightSR,weightSR,birthdaySR,ageSR,locationSR,targetStepSR;
    /**
     * weight先点就有Bug
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user_info);
        new Thread(new Runnable() {
            @Override
            public void run() {
                DBConnection.DriverConnection();
            }
        }).start();
        initView();
        mSharedPreferences = getSharedPreferences("User",MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        Intent intent = getIntent();
        flag = intent.getStringExtra("flag");
        if(flag.equals("2")){
            setTextViewEnableFalse();
            mTvGap.setText("修改个人信息");
            name = mSharedPreferences.getString("RememberName",null);
            sexSR = mSharedPreferences.getString("sex",null);
            heightSR = mSharedPreferences.getString("height",null);
            weightSR = mSharedPreferences.getString("weight",null);
            birthdaySR = mSharedPreferences.getString("birthday",null);
            locationSR = mSharedPreferences.getString("location",null);
            targetStepSR = mSharedPreferences.getString("targetStep",null);
            if(sexSR.equals("1")){
                mRbFemale.setChecked(true);
            }else{
                mRbMale.setChecked(true);
            }
            mTvHeight.setText(heightSR + "cm");
            mTvWeight.setText(weightSR + "kg");
            mTvBirthday.setText(birthdaySR);
            mTvAddress.setText(locationSR);
            mTvStep.setText(targetStepSR + "步");
        }else{
            name = intent.getStringExtra("RegisterName");
        }


        LitePal.initialize(this);

        mBtnSave.setEnabled(false);
        mBtnSave.setClickable(false);
        initJsonData();
        TextWatcher();

        mRgSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                flagSex = true;
                RadioButton radioButton = group.findViewById(checkedId);
                if(radioButton.getText().equals("男")){
                    sex = 2;//male
                }else{
                    sex = 1;//female
                }
                if(flagSex && flagHeight && flagWeight && flagBirthday && flagLocation && flagStep){
                    mBtnSave.setEnabled(true);
                    mBtnSave.setClickable(true);
                    Log.d(TAG,"click");
                }else{
                    mBtnSave.setEnabled(false);
                    mBtnSave.setClickable(false);
                    Log.d(TAG,"unclickable");
                }
            }
        });
    }

    private void setTextViewEnableFalse(){
        mBtnSave.setVisibility(View.GONE);
        mRbFemale.setEnabled(false);
        mRbFemale.setClickable(false);
        mRbMale.setEnabled(false);
        mRbMale.setClickable(false);
        mTvHeight.setEnabled(false);
        mTvHeight.setClickable(false);
        mTvWeight.setEnabled(false);
        mTvWeight.setClickable(false);
        mTvBirthday.setEnabled(false);
        mTvBirthday.setClickable(false);
        mTvAddress.setEnabled(false);
        mTvAddress.setClickable(false);
        mTvStep.setEnabled(false);
        mTvStep.setClickable(false);
        mRgSex.setEnabled(false);
        mRgSex.setClickable(false);
    }

    private void setTextViewEnableTrue(){
        mBtnSave.setVisibility(View.VISIBLE);
        mRbFemale.setEnabled(true);
        mRbFemale.setClickable(true);
        mRbMale.setEnabled(true);
        mRbMale.setClickable(true);
        mTvHeight.setEnabled(true);
        mTvHeight.setClickable(true);
        mTvWeight.setEnabled(true);
        mTvWeight.setClickable(true);
        mTvBirthday.setEnabled(true);
        mTvBirthday.setClickable(true);
        mTvAddress.setEnabled(true);
        mTvAddress.setClickable(true);
        mTvStep.setEnabled(true);
        mTvStep.setClickable(true);
        mRgSex.setEnabled(true);
        mRgSex.setClickable(true);
    }

    private void TextWatcher(){
        HeightWatcher();
        WeightWatcher();
        BirthdayWatcher();
        LocationWatcher();
        StepWatcher();
    }

    private void initView(){
        mRgSex = findViewById(R.id.rg_sex);
        mRbFemale = findViewById(R.id.rb_female);
        mRbMale = findViewById(R.id.rb_male);
        mTvHeight = findViewById(R.id.tv_height);
        mTvWeight = findViewById(R.id.tv_weight);
        mTvBirthday = findViewById(R.id.tv_birthday);
        mTvAddress = findViewById(R.id.tv_address);
        mTvStep = findViewById(R.id.tv_targetStep);
        mBtnSave = findViewById(R.id.btn_save_userInfo);
        mTvGap = findViewById(R.id.tv_gap);
        mTvGap.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        mTvHeight.setOnClickListener(this);
        mTvWeight.setOnClickListener(this);
        mTvBirthday.setOnClickListener(this);
        mTvAddress.setOnClickListener(this);
        mTvStep.setOnClickListener(this);
        mBtnSave.setOnClickListener(this);
        mTvGap.setOnClickListener(this);

        networkListener = new NetworkListener();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.tv_height:
                List<String> listHeight = new ArrayList<>();
                for (int i = 60; i < 200; i++) {
                    listHeight.add(i + "cm");
                }
                SingleOptionsPicker.openOptionsPicker(this, listHeight,1, mTvHeight);
                break;
            case R.id.tv_weight:
                List<String> listWeight = new ArrayList<>();
                for (double i = 30; i < 100; i = i +0.5) {
                    listWeight.add(i + "kg");
                }
                SingleOptionsPicker.openOptionsPicker(this, listWeight,2, mTvWeight);
                break;
            case R.id.tv_birthday:
                showPickerViewBirthday();
                break;
            case R.id.tv_address:
                showPickerViewArea();
                break;
            case R.id.tv_targetStep:
                List<String> listStep = new ArrayList<>();
                for (int i = 1000; i < 40000; i=i+500) {
                    listStep.add(i + "步");
                }
                SingleOptionsPicker.openOptionsPicker(this, listStep,3, mTvStep);
                break;
            case R.id.btn_save_userInfo:
                flagNetwork = networkListener.NetWorkState(RegisterUserInfoActivity.this);
                if(flagNetwork){
                    SaveToDatabase();
                }else{
                    ToastUtil.showMsg(RegisterUserInfoActivity.this,"请先打开移动数据，不然无法录入个人信息");
                }
                break;
            case R.id.tv_gap:
                if(mTvGap.getText().toString().equals("跳过")){
                    Ignore();
                }else{
                    setTextViewEnableTrue();
                    mBtnSave.setEnabled(true);
                    mBtnSave.setClickable(true);
                }
                break;
        }
    }

    private void SaveToDatabase(){
        if(mRbMale.isChecked()){
            sex = 2;
            //male
        }else{
            sex = 1;
            //female
        }
        Log.d("register_tag",name);
        Log.d("register_tag",String.valueOf(sex));
        String strHeight = mTvHeight.getText().toString();
        height = Integer.parseInt(strHeight.substring(0,strHeight.length()-2));
        Log.d("register_tag",String.valueOf(height));
        String strWeight = mTvWeight.getText().toString();
        weight = Double.parseDouble(strWeight.substring(0, strWeight.length() - 2));
        Log.d("register_tag",String.valueOf(weight));
        birthday = mTvBirthday.getText().toString();
        if(birthday.length() == 9){
            birthday = birthday.substring(0,5) + "0" + birthday.substring(5,9);
        }
        Log.d("register_tag",String.valueOf(birthday));
        Calendar calender = Calendar.getInstance();
        yearNow = calender.get(Calendar.YEAR);
        monthNow = calender.get(Calendar.MONTH)+1;
        dayNow = calender.get(Calendar.DATE);
        Log.d("register_tag",String.valueOf(yearNow));
        yearBirth = Integer.parseInt(birthday.substring(0,4));
        monthBirth = Integer.parseInt(birthday.substring(5,7));
        dayBirth = Integer.parseInt(birthday.substring(8,10));
        age = AgeUtil.getAge(yearNow,yearBirth,monthNow,monthBirth,dayNow,dayBirth);
        Log.d("register_tag",String.valueOf(age));
        location = mTvAddress.getText().toString();
        Log.d("register_tag",location);
        targetStep = Integer.parseInt(mTvStep.getText().toString().substring(0,mTvStep.getText().toString().length()-1));
        Log.d("register_tag",String.valueOf(targetStep));
        mEditor.putString("sex",String.valueOf(sex));
        mEditor.putString("height",String.valueOf(height));
        mEditor.putString("weight",String.valueOf(weight));
        mEditor.putString("birthday",birthday);
        mEditor.putString("age",String.valueOf(age));
        mEditor.putString("location",String.valueOf(location));
        mEditor.putString("targetStep",String.valueOf(targetStep));
        mEditor.apply();
        new Thread(new Runnable() {
            @Override
            public void run() {
                DBConnection.UpdateUserInfoByName(sex,height,weight,birthday,age,location,targetStep,name);
            }
        }).start();

//        UserInfo userInfo = LitePal.findLast(UserInfo.class);
//        userInfo.setAge(age);
//        userInfo.setBirthday(birthday);
//        userInfo.setLocation(location);
//        userInfo.setHeight(height);
//        userInfo.setWeight(weight);
//        userInfo.save();
        Log.d(TAG,String.valueOf(sex));
        Log.d(TAG,String.valueOf(height));
        Log.d(TAG,String.valueOf(weight));
        Log.d(TAG,String.valueOf(targetStep));
        Log.d(TAG,String.valueOf(age));
        Log.d(TAG,birthday);
        Log.d(TAG,location);
        if(mTvGap.getText().toString().equals("跳过")){
            Intent intent = new Intent(RegisterUserInfoActivity.this,LoginActivity.class);
            startActivity(intent);
        }else{
            ToastUtil.showMsg(RegisterUserInfoActivity.this,"修改成功");
            setTextViewEnableFalse();
        }
    }

    private void Ignore(){
        Intent intent = new Intent(RegisterUserInfoActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    private void showPickerViewBirthday(){
        Calendar calender = Calendar.getInstance();//系统当前时间
        Calendar startDate = Calendar.getInstance();
        startDate.set(1920, 1, 1);
        yearNow = calender.get(Calendar.YEAR);
        monthNow = calender.get(Calendar.MONTH)+1;
        dayNow = calender.get(Calendar.DATE);
        TimePickerView pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                yearBirth = date.getYear() - 100 + 2000;
                monthBirth = date.getMonth() + 1;
                dayBirth = date.getDate();
                birthday = String.valueOf(yearBirth) + "-"
                        + String.valueOf(monthBirth) + "-"
                        + String.valueOf(dayBirth);
                mTvBirthday.setText(birthday);

            }
        })
                .isCenterLabel(false)
                .setType(new boolean[]{true, true, true, false, false, false})
                .setDate(calender)
                .setRangDate(startDate, calender)
                .setTitleText("出生")
                .setBgColor(Color.parseColor("#F6F7F6"))
//                        .setTitleSize(getResources().getColor(R.color.text_color_333))
//                        .setSubmitColor(getResources().getColor(R.color.text_color_main))
//                        .setCancelColor(getResources().getColor(R.color.text_color_999))
                .build();
        pvTime.show();
    }


    private void showPickerViewArea() {// 弹出选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                province = options1Items.get(options1).getPickerViewText();
                city = options2Items.get(options1).get(options2);
                area = options3Items.get(options1).get(options2).get(options3);
                location = province + " " + city + " " + area;
                mTvAddress.setText(location);
            }
        })
                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .build();

        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }


    private void initJsonData() {//解析数据 （省市区三级联动）
        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        String JsonData = new GetJsonDataUtil().getJson(this, "province.json");//获取assets目录下的json文件数据

        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体

        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三级）

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市
                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    City_AreaList.add("");
                } else {
                    City_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(CityList);

            /**
             * 添加地区数据
             */
            options3Items.add(Province_AreaList);
        }
    }


    public ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    private void HeightWatcher(){
        mTvHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String strHeight = mTvHeight.getText().toString();
                height = Integer.parseInt(strHeight.substring(0,strHeight.length()-2));
                Log.d(TAG,strHeight);
                flagHeight = true;
                buttonEnable();
            }
        });
    }

    private void WeightWatcher() {
        mTvWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String strWeight = mTvWeight.getText().toString();
                weight = Double.parseDouble(strWeight.substring(0, strWeight.length() - 2));
                Log.d(TAG, strWeight);
                flagWeight = true;
                buttonEnable();
            }
        });
    }

    private void BirthdayWatcher(){
        mTvWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                age = AgeUtil.getAge(yearNow,yearBirth,monthNow,monthBirth,dayNow,dayBirth);
                Log.d(TAG,birthday);
                Log.d(TAG,"birthday");
                flagBirthday = true;
                buttonEnable();
            }
        });
    }

    private void LocationWatcher(){
        mTvWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG,location);
                Log.d(TAG,"location");
                flagLocation = true;
                buttonEnable();
            }
        });
    }

    private void StepWatcher() {
        mTvWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String strStep = mTvStep.getText().toString();
                targetStep = Integer.parseInt(strStep.substring(0,strStep.length()-1));
                Log.d(TAG, strStep);
                flagStep = true;
                buttonEnable();
            }
        });
    }

    private void buttonEnable(){
        if(flagSex && flagHeight && flagWeight && flagBirthday && flagLocation && flagStep){
            mBtnSave.setEnabled(true);
            mBtnSave.setClickable(true);
        }else if(mTvGap.getText().toString().equals("修改个人信息")){
            mBtnSave.setEnabled(true);
            mBtnSave.setClickable(true);
        }else{
            mBtnSave.setEnabled(false);
            mBtnSave.setClickable(false);
        }
    }
}
