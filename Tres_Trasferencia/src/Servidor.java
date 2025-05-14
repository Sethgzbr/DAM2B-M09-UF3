package Tres_Trasferencia.src;

import java.io.*;
import java.net.*;

public class Servidor {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private ServerSocket serverSocket;

    public Socket connectar() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor esperant connexió...");
        return serverSocket.accept();
    }

    public void tancarConnexio(Socket socket) throws IOException {
        socket.close();
        serverSocket.close();
        System.out.println("Connexió tancada.");
    }

    public void enviarFitxers(Socket socket) throws IOException, ClassNotFoundException {
        ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream sortida = new ObjectOutputStream(socket.getOutputStream());

        // Rebre el nom del fitxer
        String nomFitxer = (String) entrada.readObject();
        System.out.println("Rebuda petició per enviar: " + nomFitxer);

        try {
            Fitxer fitxer = new Fitxer(nomFitxer);
            byte[] contingut = fitxer.getContingut();
            sortida.writeObject(contingut);
            System.out.println("Fitxer enviat.");
        } catch (IOException e) {
            sortida.writeObject(null);
            System.err.println("Error en llegir el fitxer.");
        }
    }

    public static void main(String[] args) {
        try {
            Servidor servidor = new Servidor();
            Socket socket = servidor.connectar();
            servidor.enviarFitxers(socket);
            servidor.tancarConnexio(socket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

