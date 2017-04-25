import java.io.IOException;
import java.util.Scanner;

import json.JsonStatus;
import json.JsonUsers;
import model.Message;


public class Main {
    private static Session session;
    private static String login;
    private static final String[] commands = {
            "!users (get list of users)",
            "!status {ONLINE,AFK,OFFLINE} (change status)",
            "!room roomName (create/join to room)",
            "!check username (check user's status)",
            "!w username text (send private message)"
    };

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Enter your login: ");
            login = scanner.nextLine();
            System.out.println("Enter your password: ");
            String pass = scanner.nextLine();

            session = Session.getInstance(login, pass);
            int respStatus = session.changeStatusTo("ONLINE");
            if (respStatus != 200) handleCritErrorsResp(respStatus);

            System.out.println("Connected");
            Thread th = new Thread(new GetThread(session));
            th.setDaemon(true);
            th.start();

            while (true) {
                String text = scanner.nextLine();
                if (text.isEmpty()) {
                    session.changeStatusTo("OFFLINE");
                    break;
                }
                if (text.startsWith("!")) {
                    handleCommand(text);
                } else {
                    Message message = new Message(login, "all", text);
                    int status = session.sendMessage(message);
                    if (status != 200) handleCritErrorsResp(status);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void handleCritErrorsResp(int respStatus) {
        //System.out.println("Cannot login: " + respStatus);
        switch (respStatus) {
            case 404:
                System.out.println("There is no such user");
                break;
            case 401:
                System.out.println("Wrong password");
                break;
            case 400:
                System.out.println("Invalid argument");
                break;
            default:
                System.out.println("Unexpected exception: " + respStatus);
        }
        System.exit(0);
    }

    private static void handleCommand(String text) throws Exception {
        if (text.equals("!users")) {
            printUsersList();
        } else if (text.equals("!help")) {
            printAllCommands();
        } else if (text.matches("^!room \\w+$")) {
            joinChatRoom(text);
        } else if (text.matches("^!check \\w+$")) {
            checkUserStatus(text);
        } else if (text.matches("^!status \\w+$")) {
            changeOnlineStatus(text);
        } else if (text.matches("^!w \\w+ .+$")) {
            sendPrivateMessage(text);
        } else {
            System.out.println("No such command, try !help");
        }
    }

    private static void sendPrivateMessage(String text) throws IOException {
        final String[] values = text.split(" ", 3);
        String username = values[1];
        String messageText = values[2];
        Message message = new Message(login, username, messageText);
        int respStatus = session.sendMessage(message);
        if (respStatus == 404) System.out.println("User offline/doesn't exist");
        else if (respStatus != 200) handleCritErrorsResp(respStatus);
    }

    private static void changeOnlineStatus(String text) throws Exception {
        final String[] values = text.split(" ", 2);
        String status = values[1];
        int respStatus = session.changeStatusTo(status);
        if (respStatus == 400) System.out.println("Wrong status value, try !help");
        else if (respStatus != 200) handleCritErrorsResp(respStatus);
        System.out.println("status changed to: " + status);
    }

    private static void checkUserStatus(String text) throws Exception {
        final String[] values = text.split(" ", 2);
        String username = values[1];
        JsonStatus status = session.checkUserStatus(username);
        if (status == null) System.out.println("User not found: " + username);
        else System.out.println(String.format(
                "%s is %s", status.getUsername(), status.getStatus()));
    }

    private static void joinChatRoom(String text) throws Exception {
        final String[] values = text.split(" ", 2);
        String roomName = values[1];
        int respStatus = session.joinChatRoom(roomName);
        if (respStatus == 201) System.out.println("room created");
        else if (respStatus == 409) System.out.println("room already exist");
        else handleCritErrorsResp(respStatus);
        System.out.println("joined room " + roomName);
    }

    private static void printAllCommands() {
        for (int i = 0; i < commands.length; i++)
            System.out.println(commands[i]);
    }

    private static void printUsersList() throws Exception {
        JsonUsers users = session.getUsers();
        for (String username : users.getNames())
            System.out.println(username);
    }
}
