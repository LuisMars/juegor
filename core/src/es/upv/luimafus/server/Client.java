package es.upv.luimafus.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

/**
 * Write a description of class Player here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Client extends Thread {
    public static int tClient = 0;
    public Socket socket;
    public int nClient;
    public Scanner stream;
    public PrintWriter pw;

    ServerScreen serverScreen;

    public Client(ServerScreen serverScreen, Socket s) {
        try {
            this.serverScreen = serverScreen;
            socket = s;
            stream = new Scanner(socket.getInputStream());
            nClient = ++tClient;
            //System.out.println("\tClient " + nClient + " connected");
            serverScreen.print("\tClient " + nClient + " connected");
            send("\tClient " + nClient + " connected");
            start();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    public void run() {
        try {
            while(stream.hasNextLine()) {
	            String str = stream.nextLine();
                send(str);
                serverScreen.print(/*"Client " + nClient + ": " + */str);
            }
            serverScreen.print("Client " + nClient + " disconnecting");
            socket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String str) throws IOException {
        for(Client p: Server.clients) {
            if(nClient != p.nClient) {
                pw = new PrintWriter(p.socket.getOutputStream(),true);
                pw.println(/*"\tClient " + p.nClient + ": " + */str);
                if(p.isClosed())
                    pw.println(p.nClient + " disconected");
            }
        }
    }

    private boolean isClosed() {
        return socket.isClosed();
    }
}
