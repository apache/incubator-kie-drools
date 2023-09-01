package org.drools.base.base;

import org.drools.base.rule.accessor.ReadAccessor;

public interface ReadAccessorSupplier {
    ReadAccessor getReader(AccessorKey key);
}
