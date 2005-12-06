package org.drools.util.concurrent;

import org.drools.DroolsException;
import org.drools.DroolsRuntimeException;
import org.drools.WorkingMemory;
import org.drools.WorkingMemoryTemplate;

/**
 * Helper class that allows multiple WorkingMemory methods to be invoked in a
 * single synchronized block via the use of a WorkingMemoryCallback. For example:
 *     <pre><code>
 *     private void updateWorkingMemory(Foo foo, Bar oldBar, Bar newBar) {
 *         new WorkingMemoryTemplate(workingMemory).execute(new WorkingMemoryCallback() {
 *             public Object doInWorkingMemory(final WorkingMemory workingMemory) {
 *                 workingMemory.assertObject(foo);
 *
 *                 FactHandle handle = workingMemory.getFactHandle(oldBar)
 *                 workingMemory.modifyObject(handle, newBar);
 *
 *                 workingMemory.fireAllRules();
 *             }
 *         }
 *     }
 *     </code></pre>
 *
 *  The abstract method <code>getWorkingMemory</code> allows concrete subclass to define
 *  their own strategy for obtaining the WorkingMemory instance.
 */
public abstract class AbstractWorkingMemorySynchronizedTemplate implements WorkingMemoryTemplate {

    /**
     * Concrete subclasses must implement this method to provide the WorkingMemory instance.
     * @return WorkingMemory
     */
    protected abstract WorkingMemory getWorkingMemory();

    /**
     * Invokes the callback within a synchronized block on the workingMemory instance
     * passed to the constructor.
     * @param callback
     * @return Any object or null.
     */
    public Object execute(Callback callback) {
        synchronized(getWorkingMemory()) {
            try {
                return callback.doInWorkingMemory(getWorkingMemory());
            } catch (DroolsException e) {
                throw new DroolsRuntimeException(e);
            }
        }
    }
}
