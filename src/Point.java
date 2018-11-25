

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

public class Point extends Solid{

	private double radius;
	private double mass;
	private double fx, fy; // Force applied on the point.

	private Color color; // Todo
	
	public Point(double x, double y){
		super(x, y);
		this.radius = 10;
		this.mass = 10;
		this.color = Color.RED;
		this.fx = 0;
		this.fy = 0;
	}

	public Point(double x, double y, double radius){
		this(x, y);
		this.radius = radius;
		this.setSpeed(0, 0);
	}

	public Point(double x, double y, double vx, double vy, double radius){
		this(x, y, radius);
		this.setSpeed(vx, vy);
	}
	
	public Point(){
		this(0, 0);
	}


	public void display(Graphics g,
			    double x0, double y0, double zoom,
			    int panelHeight){
		
		int xDisplay = (int)(zoom*this.getX() + x0 - zoom*this.radius);
		int yDisplay = (int)(panelHeight - (zoom*this.getY() + y0) - zoom*this.radius);
		int dx = (int)(zoom*this.radius*2);
		int dy = (int)(zoom*this.radius*2);
		// System.out.println("Point.display(): x = " + xDisplay + ", y = " + yDisplay
		// + ", dx = " + dx + ", dy = " + dy + ", speed(" + this.getSpeedX() + "," + this.getSpeedY() + "), color: " + this.color);

		if(dx==0){
			System.out.println("dx==0; zoom = " + zoom + ", this.radius = " + this.radius);
		}
		
		
		g.setColor(Color.WHITE);
		g.drawOval(xDisplay, yDisplay, dx, dy);
		g.setColor(this.color);
		g.fillOval(xDisplay, yDisplay, dx, dy);
		super.display(g, x0, y0, zoom, panelHeight);
	}

	
	public void razForces(){
		this.fx = 0;
		this.fy = 0;
	}
	
	public void receiveForce(double dfx, double dfy){
		this.fx += dfx;
		this.fy += dfy;
	}


	public void updateSpeed(double dt){
		if(this.mass > 0){
			this.speed.add(this.fx * dt / this.mass,
				       this.fy * dt / this.mass);
		}
	}

	/* Factor:
	   1.0 <-> All kinetic energy is conserved.
	   0.0 <-> No kinetic energy is conserved. */
	public void dampenSpeed(double factor){
		this.speed.mult(factor);
	}

	public void setRadius(double r){
		this.radius = r;
	}
	public double getRadius(){
		return this.radius;
	}


	public void switchColor(){
		if(this.color == Color.RED){
			this.color = Color.BLUE;
		}
		else if(this.color == Color.BLUE){
			this.color = Color.RED;
		}
		else{
			this.color = Color.RED;
		}
	}


	public void setColor(Color param){
		this.color = param;
	}


	/* Return value:
	   - the collision if it happens in the future;
	   - null if no collision happens in the future. */
	public Collision getCollisionWith(Point other){

		Collision res = new Collision(this, other);
		if(res.getDate() >= 0){
			return res;
		}
		else{
			return null;
		}
	}
	
}
