package org.drools.commands;

import org.kie.api.runtime.ExecutableRunner;

public interface ChainableRunner extends InternalLocalRunner {
    void setNext(ExecutableRunner runner);
    ExecutableRunner getNext();
}
