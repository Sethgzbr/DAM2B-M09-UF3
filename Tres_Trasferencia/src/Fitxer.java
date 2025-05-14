package Tres_Trasferencia.src;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Fitxer {
    private String nom;
    private byte[] contingut;

    public Fitxer(String nom) throws IOException {
        this.nom = nom;
        carregarContingut();
    }

    private void carregarContingut() throws IOException {
        File file = new File(nom);
        if (!file.exists()) {
            throw new IOException("El fitxer no existeix.");
        }
        this.contingut = Files.readAllBytes(file.toPath());
    }

    public byte[] getContingut() {
        return contingut;
    }
}

