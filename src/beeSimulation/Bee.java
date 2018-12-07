package beeSimulation;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import sajas.core.Agent;
import sajas.core.behaviours.CyclicBehaviour;
import sajas.core.behaviours.OneShotBehaviour;

public class Bee extends Agent
{

    private ContinuousSpace<Object> space;
    private Grid<Object> grid;
    private int fightingSkill;
    private int collectingSkill;
    private int currentNectar;
    private int maxNectar;
    private int communicationRadius;
    private boolean isAlive;
    private boolean isAtHive;
    private int beeSight;
    private List<Hive> hives;
    private GridCell<Flower> cellWithMostNectar;
    private Flower flowerWithMostNectar;
    private Context<Object> context;
    private MyBeeBehaviour movement;
    private MyBeeInDangerBehaviour danger;
    private MyReceiveCommunicationsBehaviour receiveCommunications;
    private AID rescuer;
    private int agreedCount;
    private Buzzer targetBuzzer;
    private RepastEdge<Object> murderEdge;

    public Bee(ContinuousSpace<Object> space, Grid<Object> grid, int communicationRadius, int beeSight,
	    List<Hive> hives, Context<Object> context, int max)
    {
	this.space = space;
	this.grid = grid;
	this.fightingSkill = RandomHelper.nextIntFromTo(0, 10);
	this.collectingSkill = 10 - this.fightingSkill;
	if (this.collectingSkill == 0)
	    this.collectingSkill++;
	this.currentNectar = 0;
	this.maxNectar = max;
	this.communicationRadius = communicationRadius;
	this.isAlive = true;
	this.beeSight = beeSight;
	this.hives = hives;
	this.cellWithMostNectar = null;
	this.flowerWithMostNectar = new Flower();
	this.context = context;
	this.rescuer = null;
	this.targetBuzzer = null;
	this.agreedCount = 0;
    }

    public Bee()
    {
    }

    /********************** GETTERS *************************/

    public ContinuousSpace<Object> getSpace()
    {
	return space;
    }

    public Grid<Object> getGrid()
    {
	return grid;
    }

    public void incrementAgreed()
    {
	agreedCount++;
    }

    public int getAgreedCount()
    {
	return agreedCount;
    }

    public int getFightingSkill()
    {
	return fightingSkill;
    }

    public int getCollectingSkill()
    {
	return collectingSkill;
    }

    public int getCurrentNectar()
    {
	return currentNectar;
    }

    public int getMaxNectar()
    {
	return maxNectar;
    }

    public int getCommunicationRadius()
    {
	return communicationRadius;
    }

    public boolean getIsAlive()
    {
	return isAlive;
    }

    public void setIsAlive(boolean state)
    {
	this.isAlive = state;
    }

    public void setIsAtHive(boolean state)
    {
	this.isAtHive = state;
    }

    public boolean getIsAtHive()
    {
	return isAtHive;
    }

    public int getBeeSight()
    {
	return beeSight;
    }

    public void setCurrentNectar(int nectar)
    {
	this.currentNectar = nectar;
    }

    public List<Hive> getHives()
    {
	return hives;
    }

    public void setHives(List<Hive> hives)
    {
	this.hives = hives;
    }

    public GridCell<Flower> getCellWithMostNectar()
    {
	return cellWithMostNectar;
    }

    public void setCellWithMostNectar(GridCell<Flower> cellWithMostNectar)
    {
	this.cellWithMostNectar = cellWithMostNectar;
    }

    public Flower getFlowerWithMostNectar()
    {
	return flowerWithMostNectar;
    }

    public void setFlowerWithMostNectar(Flower flowerWithMostNectar)
    {
	this.flowerWithMostNectar = flowerWithMostNectar;
    }

    public AID getRescuer()
    {
	return rescuer;
    }

    public void setRescuer(AID rescuer)
    {
	this.rescuer = rescuer;
    }

    /********************** GETTERS END *************************/

    /************* AGENTS SETUP AND BEHAVIOURS *****************/

    public void setup()
    {
	movement = new MyBeeBehaviour(this);
	receiveCommunications = new MyReceiveCommunicationsBehaviour(this);
	addBehaviour(movement);
	addBehaviour(receiveCommunications);
    }

    class MyBeeBehaviour extends CyclicBehaviour
    {
	private static final long serialVersionUID = 1L;

	public MyBeeBehaviour(Agent a)
	{
	    super(a);
	}

	@Override
	public void action()
	{

	    GridPoint pt = getGrid().getLocation(Bee.this);
	    if (pursueBuzzer(pt))
		return;
	    try
	    {
		if (getCurrentNectar() >= getMaxNectar() || (smellRemainingNectar() == 0))
		    goToHive(pt);

		else if (getCurrentNectar() < getMaxNectar())
		    goLookForNectar(pt);

		Buzzer dangerousBuzz = dangerousBuzzer();
		if (dangerousBuzz != null && Bee.this.getFightingSkill() < 6)
		    addBehaviour(new MyBeeInDangerBehaviour(Bee.this, dangerousBuzz));

	    } catch (NullPointerException exception)
	    {
		exception.printStackTrace();
	    }
	}
    }

    class MyBeeInDangerBehaviour extends OneShotBehaviour
    {
	private static final long serialVersionUID = 1L;
	Buzzer dangerousBuzz;

	public MyBeeInDangerBehaviour(Agent a, Buzzer buu)
	{
	    super(a);
	    dangerousBuzz = buu;
	}

	public void sendDistressMessage()
	{
	    GridPoint pt = getGrid().getLocation(Bee.this);
	    try
	    {
		GridCellNgh<Bee> closeNghCreator = new GridCellNgh<Bee>(getGrid(), pt, Bee.class,
			getCommunicationRadius(), getCommunicationRadius());
		List<GridCell<Bee>> closeGridCells = closeNghCreator.getNeighborhood(true);

		ACLMessage rfh = new ACLMessage(ACLMessage.REQUEST);
		MessageTemplate mt;

		List<AID> fighterBees = new ArrayList<AID>();

		for (GridCell<Bee> cell : closeGridCells)
		{
		    for (Object item : cell.items())
		    {
			if (item instanceof Bee && ((Bee) item).getFightingSkill() > 5)
			{
			    AID thisBeeAID = ((Bee) item).getAID();
			    fighterBees.add(thisBeeAID);
			    rfh.addReceiver(thisBeeAID);
			}
		    }
		}

		rfh.setContent(dangerousBuzz.getAID().toString());
		rfh.setConversationId("danger");
		rfh.setReplyWith("rfh" + System.currentTimeMillis()); // Unique value
		//System.out.println("Sending RFH danger messages");
		getAgent().send(rfh);
	    } catch (NullPointerException exception)
	    {
		exception.printStackTrace();
	    }

	}

	@Override
	public void action()
	{
	    sendDistressMessage();
	}
    }

    class MyReceiveCommunicationsBehaviour extends CyclicBehaviour
    {
	private static final long serialVersionUID = 1L;

	public MyReceiveCommunicationsBehaviour(Agent a)
	{
	    super(a);
	}

	public void receiveRequestsForHelp()
	{

	    ACLMessage msg = myAgent.receive();
	    boolean done = false;
	    if (msg != null)
	    {
		if (msg.getPerformative() == ACLMessage.AGREE)
		{
		    setRescuer(msg.getSender());
		    incrementAgreed();
		    for (Object obj : getGrid().getObjects())
		    {
			if (obj instanceof Stats)
			    ((Stats) obj).incrementMurderContracts();
		    }
		}
		if (msg.getPerformative() == ACLMessage.REQUEST)
		{
		    // Message received. Process it
		    String dangerousBuzzerAID = msg.getContent();
		    ACLMessage reply = msg.createReply();

		    if (targetBuzzer != null)
		    {
			reply.setPerformative(ACLMessage.REFUSE);
		    }
		    else
		    {
			reply.setPerformative(ACLMessage.AGREE);
			targetBuzzer = (Buzzer) lookupAgent(dangerousBuzzerAID);
			if (targetBuzzer != null)
			{
			    incrementAgreed();
			    for (Object obj : getGrid().getObjects())
			    {
				if (obj instanceof Stats)
				    ((Stats) obj).incrementMurderContracts();
			    }
			    murderEdge = ((Network<Object>) context.getProjection("Killbeel Network")).addEdge(myAgent,
				    targetBuzzer);
			}

		    }
		}

	    }
	}

	@Override
	public void action()
	{
	    receiveRequestsForHelp();
	}

    }

    public boolean pursueBuzzer(GridPoint pt)
    {
	try
	{
	    if (!targetBuzzer.getIsAlive())
	    {
		targetBuzzer = null;
		return false;
	    }
	    else
	    {
		GridPoint buzzerPoint = grid.getLocation(targetBuzzer);
		if (buzzerPoint.equals(pt))
		{
		    targetBuzzer = null;
		    removeMurderEdge();
		    return false;
		}
		moveTowards(buzzerPoint);
		return true;
	    }
	} catch (NullPointerException expt)
	{
	    removeMurderEdge();
	    return false;
	}
    }

    public Buzzer dangerousBuzzer()
    {
	GridPoint pt = getGrid().getLocation(Bee.this);
	Buzzer closeBuzzer = null;
	GridCellNgh<Buzzer> closeNghCreator = new GridCellNgh<Buzzer>(getGrid(), pt, Buzzer.class, 5, 5);
	List<GridCell<Buzzer>> closeGridCells = closeNghCreator.getNeighborhood(true);
	for (GridCell<Buzzer> cell : closeGridCells)
	{
	    for (Object item : cell.items())
	    {
		if (item instanceof Buzzer)
		{
		    closeBuzzer = (Buzzer) item;
		    break;
		}
	    }
	}
	return closeBuzzer;
    }

    public Agent lookupAgent(String aid)
    {
	for (Object obj : context.getObjects(Agent.class))
	{
	    if (((Agent) obj).getAID().toString().equals(aid))
	    {
		return (Agent) obj;
	    }
	}
	return null;
    }

    public void moveTowards(GridPoint pt)
    {
	NdPoint myPoint = space.getLocation(Bee.this);
	double angle = 0;
	NdPoint goalPoint;

	if (pt == null)
	{
	    goalPoint = new NdPoint(RandomHelper.nextIntFromTo(0, 50), RandomHelper.nextIntFromTo(0, 50));
	    angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, goalPoint);
	}

	else if (pt != null && pt != getGrid().getLocation(Bee.this))
	{
	    goalPoint = new NdPoint(pt.getX(), pt.getY());
	    angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, goalPoint);
	}

	space.moveByVector(Bee.this, 1, angle, 0);
	// updating position in the grid
	myPoint = space.getLocation(Bee.this);
	getGrid().moveTo(Bee.this, (int) myPoint.getX(), (int) myPoint.getY());
    }

    public void collectNectar(int beeCurrNectar, Flower flower)
    {
	int flowerCurrNectar = flower.getCurrNectar();
	if ((flowerCurrNectar - getCollectingSkill()) < 0)
	    flower.setCurrNectar(0);
	else
	    flower.setCurrNectar(flowerCurrNectar - getCollectingSkill());

	int newBeeNectar = beeCurrNectar + Math.min(flowerCurrNectar, getCollectingSkill());
	setCurrentNectar(Math.min(newBeeNectar, 30));
	return;
    }

    public void goLookForNectar(GridPoint pt)
    {
	// move towards flower with most nectar
	GridCellNgh<Flower> nghCreator = new GridCellNgh<Flower>(getGrid(), pt, Flower.class, getBeeSight(),
		getBeeSight());

	List<GridCell<Flower>> gridCells = nghCreator.getNeighborhood(true);
	Flower flower = null;
	double max = getFlowerWithMostNectar().getCurrNectar();
	for (GridCell<Flower> cell : gridCells)
	{
	    for (Object item : cell.items())
	    {
		if (item instanceof Flower)
		{
		    if (((Flower) item).getCurrNectar() > max)
		    {
			flower=(Flower) item;
			max = flower.getCurrNectar();
			setCellWithMostNectar(cell);
			setFlowerWithMostNectar((Flower) item);
		    }
		}
	    }
	}
	
	if (max==0  || getCellWithMostNectar() == null)
	{
	    moveTowards(null);
	    return;
	}
	moveTowards(getCellWithMostNectar().getPoint());

	// get the flower's neighbourhood
	pt = getGrid().getLocation(Bee.this);
	GridCellNgh<Flower> closeNghCreator = new GridCellNgh<Flower>(getGrid(), pt, Flower.class, 1, 1);
	List<GridCell<Flower>> closeGridCells = closeNghCreator.getNeighborhood(true);
	Boolean inNeighbourhood = false;
	for (GridCell<Flower> neighbour : closeGridCells)
	{
	    if (neighbour.getPoint().equals(getCellWithMostNectar().getPoint()))
	    {
		inNeighbourhood = true;
		break;
	    }
	}

	// if bee's in the flower neighbourhood etc, collect nectar
	if (getCellWithMostNectar() != null && inNeighbourhood && getFlowerWithMostNectar().getCurrNectar() > 0
		&& getCollectingSkill() > 0)
	{
	    collectNectar(getCurrentNectar(), getFlowerWithMostNectar());
	    return;
	}
	return;
    }

    public void goToHive(GridPoint pt)
    {
	double minDist = Double.MAX_VALUE;
	GridPoint closerHiveCell = null;
	for (Hive hive : getHives())
	{
	    double dist = Point2D.distance(pt.getX(), pt.getY(), getGrid().getLocation(hive).getX(),
		    getGrid().getLocation(hive).getY());
	    if (dist < minDist)
	    {
		minDist = dist;
		closerHiveCell = getGrid().getLocation(hive);
	    }
	}
	moveTowards(closerHiveCell);

	pt = grid.getLocation(this);
	GridCellNgh<Hive> closeNghCreator = new GridCellNgh<Hive>(getGrid(), pt, Hive.class, 1, 1);
	List<GridCell<Hive>> closeGridCells = closeNghCreator.getNeighborhood(true);

	for (GridCell<Hive> neighbour : closeGridCells)
	{
	    if (neighbour.getPoint().equals(closerHiveCell))
	    {
		for (Object item : neighbour.items())
		{
		    if (item instanceof Hive)
		    {
			((Hive) item).incrementHiveStats(getCurrentNectar());
			enterFlashilyInHive();
			return;
		    }
		}

	    }
	}
    }

    public int smellRemainingNectar()
    {
	int remainingNectar = 0;
	for (Object obj : getGrid().getObjects())
	{
	    if (obj instanceof Flower)
	    {
		remainingNectar += ((Flower) obj).getCurrNectar();
	    }
	}
	return remainingNectar;
    }

    private void checkSimulationOver()
    {
	for (Object obj : grid.getObjects())
	{
	    if (obj instanceof Bee)
	    {
		if (((Bee) obj).getIsAlive() || !((Bee) obj).getIsAtHive())
		    return;
	    }
	}
	RunEnvironment.getInstance().endRun();
    }

    public void killBeel()
    {
	if(isAtHive)
	    return;
	setIsAlive(false);
	removeMurderEdge();
	removeBehaviour(movement);
	removeBehaviour(receiveCommunications);
	context.remove(this);
	checkSimulationOver();
    }

    public void enterFlashilyInHive()
    {
	if(!isAlive)
	    return;
	setIsAtHive(true);
	removeMurderEdge();
	removeBehaviour(movement);
	removeBehaviour(receiveCommunications);
	context.remove(this);
	checkSimulationOver();
    }

    public void removeMurderEdge()
    {
	if (murderEdge != null)
	    ((Network<Object>) context.getProjection("Killbeel Network")).removeEdge(murderEdge);
    }
}
