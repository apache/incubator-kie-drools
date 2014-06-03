package org.jbpm.executor;

public interface RequeueAware {

	/**
	 * Moves <code>RequestInfo</code> instances that are in running state longer than
	 * given amount of time (in milliseconds)
	 * @param olderThan amount of time in milliseconds from current time stamp
	 */
	void requeue(Long olderThan);
	
	/**
	 * Moves <code>RequestInfo</code> instance with given request id that are in running state
	 * @param requestId request unique identifier
	 */
	void requeueById(Long requestId);
}
