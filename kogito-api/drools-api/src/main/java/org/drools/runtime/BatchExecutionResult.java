package org.drools.runtime;

import java.util.Collection;

public interface BatchExecutionResult {

    Collection<String> getIdentifiers();

    Object getValue(String identifier);

}