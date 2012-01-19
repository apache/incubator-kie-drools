package org.drools.reteoo;

import java.util.Collection;
import java.util.Map;
import java.util.Queue;

import org.drools.common.EndOperationListener;
import org.drools.common.EventSupport;
import org.drools.common.InternalWorkingMemoryActions;
import org.drools.common.WorkingMemoryAction;
import org.drools.event.process.ProcessEventManager;
import org.drools.runtime.Channel;
import org.drools.runtime.ExitPoint;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.rule.LiveQuery;
import org.drools.runtime.rule.ViewChangedEventListener;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.spi.AgendaFilter;

/**
 * This is an interface for ReteooWorkingMemory implementations
 * 
 * @author etirelli
 */
public interface ReteooWorkingMemoryInterface extends InternalWorkingMemoryActions,
                                             EventSupport,
                                             ProcessEventManager {

    Collection<? extends WorkingMemoryEntryPoint> getWorkingMemoryEntryPoints();

    void fireUntilHalt();

    void fireUntilHalt( AgendaFilter agendaFilterWrapper );

    ProcessInstance createProcessInstance( String processId, Map<String, Object> parameters );

    ProcessInstance startProcessInstance( long processInstanceId );

    void registerExitPoint( String name, ExitPoint exitPoint );

    void unregisterExitPoint( String name );

    void registerChannel( String name, Channel channel );

    void unregisterChannel( String name );

    Queue<WorkingMemoryAction> getActionQueue();

    LiveQuery openLiveQuery( String query, Object[] arguments, ViewChangedEventListener listener );

    void setEndOperationListener( EndOperationListener listener );

    long getLastIdleTimestamp();

}
