package es.upv.luimafus.server;

import es.upv.luimafus.Attack;
import es.upv.luimafus.Player;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Server extends Thread {

    static List<User> users = new ArrayList<User>();
    static ServerScreen serverScreen;
    private static DatagramSocket socket;
    public Server(ServerScreen serverScreen, int port) {

        serverScreen.print("Creating server...");

        this.serverScreen = serverScreen;

        try {
            socket = new DatagramSocket(port);
        } catch (SocketException socketException) {
            socketException.printStackTrace();
            System.exit(1);
        }
        start();
    }

    public static void sendUpdate(Collection<Attack> attacks) {
        ByteArrayOutputStream msg = new ByteArrayOutputStream();
        msg.write(5);
        msg.write(users.size());
        users.stream().filter(u -> u.isReady).forEach(u -> {
            msg.write(u.playerID);
            msg.write(u.p.getAction());
            msg.write(u.p.getcHP());
        });
        msg.write(attacks.size());
        for (Attack a : attacks) {
            msg.write(a.getID());
            msg.write(a.getX());
            msg.write(a.getY());
        }
        users.stream().filter(u -> u.isReady).forEach(u -> sendBytes(u, msg));
    }

    private static void sendBytes(User us, ByteArrayOutputStream msg) {
        DatagramPacket sendPacket = new DatagramPacket(msg.toByteArray(), msg.toByteArray().length, us.socketAddress);
        try {
            socket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                byte data[] = new byte[100];
                DatagramPacket receivePacket = new DatagramPacket(data, data.length);

                socket.receive(receivePacket);

                //LOGIN
                if (receivePacket.getData()[0] == 1) {
                    users.add(new User(receivePacket.getSocketAddress(), new String(receivePacket.getData(), 1, receivePacket.getLength())));
                    serverScreen.print("\tTotal players: " + users.size());
                    TellID(users.get(users.size() - 1));
                    SendPrevID(users.get(users.size() - 1));
                    SendIDAll(users.get(users.size() - 1));

                }
                //CHAT MSG
                if (receivePacket.getData()[0] == 2) {
                    String msg = new String(receivePacket.getData(), 1, receivePacket.getLength());
                    serverScreen.print(findPlayer(receivePacket.getData()[1]).name + ": \t" + msg);


                    for (User u : users) {
                        DatagramPacket sendPacket = new DatagramPacket(receivePacket.getData(), receivePacket.getLength(), u.socketAddress);
                        socket.send(sendPacket);
                    }
                }

                //SEND MAP
                if (receivePacket.getData()[0] == 3) {
                    User u = findPlayer(receivePacket.getData()[1]);
                    u.isReady = true;
                    serverScreen.print(u.name + " is ready");
                    SendMap(serverScreen.speed, serverScreen.GameMap.map, u);
                    u.p = new Player(serverScreen.GameMap, true, false, u.name);
                    sendInitPos(u);
                }

                if (receivePacket.getData()[0] == 4) {
                    User u = findPlayer(receivePacket.getData()[1]);
                    //serverScreen.print(u.name + " action: " + receivePacket.getData()[2]);
                }

                //sendPacketToClient( receivePacket );
            } catch (IOException ioException) {
                serverScreen.print(ioException.toString() + "\n");
                ioException.printStackTrace();
            }
        }
    }

    private User findPlayer(byte b) {
        for(User u : users) {
            if(u.playerID == b)
                return u;
        }
        return null;
    }

    private void sendInitPos(User us) {
        ByteArrayOutputStream msg = new ByteArrayOutputStream();
        msg.write(4);
        int i = 0;
        for (User u : users)
            if (u.isReady)
                i++;
        msg.write(i);
        users.stream().filter(u -> u.isReady).forEach(u -> {
            msg.write(u.playerID);
            msg.write(u.p.getX());
            msg.write(u.p.getY());
        });
        sendBytes(us, msg);
    }

    private void SendID(User rec, User us) {

        ByteArrayOutputStream msg = new ByteArrayOutputStream();

            msg.write(1);
            msg.write(us.playerID);
        try {
            msg.write(us.name.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendBytes(rec, msg);
    }

    private void SendIDAll(User us) {
        for (User u : users) {
            SendID(u, us);
        }
    }

    private void SendPrevID(User rec) {
        for (User u : users) {
            SendID(rec, u);
        }
    }

    private void TellID(User us) {

        ByteArrayOutputStream msg = new ByteArrayOutputStream();

        msg.write(0);
        msg.write(us.playerID);


        sendBytes(us, msg);
    }

    private void SendMap(int speed, int[][] a, User u) {

        ByteArrayOutputStream msg = new ByteArrayOutputStream();
        msg.write(3);
        msg.write(speed);
        msg.write((byte) a.length);
        msg.write((byte) a[0].length);
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                msg.write((byte) a[i][j]);
            }
        }
        sendBytes(u, msg);
    }
}


