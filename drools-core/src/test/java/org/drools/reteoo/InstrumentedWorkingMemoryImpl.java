package org.drools.reteoo;

import org.drools.FactHandle;

/*
 * InstrumentedWorkingMemoryImpl memory thats extends WorkingMemoryImpl and exposes some
 * package protected methods.
 */
public class InstrumentedWorkingMemoryImpl extends WorkingMemoryImpl {
    public InstrumentedWorkingMemoryImpl() {
        this( new RuleBaseImpl() );
    }

    public InstrumentedWorkingMemoryImpl(RuleBaseImpl ruleBase) {
        super( ruleBase );
    }

    /**
     * Associate an object with its handle.
     * 
     * @param handle
     *            The handle.
     * @param object
     *            The object.
     */
    public Object putObject(FactHandle handle,
                            Object object) {
        return super.putObject( handle,
                                object );
    }

    /**
     * Helper method to return FactHandles with a specific id
     */
    public FactHandle createFactHandle(int id) {
        return new FactHandleImpl( id );
    }
}
