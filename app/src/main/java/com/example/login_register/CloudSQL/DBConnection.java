package com.example.login_register.CloudSQL;

import android.util.Log;

import androidx.annotation.RequiresPermission;

import com.example.login_register.R;
import com.example.login_register.Utils.ReadData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumMap;

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
     * 注册后自动建表，表明为用户名
    * */
    public static void CreateTable(String name){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
            //time datetime PRIMARY KEY,
            String tableName = "t" + name;
            String sql = "CREATE TABLE "+tableName+"(id int AUTO_INCREMENT PRIMARY KEY,time datetime NOT NULL,stepData int(50) NOT NULL)";
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

    public static void Drop(String name){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
            String TableName = name;
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

    public static void InsertData(){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
//            String sql = "INSERT INTO userInfo(userId,userName) VALUES ('31116202','zgd')";
            String sql = "INSERT INTO t(time,stepData) VALUES ('2020-04-13 13:13:00','50')";
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
     * 注册账号
    * */
    public static void RegisterAccount(String name,String phoneNum,String email,String psd,String registerDate){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
//            String sql = "INSERT INTO userInfo(userId,userName) VALUES ('31116202','zgd')";
            //String sql = "INSERT INTO demo(id,name) VALUES ('"+id+"','"+name+"')";
            String sql = "INSERT INTO userInfo(userName,phoneNumber,email,password,registerDate) " +
                    "VALUES ('"+name+"','"+phoneNum+"','"+email+"','"+psd+"','"+registerDate+"')";
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

    public static void Delete(String name){
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

    public static void UpdateData(){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
            String sql = "UPDATE userInfo SET birthday = '2020-5-11' WHERE userName = 'miaobeibei'";
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

    public static boolean CheckNameRegistered(String name){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
            String sql = "SELECT password FROM userInfo WHERE name = '"+name+"'";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()){
                String psd = resultSet.getString("password");
                //String Name = resultSet.getString("userName");
                Log.d("DB_tag",psd);
                if(psd.equals("null")){
                    result = true;
                    //无重名
                }else{
                    result = false;
                    //已有
                    break;
                }
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
        return result;
    }

}
