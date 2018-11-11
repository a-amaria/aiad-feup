package beeSimulation;

public class Hive
{
    private int totalHoneyCollected;
    private int nrBeesReturned;

    public Hive()
    {
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

    public void incrementHiveStats(int collected)
    {
	nrBeesReturned++;
	totalHoneyCollected+=collected;
    }
    

}
