package building;

import passengers.Passengers;

// TODO: Auto-generated Javadoc
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
	
	/**
	 * Checks call pending
	 *
	 * @return true, if successful
	 */
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
		for (int i = 0; i < NUM_FLOORS; i++) {
			upCalls[i] = (floors[i].peekUp() != null);
			downCalls[i] = (floors[i].peekDown() != null);
		}
	}

	/**
	 * Prioritize passenger calls from STOP STATE.
	 *
	 *If floor has people on it, check if there is only a call in one direction, if so, go in that direction
	 *If floor has people and calls in both directions, check which way has more calls in the direction above or below
	 *the current floor. If there is no one on the current floor, then choose the direction that has more total calls, and either
	 *go to the highest or lowest in that direction. If the amounts are the same, go to the lower one.
	 *
	 * @param floor the floor
	 * @return the passengers
	 */
	Passengers prioritizePassengerCalls(int floor) {
		//if there are people on the floor
		if (!floors[floor].isEmpty()) { 
			if (onlyUpCalls(floor)) return floors[floor].peekUp(); 
			else if (onlyDownCalls(floor)) return floors[floor].peekDown();
			else {
				if (numUpCallsAboveFloor(floor) >= numDownCallsBelowFloor(floor)) return floors[floor].peekUp();
				else return floors[floor].peekDown();
			}
		}
		else { //there aren't people on the current floor
			if (numUpCalls() > numDownCalls()) {
				return findFirstFloor(true);
			} else if (numUpCalls() < numDownCalls()) {
				return findFirstFloor(false);
			}
			else {
				return findClosestFloor(floor);
			}
		}
	}

	//TODO: Write any additional methods here. Things that you might consider:
	//      1. pending calls - are there any? only up? only down?
	//      2. is there a call on the current floor in the current direction
	//      3. How many up calls are pending? how many down calls are pending? 
	//      4. How many calls are pending in the direction that the elevator is going
	//      5. Should the elevator change direction?
	//
	//      These are an example - you may find you don't need some of these, or you may need more...
	
	/**
	 * Checks if there is only up calls
	 *
	 * @param floor the floor
	 * @return true, if successful
	 */
	private boolean onlyUpCalls(int floor) {
		return (floors[floor].peekUp() != null && floors[floor].peekDown() == null);
	}
	
	/**
	 * Checks if there is only down calls
	 *
	 * @param floor the floor
	 * @return true, if successful
	 */
	private boolean onlyDownCalls(int floor) {
		return (floors[floor].peekUp() == null && floors[floor].peekDown() != null);
	}
	
	
	/**
	 * Num up calls above floor.
	 *
	 * @param floor the floor
	 * @return the int
	 */
	private int numUpCallsAboveFloor(int floor) {
		int count = 0;
		for (int i = floor; i < NUM_FLOORS; i++) {
			if (upCalls[i]) {
				count++;
			}
		}
		if (count > 0) upCallPending = true;
		return count;
	}
	
	/**
	 * Num down calls below floor.
	 *
	 * @param floor the floor
	 * @return the int
	 */
	private int numDownCallsBelowFloor(int floor) {
		int count = 0;
		for (int i = floor; i >= 0; i--) {
			if (downCalls[i]) {
				count++;
			}
		}
		if (count > 0) downCallPending = true;
		return count;
	}
	
	/**
	 * Calls above floor.
	 *
	 * @param floor the floor
	 * @return true, if successful
	 */
	public boolean callsAboveFloor(int floor) {
		for (int i = floor + 1; i < NUM_FLOORS; i++) {
			if (upCalls[i] || downCalls[i]) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Calls below floor.
	 *
	 * @param floor the floor
	 * @return true, if successful
	 */
	public boolean callsBelowFloor(int floor) {
		for (int i = floor - 1; i >= 0; i--) {
			if (upCalls[i] || downCalls[i]) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Num up calls.
	 *
	 * @return the int
	 */
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
	
	/**
	 * Num down calls.
	 *
	 * @return the int
	 */
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
	 * Find first floor.
	 *
	 * @param goingUp the going up
	 * @return the passengers
	 */
	private Passengers findFirstFloor(boolean goingUp) {
		if (goingUp) {
			for (int i = 0; i < NUM_FLOORS; i++) {
				if (upCalls[i]) return floors[i].peekUp();
			}
		}
		else {
			for (int i = NUM_FLOORS-1; i >= 0; i--) {
				if (downCalls[i]) return floors[i].peekDown();
			}
		}
		return null;
	}
	
	/**
	 * Find closest floor.
	 *
	 * @param curFloor the cur floor
	 * @return the passengers
	 */
	private Passengers findClosestFloor(int curFloor) {
		int lowestUpCall = 0, highestDownCall = 0;
		for (int i = 0; i < NUM_FLOORS; i++) {
			if (downCalls[i]) {
				highestDownCall = i;
			}
		}
		for (int i = NUM_FLOORS - 1; i >= 0; i--) {
			if (upCalls[i]) {
				lowestUpCall = i;
			}
		} 
		if ((highestDownCall-curFloor) < (curFloor-lowestUpCall)) {
			System.out.println("went to highest down call");
			return floors[highestDownCall].peekDown();
		}
		else if ((highestDownCall-curFloor) > (curFloor-lowestUpCall)) {
			return floors[lowestUpCall].peekUp();
		}
		else {
			return floors[lowestUpCall].peekUp();
		}
	}

	/**
	 * Gets the floors.
	 *
	 * @return the floors
	 */
	public Floor[] getFloors() {
		return floors;
	}

	/**
	 * Sets the floors.
	 *
	 * @param floors the floors to set
	 */
	public void setFloors(Floor[] floors) {
		this.floors = floors;
	}

	/**
	 * Gets the up calls.
	 *
	 * @return the upCalls
	 */
	public boolean[] getUpCalls() {
		return upCalls;
	}

	/**
	 * Sets the up calls.
	 *
	 * @param upCalls the upCalls to set
	 */
	public void setUpCalls(boolean[] upCalls) {
		this.upCalls = upCalls;
	}

	/**
	 * Gets the down calls.
	 *
	 * @return the downCalls
	 */
	public boolean[] getDownCalls() {
		return downCalls;
	}

	/**
	 * Sets the down calls.
	 *
	 * @param downCalls the downCalls to set
	 */
	public void setDownCalls(boolean[] downCalls) {
		this.downCalls = downCalls;
	}

	/**
	 * Checks if is up call pending.
	 *
	 * @return the upCallPending
	 */
	public boolean isUpCallPending() {
		return upCallPending;
	}

	/**
	 * Sets the up call pending.
	 *
	 * @param upCallPending the upCallPending to set
	 */
	public void setUpCallPending(boolean upCallPending) {
		this.upCallPending = upCallPending;
	}

	/**
	 * Checks if is down call pending.
	 *
	 * @return the downCallPending
	 */
	public boolean isDownCallPending() {
		return downCallPending;
	}

	/**
	 * Sets the down call pending.
	 *
	 * @param downCallPending the downCallPending to set
	 */
	public void setDownCallPending(boolean downCallPending) {
		this.downCallPending = downCallPending;
	}

	/**
	 * Gets the num floors.
	 *
	 * @return the nUM_FLOORS
	 */
	public int getNUM_FLOORS() {
		return NUM_FLOORS;
	}

	/**
	 * Gets the up.
	 *
	 * @return the up
	 */
	public static int getUp() {
		return UP;
	}

	/**
	 * Gets the down.
	 *
	 * @return the down
	 */
	public static int getDown() {
		return DOWN;
	}

	
}
