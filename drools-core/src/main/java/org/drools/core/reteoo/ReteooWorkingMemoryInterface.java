package org.drools.core.reteoo;

import java.util.Collection;
import java.util.Map;
import java.util.Queue;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.EndOperationListener;
import org.drools.core.common.EventSupport;
import org.drools.core.common.InternalWorkingMemoryActions;
import org.drools.core.common.WorkingMemoryAction;
import org.kie.api.event.process.ProcessEventManager;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.LiveQuery;
import org.kie.api.runtime.rule.ViewChangedEventListener;
import org.kie.api.runtime.rule.EntryPoint;

/**
 * This is an interface for ReteooWorkingMemory implementations
 * 
 * @author etirelli
 */
public interface ReteooWorkingMemoryInterface extends InternalWorkingMemoryActions,
                                             EventSupport,
                                             ProcessEventManager {

    Map<String, WorkingMemoryEntryPoint> getWorkingMemoryEntryPoints();

    void fireUntilHalt();

    void fireUntilHalt( AgendaFilter agendaFilterWrapper );

    ProcessInstance createProcessInstance( String processId, Map<String, Object> parameters );

    ProcessInstance startProcessInstance( long processInstanceId );

    void registerChannel( String name, Channel channel );

    void unregisterChannel( String name );

    Queue<WorkingMemoryAction> getActionQueue();

    LiveQuery openLiveQuery( String query, Object[] arguments, ViewChangedEventListener listener );

    void setEndOperationListener( EndOperationListener listener );

    long getLastIdleTimestamp();

}
