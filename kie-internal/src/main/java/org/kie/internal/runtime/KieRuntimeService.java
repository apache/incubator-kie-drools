package org.kie.internal.runtime;


import org.kie.internal.utils.KieService;

public interface KieRuntimeService<T> extends KieService {
    T newKieRuntime(KnowledgeRuntime session);
}
