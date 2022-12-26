import java.net.*;
import java.io.*;

public class Chat {
    public static void main(String[] args) throws IOException {
        try {
            ServerSocket serverSocket = new ServerSocket(8888);
            new Server(serverSocket).start();
        } catch (Exception e) {}
        frame client = new frame();
        client.listen();
        client.sendEntered();
    }
}
