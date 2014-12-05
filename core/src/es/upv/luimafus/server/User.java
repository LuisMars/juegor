package es.upv.luimafus.server;

import java.net.SocketAddress;

/**
 * Created by Luis on 01/12/2014.
 */
public class User {

    SocketAddress socketAddress;
    String name;
    
    private static int totalPlayers = 0;
    public int playerID;


    public User(SocketAddress sa, String n) {
        socketAddress = sa;
        name = n;
        totalPlayers++;
        playerID = totalPlayers;
    }
}
