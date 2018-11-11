/**
 * 
 */
package beeSimulation;

import repast.simphony.context.Context;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public class Flower
{
    private ContinuousSpace<Object> space;
    private Grid<Object> grid;
    private Context<Object> context;

    private int currNectar;

    public Flower(ContinuousSpace<Object> space, Grid<Object> grid, Context<Object> context)
    {
	this.space = space;
	this.grid = grid;
	this.currNectar = RandomHelper.nextIntFromTo(0, 25);
	this.context = context;
    }

    public Flower()
    {
	this.currNectar = -1;
    }

    public int getCurrNectar()
    {
	return currNectar;
    }

    public void setCurrNectar(int currNectar)
    {
	this.currNectar = currNectar;
    }

}
