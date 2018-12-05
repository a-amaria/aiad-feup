package beeSimulation;

import java.util.List;

public class Stats
{
    private int maxHoneyAllBees;
    private double ratioFighterCollector;
    private double maxPercentageAllBees;
    private int initialNectar;
    private List<Hive> hives;
    private int nrBeeSurvivors;

    public Stats(int maxHoneyAllBees, double ratioFighterCollector, int initNectar, List<Hive> hives)
    {
	this.maxHoneyAllBees = maxHoneyAllBees;
	this.ratioFighterCollector = ratioFighterCollector;
	this.initialNectar = initNectar;
	this.maxPercentageAllBees = (maxHoneyAllBees/(double)initNectar)*100;
	this.hives = hives;
	this.nrBeeSurvivors = 0;
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
    
    public double getRatioBeeCapacity()
    {
	return this.maxPercentageAllBees/100;
    }

    public double getMaxPercentageAllBees()
    {
	return maxPercentageAllBees;
    }

    public double getOverallBeesPerformance()
    {
	double overallBeesPerformance = 0.0;
	for (Hive hive : hives)
	    overallBeesPerformance += hive.getPercentageHoneyCollected();
	return overallBeesPerformance;
    }
    
    public int getNrBeeSurvivors()
    {
	int totalNrBeeSurvivors = 0;
	for (Hive hive : hives)
	    totalNrBeeSurvivors += hive.getNrBeesReturned();
	return totalNrBeeSurvivors;
    }
}
