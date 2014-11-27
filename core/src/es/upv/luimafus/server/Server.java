package es.upv.luimafus.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Write a description of class Server here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Server extends Thread{
    public static List<Client> clients = new ArrayList<Client>();
    public static PrintWriter pw;
    private static ServerSocket ss;
    int port;
    ServerScreen serverScreen;

    public Server(ServerScreen serverScreen) {
        this.serverScreen = serverScreen;
    }

    public void start(int port) {
        this.port = port;
        try {
            ss = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        start();
    }
    public void run() {

        serverScreen.print("Server opened");
        try {
            while (true) {
                clients.add(new Client(serverScreen,ss.accept()));
            }
        } catch (IOException e) {
            serverScreen.print("Server closed");
        }
    }

    public void disconnect() throws IOException{
        ss.close();
    }

}
