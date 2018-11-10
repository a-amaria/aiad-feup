package beeSimulation;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public class Hive
{
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	private int totalHoneyCollected;
	private int nrBeesReturned;
	private int nrTotalNectar;

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

	public void setTotalHoneyCollected(int totalHoneyCollected) 
	{
		this.totalHoneyCollected += totalHoneyCollected;
	}

	public void setNrBeesReturned() 
	{
		this.nrBeesReturned++;
	}

	public int getNrTotalNectar() {
		return nrTotalNectar;
	}

	public void setNrTotalNectar(int nrTotalNectar) {
		this.nrTotalNectar = nrTotalNectar;
	}

	public void printHoneyCollected() 
	{
		System.out.println(totalHoneyCollected);
	}
}