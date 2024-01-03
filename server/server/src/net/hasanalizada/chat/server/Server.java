package net.hasanalizada.chat.server;

import net.hasanalizada.chat.network.TCPConnection;
import net.hasanalizada.chat.network.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server implements TCPConnectionListener {
    public static void main(String[] args) {
        new Server();
    }

    private final ArrayList<TCPConnection> tcpConnections = new ArrayList<>();

    private Server() {
        System.out.println("Server is running!");
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            while (true) {
                try {
                    new TCPConnection(serverSocket.accept(), this);
                } catch (IOException e) {
                    System.out.println("TCPConnection exception: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        tcpConnections.add(tcpConnection);
        sentToAllConnections("Client connected: " + tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
        sentToAllConnections(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        tcpConnections.remove(tcpConnection);
        sentToAllConnections("Client disconnected: " + tcpConnection);

    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.print("TCPConnection exception: " + e);
    }

    private void sentToAllConnections(String value) {
        System.out.printf(value);
        for (TCPConnection tcpConnection : tcpConnections) {
            tcpConnection.sendString(value);
        }
    }
}
