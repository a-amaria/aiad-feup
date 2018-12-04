package beeSimulation;

public class Stats
{
    private int maxHoneyAllBees;
    private double ratioFighterCollector;
    private double maxPercentageAllBees;
    private int initialNectar;
    
    public Stats(int maxHoneyAllBees, double ratioFighterCollector, int initNectar)
    {
	this.maxHoneyAllBees = maxHoneyAllBees;
	this.ratioFighterCollector = ratioFighterCollector;
	this.initialNectar = initNectar;
	this.maxPercentageAllBees = (maxHoneyAllBees/(double)initNectar)*100;
//	System.out.println("max honey all bees: " + maxHoneyAllBees);
//	System.out.println("% max honey bees can collect: " + maxPercentageAllBees);
    }

    public int getMaxHoneyAllBees()
    {
        return maxHoneyAllBees;
    }
    
    public int getInitialNectar()
    {
        return initialNectar;
    }
    
    public double getRatioFighterCollector()
    {
        return ratioFighterCollector;
    }
    
    public double getMaxPercentageAllBees()
    {
        return maxPercentageAllBees;
    }
}
