package org.optaplanner.constraint.streams.bavet.common;

public abstract class AbstractTuple implements Tuple {

    public final Object[] store;

    public BavetTupleState state = BavetTupleState.CREATING;

    protected AbstractTuple(int storeSize) {
        store = (storeSize <= 0) ? null : new Object[storeSize];
    }

    @Override
    public final BavetTupleState getState() {
        return state;
    }

    @Override
    public final void setState(BavetTupleState state) {
        this.state = state;
    }

    @Override
    public final Object[] getStore() {
        return store;
    }

}
