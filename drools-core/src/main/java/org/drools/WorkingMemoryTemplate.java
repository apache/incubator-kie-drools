package org.drools;

/**
 * Interface invoke WorkingMemory methods within a callback. The concrete
 * implementations of this interface can provide various services related to the
 * WorkingMemory instance (eg, synchronization).
 */
public interface WorkingMemoryTemplate {

    /**
     * Callback interface for invoking WorkingMemory methods within a
     * synchronized block.
     */
    public interface Callback {
        /**
         * Gets called by <code>WorkingMemorySynchronizedTemplate.execute</code>
         * with a WorkingMemory instance protected by a synchronized block.
         * 
         * @param workingMemory
         * @return Any object or null.
         */
        Object doInWorkingMemory(final WorkingMemory workingMemory) throws CheckedDroolsException;
    }

    /**
     * Invokes <code>Callback.doInWorkingMemory</code>. Any thrown
     * <code>DroolsException<code> is
     * softened to <code>DroolsRuntimeException</code>.
     * @param callback
     * @return Any object or null.
     */
    Object execute(Callback callback);

}