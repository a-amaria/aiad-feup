/**
 * 
 */
package beeSimulation;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import bsh.This;
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
    private int beeSight;
    private List<Hive> hives;

    public Bee(ContinuousSpace<Object> space, Grid<Object> grid, int communicationRadius, int beeSight, List<Hive> hives)
    {   
	this.space = space;
	this.grid = grid;
	this.fightingSkill = RandomHelper.nextIntFromTo(0, 10);
	this.collectingSkill = 10 - this.fightingSkill;
	this.currentNectar = 0;
	this.maxNectar = 30;
	this.communicationRadius = communicationRadius;
	this.isAlive = true;
	this.beeSight = beeSight;
	this.hives = hives;
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

    public int getBeeSight()
    {
	return beeSight;
    }
    
    public void setCurrentNectar(int nectar)
    {
	this.currentNectar = nectar;
    }
 
    public List<Hive> getHives()
    {
	return hives;
    }

    /********************** GETTERS END *************************/

    @ScheduledMethod(start = 1, interval = 1)
    public void step()
    {
	GridPoint pt = grid.getLocation(this);

	System.out.println("at step's beggining");
	if (this.getCurrentNectar() < this.getMaxNectar())
	{

	    GridCellNgh<Flower> nghCreator = new GridCellNgh<Flower>(grid, pt, Flower.class, this.getBeeSight(), this.getBeeSight());
	    List<GridCell<Flower>> gridCells = nghCreator.getNeighborhood(true);

	    Flower flower = new Flower();
	    Flower flowerWithMostNectar = new Flower();
	    GridCell<Flower> cellWithMostNectar = null;
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
			    cellWithMostNectar = cell;
			    flowerWithMostNectar = (Flower) item;
			}
		    }
		}
	    }
	    moveTowards(cellWithMostNectar.getPoint());
	    pt = grid.getLocation(this);
	    GridCellNgh<Flower> closeNghCreator = new GridCellNgh<Flower>(grid, pt, Flower.class, 1, 1);
	    List<GridCell<Flower>> closeGridCells = closeNghCreator.getNeighborhood(true);
	    Boolean inNeighborhood = false;
	    for(GridCell<Flower> neighbor : closeGridCells) 
	    {
		if(neighbor.getPoint().equals(cellWithMostNectar.getPoint())) 
		{
		    inNeighborhood=true;
		    break;
		}
	    }
	    if (cellWithMostNectar != null 
		    && inNeighborhood
		    && flowerWithMostNectar.getCurrNectar() > 0
		    && this.getCollectingSkill() > 0)
	    {
		collectNectar(this.getCurrentNectar(), flowerWithMostNectar);
		return;
	    }
	    System.out.println("Wont collect yet ");
	}

	else if (this.getCurrentNectar() >= this.getMaxNectar())
	{
	    double minDist = Double.MAX_VALUE;
	    GridPoint closerHiveCell = null;
	    for (Hive hive : this.getHives())
	    {
		double dist = Point2D.distance(pt.getX(), pt.getY(), grid.getLocation(hive).getX(), grid.getLocation(hive).getY());
		System.out.println("dist: "+ dist);
		if (dist < minDist)
		{
		    minDist = dist;
		    closerHiveCell = grid.getLocation(hive);
		}
	    }
	    moveTowards(closerHiveCell);
	    return;
	}
	return;
    }

    public void moveTowards(GridPoint pt)
    {
	//System.out.println("MOVE GOAL: [" + pt.getX() + ", " + pt.getY() + "]");
	NdPoint myPoint = space.getLocation(this);
	double angle = 0;
	NdPoint goalPoint;

	if (pt == null)
	{
	    goalPoint = new NdPoint(RandomHelper.nextIntFromTo(0, 50), RandomHelper.nextIntFromTo(0, 50));
	    angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, goalPoint);
	}

	else if (pt != null && !pt.equals(grid.getLocation(this)))
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
	
	int newBeeNectar = beeCurrNectar + Math.min(flowerCurrNectar, this.getCollectingSkill());
	this.setCurrentNectar(Math.min(newBeeNectar, 30));
	return;
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
     * nas flores da zona (looool)}
     */

}
