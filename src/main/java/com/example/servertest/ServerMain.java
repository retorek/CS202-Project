package com.example.servertest;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {

    public static void main(String[] args){
        ServerSocket serverSocket = null;
        Socket socket;

        try{
            serverSocket = new ServerSocket(8888);
            while(true){
                try{
                    socket = serverSocket.accept();
                    /*Runnable r = new ServerThread(socket);
                    Thread t = new Thread(r);
                    t.start();*/

                    ServerThread s = new ServerThread(socket);
                    s.start();
                }catch(IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        }catch(IOException e){
            System.err.println(e.getMessage());
        }
    }
}
