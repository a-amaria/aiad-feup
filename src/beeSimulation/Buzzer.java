/**
 * 
 */
package beeSimulation;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.context.Context;
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
import repast.simphony.util.SimUtilities;

public class Buzzer
{
    private ContinuousSpace<Object> space;
    private Grid<Object> grid;

    public Buzzer(ContinuousSpace<Object> space, Grid<Object> grid)
    {
	this.space = space;
	this.grid = grid;
    }

    @ScheduledMethod(start = 1, interval = 1)
    public void run()
    {
	GridPoint pt = grid.getLocation(this);
	GridCellNgh<Bee> nghCreator = new GridCellNgh<Bee>(grid, pt, Bee.class, 1, 1);
	List<GridCell<Bee>> gridCells = nghCreator.getNeighborhood(true);
	SimUtilities.shuffle(gridCells, RandomHelper.getUniform());

	GridPoint pointWithLeastBees = null;
	int minCount = Integer.MAX_VALUE;

	for (GridCell<Bee> cell : gridCells)
	{
	    if (cell.size() < minCount)
	    {
		pointWithLeastBees = cell.getPoint();
		minCount = cell.size();
	    }
	}
	moveTowards(pointWithLeastBees);
    }

    public void moveTowards(GridPoint pt)
    {
	if (!pt.equals(grid.getLocation(this)))
	{
	    NdPoint myPoint = space.getLocation(this);
	    NdPoint otherPoint = new NdPoint(pt.getX(), pt.getY());
	    double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);

	    space.moveByVector(this, 2, angle, 0);
	    myPoint = space.getLocation(this);
	    grid.moveTo(this, (int) myPoint.getX(), (int) myPoint.getY());
	    catchBees();
	}
    }

    public void catchBees()
    {
	GridPoint pt = grid.getLocation(this);
	List<Object> bees = new ArrayList<Object>();

	for (Object obj : grid.getObjectsAt(pt.getX(), pt.getY()))
	{
	    if (obj instanceof Bee)
	    {
		bees.add(obj);
	    }
	}
	if (bees.size() > 0)
	{
	    int index = RandomHelper.nextIntFromTo(0, bees.size() - 1);
	    Object bee = bees.get(index);

	    Bee b = (Bee) bees.get(index);
	    if (b.getFightingSkill() < 10)
	    { // Abelha tem de fugir ou entao morre
		System.out.println("Abelha morre após luta com vespa forte.. ou foge?");
		NdPoint spacePt = space.getLocation(bee);
		Context<Object> context = ContextUtils.getContext(bee);
		context.remove(bee);
	    }
	    else
	    {
		// probabilidade da vespa sobreviver ---> numero random entre 0 e
		// fightingskill*9
		// Se for > 15 vive, senao morre (aleatorio)
		int probability = RandomHelper.nextIntFromTo(0, b.getFightingSkill() * 6);
		if (probability < 15)
		{
		    // vespa morre
		    System.out.println("Vespa morre após luta com abelha forte");
		    NdPoint spacePt = space.getLocation(this);
		    Context<Object> context = ContextUtils.getContext(this);
		    context.remove(this);
		}
		else
		{
		    // vespa vive e continua a sua vida
		    System.out.println("Vespa vive após luta com abelha forte");
		}
	    }
	}
    }
}
