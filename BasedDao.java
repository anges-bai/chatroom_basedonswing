package com.bittech.client.dao;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.bittech.util.CommUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @Author: BaiMiao
 * @Date: 2020/1/7 10:38
 * @Description:dao层基础类，1,2,4公有操作
 * BasedDao存放：1.加载数据源 2.获取连接 4.关闭资源
 */
public class BasedDao {
    private static  DruidDataSource DATA_SOURCE;
    //1.加载数据源
    static {
        Properties pros=CommUtil.loadProperties("db.properties");
        try {
            DATA_SOURCE=(DruidDataSource) DruidDataSourceFactory
                    .createDataSource(pros);
        } catch (Exception e) {
            System.out.println("数据源加载失败");
            e.printStackTrace();
        }
    }
    //2.获取连接
    protected Connection getConnection(){
        try {
           return (Connection) DATA_SOURCE.getPooledConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    //4.关闭资源
    protected void closeResources(Connection conn, Statement statement){
        if (conn!=null){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement!=null){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    protected void closeResources(Connection conn, Statement statement,
                                  ResultSet resultSet){
        closeResources(conn,statement);
        if (resultSet!=null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
