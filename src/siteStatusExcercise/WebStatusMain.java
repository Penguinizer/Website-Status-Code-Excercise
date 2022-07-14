/**
 * @author antti
 *
 * The main file for the website status coding excercise.
 * Contains the main loop which in turn invokes functions from
 * separate classes to parse the configuration and poll sites.
 * The main loop also handles user inputs, although in the current
 * simple state this is simply a way to gracefully quit.
 */

package siteStatusExcercise;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Timer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;



public class WebStatusMain {
	protected static int pollingRate; //Note: in seconds
	protected static List<Entry<String, String>> siteMap = new ArrayList<Entry<String,String>>();
	protected static Timer pollTimer;
	protected static PollingTask pollTask;
	
	//The main function which calls the xml parser and polling timertask.
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		//Initialize a class object so that the parseXML function can simply access those without
		// requiring the use of returning a pair containing the polling period and site/requirement list.
		WebStatusMain myMain = new WebStatusMain();
		//Calling the parseXML function in order to read the file.
		System.out.println("Calling xml file parsing function.");
		myMain.parseXML();
		
		//After populating the list creating the Timer and TimerTasks.
		System.out.print("Creating Timer and Timed Task to poll sites from file.\n"
				+ "Please enter name of log file: ");
		String logName = readInput();
		
		//Create the timer and TimerTask.
		//The TimerTask is executed based on the timer.
		//Pass the logName input to the task while initializing.
		pollTimer = new Timer();
		pollTask = new PollingTask(logName, siteMap);
		
		//Use the timer to schedule the timertask.
		//Multiply Pollingrate by a thousand as the delay is in milliseconds.
		pollTimer.scheduleAtFixedRate(pollTask, new Date(),pollingRate*1000);
		
		//Enter a menu function which allows the user to gracefully exit.
		//Other functionality could also be added if wanted.
		myMain.menu();
	}
	
	//Utility functions
	//The parseXML function parses the XML configuration file.
	//This required some referencing of Java docs as well as a write-up on the DOM parser as I haven't worked with it before.
	protected void parseXML() throws ParserConfigurationException, SAXException, IOException {
		//Allow the user to either use the default file or to enter the name of their own XML file.
		int tmpint = 0;
		String fileName;
		do {
			System.out.print("Enter 1 in order to use the default XML configuration file (testsites.xml)"
					+ " or 2 to enter a custom file name:  ");
			try {
				tmpint = Integer.parseInt(readInput());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (tmpint == 1) {
				fileName = "testsites.xml";
				break;
			}
			else if (tmpint == 2) {
				fileName = readInput();
				break;
			}
			else {
				System.out.println("Please enter a valid value.");
			}
		} while(true);
		
		//First a DocumentBuilderFactory instance has to be created in order to get a new DocumentBuilder.
		DocumentBuilderFactory docBuildFac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuildFac.newDocumentBuilder();
		//Use the DocumentBuilder to parse the XML, normalize and get root.
		Document xmldoc = docBuilder.parse(new File(fileName));
		xmldoc.getDocumentElement().normalize();
		Element xmlRoot = xmldoc.getDocumentElement();
		
		//Parse the polling rate from the configuration file.
		pollingRate = Integer.parseInt(xmlRoot.getElementsByTagName("pollingrate").item(0).getTextContent());
		//System.out.println("Polling rate: " + pollingRate);
		
		//Get the list of site nodes from the root, create temporary variables for iterating.
		NodeList nodes = xmlRoot.getElementsByTagName("site");
		NodeList tmpChildren;
		Node tmpNode;
		String tmpAddress = "test";
		String tmpReq = "test";
		
		//Use nested loops to iterate through the list of sites and to iterate through the child
		// entries of those site entries in order to get the site address and requirements.
		for(int itint = 0; itint < nodes.getLength(); itint++) {
			//Get the site entry from the NodeList.
			tmpNode = nodes.item(itint);
			//Get the site entry's child nodes.
			tmpChildren = tmpNode.getChildNodes();
			//Iterate through entry's children.
			for(int secit = 0; secit < tmpChildren.getLength(); secit++) {
				tmpNode = tmpChildren.item(secit);
				//Check that the children contain the address and requirements.
				//Get the text contents if they do and save in temp variables.
				if (tmpNode.getNodeName().equals("address")) {
					tmpAddress = tmpNode.getTextContent();
				} 
				else if (tmpNode.getNodeName().equals("requirement")) {
					tmpReq = tmpNode.getTextContent();
				}
			}
			//Use Put to append the address and requirement to the list of entries.
			siteMap.add(new SimpleEntry<String, String>(tmpAddress, tmpReq));
		}
	}
	
	//A function which handles the menu functionality.
	//Called by the main function after the timer and timertasks are set up and started.
	private void menu() {
		String userAction;
		//A simple do-while menu which waits for the user to input.
		//Presently only has a graceful quit option which terminates the timed task and timer before allowing execution to end by returning.
		do {
			System.out.println("Input \"quit\" to close the program.");
			try {
				userAction = readInput();
				if (userAction.equals("quit")) {
					System.out.println("Exiting program.");
					pollTask.cancel();
					pollTimer.cancel();
					return;
				}
				else {
					continue;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}while(true);
	}
	
	//A function for reading the user's input.
	//Uses a buffered reader. It reads a line into the string variable until 
	// the variable returns something usable at which point the value is returned.
	private static String readInput() {
		BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
		String s=null;
		do{
			try {
				s = stdin.readLine();
			} catch (IOException e) {
				System.out.println(e);
			}
		} while((s==null)||(s.length()==0));
		return s;
	}
}
