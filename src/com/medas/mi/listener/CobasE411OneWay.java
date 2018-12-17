package com.medas.mi.listener;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

import org.apache.log4j.Logger;

import com.medas.mi.SerialPortControls;
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

public class CobasE411OneWay implements SerialPortEventListener, Closeable {
	static final Logger logger = Logger.getLogger(CobasE411OneWay.class);
	private static final int DEFAULT_TIME_OUT = 2000;
	private static final int DEFAULT_DATA_RATE = 9600;
	//
	private static SerialPort serialPort = null;
	// input stream from port
	// private BufferedReader input;

	private static InputStream inputStream = null;
	private static OutputStream outputStream = null;

	// bits per second for port OR Baud rate
	private  int portDataRate;
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
	
	
	

	 //region "Variable Declaration"
	
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
   public long LI_CheckSumValue ;
   public static String LI_SaveString = "";
   public static String LI_strDataReceived = "";
   public static String LI_ResultPatientID = "";
   public static int LI_Eot ;
   public char LI_SendChar;
   
  /* String  equip_param=null;
   String  LIMO_Results=null;
   */
   String  LIMO_LabID = "";
  
   
   
  //end  region
	    

	public boolean isConnected() {
	return isConnected;
}



	public CobasE411OneWay(final String portName) {
		this.portDataRate = DEFAULT_DATA_RATE;
		this.portTimeOut = DEFAULT_TIME_OUT;
		this.portName = portName;
	}

	public CobasE411OneWay(final int portDataRate, final int portTimeOut, final String portName) {
		this.portDataRate = portDataRate;
		this.portTimeOut = portTimeOut;
		this.portName = portName;
	}

	public CobasE411OneWay(String machineId, String portName, String bitsPerSecond, String dataBits, String parity,
			String stopBit, String isPrintLog,TextArea textArea) {
		this.portDataRate =  Integer.parseInt(bitsPerSecond);
		this.portTimeOut = DEFAULT_TIME_OUT;
		this.machineId = Integer.parseInt(machineId);
		this.portName = portName;
		this.dataBits = Integer.parseInt(dataBits);
		this.parity = Integer.parseInt(parity);
		this.stopBit = Integer.parseInt(stopBit);
		this.isPrintLog = isPrintLog;
		this.textArea = textArea;
		
		
		System.out.println("machineId:"+machineId);
		System.out.println("portName:"+portName);
		System.out.println("bitsPerSecond:"+bitsPerSecond);
		System.out.println("dataBits:"+dataBits);
		System.out.println("parity:"+parity);
		System.out.println("stopBit:"+stopBit);
		System.out.println("isPrintLog:"+isPrintLog);
		
	}

	public void initialize() {
		 logger.debug("initialize");
		 System.setProperty("gnu.io.rxtx.SerialPorts", portName);

		try {
			final CommPortIdentifier commPortIdentifier = CommPortIdentifier.getPortIdentifier(portName);// findCommPortIdentifier();

			// init serial port
			serialPort = (SerialPort) commPortIdentifier.open(this.getClass().getName(), portTimeOut);
			//serialPort.setSerialPortParams(portDataRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
			serialPort.setSerialPortParams(portDataRate, dataBits, stopBit,parity);


			// init input
			// input = new BufferedReader(new
			// InputStreamReader(serialPort.getInputStream()));
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
	public synchronized void serialEvent(final SerialPortEvent serialPortEvent) {
		if (serialPortEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			System.out.println("serialPortEvent.DATA_AVAILABLE" + serialPortEvent.DATA_AVAILABLE);
			// we get here if data has been received
			byte[] readBuffer = new byte[20];
			try {
				// read data
				if (inputStream.available() > 0) {
              	 inputStream.read(readBuffer);
					//System.out.println("numBytes:" + numBytes);
				}
				// print data
				String strReceived = new String(readBuffer);
				SerialPortControls.showReceivedData(textArea,strReceived);
				System.out.println("*************strReceived:" + strReceived);
					for (int i = 0; i < strReceived.length(); i++) {
					char charReceived = strReceived.charAt(i);
					switch (charReceived) {
						case (char) 5: // ENQ (enquiry)
						//System.out.println("charReceived 5:" + charReceived);
						outputStream.write(6);
						break;
						
					case (char) 2: // STX (start of text)
						strDataReceived = "";
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
						LI_ETBString= "";
						saveData(LI_SaveString);
						LI_SaveString = "";
						break;

					case (char) 21: // NAK (negative acknowledge)
						//outputStream.write(5);		
					    break;
					case (char) 6:
						break;
					default:
						if(charReceived == (char) 13) {
							strDataReceived = strDataReceived.trim() + "," + charReceived;
							} else {
							strDataReceived = strDataReceived.trim() + charReceived;
						}
						
						break;

					}
				}

				
					//logger.info(inputLine);
			} catch (Exception e) {
				 System.out.println("Event reading error:" + e.getMessage()); 
				 logger.error("Event reading error", e);
			}
		}
		

	}

	// prevent port locking on platforms like Linux.
	@Override
	public synchronized void close() throws IOException {
		System.out.println("close serialPort:" + serialPort);
		System.out.println("close inputStream:" + inputStream);
		System.out.println("close outputStream:" + outputStream);
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
		System.out.println("close serialPort 444:" + serialPort);
	}
	
	
	public synchronized void saveData(String str)
    {
       // String[] parray = new String[10];
		  String[] fields_of_o;
        
       // String[] warray = new String[10];
        String[] fields = null;
        //String[] sarray = new String[10];
        String[] records;
        //String[] tarray = new String[10];
        String[] components;
        String  LIMO_Results = "";
        String  equip_param= "";
      
        
        try
        {
        	System.out.println("************ str" +str);
           records = str.split(","); // 13>  CR  (carriage return) 
             for (int j = 0; j <= records.length - 1; j++)
            {
            	System.out.println("*************records:" + records[j]);
                if (records[j].length() > 1)
                {
                    String recChr = records[j].substring(0, 1);
                    switch (recChr)
                    {
                        case "H":
                            LI_InterfaceStatus = 1;
                            //objMachineResult.LIMO_LabID = DateTime.Now.ToString("yyyyMMddHHmmss");
                            break;
                        case "R":
                          	fields = records[j].split("\\|");
                        	System.out.println("fields:"+fields);
                        	components = fields[2].split("\\^");
                        	  LIMO_Results = fields[3].trim();
                              System.out.println("LIMO_Results:"+LIMO_Results);
                           
                        	equip_param = components[3].trim().split("\\/")[0];
                            System.out.println("equip_param:"+equip_param);
                            System.out.println("LIMO_LabID in R:"+LIMO_LabID);
                            if(LIMO_LabID!=null && !"".equals(LIMO_LabID)){
                            	dbUtil.insertRecordInEquipResults(machineId,LIMO_LabID.trim(), equip_param, LIMO_Results);
                            	equip_param=null;
                            	LIMO_Results=null;
                            }
                            
						   break;
                        case "O":
                            LI_InterfaceStatus = 3;
                            fields_of_o = records[j].split("\\|");
                             LIMO_LabID = fields_of_o[2].trim();
                         
                            break;
                        case "P":
                            break;
                        case "L":
                            LI_InterfaceStatus = 6;
                            break;
                        default:
                            break;
                    }; 
                }
            }
        }
        catch (Exception ex)
        {
        	 logger.error("Data saving error", ex);
          System.out.println("Data saving error:" + ex.getMessage()); 
        }
    }
    

}
