package practicacristomessengerserver.Modelo;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Matt Workstation
 */
public class Message {
    private String text;
    private String transmitter;
    private String receiver;
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    
    public String getText() {
        return text;
    }

    public String getTransmitter() {
        return transmitter;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTransmitter(String transmitter) {
        this.transmitter = transmitter;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
    
}
