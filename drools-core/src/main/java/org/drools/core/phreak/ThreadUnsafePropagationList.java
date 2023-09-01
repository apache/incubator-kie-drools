package org.drools.core.phreak;

import java.util.Collections;
import java.util.Iterator;

import org.drools.core.common.ReteEvaluator;

public class ThreadUnsafePropagationList implements PropagationList {

    private final ReteEvaluator reteEvaluator;

    public ThreadUnsafePropagationList( ReteEvaluator reteEvaluator ) {
        this.reteEvaluator = reteEvaluator;
    }

    @Override
    public void addEntry( PropagationEntry propagationEntry ) {
        propagationEntry.execute( reteEvaluator );
    }

    @Override
    public PropagationEntry takeAll() {
        return null;
    }

    @Override
    public void flush() {
    }

    @Override
    public void flush( PropagationEntry currentHead ) {
    }

    @Override
    public void reset() {
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean hasEntriesDeferringExpiration() {
        return false;
    }

    @Override
    public Iterator<PropagationEntry> iterator() {
        return Collections.emptyIterator();
    }

    @Override
    public void waitOnRest() {
    }

    @Override
    public void notifyWaitOnRest() {
    }

    @Override
    public void onEngineInactive() {
    }

    @Override
    public void dispose() {
    }

    @Override
    public void setFiringUntilHalt( boolean firingUntilHalt ) {
    }
}
