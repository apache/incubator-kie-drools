package org.drools.scheduler;

public interface Job {
	public void execute(JobContext ctx);
}
