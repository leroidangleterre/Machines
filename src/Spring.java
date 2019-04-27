
import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

public class Spring{

    private Point start, end;
    private double l0; // Default length;
    private double prevLength;
    private double strength;
    private double damping; // 0 <-> no damping, 1 <-> max damping. TODO

    public Spring(Point a, Point b){
        this.start = a;
        this.end = b;
        this.l0 = this.getLength();
        this.prevLength = this.l0;
        this.strength = 200;
        this.damping = 1000.0;
    }

    public Spring(Point a, Point b, double strength){
        this(a, b);
        this.setStrength(strength);
    }

    public double getLength(){
        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    public void setL0(double l0){
        this.l0 = l0;
    }

    public void setStrength(double param){
        if(param > 0){
            this.strength = param;
        }
    }

    public void display(Graphics g,
            double x0, double y0, double zoom,
            int panelHeight){
        g.setColor(Color.BLUE);

        int xDisplay0 = (int) (zoom * this.start.getX() + x0);
        int yDisplay0 = (int) (panelHeight - (zoom * this.start.getY() + y0));
        int xDisplay1 = (int) (zoom * this.end.getX() + x0);
        int yDisplay1 = (int) (panelHeight - (zoom * this.end.getY() + y0));

        g.drawLine(xDisplay0, yDisplay0, xDisplay1, yDisplay1);
    }

    /* The spring applies a force on both its points (start and end),
	   which modifies the speed of these points. */
    public void applyForce(double dt){
        double dl = this.getLength() - this.l0;
        // System.out.println("dl = " + dl + ", l = " + this.getLength());
        double dx = this.end.getX() - this.start.getX();
        double dy = this.end.getY() - this.start.getY();

        /* (dfx, dfy) is the force applied on the end of the spring.
		   This is a dampen spring, the force it applies depends on its contraction
		   or elongation (dfElastic)
		   and on the rate at which it changes its length (dfDamping).
         */
        double dfxElastic;
        double dfxDamping;
        double dfx;
        double dfyElastic;
        double dfyDamping;
        double dfy;

        // System.out.println("Spring.applyForce(): length = " + this.getLength());
        if(this.getLength() > 0.001){

            dfxElastic = -(this.strength * dl * (dx / this.l0));
            dfxDamping = this.damping * (this.prevLength - this.getLength()) * (dx / this.l0);
            dfx = (dfxElastic + dfxDamping);

            dfyElastic = -(this.strength * dl * (dy / this.l0));
            dfyDamping = this.damping * (this.prevLength - this.getLength()) * (dy / this.l0);
            dfy = (dfyElastic + dfyDamping);

            /* System.out.println("Damping: dfx = " + dfxDamping
			   + ", dfy = " + dfyDamping
			   + ", Elastic: dfx = " + dfxElastic
			   + ", dfy = " + dfyElastic);
             */
        } else{
            // Special case where the spring is reduced to a point. The reaction is random.
            // System.out.println("same point.");
            Random r = new Random();
            double fact = 100;
            dfx = 10 * (r.nextDouble() - 0.5);
            dfy = 10 * (r.nextDouble() - 0.5);
        }

        this.end.receiveForce(dfx, dfy);
        // The start of the spring receives the opposite of the force applied on the end of the spring.
        this.start.receiveForce(-dfx, -dfy);
    }

    public void updateLength(){
        this.prevLength = this.getLength();
    }

    public void increaseSize(double fact){
        this.prevLength = this.l0;
        this.l0 = this.l0 * fact;
    }
}
