package com.medas.mi.utils;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;



public class XmlUtil {
	
	 public static Map<String,String> readXmlFile(String path) {
		  Map<String,String> settings = new HashMap<String,String>();
		  try {
	           //File fXmlFile = new File("settings/CobasB221.xml");
			  File fXmlFile = new File(path);
			  System.out.println(" path:" +path);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fXmlFile);
						
				//optional, but recommended
				//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
				doc.getDocumentElement().normalize();

				System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
						
				NodeList nList = doc.getElementsByTagName("setting");
						
				System.out.println("----------------------------nList.getLength():" +nList.getLength());

				for (int temp = 0; temp < nList.getLength(); temp++) {

					Node nNode = nList.item(temp);
							
					System.out.println("\nCurrent Element :" + nNode.getNodeName());
					Element eElement = (Element) nNode;
					System.out.println("************eElement1:" +eElement.getElementsByTagName("setting").getLength());
							
					if (nNode.getNodeType() == Node.ELEMENT_NODE ) {

						//Element eElement = (Element) nNode;
						System.out.println("************eElement2:" +eElement.getElementsByTagName("setting"));

						//System.out.println("setting id : " + eElement.getAttribute("id"));
						//System.out.println("machineName : " + eElement.getElementsByTagName("machineName").item(0).getTextContent());
						System.out.println("machineName : " + eElement.getElementsByTagName("machineName").item(0).getFirstChild().getNodeValue());
							
						System.out.println("comPort : " + eElement.getElementsByTagName("comPort").item(0).getTextContent());
						System.out.println("bitsPerSecond : " + eElement.getElementsByTagName("bitsPerSecond").item(0).getTextContent());
						System.out.println("parity : " + eElement.getElementsByTagName("parity").item(0).getTextContent());
						System.out.println("stopBit : " + eElement.getElementsByTagName("stopBit").item(0).getTextContent());
						System.out.println("machineId : " + eElement.getElementsByTagName("machineId").item(0).getTextContent());
						System.out.println("dataBits : " + eElement.getElementsByTagName("dataBits").item(0).getTextContent());
						
						
						settings.put("machineName", eElement.getElementsByTagName("machineName").item(0).getTextContent());
						settings.put("machineId", eElement.getElementsByTagName("machineId").item(0).getTextContent());
						settings.put("comPort", eElement.getElementsByTagName("comPort").item(0).getTextContent());
						settings.put("bitsPerSecond", eElement.getElementsByTagName("bitsPerSecond").item(0).getTextContent());
						settings.put("parity", eElement.getElementsByTagName("parity").item(0).getTextContent());
						settings.put("stopBit", eElement.getElementsByTagName("stopBit").item(0).getTextContent());
						settings.put("dataBits", eElement.getElementsByTagName("dataBits").item(0).getTextContent());
					}
				}
			    } catch (Exception e) {
				e.printStackTrace();
			    }
		  
		  return settings;

	  }
	 
	 public static void writeXmlFile(Map<String,String> settingsMap) {
		 try {

			    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			    // root elements
			    Document doc = docBuilder.newDocument();
			    Element rootElement = doc.createElement("settings");
			    doc.appendChild(rootElement);

			   
			    
			    // staff elements
			    Element setting = doc.createElement("setting");
			    rootElement.appendChild(setting);

			    // set attribute to staff element
			   // Attr attr = doc.createAttribute("id");
			   // attr.setValue("1");
			   // setting.setAttributeNode(attr);

			    // shorten way
			    // staff.setAttribute("id", "1");
			     
			    
			    for(Entry<String, String> entry : settingsMap.entrySet()) {
			    	 
				   System.out.println("key:" +entry.getKey() + "  Value : " +entry.getValue());
				   Element element = doc.createElement(entry.getKey());
				   element.appendChild(doc.createTextNode(entry.getValue()));
				    setting.appendChild(element);
//				    if(settingsMap.size() > 7 && settingsMap.size() <15 ) {
//				    	  Element setting2 = doc.createElement("setting");
//						    rootElement.appendChild(setting2);
//						    System.out.println("key:" +entry.getKey() + "  Value : " +entry.getValue());
//							   Element element2 = doc.createElement(entry.getKey());
//							   element2.appendChild(doc.createTextNode(entry.getValue()));
//						    
//				    }

			    }

//			    // machineName elements
//			    Element machineName = doc.createElement("machineName");
//			    machineName.appendChild(doc.createTextNode("m1"));
//			    setting.appendChild(machineName);
//
//			    // comPort elements
//			    Element comPort = doc.createElement("comPort");
//			    comPort.appendChild(doc.createTextNode("COM4"));
//			    setting.appendChild(comPort);
//
//			    // bautRate elements
//			    Element bautRate = doc.createElement("bautRate");
//			    bautRate.appendChild(doc.createTextNode("9600"));
//			    setting.appendChild(bautRate);
//
//			    // parity elements
//			    Element parity = doc.createElement("parity");
//			    parity.appendChild(doc.createTextNode("None"));
//			    setting.appendChild(parity);
//			    
//			    // stopBit elements
//			    Element stopBit = doc.createElement("stopBit");
//			    stopBit.appendChild(doc.createTextNode("One"));
//			    setting.appendChild(stopBit);
//			    
//			    

			    // write the content into xml file
			    TransformerFactory transformerFactory = TransformerFactory.newInstance();
			    Transformer transformer = transformerFactory.newTransformer();
			    DOMSource source = new DOMSource(doc);
			    StreamResult result = new StreamResult(new File("settings/" + settingsMap.get("machineName") + ".xml"));
			    System.out.println("path:settings/" + settingsMap.get("machineName") + ".xml");

			    // Output to console for testing
			    // StreamResult result = new StreamResult(System.out);

			    transformer.transform(source, result);

			    System.out.println("File saved!");

			  } catch (ParserConfigurationException pce) {
			    pce.printStackTrace();
			  } catch (TransformerException tfe) {
			    tfe.printStackTrace();
			  }
	 }
	 
	 public static  List<File> readFiles() {
		 List<File> filesInFolder = null;

			try {
				filesInFolder = Files.walk(Paths.get("settings")).filter(Files::isRegularFile).map(Path::toFile)
						.collect(Collectors.toList());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return filesInFolder;
	 }

}
