package org.drools.time;

public interface Scheduler {
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
    
}
