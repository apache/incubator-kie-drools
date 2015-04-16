package org.kie.api.runtime.conf;

public enum ThreadSafeOption implements SingleValueKieSessionOption {

    YES(true),
    NO(false);

    private static final long serialVersionUID = 510l;

    /**
     * The property name for the thread safe configuration
     */
    public static final String PROPERTY_NAME = "drools.threadSafe";

    private final boolean threadSafe;

    /**
     * Private constructor to enforce the use of the factory method
     * @param threadSafe
     */
    private ThreadSafeOption( final boolean threadSafe ) {
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
