package org.drools.testcoverage.common.util;

import org.drools.compiler.builder.conf.DecisionTableConfigurationImpl;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;

/**
 * Utility class for handling resources.
 */
public final class ResourceUtil {

    public static Resource getDecisionTableResourceFromClasspath(final String resourceName,
                                                                 final Class classLoaderFromClass,
                                                                 final DecisionTableInputType type) {
        final Resource dtable =
                KieServices.Factory.get().getResources().newClassPathResource(resourceName, classLoaderFromClass);
        final DecisionTableConfiguration resourceConfig = new DecisionTableConfigurationImpl();
        resourceConfig.setInputType(type);
        dtable.setConfiguration(resourceConfig);
        return dtable;
    }

    private ResourceUtil() {
        // Creating instances of util classes should not be possible.
    }
}
