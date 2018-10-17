/**
 * 
 */
package beeSimulation;

import repast.simphony.random.RandomHelper;

//TODO 
// early draft - doesnt include collecting skill, their localization etc etc
public class Flower 
{

	private int currNectar;
	
	public Flower() 
	{
		this.currNectar = RandomHelper.nextIntFromTo(0, 25);
	}
	
	public int getNectar()
	{
		return this.currNectar;
	}

}
