package org.drools.runtime.rule;

import java.io.Externalizable;
import java.io.Serializable;

/**
 * An interface for accumulate external function implementations
 */ 
public interface AccumulateFunction extends Externalizable {

    /**
     * Creates and returns a new context object
     * @return
     */
    public Serializable createContext();

    /**
     * Initializes the accumulator
     * @param context
     * @throws Exception
     */
    public void init(Serializable context) throws Exception;

    /**
     * Executes the accumulation action
     * @param context
     * @param value
     */
    public void accumulate(Serializable context,
                           Object value);

    /**
     * Reverses the accumulation action
     * @param context
     * @param value
     * @throws Exception
     */
    public void reverse(Serializable context,
                        Object value) throws Exception;

    /**
     * Returns the current value in this accumulation session
     *
     * @param context
     * @return
     * @throws Exception
     */
    public Object getResult(Serializable context) throws Exception;

    /**
     * True if the function supports reverse. False otherwise.
     *
     * @return
     */
    public boolean supportsReverse();
}
