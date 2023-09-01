package org.kie.internal.runtime.manager.context;

import org.kie.api.runtime.manager.Context;
import org.kie.internal.process.CorrelationKey;

/**
 * Context implementation to deliver capabilities to find proper <code>RuntimeEngine</code>
 * instances based on correlation key instead of process instance id. Use by strategy:
 * <ul>
 *  <li>PerProcessInstance</li>
 * </ul>
 * To obtain instances of this context use one of the following static methods:
 * <ul>
 *  <li><code>get()</code> to get empty context when starting process instances</li>
 *  <li><code>get(CorrelationKey)</code> to get context for specific process instance</li>
 * </ul>
 *
 */
public class CorrelationKeyContext implements Context<CorrelationKey> {

    private CorrelationKey correlationKey;

    public CorrelationKeyContext(CorrelationKey key) {
        this.correlationKey = key;
    }

    @Override
    public CorrelationKey getContextId() {

        return correlationKey;
    }

    /**
     * Returns new instance of <code>CorrelationKeyContext</code> without correlation key.
     * Used for starting new instances of the process.
     * @return
     */
    public static CorrelationKeyContext get() {
        return new CorrelationKeyContext(null);
    }

    /**
     * Returns new instance of <code>CorrelationKeyContext</code> with correlation key of already existing process instance
     * @param key actual correlation key of process instance
     * @return
     */
    public static CorrelationKeyContext get(CorrelationKey key) {
        return new CorrelationKeyContext(key);
    }
}
