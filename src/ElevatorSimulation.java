
import java.util.ArrayList;
import java.util.logging.Logger;

import building.Elevator;
import javafx.animation.Animation;
import javafx.animation.Animation.Status;
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


// TODO: Auto-generated Javadoc
/**
 * The Class ElevatorSimulation.
 */
public class ElevatorSimulation extends Application {
	// Owned by Sly
	
	/**  Instantiate the GUI fields. */
	private ElevatorSimController controller;
	
	/** The num floors. */
	private final int NUM_FLOORS;
	
	/**  you MUST use millisPerTick as the duration for your timeline. */
	private static int millisPerTick = 250;

	/**  Local copies of the states for tracking purposes. */
	private final int STOP = Elevator.STOP;
	private final int MVTOFLR = Elevator.MVTOFLR;
	private final int OPENDR = Elevator.OPENDR;
	private final int OFFLD = Elevator.OFFLD;
	private final int BOARD = Elevator.BOARD;
	private final int CLOSEDR = Elevator.CLOSEDR;
	private final int MV1FLR = Elevator.MV1FLR;
	
	private BorderPane main;
	private GridPane gp;
	private final static Logger LOGGER = Logger.getLogger(ElevatorSimulation.class.getName());
	private Timeline t;
	private String defualtLogStyle = "";
	private static final int MAX_X_CELLS = 20;
	private static final int MAX_Y_CELLS = 13;
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
	 * Instantiates a new elevator simulation. Creates controller
	 */
	public ElevatorSimulation() {
		controller = new ElevatorSimController(this);	
		NUM_FLOORS = controller.getNumFloors();
	}

	/**
	 * Start. Initializes all states
	 *
	 * @param primaryStage the primary stage
	 * @throws Exception the exception
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Elevator Simulation - "+ controller.getTestName());
		primaryStage.show();

		main = new BorderPane();
        elevatorPane = new StackPane();
        makeShapes();	
		elevatorPane.getChildren().addAll(up, right, down, left, circle, o, c);
		c.setFont(Font.font("Tahoma", FontWeight.NORMAL, 24));
		o.setFont(Font.font("Tahoma", FontWeight.NORMAL, 24));
		setButtons();
        setGripPane();
        
        Scene scene = new Scene(main, 1000, 700);
		primaryStage.setScene(scene);
		initTimeline();
		
		for (int i = 0; i < passPane.length; i++) {
			passPane[i] = new HBox();
			gp.add(passPane[i], 0, i);
		}
	}
	
	/**
	 * Creates arrows to be used in updateGUI that represent a different elevator state.
	 */
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
	
	/**
	 * Initializes the control Buttons at the bottom of the border pane. Done once during start
	 */
	private void setButtons() {
		HBox buttonBox = new HBox();
		Button run = new Button("Run/Stop");
		Button step1 = new Button("Step");
		Button step2 = new Button("Step: ");
		TextField stepBox = new TextField("1");
		Button logger = new Button("Logger");
		logger.setOnAction(e -> controller.enableLogging());
		run.setOnAction(e -> toggleRun());
		step1.setOnAction(e -> stepTick(1));
		step2.setOnAction(e -> stepTick(Integer.parseInt(stepBox.getText())));
        buttonBox.getChildren().addAll(run, step1, step2, stepBox, logger, tickTxt);
        main.setBottom(buttonBox);
	}
	
	/**
	 * Initializes the GridPane in start.
	 */
	private void setGripPane() {
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
	
	/**
	 * Steps forward the controller the set number of ticks.
	 *
	 * @param numTicks the number of ticks stepped forward
	 */
	private void stepTick(int numTicks) {
		while (numTicks > 0) {
			controller.stepSim();
			numTicks--;
		}
	}
	
	/**
	 * Initializes the timeline to step the simulation based on millisPerTick.
	 */
	private void initTimeline() {
		//TODO: Code this method
		t = new Timeline(new KeyFrame(Duration.millis(millisPerTick), e -> controller.stepSim()));
	}
	
	/**
	 * Stops the timeline.
	 */
	public void endSimulation() {
		t.pause();
		System.out.println("Ended sim");
	}
	
	 
	/**
	 * Toggles run off and on based on previous state
	 */
	private void toggleRun() {
		if (t.getStatus().equals(Status.RUNNING)) {
			t.pause();
		}
		else {
			t.setCycleCount(Animation.INDEFINITE);
			t.play();
		}
	}
	
	/**
	 * Updates all parts of GUI every tick based on data passed through controller.
	 *
	 * @param currState the current state of the elevator
	 * @param currentDir the current direction of the elevator
	 * @param elevatorY the elevator Y value of the elevator(modified previously to line up with GUI Y values)
	 * @param elevatorNum the elevator number of passengers
	 * @param callingPeople the current passenger groups waiting for an elevator
	 * @param stepCnt the total amount of steps
	 */
	public void updateGUI(int currState, int currentDir, int elevatorY, int elevatorNum, ArrayList<Integer>[] callingPeople, int stepCnt) {
		tickTxt.setText("Total ticks: " + stepCnt);
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
		
		drawElevatorState(currState, currentDir);

		drawPassengers(callingPeople);
	}
	
	
	/**
	 * Draws elevator state using symbols to represent state.
	 *
	 * @param currState the current state of the elevator
	 * @param currentDir the current direction of the elevator
	 */
	private void drawElevatorState(int currState, int currentDir) {
		switch (currState) {
		case (MVTOFLR):
			if (currentDir == 1) up.setVisible(true);
			if (currentDir == -1) down.setVisible(true);
			break;
		case (MV1FLR):
			if (currentDir == 1) up.setVisible(true);
			if (currentDir == -1) down.setVisible(true);
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
	}
	
	
	/**
	 * Draws passenger groups with number representing amount in that group. Color and position represent calls going up or down.
	 *
	 * @param callingPeople the calling people waiting for an elevator
	 */
	private void drawPassengers(ArrayList<Integer>[] callingPeople) {
		for (int i = 0; i < NUM_FLOORS * 2; i++) {
			gp.getChildren().remove(passPane[i]);
			passPane[i] = new HBox();
			gp.add(passPane[i], 3, i);
			for (int j = 0; j < callingPeople[11-i].size(); j++) {
				VBox currPane = new VBox();
				StackPane currGroup = new StackPane();
				Ellipse currE = new Ellipse(0, 0, 15, 30);
				Text currText = new Text(callingPeople[11-i].get(j).toString());
				currText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 18));
				currGroup.getChildren().addAll(currE, currText);
				Circle expander = new Circle(0, 0, 20);
				expander.setVisible(false);
				currPane.getChildren().addAll(expander, currGroup);
				if (i % 2 == 0)
					currE.setFill(Color.RED);
				else
					currE.setFill(Color.BLUE);
				passPane[i].getChildren().add(currPane);
			}
		}
	}
	
	
	/**
	 * Sets the grid pane constraints.
	 */
	private void setGridPaneConstraints() {
		for (int i = 0; i < MAX_X_CELLS; i ++) 
			gp.getColumnConstraints().add(new ColumnConstraints(50));

		for (int i = 0; i < MAX_Y_CELLS; i ++) 
			gp.getRowConstraints().add(new RowConstraints(50));
	}

}
