import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientXat implements Runnable {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private volatile boolean sortir = false;
    private static final String HOST = "localhost";
    private static final int PORT = 9999;

    public void connecta() throws IOException {
        socket = new Socket(HOST, PORT);
        out = new ObjectOutputStream(socket.getOutputStream());
        System.out.println("Client connectat a " + HOST + ":" + PORT);
    }

    public void enviarMissatge(String msg) throws IOException {
        out.writeObject(msg);
        out.flush();
        System.out.println("Enviant missatge: " + msg);
    }

    public void tancarClient() throws IOException {
        if (in != null) in.close();
        if (out != null) out.close();
        if (socket != null && !socket.isClosed()) socket.close();
    }

    @Override
    public void run() {
        try {
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("DEBUG: Iniciant rebuda de missatges...");
            while (!sortir) {
                String msgRaw = (String) in.readObject();
                processaMissatge(msgRaw);
            }
        } catch (Exception e) {
            // Fluxe error
        }
    }

    private void processaMissatge(String msgRaw) {
        String codi = Missatge.getCodiMissatge(msgRaw);
        String[] parts = Missatge.getPartsMissatge(msgRaw);
        if (codi == null || parts == null) return;

        switch (codi) {
            case Missatge.CODI_SORTIR_TOTS:
                sortir = true;
                System.out.println("Tots han sortit: " + parts[1]);
                break;
            case Missatge.CODI_MSG_PERSONAL:
                System.out.println("Missatge personal de (" + parts[1] + "): " + parts[2]);
                break;
            case Missatge.CODI_MSG_GRUP:
                System.out.println("Missatge de grup: " + parts[1]);
                break;
            case Missatge.CODI_SORTIR_CLIENT:
                // Mostrem exactament aquests tres missatges quan rebem el 1003#Adéu
                System.out.println("Enviant missatge: " + msgRaw);
                System.out.println("oos null. Sortint...");
                System.out.println("Tancant client...");
                sortir = true;
                break;

            default:
                System.out.println("Error: codi desconegut a client " + codi);
        }
    }

    private void ajuda() {
        System.out.println("---------------------");
        System.out.println("Comandes disponibles:");
        System.out.println("  1.- Conectar al servidor (primer pas obligatori)");
        System.out.println("  2.- Enviar missatge personal");
        System.out.println("  3.- Enviar missatge al grup");
        System.out.println("  4.- (o línia en blanc)-> Sortir del client");
        System.out.println("  5.- Finalitzar tothom");
        System.out.println("---------------------");
    }

    private String getLinea(Scanner sc, String msg, boolean obligatori) {
        String linea;
        do {
            System.out.print(msg);
            linea = sc.nextLine().trim();
        } while (obligatori && linea.isEmpty());
        return linea;
    }

    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        Scanner sc = new Scanner(System.in);
        try {
            client.connecta();
            new Thread(client).start();
            client.ajuda();
            boolean sortir = false;
            while (!sortir) {
                String op = sc.nextLine().trim();
                String msgRaw = null;
                switch (op) {
                    case "1":
                        String nom = client.getLinea(sc, "Introdueix el nom: ", true);
                        msgRaw = Missatge.getMissatgeConectar(nom);
                        break;
                    case "2":
                        String dest = client.getLinea(sc, "Destinatari: ", true);
                        String txt = client.getLinea(sc, "Missatge a enviar: ", true);
                        msgRaw = Missatge.getMissatgePersonal(dest, txt);
                        break;
                    case "3":
                        String grp = client.getLinea(sc, "Missatge de grup: ", true);
                        msgRaw = Missatge.getMissatgeGrup(grp);
                        break;
                    case "5":
                        msgRaw = Missatge.getMissatgeSortirTots("Adéu");
                        sortir = true;
                        break;
                    case "4":
                    default:
                        msgRaw = Missatge.getMissatgeSortirClient("Adéu");
                        sortir = true;
                        break;
                }
                client.enviarMissatge(msgRaw);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.tancarClient();
            } catch (IOException e) {
                e.printStackTrace();
            }
            sc.close();
        }
    }
}