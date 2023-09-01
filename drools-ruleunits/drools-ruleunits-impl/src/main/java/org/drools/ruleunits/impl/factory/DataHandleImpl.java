package org.drools.ruleunits.impl.factory;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import org.drools.ruleunits.api.DataHandle;

public class DataHandleImpl implements DataHandle {
    private static final AtomicLong counter = new AtomicLong();

    private final long id = counter.incrementAndGet();
    private Object object;

    public DataHandleImpl(Object object) {
        this.object = object;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DataHandleImpl that = (DataHandleImpl) o;
        return id == that.id;
    }

    @Override
    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public long getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "DataHandleImpl{" +
                "id=" + id +
                ", object=" + object +
                '}';
    }
}
