package org.drools.base.rule.accessor;

public interface Wireable {
    void wire(Object object);

    interface Immutable extends Wireable {
        boolean isInitialized();
    }
}
