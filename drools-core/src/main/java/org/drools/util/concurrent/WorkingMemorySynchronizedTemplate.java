package org.drools.util.concurrent;

import org.drools.WorkingMemory;

/**
 * Helper class that allows multiple WorkingMemory methods to be invoked in a
 * single synchronized block via the use of a WorkingMemoryCallback. For
 * example:
 * 
 * <pre><code>
 *         private void updateWorkingMemory(Foo foo, Bar oldBar, Bar newBar) {
 *             new WorkingMemoryTemplate(workingMemory).execute(new WorkingMemoryCallback() {
 *                 public Object doInWorkingMemory(final WorkingMemory workingMemory) {
 *                     workingMemory.assertObject(foo);
 *    
 *                     FactHandle handle = workingMemory.getFactHandle(oldBar)
 *                     workingMemory.modifyObject(handle, newBar);
 *    
 *                     workingMemory.fireAllRules();
 *                 }
 *             }
 *         }
 * </code></pre>
 * 
 * @see org.drools.util.concurrent.AbstractWorkingMemorySynchronizedTemplate
 */
public class WorkingMemorySynchronizedTemplate extends AbstractWorkingMemorySynchronizedTemplate {

    private final WorkingMemory workingMemory;

    /**
     * Construct a new WorkingMemorySynchronizedTemplate.
     * 
     * @param workingMemory
     */
    public WorkingMemorySynchronizedTemplate(final WorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
    }

    protected WorkingMemory getWorkingMemory() {
        return this.workingMemory;
    }
}
