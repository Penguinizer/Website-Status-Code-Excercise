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
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;



public class WebStatusMain {
	protected int pollingRate; //Note: in seconds
	protected Map<String, String> siteMap = new HashMap<String,String>();
	
	//The main function which calls the xml parser and polling timertask.
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		System.out.println("Calling xml file parsing function\n");
		
		//Initialize a class object so that the parseXML function can simply access those without
		// requiring the use of returning a pair containing the polling period and site/requirement map.
		//Also calling the parseXML function in order to read the file.
		WebStatusMain myMain = new WebStatusMain();
		myMain.parseXML();
	}
	
	//Utility functions
	//The parseXML function parses the XML configuration file.
	//This required some referencing of outside resources as I haven't parsed XML in a while.
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
		
		//Fetch the polling rate from the file.
		pollingRate = Integer.parseInt(xmlRoot.getElementsByTagName("pollingrate").item(0).getTextContent());
		
		//Iterate through the list of nodes and record the site addresses and requirements into a hashmap.
		NodeList nodes = xmlRoot.getElementsByTagName("site");
		System.out.print(nodes.getLength());
	}
	
	//A function for reading the user's input.
	//Uses a buffered reader. It reads a line into the string variable until 
	// the variable returns something usable at which point the value is returned.
	private static String readInput() {
		BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
		String s = null;
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
