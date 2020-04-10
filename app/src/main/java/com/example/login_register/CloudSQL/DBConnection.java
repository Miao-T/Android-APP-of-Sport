package com.example.login_register.CloudSQL;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DBConnection {
    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String url = "jdbc:mysql://cdb-ksj8ugkk.bj.tencentcdb.com:10250/user";
    private static final String userName = "root";
    private static final String password = "DidideMiao531";

    public static void linkCreateTable(){
        Connection connection = null;
        try{
            Class.forName(driver).newInstance();
            Log.d("DB_tag","驱动加载成功！！！");
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
            String sql = "CREATE TABLE DailyStep(id int(6) AUTO_INCREMENT PRIMARY KEY,name varchar(10))";
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

    public static void linkInsert(int Id,String Name){
        Connection connection = null;
        try{
            Class.forName(driver).newInstance();
            Log.d("DB_tag","驱动加载成功！！！");
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
//            String sql = "INSERT INTO userInfo(userId,userName) VALUES ('31116202','zgd')";
            String sql = "INSERT INTO userInfo(userId,userName) VALUES ('"+Id+"','"+Name+"')";
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

    public static void linkDelete(String Name){
        Connection connection = null;
        try{
            Class.forName(driver).newInstance();
            Log.d("DB_tag","驱动加载成功！！！");
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
            String sql = "DELETE FROM userInfo WHERE userName = '"+Name+"'";
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

    public static void linkRead(){
        Connection connection = null;
        try{
            Class.forName(driver).newInstance();
            Log.d("DB_tag","驱动加载成功！！！");
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
            String sql = "select * from userInfo";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()){
                String Id = resultSet.getString("userId");
                String Name = resultSet.getString("userName");
                Log.d("DB_tag",Id + " " + Name);
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
    }
}
