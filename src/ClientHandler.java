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
    private int tmp;

    public ClientHandler(Socket asocket, int i) {
        try {
            this.socket = asocket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
            this.tmp = i;
            broadcast(clientUsername + " has join the chat!", clientUsername.split("µ")[1], tmp);
            tmp = 0;
        } catch (Exception e) {
            e.printStackTrace();
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    public void run() {
        String message;
        while (socket.isConnected()) {
            try {
                message = bufferedReader.readLine();
                if (message != null) {
                    if (message.contains(":") && message.split("µ", 2)[1].split(":", 2)[1].substring(1).contains(" ") && check_PrivateMsg(message.split("µ", 2)[1].split(":", 2)[1].substring(1).split(" ", 2)[0])) {
                        String[] whisper = message.split("µ")[1].split(":")[1].substring(2).substring(0).split(" ", 2); //destinataire et message
                        broadcast(message.split(":")[0] + " to you: " + whisper[1], whisper[0], 1);
                    }
                    else
                        broadcast(message, "0", 1);
                    if (message.contains("µ") && message.split("µ", 2)[1].equals(clientUsername.split("µ")[1] + " changed their color")) {
                        clientUsername = message.substring(0, message.indexOf("µ") + 1) + clientUsername.split("µ")[1];
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        closeAll(socket, bufferedReader, bufferedWriter);
    }

    public void broadcast(String message, String user, int i) {
        for (ClientHandler list : clientHandlers) {
            if (i == 0) {
                if (!user.equals(list.clientUsername.split("µ")[1])) {
                    try {
                        list.bufferedWriter.write(message);
                        list.bufferedWriter.newLine();
                        list.bufferedWriter.flush();
                    } catch (Exception e) {
                        closeAll(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
            else {
                if ((user.equals("0") || user.equals(list.clientUsername.split("µ")[1]))) {
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
    }

    public boolean check_PrivateMsg(String str) {
        for (int i = 0; i < clientHandlers.size(); i++) {
            if (str.equals("/" + clientHandlers.get(i).getUsername()) && !str.equals(clientUsername))
                return true;
        }
        return false;
    }

    public String getUsername () {
        return clientUsername.split("µ")[1];
    }

    public void closeAll (Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            clientHandlers.remove(this);
            if (dummy == 1) {
                broadcast(clientUsername + " has left the chat!", "0", 1);
            }
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
}
