package org.kie.internal.runtime.manager.context;

import org.kie.api.runtime.manager.Context;

/**
 * Process instance id aware implementation of the <code>Context</code> interface.
 * It's main responsibility is to be the data holder between caller and runtime manager
 * to obtain proper instances of <code>RuntimeEngine</code>.<br>
 * Used by strategy:
 * <ul>
 *  <li>PerProcessInstance</li>
 * </ul>
 * To obtain instances of this context use one of the following static methods:
 * <ul>
 *  <li><code>get()</code> to get empty context when starting process instances</li>
 *  <li><code>get(Long)</code> to get context for specific process instance</li>
 * </ul>
 */
public class ProcessInstanceIdContext implements Context<String> {

    private String processInstanceId;

    public ProcessInstanceIdContext(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    @Override
    public String getContextId() {
        return processInstanceId;
    }

    public void setContextId(String id) {
        this.processInstanceId = id;
    }

    /**
     * Returns new instance of <code>ProcessInstanceIdContext</code> without process instance id.
     * Used for starting new instances of the process.
     * @return
     */
    public static ProcessInstanceIdContext get() {
        return new ProcessInstanceIdContext(null);
    }

    /**
     * Returns new instance of <code>ProcessInstanceIdContext</code> with id of already existing process instance
     * @param processInstanceId actual identifier of process instance
     * @return
     */
    public static ProcessInstanceIdContext get(String processInstanceId) {
        return new ProcessInstanceIdContext(processInstanceId);
    }

}
