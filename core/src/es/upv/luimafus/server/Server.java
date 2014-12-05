package es.upv.luimafus.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {

    List<User> users = new ArrayList<User>();
    int[][] arr = new int[10][10];
    private DatagramSocket socket;

    ServerScreen serverScreen;

    public Server(ServerScreen serverScreen, int port) {

        serverScreen.print("Creating server...");

        this.serverScreen = serverScreen;
        populateMap();
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException socketException) {
            socketException.printStackTrace();
            System.exit(1);
        }
        start();
    }

    public void run() {
        while (true) {
            try {
                byte data[] = new byte[100];
                DatagramPacket receivePacket = new DatagramPacket(data, data.length);

                socket.receive(receivePacket);
                /*
                ServerGUI.displayMessage("\nPacket received:" +
                        "\nFrom host: " + receivePacket.getAddress() +
                        "\nHost port: " + receivePacket.getPort() +
                        "\nContaining:\n\t" + new String(receivePacket.getData(), 1, receivePacket.getLength()));
                */

                //LOGIN
                if ((receivePacket.getData()[0]) == 1) {
                    users.add(new User(receivePacket.getSocketAddress(), new String(receivePacket.getData(), 1, receivePacket.getLength())));
                    TellID(users.get(users.size() - 1));
                    SendPrevID(users.get(users.size() - 1));
                    SendIDAll(users.get(users.size() - 1));

                }
                //CHAT MSG
                if ((receivePacket.getData()[0]) == 2) {
                    serverScreen.print(new String(receivePacket.getData(), 1, receivePacket.getLength()));


                    for (User u : users) {
                        DatagramPacket sendPacket = new DatagramPacket(receivePacket.getData(), receivePacket.getLength(), u.socketAddress);
                        socket.send(sendPacket);


                    }
                }
                if ((receivePacket.getData()[0]) == 3) {
                    for (User u : users) {
                        SendMap(arr, u);
                    }

                }

                //sendPacketToClient( receivePacket );
            } catch (IOException ioException) {
                serverScreen.print(ioException.toString() + "\n");
                ioException.printStackTrace();
            }
        }
    }

    private void SendID(User rec, User us) {
        String toSend = "1" + us.playerID + us.name;
        DatagramPacket sendPacket = new DatagramPacket(toSend.getBytes(), toSend.length(), rec.socketAddress);
        try {
            socket.send(sendPacket);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
        String toSend = "0" + us.playerID;
        DatagramPacket sendPacket = new DatagramPacket(toSend.getBytes(), toSend.length(), us.socketAddress);
        try {
            socket.send(sendPacket);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void SendMap(int[][] a, User u) {

        ByteArrayOutputStream msg = new ByteArrayOutputStream();
        msg.write((byte) '3');
        msg.write((byte) a.length);
        msg.write((byte) a[0].length);
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                msg.write((byte) a[i][j]);
            }
        }
        DatagramPacket sendMap = new DatagramPacket(msg.toByteArray(), msg.toByteArray().length, u.socketAddress);
        try {
            socket.send(sendMap);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void populateMap() {
        int n = 3;
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                arr[i][j] = n++ % 128;
            }
        }
    }
}


