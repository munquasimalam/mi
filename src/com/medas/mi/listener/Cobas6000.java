package com.medas.mi.listener;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.TooManyListenersException;

import org.apache.log4j.Logger;

import com.medas.mi.SerialPortControls;
import com.medas.mi.model.PatientInfo;
import com.medas.mi.utils.AlertUtil;
import com.medas.mi.utils.dbUtil;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import javafx.scene.control.TextArea;

public class Cobas6000 implements SerialPortEventListener, Closeable {
	static final Logger logger = Logger.getLogger(Cobas6000.class);
	private static final int DEFAULT_TIME_OUT = 2000;
	private static SerialPort serialPort = null;
	// input stream from port
	public static InputStream inputStream = null;
	// output stream to port
	public static OutputStream outputStream = null;

	// bits per second for port OR Baud rate
	private int portDataRate;
	// milliseconds to block while waiting for port open
	private final int portTimeOut;
	// port name
	private final String portName;
	private int machineId = 0;
	private int dataBits;
	private int parity;
	private int stopBit;
	private String isPrintLog;
	private boolean isConnected = false;
	// text area to display data comming from machine.
	private TextArea textArea = null;
	static String strDataReceived = "";
	static String strETBString = "";
	static String QPatientID = "";
	static String RPatientID = "";
	static String strSaveString = "";
	static int eot = 0;
	char sendChar = '0';
	int CheckSumValue;
	int intStatus = 0;
	String strDispPatientid = "";

	public static String LI_QPatientID = "";
	public int LI_InterfaceStatus = 0;
	public String LI_DispPatientID = "";
	public static String LI_DataReceived = "";
	public static String LI_ETBString = "";
	public long LI_CheckSumValue;
	public static String LI_SaveString = "";
	public static String LI_strDataReceived = "";
	public static String LI_ResultPatientID = "";
	public static int LI_Eot;
	public char LI_SendChar;
	String[] componentOfQ = null;
	String patientTestList = "";
	String LIMO_LabID = "";

	public boolean isConnected() {
		return isConnected;
	}

	public Cobas6000(String portName) {
		this.portTimeOut = DEFAULT_TIME_OUT;
		this.portName = portName;
	}

	public Cobas6000(final int portDataRate, final int portTimeOut, final String portName) {
		this.portDataRate = portDataRate;
		this.portTimeOut = portTimeOut;
		this.portName = portName;
	}

	public Cobas6000(String machineId, String portName, String bitsPerSecond, String dataBits, String parity,
			String stopBit, String isPrintLog, TextArea textArea) {
		logger.info("machineId:" + machineId + "portName:" + portName + "bitsPerSecond:" + bitsPerSecond + "dataBits:"
				+ dataBits + "parity:" + parity + "stopBit:" + stopBit + "isPrintLog:" + isPrintLog);
		this.portDataRate = Integer.parseInt(bitsPerSecond);
		this.portTimeOut = DEFAULT_TIME_OUT;
		this.machineId = Integer.parseInt(machineId);
		this.portName = portName;
		this.dataBits = Integer.parseInt(dataBits);
		this.parity = Integer.parseInt(parity);
		this.stopBit = Integer.parseInt(stopBit);
		this.isPrintLog = isPrintLog;
		this.textArea = textArea;
	}

	public void initialize() {
		logger.info("initialize method:");
		// System.setProperty("gnu.io.rxtx.SerialPorts", portName);
		try {
			final CommPortIdentifier commPortIdentifier = CommPortIdentifier.getPortIdentifier(portName);// findCommPortIdentifier();
			// init serial port
			serialPort = (SerialPort) commPortIdentifier.open(this.getClass().getName(), portTimeOut);
			serialPort.setSerialPortParams(portDataRate, dataBits, stopBit, parity);
			logger.info("initialize serialPort:" + serialPort);
			System.out.println("initialize serialPort:" + serialPort);
			// init input and output stream
			inputStream = serialPort.getInputStream();
			outputStream = serialPort.getOutputStream();
			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
			isConnected = true;

		} catch (PortInUseException | UnsupportedCommOperationException | TooManyListenersException e) {
			AlertUtil.portInUseAlert(portName);
			logger.error("RXTX error", e);
		} catch (IOException e) {
			logger.error("IO error", e);
		} catch (NoSuchPortException e) {
			logger.error("Port error", e);
		}
	}

	// handle an event on the serial port
	// Read data from serial port
	@Override
	public void serialEvent(final SerialPortEvent serialPortEvent) {
		if (serialPortEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			byte[] readBuffer = new byte[40];
			try {
				// read data
				if (inputStream.available() > 0) {
					inputStream.read(readBuffer);
				}
				String strReceived = new String(readBuffer);
				SerialPortControls.showReceivedData(textArea, strReceived);
				for (int i = 0; i < strReceived.length(); i++) {
					char charReceived = strReceived.charAt(i);
					switch (charReceived) {
					case (char) 2: // STX (start of text)
						strDataReceived = "";
						break;
					case (char) 5: // ENQ (enquiry)
						outputStream.write(6);
						break;
					case (char) 4: // EOT (end of transmission)
						if (LI_QPatientID != null && LI_QPatientID != "") {
							eot = 1;
							outputStream.write(5);
						}
						break;
					case (char) 23: // ETB (end of trans. block > end of text of
									// end entermediate frame)
						LI_ETBString = strDataReceived.substring(1, strDataReceived.length());
						LI_SaveString = LI_SaveString + LI_ETBString;
						LI_ETBString = "";
						strDataReceived = "";
						outputStream.write(6);
						break;
					case (char) 3: // ETX (end of text > of end frame)
						outputStream.write(6);
						LI_ETBString = strDataReceived.substring(1, strDataReceived.length());
						LI_SaveString = LI_SaveString + LI_ETBString;
						strDataReceived = "";
						LI_ETBString = "";
						saveData(LI_SaveString);
						LI_SaveString = "";
						break;
					case (char) 6:
						if (eot == 1 && LI_QPatientID != null) {
							SendTest(LI_QPatientID, outputStream);
						}
						break;
					default:
						strDataReceived = charReceived == (char) 13 ? strDataReceived.trim() + "," + charReceived
								: strDataReceived.trim() + charReceived;
						// if (charReceived == (char) 13) {
						// strDataReceived = strDataReceived.trim() + "," +
						// charReceived;
						// } else {
						// strDataReceived = strDataReceived.trim() +
						// charReceived;
						// }
						break;
					}
				}

			} catch (Exception e) {
				System.out.println("Event reading error:" + e.getMessage());
				logger.error("Event reading error", e);
			}
		}

	}

	// prevent port locking on platforms like window, Linux.
	@Override
	public void close() throws IOException {
		try {
			if (serialPort != null) {
				serialPort.removeEventListener();
				serialPort.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
		} catch (Exception ex) {
			System.out.println("close error:" + ex.getMessage());
			logger.error("close error:", ex);
		}

	}

	/**
	 * used to save record in db in case of test Result used to take barcode
	 * from Q record in case of test Request
	 * 
	 * @param str
	 *            represents records
	 * 
	 */
	public synchronized void saveData(String str) {
		String[] fields_of_o;
		String[] fields = null;
		String[] records;
		String[] components;
		String LIMO_Results = "";
		String equip_param = "";
		String[] paramComponents;
		String[] fieldsOfQ = null;

		try {
			records = str.split(","); // 13> CR (carriage return)
			for (int j = 0; j <= records.length - 1; j++) {
				if (records[j].length() > 1) {
					String recChr = records[j].substring(0, 1);
					switch (recChr) {
					case "H":
						LI_InterfaceStatus = 1;
						break;
					case "R":
						fields = records[j].split("\\|");
						System.out.println("fields:" + fields);
						components = fields[2].split("\\^");
						LIMO_Results = fields[3].trim();
						paramComponents = components[3].trim().split("\\/");
						equip_param = paramComponents[0];
						if (LIMO_LabID != null && !"".equals(LIMO_LabID)) {
							dbUtil.insertRecordInEquipResults(machineId, LIMO_LabID.trim(), equip_param, LIMO_Results);
							equip_param = null;
							LIMO_Results = null;
						}
						break;
					case "O":
						LI_InterfaceStatus = 3;
						fields_of_o = records[j].split("\\|");
						LIMO_LabID = fields_of_o[2].trim();

						break;
					case "P":
						break;
					case "Q":
						// fieldsOfQ = str.split("\\|");
						fieldsOfQ = records[1].split("\\|");
						// componentOfQ = fieldsOfQ[12].split("\\^");
						componentOfQ = fieldsOfQ[2].split("\\^");

						if (fieldsOfQ[11].trim() != "O") {
							// LI_QPatientID = componentOfQ[1];
							LI_QPatientID = componentOfQ[2];
						} else {
							LI_QPatientID = null;
						}
						System.out.println("LI_QPatientID  :" + LI_QPatientID);
						break;
					case "L":
						LI_InterfaceStatus = 6;
						break;
					default:
						break;
					}

				}
			}
		} catch (Exception ex) {
			logger.error("Data saving error", ex);
			System.out.println("Data saving error:" + ex.getMessage());
		}
	}

	/**
	 * used to send test to instrument
	 * 
	 * @param qPatientID
	 *            represent barcode of Q
	 * 
	 * @param outputStream
	 *            to write data
	 * 
	 * 
	 */
	public void SendTest(String qPatientID, OutputStream outputStream) {
		try {
			List<PatientInfo> patienList = dbUtil.getPatientInfo(machineId, qPatientID);
			logger.info("barcode of Q :" + patienList.get(0).getBarcode());
			System.out.println("barcode of Q :" + patienList.get(0).getBarcode());

			if (patienList.size() > 0) {
				patientInfo(patienList);
				LI_InterfaceStatus = 4;
				LI_DispPatientID = qPatientID;
				int frameNumber = 0;
				String str = "";
				frameNumber = (frameNumber + 1) % 8;
				str = String.valueOf(frameNumber) + "H|\\^&||||||||||P||" + String.valueOf(((char) 13))
						+ String.valueOf(((char) 3));
				str = String.valueOf((char) 2) + str + CheckSum(str) + String.valueOf((char) 13)
						+ String.valueOf((char) 10);
				System.out.println("strOut h:" + str);
				writeString(str, outputStream);

				frameNumber = (frameNumber + 1) % 8;
				str = "";
				str = String.valueOf(frameNumber) + "P|1" + String.valueOf(((char) 13)) + String.valueOf(((char) 3));
				str = String.valueOf((char) 2) + str + CheckSum(str) + String.valueOf((char) 13)
						+ String.valueOf((char) 10);
				System.out.println("strOut p:" + str);
				writeString(str, outputStream);
				frameNumber = (frameNumber + 1) % 8;
				str = "";
				str = String.valueOf(frameNumber) + "O|1|" + qPatientID + "|" + componentOfQ[2] + "^" + componentOfQ[3]
						+ "^" + componentOfQ[4] + "^^" + componentOfQ[6] + "^" + componentOfQ[7] + "|";
				str = str + patientTestList;
				str = str + "|R||||||N||||||||||||||O" + String.valueOf(((char) 13)) + String.valueOf(((char) 3));
				str = String.valueOf((char) 2) + str + CheckSum(str) + String.valueOf((char) 13)
						+ String.valueOf((char) 10);
				System.out.println("strOut o:" + str);
				writeString(str, outputStream);
				frameNumber = (frameNumber + 1) % 8;
				str = "";
				str = String.valueOf(frameNumber) + "L|1|N" + String.valueOf(((char) 13)) + String.valueOf(((char) 3));
				str = String.valueOf((char) 2) + str + CheckSum(str) + String.valueOf((char) 13)
						+ String.valueOf((char) 10);
				System.out.println("strOut1234 l:" + str);
				writeString(str, outputStream);
				Thread.sleep(100);
				outputStream.write(4);
				eot = 0;
				str = "";
				LI_QPatientID = null;
			}
		} catch (Exception ex) {
			logger.error("SendTest error:", ex);
			System.out.println(" SendTest : " + ex.getMessage());

		}
	}

	/**
	 * @param patienList
	 *            contains patient details
	 */
	public void patientInfo(List<PatientInfo> patienList) {
		patientTestList = "";
		try {
			String patientName = patienList.get(0).getName();
			String patientAge = patienList.get(0).getAge();
			String patientSex = patienList.get(0).getSex();
			patientName = patientName.replace(".", " ");
			if (patientName.trim().length() > 20) {
				patientName = patientName.substring(0, 20);
			}

			for (int i = 0; i < patienList.size(); i++) {
				// strTestList = strTestList + "^^^" +
				// dtTestReq.Rows[i]["MachineTestID"].ToString() +@"^0\";
				patientTestList = patientTestList + "^^^" + patienList.get(i).getPatientId() + "^0\\";
			}
			if (patientTestList.length() > 0) {
				patientTestList = patientTestList.substring(0, patientTestList.length() - 1);
			}

		} catch (Exception ex) {
			logger.error("PatientInfo error:", ex);
			System.out.println(": PatientInfo : " + ex.getMessage());

		}
	}

	public String CheckSum(String str) {
		int x = 0;
		String hex = "";
		try {
			for (int i = 0; i < str.length(); i++) {
				int st = str.charAt(i);
				x = x + st;
			}
			hex = Integer.toHexString(x);
		} catch (Exception ex) {
			logger.error("CheckSum error:", ex);
			System.out.println("CheckSum error:" + ex.getMessage());

		}

		return hex.substring(hex.length() - 2).toUpperCase();
	}

	private int asciiValue(char ch) {
		int asciiValue = ch;
		return asciiValue;
	}

	private void writeString(String str, OutputStream outputStream) {
		try {
			for (int k = 0; k < str.length(); k++) {
				outputStream.write(asciiValue(str.charAt(k)));
				Thread.sleep(50);
			}
		} catch (InterruptedException ex) {
			logger.error("writeString InterruptedException:", ex);
			System.out.println("writeString InterruptedException:" + ex.getMessage());
		} catch (IOException ex) {
			logger.error("writeString error:", ex);
			System.out.println("writeString error:" + ex.getMessage());
		}
	}

}
