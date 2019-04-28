/* The machine is made of points, at least one;
 these points may be linked by a spring of a given stiffness.


 A machine evolves periodically over a fixed period.
 Each spring is defined by a default length and two instants,
 when it switches between the active and inactive state,
 which are defined by two distinct lengths.
 As each spring oscillates between two length, the machine takes several
 different states over its period.


 */
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Machine implements Comparable<Machine> {

    private static int nbMachinesCreated = 0;

    private int machineID;

    private static int NB_POINTS_MAX = 6;

    private ArrayList<Point> pointList;
    private ArrayList<Spring> springList;

    // Gravity
    private double gx, gy;

    /* This constructor creates between 1 and 10 points;
     each two points have a probability of 0.3 of being linked together.
     */
    public Machine() {
        this.machineID = nbMachinesCreated;
        nbMachinesCreated++;

        this.pointList = new ArrayList<>();
        this.springList = new ArrayList<>();

        this.buildMachine(1);

        this.gx = 0;
        this.gy = 0;
    }

    /**
     * Create a deep copy of the parameter, with points at the same coordinates
     * and springs between the same pairs of points.
     *
     * @param model
     */
    public Machine(Machine model) {
        this();

        this.pointList = new ArrayList<>();
        this.springList = new ArrayList<>();

        for (Point p : model.pointList) {
            this.pointList.add(p.clone());
        }
        for (Spring s : model.springList) {
            Spring newSpring = new Spring();
            this.springList.add(newSpring);
        }

        // Link each one of the new points to the appropriate springs.
        int pointIndex = 0;
        for (Point p : model.pointList) {
            // Identify all springs linked to the current point
            int springIndex = 0;
            for (Spring s : model.springList) {
                if (s.usesPoint(p)) {
                    this.springList.get(springIndex).addPoint(this.pointList.get(pointIndex));
                }
                springIndex++;
            }
            pointIndex++;
        }

        for (int i = 0; i < springList.size(); i++) {
            this.springList.get(i).setL0(model.springList.get(i).getL0());
        }

        this.gx = model.gx;
        this.gy = model.gy;
    }

    public Machine clone() {
        return new Machine(this);
    }

    /**
     * Build a machine of the chosen type.
     *
     * @param type
     * @value 0: regular polygon
     * @value 1: one sphere
     * @value 2: two spheres
     * @value 3: three spheres, one spring
     * @value 4: triangular mesh of points
     * @value 5: square-based rectangular mesh, with each square getting two diagonals.
     */
    private void buildMachine(int type) {
        this.buildMachine(type, false);
    }

    private void buildMachine(int type, boolean addCenter) {

        ArrayList<ArrayList<Point>> list;

        switch (type) {
            case 0:
                /* Regular polygon */
                int nbPoints = 4;
                double x,
                 y;
                double radius = 5;
                for (int i = 0; i < nbPoints; i++) {
                    // Coordinates around a circle
                    x = 5 * Math.cos(i * 2 * Math.PI / nbPoints) + 6;
                    y = 5 * Math.sin(i * 2 * Math.PI / nbPoints) + 12;
                    this.pointList.add(new Point(x, y, 1));
                    // Link this point to each pre-existing point.
                    for (int j = 0; j < i; j++) {
                        // System.out.println("     j=" + j);
                        // System.out.println("     new spring: " + i + ", " + j);
                        this.springList.add(new Spring(this.pointList.get(i), this.pointList.get(j)));
                    }
                }
                if (addCenter) {
                    Point center = new Point(0, 20, 1);
                    this.pointList.add(center);
                    for (int i = 0; i < nbPoints; i++) {
                        this.springList.add(new Spring(center, this.pointList.get(i)));
                    }
                }

                break;
            case 1:
                /* Only one sphere. */
                this.pointList.add(new Point(5, 4, 1));
                break;
            case 2:
                /* Two spheres. */
                this.pointList.add(new Point(5, 4, 1));
                this.pointList.add(new Point(5, 8, 1));
                break;
            case 3:
                /* Three spheres, one spring. */
                this.pointList.add(new Point(5, 4, 1));
                this.pointList.add(new Point(5, 10, 1));
                this.pointList.add(new Point(6, 6.1, 1));
                this.springList.add(new Spring(this.pointList.get(1), this.pointList.get(2)));
                break;
            case 4:
                /* Triangular mesh of points. */
                int size = 13;
                list = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    list.add(i, new ArrayList<>());
                    for (int j = 0; j <= i; j++) {
                        float xPoint = (float) (3 * (j - 0.5 * (double) i));
                        float yPoint = (float) (2.7 * (size - i) - 0.7);
                        Point p = new Point(xPoint, yPoint, 1);
                        list.get(i).add(p);
                        this.pointList.add(p);
                    }
                }

                for (int i = 0; i < size - 1; i++) {
                    for (int j = 0; j <= i; j++) {
                        Point p0 = list.get(i).get(j);
                        Point p1 = list.get(i + 1).get(j);
                        Point p2 = list.get(i + 1).get(j + 1);
                        this.springList.add(new Spring(p0, p1, 100));
                        this.springList.add(new Spring(p0, p2, 100));
                        this.springList.add(new Spring(p1, p2, 100));
                    }
                }
                break;
            case 5:
                /* Square-based rectangular mesh, with each square getting two diagonals. */
                int width = 15;
                int height = 10;
                double dx = 3;

                list = new ArrayList<>();

                for (int i = 0; i < height; i++) {
                    list.add(new ArrayList<>());
                    for (int j = 0; j < width; j++) {
                        Point p = new Point(dx * ((double) j - (double) width / 2 + .5), dx * ((double) i - (double) height / 2 + .5 + 5), 1);
                        this.pointList.add(p);
                        list.get(i).add(p);
                        if (i > 0 && j > 0) {
                            Point upLeftNeighbor = list.get(i - 1).get(j - 1);
                            Point upNeighbor = list.get(i - 1).get(j);
                            Point leftNeighbor = list.get(i).get(j - 1);
                            this.springList.add(new Spring(p, upLeftNeighbor));
                            this.springList.add(new Spring(upNeighbor, leftNeighbor));
                        }
                        if (i > 0) {
                            Point upNeighbor = list.get(i - 1).get(j);
                            this.springList.add(new Spring(p, upNeighbor));
                        }
                        if (j > 0) {
                            Point leftNeighbor = list.get(i).get(j - 1);
                            this.springList.add(new Spring(p, leftNeighbor));
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public String toString() {
        String res = "" + pointList.size();
        for (Point p : pointList) {
            res += " " + p.getX() + " " + p.getY() + " - ";
        }
        res += "" + springList.size();
        for (Spring s : springList) {
            // Identify the two ends of the spring, write the indices.
            int pointIndex = 0;
            for (Point p : pointList) {
                if (s.usesPoint(null)) {

                }
                pointIndex++;
            }
        }
        return res;
    }

    public void display(Graphics g,
            double x0, double y0, double zoom,
            int panelHeight) {

        for (int i = 0; i < this.pointList.size(); i++) {
            this.pointList.get(i).display(g,
                    x0, y0, zoom,
                    panelHeight);
        }
        for (int i = 0; i < this.springList.size(); i++) {
            this.springList.get(i).display(g,
                    x0, y0, zoom,
                    panelHeight);
        }

        // Display score with four digits after decimal point
        int xDisplay = (int) (zoom * (this.getXMin() + this.getXMax()) / 2 + x0);
        int yDisplay = (int) (panelHeight - (zoom * this.getYMax() + y0) - 2 * zoom);
        g.setColor(Color.black);
        String text = "" + this.getScore();
        text = text.substring(0, Math.min(6, text.length()));
        g.drawString(text, xDisplay, yDisplay);
    }

    private void razForces() {
        for (int i = 0; i < this.pointList.size(); i++) {
            this.pointList.get(i).razForces();
        }
    }

    private void computeForces(double dt) {
        this.razForces();
        for (int i = 0; i < this.springList.size(); i++) {
            this.springList.get(i).applyForce(dt);
        }

        for (int i = 0; i < this.pointList.size(); i++) {
            this.pointList.get(i).receiveForce(this.gx, this.gy);
        }
    }

    private void updateSpeeds(double dt) {
        for (int i = 0; i < this.pointList.size(); i++) {
            this.pointList.get(i).updateSpeed(dt);
        }
    }

    private void move(double dt) {
        for (int i = 0; i < this.pointList.size(); i++) {
            this.pointList.get(i).move(dt);
        }
    }

    /* When we are about to move the points, the springs
     are about to get their new length. We must here store their current length
     as the previous length. */
    private void updateSprings() {
        for (int i = 0; i < this.springList.size(); i++) {
            this.springList.get(i).updateLength();
        }
    }


    /* The evolution process is composed of three steps.
     1) All forces applied on the points are computed.
     2) All point speeds are updated.
     3) All points are moved.
     */
    public void evolve(double dt) {
        this.computeForces(dt);
        this.updateSpeeds(dt);
        this.updateSprings();
        this.move(dt);
    }

    public int getNbPoints() {
        return this.pointList.size();
    }

    public Point getPoint(int index) {
        return this.pointList.get(index);
    }

    public void setGravity(double gx, double gy) {
        this.gx = gx;
        this.gy = gy;
    }


    /* Factor:
     1.0 <-> All kinetic energy is conserved.
     0.0 <-> No kinetic energy is conserved. */
    public void dampenSpeed(double factor) {
        for (int i = 0; i < this.pointList.size(); i++) {
            this.pointList.get(i).dampenSpeed(factor);
        }
    }


    /* Insert the given collision in the list,
     while keeping that list order relatively to time. */
    private void insertCollision(Collision newColl, ArrayList<Collision> list) {

        if (list != null) {
            int i = 0;
            try {
                Collision coll = list.get(0);
                while (coll != null && coll.happensSoonerThan(newColl)) {
                    i++;
                    coll = list.get(i);
                }
                // Element at index (i-1) is sooner than new collision; inserting at position i.
            } catch (IndexOutOfBoundsException e) {
                // No element at index i (list ends at i-1); inserting at position i.
            }
            list.add(i, newColl);
        }
    }

    /* Detect the collisions between the points of this machine,
     and modify the speeds of all colliding points. */
    public ArrayList<Collision> findSelfCollisions() {

        // System.out.println("Machine.findSelfCollisions");
        ArrayList<Collision> list = new ArrayList<>();

        // TODO
        for (int i = 0; i < this.pointList.size(); i++) {
            Point a = this.pointList.get(i);
            for (int j = i + 1; j < this.pointList.size(); j++) {
                Point b = this.pointList.get(j);

                Collision coll = a.getCollisionWith(b);

                this.insertCollision(coll, list);
            }
        }

        return list;
    }

    public void increaseSpringSize(double fact) {
        for (int i = 0; i < this.springList.size(); i++) {
            this.springList.get(i).increaseSize(fact);
        }
    }

    /**
     * Remove the current trail for each point of the machine.
     */
    public void clearTrail() {
        for (Point p : pointList) {
            p.clearTrail();
        }
    }

    /**
     * Add a new Point at a random location, and link that point to the machine
     * with two springs. Does nothing if the maximum number of points is already
     * reached.
     */
    public void addRandomPoint() {
        if (this.pointList.size() < NB_POINTS_MAX) {

            double margin = 5; // how far the new point is allowed to be

            double xMin = getXMin() - margin;
            double xMax = getXMax() + margin;
            double yMin = getYMin() - margin;
            double yMax = getYMax() + margin;

            double x = xMin + Math.random() * (xMax - xMin);
            double y = yMin + Math.random() * (yMax - yMin);

            Point newPoint = new Point(x, y, 1);

            int size = pointList.size();
            if (size >= 1) {
                // Create one spring.
                int index0 = (int) (size * Math.random());
                Spring spring0 = new Spring(newPoint, pointList.get(index0));
                springList.add(spring0);
                if (size >= 2) {
                    // Create another spring
                    int index1 = (int) (size * Math.random());
                    // but not from the same point as the first spring !
                    if (index1 == index0) {
                        index1 = index1 + 1;
                        if (index1 >= size) {
                            index1 = 0;
                        }
                    }
                    Spring spring1 = new Spring(newPoint, pointList.get(index1));
                    springList.add(spring1);
                }
            }
            pointList.add(newPoint);
        }
    }

    /**
     * Choose a random point in the machine, and remove it. The springs linked
     * to that point are removed as well.
     */
    public void removeRandomPoint() {

        System.out.println("removing point");

        int size = pointList.size();
        if (size >= 1) {
            int index = (int) (size * Math.random());
            Point removedPoint = pointList.get(index);
            pointList.remove(removedPoint);
            for (Spring s : springList) {
                if (s.usesPoint(removedPoint)) {
                    springList.remove(s);
                }
            }
        }
    }

    /**
     * Find the minimum x-value of the machine.
     */
    public double getXMin() {
        double xMin = Double.POSITIVE_INFINITY;
        for (Point p : pointList) {
            xMin = Math.min(p.getX(), xMin);
        }
        return xMin;
    }

    /**
     * Find the maximum x-value of the machine.
     */
    public double getXMax() {
        double xMin = Double.NEGATIVE_INFINITY;
        for (Point p : pointList) {
            xMin = Math.max(p.getX(), xMin);
        }
        return xMin;
    }

    /**
     * Find the minimum y-value of the machine.
     */
    public double getYMin() {
        double yMin = Double.POSITIVE_INFINITY;
        for (Point p : pointList) {
            yMin = Math.min(p.getY(), yMin);
        }
        return yMin;
    }

    /**
     * Find the maximum y-value of the machine.
     */
    public double getYMax() {
        double yMax = Double.NEGATIVE_INFINITY;
        for (Point p : pointList) {
            yMax = Math.max(p.getY(), yMax);
        }
        return yMax;
    }

    public void changeRandomSpringLength() {
        if (springList.size() > 0) {
            int nbSprings = springList.size();
            int index = (int) (Math.random() * nbSprings);
            Spring modifiedSpring = springList.get(index);
            double fact = Math.random() + 0.5;
            modifiedSpring.increaseSize(fact);
        }
    }

    /**
     * Force a mutation to happen to the machine, i.e. modify the default length
     * of a spring, and in some cases, add a new point that will be linked with
     * two springs.
     *
     */
    public void mutate() {

        double probAddPoint = 0.05;
        if (Math.random() <= probAddPoint) {
            addRandomPoint();
        }

        double probRemovePoint = 0.05;
        if (Math.random() <= probRemovePoint) {
            removeRandomPoint();
        }

        changeRandomSpringLength();
    }

    /**
     * Compute a score for the machine. The score may be the height of the
     * machine, or the maximum force in the springs, ...
     *
     * @return the current value of the machine
     */
    public double getScore() {

        double result;

        result = getYMax() - getYMin();

        if (result == Double.NaN) {
            return -1;
        }

        if (result > 10000000) {
            return -1;
        }

        // Penalty if the machine goes below 0 vertically
        if (getYMin() < 0) {
            result += 20 * getYMin();
        }

        return result;
    }

    @Override
    public int compareTo(Machine m) {
        if (this.getScore() > m.getScore()) {
            return -1;
        } else if (this.getScore() < m.getScore()) {
            return +1;
        } else {
            return 0;
        }
    }
}
