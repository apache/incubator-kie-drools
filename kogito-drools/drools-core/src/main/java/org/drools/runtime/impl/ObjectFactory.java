package org.drools.runtime.impl;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {
    
    public ExecutionResultImpl createExecutionResultImpl() {
        return new ExecutionResultImpl();
    }
}
