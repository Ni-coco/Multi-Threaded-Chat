import java.net.*;
import java.io.*;

import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;
import java.net.URL;
import java.net.InetAddress;
import com.google.gson.JsonObject;
import com.google.gson.Gson;


import java.net.HttpURLConnection;
import java.net.URL;

public class Chat {
    public static void main(String[] args) throws IOException {
        try {
            ServerSocket serverSocket = new ServerSocket(8888);
            GetnChangeIp();
            new Server(serverSocket).start();
        } catch (Exception e) {}
        frame client = new frame();
        client.listen();
        client.sendEntered();
    }

    static public void GetnChangeIp() {
        try {
            URL ipAdress = new URL("https://api.jsonbin.io/v3/b/63aa0933dfc68e59d5718784");
            InetAddress inetAddress = InetAddress.getLocalHost();
            String newiP = "{\"server_ip\": \""+inetAddress.getHostAddress()+"\"}";
            HttpURLConnection connection = (HttpURLConnection) ipAdress.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("X-Access-Key", "$2b$10$QAfPKovzaXfK91VARTkrW.Dw0BxfpJiCs8DebJdaPFQG5uqq37f2O");
            connection.setDoOutput(true);
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(newiP.getBytes());
            outputStream.flush();
            outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}