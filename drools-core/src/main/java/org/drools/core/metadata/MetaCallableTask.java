package org.drools.core.metadata;


public interface MetaCallableTask<T> {

    public enum KIND { ASSERT, MODIFY, DELETE, DON, SHED }

    public T call();

    public KIND kind();

}
