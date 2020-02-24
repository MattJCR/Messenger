
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Matt Workstation
 */
public class UsersModel extends DatabaseModel{
    public ArrayList<User> getUsers(){
        ArrayList<User> usersList = new ArrayList<>();
        this.query  = "SELECT * FROM cristomessenger.user";
        try {
            this.ConnectDatabase();
            WindowChat.setSysLogs("Obteniendo usuarios...");
            this.QuerySQLExecute();
            while (this.rs.next()) {
                User UserAux = new User();
                UserAux.setId(this.rs.getString("id_user"));
                UserAux.setPassword(this.rs.getString("password"));
                UserAux.setName(this.rs.getString("name"));
                UserAux.setFirstSurname(this.rs.getString("surname1"));
                UserAux.setSecondSurname(this.rs.getString("surname2"));
                UserAux.setPhoto(this.rs.getString("photo"));
                if (this.rs.getString("state").equals("0")) {
                    UserAux.setState(true);
                }else{
                    UserAux.setState(false);
                }
                usersList.add(UserAux);
            }
        } catch (SQLException e ) {
            System.out.println(e.getMessage());
        }  finally {
            if (this.stmt != null) { 
                try {
                    this.stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(UsersModel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return usersList;
    }
    public ArrayList<User> getFriends(String myUserId){
        ArrayList<User> friendList = new ArrayList<>();
        this.query  = "SELECT id_user,name,surname1,surname2,photo,state "
                + "FROM cristomessenger.user, "
                + "(SELECT id_user_dest FROM cristomessenger.friend WHERE id_user_orig = \"" + myUserId + "\") as t1 "
                + "WHERE id_user = t1.id_user_dest";
        try {
            this.ConnectDatabase();
            WindowChat.setSysLogs("Obteniendo amigos...");
            this.QuerySQLExecute();
            while (this.rs.next()) {
                User UserAux = new User();
                UserAux.setId(this.rs.getString("id_user"));
                UserAux.setName(this.rs.getString("name"));
                UserAux.setFirstSurname(this.rs.getString("surname1"));
                UserAux.setSecondSurname(this.rs.getString("surname2"));
                UserAux.setPhoto(this.rs.getString("photo"));
                if ("0".equals(this.rs.getString("state"))) {
                    UserAux.setState(true);
                }else{
                    UserAux.setState(false);
                }
                friendList.add(UserAux);
            }
        } catch (SQLException e ) {
            System.out.println(e.getMessage());
        }  finally {
            if (this.stmt != null) { 
                try {
                    this.stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(UsersModel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return friendList;
    }
    public ArrayList<Message> getMessages(String logUser, String friendUser){
        ArrayList<Message> myMessageList = new ArrayList<>();
        this.query  = "SELECT * FROM cristomessenger.message\n" +
        "WHERE (id_user_orig = \"" + logUser + "\" and id_user_dest = \"" + friendUser + "\") or ("
        + "id_user_orig = \"" + friendUser + "\" and id_user_dest = \"" + logUser + "\") " +
        "order by date,hour asc;";
        try {
            this.ConnectDatabase();
            WindowChat.setSysLogs("Obteniendo mensajes...");
            this.QuerySQLExecute();
            while (this.rs.next()) {
                Message MessageAux = new Message();
                MessageAux.setTransmitter(this.rs.getString("id_user_orig"));
                MessageAux.setReceiver(this.rs.getString("id_user_dest"));
                MessageAux.setText(this.rs.getString("text"));
                myMessageList.add(MessageAux);
            }
        } catch (SQLException e ) {
            System.out.println(e.getMessage());
        }  finally {
            if (this.stmt != null) { 
                try {
                    this.stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(UsersModel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        return myMessageList;
    }
    public void insertMessage(Message msg){
        this.query = "INSERT INTO cristomessenger.message VALUES ('" + msg.getTransmitter() + "','"
                + msg.getReceiver() + "',DATE_FORMAT(NOW(), '%d %m %Y'),DATE_FORMAT(NOW(),'%H:%i:%S'),0,1,'"
                + msg.getText() + "')";
        this.ConnectDatabase();
        WindowChat.setSysLogs("Insertando mensaje...");
        this.QuerySQLUpdate();
        if (this.stmt != null) { 
            try {
                this.stmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(UsersModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public void insertUser(User usr){
        String status = "";
        if (usr.getState()) {
            status = "0";
        }else{
            status = "1";
        }
        this.query = "INSERT INTO cristomessenger.user VALUES ('" + usr.getId() + 
                "','" + usr.getName() + "','" + usr.getPassword() + "','" + usr.getFirstSurname() + 
                "','" + usr.getSecondSurname() + "','" + usr.getPhoto() + "'," + status + ")";
        this.ConnectDatabase();
        WindowChat.setSysLogs("Insertando usuario...");
        this.QuerySQLUpdate();
        if (this.stmt != null) { 
            try {
                this.stmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(UsersModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
