import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class ServidorXat {
    public static final int PORT = 9999;
    public static final String HOST = "localhost";
    public static final String MSG_SORTIR = "sortir";

    private Hashtable<String, GestorClients> clients = new Hashtable<>();
    private boolean sortir = false;
    private ServerSocket serverSocket;

    public void servidorAEscoltar() throws IOException {
        serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(HOST, PORT));
        System.out.println("Servidor iniciat a " + HOST + ":" + PORT);

        while (!sortir) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connectat: " + clientSocket.getRemoteSocketAddress());
            GestorClients gestor = new GestorClients(clientSocket, this);
            new Thread(gestor).start();
        }
    }

    public void pararServidor() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }

    public void finalitzarXat() {
        enviarMissatgeGrup(Missatge.getMissatgeSortirTots(MSG_SORTIR));
        System.out.println("DEBUG: multicast sortir");
        clients.clear();
        System.out.println("Tancant tots els clients.");
        System.exit(0);
    }


    public synchronized void afegirClient(GestorClients gestor) {
        String nom = gestor.getNom();
        clients.put(nom, gestor);

        System.out.println(nom + " connectat.");
        System.out.println("DEBUG: multicast Entra: " + nom);
        enviarMissatgeGrup(Missatge.getMissatgeGrup(nom + " entra"));
    }


    public synchronized void eliminarClient(String nom) {
        if (nom != null && clients.containsKey(nom)) {
            clients.remove(nom);
        }
    }

    public void enviarMissatgeGrup(String missatgeRaw) {
        for (GestorClients gestor : clients.values()) {
            gestor.enviarMissatge(missatgeRaw);
        }
    }

    public void enviarMissatgePersonal(String destinatari, String remitent, String text) {
        System.out.println("Missatge personal per (" + destinatari + ") de (" + remitent + "): " + text);
        GestorClients gestor = clients.get(destinatari);
        if (gestor != null) {
            gestor.enviarMissatge(Missatge.getMissatgePersonal(remitent, text));
        }
    }

    public static void main(String[] args) {
        ServidorXat servidor = new ServidorXat();
        try {
            servidor.servidorAEscoltar();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                servidor.pararServidor();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
