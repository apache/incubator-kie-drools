package org.kie.api.runtime.conf;

import org.kie.api.conf.OptionKey;

/**
 * An option to define if the KieSession should should be thread safe or not.
 * By default a KieSession is thread-safe. Flagging it as non-thread-sage will bring a performance improvement
 * at the cost of being no longer able to safely use the KieSession in a multithreaded environment.
 *
 * drools.threadSafe = &lt;true|false&gt;
 *
 * DEFAULT = true
 */
public enum ThreadSafeOption implements SingleValueRuleRuntimeOption {

    YES(true),
    NO(false);

    private static final long serialVersionUID = 510l;

    /**
     * The property name for the thread safety configuration
     */
    public static final String PROPERTY_NAME = "drools.threadSafe";

    public static OptionKey<ThreadSafeOption> KEY = new OptionKey<>(TYPE, PROPERTY_NAME);

    private final boolean threadSafe;

    /**
     * Private constructor to enforce the use of the factory method
     * @param threadSafe
     */
    ThreadSafeOption( final boolean threadSafe ) {
        this.threadSafe = threadSafe;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public boolean isThreadSafe() {
        return threadSafe;
    }

}