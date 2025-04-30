import java.io.*;
import java.net.*;

public class ClientXat {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    public ClientXat(String serverAddress, int port) {
        try {
            // Connect to the server
            socket = new Socket(serverAddress, port);
            System.out.println("Connected to the server.");

            // Set up input and output streams
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("Error connecting to the server: " + e.getMessage());
        }
    }

    public void sendMessage(String message) {
        if (output != null) {
            output.println(message);
        }
    }

    public String receiveMessage() {
        try {
            if (input != null) {
                return input.readLine();
            }
        } catch (IOException e) {
            System.err.println("Error reading message: " + e.getMessage());
        }
        return null;
    }

    public void close() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }

    /*public static void main(String[] args) {
        ClientXat client = new ClientXat("localhost", 12345);
        client.sendMessage("Hello, Server!");
        System.out.println("Server says: " + client.receiveMessage());
        client.close();
    }*/
}
