import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class FilServidorXat implements Runnable {
    private Socket clientSocket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    public FilServidorXat(Socket clientSocket, ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        this.clientSocket = clientSocket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        executar();
    }

    public void executar() {
        try {
            String missatge;
            while ((missatge = (String) inputStream.readObject()) != null) {
                if ("MSG_SORTIR".equals(missatge)) {
                    System.out.println("Missatge de sortida rebut. Finalitzant...");
                    break;
                }
                System.out.println("Missatge rebut: " + missatge);
                // Opcional: enviar una resposta al client
                outputStream.writeObject("Missatge rebut: " + missatge);
            }
        } catch (Exception e) {
            System.err.println("Error en rebre missatges: " + e.getMessage());
        } finally {
            try {
                inputStream.close();
                outputStream.close();
                clientSocket.close();
            } catch (Exception e) {
                System.err.println("Error en tancar els recursos: " + e.getMessage());
            }
        }
    }
}
