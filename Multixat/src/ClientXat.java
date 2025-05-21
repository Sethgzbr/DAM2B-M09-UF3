package Multixat.src;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientXat {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean sortir = false;

    public void connecta() throws IOException {
        socket = new Socket(ServidorXat.HOST, ServidorXat.PORT);
        out = new ObjectOutputStream(socket.getOutputStream());
        System.out.println("Client connectat a " + ServidorXat.HOST + ":" + ServidorXat.PORT);
    }

    public void enviarMissatge(String raw) throws IOException {
        out.writeObject(raw);
        out.flush();
        System.out.println("Enviant missatge: " + raw);
    }

    public void tancarClient() throws IOException {
        if (in != null) in.close();
        if (out != null) out.close();
        if (socket != null) socket.close();
        System.out.println("Tancant client...");
    }

    public void runReceiving() {
        new Thread(() -> {
            try {
                in = new ObjectInputStream(socket.getInputStream());
                System.out.println("DEBUG: Iniciant rebuda de missatges...");
                while (!sortir) {
                    String raw = (String) in.readObject();
                    String codi = Missatge.getCodiMissatge(raw);
                    String[] parts = Missatge.getPartsMissatge(raw);
                    switch (codi) {
                        case Missatge.CODI_SORTIR_TOTS:
                            sortir = true;
                            break;
                        case Missatge.CODI_MSG_PERSONAL:
                        case Missatge.CODI_MSG_GRUP:
                            System.out.println(parts[1] + ": " + parts[2]);
                            break;
                        default:
                            System.out.println("Missatge desconegut: " + raw);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error rebent missatge. Sortint...");
            }
        }).start();
    }

    public void ajuda() {
        System.out.println("---------------------");
        System.out.println("Comandes disponibles:");
        System.out.println("  1.- Conectar al servidor (primer pas obligatori)");
        System.out.println("  2.- Enviar missatge personal");
        System.out.println("  3.- Enviar missatge al grup");
        System.out.println("  4.- (o línia en blanc) -> Sortir del client");
        System.out.println("  5.- Finalitzar tothom");
        System.out.println("---------------------");
    }

    public String getLinea(Scanner sc, String prompt, boolean obligatori) {
        String line;
        do {
            System.out.print(prompt);
            line = sc.nextLine().trim();
        } while (obligatori && line.isEmpty());
        return line;
    }

    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        try (Scanner sc = new Scanner(System.in)) {
            client.connecta();
            client.runReceiving();
            client.ajuda();
            boolean primer = true;
            while (!client.sortir) {
                String op = sc.nextLine().trim();
                if (op.isEmpty()) op = "4";
                switch (op) {
                    case "1":
                        if (primer) {
                            String nom = client.getLinea(sc, "Introdueix el nom: ", true);
                            client.enviarMissatge(Missatge.getMissatgeConectar(nom));
                            primer = false;
                        }
                        break;
                    case "2":
                        String dest = client.getLinea(sc, "Destinatari: ", true);
                        String msg = client.getLinea(sc, "Missatge a enviar: ", true);
                        client.enviarMissatge(Missatge.getMissatgePersonal(dest, msg));
                        break;
                    case "3":
                        String gm = client.getLinea(sc, "Missatge grup: ", true);
                        client.enviarMissatge(Missatge.getMissatgeGrup(gm));
                        break;
                    case "4":
                        client.enviarMissatge(Missatge.getMissatgeSortirClient("Ad\u00e9u"));
                        client.sortir = true;
                        break;
                    case "5":
                        client.enviarMissatge(Missatge.getMissatgeSortirTots("Ad\u00e9u"));
                        client.sortir = true;
                        break;
                    default:
                        System.out.println("Opcio desconeguda.");
                }
                if (!client.sortir) client.ajuda();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { client.tancarClient(); } catch (Exception e) {}
        }
    }
}
