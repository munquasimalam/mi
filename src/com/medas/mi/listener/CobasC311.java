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
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import javafx.scene.control.TextArea;

public class CobasC311 implements SerialPortEventListener, Closeable {
	static final Logger logger = Logger.getLogger(CobasC311.class);
	private static final int DEFAULT_TIME_OUT = 2000;
	private static final int DEFAULT_DATA_RATE = 9600;
	private static SerialPort serialPort = null;
	// input stream from port
	private static InputStream inputStream = null;
	// output stream to port
	private static OutputStream outputStream = null;

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
	private TextArea textArea = null;
	static String strDataReceived = "";
	static String strETBString = "";
	static String QPatientID = "";
	static String RPatientID = "";
	static String strSaveString = "";
	static int eot = 0;
	char sendChar = '0';
	long CheckSumValue;
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

	public CobasC311(final String portName) {
		this.portDataRate = DEFAULT_DATA_RATE;
		this.portTimeOut = DEFAULT_TIME_OUT;
		this.portName = portName;
	}

	public CobasC311(final int portDataRate, final int portTimeOut, final String portName) {
		this.portDataRate = portDataRate;
		this.portTimeOut = portTimeOut;
		this.portName = portName;
	}

	public CobasC311(String machineId, String portName, String bitsPerSecond, String dataBits, String parity,
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
		logger.debug("initialize");
		System.setProperty("gnu.io.rxtx.SerialPorts", portName);

		try {
			final CommPortIdentifier commPortIdentifier = CommPortIdentifier.getPortIdentifier(portName);// findCommPortIdentifier();
			// init serial port
			serialPort = (SerialPort) commPortIdentifier.open(this.getClass().getName(), portTimeOut);
			serialPort.setSerialPortParams(portDataRate, dataBits, stopBit, parity);
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
	@Override
	public void serialEvent(final SerialPortEvent serialPortEvent) {
		if (serialPortEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			System.out.println("serialPortEvent.DATA_AVAILABLE" + serialPortEvent.DATA_AVAILABLE);
			byte[] readBuffer = new byte[20];
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
					case (char) 5: // ENQ (enquiry)
						outputStream.write(6);
						break;

					case (char) 2: // STX (start of text)
						strDataReceived = "";
						break;
					case (char) 4: // EOT (end of transmission)
						if (LI_QPatientID != null && LI_QPatientID != "") {
							eot = 1;
							outputStream.write(5);
						}
						break;
					case (char) 23: // ETB (end of trans. block)
						LI_ETBString = strDataReceived.substring(1, strDataReceived.length());
						LI_SaveString = LI_SaveString + LI_ETBString;
						LI_ETBString = "";
						strDataReceived = "";
						outputStream.write(6);
						break;
					case (char) 3: // ETX (end of text)
						outputStream.write(6);
						LI_ETBString = strDataReceived.substring(1, strDataReceived.length());
						LI_SaveString = LI_SaveString + LI_ETBString;
						strDataReceived = "";
						LI_ETBString = "";
						saveData(LI_SaveString);
						LI_SaveString = "";
						break;

					case (char) 21: // NAK (negative acknowledge)
						break;
					case (char) 6:
						// intIsDataArriving = 0;
						if (eot == 1 && LI_QPatientID != null) {
							SendTest(LI_QPatientID);
						}
						break;
					default:
						strDataReceived = charReceived == (char) 13 ? strDataReceived.trim() + "," + charReceived
								: strDataReceived.trim() + charReceived;
						break;
					}
				}

			} catch (Exception e) {
				System.out.println("Event reading error:" + e.getMessage());
				logger.error("Event reading error", e);
			}
		}
	}

	// prevent port locking on platforms like Linux.
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

	public synchronized void saveData(String str) {
		String[] fields_of_o;
		String[] fields = null;
		String[] records;
		String[] components;
		String LIMO_Results = "";
		String equip_param = "";
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
						System.out.println("LIMO_Results:" + LIMO_Results);

						equip_param = components[3].trim().split("\\/")[0];
						System.out.println("equip_param:" + equip_param);

						System.out.println("LIMO_LabID in R:" + LIMO_LabID);
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

						if (fieldsOfQ[12].trim() != "O") {
							// LI_QPatientID = componentOfQ[1];
							LI_QPatientID = componentOfQ[2];
						} else {
							LI_QPatientID = null;
						}
						System.out.println("Query  :" + LI_QPatientID);
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

	public void SendTest(String strPatientid) {
		try {
			List<PatientInfo> patienList = dbUtil.getPatientInfo(machineId, LI_QPatientID);
			System.out.println("barcode :" + patienList.get(0).getBarcode());

			if (patienList.size() > 0) {
				patientInfo(patienList);
				LI_InterfaceStatus = 4;
				LI_DispPatientID = strPatientid;
				String str = "";
				int frameNumber = 0;

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
				str = String.valueOf(frameNumber) + "O|1|" + strPatientid + "|" + componentOfQ[2] + "^"
						+ componentOfQ[3] + "^" + componentOfQ[4] + "^^" + componentOfQ[6] + "^" + componentOfQ[7]
						+ "|";
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
				System.out.println("strOut l:" + str);
				writeString(str, outputStream);
				Thread.sleep(100);
				outputStream.write(4);

				eot = 0;
				str = "";
				LI_QPatientID = null;
			}
		} catch (Exception ex) {
			System.out.println(": SendTest : " + ex.getMessage());

		}
	}

	public void patientInfo(List<PatientInfo> patienList) {
		String patientName = "";
		String patientNumber = "";
		String patientAge = "";
		String patientDOB = "";
		String patientSex = "";
		patientTestList = "";

		try {
			patientName = patienList.get(0).getName();
			patientAge = patienList.get(0).getAge();
			patientSex = patienList.get(0).getSex();
			patientName = patientName.replace(".", " ");
			if (patientName.trim().length() > 20) {
				patientName = patientName.substring(0, 20);
			}

			for (int i = 0; i < patienList.size(); i++) {
				patientTestList = patientTestList + "^^^" + patienList.get(i).getPatientId() + "@" + "^0\\";
			}
			if (patientTestList.length() > 0) {
				patientTestList = patientTestList.substring(0, patientTestList.length() - 1);
			}
		} catch (Exception excp) {
			System.out.println(": PatientInfo : " + excp.getMessage());

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
			System.out.println(" hex.substring(hex.length()-2):" + hex.substring(hex.length() - 2));

		} catch (Exception ex) {
			System.out.println("Exception:" + ex.getMessage());

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
