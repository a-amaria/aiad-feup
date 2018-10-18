/**
 * 
 */
package beeSimulation;


import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public class Buzzer 
{

	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	
	public Buzzer(ContinuousSpace<Object> space, Grid<Object> grid) 
	{
		this.space = space;
		this.grid = grid;
	}
	
	@ScheduledMethod(start=1, interval=1)
	public void step()
	{
		// TODO
	}
	
	
	public void moveTowards(GridPoint pt) 
	{
		if (!pt.equals(grid.getLocation(this))) 
		{
			NdPoint myPoint = space.getLocation(this);
			NdPoint goalPoint = new NdPoint(pt.getX(), pt.getY());
			double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, goalPoint);
			space.moveByVector(this, 1, angle, 0);
			
			//updating position in the grid
			myPoint = space.getLocation(this);
			grid.moveTo(this, (int) myPoint.getX(), (int) myPoint.getY());
		}
	}

}
