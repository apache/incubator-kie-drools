package org.drools.core.common;

import java.util.Iterator;

public interface FactHandleClassStore<T> {
    Iterator<InternalFactHandle> iterator();
}
