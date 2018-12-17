package com.medas.mi.listener;

import com.medas.mi.utils.dbUtil;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.TooManyListenersException;
import org.apache.log4j.Logger;

public class Clinitek implements SerialPortEventListener, Closeable {
  static final Logger logger = Logger.getLogger(Clinitek.class);
  private static final int DEFAULT_TIME_OUT = 2000;
  private SerialPort serialPort;
  private InputStream inputStream;
  private OutputStream outputStream;

  // bits per second for port
  private int portDataRate = 0;
  // milliseconds to block while waiting for port open
  private final int portTimeOut;
  // port name
  private String portName = null;

  // region "Variable Declaration"
  static String dataReceivedStr = null;
  public int interfaceStatus;
  public static String etbStr;
  public static String dataSaveStr;
  String labId = null;
  public boolean isConnected = false;
  int machineId = 0;
  public boolean isConnected() {
		return isConnected;
	}
  // end region

  public Clinitek(Map<String, String> portSettingMap) {
    this.portDataRate = Integer.parseInt(portSettingMap.get("bitsPerSecond").toString());
    this.portTimeOut = DEFAULT_TIME_OUT;
    this.portName = portSettingMap.get("comPort").toString();
    this.machineId = Integer.parseInt(portSettingMap.get("machineId").toString());
    initialize(portSettingMap);
  }

  public Clinitek() {
    this.portTimeOut = DEFAULT_TIME_OUT;

  }

  /**
   * It used to open and initialize the serial port.
 * @param portSettingMap it contains all the properties of serial port.
 */
  public void initialize(Map<String, String> portSettingMap) {
    logger.info("initialize method  called with Parameters:" + portSettingMap);
    try {
      final CommPortIdentifier commPortIdentifier = CommPortIdentifier.getPortIdentifier(portName);
      // init serial port
      serialPort = (SerialPort) commPortIdentifier.open(this.getClass().getName(), portTimeOut);
      serialPort.setSerialPortParams(portDataRate, SerialPort.DATABITS_8,
          SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
      inputStream = serialPort.getInputStream();
      outputStream = serialPort.getOutputStream();
      // add event listeners
      serialPort.addEventListener(this);
      serialPort.notifyOnDataAvailable(true);
      isConnected = true;
    } catch (PortInUseException portInUseEx) {
        System.out.println("PortInUseException:" + portInUseEx.getMessage());
        logger.error("PortInUseException:" + portInUseEx.getMessage());
        
      } catch (UnsupportedCommOperationException unsupportedCommOperationEx) {
        System.out.println("UnsupportedCommOperationException:" + unsupportedCommOperationEx.getMessage());
        logger.error("UnsupportedCommOperationException:" + unsupportedCommOperationEx.getMessage());
        
      } catch (TooManyListenersException tooManyListenersEx) {
        System.out.println("TooManyListenersException:" + tooManyListenersEx.getMessage());
        logger.error("TooManyListenersException:" + tooManyListenersEx.getMessage());

      } catch (IOException ioEx) {
        System.out.println("IOException:" + ioEx.getMessage());
        logger.error("IOException:" + ioEx.getMessage());
        
      } catch (NoSuchPortException noSuchPortEx) {
        System.out.println("NoSuchPortException:" + noSuchPortEx.getMessage());
        logger.error("NoSuchPortException:" + noSuchPortEx.getMessage());
      }
    }

  // handle an event on the serial port
  /* (non-Javadoc)
  * @see gnu.io.SerialPortEventListener#serialEvent(gnu.io.SerialPortEvent)
  */
  @Override
  public synchronized void serialEvent(final SerialPortEvent serialPortEvent) {
    if (serialPortEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
      // we get here if data has been received
      byte[] readBuffer = new byte[20];
      try {
        // read data
        if (inputStream.available() > 0) {
          inputStream.read(readBuffer);
        }
        // print data
        String strReceived = new String(readBuffer);
        for (int i = 0; i < strReceived.length() - 1; i++) {
          char charReceived = strReceived.charAt(i);
          switch (charReceived) {
            case (char) 5: // ENQ (enquiry)
              // System.out.println("charReceived 5:" + charReceived);
              outputStream.write(6);
              break;

            case (char) 2: // STX (start of text)
              dataReceivedStr = "";
              break;
            case (char) 23: // ETB (end of trans. block)
              etbStr = dataReceivedStr.substring(1, dataReceivedStr.length() - 1);
              dataSaveStr = dataSaveStr + etbStr;
              etbStr = "";
              dataReceivedStr = "";
              outputStream.write(6);
              break;
            case (char) 3: // ETX (end of text)
              outputStream.write(6);
              dataSaveStr = dataSaveStr + etbStr;
              dataReceivedStr = "";
              etbStr = "";
              saveData(dataSaveStr);
              dataSaveStr = "";
              break;

            case (char) 21: // NAK (negative acknowledge)
              outputStream.write(5);
              break;
            case (char) 6:
              break;
            default:
              dataReceivedStr = dataReceivedStr.trim() + charReceived;
              break;

          }
        }

      } catch (Exception ex) {
        System.out.println(ex.getMessage());
        logger.error("serialEvent():" + ex.getMessage());
      }
    }

  }

  // This should be called when you stop using the port.
  // prevent port locking on platforms like Linux.

  @Override
  public synchronized void close() throws IOException {
    if (serialPort != null) {
      serialPort.removeEventListener();
      serialPort.close();
      System.out.println("******************serialPort.close():");
    }
    if (inputStream != null) {
      inputStream.close();
    }
  }

  /**This method is used to save data in database which are comming from the Instrument.
  * @param str String is passed as a parameter.
  */
  public synchronized void saveData(String str) {
    String[] orderFields = null;
    String[] fields = null;
    String[] records = null;
    String[] components = null;
    String results = null;
    String alarm;
    String units;
    String equipParam = null;
    try {
      records = str.split(String.valueOf((char) 13)); // 13> CR (carriage return)
      for (int j = 0; j <= records.length - 1; j++) {
        if (records[j].length() > 1) {
          String recChr = records[j].substring(0, 1);
          switch (recChr) {
            case "H":
              interfaceStatus = 1;
              // DateTime.Now.ToString("yyyyMMddHHmmss");
              break;
            case "R":
              fields = records[j].split("\\|");
              System.out.println("fields:" + fields);
              components = fields[2].split("\\^");
              results = fields[3].trim();
              System.out.println("LIMO_Results:" + results);
              equipParam = components[3].trim();
              System.out.println("equip_param:" + equipParam);
              alarm = "";
              units = fields[4].toString().trim();
              alarm = results.trim();
              System.out.println("LIMO_LabID in R:" + labId);
              if (labId != null && !"".equals(labId)) {
                dbUtil.insertRecordInEquipResults(machineId,labId.trim(), equipParam, results);
                equipParam = null;
                results = null;
              }

              break;
            case "O":
              interfaceStatus = 3;
              orderFields = records[j].split("\\|");
              labId = orderFields[2].trim();
              break;
            case "P":
              break;
            case "L":
              interfaceStatus = 6;
              break;
            default:
              break;
          };
        }
      }
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      logger.error("saveData():" + ex.getMessage());
    }
  }

}
