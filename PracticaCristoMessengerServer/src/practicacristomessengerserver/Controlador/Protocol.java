/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practicacristomessengerserver.Controlador;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    public String username = "";
    private ArrayList<String> protocolList;
    private String msgUser = "";
    private String msgFriend = "";
    ArrayList<String> msgListCache = new ArrayList<String>();
    UsersController myUserController;
    private ArrayList<String> ArrayMessages = null;
    public Protocol(ArrayList<String> messageList) {
        this.ArrayMessages = messageList;
    }
    
    public ArrayList<String> start(String block, String user){
        this.username = user;
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
        }else if ("CLIENT".equals(protocolList.get(2)) && "CHAT".equals(protocolList.get(3)) && protocolList.size() == 7) {
            MainWindow.ConsoleDebug("PROTOCOL CHAT");
            System.out.println("PROTOCOL CHAT");
            response = sendMessage();
        }else if ("CLIENT".equals(protocolList.get(2)) && "GET_PHOTO".equals(protocolList.get(3)) && protocolList.size() == 5) {
            MainWindow.ConsoleDebug("PROTOCOL GET_PHOTO");
            System.out.println("PROTOCOL GET_PHOTO");
            response = sendPhoto();
            System.out.println("RESPONSE SIZE: " + response.size());
        }else{
            MainWindow.ConsoleDebug("PROTOCOL BAD_PKG");
            System.out.println("PROTOCOL BAD_PKG");
            dateTime = sdf.format(new Timestamp(System.currentTimeMillis()));
            response.add(protocolList + "#" + dateTime + "#SERVER#BAD_PKG");
        }
        return response;
    }
    private ArrayList<String> sendPhoto(){
        if (this.myUserController == null) {
            this.myUserController = new UsersController();
        }
        dateTime = sdf.format(new Timestamp(System.currentTimeMillis()));
        ArrayList<String> arrayLines = new ArrayList<String>();
        ArrayList<String> encodeLines = new ArrayList<String>();
        String line = "";
        int cont = 0, totalPkg = 0;
        //Lectura de fichero a ArrayList de String len 512chars
        try(FileInputStream input=new FileInputStream(".\\data\\" + protocolList.get(4) + "\\" + protocolList.get(4) + ".jpg")){
            int valor = input.read();
            while(valor!=-1){
                if (cont > 511) {
                    arrayLines.add(line);
                    line = "";
                    cont = 0;
                }
                line += (char)valor;
                valor=input.read();
                cont++;
                totalPkg++;
            }
            if (cont > 0) {
                arrayLines.add(line);
                cont = 0;
            }
            System.out.println("SIZE OF PICTURE: " + totalPkg);
        } catch (FileNotFoundException ex) {
            System.out.println("No existe una foto. Cargando foto por defecto...");
            MainWindow.ConsoleDebug("No existe una foto. Cargando foto por defecto...");
            //arrayLines = defaultPhoto();
        } catch (IOException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, null, ex);
        }
        encodeLines.add(protocolVersion + "#" + dateTime + "#SERVER#" + "STARTING_MULTIMEDIA_TRANSMISSION_TO#" + username);
        for (String s : arrayLines) {
            encodeLines.add(constructPhoto(Base64.getEncoder().encodeToString(s.getBytes()),totalPkg));
        }
        System.out.println("EncodeLines SIZE: " + encodeLines) ;
        //PROTOCOLCRISTOMESSENGER1.0#FECHA/HORA#SERVER#ENDING_MULTIMEDIA_TRANSMISSION#<LOGIN_CLIENTE>
        encodeLines.add(protocolVersion + "#" + dateTime + "#SERVER#ENDING_MULTIMEDIA_TRANSMISSION#" + username);
        return encodeLines;
    }
    private ArrayList<String> defaultPhoto(){
        ArrayList<String> arrayLines = new ArrayList<String>();
        String line = "";
        int cont = 0;
        try(FileInputStream input=new FileInputStream(".\\data\\default.jpg")){
            int valor = input.read();
            while(valor!=-1){
                if (cont > 511) {
                    arrayLines.add(line);
                    line = "";
                    cont = 0;
                }
                line += (char)valor;
                valor=input.read();
                cont++;
            }
            if (cont > 0) {
                arrayLines.add(line);
                cont = 0;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, null, ex);
        }
        return arrayLines;
    }
    private String constructPhoto(String photoLine, int totalPkg){
        //PROTOCOLCRISTOMESSENGER1.0#FECHA/HORA#SERVER#RESPONSE_MULTIMEDIA#<LOGIN_CLIENTE>#<TOTAL_BYTES_MULTIMEDIA>#<SIZE_PACKET_MULTIMEDIA>#@512BYTES_FOTO
        String line = protocolVersion + "#" + dateTime + "#SERVER#" + "RESPONSE_MULTIMEDIA#" + 
                protocolList.get(4) + "#" + totalPkg + "#" + photoLine.length() + "#" + photoLine;
        return line;
    }
    private ArrayList<String> sendMessage(){
        if (this.myUserController == null) {
            this.myUserController = new UsersController();
        }
        dateTime = sdf.format(new Timestamp(System.currentTimeMillis()));
        ArrayList<String> msgList = new ArrayList<String>();
        Message msg = new Message();
        msg.setDate(protocolList.get(1));
        msg.setText(protocolList.get(6));
        msg.setTransmitter(protocolList.get(4));
        msg.setReceiver(protocolList.get(5));
        if (this.myUserController.sendMessage(msg)) {
            msgList.add(protocolVersion + "#" + dateTime + "#SERVER#" + "CHAT#" + 
                msg.getTransmitter() + "#" + msg.getReceiver() + "#MESSAGE_SUCCESFULLY_PROCESSED#" + msg.getDate() );
            //PROTOCOLCRISTOMESSENGER1.0#FECHA/HORA#SERVER#CHAT#<LOGIN_ORIG#<LOGIN_DEST>#<MESSAGE>#TIMESTAMP
                ArrayMessages.add(protocolVersion + "#" + dateTime + "#SERVER#" + "CHAT#" + 
                msg.getTransmitter() + "#" + msg.getReceiver() + "#" + msg.getText() + "#" + msg.getDate());
        }else{
            msgList.add(protocolVersion + "#" + dateTime + "#SERVER#" + "CHAT#" + 
                "#FORBIDDEN_CHAT#");
        }
        return msgList;
    }
    public boolean checkMessageReceived(String block){
        String[] result = block.split("#");
        boolean check = false;
        if (result.length == 7 && "CHAT".equals(result[3]) && "RECEIVED_MESSAGE".equals(result[4])) {
            check = true;
        }
        return check;
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
        dateTime = sdf.format(new Timestamp(System.currentTimeMillis()));
        MainWindow.ConsoleDebug("Version: " + protocolList.get(0));
        System.out.println("Version: " + protocolList.get(0));
        MainWindow.ConsoleDebug("User: " + username);
        System.out.println("User: " + username);
        MainWindow.ConsoleDebug("Pass: " + protocolList.get(5));
        System.out.println("Pass: " + protocolList.get(5));
        if (myUserController.loginUser(username, protocolList.get(5))) {
            result.add(this.protocolVersion + "#" + dateTime + "#SERVER#LOGIN_CORRECT#" + username + getFriends());
        }else{
            result.add(this.protocolVersion + "#" + dateTime + "#SERVER#ERROR#BAD_LOGIN");
            this.username = "";
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
