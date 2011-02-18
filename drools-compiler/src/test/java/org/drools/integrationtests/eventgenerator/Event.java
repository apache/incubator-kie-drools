/**
 * 
 */
package org.drools.integrationtests.eventgenerator;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Matthias Groch
 *
 */
public class Event implements Cloneable{
	
	public enum EventType {CUSTOM, PRODUCTION, STATUSCHANGED, HEARTBEAT, FAILURE};
	
	private EventType eventId;
	private String parentId;
	private Map<String,String> parameters;
	private long startTime, endTime;
	
	public Event() {
		this.parameters = new HashMap <String, String>();
	}

	/**
	 * @param eventId The name of the event.
	 * @param parentId The id of the corresponding site, resource, ...
	 */
	public Event(EventType eventId, String parentId) {
		this();
		this.eventId = eventId;
		this.parentId = parentId;
	}
	
	/**
	 * @param eventId The name of the event.
	 * @param parentId The id of the corresponding site, resource, ...
	 * @param start The start instance of the event.
	 * @param end The end instance of the event.
	 */
	public Event(EventType eventId, String parentId, long start, long end) {
		this(eventId, parentId);
		this.startTime = start;
		this.endTime = end;
	}
	
	/**
	 * @return the event id
	 */
	public EventType getEventId() {
		return this.eventId;
	}

	/**
	 * @param eventId the event name to set
	 */
	public void setEventType(EventType eventId) {
		this.eventId = eventId;
	}
	
	/**
	 * @return the endTime
	 */
	public long getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 *//*
	public void setEndTime(Calendar endTime) {
		this.endTime = (Calendar)endTime.clone();
	}*/

	/**
	 * @return the startTime
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 *//*
	public void setStartTime(Calendar startTime) {
		this.startTime = (Calendar)startTime.clone();
	}*/
	
	/**
	 * @param startTime the startTime to set
	 * @param endTime the endTime to set
	 */
	public void setTimes(long startTime, long endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	/**
	 * @param startTime the startTime to set
	 * @param endTime the endTime to set
	 */
	// used for primitive events where start end end time are equal
	public void setTimes(long startAndEndTime) {
		this.startTime = startAndEndTime;
		this.endTime = startAndEndTime;
	}

	/**
	 * @return the parentId
	 */
	public String getParentId() {
		return parentId;
	}

	/**
	 * @param ressourceId the ressourceId to set
	 */
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	/**
	 * @return the parameters
	 */
	public Map<String, String> getParameters() {
		return parameters;
	}
	
	/**
	 * @param parameter name of the parameter
	 * @return value of the specified parameter
	 */
	public String getParamValue(String parameter) {
		return parameters.get(parameter);
	}
	
	/**
	 * @param parameters the parameters to set
	 */
	public void addParameters(Map<String, String> parameters) {
		this.parameters.putAll(parameters);
	}
	
	/**
	 * @param paramName the name of the added parameter to set
	 * @param paramValue the value of the added parameter to set
	 */
	public void addParameter(String paramName, String paramValue) {
		this.parameters.put(paramName, paramValue);
	}
	
	public Object clone(){
	    try
	    {
	      return super.clone();
	    }
	    catch ( CloneNotSupportedException e ) {
	      // this shouldn't happen, since we are Cloneable
	      throw new InternalError();
	    }
	  }
}
