package com.yhh.practice.chatroom.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserInputHandler implements Runnable {

    private ChatClient chatClient;

    public UserInputHandler(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public void run() {
        BufferedReader consoleReader = null;
        try {
            consoleReader = new BufferedReader(
                    new InputStreamReader(System.in)
            );
            while (true) {
                String input = consoleReader.readLine();
                chatClient.send(input);
                if (chatClient.readyToQuit(input)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (consoleReader!=null) {
                try {
                    consoleReader.close();
                    chatClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
