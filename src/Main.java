import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/* This is a simulation program where 2d-machines will use engines and springs to move. */
public class Main {

    public static void main(String args[]) {

//        System.out.println("This is the main.");
        int nbMachines = 1000;
        World world = new World(nbMachines);
        GraphicPanel panel = new GraphicPanel(world);
        Window window = new Window(panel);
        panel.repaint();

        panel.togglePlayPause();

        int period = 10000; // 10000ms, or 10s;

        Timer mainTimer = new Timer();

        class MyTimerTask extends TimerTask {

            World world;
            int step = 0;

            // Sort the machines every 1000 ms.
            int sortingDelay = 1000;

            public MyTimerTask(World w) {
                world = w;
            }

            @Override
            public void run() {
                world.sortMachines();
                world.killHalf();
                world.breed();
                world.mutate();
                world.sortMachines();
                Machine best = world.getBestMachine();
                System.out.println("best: " + best.getScore()
                        + " with " + best.getNbPoints() + " points.");
            }
        };

        mainTimer.schedule(new MyTimerTask(world), 0, period);

//        // Every 1000 ms, the machines must be sorted.
//        Timer refreshTimer = new Timer();
//        class RefreshTask extends TimerTask {
//
//            private World world;
//
//            public RefreshTask(World w) {
//                world = w;
//            }
//
//            @Override
//            public void run() {
//                world.sortMachines();
//                System.out.println("timer sort");
//            }
//        }
//        refreshTimer.schedule(new RefreshTask(world), 0, 1000);
    }
}
