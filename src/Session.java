import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import json.ChangeStatusRequest;
import json.CreateChatRoomRequest;
import json.JsonMessages;
import json.JsonStatus;
import model.Message;
import json.JsonUsers;
import utils.Constants;
import utils.ParseUtils;

/**
 * Created by arahis on 4/20/17.
 */
public class Session {
    private static Session instance;
    private final String url = "http://127.0.0.1";
    private final int port = 8080;
    private final String username;
    private final String userpass;
    private int fromIndex;

    private Session(String username, String userpass) {
        this.username = username;
        this.userpass = userpass;
    }

    public static synchronized Session getInstance(String username, String userpass) {
        if (instance == null)
            instance = new Session(username, userpass);
        return instance;
    }

    public int sendMessage(Message message) throws IOException {
        URL obj = new URL(getURL() + "/chat");
        HttpURLConnection http = (HttpURLConnection) obj.openConnection();

        http.addRequestProperty(Constants.AUTHORIZATION_HEADER, "Basic " + getEncAuth());
        http.setRequestMethod("PUT");
        http.setDoOutput(true);

        try (OutputStream os = http.getOutputStream()) {
            String json = ParseUtils.toJson(message, Message.class);
            os.write(json.getBytes(StandardCharsets.UTF_8));
            return http.getResponseCode();
        }
    }

    public int joinChatRoom(String name) throws Exception {
        URL obj = new URL(getURL() + "/chat");
        HttpURLConnection http = (HttpURLConnection) obj.openConnection();

        http.addRequestProperty(Constants.AUTHORIZATION_HEADER, "Basic " + getEncAuth());
        http.setRequestMethod("POST");
        http.setDoOutput(true);

        try (OutputStream os = http.getOutputStream()) {
            String json = ParseUtils.toJson(
                    new CreateChatRoomRequest(name), CreateChatRoomRequest.class);
            os.write(json.getBytes(StandardCharsets.UTF_8));
            fromIndex = 0;
            return http.getResponseCode();
        }
    }

    public JsonUsers getUsers() throws IOException {
        URL obj = new URL(getURL() + "/users");
        HttpURLConnection http = (HttpURLConnection) obj.openConnection();

        http.addRequestProperty(Constants.AUTHORIZATION_HEADER, "Basic " + getEncAuth());
        http.setRequestMethod("GET");

        try (InputStream is = http.getInputStream()) {
            byte[] buf = requestBodyToArray(is);
            String strBuf = new String(buf, StandardCharsets.UTF_8);
            JsonUsers users = ParseUtils.fromJson(strBuf, JsonUsers.class);
            return users;
        }
    }

    public JsonMessages getMessages() throws IOException {
        URL obj = new URL(getURL() + "/chat?from=" + fromIndex);
        HttpURLConnection http = (HttpURLConnection) obj.openConnection();

        http.addRequestProperty(Constants.AUTHORIZATION_HEADER, "Basic " + getEncAuth());
        http.setRequestMethod("GET");

        try (InputStream is = http.getInputStream()) {
            byte[] buf = requestBodyToArray(is);
            String strBuf = new String(buf, StandardCharsets.UTF_8);
            //System.out.println("received json from server: " + strBuf);
            JsonMessages messages = ParseUtils.fromJson(strBuf, JsonMessages.class);
            fromIndex += messages.getList().size();
            return messages;
        }
    }

    public JsonStatus checkUserStatus(String name) throws Exception {
        URL obj = new URL(getURL() + "/status?name=" + name);
        HttpURLConnection http = (HttpURLConnection) obj.openConnection();

        http.addRequestProperty(Constants.AUTHORIZATION_HEADER, "Basic " + getEncAuth());
        http.setRequestMethod("GET");

        try (InputStream is = http.getInputStream()) {
            byte[] buf = requestBodyToArray(is);
            String strBuf = new String(buf, StandardCharsets.UTF_8);
            return ParseUtils.fromJson(strBuf, JsonStatus.class);
        } catch (FileNotFoundException ex) {
            return null;
        }
    }

    public int changeStatusTo(String status) throws Exception {
        URL obj = new URL(getURL() + "/status");
        HttpURLConnection http = (HttpURLConnection) obj.openConnection();

        http.addRequestProperty(Constants.AUTHORIZATION_HEADER, "Basic " + getEncAuth());
        http.setRequestMethod("PUT");
        http.setDoOutput(true);

        try (OutputStream os = http.getOutputStream()) {
            String json = ParseUtils.toJson(
                    new ChangeStatusRequest(status), ChangeStatusRequest.class);
            os.write(json.getBytes(StandardCharsets.UTF_8));
            return http.getResponseCode();
        }
    }

    private String getEncAuth() {
        String authString = username + ":" + userpass;
        byte[] authEncBytes = Base64.getEncoder().encode(authString.getBytes());
        String authStringEnc = new String(authEncBytes);
        return authStringEnc;
    }

    private String getURL() {
        return url + ":" + port;
    }

    private byte[] requestBodyToArray(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[10240];
        int r;

        do {
            r = is.read(buf);
            if (r > 0) bos.write(buf, 0, r);
        } while (r != -1);

        return bos.toByteArray();
    }
}
