package es.upv.luimafus.server;

import es.upv.luimafus.Player;

import java.net.SocketAddress;

/**
 * Created by Luis on 01/12/2014.
 */
public class User extends Thread {

    static int totalPlayers = 0;
    static int readyPlayers = 0;
    public boolean userKnowsIsDead = false;
    SocketAddress socketAddress;
    String name;
    int playerID;
    boolean isReady = false;
    boolean hasMap = false;
    Player p;

    public User(SocketAddress sa, String n) {
        socketAddress = sa;
        name = n;
        playerID = totalPlayers;
        totalPlayers++;
    }

    public void run() {
        while (isReady && !hasMap) {
            //Server.SendMap(Server.serverScreen.speed, Server.serverScreen.GameMap.map, this);
            Server.SendMap2(Server.serverScreen.speed, Server.serverScreen.GameMap.map, this);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
