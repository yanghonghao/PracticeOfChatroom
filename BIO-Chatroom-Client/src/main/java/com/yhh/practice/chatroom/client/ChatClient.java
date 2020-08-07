package com.yhh.practice.chatroom.client;

import java.io.*;
import java.net.Socket;

public class ChatClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 666;
    private static final String QUIT = "quit";

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    public void send(String msg) throws IOException {
        if (msg != null) {
            writer.write(msg + "\n");
            writer.flush();
        }
    }

    public String receive() throws IOException {
        String msg = null;
        if (!socket.isInputShutdown()) {
            msg = reader.readLine();
        }
        return msg;
    }

    public boolean readyToQuit(String msg) {
        return false;
    }

    public void start() {
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            writer = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())
            );
            reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            new Thread(new UserInputHandler(this)).start();

            String msg;
            while ((msg = receive()) != null) {
                System.out.println(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public void close() {
        try {
            if (writer != null) {
                writer.close();
            }
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient();
        chatClient.start();
    }

}
