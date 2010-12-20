package org.drools.common;

import java.util.Queue;

import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.process.InternalProcessRuntime;
import org.drools.time.TimerService;

public interface InternalKnowledgeRuntime extends KnowledgeRuntime {

	TimerService getTimerService();
	
	void startOperation();
	
	void endOperation();
	
	Queue<WorkingMemoryAction> getActionQueue();
	
	void executeQueuedActions();
	
	void queueWorkingMemoryAction(WorkingMemoryAction action);
	
	InternalProcessRuntime getProcessRuntime();
	
	void setId(long id);
	
	void setEndOperationListener(EndOperationListener listener);
	
    long getLastIdleTimestamp();
	
}
