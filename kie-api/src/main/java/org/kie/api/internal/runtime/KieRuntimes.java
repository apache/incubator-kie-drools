package org.kie.api.internal.runtime;

import org.kie.api.internal.utils.KieService;

public interface KieRuntimes extends KieService {
    KieRuntimeService getRuntime(Class<?> clazz);
}
