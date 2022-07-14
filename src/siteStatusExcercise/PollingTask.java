package siteStatusExcercise;

import java.util.List;
import java.util.TimerTask;
import java.util.Map.Entry;

public class PollingTask extends TimerTask{
	protected String logName;
	protected List<Entry<String, String>> siteMap;
	public PollingTask(String inLogName, List<Entry<String, String>> inSiteMap) {
		this.logName=inLogName;
		this.siteMap=inSiteMap;
	}
	@Override
	public void run() {
		System.out.println("Task Test");
		System.out.println(siteMap);
	}
}
