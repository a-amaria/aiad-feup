package beeSimulation;

import repast.simphony.space.grid.Grid;

public class Hive
{
    private int totalHoneyCollected;
    private int nrBeesReturned;
    private double percentageHoneyCollected;
    private int initialNectar;
    private Grid<Object> grid;

    public Hive(Grid<Object> grid, int initNectar)
    {
	this.grid = grid;
	this.totalHoneyCollected = 0;
	this.nrBeesReturned = 0;
	this.percentageHoneyCollected = 0;
	this.initialNectar = initNectar;
    }

    public Grid<Object> getGrid()
    {
	return grid;
    }

    public int getTotalHoneyCollected()
    {
	return this.totalHoneyCollected;
    }

    public int getNrBeesReturned()
    {
	return this.nrBeesReturned;
    }

    public double getPercentageHoneyCollected()
    {
	return this.percentageHoneyCollected;
    }

    public void incrementHiveStats(int collected)
    {
	nrBeesReturned++;
	totalHoneyCollected+=collected;
	percentageHoneyCollected=(totalHoneyCollected/(double)initialNectar)*100;
    }
}
