package org.drools.spi;

public interface AcceptsReadAccessor
    extends
    Acceptor {
    void setReadAccessor(InternalReadAccessor readAccessor);
}
