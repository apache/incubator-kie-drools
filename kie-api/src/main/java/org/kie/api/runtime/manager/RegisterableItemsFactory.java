package org.kie.api.runtime.manager;

import java.util.List;
import java.util.Map;

import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.task.TaskLifeCycleEventListener;

/**
 * Factory that is used by <code>RuntimeManager</code> to configure <code>RuntimeEngine</code>
 * (especially KieSession) with various registerable items:
 * <ul>
 *  <li>Work item handlers</li>
 *  <li>Process event listeners</li>
 *  <li>Agenda event listeners</li>
 *  <li>Working memory event listeners</li>
 * </ul>
 * Implementations of this interface shall decide what shall be registered for given <code>RuntimeEngine</code>.
 * If there are any needs to be bound handlers or listeners to eny parts of runtime engine these can be obtained:
 * <ul>
 *  <li>KieSession</li>
 *  <li>TaskService</li>
 * </ul>
 * Although it's possible to get individual instance from runtime engine it's recommended to use <code>RuntimeEngine</code>
 * instance instead to allow most flexible behavior.
 */
public interface RegisterableItemsFactory {

    /**
     * Returns new instances of <code>WorkItemHandler</code> that will be registered on <code>RuntimeEngine</code>
     * @param runtime provides <code>RuntimeEngine</code> in case handler need to make use of it internally
     * @return map of handlers to be registered - in case of no handlers empty map shall be returned.
     */
    Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime);

    /**
     * Returns new instances of <code>ProcessEventListener</code> that will be registered on <code>RuntimeEngine</code>
     * @param runtime provides <code>RuntimeEngine</code> in case listeners need to make use of it internally
     * @return list of listeners to be registered - in case of no listeners empty list shall be returned.
     */
    List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime);

    /**
     * Returns new instances of <code>AgendaEventListener</code> that will be registered on <code>RuntimeEngine</code>
     * @param runtime provides <code>RuntimeEngine</code> in case listeners need to make use of it internally
     * @return list of listeners to be registered - in case of no listeners empty list shall be returned.
     */
    List<AgendaEventListener> getAgendaEventListeners(RuntimeEngine runtime);

    /**
     * Returns new instances of <code>RuleRuntimeEventListener</code> that will be registered on <code>RuntimeEngine</code>
     * @param runtime provides <code>RuntimeEngine</code> in case listeners need to make use of it internally
     * @return list of listeners to be registered - in case of no listeners empty list shall be returned.
     */
    List<RuleRuntimeEventListener> getRuleRuntimeEventListeners(RuntimeEngine runtime);

    /**
     * Returns globals that shall be registered on <code>KieSession</code>.
     * @param runtime provides <code>RuntimeEngine</code> in case globals need to make use of it internally
     * @return map of globals to be registered - in case of no globals empty map shall be returned.
     */
    Map<String, Object> getGlobals(RuntimeEngine runtime);

    /**
     * @return new instances of <code>TaskLifeCycleEventListener</code> that will be registered on <code>RuntimeEngine</code>.
     */
    List<TaskLifeCycleEventListener> getTaskListeners();
}
