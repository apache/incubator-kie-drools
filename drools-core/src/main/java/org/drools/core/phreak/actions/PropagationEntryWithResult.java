package org.drools.core.phreak.actions;

import java.util.concurrent.CountDownLatch;

public abstract class PropagationEntryWithResult<T> extends AbstractPropagationEntry {
    private final CountDownLatch done = new CountDownLatch(1);

    private T result;

    public final T getResult() {
        try {
            done.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    protected void done(T result) {
        this.result = result;
        done.countDown();
    }

    @Override
    public boolean requiresImmediateFlushing() {
        return true;
    }
}
