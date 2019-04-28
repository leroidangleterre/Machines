/* This class brings together all the machines and all the physical elements that will
 interact with the machines. */
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;

public class World {

    private ArrayList<Machine> machineList;
    private ArrayList<Block> blockList;

    private static int NB_MACHINES_MAX = 400;

    private boolean isRunning;

    // Gravity
    private double gx, gy;
    private boolean gravityActive;
    // Air friction
    private double dampingFactor = 0.999;

    private ArrayList<Collision> collisionList;

    private double date;

    public World(int nbMachines) {
        for (int i = 0; i < nbMachines; i++) {
            this.addMachine(new Machine());
        }
        if (this.blockList == null) {
            this.blockList = new ArrayList<>();
        }

        blockList.add(new Block(0, -1.5, getWidth(), 2, 0));
        blockList.add(new Block(getWidth() / 2, getHeight() / 2, 2, getHeight(), 0));
        blockList.add(new Block(-getWidth() / 2, getHeight() / 2, 2, getHeight(), 0));


        /* Boundaries: axis-aligned blocks. */
        // Bottom
//        this.blockList.add(new Block(-18, -2, 25, 5, 0.0));
//        this.blockList.add(new Block(18, -2, 25, 5, 0.0));
//        // Top
//        // this.blockList.add(new Block(0, 25, 25, 8, 0.0));
//        // Right
//        this.blockList.add(new Block(26, 13, 5, 25, -0.1));
//        // Left
//        this.blockList.add(new Block(-26, 13, 5, 25, 0.1));

        /* Content: other various blocks. */
//        this.blockList.add(new Block(0, 0, 15, 6, 0.05));
//        this.blockList.add(new Block(1, 0, 8, 8, 3.14 / 4 + 0.0));
//        this.blockList.add(new Block(-7, 0, 8, 8, 3.14 / 4 + 0.0));
//        this.blockList.add(new Block(8, 0, 8, 8, 3.14 / 4 + 0.0));
//        this.blockList.add(new Block(8, 0,
//                8, 8,
//                3.14 / 4 + 0.3));
//        this.blockList.add(new Block(-10, 0,
//                8, 8,
//                3.14 / 4));
        this.isRunning = false;
        this.gx = 0;
        this.gy = -9;
        this.gravityActive = true;
        for (int i = 0; i < this.machineList.size(); i++) {
            this.machineList.get(i).setGravity(this.gx, this.gy);
        }

        this.collisionList = new ArrayList<Collision>();
        this.date = 0;
    }

    public void play() {
        if (!this.isRunning) {
            this.isRunning = true;
        }

    }

    public void pause() {
        if (this.isRunning) {
            this.isRunning = false;
        }

    }

    public void addMachine(Machine m) {
        if (this.machineList == null) {
            this.machineList = new ArrayList<>();
        }
        if (machineList.size() < NB_MACHINES_MAX) {
            this.machineList.add(m);
        }
    }

    public void evolve(double dt) {

        // System.out.println("World.evolve: time = " + this.date);
        // Time remaining before the end of the timestep.
        double remainingTime = dt;

        double endOfTimestep = this.date + dt;

        if (this.machineList == null) {
            return;
        }

        boolean loop = true;

        /* Find all the collisions that might happen in the future.
         On re-loop, re-computing the list of next collisions is mandatory
         because the collision we just solved may have added new collisions
         or cancelled existing ones.
         */
        do {
            this.findCollisions(this.date, endOfTimestep);
            // System.out.println("nb coll: " + this.collisionList.size());

            /* When at least one collision happens at the appropriate time,
             it must be solved (the movements of the two objects are modified),
             and then we must re-check for collisions. */
            if (this.collisionList.size() > 0) {
                Collision coll = this.collisionList.get(0);

                coll.solve(this); // The world evolves to that instant, then the speeds are modified.
            }
            // At this point, the other collisions in the list may have been invalidated by the first one.
        } while (this.collisionList != null && this.collisionList.size() > 0);

        /* At this point, we know that no collision will happen before the beginning of the next timestep.
         We will then reach the end of the current timestep. */
        /* First version of the physics engine, where the points only collide on the blocks,
         and the order in which the collisions happen is not properly dealt with. */
        for (int i = 0; i < this.machineList.size(); i++) {
            Machine m = this.machineList.get(i);
            // m.selfCollide();
            if (this.blockList != null) {
                for (int j = 0; j < this.blockList.size(); j++) {
                    this.blockList.get(j).collide(m, dt);
                }
            }
            m.evolve(dt);
        }

        // Friction
        for (int i = 0; i < this.machineList.size(); i++) {
            Machine m = this.machineList.get(i);
            m.dampenSpeed(this.dampingFactor);
        }
    }

    public void paint(Graphics g, double panelHeight,
            double x0, double y0, double zoom) {
        this.paint(g, panelHeight,
                x0, y0, zoom,
                true, 0);
    }

    public void paint(Graphics g, double panelHeight,
            double x0, double y0, double zoom,
            boolean superposed, int nbMachinesPerColumn) {

        double dx = 0, dy = 0, zoomFact = 1;

        for (int i = 0; i < this.machineList.size(); i++) {

            for (int j = 0; j < this.blockList.size(); j++) {

                if (superposed) {
                    /* Display all machines on the same referential. */
                    dx = 0;
                    dy = 0;
                    zoomFact = 1;
                } else {
                    /* Display each machine in a dedicated location. */
                    int numLine = i / nbMachinesPerColumn;
                    int numCol = i - numLine * nbMachinesPerColumn;
                    dx = this.getWidth() * numCol * zoom;
                    dy = -this.getHeight() * numLine * zoom;
                    zoomFact = 1;
                }

                this.blockList.get(j).display(g,
                        x0 + dx, y0 + dy,
                        zoom * zoomFact,
                        (int) panelHeight);
                this.machineList.get(i).display(g,
                        x0 + dx, y0 + dy,
                        zoom * zoomFact,
                        (int) panelHeight);
            }
        }
    }

    public void toggleGravity() {

        this.gravityActive = !this.gravityActive;
        for (int i = 0; i < this.machineList.size(); i++) {
            if (this.gravityActive) {
                this.machineList.get(i).setGravity(this.gx, this.gy);
            } else {
                this.machineList.get(i).setGravity(0, 0);
            }
        }
        if (this.gravityActive) {
            System.out.println("Gravity is now active.");
        } else {
            System.out.println("Gravity is now deactivated.");
        }
    }

    public void increaseSpringSize(double fact) {
        for (int i = 0; i < this.machineList.size(); i++) {
            this.machineList.get(i).increaseSpringSize(fact);
        }
    }


    /* This function returns the physical width of the world,
     so that we know what horizontal space we need when displaying
     the machines not superposed. */
    public double getWidth() {
        // TODO
        return 30;
    }

    public double getHeight() {
        // TODO
        return 30;
    }

    public double getDate() {
        return this.date;
    }


    /* Find all the collisions that might happen within a period of time.
     At the end of this function, the list starts with the first
     of the collisions that will happen between the two specified instants. */
    private void findCollisions(double startDate, double endDate) {
        this.collisionList = new ArrayList<Collision>();

        // TODO
    }

    /**
     * Stop all machines immediately.
     */
    public void blockSpeeds() {
        for (Machine m : machineList) {
            m.dampenSpeed(0);
            m.clearTrail();
        }
    }

    /**
     * Force a mutation on each machine in this world.
     *
     */
    public void mutate() {
        int i = 0;
        for (Machine m : this.machineList) {
            m.mutate();
            System.out.println("i = " + i);
            i++;
        }
    }

    /**
     * Create new machines from the existing. Do not exceed the allowed number
     * of machines.
     */
    public void breed() {

        ArrayList<Machine> clones = new ArrayList<>();
        for (Machine m : machineList) {
            clones.add(m.clone());
        }
        machineList.addAll(clones);
        if (machineList.size() > NB_MACHINES_MAX) {
            machineList = new ArrayList(machineList.subList(0, NB_MACHINES_MAX));
        }
    }

    /**
     * Sort the machines in an order defined by their score.
     *
     */
    public void sortMachines() {
        Collections.sort(machineList);
    }

    /**
     * Remove the second half of the machines list.
     *
     */
    public void killHalf() {
        int oldSize = machineList.size();
        for (int i = oldSize - 1; i >= oldSize / 2; i--) {
            machineList.remove(i);
        }
    }

}
