package es.upv.luimafus;

import java.io.*;
import java.util.*;
import java.net.*;
/**
* Write a description of class Cliente here.
* 
* @author (your name) 
* @version (a version number or a date)
*/
public class User extends Thread {
  private static String msg = "";
  static Socket s;
  static Scanner tec;
  static Scanner in;
  static PrintWriter pw;
    WaitingScreen waitingScreen;
    public User(WaitingScreen waitingScreen, String address) {
        this.waitingScreen = waitingScreen;
        try {

            s = new Socket(address.substring(0, address.lastIndexOf(":")),
                  Integer.parseInt(address.substring(address.lastIndexOf(":")+1, address.length())));
            tec = new Scanner (System.in);
            in = new Scanner(s.getInputStream());
            pw = new PrintWriter(s.getOutputStream(),true);

            start();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String msg) {
        pw.println(msg);
    }
    public void disconnect() {

        try {
            waitingScreen.print("Desconectando del servidor...");
            s.close();
        } catch (IOException e) {
            waitingScreen.print("Servidor desconectado");
        }
    }
    public User(Socket s, Scanner in) {
        User.s = s;
        User.in = in;
    }
    
    public void run() {
        while(in.hasNextLine()) {
            //   System.out.println(in.nextLine());
            msg = in.nextLine();
            waitingScreen.print(msg);
        }
    }
}
