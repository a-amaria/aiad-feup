/**
 * 
 */
package beeSimulation;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.SimUtilities;
import sajas.core.Agent;
import sajas.core.behaviours.CyclicBehaviour;

public class Buzzer extends Agent
{
    private ContinuousSpace<Object> space;
    private Grid<Object> grid;
    private Context<Object> context;
    private MyBuzzerBehaviour movement;
    private boolean isAlive;

    public Buzzer(ContinuousSpace<Object> space, Grid<Object> grid, Context<Object> context)
    {
	this.space = space;
	this.grid = grid;
	this.context = context;
	this.isAlive=true;
    }
    
    public boolean getIsAlive()
    {
	return isAlive;
    }

    public void setIsAlive(boolean state)
    {
	this.isAlive = state;
    }

    public void setup()
    {
	movement = new MyBuzzerBehaviour(this);
	addBehaviour(movement);
    }

    public void moveTowards(GridPoint pointWithMostBees)
    {
	double angle;
	NdPoint myPoint = space.getLocation(this);

	if (pointWithMostBees == null)
	{
	    NdPoint goalPoint = new NdPoint(RandomHelper.nextIntFromTo(0, 50), RandomHelper.nextIntFromTo(0, 50));
	    angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, goalPoint);
	    space.moveByVector(Buzzer.this, 1, angle, 0);
	    myPoint = space.getLocation(Buzzer.this);
	    grid.moveTo(Buzzer.this, (int) myPoint.getX(), (int) myPoint.getY());
	    return;
	}

	if (!pointWithMostBees.equals(grid.getLocation(this)))
	{
	    NdPoint otherPoint = new NdPoint(pointWithMostBees.getX(), pointWithMostBees.getY());
	    angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
	    space.moveByVector(Buzzer.this, 1, angle, 0);
	    myPoint = space.getLocation(Buzzer.this);
	    grid.moveTo(Buzzer.this, (int) myPoint.getX(), (int) myPoint.getY());
	    return;
	}
    }

    public void killBuzzer()
    {
	setIsAlive(false);
	removeBehaviour(movement);
	context.remove(this);
    }

    public void catchBees()
    {
	GridPoint pt = grid.getLocation(this);
	List<Object> bees = new ArrayList<Object>();
	GridCellNgh<Bee> nghCreator = new GridCellNgh<Bee>(grid, pt, Bee.class, 1, 1);
	List<GridCell<Bee>> gridCells = nghCreator.getNeighborhood(true);

	for (GridCell<Bee> cell : gridCells)
	{
	    for (Object item : cell.items())
	    {
		if (item instanceof Bee)
		{
		    bees.add(item);
		}
	    }
	}

	if (bees.size() > 0)
	{
	    int index = RandomHelper.nextIntFromTo(0, bees.size() - 1);
	    Bee b = (Bee) bees.get(index);
	    int beeSurvival = 6 * b.getFightingSkill();
	    int buzzerSurvival = RandomHelper.nextIntFromTo(0, 100);
	    if (beeSurvival > buzzerSurvival)
	    {
		// vespa morre
		killBuzzer();
	    }
	    else
	    {
		b.killBeel();
	    }
	}
    }

    class MyBuzzerBehaviour extends CyclicBehaviour
    {
	private static final long serialVersionUID = 1L;

	public MyBuzzerBehaviour(Agent a)
	{
	    super(a);
	}

	@Override
	public void action()
	{

	    GridPoint pt = grid.getLocation(Buzzer.this);
	    GridCellNgh<Bee> nghCreator = new GridCellNgh<Bee>(grid, pt, Bee.class, 4, 4);
	    List<GridCell<Bee>> gridCells = nghCreator.getNeighborhood(true);
	    SimUtilities.shuffle(gridCells, RandomHelper.getUniform());

	    GridPoint pointWithMostBees = null;
	    int maxCount = Integer.MIN_VALUE;

	    for (GridCell<Bee> cell : gridCells)
	    {
		if (cell.size() > maxCount)
		{
		    pointWithMostBees = cell.getPoint();
		    maxCount = cell.size();
		}
	    }
	    moveTowards(pointWithMostBees);
	    catchBees();
	}
    }
}