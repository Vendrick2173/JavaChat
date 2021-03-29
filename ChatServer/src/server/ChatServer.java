package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class ChatServer {
    private int port;
    private Set<String> userNames = new HashSet<>();
    private Set<UserThread> userThreads = new HashSet<>();

    public ChatServer(int port) {
        this.port = port;
    }

    public void execute() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Chat Server is listening on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();//accept -- запрос на установку соединения
                System.out.println("New user connected");

                UserThread newUser = new UserThread(socket, this);
                userThreads.add(newUser);
                newUser.start();
            }
        } catch (IOException ex) {
            System.out.println("Error in the server: " + ex.getMessage());
            ex.printStackTrace();

        }
    }

    /*передача*/
    void broadcast(String message, UserThread excludeUser) {
        for (UserThread aUser : userThreads) {
            if (aUser != excludeUser) {
                aUser.sendMessage(message);
            }
        }
    }

    void addUserName(String userName) {
        userNames.add(userName);
    }

    void removeUser(String userName, UserThread aUser) {
        boolean isRemoved = userNames.remove(userName);
        if (isRemoved) {
            userThreads.remove(aUser);
            System.out.println("The user " + userName + " quitted");

        }
    }

    Set<String> getUserNames() {
        return this.userNames;
    }

    //проверка на наличие более одного польщователя(не считая текущего)
    boolean hasUsers() {
        return !this.userNames.isEmpty();
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Syntax: java server.ChatServer <port-number>");
            System.exit(0);
        }
        int port = Integer.parseInt(args[0]);

        ChatServer server = new ChatServer(port);
        server.execute();
    }

}
