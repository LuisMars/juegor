package es.upv.luimafus;

import com.badlogic.gdx.Gdx;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;


public class Client extends Thread {

    private static DatagramSocket socket;
    private static String[] players = new String[100];
    private static int ownID = -1;
    private static InetSocketAddress address;
    private static WaitingScreen waitingScreen;


    public Client(WaitingScreen ws, String add) {
        waitingScreen = ws;
        String[] a = add.split(":"); //    0.0.0.0:1111
        address = new InetSocketAddress(a[0], Integer.parseInt(a[1]));

        try {
            socket = new DatagramSocket();
        } catch (SocketException socketException) {
            socketException.printStackTrace();
            System.exit(1);
        }
        sendLogin();
        start();
    }

    private static void displayMessage(String msg) {
        waitingScreen.print(msg);
    }

    public static void sendAction(int action) {
        byte[] msg = {(byte) action};
        sendMsg(4, msg);
    }

    public static void sendMsg(int code, byte[] msg) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            os.write(code);
            if (ownID != -1)
                os.write(ownID);
            os.write(msg);

            DatagramPacket sendPacket = new DatagramPacket(os.toByteArray(), os.toByteArray().length, address);
            if (code == 3)
                waitingScreen.print(code + " " + ownID + " " + new String(os.toByteArray()));
            socket.send(sendPacket);

        } catch (IOException ioException) {
            displayMessage(ioException.toString() + "\n");
            ioException.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                byte data[] = new byte[16384];
                DatagramPacket receivePacket = new DatagramPacket(data, data.length);

                socket.receive(receivePacket);

                //recibir mi ID
                if (receivePacket.getData()[0] == 0) {
                    ownID = receivePacket.getData()[1];
                }


                //recibir logins
                if (receivePacket.getData()[0] == 1) {
                    players[receivePacket.getData()[1]] = new String(receivePacket.getData(), 2, receivePacket.getLength());
                }

                /// recibir mensaje chat
                if (receivePacket.getData()[0] == 2) {
                    displayMessage(players[receivePacket.getData()[1]] + ": \t"
                            + new String(receivePacket.getData(), 1, receivePacket.getLength()));
                }


                // recibir mapa
                if (receivePacket.getData()[0] == 3) {
                    displayMessage("Map received");
                    byte[] d = receivePacket.getData();
                    int[][] arr = new int[d[1]][d[2]];
                    int speed = d[3];
                    int n = 4;
                    //String mapa = "";
                    for (int i = 0; i < arr.length; i++) {
                        for (int j = 0; j < arr[0].length; j++) {
                            arr[i][j] = d[n++];
                            //mapa += arr[i][j] + "\t";
                        }
                        //mapa += "\n";
                    }
                    //displayMessage(mapa);
                    Gdx.app.postRunnable(() -> waitingScreen.initialiceMap(arr, speed));

                }

                //recibir posiciones iniciales
                if (receivePacket.getData()[0] == 4) {
                    byte[] d = receivePacket.getData();
                    for (String s : players)
                        System.out.println(s);
                    for (int i = 0; i < d[1]; i++) {
                        final int finalI = i;
                        Gdx.app.postRunnable(() -> waitingScreen.addPlayer(d[2 + (finalI * 3)], players[d[3 + (finalI * 3)]], d[2 + (finalI * 3)], d[4 + (finalI * 3)], d[finalI] == ownID));
                        //waitingScreen.print(players[d[i]] + " joined the game");
                    }

                    Gdx.app.postRunnable(waitingScreen::startGame);
                }


            } catch (IOException exception) {
                displayMessage(exception.toString() + "\n");
                exception.printStackTrace();
            }
        }
    }

    public void sendLogin() {
        sendMsg(1, waitingScreen.name);
    }

    public void sendChatMsg(String msg) {
        sendMsg(2, msg);
    }

    public void requestMap() {
        sendMsg(3, "");
    }

    public void sendMsg(int code, String msg) {
        sendMsg(code, msg.getBytes());
    }
}