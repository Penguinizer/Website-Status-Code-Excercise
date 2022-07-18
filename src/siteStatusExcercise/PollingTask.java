/*
 * @author antti
 * 
 * This class contains the task which checks the sites for the requirements.
 * The run function is called at a fixed rate by the Timer object in the main function.
 * This rate is configured by the XML configuration file.
 * 
 * The file contains the main run function which iterates through the list of sites, 
 */

package siteStatusExcercise;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimerTask;
import java.util.Map.Entry;

public class PollingTask extends TimerTask{
	protected String logName;
	protected List<Entry<String, String>> siteMap;
	protected HTMLInterface pageInterface;
	
	//Constructor which saves the site map and log file name to object variables.
	public PollingTask(String inLogName, List<Entry<String, String>> inSiteMap) {
		this.logName=inLogName;
		this.siteMap=inSiteMap;
		this.pageInterface = new HTMLInterface();
	}
	
	//The run function is called by the timer at a fixed rate configured by the file.
	@Override
	public void run() {
		//A quick print to notify the scheduled task is run. If just to make it easier in testing to see when it has.
		System.out.println("Scheduled polling task running.");
		
		//Iterate through the list of site and their requirements.
		//First performing the HTTPGet request to the address in the key.
		//Then checking for the requirements in the value.
		URL siteURL;
		HttpURLConnection siteConn;
		BufferedReader siteReader;
		String siteIn;
		int status;
		boolean conReq;
		Instant oldInstant;
		Duration timeDiff;
		for (Entry<String, String> siteEntry: siteMap) {
			//Boolean value reset for each iteration.
			conReq=false;
			try {
				//Create a URL object which is then used to open a connection.
				siteURL = new URL(siteEntry.getKey());
				//Save the instant in time into a variable to see how long it takes for the site to reply.
				oldInstant = Instant.now();
				//Open and configure connection.
				siteConn = (HttpURLConnection) siteURL.openConnection();
				//timeDiff = Duration.between(oldInstant, Instant.now());
				siteConn.setConnectTimeout(5000);
				siteConn.setReadTimeout(5000);
				siteConn.setRequestMethod("GET");
				//After the connection is configured the connection's response code is checked.
				status = siteConn.getResponseCode();
				if (status > 299) {
					//If connection failed get time difference here.
					timeDiff = Duration.between(oldInstant, Instant.now());
					//This means the connection failed. Read and log the error.
					//siteReader = new BufferedReader(new InputStreamReader(siteConn.getErrorStream()));
					writeLog(siteEntry.getKey() + " - Failed to connect with error: " + status + " - " + timeDiff);
					siteConn.disconnect();
					break;
				}else {
					//An input stream is opened from the connection and the time delta is checked.
					siteReader = new BufferedReader(new InputStreamReader(siteConn.getInputStream()));
					timeDiff = Duration.between(oldInstant, Instant.now());
					//Read through the inputstream and see if it contain the required phrase.
					while((siteIn = siteReader.readLine())!= null) {
						if(siteIn.contains(siteEntry.getValue())) {
							writeLog(siteEntry.getKey() + " - Success: Requirements met. - " + timeDiff);
							conReq=true;
							break;
						}
					}
					//Close the connection.
					siteConn.disconnect();
					
					//If the entire site's contents are read without the required phrase being found it is logged.
					if (!conReq) {
						writeLog(siteEntry.getKey() + "- Requirements not met: Site does not contain required phrase. - "  + timeDiff);
					}
				}	
			} catch (UnknownHostException e) {
				//If an unknown host exception is thrown it means that the IP address of the host could not be determined.
				//This is logged as the site being inaccessible.
				writeLog(siteEntry.getKey() + " - Failed to connect: UnknownHostException encountered. Site is presently unavailable.");
			} catch (MalformedURLException e) {
				//This occurs when a malformed url is entered into the configuration file. This is also logged.
				writeLog(siteEntry.getKey() + " - Failed to connect: Malformed URL Entered. Unable to connect.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//Update the HTML interface age.
		pageInterface.updatePage();
	}
	
	//Utility functions.
	//A utility function which writes a string into the log text file.
	//Returns true if successful, false if not.
	protected boolean writeLog(String logString) {
		try {
			//Create a new writer for the logfile name the user entered.
			Writer logWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logName, true), "utf-8"));
			//Write the given string into the file and then close the writer.
			String tempString = LocalDateTime.now() + " - " + logString;
			logWriter.write(tempString+"\n");
			logWriter.close();
			//Push the server status string to the HTML Page server status list.
			pageInterface.pushStatusToTable(tempString);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}