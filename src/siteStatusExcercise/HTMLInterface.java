//I'm not entirely happy with this result but I can't for the life of me figure out a more elegant solution.

package siteStatusExcercise;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Deque;
import java.util.LinkedList;

public class HTMLInterface {

	protected String templateHTMLString;
	protected String newHTMLString;
	protected String bodyString;
	protected Deque<String> serverStrings = new LinkedList<String>();
	
	public HTMLInterface() {
		//Generate a basic HTML Template when initializing object.
		templateHTMLString = "<!DOCTYPE html>\n<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"
				+ "<title>\"Site Status\"</title>\n</head>\n<body>$body</body>\n</html>";
		bodyString = "Server Status:<br>";
		this.updatePage();
	}
	
	//Function for adding a server status string to the HTML server status table in addition to writing it into the log file.
	public void pushStatusToTable(String newStatus) {
		serverStrings.add(newStatus);
	}
	
	//Update the page by writing the string into a file.
	public void updatePage() {
		//Generate a body string for the page from the list of 
		for (String entry: serverStrings) {
			bodyString+=entry+"<br>";
		}
		
		//Generate a new HTML page string from the template and body.
		newHTMLString=templateHTMLString.replace("$body", bodyString);
		
		//Use a writer to write the HTML string into a file.
		try {
			Writer htmlWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("serverStatusPage.html"), "utf-8"));
			htmlWriter.write(newHTMLString);
			htmlWriter.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}