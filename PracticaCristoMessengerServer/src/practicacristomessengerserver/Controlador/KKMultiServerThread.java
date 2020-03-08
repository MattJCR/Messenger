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
    public String username = "Unlogged";
    public PrintWriter out = null;
    public BufferedReader in = null;
    public ArrayList<String> inputMessages = new ArrayList<String>();
    private ArrayList<KKMultiServerThread> arrayClients = null;
    Protocol p;
    Thread thread;
    public boolean canUse = false;
    public KKMultiServerThread(Socket socket, ArrayList<KKMultiServerThread> clients) {
        super("KKMultiServerThread");
        this.socket = socket;
        this.arrayClients = clients;
    }
    private void sendMessages(){
        boolean check = false;
        for (KKMultiServerThread client : arrayClients) {
            if (this.canUse && client.canUse && !username.equals(client.username)) {
                client.mutex.lock();
                MainWindow.ConsoleDebug("TrySend " + username + "->" + client.username + ": " + inputMessages.size());
                System.out.println("TrySend " + username + "->" + client.username + ": " + inputMessages.size());
                for (String block : inputMessages) {
                    System.out.println("TrySend BLOCK: " + block);
                    if (block.split("#")[5].equals(client.username)) {
                        MainWindow.ConsoleDebug("SERVER[" + this.username + "]: " + block);
                        System.out.println("SERVER[" + this.username + "]: " + block);
                        client.out.println(block);
                        inputMessages.remove(block);
                        check = true;
                        break;
                    }else if (block.split("#")[4].equals(client.username)) {
                        MainWindow.ConsoleDebug("SERVER[" + this.username + "]: " + block);
                        System.out.println("SERVER[" + this.username + "]: " + block);
                        client.out.println(block);
                        inputMessages.remove(block);
                        check = true;
                        break;
                    }
                }
                client.mutex.unlock();
                if (check) {
                    break;
                }
            }
        }
    }
    private void threadMessages(){
        this.thread = new Thread(){
        @Override
        public void run(){
            try {
                Thread.sleep(10000);
                do {
                    if (canUse) {
                        try {
                            mutex.lock();
                            sendMessages();
                        }finally{
                            mutex.unlock();
                        }
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
    public void disconnectClient(){
        p.unLog();
        MainWindow.ConsoleDebug("SERVER[" + this.username + "]: DISCONNECT.");
        System.out.println("SERVER[" + this.username + "]: DISCONNECT.");
        try{
            this.thread.stop();
            this.socket.close();
        }catch(Exception e){
        }
    }
    private String secureString(String line){
        String result = line.replace("\\", "");
        result = result.replace("'", "");
        result = result.replace("\"", "");
        return result;
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
            p = new Protocol(this);
            while ((inputLine = in.readLine()) != null) {
                try{
                    mutex.lock();
                    this.canUse = false;
                    inputLine = secureString(inputLine);
                    //MainWindow.ConsoleDebug("Cliente [" + this.username + "]: " + inputLine);
                    //System.out.println("Cliente [" + this.username + "]: " + inputLine);
                    for (String line : p.start(inputLine,this.username)) {
                        //p.checkTransmissionMultimadia(line);
                        MainWindow.ConsoleDebug("SERVER[" + this.username + "]: " + line);
                        System.out.println("SERVER[" + this.username + "]: " + line);
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
            disconnectClient();
        }
    }
}
