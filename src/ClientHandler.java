import java.util.ArrayList;
import java.net.*;
import java.io.*;

public class ClientHandler extends Thread {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private String clientUsername;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private int dummy = 1;

    public ClientHandler (Socket asocket) {
        try {
            this.socket = asocket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
            broadcast(clientUsername + " has join the chat!", "0");
        } catch (Exception e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    public void run() {
        String message;
        while (socket.isConnected()) {
            try {
                message = bufferedReader.readLine();
                if (message.contains("µ") && message.split("µ")[1].split(":")[1].charAt(1) == '/') {
                    String[] whisper = message.split("µ")[1].split(":")[1].substring(2).substring(0).split(" ", 2); //destinataire et message
                    if (String.join("", clientHandlers.get(0).getAllUsername()).contains(whisper[0]));
                        broadcast(message.split(":")[0] + " to you: " + whisper[1], whisper[0]);
                }
                else
                    broadcast(message, "0");
            } catch (Exception e) {
                e.printStackTrace();
                closeAll(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    } 

    public void closeAll (Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            clientHandlers.remove(this);
            if (dummy == 1)
                broadcast(clientUsername + " has left the chat!", "0");
            dummy = 0;
            if (bufferedReader != null)
                bufferedReader.close();
            if (bufferedWriter != null)
                bufferedWriter.close();
            if (socket != null)
                socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void broadcast(String message, String user) {
        for (ClientHandler list : clientHandlers) {
            if (user == "0" || user.equals(list.clientUsername.split("µ")[1])) {
                try {
                    list.bufferedWriter.write(message);
                    list.bufferedWriter.newLine();
                    list.bufferedWriter.flush();
                } catch (Exception e) {
                    closeAll(socket, bufferedReader, bufferedWriter);
                }
            }
        }
    }

    public String getUsername () {
        return clientUsername.split("µ")[1];
    }

    public String getAllUsername() {
        String str = "";
        for (int i = 0; i < clientHandlers.size(); i++)
            str += clientHandlers.get(i).getUsername();
        return str;
    }
}
