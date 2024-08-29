import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client extends Thread {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 9999;
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;

    @Override
    public void run() {
        try (Scanner scanner = new Scanner(System.in)) {
            // setting up the socket, as well as the in and out streams
            clientSocket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            //starting the inputHandler in a seperate thread
            InputHandler inHandler = new InputHandler();
            inHandler.start();

            //Scanning for user input to output to the server
            while (true) {
                String userInput = scanner.nextLine();
                out.println(userInput);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private class InputHandler extends Thread {

        @Override
        // handling input from the server
        public void run() {
            try {
                String serverResponse;
                while ((serverResponse = in.readLine()) != null) {
                    System.out.println(serverResponse);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]) {
        Client client = new Client();
        client.start();
    }
}
