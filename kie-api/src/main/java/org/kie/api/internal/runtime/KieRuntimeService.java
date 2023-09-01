package org.kie.api.internal.runtime;

import org.kie.api.KieBase;
import org.kie.api.internal.utils.KieService;

public interface KieRuntimeService<T> extends KieService {

    T newKieRuntime(KieBase kieBase);

    Class getServiceInterface();
}
