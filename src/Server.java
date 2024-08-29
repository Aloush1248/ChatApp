import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server extends Thread {
    private static final int PORT = 9999;
    private static ArrayList<ClientHandler> clients = new ArrayList<>();

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server is running and waiting for connections...");

            // accept connections
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                // create a new client handler for the connected client
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client: clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;

            try {
                // create in and out streams
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                // get username from client
                username = getUsername();
                System.out.println("User " + username + " connected!");
                out.println("Welcome to the chat, " + username + "!");
                out.println("Type Your Message");
                String inputLine;

                // receives and broadcasts messages from client
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("[" + username + "]: " + inputLine);
                    broadcast("[" + username + "]: " + inputLine, this);
                }

                // remove the client handler from the list
                clients.remove(this);

                //close resources
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // gets username from client
        public String getUsername() throws IOException {
            out.println("Enter your username:");
            return in.readLine();
        }

        // sends messages to clients
        public void sendMessage(String message) {
            out.println(message);
            out.println("Type a message");
        }
    }

    public static void main(String args[]) {
        Server server = new Server();
        server.start();
    }
}
