package org.drools.repository.modeshape;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;

import org.drools.repository.JCRRepositoryConfigurator;
import org.jboss.security.config.IDTrustConfiguration;
import org.modeshape.jcr.CndNodeTypeReader;

/**
 * This specialized {@link JCRRepositoryConfigurator} simply initializes the IDTrust JAAS implementation optionally used by
 * ModeShape for authentication and authorization.
 */
public class ModeShapeRepositoryConfigurator extends JCRRepositoryConfigurator {

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

    public ModeShapeRepositoryConfigurator() {
    }
    
	public void registerNodeTypesFromCndFile(String cndFileName, Session session, Workspace workspace)
			throws RepositoryException {
		CndNodeTypeReader reader = new CndNodeTypeReader(session);
		try {
			reader.read(cndFileName);
			workspace.getNodeTypeManager().registerNodeTypes(reader.getNodeTypeDefinitions(), false);
		} catch (Exception e) {
			throw new RepositoryException(e);
		}
		
	}
	
    public void shutdown() {
        if (factory instanceof org.modeshape.jcr.api.RepositoryFactory) {
            ((org.modeshape.jcr.api.RepositoryFactory)factory).shutdown();
        }
    }
}
