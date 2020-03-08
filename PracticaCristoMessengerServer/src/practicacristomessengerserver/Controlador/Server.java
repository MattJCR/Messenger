/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practicacristomessengerserver.Controlador;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import practicacristomessengerserver.MainWindow;

/**
 *
 * @author Matt Workstation
 */
public class Server extends Thread{
    private int portNumber = 39999;
    private ServerSocket myServerSocket;
    private ArrayList<KKMultiServerThread> arrayServerThread = new ArrayList();
    private boolean listening;
    private boolean checkStatus = true;
    private UsersController myServerUserController = new UsersController();
    Thread thread;
    public Server(int port) {
        this.portNumber = port;
    }
    
    public void StopServer(){
            listening = false;
            for (KKMultiServerThread hebra : arrayServerThread) {
                try{
                    hebra.disconnectClient();
                    hebra.stop();
                    arrayServerThread.remove(hebra);
                }catch(Exception ex){
                    System.out.println("StopServer: " + ex);
                }  
            }
    }
    public void StatusClient(){
        this.thread = new Thread(){
        @Override
        public void run(){
            try {
                do {
                    for (KKMultiServerThread client : arrayServerThread) {
                        System.out.println("Client " + client.username + ": " + client.getState());
                        MainWindow.ConsoleDebug("Client " + client.username + ": " + client.getState());
                        if ("TERMINATED".equals(client.getState().toString())) {
                            arrayServerThread.remove(client);
                            break;
                        }
                    }
                    Thread.sleep(5000);
                } while (checkStatus);
            } catch (InterruptedException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
      };
      thread.start();
    }
    public void CheckClients(){
        this.thread = new Thread(){
        @Override
        public void run(){
            try {
                do {
                    for (String user : myServerUserController.getAllUsers()) {
                        boolean check = false;
                        int count = 0;
                        for (KKMultiServerThread client : arrayServerThread) {
                            if (client.username.equals(user.split("#")[0])) {
                                check = true;
                                count++;
                            }
                        }
                        if (count > 1) {
                            for (KKMultiServerThread client : arrayServerThread) {
                                if (client.username.equals(user.split("#")[0])) {
                                    client.disconnectClient();
                                    break;
                                }
                            }
                        }
                        if (!check && !"0".equals(user.split("#")[1])) {
                            System.out.println("SERVER: disconnected user " + user.split("#")[0] + ".");
                            myServerUserController.changeStatusUser(user.split("#")[0],0);
                        }else if (check && !"1".equals(user.split("#")[1])) {
                            myServerUserController.changeStatusUser(user.split("#")[0],1);
                        }
                    }
                    Thread.sleep(10000);
                } while (checkStatus);
            } catch (InterruptedException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
      };
      thread.start();
    }
    @Override
    public void run(){
        listening = true;
        System.out.println("USER LIST: ");
        for (String user : myServerUserController.getAllUsers()) {
            String status = "DISCONNECTED.";
            if ("0".equals(user.split("#")[1])) {
                status = "CONNECTED";
            }
            String line = (user.split("#")[0] +  " -> " + status);
            MainWindow.ConsoleDebug(line);
            System.out.println(line);
        }
        StatusClient();
        CheckClients();
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) { 
            myServerSocket = serverSocket;
            while (listening) {
                int numberThread = arrayServerThread.size();
                System.out.println("Escuchando nueva hebra...");
                MainWindow.ConsoleDebug("Escuchando nueva hebra...");
                KKMultiServerThread hebra = new KKMultiServerThread(serverSocket.accept(),arrayServerThread);
	        arrayServerThread.add(hebra);
                hebra.start();
                System.out.println("Hebra[" + numberThread +"] inicializada...");
                MainWindow.ConsoleDebug("Hebra[" + numberThread +"] inicializada...");
	    }
            MainWindow.ConsoleDebug("Salida de escuchando hebras...");
            System.out.println("Salida de escuchando hebras...");
        } catch (IOException e) {
            MainWindow.ConsoleDebug("Could not listen on port " + portNumber);
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
    
}
