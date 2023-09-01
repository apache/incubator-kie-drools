package org.kie.api.internal.assembler;

import org.kie.api.io.Resource;

public interface ProcessedResource {
    String getName();
    String getNamespace();
    Resource getResource();
}
