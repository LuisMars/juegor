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
    int[][] arr = null;
    boolean[] linesReceived = null;
    boolean mapReceived = false;
    int speed;

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

    private static void print(String msg) {
        waitingScreen.print(msg);
    }

    public static void iHaveMap() {
        sendMsg(5, "");
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
                print(code + " " + ownID + " " + new String(os.toByteArray()));
            socket.send(sendPacket);

        } catch (IOException ioException) {
            print(ioException.toString() + "\n");
            ioException.printStackTrace();
        }
    }

    public static void sendMsg(int code, String msg) {
        sendMsg(code, msg.getBytes());
    }

    public void run() {
        while (true) {
            try {
                byte data[] = new byte[60000];
                DatagramPacket receivePacket = new DatagramPacket(data, data.length);

                socket.receive(receivePacket);

                //recibir mi ID
                switch (receivePacket.getData()[0]) {
                    case 0: {
                        ownID = receivePacket.getData()[1];
                        break;
                    }
                    //recibir logins
                    case 1: {
                        players[receivePacket.getData()[1]] = new String(receivePacket.getData(), 2, receivePacket.getLength());
                        break;
                    }

                    /// recibir mensaje chat
                    case 2: {
                        print(players[receivePacket.getData()[1]] + ": \t"
                                + new String(receivePacket.getData(), 1, receivePacket.getLength()));
                        break;
                    }

                    // recibir mapa 2
                    case 3: {
                        if (!mapReceived) {
                            byte[] d = receivePacket.getData();
                            if (arr == null) {
                                arr = new int[d[1]][d[2]];
                                linesReceived = new boolean[d[1]];
                                speed = d[3];
                            }
                            if (linesReceived[d[4]])
                                break;
                            int n = 5;
                            int i = d[4];
                            linesReceived[d[4]] = true;

                            for (int j = 0; j < arr[0].length; j++) {
                                arr[i][j] = d[n++];
                            }
                            mapReceived = true;
                            for (Boolean b : linesReceived) {
                                if (!b) {
                                    mapReceived = false;
                                    break;
                                }
                            }
                        }
                        if (mapReceived)
                            Gdx.app.postRunnable(() -> waitingScreen.initializeMap(arr, speed));
                        break;
                    }
                    /*// recibir mapa 1
                    case 30: {
                        print("Map received.\nWaiting for other players to connect.");
                        byte[] d = receivePacket.getData();
                        int[][] arr = new int[d[1]][d[2]];
                        int speed = d[3];
                        int n = 4;
                        for (int i = 0; i < arr.length; i++) {
                            for (int j = 0; j < arr[0].length; j++) {
                                arr[i][j] = d[n++];
                            }
                        }
                        Gdx.app.postRunnable(() -> waitingScreen.initializeMap(arr, speed));
                        break;
                    }*/
                    //recibir posiciones iniciales
                    case 4: {
                        print("Initializing...");
                        byte[] d = receivePacket.getData();
                        for (int i = 0; i < d[1]; i++) {
                            final int finalI = i * 4;

                            Gdx.app.postRunnable(() ->
                                    waitingScreen.addPlayer(
                                            d[2 + finalI],
                                            players[d[2 + finalI]],
                                            d[3 + finalI],
                                            d[4 + finalI],
                                            d[5 + finalI],
                                            d[2 + finalI] == ownID));
                            print(players[d[2 + finalI]] + " is ready.");
                        }

                        Gdx.app.postRunnable(waitingScreen::startGame);
                        break;
                    }
                    //recibir estado
                    case 5: {
                        byte[] d = receivePacket.getData();
                        int n = d[1]; //n players
                        for (int i = 0; i < n; i++) {
                            final int finalI = i * 6;
                            Gdx.app.postRunnable(() ->
                                    waitingScreen.setPlayer(
                                            d[2 + finalI],
                                            d[3 + finalI],
                                            d[4 + finalI],
                                            d[5 + finalI],
                                            d[6 + finalI],
                                            d[7 + finalI]));
                        }
                        break;
                    }

                    case 6: {
                        Gdx.app.postRunnable(waitingScreen::endGame);
                        break;
                    }
                    case 7: {
                        byte[] d = receivePacket.getData();
                        Gdx.app.postRunnable(() -> {
                            if (d[1] == ownID)
                                print("Restarting....");
                            waitingScreen.resetPlayer(d[1]);
                            waitingScreen.startGame();
                        });
                    }
                }
            } catch (IOException exception) {
                print(exception.toString() + "\n");
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


}