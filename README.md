# Website Status Code Excercise
 Programming excercise which focuses on polling the status of sites read from a configuration file.
 
Purpose of the program and excercise:
----
 The requirements are that the program periodically makes HTTP requests to pages read from a file. The poage contents are then verified by matching a content requirement read from the file. The time required for the webserver to complete the request is also checked. This information is then written to a log file.
 
 This is accomplished through the use of a two step process. The first step is to parse the xml file using a class called by the main function. This is done both to record the polling frequency as well as to populate a list of sites which are to be polled. The list of sites is appended onto a list of tuples with one half being the site, the other the string requirement. After the full file has been parsed the list and polling rate are returned to the main function.
 
 Once the list of sites has been populate the second step is to utilize a secondary thread which iterates through the list, checks the site for the requirements and then logs the result. This is done utilizing the Java TimerTask and Timer classes. The class which iterates through the list implements the TimerTask abstract class which is scheduled using the Timer. This requires that the polling frequency is infrequent enough so that it is longer than the duration required to iterate through the list of sites.
 
 The scheduled task iterates through the list and logs the results into a file. The log entries use the following format: The address of the website - The status: Either "Success", "Connection Failed" or "Requirements not met" - The time taken to complete the request

How to use:
----
 As it stands the program does not come precompiled. Although the configuration file (testsites.xml) contains several entries used for testing additional ones can be added. After adding the sites the code should be compiled and/or run in the environment of the user's choice. The program will create a log file if one does not exist and then append to it.
  
Step by step:
----
 * TODO once more of code is in place
  