package org.kie.internal.runtime;

import java.util.Collection;

public interface StatelessKnowledgeSessionResults {
    Collection<String> getIdentifiers();

    Object getValue(String identifier);
}
