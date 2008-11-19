package org.drools.runtime;

import java.util.Collection;

public interface StatelessKnowledgeSessionResults {
    Collection<String> getIdentifiers();

    Object getValue(String identifier);
}
