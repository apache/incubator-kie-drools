/**
 * 
 */
package org.drools.integrationtests.eventgenerator;


public class PseudoSessionClock
	    {
 	    
 	    private long timer;
 	
 	    public PseudoSessionClock() {
 	        this.timer = 0;
 	    }
 	    
 	    /* (non-Javadoc)
 	     * @see org.drools.temporal.SessionClock#getCurrentTime()
 	     */
 	    public long getCurrentTime() {
 	        return this.timer;
 	    }
 	    
 	    public long advanceTime( long millisecs ) {
 	        this.timer += millisecs;
 	        return this.timer;
 	    }
 	    
 	   public long setTime( long timer ) {
	        this.timer = timer;
	        return this.timer;
	    }
 	   
 	    // using current system time as reference
 	    public long calcFuturePointInTime (long timeToAdd){
	    	return this.timer+timeToAdd;
		}
 	    
 	    // ------------------------------------------------------------------------------------
 	    // static convenience methods
 	    
 	    // using an arbitrary starting point as reference
 	    public static long calcFuturePointInTime (long currentTime, long timeToAdd){
 	    	return currentTime+timeToAdd;
 		}
 	    
 	    // convert seconds to milliseconds
 		public static long timeInSeconds (long timeInSecs){
 			return timeInSecs*1000;
 		}
 		
 		// convert seconds to milliseconds
 		public static long timeInMinutes (long timeInMins){
 			return timeInSeconds(timeInMins)*60;
 		}
 		
 		// convert seconds to milliseconds
 		public static long timeInHours (long timeInHrs){
 			return timeInMinutes(timeInHrs)*60;
 		}
 		
 		// convert time given as hours, minutes, seconds, milliseconds to milliseconds
 		public static long timeinHMSM (long hours, long mins, long secs, long msecs){
 			return timeInHours(hours) + timeInMinutes(mins) + timeInSeconds(secs) + msecs;
 		}
 	}
