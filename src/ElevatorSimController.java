import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;

import building.Building;
import genericqueue.GenericQueue;
import myfileio.MyFileIO;
import passengers.Passengers;

// TODO: Auto-generated Javadoc
/**
 * The Class ElevatorSimController.
 */
// TODO: Auto-generated Javadoc
public class ElevatorSimController {
	// Owner by Sly
	
	/**  Constant to specify the configuration file for the simulation. */
	private static final String SIM_CONFIG = "ElevatorSimConfig.csv";
	
	/**  Constant to make the Passenger queue contents visible after initialization. */
	private boolean PASSQ_DEBUG=false;
	
	/** The gui. */
	private ElevatorSimulation gui;
	
	/** The building. */
	private Building building;
	
	/** The fio. */
	private MyFileIO fio;

	/** The num floors. */
	private final int NUM_FLOORS;
	
	/** The num floors. */
	private int numFloors;
	
	/** The capacity. */
	private int capacity;
	
	/** The floor ticks. */
	private int floorTicks;
	
	/** The door ticks. */
	private int doorTicks;
	
	/** The pass per tick. */
	private int passPerTick;
	
	/** The testfile. */
	private String testfile;
	
	/** The logfile. */
	private String logfile;
	
	/** The step cnt. */
	private int stepCnt = 0;
	
	/** The end sim. */
	private boolean endSim = false;
	
	/** passQ holds the time-ordered queue of Passengers, initialized at the start 
	 *  of the simulation. At the end of the simulation, the queue will be empty.
	 */
	private GenericQueue<Passengers> passQ;

	/**  The size of the queue to store Passengers at the start of the simulation. */
	private final int PASSENGERS_QSIZE = 1000;	
	
	/** The building passengers index. */
	private final int buildingPassengersIndex = 3;
	
	/**
	 * Instantiates a new elevator sim controller. 
	 * Reads the configuration file to configure the building and
	 * the elevator characteristics and also select the test
	 * to run. Reads the passenger data for the test to run to
	 * initialize the passenger queue in building...
	 *
	 * @param gui the gui
	 */
	public ElevatorSimController(ElevatorSimulation gui) {
		this.gui = gui;
		fio = new MyFileIO();
		configSimulation(SIM_CONFIG);
		NUM_FLOORS = numFloors;
		logfile = testfile.replaceAll(".csv", ".log");
		building = new Building(NUM_FLOORS,logfile);
		passQ = new GenericQueue<>(PASSENGERS_QSIZE);
		//TODO: YOU still need to configure the elevator in the building here....
		building.configElevator(numFloors, capacity, floorTicks, doorTicks, passPerTick);
		initializePassengerData(testfile);	
	}
	
	/**
	 * Controller updates the gui every step by getting values from building.
	 */
	private void controllerUpdatesTheGui() {
		if (gui == null)
			return;
		int elevatorY = building.getElevatorState()[2]; //is getCurrFloor 0-5
		//floor 1 at 11
		// floor 6 at 1
		elevatorY = NUM_FLOORS - (elevatorY + 1); // inverts
		elevatorY = elevatorY * 2 + 1; //adjusts to scale
		gui.updateGUI(building.getElevatorState()[0], building.getElevatorState()[1], elevatorY, 
				building.getElevatorState()[buildingPassengersIndex], building.getWaitingPassengers(), stepCnt);
	}
	
	/**
	 * Config simulation. Reads the filename, and parses the
	 * parameters.
	 *
	 * @param filename the filename
	 */
	private void configSimulation(String filename) {
		File configFile = fio.getFileHandle(filename);
		try ( BufferedReader br = fio.openBufferedReader(configFile)) {
			String line;
			while ((line = br.readLine())!= null) {
				parseElevatorConfigData(line);
			}
			fio.closeFile(br);
		} catch (IOException e) { 
			System.err.println("Error in reading file: "+filename);
			e.printStackTrace();
		}
	}
	
	/**
	 * Parses the elevator simulation config file to configure the simulation:
	 * number of floors and elevators, the actual test file to run, and the
	 * elevator characteristics.
	 *
	 * @param line the line
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void parseElevatorConfigData(String line) throws IOException {
		String[] values = line.split(",");
		if (values[0].equals("numFloors")) {
			numFloors = Integer.parseInt(values[1]);
		} else if (values[0].equals("passCSV")) {
			testfile = values[1];
		} else if (values[0].equals("capacity")) {
			capacity = Integer.parseInt(values[1]);
		} else if (values[0].equals("floorTicks")) {
			floorTicks = Integer.parseInt(values[1]);
		} else if (values[0].equals("doorTicks")) {
			doorTicks = Integer.parseInt(values[1]);
		} else if (values[0].equals("passPerTick")) {
			passPerTick = Integer.parseInt(values[1]);
		}
	}
	
	/**
	 * Initialize passenger data. Reads the supplied filename,
	 * and for each passenger group, identifies the pertinent information
	 * and adds it to the passengers queue in Building...
	 *
	 * @param filename the filename
	 */
	private void initializePassengerData(String filename) {
		boolean firstLine = true;
		File passInput = fio.getFileHandle(filename);
		try (BufferedReader br = fio.openBufferedReader(passInput)) {
			String line;
			while ((line = br.readLine())!= null) {
				if (firstLine) {
					firstLine = false;
					continue;
				}
				parsePassengerData(line);
			}
			fio.closeFile(br);
		} catch (IOException e) { 
			System.err.println("Error in reading file: "+filename);
			e.printStackTrace();
		}
		if (PASSQ_DEBUG) dumpPassQ();
	}	
	
	/**
	 * Parses the line of passenger data into tokens, and 
	 * passes those values to the building to be added to the
	 * passenger queue.
	 *
	 * @param line the line of passenger input data
	 */
	private void parsePassengerData(String line) {
		int time=0, numPass=0,fromFloor=0, toFloor=0;
		boolean polite = true;
		int wait = 1000;
		String[] values = line.split(",");
		for (int i = 0; i < values.length; i++) {
			switch (i) {
				case 0 : time      = Integer.parseInt(values[i]); break;
				case 1 : numPass   = Integer.parseInt(values[i]); break;
				case 2 : fromFloor   = Integer.parseInt(values[i]); break;
				case 3 : toFloor  = Integer.parseInt(values[i]); break;
				case 5 : wait      = Integer.parseInt(values[i]); break;
				case 4 : polite = "TRUE".equalsIgnoreCase(values[i]); break;
			}
		}
		passQ.add(new Passengers(time,numPass,fromFloor,toFloor,polite,wait));	
	}
	
	/**
	 * Gets the number of floors in the building.
	 *
	 * @return the num floors
	 */
	public int getNumFloors() {
		return NUM_FLOORS;
	}
	
	/**
	 * Gets the test name.
	 *
	 * @return the test name
	 */
	public String getTestName() {
		return (testfile.replaceAll(".csv", ""));
	}

	/**
	 * Enables or disables logging. A pass-through from the GUI to building
	 */
	public void enableLogging() {
		building.toggleLogging();
	}
	
 	/**
	 * Step sim. See the comments below for the functionality you
	 * must implement......
	 * Written by Dan
	 */
	public void stepSim() {
		stepCnt++;
		if (!passQ.isEmpty() || !allInStopState()) {
			checkPassQueue();
			building.updateElevator(stepCnt);
			controllerUpdatesTheGui();
		}
		else {
			controllerUpdatesTheGui();
			building.closeLogs(stepCnt);
			building.processPassengerData();
			if (gui != null) gui.endSimulation();
		}
	}
	
	/**
	 * Check pass queue.
	 * Written by Dan
	 */
	private void checkPassQueue() {
		boolean end = false;
		ArrayList<Passengers> newPassengers = new ArrayList<Passengers>();
		while (!end) {
			if (passQ.isEmpty()) break;
			if (passQ.peek().getTime() == stepCnt) {
				newPassengers.add(passQ.peek());
				passQ.poll();
			}
			else {
				end = true;
			}
		}
		building.addPassengersToQueue(newPassengers);
	}
	
	/**
	 * Written by Dan
	 * 
	 * All in stop state.
	 *
	 * @return true, if successful
	 */
	private boolean allInStopState() {
		return (building.getElevatorState()[0] == 0 && building.getElevatorState()[4] == 0);
	}

	/**
	 * Dump passQ contents. Debug hook to view the contents of the passenger queue...
	 */
	public void dumpPassQ() {
		ListIterator<Passengers> passengers = passQ.getListIterator();
		if (passengers != null) {
			System.out.println("Passengers Queue:");
			while (passengers.hasNext()) {
				Passengers p = passengers.next();
				System.out.println(p);
			}
		}
	}


	/**
	 * Gets the building. ONLY USED FOR JUNIT TESTING - YOUR GUI SHOULD NOT ACCESS THIS!.
	 *
	 * @return the building
	 */
	Building getBuilding() {
		return building;
	}

}
