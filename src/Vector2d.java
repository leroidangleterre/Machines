/* A simple 2-dimensional vector class. */

public class Vector2d{

	private double x, y;


	public Vector2d(double x, double y){
		this.x = x;
		this.y = y;
	}
	public Vector2d(){
		this(0, 0);
	}

	public Vector2d clone(){
		return new Vector2d(this.x, this.y);
	}
	
	public void add(Vector2d other){
		this.x += other.x;
		this.y += other.y;
	}
	public void add(double dx, double dy){
		this.x += dx;
		this.y += dy;
	}

	public void mult(double factor){
		this.x *= factor;
		this.y *= factor;
	}

	public double getX(){
		return this.x;
	}
	public double getY(){
		return this.y;
	}
	public void setX(double xP){
		this.x = xP;
	}
	public void setY(double yP){
		this.y = yP;
	}

	public String toString(){
		return "v{" + this.x + ", " + this.y + "}";
	}
}
