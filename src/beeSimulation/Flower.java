/**
 * 
 */
package beeSimulation;

import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

//TODO 
// early draft - doesnt include collecting skill, their localization etc etc
public class Flower
{
    private ContinuousSpace<Object> space;
    private Grid<Object> grid;

    private int currNectar;

    public Flower(ContinuousSpace<Object> space, Grid<Object> grid)
    {
	this.space = space;
	this.grid = grid;
	this.currNectar = RandomHelper.nextIntFromTo(0, 25);
    }
    
    public Flower()
    {
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
