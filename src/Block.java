/* This class describes a physical element that will interact with the machines.

   A block is essentially an oriented 2d-rectangle.
*/

import java.awt.Color;
import java.awt.Graphics;

public class Block extends Solid{

	private double angle;
	private double width, height;

	/* Value between 0 and 1.
	   if 0: the shock absorbs all the energy of the normal speed;
	   if 1: the energy is conserved.
	*/
	private double elasticity;


	
	public Block(double x, double y, double width, double height, double angle){
		super(x, y);
		this.angle = angle;
		this.width = width;
		this.height = height;
		this.elasticity = .5;
	}


	public void display(Graphics g,
			    double x0, double y0, double zoom,
			    int panelHeight){

		super.display(g, x0, y0, zoom, panelHeight);

		/* The coordinates of the 4 points (A, B, C, D) are computed when needed. */
		
		double s = Math.sin(this.angle);
		double c = Math.cos(this.angle);
		double h = this.height / 2;
		double w = this.width / 2;

		/* Coordinates in the real world. */
		double ax = -h*s + w*c + this.getX();
		double ay = h*c + w*s + this.getY();
		double bx = -h*s - w*c + this.getX();
		double by = h*c - w*s + this.getY();
		double cx = -w*c + h*s + this.getX();
		double cy = -w*s - h*c + this.getY();
		double dx = h*s + w*c + this.getX();
		double dy = -h*c + w*s + this.getY();

		/* Tables of the coordinates converted into panel world. */
		int tabX[] = {(int)(zoom*ax + x0),
			      (int)(zoom*bx + x0),
			      (int)(zoom*cx + x0),
			      (int)(zoom*dx + x0)};
		int tabY[] = {(int)(panelHeight - (zoom*ay + y0)),
			      (int)(panelHeight - (zoom*by + y0)),
			      (int)(panelHeight - (zoom*cy + y0)),
			      (int)(panelHeight - (zoom*dy + y0))};
		
		g.setColor(Color.BLACK);
		g.fillPolygon(tabX, tabY, 4);

		g.setColor(Color.WHITE);
		g.drawPolygon(tabX, tabY, 4);
	}

	public void collide(Machine m, double dt){
		for(int i=0; i<m.getNbPoints(); i++){
			this.collide(m.getPoint(i), dt);
		}
	}


	public void collide(Point p, double dt){

		double c = Math.cos(this.angle);
		double s = Math.sin(this.angle);
		double xConv = (p.getX()-this.getX())*c + (p.getY()-this.getY())*s;
		double yConv = -(p.getX()-this.getX())*s + (p.getY()-this.getY())*c;
		double saveX = xConv;
		double saveY = yConv;
		double dx = 0, dy = 0; // Used when collision on the corners.
		boolean flagCorner = false;
		double overlap; // Overlapping distance, thus always positive when collision.

		/* The speed (vxConv, vyConv) is expressed locally to the block. */
		/* Conversion with the inverse matrix. */
		double vxConv = p.getSpeedX()*c + p.getSpeedY()*s;
		double vyConv = -p.getSpeedX()*s + p.getSpeedY()*c;

		/* This is used to compute the speed after collision with a corner. */
		double vxCorner;
		double vyCorner;
		
		double r = p.getRadius();
		
		boolean flagHoriz, flagVertic;
		int indexCorner = 0; /* Possible values: 0 when no corner collides,
					9, 10, 11, 12 when the corresponding corner collides. */


		if(xConv < -this.width/2 - r){
			// System.out.println("LEFT");
		}
		else if(xConv > this.width/2 + r){
			// System.out.println("RIGHT");
		}
		else{
			if(yConv > this.height/2 + r){
				// System.out.println("TOP");
			}
			else if(yConv < -this.height/2 - r){
				// System.out.println("BOTTOM");
			}
			else{
				// Regions 1 to 12
				if(xConv < -this.width/2){
					// Regions 9, 7, 10
					if(yConv > this.height/2){
						// Region 9
						// System.out.println("9");
						flagCorner = true;
						dx = +this.width/2;
						dy = -this.height/2;
					}
					else if(yConv > -this.height/2){
						// Region 7
						if(vxConv > 0){
							// System.out.println("7");
							overlap = r - (-this.width/2 - xConv);
							vxConv = -vxConv * this.elasticity;
							xConv -= (1+this.elasticity)*overlap;
						}
					}
					else{
						// Region 10
						// System.out.println("10");
						flagCorner = true;
						dx = +this.width/2;
						dy = +this.height/2;
					}
				}
				else if(xConv < this.width/2){
					// Regions 1 to 6
					if(yConv > this.height/2){
						// Region 1
						if(vyConv < 0){
							// System.out.println("1");
							overlap = r - (yConv - this.height/2);
							// The vertical component of the speed gets reflected.
							vyConv = -vyConv * this.elasticity;
							// The point has moved away from the block;
							//it is now located on the surface when the elasticity is zero, or at one radius if the elasticity is one.
							yConv += (1+this.elasticity)*overlap; 
						}
					}
					else if(yConv > -this.height/2){
						// Regions 2 to 5
						// System.out.println("2 3 4 5");
					}
					else{
						// Region 6
						if(vyConv > 0){
							// System.out.println("6");
							overlap = r - (-this.height/2 - yConv);
							vyConv = -vyConv * this.elasticity;
							yConv -= (1+this.elasticity)*overlap;
						}
					}
				}
				else{
					// Regions 12, 8, 11
					if(yConv > this.height/2){
						// Region 12
						// System.out.println("12");
						flagCorner = true;
						dx = -this.width/2;
						dy = -this.height/2;
					}
					else if(yConv > -this.height/2){
						// Region 8
						if(vxConv < 0){
							// System.out.println("8");
							overlap = r - (xConv - this.width/2);
							vxConv = -vxConv * this.elasticity;
							xConv += (1+this.elasticity)*overlap;
						}
					}
					else{
						// Region 11
						// System.out.println("11");
						flagCorner = true;
						dx = -this.width/2;
						dy = +this.height/2;
					}
				}
			}
		}


		/* Detect and compute the collision on a corner.
		   When flagCorner is active, it means that the manhattan distance between the corner and the center
		   of the circle is small enough, but there may not be an actual collision. */
		if(flagCorner){

			double xC = xConv + dx;
			double yC = yConv + dy;
			double norm = Math.sqrt(xC*xC + yC*yC);
			if(norm <= p.getRadius()){

				// Ok, now an actual collision is happening.

				// System.out.println("Collision on corner at distance " + norm);
				double xNormed = xC/norm;
				double yNormed = yC/norm;

				/* Compute the radial and tangential components of the speed. */
				double radSpeed = xNormed * vxConv + yNormed * vyConv;
				double tanSpeed = -yNormed * vxConv + xNormed * vyConv;

				if(radSpeed < 0){
					// The circle is moving towards the corner, which means that the collision will happen.
					/* The radial speed is reflected. */
					radSpeed = -radSpeed;
					/* Reflect the position: The circle is moved away from the collision point
					   on a distance equal to twice the overlapping distance. */
					double dR = p.getRadius() - norm;
					// System.out.println("dR = " + dR);
					xConv += 2*dR*xNormed;
					yConv += 2*dR*yNormed;
				}

				/* Speed expressed relatively to the rectangle, after the corner collision. */
				vxConv = xNormed*radSpeed - yNormed * tanSpeed;
				vyConv = yNormed*radSpeed + xNormed * tanSpeed;
					
					
				
			}
		}

		/* Convert the coordinates back into the initial referential. */
		/* NB: double xConv = (p.getX()-this.getX())*c + (p.getY()-this.getY())*s;
		   double yConv = -(p.getX()-this.getX())*s + (p.getY()-this.getY())*c;
		  
		   which means that:
		   p.x - this.x = xConv*c - yConv*s;
		   p.y - this.y = xConv*s + yConv*c;
		*/

		p.setPos((xConv*c - yConv*s) + this.getX(),
			 (xConv*s + yConv*c) + this.getY());

		p.setSpeed((vxConv*c - vyConv*s),
			   (vxConv*s + vyConv*c));
	}


}
