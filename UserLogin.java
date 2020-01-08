package com.bittech.client.service;

import com.bittech.client.dao.AccountDao;
import com.bittech.client.entity.User;
import com.bittech.util.CommUtil;
import com.bittech.vo.MessageVO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Scanner;
import java.util.Set;

/**
 * @Author: BaiMiao
 * @Date: 2020/1/7 11:59
 * @Description:
 */
public class UserLogin {
    private JPanel loginPanel;
    private JPanel labelPanel;
    private JPanel userNamePanel;
    private JTextField userNameText;
    private JPanel BtnPanel;
    private JButton regBtn;
    private JButton loginBtn;
    private JPanel password1Jpanel;
    private JLabel password;
    private JPasswordField passwordText;

    private AccountDao accountDao=new AccountDao();

    public UserLogin() {

        JFrame frame = new JFrame("用户登录");
        frame.setContentPane(loginPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        //点击注册按钮，弹出注册页面
        regBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              new UserReg();
            }
        });
        //点击登录按钮，验证用户的输入信息是否正确
        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //获取用户输入
                String userName=userNameText.getText();
                String password=String.valueOf(
                        passwordText.getPassword());
                User user=accountDao.userLogin(userName,password);
                if (user!=null){
                    //登录成功
                    JOptionPane.showMessageDialog(null,"登录成功",
                            "提示信息",JOptionPane.INFORMATION_MESSAGE);

                    //与服务器建立连接,将用户名注册到服务端缓存
                    Connect2Server connect2Server=new Connect2Server();

                    MessageVO messageVO=new MessageVO();
                    messageVO.setType(1);
                    messageVO.setContent(userName);
                    String msgJson=CommUtil.object2Json(messageVO);
                    try {
                        //发送信息
                        PrintStream out=new PrintStream(connect2Server.getOut(),
                                true,"UTF-8");
                        out.println(msgJson);
                        //读取服务端发回的响应，加载用户列表,读取到所有的在线好友信息
                        Scanner in=new Scanner(connect2Server.getIn());
                        if (in.hasNextLine()){
                            String jsonStr=in.nextLine();
                            MessageVO msgFromServer=(MessageVO)CommUtil.json2Object(jsonStr,
                                    MessageVO.class);
                            Set<String> names=(Set<String>) CommUtil.json2Object(msgFromServer.getContent(),
                                    Set.class);
                            System.out.println("在线好友为:"+names);
                            //加载好友列表界面，将登陆界面不可见
                            frame.setVisible(false);
                            //跳转到好友列表界面需要传递用户名，与服务器建立的连接,所有在线的好友信息
                            new FriendList(userName,connect2Server,names);
                        }

                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }
                    //客户端
                    //1.与服务器建立连接，将自己的用户名与Socket保存到服务端缓存
                    //2.读取服务端的所有在线好友信息
                    //3.新建一个后台线程不断读取服务器发来的信息
                }else {
                    JOptionPane.showMessageDialog(null,"登录失败",
                            "错误信息",JOptionPane.ERROR_MESSAGE);
                    //保留当前登录界面
                }
            }
        });
    }

    public static void main(String[] args) {
        new UserLogin();
    }
}
