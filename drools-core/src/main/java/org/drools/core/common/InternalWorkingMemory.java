package org.drools.core.common;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.drools.core.WorkingMemory;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.event.AgendaEventSupport;
import org.drools.core.event.RuleRuntimeEventSupport;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.runtime.process.InternalProcessRuntime;
import org.drools.core.rule.consequence.InternalMatch;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.rule.EntryPoint;

public interface InternalWorkingMemory
    extends WorkingMemory, WorkingMemoryEntryPoint, EventSupport {

    InternalAgenda getAgenda();

    long getIdentifier();
    void setIdentifier(long id);

    void setRuleRuntimeEventSupport(RuleRuntimeEventSupport workingMemoryEventSupport);

    void setAgendaEventSupport(AgendaEventSupport agendaEventSupport);

    void clearNodeMemory(MemoryFactory node);
    
    NodeMemories getNodeMemories();

    default FactHandleClassStore getStoreForClass(Class<?> clazz) {
        return getObjectStore().getStoreForClass(clazz);
    }

    Lock getLock();

    InternalFactHandle getInitialFactHandle();
    
    InternalKnowledgeRuntime getKnowledgeRuntime();
    
    /**
     * Returns a map of channel Id->Channel of all channels in
     * this working memory
     * 
     * @return
     */
    Map< String, Channel> getChannels();
    
    Collection< ? extends EntryPoint> getEntryPoints();

    void startBatchExecution();
    
    void endBatchExecution();
    
    /**
     * Returns the number of time units (usually ms) that the engine is idle
     * according to the session clock or -1 if it is not idle.
     * 
     * This method is not synchronised and might return an approximate value.
     *  
     * @return
     */
    long getIdleTime();
    
    /**
     * Returns the number of time units (usually ms) to
     * the next scheduled job
     * 
     * @return the number of time units until the next scheduled job or -1 if
     *         there is no job scheduled
     */
    long getTimeToNextJob();
    
    void updateEntryPointsCache();
    
    /**
     * Returns the total number of facts in the working memory, i.e., counting
     * all facts from all entry points. This is an approximate value and may not
     * be accurate due to the concurrent nature of the entry points.
     * 
     * @return
     */
    long getTotalFactCount();
    
    InternalProcessRuntime getProcessRuntime();
    InternalProcessRuntime internalGetProcessRuntime();

    void closeLiveQuery(InternalFactHandle factHandle);

    void flushPropagations();

    void activate();
    void deactivate();
    boolean tryDeactivate();

    Iterator<? extends PropagationEntry> getActionsIterator();

    void removeGlobal(String identifier);

    void notifyWaitOnRest();

    void cancelActivation(InternalMatch internalMatch, boolean declarativeAgenda);

    default boolean isThreadSafe() {
        return getRuleSessionConfiguration().isThreadSafe();
    }
}
