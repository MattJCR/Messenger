
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JList;

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
        WindowChat.setSysLogs("ID_INSERTED: "+id);
        WindowChat.setSysLogs("PASS_INSERTED: "+pass);
        WindowChat.setSysLogs("Logueando en el sistema...");
        WindowChat.setSysLogs("Buscando usuario y contraseña en la BD...");
        for (User u: myUserModel.getUsers()) {
            if (u.getId().equals(id) & u.getPassword().equals(pass)) {
                check = true;
                this.user = u;
                WindowChat.setSysLogs("Encontrado User: " + u.getId());
                WindowChat.setSysLogs("La Contraseña coincide.");
                WindowChat.setSysLogs("Login correcto con el usuario " + u.getId() + ".");
            }
        }
        return check;
    }
    public ArrayList<User> loadFriends(){
        UsersModel myUserModel = new UsersModel();
        ArrayList<User> friendList = myUserModel.getFriends(this.user.getId());
        WindowChat.setSysLogs("Amigos cargados: " + friendList.size());
        return friendList;
    }
    public ArrayList<Message> loadMessages(String friendId){
        UsersModel myUserModel = new UsersModel();
        ArrayList<Message> messageList = myUserModel.getMessages(this.user.getId(),friendId);
        WindowChat.setSysLogs("Mensajes cargados del usuario " + friendId + ": " + messageList.size());
        return messageList;
    }
    public ArrayList<User> loadNewFriends(){
        UsersModel myUserModel = new UsersModel();
        ArrayList<User> friendList = myUserModel.getFriends(this.user.getId());
        WindowChat.setSysLogs("Amigos cargados: " + friendList.size());
        return friendList;
    }
    public void sendMessage(Message msg){
        WindowChat.setSysLogs("Valores del mensage Transmitter: " + msg.getTransmitter());
        WindowChat.setSysLogs("Valores del mensage Receiver: " + msg.getReceiver());
        WindowChat.setSysLogs("Valores del mensage Text: " + msg.getText());
        UsersModel myUserModel = new UsersModel();
        myUserModel.insertMessage(msg);
        WindowChat.setSysLogs("Mensaje insertado...");
    }
    public void sendUser(User usr){
        UsersModel myUserModel = new UsersModel();
        myUserModel.insertUser(usr);
        WindowChat.setSysLogs("Usuario insertado...");
    }
}
