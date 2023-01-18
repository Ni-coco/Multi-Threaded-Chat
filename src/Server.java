import java.net.*;

public class Server extends Thread {
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void run() {
        int i = 1;
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                new ClientHandler(socket, i).start();
                i = 0;
            }
        } catch (Exception e) {
            GetJson.changeServer('n');
            closeServerSocket();
        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null)
                serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
