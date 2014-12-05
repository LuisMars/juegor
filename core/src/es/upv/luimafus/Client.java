package es.upv.luimafus;

import java.io.*;
import java.net.*;


public class Client extends Thread {

    private static DatagramSocket socket;
    private static String[] players = new String[100];
    private static int ownID = -1;
    private InetSocketAddress address;
    private WaitingScreen waitingScreen;

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


    public void run() {
        while (true) {
            try {
                byte data[] = new byte[1024];
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
                    byte[] d = receivePacket.getData();
                    int[][] arr = new int[d[1]][d[2]];
                    int n = 3;
                    String mapa = "";
                    for (int i = 0; i < arr.length; i++) {
                        for (int j = 0; j < arr[0].length; j++) {
                            arr[i][j] = d[n++];
                            mapa += arr[i][j] + "\t";
                        }
                        mapa += "\n";
                    }

                    displayMessage(mapa);
                }


            } catch (IOException exception) {
                displayMessage(exception.toString() + "\n");
                exception.printStackTrace();
            }
        }
    }


    private void displayMessage(String msg) {
        waitingScreen.print(msg);
    }


    public void sendLogin() {
        sendMsg(1, waitingScreen.name);
    }


    public void sendChatMsg(String msg) {

        sendMsg(2,msg);
    }

    public void sendMsg(int code, String msg) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            os.write(code);
            if(ownID != -1)
                os.write(ownID);
            os.write(msg.getBytes());

            DatagramPacket sendPacket = new DatagramPacket(os.toByteArray(), os.toByteArray().length, address);
            //if(code == 2)
            //    waitingScreen.print(new String(os.toByteArray()));
            socket.send(sendPacket);

        } catch (IOException ioException) {
            displayMessage(ioException.toString() + "\n");
            ioException.printStackTrace();
        }
    }

    public void requestMap() {

    }

}