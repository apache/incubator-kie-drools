package org.kie.internal.utils;

import org.kie.api.Service;
import org.kie.api.builder.KieModule;

public interface ClassLoaderResolver extends Service {
    
    public ClassLoader getClassLoader( KieModule kmodule );

}
