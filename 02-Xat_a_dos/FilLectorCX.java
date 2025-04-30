import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;

public class FilLectorCX {
    private ObjectOutputStream objectOutputStream;

    // Constructor que rep un nom de fitxer
    public FilLectorCX(String fileName) throws IOException {
        objectOutputStream = new ObjectOutputStream(new FileOutputStream(fileName));
    }

    // Constructor que rep un OutputStream
    public FilLectorCX(OutputStream outputStream) throws IOException {
        objectOutputStream = new ObjectOutputStream(outputStream);
    }

    public void writeObject(Object obj) throws IOException {
        objectOutputStream.writeObject(obj);
    }

    public void close() throws IOException {
        if (objectOutputStream != null) {
            objectOutputStream.close();
        }
    }

    // Mètode d'execució que rep els missatges del Xat
    public void executarMissatges(String[] missatges) throws IOException {
        for (String missatge : missatges) {
            writeObject(missatge);
        }
    }
}
