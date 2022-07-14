/*
 * @author antti
 * 
 * 
 */

package siteStatusExcercise;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.Map.Entry;

public class PollingTask extends TimerTask{
	protected String logName;
	protected List<Entry<String, String>> siteMap;
	
	//Constructor which saves the site map and log file name to object variables.
	public PollingTask(String inLogName, List<Entry<String, String>> inSiteMap) {
		this.logName=inLogName;
		this.siteMap=inSiteMap;
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
		for (Entry<String, String> siteEntry: siteMap) {
			//Boolean value re-set.
			conReq=false;
			try {
				//Create a URL object which is then used to open a connection.
				//This URL object is then used to open a HttpURLConnection is then configured.
				siteURL = new URL(siteEntry.getKey());
				siteConn = (HttpURLConnection) siteURL.openConnection();
				siteConn.setConnectTimeout(5000);
				siteConn.setReadTimeout(5000);
				siteConn.setRequestMethod("GET");
				//After the connection is configured the connection's response code is checked.
				status = siteConn.getResponseCode();
				if (status > 299) {
					//This means the connection failed.
					//Read and log the error.
					//siteReader = new BufferedReader(new InputStreamReader(siteConn.getErrorStream()));
					writeLog(siteEntry.getKey() + " - Failed to connect with error: " + status);
				}else {
					//An input stream is opened from the connection
					//The input stream is scanned for the required phrase. If present the loop is broken and the result is logged.
					siteReader = new BufferedReader(new InputStreamReader(siteConn.getInputStream()));
					while((siteIn = siteReader.readLine())!= null) {
						if(siteIn.contains(siteEntry.getValue())) {
							//System.out.println(siteEntry.getKey() + " doot: " + siteIn);
							writeLog(siteEntry.getKey() + " - Success: Requirements met. - ");
							conReq=true;
							break;
						}
					}
					//Close the connection.
					siteConn.disconnect();
					
					//If the entire site's contents are read without the required phrase being found it is logged.
					if (!conReq) {
						//System.out.println(siteEntry.getKey() + ": Site did not contain the required phrase.");
						writeLog(siteEntry.getKey() + "- Requirements not met: Site does not contain required phrase. - ");
					}
				}	
			} catch (UnknownHostException e) {
				//If an unknown host exception is thrown it means that the IP address of the host could not be determined.
				//This is logged as the site being inaccessible.
				//System.out.println("Unknown Host Exception.");
				writeLog(siteEntry.getKey() + " - Failed to connect: UnknownHostException encountered. Site is presently unavailable.");
			} catch (MalformedURLException e) {
				//This occurs when a malformed url is entered into the configuration file. This is also logged.
				//System.out.println("Malformed URL Exception.");
				writeLog(siteEntry.getKey() + " - Failed to connect: Malformed URL Entered. Unable to connect.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	//Utility functions.
	//A utility function which writes a string into the log text file.
	//Returns true if successful, false if not.
	protected boolean writeLog(String logString) {
		try {
			//Create a new writer for the logfile name the user entered.
			Writer logWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logName, true), "utf-8"));
			//Write the given string into the file and then close the writer.
			logWriter.write(new Date() + " - " + logString + "\n");
			logWriter.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
