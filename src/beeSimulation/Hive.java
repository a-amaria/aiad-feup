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
	System.out.println("initial nectar: " + initialNectar);
	System.out.println("honey collected: " + totalHoneyCollected);
	System.out.println("% honey collected so far: " + percentageHoneyCollected);
	System.out.println("max all bees can carry: " + getMaxAllBees());
	System.out.println("% max q as abelhas podem atingir/cap: " + getBeeCap());
	System.out.println("ratio fighter-collector: " + getRatio());
	System.out.println("bees performance % " + getPerformance());
	System.out.println("ratio max all bees can carry/100: " + getRatioBees());
	System.out.println("nr survivors: " + getSurvivors());
    }

    private double getBeeCap()
    {
	double beeCap = 0.0;
	for (Object obj : getGrid().getObjects())
	{
	    if (obj instanceof Stats)
	    {
		beeCap = ((Stats) obj).getMaxPercentageAllBees();
	    }
	}
	return beeCap;
    }

    private int getMaxAllBees()
    {
	int allBeesCapacity = 0;
	for (Object obj : getGrid().getObjects())
	{
	    if (obj instanceof Stats)
	    {
		allBeesCapacity = ((Stats) obj).getMaxHoneyAllBees();
	    }
	}
	return allBeesCapacity;
    }
    
    private int getSurvivors()
    {
	int survivors = 0;
	for (Object obj : getGrid().getObjects())
	{
	    if (obj instanceof Stats)
	    {
		survivors = ((Stats) obj).getNrBeeSurvivors();
	    }
	}
	return survivors;
    }
    
    private double getRatio()
    {
	double ratio = 0.0;
	for (Object obj : getGrid().getObjects())
	{
	    if (obj instanceof Stats)
	    {
		ratio = ((Stats) obj).getRatioFighterCollector();
	    }
	}
	return ratio;
    }
    
    private double getRatioBees()
    {
	double ratio = 0.0;
	for (Object obj : getGrid().getObjects())
	{
	    if (obj instanceof Stats)
	    {
		ratio = ((Stats) obj).getRatioBeeCapacity();
	    }
	}
	return ratio;
    }
    
    private double getPerformance()
    {
	double ratio = 0.0;
	for (Object obj : getGrid().getObjects())
	{
	    if (obj instanceof Stats)
	    {
		ratio = ((Stats) obj).getOverallBeesPerformance();
	    }
	}
	return ratio;
    }
}
