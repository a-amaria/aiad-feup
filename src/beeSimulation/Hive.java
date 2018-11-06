package beeSimulation;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public class Hive
{
    private ContinuousSpace<Object> space;
    private Grid<Object> grid;
    private int totalHoneyCollected;
    private int nrBeesReturned;

    public Hive(ContinuousSpace<Object> space, Grid<Object> grid)
    {
	this.space = space;
	this.grid = grid;
	this.totalHoneyCollected = 0;
	this.nrBeesReturned = 0;
    }
    
    public int getTotalHoneyCollected()
    {
	return this.totalHoneyCollected;
    }
    
    public int getNrBeesReturned()
    {
	return this.nrBeesReturned;
    }

}
