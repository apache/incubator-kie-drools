package org.drools.base.rule.accessor;

public interface GlobalResolver {
    Object resolveGlobal(String identifier);

    void setGlobal(String identifier, Object value);

    void removeGlobal(String identifier);

    void clear();
}
