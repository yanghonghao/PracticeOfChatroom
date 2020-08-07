package com.yhh.practice.chatroom.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ChatHandler implements Runnable {

    private final ChatServer chatServer;

    private final Socket socket;

    public ChatHandler(ChatServer chatServer, Socket socket) {
        this.chatServer = chatServer;
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        try {
            // TODO 存储新上线用户
            chatServer.addClient(socket);
            // TODO 读取用户发送的消息
            reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            String msg;
            while ((msg = reader.readLine()) != null) {
                String fwMsg = "客户端[" + socket.getPort() + "]: " + msg + "\n";
                System.out.print(fwMsg);
                chatServer.forwardMessage(socket, fwMsg);
                if (chatServer.readyToQuit(msg)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                chatServer.removeClient(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
