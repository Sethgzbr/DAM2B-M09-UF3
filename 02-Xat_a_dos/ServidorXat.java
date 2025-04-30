import java.net.ServerSocket;

public class ServidorXat {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private static final String MSG_SORTIR = "sortir";
    private ServerSocket serverSocket;

    public void iniciarServidor() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciat al port " + PORT);
        } catch (Exception e) {
            System.err.println("Error en iniciar el servidor: " + e.getMessage());
        }
    }

    public void pararServidor() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Servidor aturat.");
            }
        } catch (Exception e) {
            System.err.println("Error en aturar el servidor: " + e.getMessage());
        }
    }

    public String getNom(java.io.InputStream inputStream) {
        try (java.util.Scanner scanner = new java.util.Scanner(inputStream)) {
            System.out.print("Introdueix el teu nom: ");
            return scanner.nextLine();
        } catch (Exception e) {
            System.err.println("Error en obtenir el nom del client: " + e.getMessage());
            return null;
        }
    }

    public static <FilServidorXat> void main(String[] args) {
        ServidorXat servidor = new ServidorXat();
        servidor.iniciarServidor();

        try (java.net.Socket clientSocket = servidor.serverSocket.accept();
             java.io.InputStream inputStream = clientSocket.getInputStream();
             java.io.OutputStream outputStream = clientSocket.getOutputStream()) {

            System.out.println("Client connectat.");

            String nomClient = servidor.getNom(inputStream);
            System.out.println("Nom del client: " + nomClient);

            Runnable filServidor = new Runnable() {
                @Override
                public void run() {
                    // Implement the logic for handling the client here
                    try {
                        java.util.Scanner scanner = new java.util.Scanner(inputStream);
                        String message;
                        while ((message = scanner.nextLine()) != null) {
                            System.out.println("Missatge del client: " + message);
                            if (message.equalsIgnoreCase(MSG_SORTIR)) {
                                break;
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error en el fil del servidor: " + e.getMessage());
                    }
                }
            };
            Thread thread = new Thread(filServidor);
            thread.start();

            try (java.util.Scanner consola = new java.util.Scanner(System.in)) {
                String missatge;
                do {
                    System.out.print("Envia un missatge: ");
                    missatge = consola.nextLine();
                    outputStream.write((missatge + "\n").getBytes());
                } while (!missatge.equalsIgnoreCase(MSG_SORTIR));
            }

            thread.join();
        } catch (Exception e) {
            System.err.println("Error en la comunicació amb el client: " + e.getMessage());
        } finally {
            servidor.pararServidor();
        }
    }
}
