package practicacristomessengerserver.Modelo;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public ArrayList<User> getUsers(){
        ArrayList<User> usersList = new ArrayList<>();
        this.query  = "SELECT * FROM cristomessenger.user";
        try {
            this.ConnectDatabase();
            //WindowChat.setSysLogs("Obteniendo usuarios...");
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
    public User getOnlyUser(String idUser){
        User user = new User();
        this.query  = "SELECT * FROM cristomessenger.user WHERE id_user = '" + idUser + "';";
        try {
            this.ConnectDatabase();
            //WindowChat.setSysLogs("Obteniendo usuarios...");
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
                user = UserAux;
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
        return user;
    }
    public User getUser(String uName){
        User user = new User();
        this.query  = "SELECT * FROM cristomessenger.user WHERE id_user='" + uName + "'";
        try {
            this.ConnectDatabase();
            //WindowChat.setSysLogs("Obteniendo usuarios...");
            this.QuerySQLExecute();
            while (this.rs.next()) {
                user.setId(this.rs.getString("id_user"));
                user.setPassword(this.rs.getString("password"));
                user.setName(this.rs.getString("name"));
                user.setFirstSurname(this.rs.getString("surname1"));
                user.setSecondSurname(this.rs.getString("surname2"));
                user.setPhoto(this.rs.getString("photo"));
                if (this.rs.getString("state").equals("0")) {
                    user.setState(true);
                }else{
                    user.setState(false);
                }
            }
            System.out.println("BD user" + rs.getFetchSize() + ": " + user.getId() );
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
        return user;
    }
    public boolean checkIsFriend(String idUser,String idFriend){
        boolean check = false;
        if (idUser.equals(idFriend)) {
            check = true;
        }else{
            this.query  = "SELECT COUNT(*) as number FROM cristomessenger.friend WHERE id_user_orig = '" + idUser 
                    + "' && id_user_dest = '" + idFriend + "';";
            try {
                this.ConnectDatabase();
                this.QuerySQLExecute();
                while (this.rs.next()) {
                    if (Integer.parseInt(this.rs.getString("number")) == 1) {
                        check = true;
                    }
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
        }
        return check;
    }
    public ArrayList<User> getFriends(String myUserId){
        ArrayList<User> friendList = new ArrayList<>();
        this.query  = "SELECT id_user,name,surname1,surname2,photo,state "
                + "FROM cristomessenger.user, "
                + "(SELECT id_user_dest FROM cristomessenger.friend WHERE id_user_orig = \"" + myUserId + "\") as t1 "
                + "WHERE id_user = t1.id_user_dest";
        try {
            this.ConnectDatabase();
            //WindowChat.setSysLogs("Obteniendo amigos...");
            this.QuerySQLExecute();
            System.out.println("QUERY SIZE: " + rs.getFetchSize());
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
    
    public ArrayList<Message> getMessages(String logUser, String friendUser, String date){
        System.out.println("getMessagesDate.length(): " + date.length());
        String dateAux = date.split(" ")[0];
        String lastDay = sdf.format(Timestamp.valueOf(date).getTime() - (24*60*60*1000));
        lastDay = lastDay.split(" ")[0];
        ArrayList<Message> myMessageList = new ArrayList<>();
        this.query  = "SELECT * FROM cristomessenger.message\n" +
        "WHERE ((id_user_orig = \"" + logUser + "\" and id_user_dest = \"" + friendUser + "\") or ("
        + "id_user_orig = \"" + friendUser + "\" and id_user_dest = \"" + logUser + "\")) and "
        + "datetime > \"" + lastDay + "%\" and " + "datetime < \"" + dateAux + "%\" order by datetime asc;";
        System.out.println("getMessagesDate: " + date);
        try {
            this.ConnectDatabase();
            this.QuerySQLExecute();
            while (this.rs.next()) {
                Message MessageAux = new Message();
                MessageAux.setTransmitter(this.rs.getString("id_user_orig"));
                MessageAux.setReceiver(this.rs.getString("id_user_dest"));
                MessageAux.setText(this.rs.getString("text"));
                MessageAux.setDate(this.rs.getString("datetime"));
                myMessageList.add(MessageAux);
            }
            System.out.println("getMessages: " + myMessageList.size());
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
        if (myMessageList.isEmpty()) {
            myMessageList = getTop10Messages(logUser,friendUser,date);
        }
        return myMessageList;
    }
    public int getSizeMessages(String logUser, String friendUser, String date) {
        int number = 0;
        String dateAux = date.split(" ")[0];
        this.query  = "SELECT COUNT(*) as number FROM cristomessenger.message\n" +
        "WHERE ((id_user_orig = \"" + logUser + "\" and id_user_dest = \"" + friendUser + "\") or ("
        + "id_user_orig = \"" + friendUser + "\" and id_user_dest = \""  + logUser + "\")) and "
        + "datetime < \"" + dateAux + "%\";";
        try {
            this.ConnectDatabase();
            this.QuerySQLExecute();
            while (this.rs.next()) {
                number = Integer.parseInt(this.rs.getString("number"));
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
        System.out.println("Value of getSizeMessages: " + number);
        return number;
    }
    public ArrayList<Message> getTop10Messages(String logUser, String friendUser, String date){
        System.out.println("getMessagesDate.length(): " + date.length());
        //String dateAux = date.split(" ")[0];
        ArrayList<Message> myMessageList = new ArrayList<>();
        System.out.println("getTop10Date: " + date);
        this.query  = "SELECT * FROM cristomessenger.message\n" +
        "WHERE ((id_user_orig = \"" + logUser + "\" and id_user_dest = \"" + friendUser + "\") or ("
        + "id_user_orig = \"" + friendUser + "\" and id_user_dest = \"" + logUser + "\")) and "
        + "datetime < \"" + date + "\" " +
        "order by datetime asc LIMIT 10;";
        try {
            this.ConnectDatabase();
            this.QuerySQLExecute();
            while (this.rs.next()) {
                Message MessageAux = new Message();
                MessageAux.setTransmitter(this.rs.getString("id_user_orig"));
                MessageAux.setReceiver(this.rs.getString("id_user_dest"));
                MessageAux.setText(this.rs.getString("text"));
                MessageAux.setDate(this.rs.getString("datetime"));
                myMessageList.add(MessageAux);
            }
            System.out.println("getTop10MSG: " + myMessageList.size());
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
    
    /*
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
    }*/
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
        //WindowChat.setSysLogs("Insertando usuario...");
        this.QuerySQLUpdate();
        if (this.stmt != null) { 
            try {
                this.stmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(UsersModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public void updateStatus(String idUser, int status){
        this.query  = "UPDATE cristomessenger.user SET state ='" + status + "' "
                + "WHERE id_user = '" + idUser + "';";
        try {
            this.ConnectDatabase();
            this.QuerySQLUpdate();
        } catch ( Exception e ) {
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
        System.out.println("Status of " + idUser + ": " + status);
    }

    
}
