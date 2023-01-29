import com.google.gson.JsonObject;
import java.net.HttpURLConnection;
import com.google.gson.Gson;
import java.net.URL;
import java.io.*;

public class GetJson {

    static public void setServer(char a) {
        try {
            URL getJson = new URL("https://api.jsonbin.io/v3/b/63aa0933dfc68e59d5718784");
            String newiP = "{\"server_ip\": \""+getIPv6()+"\",\"run\": \""+a+"\"}";
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

    static public String getIPv4() { 
        try {
            URL whatismyip = new URL("https://api4.ipify.org/");
            BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            return in.readLine();
        } catch (Exception e) {e.printStackTrace();}
        return "";
    }

    static public String getIPv6() { 
        try {
            URL whatismyip = new URL("https://api6.ipify.org/");
            BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            return in.readLine();
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

    static public int getBestScore(int score) {
        try {
            URL getJson = new URL("https://api.jsonbin.io/v3/b/63d6ba34ebd26539d06c73c9");
            HttpURLConnection connection = (HttpURLConnection) getJson.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Access-Key", "$2b$10$QAfPKovzaXfK91VARTkrW.Dw0BxfpJiCs8DebJdaPFQG5uqq37f2O");
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String tmp = reader.readLine();
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(tmp, JsonObject.class);
            JsonObject data = json.get("record").getAsJsonObject();
            int record = Integer.parseInt(data.get("best").toString().replaceAll("\"", ""));
            if (record < score) {
                setBestScore(score);
                return score;
            }
            else
                return record;
        } catch (Exception e) {e.printStackTrace();}
        return 0;
    }

    static public void setBestScore(int score) {
        try {
            URL getJson = new URL("https://api.jsonbin.io/v3/b/63d6ba34ebd26539d06c73c9");
            HttpURLConnection connection = (HttpURLConnection) getJson.openConnection();
            String newRecord = "{\"best\": \""+Integer.toString(score)+"\"}";
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("X-Access-Key", "$2b$10$QAfPKovzaXfK91VARTkrW.Dw0BxfpJiCs8DebJdaPFQG5uqq37f2O");
            connection.setDoOutput(true);
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(newRecord.getBytes());
            outputStream.flush();
            outputStream.close();
            int reponse = connection.getResponseCode();
        } catch (Exception e) {e.printStackTrace();}
    }
}
