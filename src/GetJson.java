import com.google.gson.JsonObject;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import com.google.gson.Gson;
import java.net.URL;
import java.io.*;

public class GetJson {

    static public void changeServer(char a) {
        try {
            URL ipAdress = new URL("https://api.jsonbin.io/v3/b/63aa0933dfc68e59d5718784");
            InetAddress inetAddress = InetAddress.getLocalHost();
            String newiP = "{\"server_ip\": \""+inetAddress.getHostAddress()+"\",\"run\": \""+a+"\"}";
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

    static public boolean getRun() {
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

    static public String getIP() {
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
            return data.get("server_ip").toString().replaceAll("\"", "");
        } catch (Exception e) {e.printStackTrace();}
        return "";
    }
}