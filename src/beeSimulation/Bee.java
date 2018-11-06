/**
 * 
 */
package beeSimulation;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;

public class Bee
{

    private ContinuousSpace<Object> space;
    private Grid<Object> grid;
    private int fightingSkill;
    private int collectingSkill;
    private int currentNectar;
    private int maxNectar;
    private int communicationRadius;
    private boolean isAlive;

    public Bee(ContinuousSpace<Object> space, Grid<Object> grid, int communicationRadius)
    {
	this.space = space;
	this.grid = grid;
	this.fightingSkill = RandomHelper.nextIntFromTo(0, 10);
	this.collectingSkill = 10 - this.fightingSkill;
	this.currentNectar = 0;
	this.maxNectar = 100;
	this.communicationRadius = communicationRadius;
	this.isAlive = true;
    }

    /********************** GETTERS *************************/

    public ContinuousSpace<Object> getSpace()
    {
	return space;
    }

    public Grid<Object> getGrid()
    {
	return grid;
    }

    public int getFightingSkill()
    {
	return fightingSkill;
    }

    public int getCollectingSkill()
    {
	return collectingSkill;
    }

    public int getCurrentNectar()
    {
	return currentNectar;
    }

    public int getMaxNectar()
    {
	return maxNectar;
    }

    public int getCommunicationRadius()
    {
	return communicationRadius;
    }

    public boolean getIsAlive()
    {
	return isAlive;
    }

    public void setCurrentNectar(int nectar)
    {
	this.currentNectar = nectar;
    }

    /********************** GETTERS END *************************/

    @ScheduledMethod(start = 1, interval = 1)
    public void step()
    {
	GridPoint pt = grid.getLocation(this);

	if (this.getCurrentNectar() < this.getMaxNectar())
	{

	    GridCellNgh<Flower> nghCreator = new GridCellNgh<Flower>(grid, pt, Flower.class, 15, 15);
	    List<GridCell<Flower>> gridCells = nghCreator.getNeighborhood(true);
 
	    Flower flower = new Flower();
	    Flower flowerWithMostNectar = new Flower();
	    GridPoint cellWithMostNectar = null;
	    double max = -1.0;

	    for (GridCell<Flower> cell : gridCells)
	    {
		for (Object item : cell.items())
		{
		    if (item instanceof Flower)
		    {
			flower = (Flower) item;
			if (flower.getCurrNectar() > max)
			{
			    max = flower.getCurrNectar();
			    cellWithMostNectar = cell.getPoint();
			    flowerWithMostNectar = (Flower) item;
			}
		    }
		}
	    }
	    System.out.println("Im about to move ");
	    moveTowards(cellWithMostNectar);
	    System.out.println("Just moved ");
	    if (cellWithMostNectar != null 
		    && pt.getX() == cellWithMostNectar.getX() && pt.getY() == cellWithMostNectar.getY() 
		    && flowerWithMostNectar.getCurrNectar() > 0
		    && this.getCollectingSkill() > 0)
	    {
		System.out.println("Im about to collect ");
		collectNectar(this.getCurrentNectar(), flowerWithMostNectar);
	    }
	    System.out.println("Wont collect yet ");
	}

	else
	{
	    System.out.println("Full of honey ");
	    GridCellNgh<Hive> nghCreator = new GridCellNgh<Hive>(grid, pt, Hive.class, 40, 40);
	    List<GridCell<Hive>> gridCells = nghCreator.getNeighborhood(true);

	    double minDist = Double.MAX_VALUE;
	    GridPoint closerHiveCell = null;
	    for (GridCell<Hive> cell : gridCells)
	    {
		double dist = Point2D.distance(pt.getX(), pt.getY(), cell.getPoint().getX(), cell.getPoint().getY());
		if (dist < minDist)
		{
		    minDist = dist;
		    closerHiveCell = cell.getPoint();
		}
	    }
	    moveTowards(closerHiveCell);
	}
	// fightWasps(null);
    }

    public void moveTowards(GridPoint pt)
    {
	NdPoint myPoint = space.getLocation(this);
	double angle = 0;
	NdPoint goalPoint;

	if (pt == null)
	{
	    goalPoint = new NdPoint(RandomHelper.nextIntFromTo(0, 50), RandomHelper.nextIntFromTo(0, 50));
	    angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, goalPoint);
	}

	if (pt != null && !pt.equals(grid.getLocation(this)))
	{
	    goalPoint = new NdPoint(pt.getX(), pt.getY());
	    angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, goalPoint);
	}

	space.moveByVector(this, 1, angle, 0);
	// updating position in the grid
	myPoint = space.getLocation(this);
	grid.moveTo(this, (int) myPoint.getX(), (int) myPoint.getY());
    }

    public void collectNectar(int beeCurrNectar, Flower flower)
    {
	System.out.println("IM COLLECTING ");
	int flowerCurrNectar = flower.getCurrNectar();
	if ((flowerCurrNectar - this.getCollectingSkill()) < 0)
	    flower.setCurrNectar(0);
	else
	    flower.setCurrNectar(flowerCurrNectar - this.getCollectingSkill());
	this.setCurrentNectar(beeCurrNectar + Math.min(flowerCurrNectar, this.getCollectingSkill()));
	System.out.println("no flower nectar or skill below zero ");
	return;
    }

    public void fightWasps(GridCell<Buzzer> buzz)
    {
	GridPoint myCurrPoint = grid.getLocation(this);

	GridCellNgh<Buzzer> nghCreator = new GridCellNgh<Buzzer>(grid, myCurrPoint, Buzzer.class, 1, 1);
	List<GridCell<Buzzer>> gridCells = nghCreator.getNeighborhood(true);
	SimUtilities.shuffle(gridCells, RandomHelper.getUniform());

	GridPoint pointWithLeastWasps = null;
	int min = Integer.MAX_VALUE;

	for (GridCell<Buzzer> cell : gridCells)
	{
	    if (cell.size() < min)
	    {
		pointWithLeastWasps = cell.getPoint();
		min = cell.size();
	    }
	}

	if (this.getFightingSkill() < 5 && this.getIsAlive())
	    moveTowards(pointWithLeastWasps);
    }

    /*
     * public void messageBees(GridPoint flowerPoint, Flower flower, String type,
     * int commRadius) // danger or flower with // a lot of nectar { if
     * (flower.getCurrNectar() > 0) { GridPoint myCurrLocation =
     * grid.getLocation(this); List<Object> flowers = new ArrayList<Object>(); for
     * (Object obj : grid.getObjectsAt(myCurrLocation.getX(),
     * myCurrLocation.getY())) { if (obj instanceof Flower) flowers.add(obj); } }
     * return; } // separar em duas diferentes?
     * 
     * public void actionAfterRecMsg() { // no caso da luta - se calhar as q
     * chegaram deviam ir comunicando q ja chegaram // ate chegar a um certo numero,
     * e // caso ja tenham ido muitas n vai // no caso do mel - mais complexo,
     * avaliar as cargas das q estao mais perto e a // quantidade de nectar q tem
     * nas flores da zona }
     */

}
