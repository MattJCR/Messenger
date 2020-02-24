/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practicacristomessengerserver.Controlador;
/**
 *
 * @author Matt Workstation
 */
import practicacristomessengerserver.Controlador.Protocol;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import practicacristomessengerserver.MainWindow;

public class KKMultiServerThread extends Thread {
    public Lock mutex;
    private Socket socket = null;
    public String username = "";
    public PrintWriter out = null;
    public BufferedReader in = null;
    public ArrayList<String> inputMessages = new ArrayList<String>();
    private ArrayList<KKMultiServerThread> arrayClients = null;
    Protocol p;
    public KKMultiServerThread(Socket socket, ArrayList<KKMultiServerThread> clients) {
        super("KKMultiServerThread");
        this.socket = socket;
        this.arrayClients = clients;
    }
    public void stopHebra(){
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(KKMultiServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void sendMessages(){
        boolean check = false;
        for (KKMultiServerThread client : arrayClients) {
            //client.out.println("");
            if (!username.equals(client.username)) {
                MainWindow.ConsoleDebug("TrySend " + username + "->" + client.username + ": " + inputMessages.size());
                System.out.println("TrySend " + username + "->" + client.username + ": " + inputMessages.size());
                for (String block : inputMessages) {
                    if (block.split("#")[5].equals(client.username)) {
                        MainWindow.ConsoleDebug("OK!");
                        System.out.println("OK!");
                        client.out.println(block);
                        inputMessages.remove(block);
                        check = true;
                        break;
                    }
                }
            }
            if (!check) {
                client.out.println("");
            }else{
                break;
            }
        }
    }
    private void threadMessages(){
        Thread thread = new Thread(){
        @Override
        public void run(){
            try {
                Thread.sleep(10000);
                do {
                    try {
                        mutex.lock();
                        sendMessages();
                    }finally{
                        mutex.unlock();
                    }
                    Thread.sleep(5000);
                } while (true);
            } catch (InterruptedException ex) {
                Logger.getLogger(KKMultiServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
      };
      thread.start();
    }
    @Override
    public void run() {
        try {
            mutex = new ReentrantLock();
            threadMessages();
            out = new PrintWriter(this.socket.getOutputStream(), true);                   
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            if (MainWindow.MSG_BIENVENIDAD) {
                out.println("Conexión establecidad con " + socket.getLocalAddress());
            }
            String inputLine;
            p = new Protocol(inputMessages);
            while ((inputLine = in.readLine()) != null) {
                try{
                    mutex.lock();
                    MainWindow.ConsoleDebug("Cliente [" + socket.getInetAddress() + "]: " + inputLine);
                    System.out.println("Cliente [" + socket.getInetAddress() + "]: " + inputLine);
                    for (String line : p.start(inputLine,this.username)) {
                        out.println(line);
                        this.username = p.username;
                    }
                }finally{
                    mutex.unlock();
                }
            }
            socket.close();
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                + this.socket.getPort() + " or listening for a connection");
            MainWindow.ConsoleDebug("Exception caught when trying to listen on port "
                + this.socket.getPort() + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}
