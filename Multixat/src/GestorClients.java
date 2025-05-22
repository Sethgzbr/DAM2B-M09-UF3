import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GestorClients implements Runnable {
    private Socket client;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ServidorXat servidor;
    private String nom;
    private boolean sortir = false;

    public GestorClients(Socket client, ServidorXat servidor) throws IOException {
        this.client = client;
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
                String msgRaw = (String) in.readObject();
                processaMissatge(msgRaw);
            }
        } catch (Exception e) {
            // Client tancat
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void enviarMissatge(String missatgeRaw) {
        try {
            out.writeObject(missatgeRaw);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processaMissatge(String msgRaw) {
        String codi = Missatge.getCodiMissatge(msgRaw);
        String[] parts = Missatge.getPartsMissatge(msgRaw);
        if (codi == null || parts == null) return;

        switch (codi) {
            case Missatge.CODI_CONECTAR:
                nom = parts[1];
                servidor.afegirClient(this);
                break;
            case Missatge.CODI_SORTIR_CLIENT:
                // Notifica als altres clients que aquest client (nom) està sortint
                servidor.enviarMissatgeGrup(Missatge.getMissatgeSortirClient("Adéu"));
                // Ara tanquem el client que ha demanat sortir
                sortir = true;
                servidor.eliminarClient(nom);
                break;
            case Missatge.CODI_SORTIR_TOTS:
                sortir = true;
                servidor.finalitzarXat();
                break;
            case Missatge.CODI_MSG_PERSONAL:
                String dest = parts[1];
                String text = parts[2];
                servidor.enviarMissatgePersonal(dest, nom, text);
                break;
            case Missatge.CODI_MSG_GRUP:
                String txt = parts[1];
                servidor.enviarMissatgeGrup(Missatge.getMissatgeGrup(txt));
                break;
            default:
                System.out.println("Error: codi desconegut " + codi);
        }
    }
}