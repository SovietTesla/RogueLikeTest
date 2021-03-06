package gameBase;

import java.awt.Color;
import java.util.LinkedList;

public class Logger {
	private LinkedList<Message> loggedMessages;
	private int messageWidth;
	private final int maxLogMessages;
	
	/**
	 * 
	 * @param width maximum length of messages
	 * @param maxMessages maximum number of messages
	 */
	public Logger(int width, int maxMessages){
		loggedMessages = new LinkedList<Message>();
		messageWidth = width;
		maxLogMessages = maxMessages;
	}
	
	/**
	 * Adds a message to the log
	 * @param msg message to be added
	 */
	public void logMessage(String msg){
		logMessage(msg, Color.WHITE);
	}
	public void logMessage(String msg, Color color){
		for(String m : splitMessage(msg)){
			loggedMessages.add(new Message(m, color));
		}
		while(loggedMessages.size() > maxLogMessages){
			loggedMessages.removeFirst();
		}
	}
	
	private LinkedList<String> splitMessage(String msg){
		LinkedList<String> splitUpMessages = new LinkedList<String>();
		recSplit(msg, splitUpMessages);
		return splitUpMessages;
	}
	
	private void recSplit(String msg, LinkedList<String> splitUpMessages){
		if(msg.length() <= messageWidth){
			splitUpMessages.add(msg);
			return;
		}
		int indexOfSpace = messageWidth;
		for(int i = 0; i < messageWidth; i++){
			if(msg.charAt(i) == ' '){
				indexOfSpace = i + 1;
			}
		}
		
		String firstMsg = msg.substring(0, indexOfSpace);
		String nextMsg = msg.substring(indexOfSpace);
		splitUpMessages.add(firstMsg);
		recSplit(nextMsg, splitUpMessages);
	}
	
	/**
	 * returns all messages logged in a Linked List
	 * @return all messages
	 */
	public LinkedList<Message> getMessages(){
		return loggedMessages;
	}
}
