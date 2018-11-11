/**
 * 
 */
package beeSimulation;

import java.awt.geom.Point2D;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import sajas.core.Agent;
import sajas.core.behaviours.CyclicBehaviour;

public class Bee extends Agent
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
    private GridCell<Flower> cellWithMostNectar;
    private Flower flowerWithMostNectar;
    private Context<Object> context;
    private MyBeeBehaviour movement;

    public Bee(ContinuousSpace<Object> space, Grid<Object> grid, int communicationRadius, int beeSight,
	    List<Hive> hives, Context<Object> context)
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
	this.cellWithMostNectar = null;
	this.flowerWithMostNectar = new Flower();
	this.context = context;
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

    public void setIsAlive(boolean state)
    {
	this.isAlive = state;
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

    public void setHives(List<Hive> hives)
    {
	this.hives = hives;
    }

    public GridCell<Flower> getCellWithMostNectar()
    {
	return cellWithMostNectar;
    }

    public void setCellWithMostNectar(GridCell<Flower> cellWithMostNectar)
    {
	this.cellWithMostNectar = cellWithMostNectar;
    }

    public Flower getFlowerWithMostNectar()
    {
	return flowerWithMostNectar;
    }

    public void setFlowerWithMostNectar(Flower flowerWithMostNectar)
    {
	this.flowerWithMostNectar = flowerWithMostNectar;
    }

    /********************** GETTERS END *************************/

    public void setup()
    {
	movement = new MyBeeBehaviour(this);
	addBehaviour(movement);
    }

    class MyBeeBehaviour extends CyclicBehaviour
    {
	private static final long serialVersionUID = 1L;

	public MyBeeBehaviour(Agent a)
	{
	    super(a);
	}

	@Override
	public void action()
	{
	    GridPoint pt = getGrid().getLocation(Bee.this);
	    if (getCurrentNectar() >= getMaxNectar() || (smellRemainingNectar() == 0))
	    {
		double minDist = Double.MAX_VALUE;
		GridPoint closerHiveCell = null;
		for (Hive hive : getHives())
		{
		    double dist = Point2D.distance(pt.getX(), pt.getY(), getGrid().getLocation(hive).getX(),
			    getGrid().getLocation(hive).getY());
		    if (dist < minDist)
		    {
			minDist = dist;
			closerHiveCell = getGrid().getLocation(hive);
		    }
		}
		moveTowards(closerHiveCell);
		return;
	    }

	    else if (getCurrentNectar() < getMaxNectar())
	    {
		GridCellNgh<Flower> nghCreator = new GridCellNgh<Flower>(getGrid(), pt, Flower.class, getBeeSight(),
			getBeeSight());

		List<GridCell<Flower>> gridCells = nghCreator.getNeighborhood(true);
		Flower flower = new Flower();
		double max = getFlowerWithMostNectar().getCurrNectar();
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
				setCellWithMostNectar(cell);
				setFlowerWithMostNectar((Flower) item);
			    }
			}
		    }
		}

		if (getCellWithMostNectar() == null)
		{
		    moveTowards(null);
		    return;
		}
		moveTowards(getCellWithMostNectar().getPoint());

		pt = getGrid().getLocation(Bee.this);
		GridCellNgh<Flower> closeNghCreator = new GridCellNgh<Flower>(getGrid(), pt, Flower.class, 1, 1);
		List<GridCell<Flower>> closeGridCells = closeNghCreator.getNeighborhood(true);
		Boolean inNeighborhood = false;
		for (GridCell<Flower> neighbor : closeGridCells)
		{
		    if (neighbor.getPoint().equals(getCellWithMostNectar().getPoint()))
		    {
			inNeighborhood = true;
			break;
		    }
		}
		if (getCellWithMostNectar() != null && inNeighborhood && getFlowerWithMostNectar().getCurrNectar() > 0
			&& getCollectingSkill() > 0)
		{
		    collectNectar(getCurrentNectar(), getFlowerWithMostNectar());
		    return;
		}
		System.out.println("Wont collect yet ");
	    }
	    return;
	}
    }

    public void moveTowards(GridPoint pt)
    {
	NdPoint myPoint = space.getLocation(Bee.this);
	double angle = 0;
	NdPoint goalPoint;

	if (pt == null)
	{
	    goalPoint = new NdPoint(RandomHelper.nextIntFromTo(0, 50), RandomHelper.nextIntFromTo(0, 50));
	    angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, goalPoint);
	}

	else if (pt != null && pt != getGrid().getLocation(Bee.this))
	{
	    goalPoint = new NdPoint(pt.getX(), pt.getY());
	    angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, goalPoint);
	}

	space.moveByVector(Bee.this, 1, angle, 0);
	// updating position in the grid
	myPoint = space.getLocation(Bee.this);
	getGrid().moveTo(Bee.this, (int) myPoint.getX(), (int) myPoint.getY());
    }

    public void collectNectar(int beeCurrNectar, Flower flower)
    {
	int flowerCurrNectar = flower.getCurrNectar();
	if ((flowerCurrNectar - getCollectingSkill()) < 0)
	    flower.setCurrNectar(0);
	else
	    flower.setCurrNectar(flowerCurrNectar - getCollectingSkill());

	int newBeeNectar = beeCurrNectar + Math.min(flowerCurrNectar, getCollectingSkill());
	setCurrentNectar(Math.min(newBeeNectar, 30));
	return;
    }

    public int smellRemainingNectar()
    {
	int remainingNectar = 0;
	for (Object obj : getGrid().getObjects())
	{
	    if (obj instanceof Flower)
	    {
		remainingNectar += ((Flower) obj).getCurrNectar();
	    }
	}
	return remainingNectar;
    }

    private void checkSimulationOver()
    {
	for (Object obj : grid.getObjects())
	{
	    if (obj instanceof Bee)
	    {
		if (((Bee) obj).getIsAlive() )
			//|| !((Bee) obj).getIsAtHive())
		    return;
	    }
	}
	RunEnvironment.getInstance().endRun();
    }

    public void killBee()
    {
	setIsAlive(false);
	removeBehaviour(movement);
	context.remove(this);
	checkSimulationOver();
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
