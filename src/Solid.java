/* This class describes an object that can translate and rotate. */

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Solid{

	protected Vector2d pos, prevPos; // Current and previous positions.
	protected Vector2d speed;
	private ArrayList<Vector2d>trail;
	private int maxTrailSize;

	private int date;
	private int trailPeriod;

	public Solid(double xP, double yP){
		this.pos = new Vector2d(xP, yP);
		this.prevPos = this.pos.clone();
		this.trail = new ArrayList<Vector2d>();
		this.maxTrailSize = 50;
		this.date = 0;
		this.trailPeriod = 10;
		this.speed = new Vector2d();
	}

	public double getX(){
		return this.pos.getX();
	}
	public double getY(){
		return this.pos.getY();
	}

	public void setPos(double x, double y){
		this.pos = new Vector2d(x, y);
	}

	public void setSpeedX(double vx){
		this.speed.setX(vx);
	}
	public void setSpeedY(double vy){
		this.speed.setY(vy);
	}
	public void setSpeed(double vx, double vy){
		this.speed = new Vector2d(vx, vy);
	}
	public double getSpeedX(){
		return this.speed.getX();
	}
	public double getSpeedY(){
		return this.speed.getY();
	}
	public Vector2d getSpeed(){
		return this.speed.clone();
	}

	public void updateTrail(){

		this.date++;
		if(this.trailPeriod*(this.date/this.trailPeriod) == this.date){
			
		
			/* Add the current position at the first rank in the trail. */
			this.trail.add(0, pos.clone());

			/* Keep the trail at or below the max of authorized positions. */
			try{
				if(this.maxTrailSize >= 0){
					if(this.trail.size() >= this.maxTrailSize){
						// System.out.println("trail is too long, current =" + this.trail.size() + ", max = " + this.maxTrailSize);
						this.trail.remove(this.trail.size()-1);
					}
				}
				// If the max is set to -1, it means infinite track.
			}
			catch(IndexOutOfBoundsException e){
				System.out.println("Trail error");
			}
		}
	}

	public void translate(double dx, double dy){

		/* Move the solid. */
		this.pos.add(new Vector2d(dx, dy));
	}

	
	/* Move the point according to its current speed. */
	public void move(double dt){
		
		this.prevPos = this.pos.clone();
		
		this.translate(dt * this.speed.getX(), dt * this.speed.getY());
		this.updateTrail();
	}

	
	/* Rotate the solid around the point of coordinates (x0, y0). */
	public void rotate(double angle, double x0, double y0){
		// TODO


	}
	
	public void display(Graphics g,
			    double x0, double y0, double zoom,
			    int panelHeight){

		this.displayTrack(g, x0, y0, zoom, panelHeight);
	}
	
	
	public void displayTrack(Graphics g,
				 double x0, double y0, double zoom,
				 int panelHeight){

		// System.out.println("Solid.displayTrack: size = " + this.trail.size());
		/* Display the list of previous positions of the solid. */
		
		for(int i=0; i<this.trail.size(); i++){

			/* Real coordinates of the position. */
			double x = this.trail.get(i).getX();
			double y = this.trail.get(i).getY();

			/* Coordinates on the panel. */
			int x2 = (int)(zoom*x + x0);
			int y2 = (int)(panelHeight - (zoom*y + y0));

			g.setColor(Color.BLACK);
			g.fillOval(x2-1, y2-1, 3, 3);
		}
	}
}
