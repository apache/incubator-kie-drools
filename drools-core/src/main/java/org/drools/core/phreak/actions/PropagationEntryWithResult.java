package org.drools.core.phreak.actions;

import org.drools.base.base.ValueResolver;
import org.drools.base.phreak.actions.AbstractPropagationEntry;

import java.util.concurrent.CountDownLatch;

public abstract class PropagationEntryWithResult<T extends ValueResolver, R> extends AbstractPropagationEntry<T> {
    private final CountDownLatch done = new CountDownLatch(1);

    private R result;

    public final R getResult() {
        try {
            done.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    protected void done(R result) {
        this.result = result;
        done.countDown();
    }

    @Override
    public boolean requiresImmediateFlushing() {
        return true;
    }
}
