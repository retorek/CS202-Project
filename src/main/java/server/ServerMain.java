package server;

import marketplace.Marketplace;
import server.Database;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class ServerMain {

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket socket;

        try {
            Database db = new Database();
            db.connect();

            Marketplace marketplace = new Marketplace(db.getConnection());

            serverSocket = new ServerSocket(8880);
            while(true){
                try{
                    socket = serverSocket.accept();

                    ServerThread s = new ServerThread(socket, db);
                    s.start();
                }catch(IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        }catch(IOException e){
            System.err.println(e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}