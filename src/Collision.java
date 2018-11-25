public class Collision{

	private Solid solid0;
	private Solid solid1;

	private double date; /* Negative value: Collision in the past or no collision at all;
				Positive value: collision in the future. */

	public Collision(Solid s0, Solid s1){

		this.solid0 = s0;
		this.solid1 = s1;

		this.computeDate();
	}

	/* This function determines whether the collision will actually happen;
	   if so, it sets the date of the collision. */
	private void computeDate(){
		this.date = -1; // default value.
		// TODO
	}

	public double getDate(){
		return this.date;
	}


	public boolean happensSoonerThan(Collision other){
		return this.date < other.date;
	}
	

	/* Solve the collision.
	   - Step 1): bring the two colliding solids together by having the world evolve;
	   - Step 2): change the speeds of the two involved solids.
	*/
	public void solve(World w){

	}
}
