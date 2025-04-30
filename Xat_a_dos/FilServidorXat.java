package Xat_a_dos;
import java.io.ObjectInputStream;

class FilServidorXat implements Runnable {
    private ObjectInputStream input;

    public FilServidorXat(ObjectInputStream input) {
        this.input = input;
    }

    @Override
    public void run() {
        try {
            String missatge;
            while ((missatge = (String) input.readObject()) != null) {
                System.out.println("Rebut: " + missatge);
                if (missatge.equalsIgnoreCase(ServidorXat.getMissatge())) {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Fil de xat finalitzat.");
        }
    }
}