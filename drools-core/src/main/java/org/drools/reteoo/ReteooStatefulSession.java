package org.drools.reteoo;

import java.util.List;

import org.drools.FactHandle;
import org.drools.StatefulSession;
import org.drools.common.InternalRuleBase;
import org.drools.concurrent.AssertObject;
import org.drools.concurrent.AssertObjects;
import org.drools.concurrent.ExecutorService;
import org.drools.concurrent.FireAllRules;
import org.drools.concurrent.Future;
import org.drools.concurrent.ModifyObject;
import org.drools.concurrent.RetractObject;
import org.drools.spi.AgendaFilter;

public class ReteooStatefulSession extends ReteooWorkingMemory
    implements
    StatefulSession {
    private final ExecutorService executor;

    public ReteooStatefulSession(final int id,
                                 final InternalRuleBase ruleBase,
                                 final ExecutorService executorService) {
        super( id,
               ruleBase );
        this.executor = executorService;
    }

    public Future asyncAssertObject(final Object object) {
        final AssertObject assertObject = new AssertObject( object );
        this.executor.submit( assertObject );
        return assertObject;
    }

    public Future asyncRetractObject(final FactHandle factHandle) {
        return this.executor.submit( new RetractObject( factHandle ) );
    }

    public Future asyncModifyObject(final FactHandle factHandle,
                                    final Object object) {
        return this.executor.submit( new ModifyObject( factHandle,
                                                       object ) );
    }

    public Future asyncAssertObjects(final Object[] list) {
        final AssertObjects assertObjects = new AssertObjects( list );
        this.executor.submit( assertObjects );
        return assertObjects;
    }

    public Future asyncAssertObjects(final List list) {
        final AssertObjects assertObjects = new AssertObjects( list );
        this.executor.submit( assertObjects );
        return assertObjects;
    }

    public Future asyncFireAllRules(final AgendaFilter agendaFilter) {
        final FireAllRules fireAllRules = new FireAllRules( agendaFilter );
        this.executor.submit( fireAllRules );
        return fireAllRules;
    }

    public Future asyncFireAllRules() {
        final FireAllRules fireAllRules = new FireAllRules( null );
        this.executor.submit( fireAllRules );
        return fireAllRules;
    }
    
    public void dispose() {
        this.ruleBase.disposeStatefulSession( this );
    }    
}
