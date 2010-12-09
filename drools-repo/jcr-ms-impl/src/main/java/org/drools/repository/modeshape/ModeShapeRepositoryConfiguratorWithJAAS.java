package org.drools.repository.modeshape;

import org.drools.repository.JCRRepositoryConfigurator;
import org.jboss.security.config.IDTrustConfiguration;

/**
 * This specialized {@link JCRRepositoryConfigurator} simply initializes the IDTrust JAAS implementation optionally used by
 * ModeShape for authentication and authorization.
 */
public class ModeShapeRepositoryConfiguratorWithJAAS extends ModeShapeRepositoryConfigurator {

    static {
        // Initialize IDTrust
        String configFile = "modeshape/jaas.conf.xml";
        IDTrustConfiguration idtrustConfig = new IDTrustConfiguration();
        try {
            idtrustConfig.config(configFile);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
