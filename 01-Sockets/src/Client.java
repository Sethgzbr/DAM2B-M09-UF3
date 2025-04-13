import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static final int PORT = 7777;
    private static final String HOST = "localhost";
    private Socket socket;
    private PrintWriter out;

    public void conecta() {
        try {
            socket = new Socket(HOST, PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Connectat a servidor en " + HOST + ":" + PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enviaDades(String dades) {
        try {
            out.println(dades);
            System.out.println("Enviant al servidor: " + dades);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tanca() {
        try {
            out.close();
            socket.close();
            System.out.println("Client tancat.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.conecta();
        client.enviaDades("Prova d'enviament 1");
        client.enviaDades("Prova d'enviament 2");
        client.enviaDades("Adeu!");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Prem Enter per tancar el client...");
        try {
            in.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        client.tanca();
    }
}
