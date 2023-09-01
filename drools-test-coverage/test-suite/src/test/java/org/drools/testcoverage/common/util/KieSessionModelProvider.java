package org.drools.testcoverage.common.util;

import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.runtime.KieSessionConfiguration;

/**
 * Basic provider class for KieSessionModel instances.
 */
public interface KieSessionModelProvider {

    KieSessionModel getKieSessionModel (KieBaseModel kieBaseModel);
    KieSessionConfiguration getKieSessionConfiguration();
}
