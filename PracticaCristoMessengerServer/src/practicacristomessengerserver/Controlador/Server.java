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
import java.util.logging.Level;
import java.util.logging.Logger;
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
    public Server(int port) {
        this.portNumber = port;
    }
    
    public void StopServer(){
            listening = false;
            for (KKMultiServerThread hebra : arrayServerThread) {
                hebra.stopHebra();
            }
    }
    @Override
    public void run(){
        listening = true;
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) { 
            myServerSocket = serverSocket;
            while (listening) {
                int numberThread = arrayServerThread.size();
                System.out.println("Escuchando nueva hebra...");
                MainWindow.ConsoleDebug("Escuchando nueva hebra...");
                KKMultiServerThread hebra = new KKMultiServerThread(serverSocket.accept());
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
