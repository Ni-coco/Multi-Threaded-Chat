import java.util.ArrayList;
import java.net.*;
import java.io.*;

public class ClientHandler extends Thread {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private String clientUsername;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private int tic = 0;
    private int dummy = 1;
    private int tmp;

    public ClientHandler(Socket asocket, int i) {
        try {
            this.socket = asocket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            while (checkUser(bufferedReader.readLine())) {
                bufferedWriter.write("take");
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
            clientHandlers.add(this);
            this.tmp = i;
            broadcast(clientUsername + " has join the chat!", clientUsername.split("µ")[1], tmp);
            tmp = 0;
            firstTic();
        } catch (Exception e) {
            e.printStackTrace();
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    public boolean checkUser(String str) {
        for (int i = 0; i < clientHandlers.size(); i++) {
            if (str.contains("µ") && str.split("µ")[1].equals(clientHandlers.get(i).getUsername()))
                return true;
        }
        clientUsername = str;
        return false;
    }

    public void run() {
        String message;
        while (socket.isConnected()) {
            try {
                message = bufferedReader.readLine();
                System.out.println("Server ="+message);
                if (message.equals("/tic--"))
                    mergeTic(-1);
                else if (message.equals("/tic++"))
                    mergeTic(1);
                else if (message != null && !message.equals(clientUsername)) {
                    if (message.contains(":") && message.split("µ", 2)[1].split(":", 2)[1].substring(1).contains(" ") && check_PrivateMsg(message.split("µ", 2)[1].split(":", 2)[1].substring(1).split(" ", 2)[0])) {
                        String[] whisper = message.split("µ")[1].split(":")[1].substring(2).substring(0).split(" ", 2); //destinataire et message
                        broadcast(message.split(":")[0] + " to you: " + whisper[1], whisper[0], 1);
                    }
                    else if (message.contains("//") && message.split("//")[0].equals("(MsgHistoric=) ")) {
                        broadcast(message, message.split("//")[message.split("//").length - 1].split("µ", 2)[1].split(" ", 2)[0], 1); //destinataire
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

    public void firstTic() {
        for (int i = 0; i < clientHandlers.size(); i++)
            if (clientUsername.split("µ")[1] != clientHandlers.get(i).getUsername()) {
                if (tic != clientHandlers.get(i).getTic()) {
                    tic = clientHandlers.get(i).getTic();
                    broadcast("/tic=µ"+tic, clientUsername.split("µ")[1], 1);
                }
            }
    }

    public void mergeTic(int i) {
        tic += i;
        for (ClientHandler list : clientHandlers)
            list.setTic(tic);
        if (i == -1)
            broadcast("/tic--", "0", 1);
        if (i == 1)
            broadcast("/tic++", "0", 1);
        if (tic == 2)
            broadcast("/ticfull", "0", 1); //On envoi /ticfull pour tout le monde
    }

    public void setTic(int i) {
        tic = i;
    }

    public int getTic() {
        return tic;
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
