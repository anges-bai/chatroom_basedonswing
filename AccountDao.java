package com.bittech.client.dao;

import com.bittech.client.entity.User;
import org.apache.commons.codec.digest.DigestUtils;

import java.sql.*;

/**
 * @Author: BaiMiao
 * @Date: 2020/1/7 11:00
 * @Description:
 */
public class AccountDao extends BasedDao {
    public boolean userReg(User user){
        //insert
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        try{
            connection=getConnection();
            String sql="INSERT INTO user(username,password,brief)"+
                    "VALUES (?,?,?)";
            preparedStatement=connection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1,user.getUsername());
            preparedStatement.setString(2,DigestUtils.md5Hex(user.getPassword()));
            preparedStatement.setString(3,user.getBrief());
            int rows=preparedStatement.executeUpdate();
            if (rows==1)
                return true;
        }catch(SQLException e){
            System.out.println("用户注册失败");
        }finally {
            closeResources(connection,preparedStatement);
        }
        return false;
    }

    public User userLogin(String userName,String password){
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;
        try {
            connection=getConnection();
            String sql="select * from user where username=? and password=?";
            preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1,userName);
            preparedStatement.setString(2,DigestUtils.md5Hex(password));
            resultSet=preparedStatement.executeQuery();
            if (resultSet.next()){
                User user=new User();
                user.setId(resultSet.getInt("id"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setBrief(resultSet.getString("brief"));
                return user;
            }
        }catch (SQLException e){
            System.out.println("用户登录失败");
        }finally {
            closeResources(connection,preparedStatement,resultSet);
        }
        return null;
    }
}
