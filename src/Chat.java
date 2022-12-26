import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.*;
import java.io.*;

import com.google.gson.JsonObject;
import com.google.gson.Gson;

public class Chat {
    public static void main(String[] args) {
        int server = 0;
        try {
            if (!checkRun()) {
                ServerSocket serverSocket = new ServerSocket(8888);
                server = 1;
                changeServer();
                new Server(serverSocket).start();
            }
        } catch (Exception e) {}
        frame client = new frame(server);
        client.listen();
        client.sendEntered();
    }

    static public void changeServer() {
        try {
            URL ipAdress = new URL("https://api.jsonbin.io/v3/b/63aa0933dfc68e59d5718784");
            InetAddress inetAddress = InetAddress.getLocalHost();
            String newiP = "{\"server_ip\": \""+inetAddress.getHostAddress()+"\",\"run\": \"y\"}"; //changer n en o pour ne pas recréer le serveur.
            HttpURLConnection connection = (HttpURLConnection) ipAdress.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("X-Access-Key", "$2b$10$QAfPKovzaXfK91VARTkrW.Dw0BxfpJiCs8DebJdaPFQG5uqq37f2O");
            connection.setDoOutput(true);
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(newiP.getBytes());
            outputStream.flush();
            outputStream.close();
            int reponse = connection.getResponseCode();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
    static public boolean checkRun() {
        try {
            URL ipAdress = new URL("https://api.jsonbin.io/v3/b/63aa0933dfc68e59d5718784");
            HttpURLConnection connection = (HttpURLConnection) ipAdress.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Access-Key", "$2b$10$QAfPKovzaXfK91VARTkrW.Dw0BxfpJiCs8DebJdaPFQG5uqq37f2O");
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String tmp = reader.readLine();
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(tmp, JsonObject.class);
            JsonObject data = json.get("record").getAsJsonObject();
            if (!data.get("run").getAsString().equals("y"))
                return false;
            return true;
        } catch (Exception e) {e.printStackTrace();}
        return false;
    }
}