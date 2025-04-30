package Xat_a_dos;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ServidorXat {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private static final String MSG_SORTIR = "sortir";
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public void iniciarServidor() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
    }
    public static String getMissatge(){return MSG_SORTIR;}
    public void pararServidor() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
            System.out.println("Servidor aturat.");
        }
    }

    public String getNom(ObjectInputStream input, ObjectOutputStream output) throws IOException, ClassNotFoundException {
        output.writeObject("Escriu el teu nom:");
        return (String) input.readObject();
    }

    public static void main(String[] args) {
        ServidorXat servidor = new ServidorXat();
        try {
            servidor.iniciarServidor();
            servidor.clientSocket = servidor.serverSocket.accept();
            System.out.println("Client connectat: " + servidor.clientSocket.getRemoteSocketAddress());

            servidor.output = new ObjectOutputStream(servidor.clientSocket.getOutputStream());
            servidor.input = new ObjectInputStream(servidor.clientSocket.getInputStream());

            String nomClient = servidor.getNom(servidor.input, servidor.output);
            System.out.println("Nom rebut: " + nomClient);

            FilServidorXat fil = new FilServidorXat(servidor.input);
            Thread thread = new Thread(fil);
            thread.start();
            System.out.println("Fil de xat creat. Fil de " + nomClient + " iniciat");
            System.out.println("Fil de " + nomClient + " iniciat");

            Scanner scanner = new Scanner(System.in);
            String missatge;
            do {
                System.out.print("Missatge ('sortir' per tancar): ");
                missatge = scanner.nextLine();
                servidor.output.writeObject(missatge);
            } while (!missatge.equalsIgnoreCase(MSG_SORTIR));

            thread.join();
            scanner.close();
            servidor.clientSocket.close();
            servidor.pararServidor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}