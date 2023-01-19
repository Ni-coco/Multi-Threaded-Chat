import com.google.gson.JsonObject;
import java.net.HttpURLConnection;
import com.google.gson.Gson;
import java.net.URL;
import java.io.*;

public class GetJson {

    static String IPv6 = "2a02:8429:813e:8f01:c54b:b717:e6a5:17a7";

    static public void changeServer(char a) {
        try {
            URL getJson = new URL("https://api.jsonbin.io/v3/b/63aa0933dfc68e59d5718784");
            String newiP = "{\"server_ip\": \""+IPv6+"\",\"run\": \""+a+"\"}";
            HttpURLConnection connection = (HttpURLConnection) getJson.openConnection();
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

    static public String getPublicIp() { 
        try {
            URL whatismyip = new URL("http://checkip.dyndns.org");
            BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            String ip = in.readLine();
            return ip.split("Current IP Address: ")[1].split("</body>")[0].trim();
        } catch (Exception e) {e.printStackTrace();}
        return "";
    }

    static public boolean getRun() {
        try {
            URL getJson = new URL("https://api.jsonbin.io/v3/b/63aa0933dfc68e59d5718784");
            HttpURLConnection connection = (HttpURLConnection) getJson.openConnection();
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
            URL getJson = new URL("https://api.jsonbin.io/v3/b/63aa0933dfc68e59d5718784");
            HttpURLConnection connection = (HttpURLConnection) getJson.openConnection();
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
