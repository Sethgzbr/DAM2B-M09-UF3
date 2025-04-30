package Xat_a_dos;
import java.io.ObjectInputStream;

class FilLectorCX implements Runnable {
    private ObjectInputStream input;

    public FilLectorCX(ObjectInputStream input) {
        this.input = input;
    }

    @Override
    public void run() {
        try {
            String missatge;
            while ((missatge = (String) input.readObject()) != null) {
                System.out.println("Rebut: " + missatge);
                if (missatge.equalsIgnoreCase("sortir")) {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Connexió tancada.");
        }
    }
}