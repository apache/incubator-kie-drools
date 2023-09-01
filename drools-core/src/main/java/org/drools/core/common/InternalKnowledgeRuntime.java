package org.drools.core.common;

import org.drools.core.runtime.process.InternalProcessRuntime;
import org.drools.core.time.TimerService;
import org.kie.api.runtime.KieRuntime;

public interface InternalKnowledgeRuntime extends KieRuntime {

    TimerService getTimerService();

    InternalProcessRuntime getProcessRuntime();

    void setIdentifier(long id);

    void setEndOperationListener(EndOperationListener listener);

    long getLastIdleTimestamp();

}
