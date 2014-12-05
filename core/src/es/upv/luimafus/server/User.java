package es.upv.luimafus.server;

import java.net.SocketAddress;

/**
 * Created by Luis on 01/12/2014.
 */
public class User {

    SocketAddress socketAddress;
    String name;
    
    static int totalPlayers = 0;
    int playerID;
    boolean isReady = false;

    public User(SocketAddress sa, String n) {
        socketAddress = sa;
        name = n;
        playerID = totalPlayers;
        totalPlayers++;
    }
}
