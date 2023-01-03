import java.net.*;
import com.formdev.flatlaf.FlatDarkLaf;

public class Chat {
    public static void main(String[] args) {
        int server = 0;
        try {
            if (!GetJson.getRun()) {
                ServerSocket serverSocket = new ServerSocket(8888);
                server = 1;
                GetJson.changeServer('y');
                new Server(serverSocket).start();
            }
        } catch (Exception e) {}
        FlatDarkLaf.setup();
        new frame(server);
    }
}