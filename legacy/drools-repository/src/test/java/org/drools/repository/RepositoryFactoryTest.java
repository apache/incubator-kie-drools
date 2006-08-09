package org.drools.repository;

import junit.framework.TestCase;

public class RepositoryFactoryTest extends TestCase {

    public void testFactory() {
        RepositoryManager manager = RepositoryFactory.getRepository();
        assertNotNull(manager);
        manager.listRuleSets();        
    }
    
}
