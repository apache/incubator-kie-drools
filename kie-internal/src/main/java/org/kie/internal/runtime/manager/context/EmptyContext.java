package org.kie.internal.runtime.manager.context;

import org.kie.api.runtime.manager.Context;

/**
 * Default implementation of <code>Context</code> interface that does not bring any specifics
 * to the RuntimeManager and is usually used for instances of manager that does not deal with
 * contextual information:
 * <ul>
 *  <li>Singleton</li>
 *  <li>PerRequest</li>
 * </ul>
 * To obtain instances of this context use static <code>get()</code> method.
 */
public class EmptyContext implements Context<String> {

    protected static final String CONTEXT_ID = "EmptyContext";

    protected static final Context<String> INSTANCE = new EmptyContext();

    protected EmptyContext() {

    }
    @Override
    public String getContextId() {

        return CONTEXT_ID;
    }

    /**
     * Returns instances of the empty context.
     * @return
     */
    public static Context<String> get() {
        return INSTANCE;
    }
}
