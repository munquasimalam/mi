package com.medas.mi;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;

import java.util.ArrayList;

import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;

  public class CommPortIdentifierFinder {
    static final Logger logger = Logger.getLogger(MainControls.class);
   static int  count = -1;
   private static List<String> portList = new ArrayList<String>();
  
    //CommPortIdentifier commPortIdentifier = null;
    //CommPortIdentifier comPortId = null;

	public List<String> findMachinePorts() throws PortNotFoundException, NoSuchPortException {
		count++;
		if(count == 0) {
			final Enumeration commPortEnumerator = CommPortIdentifier.getPortIdentifiers();
			
			// portList = new ArrayList<String>();
	        while (commPortEnumerator.hasMoreElements()) {
			  final CommPortIdentifier currentCommPortIdentifier = (CommPortIdentifier) commPortEnumerator.nextElement();
	          portList.add(currentCommPortIdentifier.getName());
			 // commPortIdentifier = currentCommPortIdentifier;
			}
		}
	
		if (portList.size() == 0) {
			throw new PortNotFoundException("Could not find COM port.");
		}

		return portList;
	}
}