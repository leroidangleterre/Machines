import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Graphics;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GraphicPanel extends JPanel {

    /* The origin of the represented environment will be visible
     at the x0-th pixel column and at the y0-th pixel line,
     starting from the lower-left corner.
     The zoom value is the amount of pixels between that origin
     and the point of coordinates (1, 0).  */
    private double x0, y0, zoom;
    private int nbMachinesPerCol = 40; // When not superposed, how many columns we want to see

    private World world;
    private Timer timer;
    private boolean isRunning;

    private int date;

    private Window window;

    private double defaultPeriod; // In seconds; period of the sim time.

    /* Either the machines are superposed (each one is displayed in the same referential),
     or they all have their own referential. */
    private boolean superposed = false;

    public GraphicPanel(Window w) {
        this();
    }

    public GraphicPanel() {
        super();
        this.x0 = 206;
        this.y0 = 548;
        this.zoom = 5.438;
        this.defaultPeriod = 0.05;
        ActionListener listener = new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                date++;
                // System.out.println("Timer " + date);
                world.evolve(defaultPeriod);
                repaint();
            }
        };
        int period = 10;
        this.timer = new Timer(period, listener);
        this.isRunning = false;
        this.date = 0;
    }

    public GraphicPanel(World w) {
        this();
        this.world = w;
    }

    public void eraseAll(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0,
                (int) (this.getSize().getWidth()),
                (int) (this.getSize().getHeight()));
    }

    @Override
    public void paintComponent(Graphics g) {

//        System.out.println("GraphicPanel.repaint(); x0 = " + this.x0 + ", y0 = " + this.y0 + ", zoom = " + this.zoom);
        this.eraseAll(g);

        double panelHeight = this.getSize().getHeight();

        /* Offset used when the machines are not superposed. */
        double dx, dy;
        double zoomFact;

        this.world.paint(g, panelHeight,
                this.x0, this.y0, this.zoom,
                this.superposed,
                this.nbMachinesPerCol);

        this.drawAxis(g, panelHeight);
    }

    public void drawAxis(Graphics g, double panelHeight) {
        g.setColor(Color.BLACK);
        g.drawLine((int) (this.x0), (int) (panelHeight - this.y0),
                (int) (this.x0 + 10 * this.zoom), (int) (panelHeight - this.y0));
        g.drawLine((int) (this.x0), (int) (panelHeight - this.y0),
                (int) (this.x0), (int) (panelHeight - (this.y0 + 10 * this.zoom)));
    }

    public double getX0() {
        return this.x0;
    }

    public void setX0(double newX0) {
        this.x0 = newX0;
        repaint();
    }

    public double getY0() {
        return this.x0;
    }

    public void setY0(double newY0) {
        this.y0 = newY0;
        repaint();
    }

    public void translate(double dx, double dy) {
        this.x0 += dx;
        this.y0 += dy;
        repaint();
    }

    public double getZoom() {
        return this.zoom;
    }

    public void setZoom(double newZoom) {
        this.zoom = newZoom;
        repaint();
    }

    public void multiplyZoom(double fact) {
        this.zoom *= fact;
        repaint();
    }

    public void zoomOnMouse(double fact, int xMouse, int yMouse) {

        double panelHeight = this.getSize().getHeight();

        x0 = fact * (x0 - xMouse) + xMouse;
        y0 = (panelHeight - yMouse) + fact * (y0 - (panelHeight - yMouse));

        this.zoom *= fact;
        repaint();
    }

    public void resetView() {

        /* Maximal dimensions of the world (blocks + machines). */
        int width = this.getWidth();
        int height = this.getHeight();

        // TODO
        if (this.superposed) {
            this.x0 = width / 2;
            this.y0 = height / 2;
            this.zoom = this.world.getWidth();
        } else {
            this.x0 = 0;
            this.y0 = 0;
            this.zoom = 1;
        }
        repaint();
    }

    public void swipe(int dx, int dy) {
        this.x0 += dx;
        this.y0 += dy;
        repaint();
    }

    public void zoomIn() {
        this.zoom *= 1.1;
        repaint();
    }

    public void zoomOut() {
        this.zoom /= 1.1;
        repaint();
    }

    public void evolve(double dt) {
        this.world.evolve(dt);
        this.repaint();
    }

    public void evolve() {
        this.evolve(this.defaultPeriod);
    }

    public void switchSuperposition() {
        this.superposed = !this.superposed;
        this.repaint();
    }

    public void play() {
        this.timer.start();
        this.world.play();
    }

    public void pause() {
        this.timer.stop();
        this.world.pause();
    }

    public void togglePlayPause() {
        if (this.isRunning) {
            this.isRunning = false;
            this.pause();
        } else {
            this.isRunning = true;
            this.play();
        }
    }

    public void toggleGravity() {
        this.world.toggleGravity();
    }

    public void increaseSpringSize(double fact) {
        this.world.increaseSpringSize(fact);
    }

    /**
     * Stop all machines immediately.
     */
    public void blockSpeeds() {
        world.blockSpeeds();
    }

    public void mutate() {
        world.mutate();
        repaint();
    }

    public void breed() {
        world.breed();
        repaint();
    }

    public void sortMachines() {
        world.sortMachines();
        repaint();
    }

    public void killHalf() {
        world.killHalf();
        repaint();
    }

    public void extendSprings(double dL) {
        world.extendSprings(dL);
    }

    void doCompleteEvolutionStep() {
        System.out.println("do complete evol step");
        world.sortMachines();
        world.killHalf();
        world.breed();
        world.mutate();
        repaint();
    }
}
