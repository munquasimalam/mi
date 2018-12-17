
package com.medas.mi;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.medas.mi.listener.Cobas6000;
import com.medas.mi.listener.CobasB221;
import com.medas.mi.listener.CobasC111;
import com.medas.mi.listener.CobasC311;
import com.medas.mi.listener.CobasE411;
import com.medas.mi.listener.CobasP612;
import com.medas.mi.listener.Minividas;
import com.medas.mi.listener.Urisys1100;
import com.medas.mi.utils.AlertUtil;
import com.medas.mi.utils.XmlUtil;

import gnu.io.NoSuchPortException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * This class is used to open main Controls. which contains Menu,
 * MenuItems,FlowPane etc. Other Controls are added in FlowPane.
 */
public class MainControls extends Application {
	static final Logger logger = Logger.getLogger(MainControls.class);

	// BorderPane border = null;
	MenuItem menuItemLogin = null;
	Menu menuConf = null;
	// SerialPortListener serialPortListener = new SerialPortListener();
	public static Map<String, String> portSettingMap = null;
	public static FlowPane flowPane = null;
	private SerialPortControls serialPortControls = null;

	/**
	 * main method to launch the app.
	 * 
	 * @param args
	 *            is array of String.
	 */
	public static void main(String[] args) {
		// try {
		// new Thread() {
		// @Override
		// public void run () {
		// PropertyConfigurator.configure("log4j.properties");
		// logger.info("MainControls Launched :");
		// launch(MainControls.class, args);
		// }
		//
		// }.start();
		//
		// } catch (Exception ex) {
		// logger.error(ex.getMessage(), ex);
		// }

		try {
			PropertyConfigurator.configure("log4j.properties");
			logger.info("MainControls Launched :");
			launch(MainControls.class, args);

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}

	}

	@Override
	public void init() {
		System.out.println("init1:");
		flowPane = addFlowPane();
		serialPortControls = new SerialPortControls();

	}

	@Override
	public void start(Stage mainStage) {
		System.out.println("start2:");
		// Using border pane as the root for scene
		BorderPane borderPane = new BorderPane();
		HBox hbox = addHBox();
		borderPane.setTop(hbox);
		// Add a stack to the HBox in the top region
		// addStackPane(hbox);
		borderPane.setCenter(addFlowPane());
		Scene scene = new Scene(borderPane, 600, 400);
		mainStage.setScene(scene);
		mainStage.setTitle("Main Controls");
		mainStage.show();

		List<File> filesInFolder = null;
		try {
			filesInFolder = Files.walk(Paths.get("settings")).filter(Files::isRegularFile).map(Path::toFile)
					.collect(Collectors.toList());

			System.out.println("filesInFolder:" + filesInFolder);
			for (int i = 0; i < filesInFolder.size(); i++) {
				Map<String, String> settings = XmlUtil.readXmlFile(filesInFolder.get(i).getPath());
				if (settings.size() > 0) {
					System.out.println("settings.size() in start222:" + settings.size());
					serialPortControls.addControlsToFlowPane(flowPane, settings);

				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * Creates an HBox with MenuBar,Menu,MunuItems for the top region
	 */
	private HBox addHBox() {
		HBox hbox = new HBox();
		// hbox.setPadding(new Insets(15, 12, 15, 12));
		// hbox.setSpacing(10); // Gap between nodes
		// hbox.setStyle("-fx-background-color: #336699;");
		// hbox.setStyle("-fx-background-color: #CD5C5C;");

		menuConf = new Menu("Configuration");
		menuItemLogin = new MenuItem("Login");
		menuItemLogin.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				boolean isDisable = "Login".equals(menuItemLogin.getText()) ? false : true;
				menuConf.setDisable(isDisable);
				String loginText = "Logout".equals(menuItemLogin.getText()) ? "Login" : "Logout";
				menuItemLogin.setText(loginText);
				new LoginComponent().showLoginScreen(menuItemLogin);
			}
		});
		MenuItem clear = new MenuItem("Clear");
		clear.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));
		clear.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
			}
		});

		MenuItem exit = new MenuItem("Exit");
		exit.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				System.exit(0);
			}
		});

		Menu menuMenu = new Menu("Menu");
		menuMenu.getItems().addAll(menuItemLogin, clear, new SeparatorMenuItem(), exit);
		menuConf.setDisable(true);
		MenuItem serialPort = new MenuItem("Serial Port");
		serialPort.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				serialPortControls.addControlsToFlowPane(flowPane, null);
			}
		});
		MenuItem tcpServer = new MenuItem("TCP Server");
		tcpServer.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
			}
		});

		MenuItem tcpClient = new MenuItem("TCP Client");
		tcpClient.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
			}
		});

		menuConf.getItems().addAll(serialPort, tcpServer, tcpClient);
		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(menuMenu, menuConf);
		hbox.getChildren().addAll(menuBar);
		return hbox;
	}

	/*
	 * Uses a stack pane to create a help icon and adds it to the right side of
	 * an HBox
	 * 
	 * @param hb HBox to add the stack to
	 */
	private void addStackPane(HBox hb) {

		// portSettingMap = new PortSettingComponent.portSettingMap;

		Rectangle helpIcon = new Rectangle(30.0, 25.0);
		helpIcon.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
				new Stop[] { new Stop(0, Color.web("#4977A3")), new Stop(0.5, Color.web("#B0C6DA")),
						new Stop(1, Color.web("#9CB6CF")), }));
		helpIcon.setStroke(Color.web("#D0E6FA"));
		helpIcon.setArcHeight(3.5);
		helpIcon.setArcWidth(3.5);
		Text helpText = new Text("");
		Button saveBtn = new Button("save");
		saveBtn.setOnAction((event) -> {
			try {
				// XmlUtil.writeXmlFile(portSettingMap);
				System.out.println("created xml");
			} catch (Exception e) {
				System.out.println(e.getMessage());
				logger.error("addControlsToFlowPane():" + e.getMessage());

			}
		});
		helpText.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
		helpText.setFill(Color.WHITE);
		helpText.setStroke(Color.web("#7080A0"));
		StackPane stack = new StackPane();
		stack.getChildren().addAll(helpIcon, helpText, saveBtn);
		stack.setAlignment(Pos.CENTER_RIGHT);
		StackPane.setMargin(helpText, new Insets(0, 10, 0, 0));
		hb.getChildren().add(stack);
		HBox.setHgrow(stack, Priority.ALWAYS);

	}

	/*
	 * Creates a horizontal flow pane .
	 */

	private static FlowPane addFlowPane() {
		flowPane = new FlowPane();
		flowPane.setPadding(new Insets(5, 0, 5, 0));
		flowPane.setVgap(4);
		flowPane.setHgap(4);
		flowPane.setPrefWrapLength(270); // preferred width allows for two
											// columns
		flowPane.setStyle("-fx-background-color: DAE6F3;");
		return flowPane;
	}

}
