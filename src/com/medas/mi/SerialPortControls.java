
package com.medas.mi;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Set;

import org.apache.log4j.Logger;

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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

/**
 * It is used to create Serial port Controls. It Contains Menu, MunuItem etc.
 */
public class SerialPortControls {
	static final Logger logger = Logger.getLogger(SerialPortControls.class);
	static int controlsCount = -1;
	static MenuBar menuBarSerialPortControls = null;
	static TextArea textArea = null;
	private static boolean startFlag = true;
	// static CopyOnWriteArrayList<Integer> controlsIndexList = new
	// CopyOnWriteArrayList<Integer>();
	static List<Integer> controlsIndexList = new ArrayList<Integer>();
	public static Map<String, String> portSettingMap = null;
	// public List<String> xmlSetting = new ArrayList<String>();
	// public List<String> xmlSettingList = new ArrayList<String>();
	public List<PortSettingComponent> xmlSettingList = new ArrayList<PortSettingComponent>();
	private PortSettingComponent portSettingComponent = new PortSettingComponent();
	// private static Set<String> machineNames = new HashSet<String>();
	public static Map<Integer, String> machineNames = new HashMap<Integer, String>();
	
//	Button settingBtn = null;
//	Button connectBtn = null;
//	Button removeBtn = null;

	/**
	 * It is used to add Serial Port Controls in Flow pane.
	 * 
	 * @param flow
	 *            is the Object of Flow Pane
	 * @return it return object of FlowPane.
	 */
	public FlowPane addControlsToFlowPane(FlowPane flowPaneMainControls, Map<String, String> settings) {

		FlowPane flowPaneSerialControls = new FlowPane();
		// flowPaneSerialControls.setMaxWidth(1000);
		// flowPaneSerialControls.setMaxHeight(20);
		flowPaneSerialControls.setPadding(new Insets(10, 10, 10, 10));
		// flowPaneSerialControls.setStyle("-fx-background-color: #D8BFD8;");
		// flowPaneSerialControls.setStyle("-fx-background-color: #CD5C5C;");
		flowPaneSerialControls.setStyle("-fx-background-color: #87CEFA;");
		flowPaneSerialControls.setHgap(5);
		ImageView imageView = new ImageView("com/medas/mi/graphics/cross.png");

		Label instrumentName = new Label();

		Button settingBtn = new Button("S");
		Button connectBtn = new Button("C");
		Button removeBtn = new Button("R");
//		 settingBtn = new Button("S");
//		 connectBtn = new Button("C");
//		 removeBtn = new Button("R");
		
		flowPaneSerialControls.getChildren().addAll(imageView, instrumentName, settingBtn, connectBtn, removeBtn);

		if ("C".equalsIgnoreCase(connectBtn.getText())) {
			connectBtn.setDisable(true);
		}

		controlsCount++;
		controlsIndexList.add(controlsCount);
		System.out.println("*************controlsIndexList add panel:" + controlsIndexList);
		removeBtn.setId(String.valueOf(controlsCount));
		machineNames.put(controlsCount, "");
		System.out.println("machineNames add controls:" + machineNames);
		textArea = new TextArea(String.valueOf(controlsCount));
		System.out.println("count set in textArea:" + controlsCount);

		AnchorPane anchorpane = new AnchorPane();

		// FlowPane anchorpane = new FlowPane();
		// anchorpane.setMaxWidth(1000);
		// anchorpane.setMaxHeight(1000);

		TextArea textArea = new TextArea();

		textArea.setMaxWidth(250);// height and width of AnchorPane and flowPane
									// are only decided by TextArea and
									// BorderPane.
		textArea.setMaxHeight(100);

		textArea.appendText("mmmmmmmmmmm \n");

		anchorpane.setStyle("-fx-background-color: #A9A9A9;");

		anchorpane.getChildren().addAll(textArea);

		BorderPane pane = new BorderPane();
		pane.setMaxWidth(250);
		pane.setMaxHeight(100);
		pane.setTop(flowPaneSerialControls);
		pane.setCenter(anchorpane);

		flowPaneMainControls.getChildren().add(pane);
		if (startFlag) {
			connectOnStartUp(settingBtn, connectBtn, removeBtn, instrumentName, imageView, settings);
		}

		settingBtn.setOnAction((event) -> {
			try {

				// new PortSettingComponent().createPortSettingWindow();
				portSettingComponent.createPortSettingWindow();

				connectBtn.setDisable(false);
			} catch (NoSuchPortException e) {
				System.out.println(e.getMessage());
				logger.error("addControlsToFlowPane():" + e.getMessage());

			} catch (PortNotFoundException ex) {
				System.out.println(ex.getMessage());
				logger.error("addControlsToFlowPane():" + ex.getMessage());
			}
		});

		connectBtn.setOnAction((event) -> {
			startFlag = false;
			
			portSettingMap = portSettingComponent.getPortSetting();
			if(portSettingMap.size() == 0) {
				
				portSettingMap = XmlUtil.readXmlFile("settings/"+ instrumentName.getText() +".xml");
				 String instName = instrumentName.getText();	
				 System.out.println("insName:" +instName);
			}
			
			
			System.out.println("portSettingMap:" +portSettingMap);
			boolean isDisable = "C".equalsIgnoreCase(connectBtn.getText()) ? true : false;
			if ("C".equalsIgnoreCase(connectBtn.getText())) {

				
				//portSettingMap = portSettingComponent.getPortSetting();
				machineNames.put(Integer.parseInt(removeBtn.getId()), portSettingMap.get("machineName"));
				System.out.println("machineNames connect:" + machineNames);
				/*
				 * machineNames.add(portSettingMap.get("machineName"));
				 * System.out.println("machineNames connect:" +machineNames);
				 */
				//
				// PortSettingComponent portSettingComp = new
				// PortSettingComponent();
				// for(Entry<String, String> entry : portSettingMap.entrySet())
				// {
				// System.out.println("key:" +entry.getKey() + " Value : "
				// +entry.getValue());
				// portSettingComp.setMachineName(portSettingMap.get("machineName"));
				// portSettingComp.machineId(portSettingMap.get("machineId"));
				// portSettingComp.setComPort(portSettingMap.get("comPort"));
				// portSettingComp.setBitsPerSecond(portSettingMap.get("bitsPerSecond"));
				// portSettingComp.setDataBits(portSettingMap.get("dataBits"));
				// portSettingComp.setStopBit(portSettingMap.get("stopBit"));
				// portSettingComp.setParity(portSettingMap.get("Parity"));
				// portSettingComp.setIsPrintLog(portSettingMap.get("isPrintLog"));
				//
				// // xmlSetting.add(portSettingComp);
				//
				// }
				// xmlSettingList.add(Integer.parseInt(removeBtn.getId()),portSettingComp);
				
				//instrumentName.setText(PortSettingComponent.instrumentName);
				instrumentName.setText(portSettingMap.get("machineName"));
				
				System.out.println("instrumentName.getText()connectBtn:" + instrumentName.getText());
				System.out.println("machineName connectBtn:" + portSettingMap.get("machineName"));

				System.out.println(" ************c portSettingMap:" + portSettingMap);
				// new SerialPortListener(portSettingMap);
				switch (portSettingMap.get("machineName")) {
				// switch ("cobas p 612") {
				// case "cobas p 612":
				case "CobasP612":
					final CobasP612 serialPortListenerCobasP612 = new CobasP612(portSettingMap.get("machineId"),
							portSettingMap.get("comPort"), portSettingMap.get("bitsPerSecond"),
							portSettingMap.get("dataBits"), portSettingMap.get("parity"), portSettingMap.get("stopBit"),
							portSettingMap.get("isPrintLog"), textArea);
					serialPortListenerCobasP612.initialize();

					System.out.println("isConnected:" + serialPortListenerCobasP612.isConnected());
					if (serialPortListenerCobasP612.isConnected()) {
						AlertUtil.connectionSuccessAlert(portSettingMap.get("comPort").toString());
						imageView.setImage(new Image("com/medas/mi/graphics/tick.png"));
						// xmlSettingList.add(Integer.parseInt(removeBtn.getId()),portSettingComp);

					} else {
						imageView.setImage(new Image("com/medas/mi/graphics/cross.png"));
					}
					System.out.println("****com port" + portSettingMap.get("comPort"));
					System.out.println("****CobasP612");

					break;
				// case "cobas 6000 (SWA)":
				case "Cobas6000":
					// new SerialPortListenerCobas6000(portSettingMap);
					final Cobas6000 serialPortListenerCobas6000 = new Cobas6000(portSettingMap.get("machineId"),
							portSettingMap.get("comPort"), portSettingMap.get("bitsPerSecond"),
							portSettingMap.get("dataBits"), portSettingMap.get("parity"), portSettingMap.get("stopBit"),
							portSettingMap.get("isPrintLog"), textArea);
					serialPortListenerCobas6000.initialize();
					System.out.println("isConnected:" + serialPortListenerCobas6000.isConnected());
					if (serialPortListenerCobas6000.isConnected()) {
						AlertUtil.connectionSuccessAlert(portSettingMap.get("comPort").toString());
						imageView.setImage(new Image("com/medas/mi/graphics/tick.png"));
					} else {
						imageView.setImage(new Image("com/medas/mi/graphics/cross.png"));
					}
					System.out.println("****com port" + portSettingMap.get("comPort"));
					System.out.println("****cobas  6000 (SWA)");

					break;
				// case "cobas b 221 (Roche OMNI S)":
				case "CobasB221":
					final CobasB221 serialPortListenerCobasB221 = new CobasB221(portSettingMap.get("machineId"),
							portSettingMap.get("comPort"), portSettingMap.get("bitsPerSecond"),
							portSettingMap.get("dataBits"), portSettingMap.get("parity"), portSettingMap.get("stopBit"),
							portSettingMap.get("isPrintLog"), textArea);
					serialPortListenerCobasB221.initialize();
					System.out.println("isConnected:" + serialPortListenerCobasB221.isConnected());
					if (serialPortListenerCobasB221.isConnected()) {
						AlertUtil.connectionSuccessAlert(portSettingMap.get("comPort").toString());
						imageView.setImage(new Image("com/medas/mi/graphics/tick.png"));
					} else {
						imageView.setImage(new Image("com/medas/mi/graphics/cross.png"));
					}
					System.out.println("****com port" + portSettingMap.get("comPort"));
					System.out.println("****cobas  b 221 (Roche OMNI S)");
					break;
				case "CobasC311":
					final CobasC311 serialPortListenerCobasC311 = new CobasC311(portSettingMap.get("machineId"),
							portSettingMap.get("comPort"), portSettingMap.get("bitsPerSecond"),
							portSettingMap.get("dataBits"), portSettingMap.get("parity"), portSettingMap.get("stopBit"),
							portSettingMap.get("isPrintLog"), textArea);
					serialPortListenerCobasC311.initialize();
					System.out.println("isConnected:" + serialPortListenerCobasC311.isConnected());
					if (serialPortListenerCobasC311.isConnected()) {
						AlertUtil.connectionSuccessAlert(portSettingMap.get("comPort").toString());
						imageView.setImage(new Image("com/medas/mi/graphics/tick.png"));
					} else {
						imageView.setImage(new Image("com/medas/mi/graphics/cross.png"));
					}
					System.out.println("****com port" + portSettingMap.get("comPort"));
					System.out.println("****cobas  c 311 (SWA)");

					break;
				case "CobasC111":
					final CobasC111 cobasC111 = new CobasC111(portSettingMap.get("machineId"),
							portSettingMap.get("comPort"), portSettingMap.get("bitsPerSecond"),
							portSettingMap.get("dataBits"), portSettingMap.get("parity"), portSettingMap.get("stopBit"),
							portSettingMap.get("isPrintLog"), textArea);
					cobasC111.initialize();
					System.out.println("isConnected:" + cobasC111.isConnected());
					if (cobasC111.isConnected()) {
						AlertUtil.connectionSuccessAlert(portSettingMap.get("comPort").toString());
						imageView.setImage(new Image("com/medas/mi/graphics/tick.png"));
					} else {
						imageView.setImage(new Image("com/medas/mi/graphics/cross.png"));
					}
					System.out.println("****com port" + portSettingMap.get("comPort"));
					System.out.println("****cobasC111");

					break;
				case "CobasE411":
					final CobasE411 serialPortListenerCobasE411 = new CobasE411(portSettingMap.get("machineId"),
							portSettingMap.get("comPort"), portSettingMap.get("bitsPerSecond"),
							portSettingMap.get("dataBits"), portSettingMap.get("parity"), portSettingMap.get("stopBit"),
							portSettingMap.get("isPrintLog"), textArea);
					serialPortListenerCobasE411.initialize();
					System.out.println("isConnected:" + serialPortListenerCobasE411.isConnected());
					if (serialPortListenerCobasE411.isConnected()) {
						AlertUtil.connectionSuccessAlert(portSettingMap.get("comPort").toString());
						imageView.setImage(new Image("com/medas/mi/graphics/tick.png"));
					} else {
						imageView.setImage(new Image("com/medas/mi/graphics/cross.png"));
					}
					System.out.println("****com port" + portSettingMap.get("comPort"));
					System.out.println("****cobas  e 411 (SWA)");
					break;

				case "Urisys1100":
					final Urisys1100 serialPortListenerUrisys1100 = new Urisys1100(portSettingMap.get("machineId"),
							portSettingMap.get("comPort"), portSettingMap.get("bitsPerSecond"),
							portSettingMap.get("dataBits"), portSettingMap.get("parity"), portSettingMap.get("stopBit"),
							portSettingMap.get("isPrintLog"), textArea);
					serialPortListenerUrisys1100.initialize();
					System.out.println("isConnected:" + serialPortListenerUrisys1100.isConnected());
					if (serialPortListenerUrisys1100.isConnected()) {
						AlertUtil.connectionSuccessAlert(portSettingMap.get("comPort").toString());
						imageView.setImage(new Image("com/medas/mi/graphics/tick.png"));
					} else {
						imageView.setImage(new Image("com/medas/mi/graphics/cross.png"));
					}
					System.out.println("****com port" + portSettingMap.get("comPort"));
					System.out.println("****URISYS 1100");
					break;

				case "Minividas":
					final Minividas minividas = new Minividas(portSettingMap.get("machineId"),
							portSettingMap.get("comPort"), portSettingMap.get("bitsPerSecond"),
							portSettingMap.get("dataBits"), portSettingMap.get("parity"), portSettingMap.get("stopBit"),
							portSettingMap.get("isPrintLog"), textArea);
					minividas.initialize();
					System.out.println("isConnected:" + minividas.isConnected());
					if (minividas.isConnected()) {
						AlertUtil.connectionSuccessAlert(portSettingMap.get("comPort").toString());
						imageView.setImage(new Image("com/medas/mi/graphics/tick.png"));
					} else {
						imageView.setImage(new Image("com/medas/mi/graphics/cross.png"));
					}
					System.out.println("****com port" + portSettingMap.get("comPort"));
					System.out.println("****minividas");
					// case "clinitek":
					// SerialPortListenerClinitek serialPortListenerClinitek =
					// new SerialPortListenerClinitek(portSettingMap);
					// System.out.println("isConnected:"+serialPortListenerClinitek.isConnected());
					// if(serialPortListenerClinitek.isConnected()) {
					// AlertUtil.connectionSuccessAlert(portSettingMap.get("comPort").toString());
					// }
					// break;

				default:
					System.out.println("No Correct Machine Name Selected.");
					logger.error("No Correct Machine Name Selected.");

				}

				settingBtn.setDisable(isDisable);
				removeBtn.setDisable(isDisable);
				connectBtn.setText("D");

				// imageView.setImage(new
				// Image("com/medas/mi/graphics/tick.png"));

			} else if ("D".equalsIgnoreCase(connectBtn.getText())) {

				try {
					// xmlSettingList.remove(portSettingMap);
					// System.out.println(" D xmlSettingList:" +xmlSettingList);

					switch (portSettingMap.get("machineName")) {
					case "Cobas6000":
						new Cobas6000(portSettingMap.get("comPort")).close();
						break;

					case "CobasP612":
						new CobasP612(portSettingMap.get("comPort")).close();
						break;
					case "CobasB221":
						new CobasB221(portSettingMap.get("comPort")).close();
						break;
					case "CobasC311":
						new CobasC311(portSettingMap.get("comPort")).close();
						break;
					case "CobasE411":
						new CobasE411(portSettingMap.get("comPort")).close();
						break;
					case "Urisys1100":
						new Urisys1100(portSettingMap.get("comPort")).close();
						break;
					// case "clinitek":
					// new
					// SerialPortListenerCobas6000(portSettingMap.get("comPort")).close();
					// break;

					}

					imageView.setImage(new Image("com/medas/mi/graphics/cross.png"));
					settingBtn.setDisable(isDisable);
					removeBtn.setDisable(isDisable);
					connectBtn.setText("C");

				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		});

		removeBtn.setOnAction((event) -> {
			System.out.println(" removeItem Id123..  :" + removeBtn.getId());
			removeFlowPaneControls(flowPaneMainControls, controlsIndexList, removeBtn, instrumentName);
		});

		return flowPaneMainControls;

	}

	private static FlowPane removeFlowPaneControls(FlowPane flowPaneMainControls, List<Integer> controlsIndexList,
			Button removeBtn, Label instrumentName) {
		try {

			int controlsIndex = controlsIndexList.indexOf(Integer.parseInt(removeBtn.getId()));
			System.out.println("controlsIndex:" + controlsIndex);

			switch (controlsIndex) {
			case 0:
				flowPaneMainControls.getChildren().remove(0);
				break;
			case 1:
				flowPaneMainControls.getChildren().remove(1);
				break;
			case 2:
				flowPaneMainControls.getChildren().remove(controlsIndex);
				break;
			case 3:
				flowPaneMainControls.getChildren().remove(controlsIndex);
				break;
			case 4:
				flowPaneMainControls.getChildren().remove(controlsIndex);
				break;
			case 5:
				flowPaneMainControls.getChildren().remove(controlsIndex);
				break;
			case 6:
				flowPaneMainControls.getChildren().remove(controlsIndex);
				break;
			case 7:
				flowPaneMainControls.getChildren().remove(controlsIndex);
				break;
			case 8:
				flowPaneMainControls.getChildren().remove(controlsIndex);
				break;
			case 9:
				flowPaneMainControls.getChildren().remove(controlsIndex);
				break;
			default:
				flowPaneMainControls.getChildren().remove(controlsIndex);
				break;

			}

			// flow.getChildren().remove(menuBarSerialPortControls);
			// flow.getChildren().remove(textArea);
			controlsIndexList.remove(controlsIndex);
			System.out.println("*************controlsIndexList remove:" + controlsIndexList);

			List<File> filesInFolder = null;

			filesInFolder = Files.walk(Paths.get("settings")).filter(Files::isRegularFile).map(Path::toFile)
					.collect(Collectors.toList());

			// System.out.println("filesInFolder.get(0).getPath():"
			// +filesInFolder.get(0).getName());

			for (int i = 0; i < filesInFolder.size(); i++) {
				if (filesInFolder.get(i).getName()
						.equals(machineNames.get(Integer.parseInt(removeBtn.getId())) + ".xml")) {
					System.out.println("filesInFolder delete delete:" + filesInFolder.size());
					filesInFolder.get(i).delete();
					System.out.println("filesInFolder after delete:" + filesInFolder.size());
				}

			}
			System.out.println("machineNames before remove:" + machineNames);
			// System.out.println("instrumentName.getText():"
			// +instrumentName.getText());
			machineNames.remove(Integer.parseInt(removeBtn.getId()));
			System.out.println("machineNames after remove:" + machineNames);

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}

		return flowPaneMainControls;
	}

	public static void showReceivedData(TextArea textArea, String strReceived) {
		textArea.appendText(strReceived + "\n");
	}

	public void connectOnStartUp(Button settingBtn, Button connectBtn, Button removeBtn, Label instrumentName,
			ImageView imageView, Map<String, String> settings) {
		boolean isDisable = "C".equalsIgnoreCase(connectBtn.getText()) ? true : false;
		Map<String, String> portSettingMap = settings;
		if ("C".equalsIgnoreCase(connectBtn.getText()) && settings != null) {

			// Map<String,String> portSettingMap =
			// PortSettingComponent.portSettingMap;
			// portSettingMap = portSettingComponent.getPortSetting();
			//Map<String, String> portSettingMap = settings;
			// List<String> xmlSetting = new ArrayList<String>();
			//
			// PortSettingComponent portSettingComp = new
			// PortSettingComponent();
			// for(Entry<String, String> entry : portSettingMap.entrySet()) {
			// System.out.println("key:" +entry.getKey() + " Value : "
			// +entry.getValue());
			// portSettingComp.setMachineName(portSettingMap.get("machineName"));
			// portSettingComp.machineId(portSettingMap.get("machineId"));
			// portSettingComp.setComPort(portSettingMap.get("comPort"));
			// portSettingComp.setBitsPerSecond(portSettingMap.get("bitsPerSecond"));
			// portSettingComp.setDataBits(portSettingMap.get("dataBits"));
			// portSettingComp.setStopBit(portSettingMap.get("stopBit"));
			// portSettingComp.setParity(portSettingMap.get("Parity"));
			// portSettingComp.setIsPrintLog(portSettingMap.get("isPrintLog"));
			//
			// // xmlSetting.add(portSettingComp);
			//
			// }
			// xmlSettingList.add(Integer.parseInt(removeBtn.getId()),portSettingComp);
			instrumentName.setText(portSettingMap.get("machineName"));
			//machineNames.put(controlsCount, portSettingMap.get("machineName"));

			// System.out.println("instrumentName
			// connectBtn:"+PortSettingComponent.instrumentName);
			System.out.println("machineName connectBtn:" + portSettingMap.get("machineName"));

			System.out.println(" ************c portSettingMap:" + portSettingMap);
			// new SerialPortListener(portSettingMap);
			switch (portSettingMap.get("machineName")) {
			// switch ("cobas p 612") {
			// case "cobas p 612":
			case "CobasP612":
				final CobasP612 serialPortListenerCobasP612 = new CobasP612(portSettingMap.get("machineId"),
						portSettingMap.get("comPort"), portSettingMap.get("bitsPerSecond"),
						portSettingMap.get("dataBits"), portSettingMap.get("parity"), portSettingMap.get("stopBit"),
						portSettingMap.get("isPrintLog"), textArea);
				serialPortListenerCobasP612.initialize();

				System.out.println("isConnected:" + serialPortListenerCobasP612.isConnected());
				if (serialPortListenerCobasP612.isConnected()) {
					AlertUtil.connectionSuccessAlert(portSettingMap.get("comPort").toString());
					imageView.setImage(new Image("com/medas/mi/graphics/tick.png"));
					// xmlSettingList.add(Integer.parseInt(removeBtn.getId()),portSettingComp);

				} else {
					imageView.setImage(new Image("com/medas/mi/graphics/cross.png"));
				}
				System.out.println("****com port" + portSettingMap.get("comPort"));
				System.out.println("****CobasP612");

				break;
			// case "cobas 6000 (SWA)":
			case "Cobas6000":
				// new SerialPortListenerCobas6000(portSettingMap);
				final Cobas6000 serialPortListenerCobas6000 = new Cobas6000(portSettingMap.get("machineId"),
						portSettingMap.get("comPort"), portSettingMap.get("bitsPerSecond"),
						portSettingMap.get("dataBits"), portSettingMap.get("parity"), portSettingMap.get("stopBit"),
						portSettingMap.get("isPrintLog"), textArea);
				serialPortListenerCobas6000.initialize();
				System.out.println("isConnected:" + serialPortListenerCobas6000.isConnected());
				if (serialPortListenerCobas6000.isConnected()) {
					imageView.setImage(new Image("com/medas/mi/graphics/tick.png"));
				} else {
					imageView.setImage(new Image("com/medas/mi/graphics/cross.png"));
				}
				System.out.println("****com port" + portSettingMap.get("comPort"));
				System.out.println("****cobas  6000 (SWA)");

				break;
			// case "cobas b 221 (Roche OMNI S)":
			case "CobasB221":
				final CobasB221 serialPortListenerCobasB221 = new CobasB221(portSettingMap.get("machineId"),
						portSettingMap.get("comPort"), portSettingMap.get("bitsPerSecond"),
						portSettingMap.get("dataBits"), portSettingMap.get("parity"), portSettingMap.get("stopBit"),
						portSettingMap.get("isPrintLog"), textArea);
				serialPortListenerCobasB221.initialize();
				System.out.println("isConnected:" + serialPortListenerCobasB221.isConnected());
				if (serialPortListenerCobasB221.isConnected()) {
					// AlertUtil.connectionSuccessAlert(portSettingMap.get("comPort").toString());
					imageView.setImage(new Image("com/medas/mi/graphics/tick.png"));
				} else {
					imageView.setImage(new Image("com/medas/mi/graphics/cross.png"));
				}
				System.out.println("****com port" + portSettingMap.get("comPort"));
				System.out.println("****cobas  b 221 (Roche OMNI S)");
				break;
			case "CobasC311":
				final CobasC311 serialPortListenerCobasC311 = new CobasC311(portSettingMap.get("machineId"),
						portSettingMap.get("comPort"), portSettingMap.get("bitsPerSecond"),
						portSettingMap.get("dataBits"), portSettingMap.get("parity"), portSettingMap.get("stopBit"),
						portSettingMap.get("isPrintLog"), textArea);
				serialPortListenerCobasC311.initialize();
				System.out.println("isConnected:" + serialPortListenerCobasC311.isConnected());
				if (serialPortListenerCobasC311.isConnected()) {
					imageView.setImage(new Image("com/medas/mi/graphics/tick.png"));
				} else {
					imageView.setImage(new Image("com/medas/mi/graphics/cross.png"));
				}
				System.out.println("****com port" + portSettingMap.get("comPort"));
				System.out.println("****cobas  c 311 (SWA)");

				break;
			case "CobasC111":
				final CobasC111 cobasC111 = new CobasC111(portSettingMap.get("machineId"),
						portSettingMap.get("comPort"), portSettingMap.get("bitsPerSecond"),
						portSettingMap.get("dataBits"), portSettingMap.get("parity"), portSettingMap.get("stopBit"),
						portSettingMap.get("isPrintLog"), textArea);
				cobasC111.initialize();
				System.out.println("isConnected:" + cobasC111.isConnected());
				if (cobasC111.isConnected()) {
					imageView.setImage(new Image("com/medas/mi/graphics/tick.png"));
				} else {
					imageView.setImage(new Image("com/medas/mi/graphics/cross.png"));
				}
				System.out.println("****com port" + portSettingMap.get("comPort"));
				System.out.println("****cobasC111");

				break;
			case "CobasE411":
				final CobasE411 serialPortListenerCobasE411 = new CobasE411(portSettingMap.get("machineId"),
						portSettingMap.get("comPort"), portSettingMap.get("bitsPerSecond"),
						portSettingMap.get("dataBits"), portSettingMap.get("parity"), portSettingMap.get("stopBit"),
						portSettingMap.get("isPrintLog"), textArea);
				serialPortListenerCobasE411.initialize();
				System.out.println("isConnected:" + serialPortListenerCobasE411.isConnected());
				if (serialPortListenerCobasE411.isConnected()) {
					imageView.setImage(new Image("com/medas/mi/graphics/tick.png"));
				} else {
					imageView.setImage(new Image("com/medas/mi/graphics/cross.png"));
				}
				System.out.println("****com port" + portSettingMap.get("comPort"));
				System.out.println("****cobas  e 411 (SWA)");
				break;

			case "Urisys1100":
				final Urisys1100 serialPortListenerUrisys1100 = new Urisys1100(portSettingMap.get("machineId"),
						portSettingMap.get("comPort"), portSettingMap.get("bitsPerSecond"),
						portSettingMap.get("dataBits"), portSettingMap.get("parity"), portSettingMap.get("stopBit"),
						portSettingMap.get("isPrintLog"), textArea);
				serialPortListenerUrisys1100.initialize();
				System.out.println("isConnected:" + serialPortListenerUrisys1100.isConnected());
				if (serialPortListenerUrisys1100.isConnected()) {
					imageView.setImage(new Image("com/medas/mi/graphics/tick.png"));
				} else {
					imageView.setImage(new Image("com/medas/mi/graphics/cross.png"));
				}
				System.out.println("****com port" + portSettingMap.get("comPort"));
				System.out.println("****URISYS 1100");
				break;

			case "Minividas":
				final Minividas minividas = new Minividas(portSettingMap.get("machineId"),
						portSettingMap.get("comPort"), portSettingMap.get("bitsPerSecond"),
						portSettingMap.get("dataBits"), portSettingMap.get("parity"), portSettingMap.get("stopBit"),
						portSettingMap.get("isPrintLog"), textArea);
				minividas.initialize();
				System.out.println("isConnected:" + minividas.isConnected());
				if (minividas.isConnected()) {
					imageView.setImage(new Image("com/medas/mi/graphics/tick.png"));
				} else {
					imageView.setImage(new Image("com/medas/mi/graphics/cross.png"));
				}
				System.out.println("****com port" + portSettingMap.get("comPort"));
				System.out.println("****minividas");
				// case "clinitek":
				// SerialPortListenerClinitek serialPortListenerClinitek = new
				// SerialPortListenerClinitek(portSettingMap);
				// System.out.println("isConnected:"+serialPortListenerClinitek.isConnected());
				// if(serialPortListenerClinitek.isConnected()) {
				// AlertUtil.connectionSuccessAlert(portSettingMap.get("comPort").toString());
				// }
				// break;

			default:
				System.out.println("No Correct Machine Name Selected.");
				logger.error("No Correct Machine Name Selected.");

			}

			settingBtn.setDisable(isDisable);
			removeBtn.setDisable(isDisable);
			connectBtn.setDisable(false);
			connectBtn.setText("D");
		} else if ("D".equalsIgnoreCase(connectBtn.getText())) {

			try {
				// xmlSettingList.remove(portSettingMap);
				// System.out.println(" D xmlSettingList:" +xmlSettingList);

				switch (portSettingMap.get("machineName")) {
				case "Cobas6000":
					new Cobas6000(portSettingMap.get("comPort")).close();
					break;

				case "CobasP612":
					new CobasP612(portSettingMap.get("comPort")).close();
					break;
				case "CobasB221":
					new CobasB221(portSettingMap.get("comPort")).close();
					break;
				case "CobasC311":
					new CobasC311(portSettingMap.get("comPort")).close();
					break;
				case "CobasE411":
					new CobasE411(portSettingMap.get("comPort")).close();
					break;
				case "Urisys1100":
					new Urisys1100(portSettingMap.get("comPort")).close();
					break;
				// case "clinitek":
				// new
				// SerialPortListenerCobas6000(portSettingMap.get("comPort")).close();
				// break;

				}

				imageView.setImage(new Image("com/medas/mi/graphics/cross.png"));
				settingBtn.setDisable(isDisable);
				removeBtn.setDisable(isDisable);
				connectBtn.setText("C");

			} catch (IOException e) {
				e.printStackTrace();
			}
		}


	}
	
	/*public  void setVisibility() {
		boolean isDisable = "C".equalsIgnoreCase(connectBtn.getText()) ? true : false;
		if ("C".equalsIgnoreCase(connectBtn.getText())) {
			settingBtn.setDisable(false);
			removeBtn.setDisable(false);
			connectBtn.setDisable(true);

		} else if("D".equalsIgnoreCase(connectBtn.getText())) {

			settingBtn.setDisable(true);
			removeBtn.setDisable(true);
			connectBtn.setDisable(false);
		}
	}*/

}

