import java.io.*;
import java.util.ArrayList;
import java.net.*;

public class ClientHandler extends Thread {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private String clientUsername;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public ClientHandler (Socket asocket) {
        try {
            this.socket = asocket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
            broadcast(clientUsername + " a rejoint le serveur!");
        } catch (Exception e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    public void run() {
        String message;
        while (socket.isConnected()) {
            try {
                message = bufferedReader.readLine();
                broadcast(message);
            } catch (Exception e) {
                closeAll(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    } 

    public void broadcast(String message) {
        for (ClientHandler list : clientHandlers) {
            try {
                if (!list.clientUsername.equals(clientUsername)) {
                    list.bufferedWriter.write(message);
                    list.bufferedWriter.newLine();
                    list.bufferedWriter.flush();
                }
            } catch (Exception e) {
                closeAll(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void closeAll (Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            clientHandlers.remove(this);
            broadcast(clientUsername + " a quitté le serveur!");
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
}
