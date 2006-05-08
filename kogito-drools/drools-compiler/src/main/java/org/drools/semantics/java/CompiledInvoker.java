package org.drools.semantics.java;

import java.util.List;

import org.drools.spi.Invoker;

public interface CompiledInvoker extends Invoker {
    public List getMethodBytecode();
}
