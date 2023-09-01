package org.drools.core.time;

import org.drools.base.time.JobHandle;
import org.drools.base.time.Trigger;

public interface SchedulerService {

    /**
     * Schedule a job for later execution
     * 
     * @param job
     * @param ctx
     * @param trigger
     * 
     * @return
     */
    JobHandle scheduleJob(Job job, JobContext ctx, Trigger trigger);

    /**
     * Remove the job identified by the given job handle from the 
     * scheduled queue
     * 
     * @param jobHandle the job identity handle
     * 
     * @return
     */
    void removeJob(JobHandle jobHandle);
}
