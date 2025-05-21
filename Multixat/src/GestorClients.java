package Multixat.src;
import java.io.*;
import java.net.Socket;

public class GestorClients extends Thread {
    private Socket client;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ServidorXat servidor;
    private String nom;
    private boolean sortir = false;

    public GestorClients(Socket clientSocket, ServidorXat servidor) throws IOException {
        this.client = clientSocket;
        this.servidor = servidor;
        out = new ObjectOutputStream(client.getOutputStream());
        in = new ObjectInputStream(client.getInputStream());
    }

    public String getNom() {
        return nom;
    }

    @Override
    public void run() {
        try {
            while (!sortir) {
                String missatgeRaw = (String) in.readObject();
                processaMissatge(missatgeRaw);
            }
        } catch (Exception e) {
            System.out.println("Error en gestor de client: " + e.getMessage());
        } finally {
            try {
                client.close();
            } catch (IOException e) { }
        }
    }

    public void enviarMissatge(String remitent, String missatge) {
        try {
            String raw = Missatge.getMissatgeGrup(remitent + ": " + missatge);
            out.writeObject(raw);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processaMissatge(String missatgeRaw) {
        String codi = Missatge.getCodiMissatge(missatgeRaw);
        String[] parts = Missatge.getPartsMissatge(missatgeRaw);
        if (codi == null || parts == null) return;

        switch (codi) {
            case Missatge.CODI_CONECTAR:
                nom = parts[1];
                servidor.afegirClient(this);
                break;
            case Missatge.CODI_SORTIR_CLIENT:
                sortir = true;
                servidor.eliminarClient(nom);
                break;
            case Missatge.CODI_SORTIR_TOTS:
                sortir = true;
                servidor.finalitzarXat();
                break;
            case Missatge.CODI_MSG_PERSONAL:
                String dest = parts[1];
                String msg = parts[2];
                servidor.enviarMissatgePersonal(dest, nom, msg);
                break;
            case Missatge.CODI_MSG_GRUP:
                String gm = parts[1];
                servidor.enviarMissatgeGrup(nom + ": " + gm);
                break;
            default:
                System.out.println("Codi desconegut: " + missatgeRaw);
        }
    }
}
