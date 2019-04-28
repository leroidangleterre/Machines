/* This is a simulation program where 2d-machines will use engines and springs to move. */
import javax.swing.JFrame;

public class Main {

    public static void main(String args[]) {

        System.out.println("This is the main.");

        int nbMachines = 100;
        World world = new World(nbMachines);
        GraphicPanel panel = new GraphicPanel(world);
        Window window = new Window(panel);
        panel.repaint();

        System.out.println("End.");
    }
}
