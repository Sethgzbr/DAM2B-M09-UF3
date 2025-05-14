package Tres_Trasferencia.src;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.nio.file.*;

public class Client {
    private static final String DIR_ARRIBADA = "/tmp"; // o C:\\Temp per a Windows
    private Socket socket;
    private ObjectOutputStream sortida;
    private ObjectInputStream entrada;

    public void connectar() throws IOException {
        socket = new Socket("localhost", 9999);
        sortida = new ObjectOutputStream(socket.getOutputStream());
        entrada = new ObjectInputStream(socket.getInputStream());
        System.out.println("Connectat al servidor.");
    }

    public void rebreFitxers() throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Introdueix la ruta completa del fitxer a rebre: ");
        String nomFitxer = scanner.nextLine();

        sortida.writeObject(nomFitxer); // Envia nom al servidor

        byte[] dades = (byte[]) entrada.readObject();

        if (dades == null) {
            System.out.println("Error: El fitxer no s'ha pogut rebre.");
            return;
        }

        Path path = Paths.get(DIR_ARRIBADA, new File(nomFitxer).getName());
        Files.write(path, dades);
        System.out.println("Fitxer rebut i guardat a: " + path.toString());
    }

    public void tancarConnexio() throws IOException {
        socket.close();
        System.out.println("Connexió tancada.");
    }

    public static void main(String[] args) {
        try {
            Client client = new Client();
            client.connectar();
            client.rebreFitxers();
            client.tancarConnexio();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

