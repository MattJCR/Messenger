package practicacristomessengerserver.Modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
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
public class DatabaseModel {
    final protected static String ANSI_RESET = "\u001B[0m";
    final protected static String ANSI_RED = "\u001B[31m";
    final protected static String ANSI_GREEN = "\u001B[32m";
    protected Connection conn;
    protected ResultSet rs;
    protected Statement stmt;
    //private String user = "root";
    private String user = "clasedam2";
    private String pass = "root";
    //private String url = "jdbc:mysql://localhost:3306/?useSSL=false";
    private String url = "jdbc:mysql://clasedam2.ddns.net:3306/?useSSL=false";
    protected String query;
    DatabaseModel(){
    this.conn = null;
    this.rs = null;
    this.stmt = null;
    }
    public void ConnectDatabase(){
        Properties connectionProps = new Properties();
        connectionProps.put("user", this.user);
        connectionProps.put("password", this.pass);
        try {
            this.conn = DriverManager.getConnection(this.url,connectionProps);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        //WindowChat.setSysLogs("Connected to database");
    }
    protected void QuerySQLExecute(){
        try {
            //WindowChat.setSysLogs("Ejecutando consulta a la BD...");
            this.stmt = conn.createStatement();
            this.rs = this.stmt.executeQuery(this.query);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    protected void QuerySQLUpdate(){
        try {
            //WindowChat.setSysLogs("Ejecutando actualizacion en la BD...");
            this.stmt = conn.createStatement();
            this.stmt.executeUpdate(this.query);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
