package com.example.login_register;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.login_register.LitePalDatabase.UserInfo;
import com.example.login_register.PickerView.JsonBean;
import com.example.login_register.PickerView.SingleOptionsPicker;
import com.example.login_register.Utils.AgeUtil;
import com.example.login_register.Utils.BaseActivity;
import com.example.login_register.Utils.GetJsonDataUtil;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserInfoActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "userInfo_tag";
    private TextView mTvHeight,mTvWeight,mTvBirthday,mTvAddress;
    private Button mBtnSave;
    private List<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    public int yearBirth,monthBirth,dayBirth;
    public int yearNow,monthNow,dayNow;
    public String birthday;
    public int height,weight,age;
    public String province,city,area,location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        LitePal.initialize(this);
        mTvHeight = findViewById(R.id.tv_height);
        mTvWeight = findViewById(R.id.tv_weight);
        mTvBirthday = findViewById(R.id.tv_birthday);
        mTvAddress = findViewById(R.id.tv_address);
        mBtnSave = findViewById(R.id.btn_save_userInfo);

        mTvHeight.setOnClickListener(this);
        mTvWeight.setOnClickListener(this);
        mTvBirthday.setOnClickListener(this);
        mTvAddress.setOnClickListener(this);
        mBtnSave.setOnClickListener(this);
        initJsonData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.tv_height:
                List<String> listHeight = new ArrayList<>();
                for (int i = 60; i < 200; i++) {
                    listHeight.add(i + "CM");
                }
                SingleOptionsPicker.openOptionsPicker(this, listHeight,1, mTvHeight);
//                String height = mTvHeight.getText().toString();
//                Log.d(TAG,height);
                break;
            case R.id.tv_weight:
                List<String> listWeight = new ArrayList<>();
                for (int i = 30; i < 100; i++) {
                    listWeight.add(i + "KG");
                }
                SingleOptionsPicker.openOptionsPicker(this, listWeight,2, mTvWeight);
                break;
            case R.id.tv_birthday:
                showPickerViewBirthday();
                break;
            case R.id.tv_address:
                showPickerViewArea();
                break;
            case R.id.btn_save_userInfo:
                SaveToLitePal();
                break;
        }
    }

    private void SaveToLitePal(){
        String strHeight = mTvHeight.getText().toString();
        height = Integer.parseInt(strHeight.substring(0,strHeight.length()-2));
        String strWeight = mTvWeight.getText().toString();
        weight = Integer.parseInt(strWeight.substring(0,strWeight.length()-2));
        age = AgeUtil.getAge(yearNow,yearBirth,monthNow,monthBirth,dayNow,dayBirth);

        UserInfo userInfo = LitePal.findLast(UserInfo.class);
        userInfo.setAge(age);
        userInfo.setBirthday(birthday);
        userInfo.setLocation(location);
        userInfo.setHeight(height);
        userInfo.setWeight(weight);
        userInfo.save();

        Log.d(TAG,String.valueOf(height));
        Log.d(TAG,String.valueOf(weight));
        Log.d(TAG,String.valueOf(age));
        Log.d(TAG,birthday);
        Log.d(TAG,location);
        Intent intent = new Intent(UserInfoActivity.this,LoginActivity.class);
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
                birthday = yearBirth + "年 "
                        + monthBirth + "月 "
                        + dayBirth + "日";
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
}
