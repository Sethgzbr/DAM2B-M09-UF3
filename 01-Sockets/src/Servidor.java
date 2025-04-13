import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    private static final int PORT = 7777;
    private static final String HOST = "localhost";
    private ServerSocket serverSocket;
    private Socket socket;

    public void conecta() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor en marxa a " + HOST +":"+ PORT);
            System.out.println("Esperant connexions a " + HOST + ":" + PORT);
            socket = serverSocket.accept();
            System.out.println("Client conectat: " + socket.getRemoteSocketAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void obteDades() {
        try(BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String line;
            while((line = in.readLine()) != null) {
                System.out.println("Rebut: " + line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tanca() {
        try {
            socket.close();
            serverSocket.close();
            System.out.println("Servidor tancat.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        servidor.conecta();
        servidor.obteDades();
        servidor.tanca();
    }
}