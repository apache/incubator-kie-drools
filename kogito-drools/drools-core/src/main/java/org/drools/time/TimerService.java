/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.time;

/**
 * An interface for all timer service implementations used in a drools session.
 * 
 */
public interface TimerService {
    
    /**
     * Returns the current time from the scheduler clock
     * 
     * @return the current timestamp
     * 
     */
    public long getCurrentTime();
    
    /**
     * Schedule a job for later execution
     * 
     * @param job
     * @param ctx
     * @param trigger
     * 
     * @return
     */
	public JobHandle scheduleJob(Job job, JobContext ctx, Trigger trigger);
	
	/**
	 * Remove the job identified by the given job handle from the 
	 * scheduled queue
	 * 
	 * @param jobHandle the job identity handle
	 * 
	 * @return
	 */
	public boolean removeJob(JobHandle jobHandle);
	
	/**
	 * Shuts the service down
	 */
    public void shutdown();
    
    /**
     * Returns the number of time units (usually ms) to
     * the next scheduled job
     * 
     * @return the number of time units until the next scheduled job or -1 if
     *         there is no job scheduled
     */
    public long getTimeToNextJob();
	
}
