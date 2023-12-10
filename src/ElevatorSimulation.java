
import java.util.ArrayList;
import java.util.logging.Logger;

import building.Elevator;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight; 


public class ElevatorSimulation extends Application {
	/** Instantiate the GUI fields */
	private ElevatorSimController controller;
	private final int NUM_FLOORS;
	private int currFloor;
	private int passengers;
	private int time;
	
	/** you MUST use millisPerTick as the duration for your timeline */
	private static int millisPerTick = 250;

	/** Local copies of the states for tracking purposes */
	private final int STOP = Elevator.STOP;
	private final int MVTOFLR = Elevator.MVTOFLR;
	private final int OPENDR = Elevator.OPENDR;
	private final int OFFLD = Elevator.OFFLD;
	private final int BOARD = Elevator.BOARD;
	private final int CLOSEDR = Elevator.CLOSEDR;
	private final int MV1FLR = Elevator.MV1FLR;
	
	// My vars
	private BorderPane main;
	private HBox buttonBox;
	private GridPane gp;
	private final static Logger LOGGER = Logger.getLogger(ElevatorSimulation.class.getName());
	private Timeline t;
	private String defualtLogStyle = "";
	private static final int MAX_X_CELLS = 20; // width/50
	private static final int MAX_Y_CELLS = 13;// height/50
	private Button tickTxt = new Button("Total Ticks: 0");
	
	// Signs and other elevator stuff
	private StackPane elevatorPane;
	private Polygon up, down, left, right;
	private int elevatorX = 1, elevatorY = 11;
	private Circle circle = new Circle(0, 0, 10);
	private Text c = new Text(1050, 150, "C"), o = new Text(50, 50, "O");
	private Line eLeft = new Line(50, -50, 50, 50);
	private Line eRight = new Line(50, -50, 50, 50);
	private Line eTop = new Line(-100, 0, 0, 0);
	private Line eBottom = new Line(-100, 0, 0, 0);
	private Text elevatorNum = new Text(0, 0, "0");
	
	// Building GUI stuff
	private Line g0 = new Line(0, 0, 650, 0);
	private Line g1 = new Line(0, 0, 650, 0);
	private Line g2 = new Line(0, 0, 650, 0);
	private Line g3 = new Line(0, 0, 650, 0);
	private Line g4 = new Line(0, 0, 650, 0);
	private Line g5 = new Line(0, 0, 650, 0);
	private Line g6 = new Line(0, 0, 650, 0);
	
	// Passengers
	private HBox[] passPane = new HBox[12];
	
	
	/**
	 * Instantiates a new elevator simulation.
	 */
	public ElevatorSimulation() {
		controller = new ElevatorSimController(this);	
		NUM_FLOORS = controller.getNumFloors();
		currFloor = 0;
	}

	/**
	 * Start.
	 *
	 * @param primaryStage the primary stage
	 * @throws Exception the exception
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		// You need to design the GUI. Note that the test name should
		// appear in the Title of the window!!
		primaryStage.setTitle("Elevator Simulation - "+ controller.getTestName());
		primaryStage.show();

		//TODO: Complete your GUI, including adding any helper methods.
		//      Meet the 30 line limit...
		main = new BorderPane();
		buttonBox = new HBox();
		Button run = new Button("Run");
		Button step1 = new Button("Step");
		Button step2 = new Button("Step: ");
		TextField stepBox = new TextField("1");
		Button logger = new Button("Logger");
		logger.setOnAction(e -> controller.enableLogging());
		run.setOnAction(e -> {t.setCycleCount(Animation.INDEFINITE); t.play();});
		step1.setOnAction(e -> stepTick(1));
		step2.setOnAction(e -> stepTick(Integer.parseInt(stepBox.getText())));
        buttonBox.getChildren().addAll(run, step1, step2, stepBox, logger, tickTxt);
        main.setBottom(buttonBox);
        
        elevatorPane = new StackPane();
        makeShapes();	
		elevatorPane.getChildren().addAll(up, right, down, left, circle, o, c);
		c.setFont(Font.font("Tahoma", FontWeight.NORMAL, 24));
		o.setFont(Font.font("Tahoma", FontWeight.NORMAL, 24));
		
        gp = new GridPane();
        main.setCenter(gp);
        setGridPaneConstraints();
        gp.add(elevatorPane, elevatorX - 1, elevatorY);
        gp.add(eLeft, elevatorX - 1, elevatorY);
        gp.add(eRight, elevatorX + 1, elevatorY);
        gp.add(eTop, elevatorX - 1, elevatorY - 1);
        gp.add(eBottom, elevatorX - 1, elevatorY + 1);
        gp.add(elevatorNum, elevatorX, elevatorY);
        elevatorNum.setFont(Font.font("Tahoma", FontWeight.NORMAL, 24));
        gp.add(g0, 2, 12);
        gp.add(g1, 2, 10);
        gp.add(g2, 2, 8);
        gp.add(g3, 2, 6);
        gp.add(g4, 2, 4);
        gp.add(g5, 2, 2);
        gp.add(g6, 2, 0);
        
        Scene scene = new Scene(main, 1000, 700);
		primaryStage.setScene(scene);
		initTimeline();
		
		/*ArrayList<Integer>[] testList = new ArrayList[12];
		ArrayList<Integer> testArrayList = new ArrayList<Integer>();
		testArrayList.add(1);
		testArrayList.add(2);
		testList[0] = testArrayList;
		testList[1] = testArrayList;
		testList[2] = testArrayList;
		testList[3] = testArrayList;
		testList[4] = testArrayList;
		testList[5] = testArrayList;
		testList[6] = testArrayList;
		testList[7] = testArrayList;
		testList[8] = testArrayList;
		testList[9] = testArrayList;
		testList[10] = testArrayList;
		testList[11] = testArrayList;
		updateGUI(Elevator.STOP, 0, 1, 0, testList);*/
		
		for (int i = 0; i < 12; i++) {
			passPane[i] = new HBox();
			gp.add(passPane[i], 0, i);
		}
	}
	
	/**
	 * The main method. Allows command line to modulate the speed of the simulation.
	 *
	 * @param args the arguments
	 */
	public static void main (String[] args) {
		if (args.length>0) {
			for (int i = 0; i < args.length-1; i++) {
				if ("-m".equals(args[i])) {
					try {
						ElevatorSimulation.millisPerTick = Integer.parseInt(args[i+1]);
					} catch (NumberFormatException e) {
						System.out.println("Unable to update millisPerTick to "+args[i+1]);
					}
				}
			}
		}
		Application.launch(args);
	}
	
	private void stepTick(int numTicks) {
		while (numTicks > 0) {
			controller.stepSim();
			numTicks--;
		}
	}
	
	private void initTimeline() {
		//TODO: Code this method
		t = new Timeline(new KeyFrame(Duration.millis(millisPerTick), e -> controller.stepSim()));
		t.setCycleCount(Animation.INDEFINITE);
	}
	
	public void endSimulation() {
		t.pause();
	}
	
	private void makeShapes() {
		up = new Polygon();  //Up
		up.getPoints().addAll(5.0,20.0,25.0,20.0,15.0,20-10*Math.pow(3,0.5));
		up.setStroke(Color.RED);
		up.setStrokeWidth(2);
		up.setFill(Color.RED);
		right = new Polygon();  // Right
		right.getPoints().addAll(up.getPoints());
		right.setRotate(90);
		right.setStroke(Color.CYAN);
		right.setStrokeWidth(2);
		right.setFill(Color.CYAN);
		down = new Polygon(); // Down
		down.getPoints().addAll(up.getPoints());
		down.setRotate(180);
		down.setStroke(Color.PURPLE);
		down.setStrokeWidth(2);
		down.setFill(Color.PURPLE);
		left = new Polygon();  //Left
		left.getPoints().addAll(up.getPoints());
		left.setRotate(270);
		left.setStroke(Color.GREEN);
		left.setStrokeWidth(2);
		left.setFill(Color.GREEN);
		up.setVisible(true);
		right.setVisible(false);
		down.setVisible(false);
		left.setVisible(false);
		circle.setFill(Color.RED);
	}
	
	public void updateGUI(int currState, int currentDir, int elevatorY, int elevatorNum, ArrayList<Integer>[] callingPeople) {
		tickTxt.setText("Total ticks: " + t.getCycleCount());
		this.elevatorY = elevatorY;
		this.elevatorNum.setText(Integer.toString(elevatorNum));
		up.setVisible(false);
		right.setVisible(false);
		down.setVisible(false);
		left.setVisible(false);
		circle.setVisible(false);
		c.setVisible(false);
		o.setVisible(false);
		
		gp.getChildren().removeAll(elevatorPane, eLeft, eRight, eTop, eBottom, this.elevatorNum);
		gp.add(elevatorPane, elevatorX - 1, elevatorY);
        gp.add(eLeft, elevatorX - 1, elevatorY);
        gp.add(eRight, elevatorX + 1, elevatorY);
        gp.add(eTop, elevatorX - 1, elevatorY - 1);
        gp.add(eBottom, elevatorX - 1, elevatorY + 1);
        gp.add(this.elevatorNum, elevatorX, elevatorY);
		
		switch (currState) {
		case (MVTOFLR):
			if (currentDir == 1)
				up.setVisible(true);
			if (currentDir == -1)
				down.setVisible(true);
			break;
		case (MV1FLR):
			if (currentDir == 1)
				up.setVisible(true);
			if (currentDir == -1)
				down.setVisible(true);
			break;
		case (OFFLD):
			right.setVisible(true);
			break;
		case (BOARD):
			left.setVisible(true);
			break;
		case (STOP):
			circle.setVisible(true);
			break;
		case (OPENDR):
			o.setVisible(true);
			break;
		case (CLOSEDR):
			c.setVisible(true);
			break;
		}

		for (int i = 0; i < NUM_FLOORS * 2; i++) {
			// get array of two array lists for each floor
			// loop through each array list and instantiate ellipse for each(change color and location for up vs. down)
				// Instantiate numbers on top
			
			gp.getChildren().remove(passPane[i]);
			passPane[i] = new HBox();
			gp.add(passPane[i], 3, i);
			for (int j = 0; j < callingPeople[i].size(); j++) {
				VBox currPane = new VBox();
				StackPane currGroup = new StackPane();
				Ellipse currE = new Ellipse(0, 0, 15, 30);
				Text currText = new Text(callingPeople[i].get(j).toString());
				currText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 18));
				currGroup.getChildren().addAll(currE, currText);
				Circle expander = new Circle(0, 0, 20);
				expander.setVisible(false);
				currPane.getChildren().addAll(expander, currGroup);
				if (i % 2 == 0)
					currE.setFill(Color.RED);
				else
					currE.setFill(Color.BLUE);
				// Add everything to scene in loop
				passPane[i].getChildren().add(currPane);
			}
		}
	}
	
	public void setGridPaneConstraints() {
		for (int i = 0; i < MAX_X_CELLS; i ++) 
			gp.getColumnConstraints().add(new ColumnConstraints(50));

		for (int i = 0; i < MAX_Y_CELLS; i ++) 
			gp.getRowConstraints().add(new RowConstraints(50));
	}

}
