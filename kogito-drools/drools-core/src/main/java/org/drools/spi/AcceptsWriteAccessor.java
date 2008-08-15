package org.drools.spi;

public interface AcceptsWriteAccessor
    extends
    Acceptor {
    void setWriteAccessor(WriteAccessor writeAccessor);
}
