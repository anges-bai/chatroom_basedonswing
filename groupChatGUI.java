package com.bittech.client.service;

import com.bittech.util.CommUtil;
import com.bittech.vo.MessageVO;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author: BaiMiao
 * @Date: 2020/1/9 20:58
 * @Description:
 */
public class groupChatGUI {
    private JPanel groupChatPanel;
    private JTextArea readFromServer;
    private JTextField send2Server;
    private JPanel friendPanel;

    private String groupName;
    private JFrame frame;
    private Set<String> friends;
    private Connect2Server connect2Server;
    private String myName;

   public groupChatGUI(String groupName,Set<String> friends,
                       Connect2Server connect2Server,String myName){
       this.groupName=groupName;
       this.friends=friends;
       this.connect2Server=connect2Server;
       this.myName=myName;
        frame= new JFrame(groupName);
       frame.setContentPane(groupChatPanel);
       frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
       frame.setSize(400,400);
       frame.setLocationRelativeTo(null);
       frame.setVisible(true);
       //1.好友列表的展示
       friendPanel.setLayout(new BoxLayout(friendPanel,BoxLayout.Y_AXIS));
       Iterator<String> iterator=friends.iterator();
       while (iterator.hasNext()){
           String friendName=iterator.next();
           JLabel label=new JLabel(friendName);
           friendPanel.add(label);
       }
       //2.文本框发送事件
       send2Server.addKeyListener(new KeyAdapter() {
           @Override
           public void keyPressed(KeyEvent e) {
               StringBuilder sb=new StringBuilder();
               sb.append(send2Server.getText());
               //输入回车
               if (e.getKeyCode()==KeyEvent.VK_ENTER){
                   //type:4
                   //content:senderName-msg
                   //to:groupName
                   String str=sb.toString();
                   MessageVO messageVO=new MessageVO();
                   messageVO.setType(4);
                   messageVO.setContent(myName+"-"+str);
                   messageVO.setTo(groupName);
                   try {
                       PrintStream out=new PrintStream(connect2Server.getOut(),
                               true,"UTF-8");
                       out.println(CommUtil.object2Json(messageVO));
                       readFromServer.append(myName+"说:"+str+"\n");
                
                       send2Server.setText("");
                   } catch (UnsupportedEncodingException e1) {
                       e1.printStackTrace();
                   }
               }
           }
       });
   }
   public void readFromServer(String msg){
       readFromServer.append(msg+"\n");
   }
   public JFrame getFrame(){
       return frame;
   }
}
