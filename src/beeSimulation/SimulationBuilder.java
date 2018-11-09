/**
 * 
 */
package beeSimulation;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;

public class SimulationBuilder implements ContextBuilder<Object>
{

    public SimulationBuilder()
    {

    }

    @Override
    public Context build(Context<Object> context)
    {
	NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>("death network", context, true);
	netBuilder.buildNetwork();

	context.setId("beeSimulation");

	ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
	ContinuousSpace<Object> space = spaceFactory.createContinuousSpace("space", context,
		new RandomCartesianAdder<Object>(), new repast.simphony.space.continuous.WrapAroundBorders(), 50, 50);

	GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);

	Grid<Object> grid = gridFactory.createGrid("grid", context, new GridBuilderParameters<Object>(
		new WrapAroundBorders(), new SimpleGridAdder<Object>(), true, 50, 50));

	Parameters params = RunEnvironment.getInstance().getParameters();
	int beeCount = params.getInteger("bee_count");
	int flowerCount = params.getInteger("flower_count");
	int communicationRadius = params.getInteger("comm_radius");
	int hiveCount = params.getInteger("hive_count");
	int buzzerCount = params.getInteger("buzzer_count");
	int beeSight = params.getInteger("bee_sight");


	for (int i = 0; i < beeCount; i++)
	    context.add(new Bee(space, grid, communicationRadius, beeSight));

	for (int i = 0; i < flowerCount; i++)
	    context.add(new Flower(space, grid));

	for (int i = 0; i < hiveCount; i++)
	    context.add(new Hive(space, grid));
	
	for (int i = 0; i < buzzerCount; i++) {
		context.add(new Buzzer(space, grid));
	}
	
	// total honey in flowers - another important fact

	// iterates through the all agents in the context, retrieves each one�s location
	// in the ContinuousSpace
	// and moves it to the corresponding location in the Grid.
	for (Object obj : context)
	{
	    NdPoint pt = space.getLocation(obj);
	    grid.moveTo(obj, (int) pt.getX(), (int) pt.getY());
	}

	return context;
    }

}
