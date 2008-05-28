package org.drools.scheduler;

public interface Scheduler {
	public JobHandle scheduleJob(Job job, JobContext ctx, Trigger trigger);
	
	public boolean removeJob(JobHandle jobHandle);
}
