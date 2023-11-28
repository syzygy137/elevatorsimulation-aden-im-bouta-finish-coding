package building;

import passengers.Passengers;

/**
 * The Class CallManager. This class models all of the calls on each floor,
 * and then provides methods that allow the building to determine what needs
 * to happen (ie, state transitions).
 */
public class CallManager {
	
	/** The floors. */
	private Floor[] floors;
	
	/** The num floors. */
	private final int NUM_FLOORS;
	
	/** The Constant UP. */
	private final static int UP = 1;
	
	/** The Constant DOWN. */
	private final static int DOWN = -1;
	
	/** The up calls array indicates whether or not there is a up call on each floor. */
	private boolean[] upCalls;
	
	/** The down calls array indicates whether or not there is a down call on each floor. */
	private boolean[] downCalls;
	
	/** The up call pending - true if any up calls exist */
	private boolean upCallPending;
	
	/** The down call pending - true if any down calls exit */
	private boolean downCallPending;
	
	//TODO: Add any additional fields here..
	
	/**
	 * Instantiates a new call manager.
	 *
	 * @param floors the floors
	 * @param numFloors the num floors
	 */
	public CallManager(Floor[] floors, int numFloors) {
		this.floors = floors;
		NUM_FLOORS = numFloors;
		upCalls = new boolean[NUM_FLOORS];
		downCalls = new boolean[NUM_FLOORS];
		upCallPending = false;
		downCallPending = false;
		
		//TODO: Initialize any added fields here
	}
	
	public boolean callPending() {
		return (numUpCalls() + numDownCalls() > 0);
	}
	
	/**
	 * Update call status. This is an optional method that could be used to compute
	 * the values of all up and down call fields statically once per tick (to be
	 * more efficient, could only update when there has been a change to the floor queues -
	 * either passengers being added or being removed. The alternative is to dynamically
	 * recalculate the values of specific fields when needed.
	 */
	void updateCallStatus() {
		//TODO: Write this method if you choose to implement it...
		
	}

	/**
	 * Prioritize passenger calls from STOP STATE
	 *
	 * @param floor the floor
	 * @return the passengers
	 */
	Passengers prioritizePassengerCalls(int floor) {
		//TODO: Write this method based upon prioritization from STOP...
		if (numUpCalls() > numDownCalls()) {
			
		} else if (numUpCalls() > numDownCalls()) {
			// choose highest downCallFloor
		}
		else {
			//choose closest floor, if same prioritize lowest floor
		}
		return null;
	}

	//TODO: Write any additional methods here. Things that you might consider:
	//      1. pending calls - are there any? only up? only down?
	//      2. is there a call on the current floor in the current direction
	//      3. How many up calls are pending? how many down calls are pending? 
	//      4. How many calls are pending in the direction that the elevator is going
	//      5. Should the elevator change direction?
	//
	//      These are an example - you may find you don't need some of these, or you may need more...
	
	private int numUpCalls() {
		int count = 0;
		for (int i = 0; i < NUM_FLOORS; i++) {
			if (upCalls[i]) {
				count++;
			}
		}
		if (count > 0) upCallPending = true;
		return count;
	}
	private int numDownCalls() {
		int count = 0;
		for (int i = 0; i < NUM_FLOORS; i++) {
			if (downCalls[i]) {
				count++;
			}
		}
		if (count > 0) downCallPending = true;
		return count;
	}

	/**
	 * @return the floors
	 */
	public Floor[] getFloors() {
		return floors;
	}

	/**
	 * @param floors the floors to set
	 */
	public void setFloors(Floor[] floors) {
		this.floors = floors;
	}

	/**
	 * @return the upCalls
	 */
	public boolean[] getUpCalls() {
		return upCalls;
	}

	/**
	 * @param upCalls the upCalls to set
	 */
	public void setUpCalls(boolean[] upCalls) {
		this.upCalls = upCalls;
	}

	/**
	 * @return the downCalls
	 */
	public boolean[] getDownCalls() {
		return downCalls;
	}

	/**
	 * @param downCalls the downCalls to set
	 */
	public void setDownCalls(boolean[] downCalls) {
		this.downCalls = downCalls;
	}

	/**
	 * @return the upCallPending
	 */
	public boolean isUpCallPending() {
		return upCallPending;
	}

	/**
	 * @param upCallPending the upCallPending to set
	 */
	public void setUpCallPending(boolean upCallPending) {
		this.upCallPending = upCallPending;
	}

	/**
	 * @return the downCallPending
	 */
	public boolean isDownCallPending() {
		return downCallPending;
	}

	/**
	 * @param downCallPending the downCallPending to set
	 */
	public void setDownCallPending(boolean downCallPending) {
		this.downCallPending = downCallPending;
	}

	/**
	 * @return the nUM_FLOORS
	 */
	public int getNUM_FLOORS() {
		return NUM_FLOORS;
	}

	/**
	 * @return the up
	 */
	public static int getUp() {
		return UP;
	}

	/**
	 * @return the down
	 */
	public static int getDown() {
		return DOWN;
	}

	
}
