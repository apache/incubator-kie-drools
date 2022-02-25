package org.drools.compiler.builder.impl.processors;

import org.kie.internal.builder.ResourceChange;

// temporary workaround
public interface FilterCondition {
    boolean accepts(ResourceChange.Type type, String namespace, String name);
}
