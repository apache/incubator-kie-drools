package org.drools.drl.quarkus.deployment;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.kie.api.io.ResourceType;

public class DroolsCompilationProvider extends AbstractCompilationProvider {

    private static final Set<String> MANAGED_EXTENSIONS = initExtensions();

    private static Set<String> initExtensions() {
        Set<String> extensions = new HashSet<>();
        extensions.addAll(ResourceType.DRL.getAllExtensions());
        extensions.addAll(ResourceType.DTABLE.getAllExtensions());
        return Collections.unmodifiableSet(extensions);
    }

    @Override
    public Set<String> handledExtensions() {
        return MANAGED_EXTENSIONS;
    }
}
