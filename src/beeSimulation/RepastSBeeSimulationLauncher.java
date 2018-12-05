/**
 * 
 */
package beeSimulation;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;
import sajas.sim.repasts.RepastSLauncher;
import sajas.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;
import jade.core.AID;
import sajas.core.Runtime;
import sajas.wrapper.ContainerController;

public class RepastSBeeSimulationLauncher extends RepastSLauncher
{
    private ContainerController mainContainer;
    private Context<Object> context;
    private Grid<Object> grid;
    private ContinuousSpace<Object> space;
    List<Hive> hives;
    int initNectar = 0;
    int maxHoneyAllBees = 0;
    int nrFighters = 0;
    int nrCollectors = 0;
    double ratioFighterCollector = 0.0;

    public static Agent getAgent(Context<?> context, AID aid)
    {
	for (Object obj : context.getObjects(Agent.class))
	{
	    if (((Agent) obj).getAID().equals(aid))
	    {
		return (Agent) obj;
	    }
	}
	return null;
    }

    @Override
    public String getName()
    {
	return "Bee Simulation -- SAJaS RepastS";
    }

    @Override
    protected void launchJADE()
    {
	Runtime rt = Runtime.instance();
	Profile p1 = new ProfileImpl();
	mainContainer = rt.createMainContainer(p1);
	launchAgents();
    }

    private void launchAgents()
    {
	Parameters params = RunEnvironment.getInstance().getParameters();
	int beeCount = params.getInteger("bee_count");
	int flowerCount = params.getInteger("flower_count");
	int communicationRadius = params.getInteger("comm_radius");
	int hiveCount = params.getInteger("hive_count");
	int buzzerCount = params.getInteger("buzzer_count");
	int beeSight = params.getInteger("bee_sight");
	int maxCapacity = params.getInteger("max_capacity");
	
	spawnFlowers(space, grid, context, flowerCount);
	spawnHives(hiveCount, grid, initNectar);
	spawnBees(space, grid, context, communicationRadius, beeSight, beeCount, maxCapacity);
	spawnStats(maxHoneyAllBees, ratioFighterCollector, initNectar);
	spawnBuzzers(space, grid, context, buzzerCount);
    }

    private void spawnFlowers(ContinuousSpace<Object> space, Grid<Object> grid, Context<Object> context, int flowerCount)
    {
	initNectar = 0;
	for (int i = 0; i < flowerCount; i++)
	{
	    Flower newFlower = new Flower(space, grid, context);
	    context.add(newFlower);
	    NdPoint flowerPt = space.getLocation(newFlower);
	    grid.moveTo(newFlower, (int) flowerPt.getX(), (int) flowerPt.getY());
	    initNectar+=newFlower.getCurrNectar();
	}
    }
    
    private void spawnBees(ContinuousSpace<Object> space, Grid<Object> grid, Context<Object> context,
	    int communicationRadius, int beeSight,int beeCount, int maxCapacity)
    {
	maxHoneyAllBees = 0;
	nrFighters = 0;
	nrCollectors = 0;
	ratioFighterCollector = 0;
	
	for (int i = 0; i < beeCount; i++)
	{
	    Bee newBee = new Bee(space, grid, communicationRadius, beeSight, hives, context, maxCapacity);
	    context.add(newBee);
	    NdPoint beePt = space.getLocation(newBee);
	    grid.moveTo(newBee, (int) beePt.getX(), (int) beePt.getY());
	    maxHoneyAllBees+=maxCapacity;
	    
	    if (newBee.getFightingSkill() > 5)
		nrFighters++;
	    else
		nrCollectors++;
	    
	    try
	    {
		mainContainer.acceptNewAgent("bee" + i, newBee).start();
	    } catch (StaleProxyException e)
	    {
		e.printStackTrace();
	    }
	}
	
	ratioFighterCollector = nrFighters/(double)nrCollectors;
	return;
    }

    private void spawnHives(int hiveCount, Grid<Object> grid, int initNectar)
    {
	hives = new ArrayList<>();
	for (int i = 0; i < hiveCount; i++)
	{
	    Hive newHive = new Hive(grid, initNectar);
	    context.add(newHive);
	    NdPoint hivePt = space.getLocation(newHive);
	    grid.moveTo(newHive, (int) hivePt.getX(), (int) hivePt.getY());
	    hives.add(newHive);
	}
	return;
    }

    private void spawnBuzzers(ContinuousSpace<Object> space, Grid<Object> grid, Context<Object> context,
	    int buzzerCount)
    {
	for (int i = 0; i < buzzerCount; i++)
	{
	    Buzzer newBuzzer = new Buzzer(space, grid, context);
	    context.add(newBuzzer);
	    NdPoint buzzerPt = space.getLocation(newBuzzer);
	    grid.moveTo(newBuzzer, (int) buzzerPt.getX(), (int) buzzerPt.getY());
	    
	    try
	    {
		mainContainer.acceptNewAgent("buzzer" + i, newBuzzer).start();
	    } catch (StaleProxyException e)
	    {
		e.printStackTrace();
	    }
	}
	return;
    }
    
    private void spawnStats(int maxHoneyAllBees, double ratioFighterCollector, int initNectar)
    {
	Stats newStats = new Stats(maxHoneyAllBees, ratioFighterCollector, initNectar, hives);
	context.add(newStats);
	return;
    }

    @Override
    public Context<?> build(Context<Object> context)
    {
	ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
	ContinuousSpace<Object> space = spaceFactory.createContinuousSpace("space", context,
		new RandomCartesianAdder<Object>(), new repast.simphony.space.continuous.WrapAroundBorders(), 50, 50);

	GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);

	Grid<Object> grid = gridFactory.createGrid("grid", context, new GridBuilderParameters<Object>(
		new WrapAroundBorders(), new SimpleGridAdder<Object>(), true, 50, 50));
	
	NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>("Killbeel Network", context , true );
	netBuilder.buildNetwork();
	this.grid = grid;
	this.space = space;
	this.context = context;
	
	return 
		super.build(this.context);
    }
}
