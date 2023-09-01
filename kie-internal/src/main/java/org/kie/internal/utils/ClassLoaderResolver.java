package org.kie.internal.utils;

import org.kie.api.builder.KieModule;
import org.kie.api.internal.utils.KieService;

public interface ClassLoaderResolver extends KieService {

    ClassLoader getClassLoader( KieModule kmodule );

}
