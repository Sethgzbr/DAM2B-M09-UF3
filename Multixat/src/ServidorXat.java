package Multixat.src;
import java.io.*;
import java.net.*;
import java.util.Hashtable;

public class ServidorXat {
    public static final int PORT = 9999;
    public static final String HOST = "localhost";
    public static final String MSG_SORTIR = "sortir";

    private ServerSocket serverSocket;
    private Hashtable<String, GestorClients> clients = new Hashtable<>();
    private boolean sortir = false;

    public void servidorAEscoltar() throws IOException {
        serverSocket = new ServerSocket(PORT, 0, InetAddress.getByName(HOST));
        System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
        while (!sortir) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connectat: " + clientSocket.getRemoteSocketAddress());
            GestorClients gestor = new GestorClients(clientSocket, this);
            gestor.start();
        }
    }

    public void pararServidor() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }

    public synchronized void finalitzarXat() {
        enviarMissatgeGrup(MSG_SORTIR);
        clients.clear();
        System.out.println("Tancant tots els clients.");
        sortir = true;
    }

    public synchronized void afegirClient(GestorClients g) {
        clients.put(g.getNom(), g);
        enviarMissatgeGrup(g.getNom() + " entra.");
        System.out.println("DEBUG: multicast Entra: " + g.getNom());
    }

    public synchronized void eliminarClient(String nom) {
        if (clients.containsKey(nom)) {
            clients.remove(nom);
            System.out.println("Client eliminat: " + nom);
        }
    }

    public synchronized void enviarMissatgeGrup(String missatge) {
        String raw = Missatge.getMissatgeGrup(missatge);
        for (GestorClients g : clients.values()) {
            g.enviarMissatge("Servidor", missatge);
        }
    }

    public synchronized void enviarMissatgePersonal(String destinatari, String remitent, String missatge) {
        GestorClients g = clients.get(destinatari);
        if (g != null) {
            g.enviarMissatge(remitent, missatge);
            System.out.println("Missatge personal per (" + destinatari + ") de (" + remitent + "): " + missatge);
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