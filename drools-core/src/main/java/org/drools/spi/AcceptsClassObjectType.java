package org.drools.spi;

import org.drools.base.ClassObjectType;

public interface AcceptsClassObjectType
    extends
    Acceptor {
    public void setClassObjectType(ClassObjectType classObjectType);
}
