package com.yhh.practice.chatroom.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {

    private static final int PORT = 666;

    private static final String QUIT = "quit";

    private ServerSocket serverSocket;

    private final Map<Integer, Writer> connectedClients = new HashMap<>();

    public synchronized void addClient(Socket socket) throws IOException {
        if (socket != null) {
            int port = socket.getPort();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            connectedClients.put(port, writer);
            System.out.println("客户端 [" + port + "]: 已连接");
        }
    }

    public synchronized void removeClient(Socket socket) throws IOException {
        if (socket != null) {
            int port = socket.getPort();
            if (connectedClients.containsKey(port)) {
                connectedClients.get(port).close();
                connectedClients.remove(port);
                socket.close();
                System.out.println("客户端 [" + port + "]: 已断开");
            }
        }
    }

    public boolean readyToQuit(String msg) {
        return QUIT.equals(msg);
    }

    public void forwardMessage(Socket socket, String fwMsg) throws IOException {
        for (Integer port : connectedClients.keySet()) {
            if (!port.equals(socket.getPort())) {
                Writer writer = connectedClients.get(port);
                writer.write(fwMsg);
                writer.flush();
            }
        }
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("服务端已启动，监听端口：" + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ChatHandler(this, socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public synchronized void close() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
                System.out.println("服务端已关闭");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        chatServer.start();
    }

}
