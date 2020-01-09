package com.bittech.client.service;

import com.bittech.util.CommUtil;
import com.bittech.vo.MessageVO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: BaiMiao
 * @Date: 2020/1/7 19:06
 * @Description:
 */
public class FriendList {
    private JPanel friendListPanel;
    private JScrollPane friendPanel;
    private JButton createGroupBtn;
    private JScrollPane groupPanel;

    private String myName;
    private Connect2Server connect2Server;
    private Set<String> names;

    //缓存所有的私聊界面
    private Map<String, PrivateChat> privateChatMap = new ConcurrentHashMap<>();
    //缓存当前客户端的群聊信息
    private Map<String, Set<String>> groupInfo = new ConcurrentHashMap<>();

    //缓存当前客户端群聊界面
    private Map<String,groupChatGUI> groupChatGUIMap1=new ConcurrentHashMap<>();
    private class DaemonTask implements Runnable {
        private Scanner scanner = new Scanner(connect2Server.getIn());

        @Override
        public void run() {
            while (true) {
                if (scanner.hasNextLine()) {
                    String strFromServer = scanner.nextLine();
                    if (strFromServer.startsWith("newLogin:")) {
                        //好友上线提醒
                        String newFriend = strFromServer.split(":")[1];
                        JOptionPane.showMessageDialog(null,
                                newFriend + "上线了", "上线提醒",
                                JOptionPane.INFORMATION_MESSAGE);
                        names.add(newFriend);
                        //再次刷新好友列表
                        reloadFriendList();
                    }
                    if (strFromServer.startsWith("{")) {
                        //此时是个json串
                        MessageVO messageVOFromClient = (MessageVO) CommUtil.json2Object(strFromServer,
                                MessageVO.class);
                        if (messageVOFromClient.getType().equals(2)) {
                            //私聊信息
                            String senderName = messageVOFromClient.getContent()
                                    .split("-")[0];
                            String msg = messageVOFromClient.getContent().split("-")[1];
                            if (privateChatMap.containsKey(senderName)) {
                                PrivateChat privateChat = privateChatMap.get(senderName);
                                privateChat.getFrame().setVisible(true);
                                privateChat.ReadFromServer(senderName + "说:" + msg);
                            } else {
                                PrivateChat privateChat = new PrivateChat(senderName,
                                        myName, connect2Server);
                                privateChatMap.put(senderName, privateChat);
                                privateChat.ReadFromServer(senderName + "说:" + msg);
                            }

                        }
                        else if (messageVOFromClient.getType().equals(4)){
                            //群聊信息
                            String senderName=messageVOFromClient.getContent().split("-")[0];
                            String groupMsg=messageVOFromClient.getContent().split("-")[1];
                            String groupName=messageVOFromClient.getTo().split("-")[0];
                            if (groupInfo.containsKey(groupName)){
                                if (groupChatGUIMap1.containsKey(groupName)){
                                    groupChatGUI groupChatGUI1=groupChatGUIMap1.get(groupName);
                                    groupChatGUI1.getFrame().setVisible(true);
                                    groupChatGUI1.readFromServer(senderName+"说:"+groupMsg);
                                }else{
                                    Set<String> friends=groupInfo.get(groupName);
                                    groupChatGUI groupChatGUI1=new groupChatGUI(groupName,friends,
                                            connect2Server,myName);
                                    groupChatGUIMap1.put(groupName,groupChatGUI1);
                                    groupChatGUI1.readFromServer(senderName+"说:"+groupMsg);
                                }
                            }else{
                                //1.将群信息，群成员添加到本客户端群聊列表
                                Set<String> friends=(Set<String>)CommUtil.json2Object(messageVOFromClient.getTo(),
                                        Set.class);
                                addGroupInfo(groupName,friends);
                                reloadGroupList();
                                //2.唤起群聊界面
                                groupChatGUI groupChatGUI1=new groupChatGUI(groupName,friends,connect2Server,
                                        myName);
                                groupChatGUIMap1.put(groupName,groupChatGUI1);
                                groupChatGUI1.readFromServer(senderName+"说:"+groupMsg);

                            }
                        }
                    }
                }
            }

        }
    }

    private class PrivateLabelAction implements MouseListener {
        private String labelName;

        public PrivateLabelAction(String labelName) {
            this.labelName = labelName;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            //判断缓存中是否有指定的私聊界面
            if (privateChatMap.containsKey(labelName)) {
                PrivateChat privateChat = privateChatMap.get(labelName);
                privateChat.getFrame().setVisible(true);
            } else {
                PrivateChat privateChat = new PrivateChat(labelName, myName, connect2Server);
                privateChatMap.put(labelName, privateChat);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    private class GroupLabelAction implements MouseListener{
        private String groupName;

        public GroupLabelAction(String groupName) {
            this.groupName = groupName;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (groupChatGUIMap1.containsKey(groupName)){
                groupChatGUI groupChatGUI1=groupChatGUIMap1.get(groupName);
                groupChatGUI1.getFrame().setVisible(true);
            }else {
                Set<String> friends =groupInfo.get(groupName);
                groupChatGUI groupChatGUI1=new groupChatGUI(groupName,friends,
                        connect2Server,myName);
                groupChatGUIMap1.put(groupName,groupChatGUI1);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    public FriendList(String myName, Connect2Server connect2Server,
                      Set<String> names) {
        this.myName = myName;
        this.connect2Server = connect2Server;
        this.names = names;

        JFrame frame = new JFrame("myName");
        frame.setContentPane(friendListPanel);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
        reloadFriendList();

        //新启动一个后台线程不断监听服务端发来的信息
        Thread daemonThread = new Thread(new DaemonTask());
        daemonThread.setDaemon(true);
        daemonThread.start();
        //点击创建群组弹出创建界面
        createGroupBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CreateGroupGUI(names,myName,connect2Server,FriendList.this);
            }
        });
    }

    public void reloadFriendList() {
        JPanel friendLabelPanel = new JPanel();
        JLabel[] labels = new JLabel[names.size()];
        //迭代遍历set集合
        Iterator<String> iterator = names.iterator();
        //设置标签为纵向对齐
        friendLabelPanel.setLayout(new BoxLayout(friendLabelPanel,
                BoxLayout.Y_AXIS));
        int i = 0;
        while (iterator.hasNext()) {
            String labelName = iterator.next();
            labels[i] = new JLabel(labelName);
            //给每个标签附加按钮点击事件
            labels[i].addMouseListener(new GroupLabelAction(labelName));
            friendLabelPanel.add(labels[i]);
            i++;
        }
        this.friendPanel.setViewportView(friendLabelPanel);
        //设置滚动条为垂直滚动条
        this.friendPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.friendPanel.revalidate();
    }

    //刷新群聊列表的群聊信息

    public void reloadGroupList() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        Set<String> groupNames = groupInfo.keySet();
        Iterator<String> iterator = groupNames.iterator();
        while (iterator.hasNext()) {
            String groupName = iterator.next();
            JLabel label = new JLabel(groupName);
            label.addMouseListener(new GroupLabelAction(groupName));
            jPanel.add(label);
        }
        groupPanel.setViewportView(jPanel);
        groupPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        groupPanel.revalidate();
    }
    public void addGroupInfo(String groupName,Set<String> friends){
        groupInfo.put(groupName,friends);
    }
}
