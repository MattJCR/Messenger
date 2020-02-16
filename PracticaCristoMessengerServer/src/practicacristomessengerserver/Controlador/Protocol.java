/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practicacristomessengerserver.Controlador;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import practicacristomessengerserver.MainWindow;
import practicacristomessengerserver.Modelo.Message;
import practicacristomessengerserver.Modelo.User;

/**
 *
 * @author Matt Workstation
 */
public class Protocol {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String dateTime = "";
    private final String protocolVersion = "PROTOCOLCRISTOMESSENGER1.0";
    private String username = "";
    private String password = "";
    private ArrayList<String> protocolList;
    private String msgUser = "";
    private String msgFriend = "";
    ArrayList<String> msgListCache = new ArrayList<String>();
    UsersController myUserController;
    public ArrayList<String> start(String block){
        this.protocolList = readBlock(block.toCharArray());
        ArrayList<String> response = new ArrayList<String>();
        
        for (String p :protocolList) {
            System.out.println(p);
        }
        System.out.println("protocolList SIZE: " + protocolList.size());
        
        if ("CLIENT".equals(protocolList.get(2)) && "LOGIN".equals(protocolList.get(3)) && protocolList.size() == 6) {
            MainWindow.ConsoleDebug("PROTOCOL CHECK LOG.");
            System.out.println("PROTOCOL CHECK LOG.");
            response = checkLog();
        }else if ("CLIENT".equals(protocolList.get(2)) && "MSGS".equals(protocolList.get(3)) && protocolList.size() == 7) {
            MainWindow.ConsoleDebug("PROTOCOL GET MESSAGES");
            System.out.println("PROTOCOL GET MESSAGES");
            msgListCache = getMenssages(protocolList.get(4),protocolList.get(5),protocolList.get(6));
            response.add(msgListCache.get(0));
            msgUser = protocolList.get(4);
            msgFriend = protocolList.get(5);
        }else if ("CLIENT".equals(protocolList.get(2)) && "MSGS".equals(protocolList.get(3)) && 
                "OK_SEND!".equals(protocolList.get(4)) && protocolList.size() == 5 && !"".equals(msgUser) && !"".equals(msgFriend)) {
            MainWindow.ConsoleDebug("PROTOCOL OK_SEND");
            System.out.println("PROTOCOL OK_SEND");
            msgListCache.remove(0);
            response = msgListCache;
        }else if ("CLIENT".equals(protocolList.get(2)) && "MSGS".equals(protocolList.get(3)) && "ALL_RECEIVED".equals(protocolList.get(4)) && protocolList.size() == 5) {
            MainWindow.ConsoleDebug("PROTOCOL ALL_RECEIVED");
            System.out.println("PROTOCOL ALL_RECEIVED");
            response.clear();
            msgListCache.clear();
            msgUser = "";
            msgFriend = "";
        }else if ("CLIENT".equals(protocolList.get(2)) && "ALLDATA_USER".equals(protocolList.get(3)) && protocolList.size() == 5) {
            MainWindow.ConsoleDebug("PROTOCOL ALLDATA_USER");
            System.out.println("PROTOCOL ALLDATA_USER");
            response = allDataUser(protocolList.get(4));
        }else if ("CLIENT".equals(protocolList.get(2)) && "STATUS".equals(protocolList.get(3)) && protocolList.size() == 6) {
            MainWindow.ConsoleDebug("PROTOCOL STATUS");
            System.out.println("PROTOCOL STATUS");
            response = getUserStatus(protocolList.get(4),protocolList.get(5));
        }else{
            MainWindow.ConsoleDebug("PROTOCOL BAD_PKG");
            System.out.println("PROTOCOL BAD_PKG");
            dateTime = sdf.format(new Timestamp(System.currentTimeMillis()));
            response.add(protocolList + "#" + dateTime + "#SERVER#BAD_PKG");
        }
        return response;
    }

    public ArrayList<String> readBlock(char[] block){
        ArrayList<String> blockList = new ArrayList();
        String acumulador = "";
        for (char c : block) {
            if (c != '#') {
                acumulador += c;
            }else{
                blockList.add(acumulador);
                acumulador = "";
            }
        }
        blockList.add(acumulador);
        return blockList;
    }
    private ArrayList<String> getMenssages (String user, String friend, String date){
        if (this.myUserController == null) {
            this.myUserController = new UsersController();
        }
        int msgHistorySize = 0;
        dateTime = sdf.format(new Timestamp(System.currentTimeMillis()));
        ArrayList<String> msgList = new ArrayList<String>();
        ArrayList<Message> messageList = this.myUserController.loadMessages(user, friend, date);
        msgHistorySize = this.myUserController.sizeMessages(user, friend, date);
        msgList.add(protocolVersion + "#" + dateTime + "#SERVER#" + "MSGS#" + 
                user + "#" + friend + "#" + msgHistorySize + "#" + (messageList.size()));
        String header = protocolVersion + "#" + dateTime + "#SERVER#MSGS#";
        for (Message msg : messageList) {
            msgList.add(header + msg.getTransmitter() + "#" + msg.getReceiver() + "#" + msg.getDate() + "#" + msg.getText());
        }
        return msgList;
    }
    private String getFriends(){
        String acumulador = "";
        this.myUserController = new UsersController();
        System.out.println("USERNAME: " + username);
        ArrayList<User> usersList = myUserController.loadFriends(username);
        System.out.println("USERLIST SIZE: "+ usersList.size());
        acumulador += "#FRIENDS#" + usersList.size();
        for (User u : usersList) {
            //acumulador += "#" + u.getName();
            acumulador += "#" + u.getId();
            if (u.getState()) {
                acumulador += "#CONNECTED";
            }else{
                acumulador += "#NOT_CONNECTED";
            }
        }
        return acumulador;
    }
    private ArrayList<String> checkLog(){
        ArrayList<String> result = new ArrayList<String>();
        this.myUserController = new UsersController();
        this.username = protocolList.get(4);
        this.password = protocolList.get(5);
        dateTime = sdf.format(new Timestamp(System.currentTimeMillis()));
        MainWindow.ConsoleDebug("Version: " + protocolList.get(0));
        System.out.println("Version: " + protocolList.get(0));
        MainWindow.ConsoleDebug("User: " + username);
        System.out.println("User: " + username);
        MainWindow.ConsoleDebug("Pass: " + password);
        System.out.println("Pass: " + password);
        if (myUserController.loginUser(username, password)) {
            result.add(this.protocolVersion + "#" + dateTime + "#SERVER#LOGIN_CORRECT#" + username + getFriends());
        }else{
            result.add(this.protocolVersion + "#" + dateTime + "#SERVER#ERROR#BAD_LOGIN");
        }
        MainWindow.ConsoleDebug(result.get(0));
        System.out.println(result);
        return result;
    }
    private ArrayList<String> allDataUser(String friend){
        User user = new User();
        if (this.myUserController == null) {
            this.myUserController = new UsersController();
        }
        ArrayList<String> result = new ArrayList<String>();
        System.out.println("Check friendship...");
        if (this.myUserController.isFriend(username, friend)) {
            user = this.myUserController.getInfoUser(friend);
            System.out.println("True");
        }else{
            System.out.println("False");
        }
        dateTime = sdf.format(new Timestamp(System.currentTimeMillis()));
        result.add(protocolVersion + "#" + dateTime + "#SERVER#" + "ALLDATA_USER#" + 
                user.getId() + "#" + user.getName() + "#" + user.getFirstSurname() + "#" + user.getSecondSurname());
        return result;
    }
    private ArrayList<String> getUserStatus(String idUser, String idFriend){
        ArrayList<String> result = new ArrayList<String>();
        String status = "NOT_CONNECTED";
        if (this.myUserController == null) {
            this.myUserController = new UsersController();
        }
        if (this.myUserController.getStatusIfIsFriend(idUser, idFriend)) {
            status = "CONNECTED";
        }
        dateTime = sdf.format(new Timestamp(System.currentTimeMillis()));
        result.add(protocolVersion + "#" + dateTime + "#SERVER#" + 
                    "STATUS#" + idFriend + "#" + status);
        return result;
    }
}
