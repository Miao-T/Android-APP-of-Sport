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

    public static void DriverConnection(){
        try{
            Class.forName(driver).newInstance();
            Log.d("DB_tag","驱动加载成功！！！");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void CreateTable(String tableName){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
            String sql = "CREATE TABLE "+tableName+"(time varchar(50) PRIMARY KEY,stepData varchar(50) NOT NULL)";
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

    public static void DropTable(){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
            String sql = "DROP TABLE didi";
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

    public static void InsertData(int id,String name){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
//            String sql = "INSERT INTO userInfo(userId,userName) VALUES ('31116202','zgd')";
            String sql = "INSERT INTO demo(id,name) VALUES ('"+id+"','"+name+"')";
            //String sql = "INSERT INTO userInfo(userId,userName) VALUES ('"+Id+"','"+Name+"')";
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

    public static void DeleteData(String name){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
            String sql = "DELETE FROM demo WHERE name = '"+name+"'";
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

    public static void UpdateData(String name,int id){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
            String sql = "UPDATE demo SET id = '"+id+"' WHERE name = '"+name+"'";
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
    public EnumMap<UserInfoData, Object> ReadCloudData(String name) {
        Connection connection = null;
        EnumMap<UserInfoData,Object> retMap = new EnumMap<UserInfoData, Object>(UserInfoData.class);
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
            String sql = "SELECT id,t FROM demo WHERE name = '"+name+"'";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()){
                String id = resultSet.getString("id");
                Timestamp RegisterTime = resultSet.getTimestamp("t");
                retMap.put(UserInfoData.id,id);
                retMap.put(UserInfoData.registerTime,RegisterTime);
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


    public static void ReadData(String dataName, String name){
        Connection connection = null;
        String result;
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
            String sql = "SELECT "+dataName+" FROM demo WHERE name = '"+name+"'";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()){
                result = resultSet.getString(""+dataName+"");
                //String Name = resultSet.getString("userName");
                Log.d("DB_tag",result);
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



    public static boolean VerifyData(String Name){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(url,userName,password);
            Log.d("DB_tag","连接数据库成功！！！");
            String sql = "SELECT id,t FROM demo WHERE name = '"+Name+"'";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()){
                String Id = resultSet.getString("id");
                Timestamp RegisterTime = resultSet.getTimestamp("t");
                //String Name = resultSet.getString("userName");
                Log.d("DB_tag",Id + " " + RegisterTime);
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
        return true;
    }
}
