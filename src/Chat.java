import java.net.*;
import java.util.Scanner;

public class Chat {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Entrez votre pseudo -> ");
        String user = scanner.nextLine();
        try {
            ServerSocket serverSocket = new ServerSocket(8888);
            System.out.println("Serveur crée");
            new Server(serverSocket).start();
        } catch (Exception e) {
            System.out.println("Vous êtes connecté!");
        }
        Client client = new Client(user);
        client.listen();
        client.sendMessage();
        scanner.close();
    }
}
