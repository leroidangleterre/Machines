/* This class describes a target that must be reached by a machine in order to be selected as "good", "efficient".


   At the beginning, the machine is given a target objective (create a class Target).
   We can evaluate how adapted to this target the machine is.
   Examples of objectives include:
   - a location that must be reached by at least one point;
   - a speed that must be reached by at least one point or by mean value
   - a location that must be avoided as much as possible;
   - ...
*/


public class Target{

	/* These attributes describe the type of the target,
	   i.e. what parameters of the machine will be taken into account. */

	public Target(){

	}


	public double eval(Machine m){

		return 0;
	}
}
