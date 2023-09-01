package org.kie.api.persistence.jpa;

import org.kie.api.KieBase;
import org.kie.api.internal.utils.KieService;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;

public interface KieStoreServices extends KieService {

    KieSession newKieSession(KieBase kbase,
                             KieSessionConfiguration configuration,
                             Environment environment);

    /**
     * Deprecated use {@link  #loadKieSession(Long, KieBase, KieSessionConfiguration, Environment)} instead
     */
    @Deprecated
    KieSession loadKieSession(int id,
                              KieBase kbase,
                              KieSessionConfiguration configuration,
                              Environment environment);

    KieSession loadKieSession(Long id,
            KieBase kbase,
            KieSessionConfiguration configuration,
            Environment environment);

}
