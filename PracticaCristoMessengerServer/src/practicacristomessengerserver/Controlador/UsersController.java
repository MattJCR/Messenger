package practicacristomessengerserver.Controlador;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import practicacristomessengerserver.MainWindow;
import practicacristomessengerserver.Modelo.Message;
import practicacristomessengerserver.Modelo.User;
import practicacristomessengerserver.Modelo.UsersModel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Matt Workstation
 */
public class UsersController {
    private User user;
    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
    public boolean loginUser(String id, String pass){
        UsersModel myUserModel = new UsersModel();
        boolean check = false;
        for (User u: myUserModel.getUsers()) {
            if (u.getId().equals(id) & u.getPassword().equals(pass)) {
                check = true;
                this.user = u;
                myUserModel.updateStatus(id, 0);
                break;
            }
        }
        return check;
    }
    public void unloginUser(String id){
        UsersModel myUserModel = new UsersModel();
        myUserModel.updateStatus(id, 1);
    }
    public ArrayList<User> loadFriends(String u){
        UsersModel myUserModel = new UsersModel();
        ArrayList<User> friendList = myUserModel.getFriends(u);
        System.out.println("Amigos de " + u +  " cargados: " + friendList.size());
        MainWindow.ConsoleDebug("Amigos de " + u +  " cargados: " + friendList.size());
        return friendList;
    }
    public User getInfoUser(String idUser){
        UsersModel myUserModel = new UsersModel();
        return myUserModel.getOnlyUser(idUser);
    }
    public boolean getStatusIfIsFriend(String idUser,String idFriend){
        boolean status = false;
        if (isFriend(idUser,idFriend)) {
            status = getInfoUser(idFriend).getState();
        }
        return status;
    }
    public boolean isFriend(String idUser, String idFriend){
        UsersModel myUserModel = new UsersModel();
        return myUserModel.checkIsFriend(idUser, idFriend);
    }
    public ArrayList<Message> loadMessages(String userID,String friendId, String date){
        UsersModel myUserModel = new UsersModel();
        ArrayList<Message> messageList = myUserModel.getMessages(userID,friendId, date);
        System.out.println("Mensajes cargados del usuario " + friendId + ": " + messageList.size());
        MainWindow.ConsoleDebug("Mensajes cargados del usuario " + friendId + ": " + messageList.size());
        return messageList;
    }
    public int sizeMessages(String userID,String friendId, String date){
        UsersModel myUserModel = new UsersModel();
        int number = myUserModel.getSizeMessages(userID,friendId, date);
        System.out.println("Mensajes totales en el historico del usuario " + friendId + ": " + number);
        MainWindow.ConsoleDebug("Mensajes totales en el historico del usuario " + friendId + ": " + number);
        return number;
    }
    public ArrayList<User> loadNewFriends(){
        UsersModel myUserModel = new UsersModel();
        ArrayList<User> friendList = myUserModel.getFriends(this.user.getId());
        //WindowChat.setSysLogs("Amigos cargados: " + friendList.size());
        return friendList;
    }
    /*
    public void sendMessage(Message msg){
        WindowChat.setSysLogs("Valores del mensage Transmitter: " + msg.getTransmitter());
        WindowChat.setSysLogs("Valores del mensage Receiver: " + msg.getReceiver());
        WindowChat.setSysLogs("Valores del mensage Text: " + msg.getText());
        UsersModel myUserModel = new UsersModel();
        myUserModel.insertMessage(msg);
        WindowChat.setSysLogs("Mensaje insertado...");
    }*/
    public void sendUser(User usr){
        UsersModel myUserModel = new UsersModel();
        myUserModel.insertUser(usr);
        //WindowChat.setSysLogs("Usuario insertado...");
    }
}