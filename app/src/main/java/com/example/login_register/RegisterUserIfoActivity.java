package com.example.login_register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
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
import com.example.login_register.PickerView.Data;
import com.example.login_register.PickerView.JsonBean;
import com.example.login_register.PickerView.SingleOptionsPicker;
import com.example.login_register.Utils.BaseActivity;
import com.example.login_register.Utils.ToastUtil;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RegisterUserIfoActivity extends BaseActivity implements View.OnClickListener{
    private TextView textview1, textview2, textview3, textview4,textview5,textview6;
    private ArrayList<Data> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    //默认选中项（第一次为0,0,0）
    private int select1, select2, select3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user_ifo);

        textview1 = this.findViewById(R.id.textview1);
        textview2 = this.findViewById(R.id.textview2);
        textview3 = this.findViewById(R.id.textview3);
        textview4 = this.findViewById(R.id.textview4);
        textview5 = this.findViewById(R.id.textview5);
        textview6 = this.findViewById(R.id.textview6);
        textview1.setOnClickListener(this);
        textview2.setOnClickListener(this);
        textview3.setOnClickListener(this);
        textview4.setOnClickListener(this);
        textview5.setOnClickListener(this);
        textview6.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textview1:
                List<String> list1 = new ArrayList<>();
                for (int i = 0; i < 101; i++) {
                    list1.add(i + "岁");
                }
                SingleOptionsPicker.openOptionsPicker(this, list1, 1, textview1);
                break;

            case R.id.textview2:
                List<String> list2 = new ArrayList<>();
                for (int i = 80; i < 200; i++) {
                    list2.add(i + "CM");
                }
                SingleOptionsPicker.openOptionsPicker(this, list2, 2, textview2);
                break;

            case R.id.textview3:
                List<String> list3 = new ArrayList<>();
                for (int i = 30; i < 100; i++) {
                    list3.add(i + "KG");
                }
                SingleOptionsPicker.openOptionsPicker(this, list3, 3, textview3);
                break;

            case R.id.textview5:
                Calendar calender = Calendar.getInstance();//系统当前时间
                Calendar startDate = Calendar.getInstance();
                startDate.set(1920, 1, 1);
                Calendar selectedDate = Calendar.getInstance();
                TimePickerView pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        Log.d("TimeSelect",String.valueOf(date.getYear()-100+2000));
                        Log.d("TimeSelect",String.valueOf(date.getMonth()+1));
                        Log.d("TimeSelect",String.valueOf(date.getDate()));
                        String SelectedDate = String.valueOf(date.getYear()-100+2000) + "年" +
                                String.valueOf(date.getMonth()+1) + "月" +
                                String.valueOf(date.getDate()) + "日";
                        textview5.setText(SelectedDate);
//                        Data mTextViewYear = new Data();
//                        Data mTextViewMonth = new Data();
//                        Data mTextViewDay = new Data();
//
//                        Calendar calendar = Calendar.getInstance();
//                        mTextViewYear.setName(calendar.get(Calendar.YEAR) + "");
//                        mTextViewMonth.setName(calendar.get(Calendar.MONTH) + 1 + "");// 月份比实际的月份少1个月
//                        mTextViewDay.setName(calendar.get(Calendar.DAY_OF_MONTH) + "");
//                        Log.e("TimeSelect", "birth:" + date);
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

                pvTime.show(v);
                break;

            case R.id.textview6:
                Intent intent = new Intent(RegisterUserIfoActivity.this,OptionPickerActivity.class);
                startActivity(intent);
            break;

            case R.id.textview4:
                //省份
                ArrayList<Data> list = new ArrayList<>();
                //添加3个个省份
                Data data1 = new Data();
                Data data2 = new Data();
                Data data3 = new Data();
                data1.setName("江西省");
                data2.setName("河南省");
                data3.setName("湖北省");
                //添加市数据（省下面的数据）
                ArrayList<Data.CityBean> cityBeans1 = new ArrayList<>();
                ArrayList<Data.CityBean> cityBeans2 = new ArrayList<>();
                ArrayList<Data.CityBean> cityBeans3 = new ArrayList<>();
                //（第一个省份下面的市数据）
                Data.CityBean cityBean1 = new Data.CityBean();
                Data.CityBean cityBean2 = new Data.CityBean();
                Data.CityBean cityBean3 = new Data.CityBean();
                cityBean1.setName("南昌");
                cityBean2.setName("九江");
                cityBean3.setName("赣州");
                cityBeans1.add(cityBean1);
                cityBeans1.add(cityBean2);
                cityBeans1.add(cityBean3);
                //（第二个省份下面的市数据）
                Data.CityBean cityBean4 = new Data.CityBean();
                Data.CityBean cityBean5 = new Data.CityBean();
                Data.CityBean cityBean6 = new Data.CityBean();
                cityBean4.setName("洛阳");
                cityBean5.setName("开封");
                cityBean6.setName("许昌");
                cityBeans2.add(cityBean4);
                cityBeans2.add(cityBean5);
                cityBeans2.add(cityBean6);
                //（第三个省份下面的市数据）
                Data.CityBean cityBean7 = new Data.CityBean();
                Data.CityBean cityBean8 = new Data.CityBean();
                Data.CityBean cityBean9 = new Data.CityBean();
                cityBean7.setName("黄冈");
                cityBean8.setName("荆州");
                cityBean9.setName("鄂州");
                cityBeans3.add(cityBean7);
                cityBeans3.add(cityBean8);
                cityBeans3.add(cityBean9);

                //为市添加区（一共为9个市）
                ArrayList<String> area1 = new ArrayList<>();
                ArrayList<String> area2 = new ArrayList<>();
                ArrayList<String> area3 = new ArrayList<>();
                ArrayList<String> area4 = new ArrayList<>();
                ArrayList<String> area5 = new ArrayList<>();
                ArrayList<String> area6 = new ArrayList<>();
                ArrayList<String> area7 = new ArrayList<>();
                ArrayList<String> area8 = new ArrayList<>();
                ArrayList<String> area9 = new ArrayList<>();
                area1.add("东湖区");
                area1.add("西湖区");
                area1.add("青云谱区");
                area2.add("浔阳区");
                area2.add("濂溪区");
                area2.add("九江县");
                area3.add("赣县");
                area3.add("信丰");
                area3.add("南康");
                area4.add("西工区");
                area4.add("洛龙区");
                area4.add("涧西区");
                area5.add("鼓楼区");
                area5.add("龙亭区");
                area5.add("金明区");
                area6.add("禹州市");
                area6.add("长葛市");
                area6.add("许昌县");
                area7.add("团风县");
                area7.add("浠水县");
                area7.add("罗田县");
                area8.add("荆州区");
                area8.add("沙市区");
                area8.add("江陵县");
                area9.add("鄂城区");
                area9.add("华容区");
                area9.add("梁子湖区");

                //将区添加至市中（27个区）
                cityBean1.setArea(area1);
                cityBean2.setArea(area2);
                cityBean3.setArea(area3);
                cityBean4.setArea(area4);
                cityBean5.setArea(area5);
                cityBean6.setArea(area6);
                cityBean7.setArea(area7);
                cityBean8.setArea(area8);
                cityBean9.setArea(area9);

                //将市添加至省中（9个市）
                data1.setCity(cityBeans1);
                data2.setCity(cityBeans2);
                data3.setCity(cityBeans3);

                //将市添加至省中(3个省)
                list.add(data1);
                list.add(data2);
                list.add(data3);

                /**
                 * 添加省份数据(也可以添加string)
                 * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
                 * PickerView会通过getPickerViewText方法获取字符串显示出来。
                 */
                options1Items = list;

                for (int i = 0; i < list.size(); i++) {//遍历省份
                    ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
                    ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）
                    for (int c = 0; c < list.get(i).getCity().size(); c++) {//遍历该省份的所有城市
                        String CityName = list.get(i).getCity().get(c).getName();
                        CityList.add(CityName);//添加城市
                        ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表
                        //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                        if (list.get(i).getCity().get(c).getArea() == null
                                || list.get(i).getCity().get(c).getArea().size() == 0) {
                            City_AreaList.add("");
                        } else {
                            City_AreaList.addAll(list.get(i).getCity().get(c).getArea());
                        }
                        Province_AreaList.add(City_AreaList);//添加该省所有地区数据
                    }
                    options2Items.add(CityList);
                    options3Items.add(Province_AreaList);
                }

                new SingleOptionsPicker(this, select1, select2, select3,
                        options1Items, options2Items, options3Items,
                        new SingleOptionsPicker.OnPickerOptionsClickListener() {
                            @Override
                            public void onOptionsSelect(int options1, int options2, int options3, View view) {
                                //返回的分别是三个级别的选中位置
                                String string = options1Items.get(options1).getPickerViewText() +
                                        options2Items.get(options1).get(options2) +
                                        options3Items.get(options1).get(options2).get(options3);
                                textview4.setText(string);
                                //将选择后的选中项赋值
                                select1 = options1;
                                select2 = options2;
                                select3 = options3;
                            }
                        }).show();
                break;
        }
    }
}

