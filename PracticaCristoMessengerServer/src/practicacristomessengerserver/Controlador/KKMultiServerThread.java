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
import java.util.logging.Level;
import java.util.logging.Logger;
import practicacristomessengerserver.MainWindow;

public class KKMultiServerThread extends Thread {
    private Socket socket = null;

    public KKMultiServerThread(Socket socket) {
        super("KKMultiServerThread");
        this.socket = socket;
    }
    public void stopHebra(){
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(KKMultiServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void run() {
        try (   
            PrintWriter out =
                new PrintWriter(this.socket.getOutputStream(), true);                   
            BufferedReader in = new BufferedReader(
                new InputStreamReader(this.socket.getInputStream()));
        ) {
            if (MainWindow.MSG_BIENVENIDAD) {
                out.println("Conexión establecidad con " + socket.getLocalAddress());
            }
            String inputLine;
            Protocol p = new Protocol();
            while ((inputLine = in.readLine()) != null) {
                MainWindow.ConsoleDebug("Cliente [" + socket.getInetAddress() + "]: " + inputLine);
                System.out.println("Cliente [" + socket.getInetAddress() + "]: " + inputLine);

                for (String line : p.start(inputLine)) {
                    out.println(line);
                }
                
                ///////////////////////////
                /*String acumulador = "";
                for (String line : p.start(inputLine)) {
                    acumulador+= line;
                }
                out.println(acumulador);*/
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
