package org.drools.testcoverage.common.model;

import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractBean {
    private static final AtomicLong idGenerator = new AtomicLong(0 );

    private final long id;
    private int value;

    protected AbstractBean() {
        id = idGenerator.getAndIncrement();
    }

    public AbstractBean(final int value) {
        this();
        this.value = value;
    }

    public AbstractBean(final long id, final int value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public int hashCode() {
        return (int)(id ^ (id >>> 32));
    }

    @Override
    public boolean equals( Object obj ) {
        return this.getClass() == obj.getClass() && id == ((AbstractBean)obj).id;
    }

    public long getId() {
        return id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(final int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + id + ")";
    }
}
