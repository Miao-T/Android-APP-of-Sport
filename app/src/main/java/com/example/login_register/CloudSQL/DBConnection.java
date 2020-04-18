package com.example.login_register.CloudSQL;

import android.util.Log;

import androidx.annotation.RequiresPermission;

import com.example.login_register.R;
import com.example.login_register.Utils.ReadData;
import com.github.mikephil.charting.data.BarEntry;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;

import javax.xml.transform.Result;

public class DBConnection implements ReadData {
    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String url = "jdbc:mysql://cdb-ksj8ugkk.bj.tencentcdb.com:10250/user";
    private static final String userName = "root";
    private static final String password = "DidideMiao531";
    private static boolean result;


    /**
     * 加载驱动
    * */
    public static void DriverConnection(){
        try{
            Class.forName(driver).newInstance();
            Log.d("DB_tag","驱动加载成功！！！");
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 注册账号
     * */
    public static void RegisterAccount(String name,String phoneNum,String psd,String registerDate){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
//            String sql = "INSERT INTO userInfo(userId,userName) VALUES ('31116202','zgd')";
            //String sql = "INSERT INTO demo(id,name) VALUES ('"+id+"','"+name+"')";
            String sql = "INSERT INTO userInfo(userName,phoneNumber,password,registerDate) " +
                    "VALUES ('"+name+"','"+phoneNum+"','"+psd+"','"+registerDate+"')";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.execute(sql);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(connection != null){
                try{
                    connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 注册后自动建表，表明为用户名
    * */
    public static void CreateTable(String name){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
            //time datetime PRIMARY KEY,
            String tableName = "t" + name;
            String sql = "CREATE TABLE "+tableName+"(id int AUTO_INCREMENT PRIMARY KEY,time datetime NOT NULL,stepData varchar(50) NOT NULL,state varchar(5) NOT NULL)";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.execute(sql);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(connection != null){
                try{
                    connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 录入用户信息
     * */
    public static void UpdateUserInfoByName(int sex,int height,double weight,String birthday,int age,String location,int targetStep,String name){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
            String sql  = "UPDATE userInfo SET sex = '"+sex+"',height = '"+height+"',weight = '"+weight+"'," +
                    "birthday = '"+birthday+"', age = '"+age+"',location = '"+location+"',targetStep = '"+targetStep+"'" +
                    "WHERE userName = '"+name+"'";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.execute(sql);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(connection != null){
                try{
                    connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 读取用户信息
     * */
    @Override
    public EnumMap<UserInfoData, Object> ReadCloudData(String name,String phoneNum) {
        Connection connection = null;
        EnumMap<UserInfoData,Object> retMap = new EnumMap<UserInfoData, Object>(UserInfoData.class);
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
            String sql = "SELECT userId,userName,password FROM userInfo WHERE userName = '"+name+"'  OR phoneNumber = '"+phoneNum+"'";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()){
                int userId = resultSet.getInt("userId");
                String userName = resultSet.getString("userName");
                String psd = resultSet.getString("password");
                retMap.put(UserInfoData.userId,userId);
                retMap.put(UserInfoData.password,psd);
                retMap.put(UserInfoData.userName,userName);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(connection != null){
                try {
                    connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return retMap;
    }


    /**
     * 注销账号表
    * */
    public static void DropTable(String name){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
            String TableName = "t" + name;
            String sql = "DROP TABLE "+TableName+"";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.execute(sql);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(connection != null){
                try{
                    connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 注销账号信息
     * */
    public static void DeleteAccountData(String name){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
            //String sql = "ALTER TABLE userInfo CHANGE userId userId int AUTO_INCREMENT";
            String sql = "DELETE FROM userInfo WHERE userName = '"+name+"'";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.execute(sql);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(connection != null){
                try{
                    connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 每小时自动存步数
    * */
    public static void AutoInsertStep(String date,String step,String name,String state){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
            String tableName = "t" + name;
            String sql = "INSERT INTO "+tableName+"(time,stepData,state) VALUES ('"+date+"','"+step+"','"+state+"')";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.execute(sql);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(connection != null){
                try{
                    connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 读取并计算之前的总步数
     * */
    public static Integer ReadLastStep(String name,String time){
        Connection connection = null;
        Integer totalStep = 0;
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
            String tableName = "t" + name;
            String exactTime = time + "%";
            //String sql = "SELECT stepData FROM "+name+" ORDER by id DESC limit 1";
            String sql = "SELECT stepData FROM "+tableName+" WHERE time LIKE '"+exactTime+"' AND state = '0'";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            totalStep = 0;
            while (resultSet.next()){
                String step = resultSet.getString("stepData");
                totalStep = Integer.parseInt(step) + totalStep;
                Log.d("DB_tag1",step);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(connection != null){
                try{
                    connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return totalStep;
    }

    /**
     * 模糊查询每小时步数 ———每日
     * */
    public static List ReadStepHour(String time,String name){
        Connection connection = null;
        List stepList = new ArrayList();
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
            String exactTime = time + "%";
            String tableName = "t" + name;
            String sql = "SELECT stepData FROM "+tableName+" WHERE time LIKE '"+exactTime+"' AND state = '0'";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()){
                String step = resultSet.getString("stepData");
                Log.d("DB_tag",String.valueOf(step));
                stepList.add(step);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(connection != null){
                try{
                    connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return stepList;
    }

    /**
     * 模糊查询每日步数 —— 每月
     * */
    public static List ReadStepDaily(String time,String name){
        Connection connection = null;
        List stepList = new ArrayList();
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
            String exactTime = time + "%";
            String tableName = "t" + name;
            String sql = "SELECT stepData FROM "+tableName+" WHERE time LIKE '"+exactTime+"' AND state = '1'";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()){
                String step = resultSet.getString("stepData");
                Log.d("DB_tag",String.valueOf(step));
                stepList.add(step);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(connection != null){
                try{
                    connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return stepList;
    }

    /**
     * 模糊查询每日步数 —— 每周
     * */
//    public static String ReadStepWeekly(String time,String name){
//        Connection connection = null;
//        String weekStep = null;
//        try{
//            connection = DriverManager.getConnection(url,userName,password);
//            Log.d("DB_tag","连接数据库成功！！！");
//            String exactTime = time + "%";
//            String tableName = "t" + name;
//            String sql = "SELECT stepData FROM "+tableName+" WHERE time LIKE '"+exactTime+"' AND state = '1'";
//            PreparedStatement ps = connection.prepareStatement(sql);
//            ResultSet resultSet = ps.executeQuery();
//            while (resultSet.next()){
//                String step = resultSet.getString("stepData");
//                Log.d("DB_tag",String.valueOf(step));
//                    weekStep = step;
//            }
//        }catch (Exception e){
//            weekStep = "0";
//            e.printStackTrace();
//        }finally {
//            if(connection != null){
//                try{
//                    connection.close();
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        }
//        return weekStep;
//    }

    public static List ReadStepWeekly(String startDay,String sqlTime,String name){
        Connection connection = null;
        List stepList = new ArrayList();
        List dayList = new ArrayList();
        List list = new ArrayList();
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
            String tableName = "t" + name;
            //String sql = "SELECT stepData FROM "+tableName+" WHERE time LIKE '"+exactTime+"' AND state = '1'";
            String sql = "SELECT stepData,time FROM "+tableName+" WHERE "+sqlTime+"AND state = '1'";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()){
                String step = resultSet.getString("stepData");
                String day = resultSet.getString("time");
                Log.d("DB_tag","week" + String.valueOf(step));
                Log.d("DB_tag","week" + String.valueOf(day));
                dayList.add(day);
                stepList.add(step);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(connection != null){
                try{
                    connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        int k = 7 - stepList.size();
        Log.d("DB_tag","startDay" + startDay);
        Log.d("DB_tag",String.valueOf(k));
        Log.d("DB_tag",String.valueOf(dayList.get(0)));
        if(!dayList.get(0).equals(startDay) && k != 0){
            for(int i = 0; i < k; i++){
                list.add("0");
            }
            for(int i = k; i < 7; i++){
                list.add(stepList.get(i));
            }
            Log.d("DB_tag","week list");
            return list;
        }else{
            Log.d("DB_tag","week stepList");
            return stepList;
        }
    }

    public static void UpdateData(String psd,String num){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
            String sql = "UPDATE userInfo SET password = '"+psd+"' WHERE phoneNumber = '"+num+"'";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.execute(sql);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(connection != null){
                try{
                    connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * **********************************************************************
    * */

    public static void AddRequestList(String userA,String userB){
        Connection connection = null;
        String sql;
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
            sql = "INSERT INTO userRequestList(userA,userB,state) VALUES ('"+userA+"','"+userB+"','0')";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.execute(sql);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(connection != null){
                try{
                    connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public static String ReadRequestList0(String userB){
        Connection connection = null;
        String userA = null;
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
            String sql = "SELECT userA FROM userRequestList WHERE userB = '"+userB+"' AND state = '0'";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()){
                userA = resultSet.getString("userA");
                Log.d("DB_tag",String.valueOf(userA));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(connection != null){
                try{
                    connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return userA;
    }

    public static List<String> ReadRequestListYes(String user){
        Connection connection = null;
        List<String> list = new ArrayList<>();
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
            String sql = "SELECT userA,userB FROM userRequestList WHERE userA = '"+user+"' OR userB = '"+user+"'";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()){
                String userA = resultSet.getString("userA");
                String userB = resultSet.getString("userB");
                Log.d("DB_tag",userA + " " + userB);
                if(userA.equals(user)){
                    list.add(userB);
                }else if(userB.equals(user)){
                    list.add(userA);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(connection != null){
                try{
                    connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    public static String ReadRequestListNo(String userA){
        Connection connection = null;
        String userB = null;
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
            String sql = "SELECT userB FROM userRequestList WHERE userA = '"+userA+"' AND state = '2'";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()){
                userB = resultSet.getString("userB");
                Log.d("DB_tag",String.valueOf(userA));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(connection != null){
                try{
                    connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return userB;
    }

    public static void UpdateRequestState(String state,String userA,String userB){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
            String sql = "UPDATE userRequestList SET state= '"+state+"' WHERE userA = '"+userA+"' AND userB = '"+userB+"'";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.execute(sql);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(connection != null){
                try{
                    connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }






    public static void DeleteStep(){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(url,userName,password);
            //String sql = "ALTER TABLE userInfo CHANGE userId userId int AUTO_INCREMENT";
            String sql = "DELETE FROM userRequestList WHERE userA = 'miaobeibei'";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.execute(sql);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(connection != null){
                try{
                    connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public static void Table() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, userName, password);
            Log.d("DB_tag", "连接数据库成功！！！");
            //time datetime PRIMARY KEY,
            String sql = "CREATE TABLE t(id int AUTO_INCREMENT PRIMARY KEY,time datetime NOT NULL,stepData varchar(50) NOT NULL,state varchar(5) NOT NULL)";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void InsertStep(String date){
        Connection connection = null;
        String sql;
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
            String dateInsert = "2020-04-" + date + " 00:00:00";
            if(Integer.parseInt(date)%3 == 0){
                sql = "INSERT INTO tlindidi(time,stepData,state) VALUES ('"+dateInsert+"','50','1')";
            }else if(Integer.parseInt(date)%3 == 1){
                sql = "INSERT INTO tlindidi(time,stepData,state) VALUES ('"+dateInsert+"','100','1')";
            }else{
                sql = "INSERT INTO tlindidi(time,stepData,state) VALUES ('"+dateInsert+"','80','1')";
            }
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.execute(sql);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(connection != null){
                try{
                    connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public static void Drop(){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
            String sql = "DROP TABLE t";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.execute(sql);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(connection != null){
                try{
                    connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }


    public static void Delete(){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
            //String sql = "ALTER TABLE userInfo CHANGE userId userId int AUTO_INCREMENT";
            String sql = "DELETE FROM tlindidi WHERE stepData = '3' OR stepData = '4' OR stepData = 'null'";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.execute(sql);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(connection != null){
                try{
                    connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }


}
