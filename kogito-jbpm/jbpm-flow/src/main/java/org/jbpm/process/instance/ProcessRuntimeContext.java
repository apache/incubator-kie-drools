package org.jbpm.process.instance;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.drools.core.common.WorkingMemoryAction;
import org.kie.api.definition.process.Process;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.process.CorrelationKey;

public interface ProcessRuntimeContext {

    Collection<Process> getProcesses();

    Optional<Process> findProcess(String id);

    void startOperation();

    void endOperation();

    void queueWorkingMemoryAction(WorkingMemoryAction action);

    WorkItemManager getWorkItemManager();

    void addEventListener(DefaultAgendaEventListener conditional);

    boolean isActive();

    ProcessInstance createProcessInstance(
            Process process,
            CorrelationKey correlationKey,
            Map<String, Object> parameters);
}
