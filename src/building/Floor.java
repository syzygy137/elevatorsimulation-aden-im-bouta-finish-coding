package building;
import java.util.ArrayList;
// ListIterater can be used to look at the contents of the floor queues for 
// debug/display purposes...
import java.util.ListIterator;

import genericqueue.GenericQueue;
import passengers.Passengers;

// TODO: Auto-generated Javadoc
/**
 * The Class Floor. This class provides the up/down queues to hold
 * Passengers as they wait for the Elevator. Written by Henry
 */
public class Floor {
	/**  Constant for representing direction. */
	private static final int UP = 1;
	private static final int DOWN = -1;

	/** The queues to represent Passengers going UP or DOWN */	
	private GenericQueue<Passengers> down;
	private GenericQueue<Passengers> up;

	/**
	 * Instantiates a new floor.
	 *
	 * @param qSize the q size
	 */
	public Floor(int qSize) {
		down = new GenericQueue<Passengers>(qSize);
		up = new GenericQueue<Passengers>(qSize);
	}
	
	// TODO: Write the helper methods needed for this class. 
	// You probably will only be accessing one queue at any
	// given time based upon direction - you could choose to 
	// account for this in your methods.
	/**
	 * Adds the passengers.
	 *
	 * @param passengers the passengers
	 */
	void addPassengers(Passengers passengers) {
		if (passengers.getDirection() == UP) {
			up.add(passengers);
		} else {
			down.add(passengers);
		}
	}
	
	/**
	 * Gets all passengers.
	 *
	 * @param getUp the get up
	 * @return the array list
	 */
	ArrayList<Integer> allPassengers(boolean getUp) {
		GenericQueue<Passengers> queue;
		if (getUp) {
			queue = up;
		} else {
			queue = down;
		}
		GenericQueue<Passengers> temp = new GenericQueue<Passengers>(queue.size());
		ArrayList<Integer> all = new ArrayList<Integer>();
		while (!queue.isEmpty()) {
			temp.add(queue.remove());
		}
		while (!temp.isEmpty()) {
			Passengers passengers = temp.remove();
			queue.add(passengers);
			all.add(passengers.getNumPass());
		}
		return all;
	}
	
	
	/**
	 * Peeks the up queue
	 *
	 * @return the passengers
	 */
	Passengers peekUp() {
		return up.peek();
	}
	
	/**
	 * Peeks the down queue
	 *
	 * @return the passengers
	 */
	Passengers peekDown() {
		return down.peek();
	}
	
	/**
	 * Removes the first person in up
	 *
	 * @return the passengers
	 */
	Passengers removeUp() {
		return up.remove();
	}
	
	/**
	 * Removes the first person in down
	 *
	 * @return the passengers
	 */
	Passengers removeDown() {
		return down.remove();
	}
	
	
	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 */
	boolean isEmpty() {
		return up.peek() == null && down.peek() == null;
	}
	/**
	 * Queue string. This method provides visibility into the queue
	 * contents as a string. What exactly you would want to visualize 
	 * is up to you
	 *
	 * @param dir determines which queue to look at
	 * @return the string of queue contents
	 */
	String queueString(int dir) {
		String str = "";
		ListIterator<Passengers> list;
		list = (dir == UP) ?up.getListIterator() : down.getListIterator();
		if (list != null) {
			while (list.hasNext()) {
				// choose what you to add to the str here.
				// Example: str += list.next().getNumPass();
				if (list.hasNext()) str += ",";
			}
		}
		return str;	
	}
	
	
}
