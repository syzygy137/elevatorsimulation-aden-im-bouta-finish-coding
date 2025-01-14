package building;
import java.util.ArrayList;

import passengers.Passengers;


// TODO: Auto-generated Javadoc
/**
 * The Class Elevator, written by Henry
 *
 * @author This class will represent an elevator, and will contain
 * configuration information (capacity, speed, etc) as well
 * as state information - such as stopped, direction, and count
 * of passengers targeting each floor...
 */
public class Elevator {
	
	/**  Elevator State Variables - These are visible publicly. */
	public final static int STOP = 0;
	
	/** The Constant MVTOFLR. */
	public final static int MVTOFLR = 1;
	
	/** The Constant OPENDR. */
	public final static int OPENDR = 2;
	
	/** The Constant OFFLD. */
	public final static int OFFLD = 3;
	
	/** The Constant BOARD. */
	public final static int BOARD = 4;
	
	/** The Constant CLOSEDR. */
	public final static int CLOSEDR = 5;
	
	/** The Constant MV1FLR. */
	public final static int MV1FLR = 6;

	/** Default configuration parameters for the elevator. These should be
	 *  updated in the constructor.
	 */
	private int capacity = 15;				// The number of PEOPLE the elevator can hold
	
	/** The ticks per floor. */
	private int ticksPerFloor = 5;			// The time it takes the elevator to move between floors
	
	/** The ticks door open close. */
	private int ticksDoorOpenClose = 2;  	// The time it takes for doors to go from OPEN <=> CLOSED
	
	/** The pass per tick. */
	private int passPerTick = 3;            // The number of PEOPLE that can enter/exit the elevator per tick
	
	/**  Finite State Machine State Variables. */
	private int currState;		// current state
	
	/** The prev state. */
	private int prevState;      // prior state
	
	/** The prev floor. */
	private int prevFloor;      // prior floor
	
	/** The curr floor. */
	private int currFloor;      // current floor
	
	/** The direction. */
	private int direction;      // direction the Elevator is traveling in.

	/** The time in state. */
	private int timeInState;    // represents the time in a given state
	                            // reset on state entry, used to determine if
	                            // state has completed or if floor has changed
	                            // *not* used in all states 

	/** The door state. */
                            	private int doorState;      // used to model the state of the doors - OPEN, CLOSED
	                            // or moving

    
    private int passDelay = 0;
    
    private int boardedPassengers;
    private int offloadedPassengers;
    
    private boolean skipped;

	
	/** The passengers. */
                            	private int passengers;  	// the number of people in the elevator
	
	/** The pass by floor. */
	private ArrayList<Passengers>[] passByFloor;  // Passengers to exit on the corresponding floor

	/** The move to floor. */
	private int moveToFloor;	// When exiting the STOP state, this is the floor to move to without
	                            // stopping.
	
	/** The post move to floor dir. */
    private int postMoveToFloorDir; // This is the direction that the elevator will travel AFTER reaching
	                                // the moveToFloor in MVTOFLR state.

	/**
	 * Instantiates a new elevator.
	 *
	 * @param numFloors the num floors
	 * @param capacity the capacity
	 * @param floorTicks the floor ticks
	 * @param doorTicks the door ticks
	 * @param passPerTick the pass per tick
	 * Reviewed by Dan
	 */
    @SuppressWarnings("unchecked")
	public Elevator(int numFloors,int capacity, int floorTicks, int doorTicks, int passPerTick) {		
		this.prevState = STOP;
		this.currState = STOP;
		this.timeInState = 0;
		this.currFloor = 0;
		passByFloor = new ArrayList[numFloors];
		
		for (int i = 0; i < numFloors; i++) 
			passByFloor[i] = new ArrayList<Passengers>(); 
		this.capacity = capacity;
		this.ticksPerFloor = floorTicks;
		this.ticksDoorOpenClose = doorTicks;
		this.passPerTick = passPerTick;
		
		passengers = 0;
		doorState = 0;
	}
                                	
    /**
     * Gets if any passengers to get off.
     *
     * @return true, if passengers
     * Reviewed by Dan
     */
    boolean passengersToGetOff() {
    	if (passByFloor[currFloor].isEmpty()) {
    		return false;
    	}
    	return true;
    }
    

    
    /**
     * Gets if passengers to board on a certain floor.
     *
     * @param floor the floor
     * @return true, if passengers
     * Reviewed by Dan
     */
    boolean passengersToBoard(Floor floor) {
    	if (currState == MVTOFLR && currFloor == moveToFloor)
    		return true;

    	if (direction == Building.UP) {
    		if (!(floor.peekUp() == null) ) {
    			return true;
    		}
    	} else {
    		if (!(floor.peekDown() == null) ) {
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * Tells next passengers to board on a certain floor.
     *
     * @param floor the floor
     * @return the passengers
     * Reviewed by Dan
     */
    Passengers board(Floor floor) {
    	Passengers passengers = (direction == Building.UP) ? floor.peekUp() : floor.peekDown();
    	if (passengers.getNumPass() > capacity - this.passengers) {
    		return null;
    	}
    	Passengers temp = (direction == Building.UP) ? floor.removeUp() : floor.removeDown();
    	this.passengers += passengers.getNumPass();
    	boardedPassengers += passengers.getNumPass();
    	passByFloor[passengers.getDestFloor()].add(passengers);
    	return passengers;
    }
    

    
    /**
     * Offload passengers from elevator.
     *
     * @return the passengers
     * Reviewed by Dan
     */
    Passengers offload() {
    	Passengers passengers = passByFloor[currFloor].remove(0);
    	this.passengers -= passengers.getNumPass();
    	offloadedPassengers += passengers.getNumPass();
    	return passengers;
    }
    

    
	
	//TODO: Add Getter/Setters and any methods that you deem are required. Examples 
	//      include:
	//      1) moving the elevator
	//      2) closing the doors
	//      3) opening the doors
	//      and so on...
                         		
	/**
	 * Gets the capacity.
	 *
	 * @return the capacity
	 * 
	 */
	int getCapacity() {
		return capacity;
	}
	
	/**
	 * Gets the num of passengers.
	 *
	 * @return the passengers
	 */
	int getNumPassengers() {
		return passengers;
	}

	/**
	 * Gets the ticks per floor.
	 *
	 * @return the ticks per floor
	 */
	int getTicksPerFloor() {
		return ticksPerFloor;
	}

	/**
	 * Gets the ticks door open close.
	 *
	 * @return the ticks door open close
	 */
	int getTicksDoorOpenClose() {
		return ticksDoorOpenClose;
	}

	/**
	 * Gets the pass per tick.
	 *
	 * @return the pass per tick
	 */
	int getPassPerTick() {
		return passPerTick;
	}

	/**
	 * Gets the curr state.
	 *
	 * @return the curr state
	 */
	int getCurrState() {
		return currState;
	}

	/**
	 * Gets the prev state.
	 *
	 * @return the prev state
	 */
	int getPrevState() {
		return prevState;
	}

	/**
	 * Gets the prev floor.
	 *
	 * @return the prev floor
	 */
	int getPrevFloor() {
		return prevFloor;
	}

	/**
	 * Gets the curr floor.
	 *
	 * @return the curr floor
	 */
	int getCurrFloor() {
		return currFloor;
	}
	
	/**
	 * Sets the direction.
	 *
	 * @param direction the new direction
	 * @return void
	 */
	void setDirection(int direction) {
		this.direction = direction;
	}
	
	/**
	 * Gets the direction.
	 *
	 * @return void
	 */
	int getDirection() {
		return direction;
	}
	
	/**
	 * Sets the floor to move to.
	 *
	 * @param moveToFloor the new move to floor
	 * @return void
	 */
	void setMoveToFloor(int moveToFloor) {
		this.moveToFloor = moveToFloor;
	}
	
	/**
	 * Gets the floor to move to.
	 *
	 * @return int of the floor
	 */
	int getMoveToFloor() {
		return moveToFloor;
	}
	
	/**
	 * Sets the direction to go after reaching the floor.
	 *
	 * @param postMoveToFloorDir the new post move to floor dir
	 * @return void
	 */
	void setPostMoveToFloorDir(int postMoveToFloorDir) {
		this.postMoveToFloorDir = postMoveToFloorDir;
	}
	
	/**
	 * Sets the direction to go after reaching the floor.
	 *
	 * @return int
	 */
	int getPostMoveToFloorDir() {
		return postMoveToFloorDir;
	}
	
	/**
	 * Gets the door state.
	 *
	 * @return int
	 */
	int getDoorState() {
		return doorState;
	}
	
	/**
	 * Returns true if empty.
	 *
	 * @return boolean
	 */
	boolean isEmpty() {
		return passengers == 0;
	}
	
	/**
	 * Gets the delay.
	 *
	 * @return the delay
	 */
	int getDelay() {
		return passDelay;
	}
	
	/**
	 * Checks if delay is over.
	 *
	 * @return true, if it is
	 * Reviewed by Dan
	 */
	boolean delayIsOver() {
		if (passDelay <= timeInState) {
			offloadedPassengers = 0;
			boardedPassengers = 0;
			passDelay = 0;
			return true;
		}
		return false;
	}
	
	/**
	 * Updates delay to elevator.
	 *
	 * @param boarding the boarding
	 * Reviewed by Dan
	 */
	void updateDelay(boolean boarding) {
		passDelay = ((boarding ? boardedPassengers : offloadedPassengers) + passPerTick - 1) / passPerTick;
	}
	
	/**
	 * Update door.
	 */
	void updateDoor() {
		if (currState == OPENDR)
			doorState++;
		if (currState == CLOSEDR)
			doorState--;
	}

	
	/**
	 * Update floor.
	 */
	void updateFloor() {
		prevFloor = currFloor;
		if (currState == MVTOFLR || currState == MV1FLR) {
			if (timeInState % ticksPerFloor == 0) {
				currFloor += direction;
			}
		}
	}
	

	

	/**
	 * Update curr state.
	 *
	 * @param nextState the next state
	 */
	void updateCurrState(int nextState) {
		prevState = currState;
		currState = nextState;
		if (currState == prevState) {
			timeInState++;
		} else {
			timeInState = 1;
		}
	}
	
	/**
	 * At floor.
	 *
	 * @return true, if successful
	 */
	boolean atFloor() {
		return timeInState % ticksPerFloor == 0;
	}
	
	/**
	 * Can fit.
	 *
	 * @param passengers the passengers
	 * @return true, if successful
	 */
	boolean canFit(Passengers passengers) {
		return passengers.getNumPass() < capacity - this.passengers;
	}
	
	/**
	 * Checks for skipped.
	 *
	 * @return true, if successful
	 */
	boolean hasSkipped() {
		return skipped;
	}
	
	/**
	 * Skipped.
	 *
	 * @param skipped the skipped
	 */
	void skipped(boolean skipped) {
		this.skipped = skipped;
	}
	
}
