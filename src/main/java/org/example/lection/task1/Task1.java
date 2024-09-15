package org.example.lection.task1;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Task1 {
    public static void main(String[] args) {


        try {
            InetAddress address = InetAddress.getLocalHost();
            Socket client = new Socket(address, 1300);
        } catch (UnknownHostException e){
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
