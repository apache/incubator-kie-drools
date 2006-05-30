package org.drools.common;

import java.util.Map;

import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.WorkingMemory;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.spi.FactHandleFactory;
import org.drools.spi.PropagationContext;

public interface InternalRuleBase
    extends
    RuleBase {

    public FactHandleFactory newFactHandleFactory();

    public Map getGlobals();

    public RuleBaseConfiguration getConfiguration();

    void disposeWorkingMemory(WorkingMemory workingMemory);

    /**
     * Assert a fact object.
     * 
     * @param handle
     *            The handle.
     * @param object
     *            The fact.
     * @param workingMemory
     *            The working-memory.
     * 
     * @throws FactException
     *             If an error occurs while performing the assertion.
     */
    public void assertObject(FactHandle handle,
                             Object object,
                             PropagationContext context,
                             ReteooWorkingMemory workingMemory) throws FactException;

    public void modifyObject(FactHandle handle,
                             PropagationContext context,
                             ReteooWorkingMemory workingMemory) throws FactException;

    /**
     * Retract a fact object.
     * 
     * @param handle
     *            The handle.
     * @param workingMemory
     *            The working-memory.
     * 
     * @throws FactException
     *             If an error occurs while performing the retraction.
     */
    public void retractObject(FactHandle handle,
                              PropagationContext context,
                              ReteooWorkingMemory workingMemory) throws FactException;
}
