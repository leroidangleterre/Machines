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
import javax.swing.JPanel;
import java.util.ArrayList;
import java.util.Random;

public class Machine{

	private ArrayList<Point>pointList;
	private ArrayList<Spring>springList;


	// Gravity
	private double gx, gy;
	
	/* This constructor creates between 1 and 10 points;
	   each two points have a probability of 0.3 of being linked together.
	 */
	public Machine(){
		this.pointList = new ArrayList<Point>();
		this.springList = new ArrayList<Spring>();

		this.buildMachine(5);

		this.gx = 0;
		this.gy = 0;
	}

	private void buildMachine(int type){
		this.buildMachine(type, false);
	}

	private void buildMachine(int type, boolean addCenter){
		
		switch(type){
		case 0:
			/* Regular polygon */
			int nbPoints = 3;
			double x, y;
			double radius = 5;
			for(int i=0; i<nbPoints; i++){
				// Coordinates around a circle
				x = 5*Math.cos(i*2*Math.PI/nbPoints) + 6;
				y = 5*Math.sin(i*2*Math.PI/nbPoints) + 12;
				this.pointList.add(new Point(x, y, 1));
				// Link this point to each pre-existing point.
				for(int j=0; j<i; j++){
					// System.out.println("     j=" + j);
					// System.out.println("     new spring: " + i + ", " + j);
					this.springList.add(new Spring(this.pointList.get(i), this.pointList.get(j)));
				}
			}
			if(addCenter){
				Point center = new Point(0, 20, 1);
				this.pointList.add(center);
				for(int i=0; i<nbPoints; i++){
					this.springList.add(new Spring(center, this.pointList.get(i)));
				}
			}
			
			break;
		case 1:
			/* Only one sphere. */
			this.pointList.add(new Point(5, 4, 1));
		case 2:
			/* Two spheres. */
			this.pointList.add(new Point(5, 4, 1));
			this.pointList.add(new Point(5, 8, 1));
		case 3:
			/* Three spheres, one spring. */
			this.pointList.add(new Point(5, 4, 1));
			this.pointList.add(new Point(5, 10, 1));
			this.pointList.add(new Point(6, 6.1, 1));
			this.springList.add(new Spring(this.pointList.get(1), this.pointList.get(2)));
		case 4:
			/* Triangular mesh of points. */
			int size = 3;
			ArrayList<ArrayList<Point>> list = new ArrayList<ArrayList<Point>>();
			for(int i=0; i<size; i++){
				list.add(i, new ArrayList<Point>());
				for(int j=0; j<=i; j++){
					Point p = new Point(3*(j - 0.5*(double)i), 2.7*(size-i)+5, 1);
					list.get(i).add(p);
					this.pointList.add(p);
				}
			}
			
			for(int i=0; i<size-1; i++){
				for(int j=0; j<=i; j++){
					Point p0 = list.get(i).get(j);
					Point p1 = list.get(i+1).get(j);
					Point p2 = list.get(i+1).get(j+1);
					this.springList.add(new Spring(p0, p1, 100));
					this.springList.add(new Spring(p0, p2, 100));
					this.springList.add(new Spring(p1, p2, 100));
				}
			}
			break;
		case 5:
			/* Square-based rectangular mesh, with each square getting two diagonals. */
			int width = 10;
			int height = 4;
			double dx = 3;
			
			ArrayList<ArrayList<Point>> list = new ArrayList<
				
			for(int i=0; i<height; i++){
				System.out.println("i = " + i);
				for(int j=0; j<width; j++){
					System.out.println("j = " + j);
					Point p = new Point(dx*((double)j-(double)width/2 + .5), dx*((double)i-(double)height/2 + .5 + 5), 1);
					this.pointList.add(p);
				}
			}
			break;
		default:
			break;
		}
	}


	public void display(Graphics g,
			    double x0, double y0, double zoom,
			    int panelHeight){
		
		for(int i=0; i<this.pointList.size(); i++){
			this.pointList.get(i).display(g,
						      x0, y0, zoom,
						      panelHeight);
		}
		for(int i=0; i<this.springList.size(); i++){
			this.springList.get(i).display(g,
						       x0, y0, zoom,
						       panelHeight);
		}
	}

	private void razForces(){
		for(int i=0; i<this.pointList.size(); i++){
			this.pointList.get(i).razForces();
		}
	}
	
	private void computeForces(double dt){
		this.razForces();
		for(int i=0; i<this.springList.size(); i++){
			this.springList.get(i).applyForce(dt);
		}

		for(int i=0; i<this.pointList.size(); i++){
			this.pointList.get(i).receiveForce(this.gx, this.gy);
		}
	}

	private void updateSpeeds(double dt){
		for(int i=0; i<this.pointList.size(); i++){
			this.pointList.get(i).updateSpeed(dt);
		}
	}
	private void move(double dt){
		for(int i=0; i<this.pointList.size(); i++){
			this.pointList.get(i).move(dt);
		}
	}

	/* When we are about to move the points, the springs
	   are about to get their new length. We must here store their current length
	   as the previous length. */
	private void updateSprings(){
		for(int i=0; i<this.springList.size(); i++){
			this.springList.get(i).updateLength();
		}
	}
	

	/* The evolution process is composed of three steps.
	   1) All forces applied on the points are computed.
	   2) All point speeds are updated.
	   3) All points are moved.
	*/
	public void evolve(double dt){
		// System.out.println("Machine: evolve(" + dt + ");");
		this.computeForces(dt);
		this.updateSpeeds(dt);
		this.updateSprings();
		this.move(dt);
	}

	public int getNbPoints(){
		return this.pointList.size();
	}
	public Point getPoint(int index){
		return this.pointList.get(index);
	}


	public void setGravity(double gx, double gy){
		this.gx = gx;
		this.gy = gy;
	}


	/* Factor:
	   1.0 <-> All kinetic energy is conserved.
	   0.0 <-> No kinetic energy is conserved. */
	public void dampenSpeed(double factor){
		for(int i=0; i<this.pointList.size(); i++){
			this.pointList.get(i).dampenSpeed(factor);
		}
	}


	/* Insert the given collision in the list,
	   while keeping that list order relatively to time. */
	private void insertCollision(Collision newColl, ArrayList<Collision>list){
		
		// System.out.println("Machine.insertCollisions");

		if(list != null){
			int i = 0;
			try{
				Collision coll = list.get(0);
				while(coll != null && coll.happensSoonerThan(newColl)){
					i++;
					coll = list.get(i);
				}
				// Element at index (i-1) is sooner than new collision; inserting at position i.
			}
			catch(IndexOutOfBoundsException e){
				// No element at index i (list ends at i-1); inserting at position i.
			}
			list.add(i, newColl);
		}
		
		// System.out.println("Machine.insertCollisions end");
	}

	

	/* Detect the collisions between the points of this machine,
	   and modify the speeds of all colliding points. */
	public ArrayList<Collision> findSelfCollisions(){
		
		// System.out.println("Machine.findSelfCollisions");

		ArrayList<Collision> list = new ArrayList<Collision>();

		// TODO
		for(int i=0; i<this.pointList.size(); i++){
			Point a = this.pointList.get(i);
			for(int j=i+1; j<this.pointList.size(); j++){
				Point b = this.pointList.get(j);

				Collision coll = a.getCollisionWith(b);
				
				this.insertCollision(coll, list);
			}
		}
		
		// System.out.println("Machine.findSelfCollisions end");
		return list;
	}
	
	public void increaseSpringSize(double fact){
		for(int i=0; i<this.springList.size(); i++){
			this.springList.get(i).increaseSize(fact);
		}
	}
}
