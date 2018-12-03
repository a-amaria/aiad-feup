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
	this.totalHoneyCollected = 0;
	this.nrBeesReturned = 0;
	this.percentageHoneyCollected = 0;
	this.initialNectar = initNectar;
	this.grid=grid;
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
    
    public Grid<Object> getGrid()
    {
	return grid;
    }

    public void incrementHiveStats(int collected)
    {
	nrBeesReturned++;
	totalHoneyCollected+=collected;
	percentageHoneyCollected=(totalHoneyCollected/(double)initialNectar)*100;
	System.out.println("initial nectar: " + initialNectar);
	System.out.println("honey collected: " + totalHoneyCollected);
	System.out.println("% honey collected so far: " + percentageHoneyCollected);
    }
}
