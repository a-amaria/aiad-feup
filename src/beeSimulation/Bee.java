/**
 * 
 */
package beeSimulation;

import java.util.List;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.SimUtilities;

public class Bee 
{

	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	private int fightingSkill;
	private int collectingSkill;
	private int currentNectar;
	private int maxNectar;
	
	public Bee(ContinuousSpace<Object> space, Grid<Object> grid) 
	{
		this.space = space;
		this.grid = grid;
		this.fightingSkill = RandomHelper.nextIntFromTo(0, 10);
		this.collectingSkill = 10-this.fightingSkill;
		this.currentNectar = 0;
		this.maxNectar = 100;
	}
	
	@ScheduledMethod(start=1, interval=1)
	public void step()
	{
		GridPoint pt = grid.getLocation(this);
		  
		GridCellNgh <Flower> nghCreator = new GridCellNgh <Flower>(grid, pt, Flower.class, 1, 1);
		List <GridCell <Flower>> gridCells = nghCreator.getNeighborhood(true); 
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		
		Flower flowerWithMostNectar = null;
		GridPoint cellWithMostNectar = null;
		double max = -1.0;
		for (GridCell<Flower> cell : gridCells)
		{
			for (Flower flower : cell.items())
			{
				if (flower.getNectar() > max)
				{
					flowerWithMostNectar = flower;
					max = flower.getNectar();
					break;
				}
			}
			cellWithMostNectar = cell.getPoint();
		}
		moveTowards(cellWithMostNectar);
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
