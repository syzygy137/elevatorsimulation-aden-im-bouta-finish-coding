package building;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import myfileio.MyFileIO;
import passengers.Passengers;

// TODO: Auto-generated Javadoc
/**
 * The Class Building, written by Henry
 */
// TODO: Auto-generated Javadoc
public class Building {
	
	/**  Constants for direction. */
	public final static int UP = 1;
	
	/** The Constant DOWN. */
	public final static int DOWN = -1;
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(Building.class.getName());
	
	/**  The fh - used by LOGGER to write the log messages to a file. */
	private FileHandler fh;
	
	/**  The fio for writing necessary files for data analysis. */
	private MyFileIO fio;
	
	/**  File that will receive the information for data analysis. */
	private File passDataFile;

	/**  passSuccess holds all Passengers who arrived at their destination floor. */
	private ArrayList<Passengers> passSuccess;
	
	/**  gaveUp holds all Passengers who gave up and did not use the elevator. */
	private ArrayList<Passengers> gaveUp;
	
	/**  The number of floors - must be initialized in constructor. */
	private final int NUM_FLOORS;
	
	/**  The size of the up/down queues on each floor. */
	private final int FLOOR_QSIZE = 10;	
	
	/** The floors. */
	public Floor[] floors;
	
	/** The elevator. */
	private Elevator elevator;
	
	/**  The Call Manager - it tracks calls for the elevator, analyzes them to answer questions and prioritize calls. */
	private CallManager callMgr;
	
	// Add any fields that you think you might need here...
	private final int stateConstant = 0;
	private final int directionConstant = 1;
	private final int floorConstant = 2;
	private final int passengersConstant = 3;
	private final int prevStateConstant = 4;
	
	
	/**
	 * Instantiates a new building.
	 *
	 * @param numFloors the num floors
	 * @param logfile the logfile
	 */
	public Building(int numFloors, String logfile) {
		NUM_FLOORS = numFloors;
		passSuccess = new ArrayList<Passengers>();
		gaveUp = new ArrayList<Passengers>();
		initializeBuildingLogger(logfile);
		// passDataFile is where you will write all the results for those passengers who successfully
		// arrived at their destination and those who gave up...
		fio = new MyFileIO();
		passDataFile = fio.getFileHandle(logfile.replaceAll(".log","PassData.csv"));
		
		// create the floors, call manager and the elevator arrays
		// note that YOU will need to create and config each specific elevator...
		floors = new Floor[NUM_FLOORS];
		for (int i = 0; i < NUM_FLOORS; i++) {
			floors[i]= new Floor(FLOOR_QSIZE); 
		}
		callMgr = new CallManager(floors,NUM_FLOORS);
		//TODO: if you defined new fields, make sure to initialize them here
		
	}
	
	/**
	 * Config elevator.
	 *
	 * @param numFloors the num floors
	 * @param capacity the capacity
	 * @param floorTicks the floor ticks
	 * @param doorTicks the door ticks
	 * @param passPerTick the pass per tick
	 */
	public void configElevator(int numFloors,int capacity, int floorTicks, int doorTicks, int passPerTick) {
		elevator = new Elevator(numFloors, capacity, floorTicks, doorTicks, passPerTick);
	}
		
	// TODO: Place all of your code HERE - state methods and helpers...
	
	/**
	 * Adds the passengers to queue.
	 *
	 * @param passengerList the passenger list
	 */
	public void addPassengersToQueue(ArrayList<Passengers> passengerList) {
		for (Passengers passengers : passengerList) {
			logCalls(passengers.getTime(), passengers.getNumPass(), passengers.getOnFloor(), passengers.getDirection(), passengers.getId());
			floors[passengers.getOnFloor()].addPassengers(passengers);
		}
	}
	
	// DO NOT CHANGE ANYTHING BELOW THIS LINE:
	/**
	 * Initialize building logger. Sets formating, file to log to, and
	 * turns the logger OFF by default
	 *
	 * @param logfile the file to log information to
	 */
	void initializeBuildingLogger(String logfile) {
		System.setProperty("java.util.logging.SimpleFormatter.format","%4$-7s %5$s%n");
		LOGGER.setLevel(Level.OFF);
		try {
			fh = new FileHandler(logfile);
			LOGGER.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 *  Implement the state methods here.
	 *
	 * @param time the time
	 * @return the int
	 */
	private int currStateStop(int time) {
		callMgr.updateCallStatus();
		if (callMgr.callPending()) {
			int currFloor = elevator.getCurrFloor();
			Passengers passengers = callMgr.prioritizePassengerCalls(currFloor);
			int destFloor = passengers.getOnFloor();
			if (destFloor == currFloor) {
				elevator.setDirection(passengers.getDirection());
				return Elevator.OPENDR;
			}
			if (destFloor > currFloor) {
				elevator.setDirection(1);
			} else {
				elevator.setDirection(-1);
			}
			elevator.setMoveToFloor(destFloor);
			elevator.setPostMoveToFloorDir(passengers.getDirection());
			return Elevator.MVTOFLR;
		}
		return Elevator.STOP;
	}

	/**
	 * Curr state mv to flr.
	 *
	 * @param time the time
	 * @return the int
	 */
	private int currStateMvToFlr(int time) {
		elevator.updateFloor();
		if (elevator.getCurrFloor() == elevator.getMoveToFloor()) {
			elevator.setDirection(elevator.getPostMoveToFloorDir());
			return Elevator.OPENDR;
		}
		return Elevator.MVTOFLR;
	}
		
	/**
	 * Curr state open dr.
	 *
	 * @param time the time
	 * @return the int
	 */
	private int currStateOpenDr(int time) {
		elevator.updateFloor();
		elevator.updateDoor();
		if (elevator.getDoorState() == elevator.getTicksDoorOpenClose()) {
			if (elevator.passengersToGetOff()) {
				return Elevator.OFFLD;
			}
			if (elevator.passengersToBoard(floors[elevator.getCurrFloor()])) {
				elevator.skipped(false);
				return Elevator.BOARD;
			}
			return Elevator.CLOSEDR;
		}
		return Elevator.OPENDR;
	}
	
	/**
	 * Curr state off ld.
	 *
	 * @param time the time
	 * @return the int
	 */
	private int currStateOffLd(int time) {
		while (elevator.passengersToGetOff()) {
			Passengers passengers = elevator.offload();
			logArrival(time, passengers.getNumPass(), passengers.getDestFloor(), passengers.getId());
		}
		elevator.updateDelay(false);
		if (elevator.delayIsOver()) {
			changeDirection();
			if (elevator.passengersToBoard(floors[elevator.getCurrFloor()])) {
				elevator.skipped(false);
				return Elevator.BOARD;
			}
			return Elevator.CLOSEDR;
		}
		return Elevator.OFFLD;
	}
	
	/**
	 * Curr state board.
	 *
	 * @param time the time
	 * @return the int
	 */
	private int currStateBoard(int time) {
		Floor floor = floors[elevator.getCurrFloor()];
		boolean skip = false;
		while(elevator.passengersToBoard(floor)) {
			Passengers passengers = elevator.getDirection() == UP ? floor.peekUp() : floor.peekDown();
			if (time > passengers.getTimeWillGiveUp()) {
				logGiveUp(time, passengers.getNumPass(), passengers.getOnFloor(), passengers.getDirection(), passengers.getId());
				passengers = elevator.getDirection() == UP ? floor.removeUp() : floor.removeDown();
				elevator.skipped(false);
				continue;
			}
			passengers = elevator.board(floor);
			if (passengers == null) {
				skip = true;
				break;
			}
			logBoard(time, passengers.getNumPass(), passengers.getOnFloor(), passengers.getDirection(), passengers.getId());
		}
		if (skip && !elevator.hasSkipped()) {
			Passengers passengers = (elevator.getDirection() == UP ? floor.peekUp() : floor.peekDown());
			logSkip(time, passengers.getNumPass(), passengers.getOnFloor(), passengers.getDirection(), passengers.getId());
			elevator.skipped(true);
		}
		elevator.updateDelay(true);
		if (elevator.delayIsOver()) {
			return Elevator.CLOSEDR;
		}
		return Elevator.BOARD;
	}
	
	/**
	 * Curr state close dr.
	 *
	 * @param time the time
	 * @return the int
	 */
	private int currStateCloseDr(int time) {
		elevator.updateDoor();
		Floor floor = floors[elevator.getCurrFloor()];
		Passengers curPassengers = elevator.getDirection() == UP ? floor.peekUp() : floor.peekDown();
		if (curPassengers != null && !curPassengers.isPolite()) {
			if (elevator.getDirection() == UP) floors[elevator.getCurrFloor()].peekUp().setPolite(true);
			else floors[elevator.getCurrFloor()].peekDown().setPolite(true);
			return Elevator.OPENDR;
		}
		if (elevator.getDoorState() == 0 && shouldOpenDrCloseDr(changeDirection())) {
			return Elevator.OPENDR;
		}
		
		if (elevator.getDoorState() == 0) {
			callMgr.updateCallStatus();
			if (elevator.isEmpty()) {
				if (!callMgr.callPending()) {
					return Elevator.STOP;
				}
			}
			return Elevator.MV1FLR;
		}
		return Elevator.CLOSEDR;
	}
	
	/**
	 * Curr state mv 1 flr.
	 *
	 * @param time the time
	 * @return the int
	 */
	private int currStateMv1Flr(int time) {
		elevator.updateFloor();
		changeDirection();
		Floor floor = floors[elevator.getCurrFloor()];
		if (elevator.atFloor() && (elevator.passengersToGetOff() || 
				(elevator.getDirection() == UP ? floor.peekUp() != null : floor.peekDown() != null))) {
			return Elevator.OPENDR;
		}
		return Elevator.MV1FLR;
	}
	
	/**
	 * Should open dr close dr.
	 *
	 * @param dirChange the dir change
	 * @return true, if successful
	 */
	private boolean shouldOpenDrCloseDr(boolean dirChange) {
		if (elevator.isEmpty() && elevator.passengersToBoard(floors[elevator.getCurrFloor()])) {
			Floor floor = floors[elevator.getCurrFloor()];
			if ((elevator.getDirection() == UP) ? 
					floor.peekUp() != null && elevator.canFit(floor.peekUp()) && 
					(dirChange || !callMgr.callsAboveFloor(elevator.getCurrFloor())): 
						floor.peekDown() != null && elevator.canFit(floor.peekDown()) && 
						(dirChange || !callMgr.callsBelowFloor(elevator.getCurrFloor()))) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Change direction.
	 *
	 * @return true, if successful
	 */
	private boolean changeDirection() {
		callMgr.updateCallStatus();
		int dir = elevator.getDirection();
		if (elevator.getNumPassengers() == 0) {
			Floor floor = floors[elevator.getCurrFloor()];
			if (elevator.getDirection() == UP) {
				if (!callMgr.callsAboveFloor(elevator.getCurrFloor()) && floor.peekUp() == null) {
					if (elevator.getCurrState() == Elevator.OFFLD || elevator.getCurrState() == Elevator.MV1FLR)
						if (floor.peekDown() != null)
							elevator.setDirection(DOWN);
					if (elevator.getCurrState() == Elevator.CLOSEDR && callMgr.callPending())
						elevator.setDirection(DOWN);
				}
			} else {
				if (!callMgr.callsBelowFloor(elevator.getCurrFloor()) && floor.peekDown() == null) {
					if (elevator.getCurrState() == Elevator.OFFLD || elevator.getCurrState() == Elevator.MV1FLR)
						if (floor.peekUp() != null)
							elevator.setDirection(UP);
					if (elevator.getCurrState() == Elevator.CLOSEDR && callMgr.callPending()) {
						elevator.setDirection(UP);
					}
				}
			}
		}
		return dir != elevator.getDirection();
	}
	

	
	/**
	 * Elevator state or floor changed.
	 *
	 * @return true, if successful
	 */
	private boolean elevatorStateOrFloorChanged() {
		return elevator.getPrevState() != elevator.getCurrState() || elevator.getPrevFloor() != elevator.getCurrFloor();
	}
	
	/**
	 * Update elevator - this is called AFTER time has been incremented.
	 * -  Logs any state changes, if the have occurred,
	 * -  Calls appropriate method based upon currState to perform
	 *    any actions and calculate next state...
	 *
	 * @param time the time
	 */
	public void updateElevator(int time) {
		if (elevatorStateOrFloorChanged())
			logElevatorStateOrFloorChanged(time,elevator.getPrevState(),elevator.getCurrState(),
					                       elevator.getPrevFloor(),elevator.getCurrFloor());

		switch (elevator.getCurrState()) {
		case Elevator.STOP: elevator.updateCurrState(currStateStop(time)); break;
		case Elevator.MVTOFLR: elevator.updateCurrState(currStateMvToFlr(time)); break;
		case Elevator.OPENDR: elevator.updateCurrState(currStateOpenDr(time)); break;
		case Elevator.OFFLD: elevator.updateCurrState(currStateOffLd(time)); break;
		case Elevator.BOARD: elevator.updateCurrState(currStateBoard(time)); break;
		case Elevator.CLOSEDR: elevator.updateCurrState(currStateCloseDr(time)); break;
		case Elevator.MV1FLR: elevator.updateCurrState(currStateMv1Flr(time)); break;
		}

	}
	
	/**
	 * Gives GUI the elevator.
	 *
	 * @return the elevator state
	 */
	public int[] getElevatorState() {
		int[] state = new int[prevStateConstant + 1];
		state[stateConstant] = elevator.getCurrState();
		state[directionConstant] = elevator.getDirection();
		state[floorConstant] = elevator.getCurrFloor();
		state[passengersConstant] = elevator.getNumPassengers();
		state[prevStateConstant] = elevator.getPrevState();
		return state;
	}
	
	/**
	 * Gives GUI the waiting passengers.
	 *
	 * @return the waiting passengers
	 */
	public ArrayList<Integer>[] getWaitingPassengers() {
		ArrayList<Integer>[] allPassengers = new ArrayList[floors.length * 2];
		for (int i = 0; i < floors.length * 2; i += 2) {
			allPassengers[i + 1] = floors[i / 2].allPassengers(true);
			allPassengers[i] = floors[i / 2].allPassengers(false);
		}
		return allPassengers;
	}
	
	

	/**
	 * Process passenger data. Do NOT change this - it simply dumps the 
	 * collected passenger data for successful arrivals and give ups. These are
	 * assumed to be ArrayLists...
	 */
	public void processPassengerData() {
		
		try {
			BufferedWriter out = fio.openBufferedWriter(passDataFile);
			out.write("ID,Number,From,To,WaitToBoard,TotalTime\n");
			for (Passengers p : passSuccess) {
				String str = p.getId()+","+p.getNumPass()+","+(p.getOnFloor()+1)+","+(p.getDestFloor()+1)+","+
				             (p.getBoardTime() - p.getTime())+","+(p.getTimeArrived() - p.getTime())+"\n";
				out.write(str);
			}
			for (Passengers p : gaveUp) {
				String str = p.getId()+","+p.getNumPass()+","+(p.getOnFloor()+1)+","+(p.getDestFloor()+1)+","+
				             p.getWaitTime()+",-1\n";
				out.write(str);
			}
			fio.closeFile(out);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Toggles logger on/off
	 */
	public void toggleLogging() {
		if (LOGGER.getLevel() == Level.OFF) {
			enableLogging();
		} else {
			LOGGER.setLevel(Level.OFF);
		}
			
	}

	/**
	 * Enable logging. Prints the initial configuration message.
	 * For testing, logging must be enabled BEFORE the run starts.
	 */
	public void enableLogging() {
		LOGGER.setLevel(Level.INFO);
			logElevatorConfig(elevator.getCapacity(),elevator.getTicksPerFloor(), elevator.getTicksDoorOpenClose(), 
					          elevator.getPassPerTick(), elevator.getCurrState(),elevator.getCurrFloor());
		
	}
	
	/**
	 * Close logs, and pause the timeline in the GUI.
	 *
	 * @param time the time
	 */
	public void closeLogs(int time) {
		if (LOGGER.getLevel() == Level.INFO) {
			logEndSimulation(time);
			fh.flush();
			fh.close();
		}
	}
	
	/**
	 * Prints the state.
	 *
	 * @param state the state
	 * @return the string
	 */
	private String printState(int state) {
		String str = "";
		
		switch (state) {
			case Elevator.STOP: 		str =  "STOP   "; break;
			case Elevator.MVTOFLR: 		str =  "MVTOFLR"; break;
			case Elevator.OPENDR:   	str =  "OPENDR "; break;
			case Elevator.CLOSEDR:		str =  "CLOSEDR"; break;
			case Elevator.BOARD:		str =  "BOARD  "; break;
			case Elevator.OFFLD:		str =  "OFFLD  "; break;
			case Elevator.MV1FLR:		str =  "MV1FLR "; break;
			default:					str =  "UNDEF  "; break;
		}
		return(str);
	}
	
	/**
	 * Log elevator config.
	 *
	 * @param capacity the capacity
	 * @param ticksPerFloor the ticks per floor
	 * @param ticksDoorOpenClose the ticks door open close
	 * @param passPerTick the pass per tick
	 * @param state the state
	 * @param floor the floor
	 */
	private void logElevatorConfig(int capacity, int ticksPerFloor, int ticksDoorOpenClose, 
			                       int passPerTick, int state, int floor) {
		LOGGER.info("CONFIG:   Capacity="+capacity+"   Ticks-Floor="+ticksPerFloor+"   Ticks-Door="+ticksDoorOpenClose+
				    "   Ticks-Passengers="+passPerTick+"   CurrState=" + (printState(state))+"   CurrFloor="+(floor+1));
	}
		
	/**
	 * Log elevator state changed.
	 *
	 * @param time the time
	 * @param prevState the prev state
	 * @param currState the curr state
	 * @param prevFloor the prev floor
	 * @param currFloor the curr floor
	 */
	private void logElevatorStateOrFloorChanged(int time, int prevState, int currState, int prevFloor, int currFloor) {
		LOGGER.info("Time="+time+"   Prev State: " + printState(prevState) + "   Curr State: "+printState(currState)
		            +"   PrevFloor: "+(prevFloor+1) + "   CurrFloor: " + (currFloor+1));
	}
	
	/**
	 * Log arrival.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param id the id
	 */
	private void logArrival(int time, int numPass, int floor,int id) {
		LOGGER.info("Time="+time+"   Arrived="+numPass+" Floor="+ (floor+1)
		            +" passID=" + id);						
	}
	
	/**
	 * Log calls.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param dir the dir
	 * @param id the id
	 */
	private void logCalls(int time, int numPass, int floor, int dir, int id) {
		LOGGER.info("Time="+time+"   Called="+numPass+" Floor="+ (floor +1)
			 	    +" Dir="+((dir>0)?"Up":"Down")+"   passID=" + id);
	}
	
	/**
	 * Log give up.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param dir the dir
	 * @param id the id
	 */
	private void logGiveUp(int time, int numPass, int floor, int dir, int id) {
		LOGGER.info("Time="+time+"   GaveUp="+numPass+" Floor="+ (floor+1) 
				    +" Dir="+((dir>0)?"Up":"Down")+"   passID=" + id);				
	}

	/**
	 * Log skip.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param dir the dir
	 * @param id the id
	 */
	private void logSkip(int time, int numPass, int floor, int dir, int id) {
		LOGGER.info("Time="+time+"   Skip="+numPass+" Floor="+ (floor+1) 
			   	    +" Dir="+((dir>0)?"Up":"Down")+"   passID=" + id);				
	}
	
	/**
	 * Log board.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param dir the dir
	 * @param id the id
	 */
	private void logBoard(int time, int numPass, int floor, int dir, int id) {
		LOGGER.info("Time="+time+"   Board="+numPass+" Floor="+ (floor+1) 
				    +" Dir="+((dir>0)?"Up":"Down")+"   passID=" + id);				
	}
	
	/**
	 * Log end simulation.
	 *
	 * @param time the time
	 */
	private void logEndSimulation(int time) {
		LOGGER.info("Time="+time+"   Detected End of Simulation");
	}
}
