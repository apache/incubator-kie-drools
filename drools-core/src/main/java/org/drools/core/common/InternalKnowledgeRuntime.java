package org.drools.core.common;

import java.util.Queue;

import org.drools.core.runtime.process.InternalProcessRuntime;
import org.drools.core.time.TimerService;
import org.kie.internal.runtime.KnowledgeRuntime;

public interface InternalKnowledgeRuntime extends KnowledgeRuntime {

    TimerService getTimerService();

    void startOperation();

    void endOperation();

    Queue<WorkingMemoryAction> getActionQueue();

    void executeQueuedActions();

    void queueWorkingMemoryAction(WorkingMemoryAction action);

    InternalProcessRuntime getProcessRuntime();

    void setId(Long id);

    void setEndOperationListener(EndOperationListener listener);

    long getLastIdleTimestamp();

}
