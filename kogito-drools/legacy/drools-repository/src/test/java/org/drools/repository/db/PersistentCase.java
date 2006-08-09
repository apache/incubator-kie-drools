package org.drools.repository.db;

import junit.framework.TestCase;

import org.drools.repository.RepositoryFactory;
import org.drools.repository.RepositoryManager;

public class PersistentCase extends TestCase {

    public void testDummy() {
        //I need this as I often run all tests from within eclipse
        getRepo();
    }
    
    public RepositoryManager getRepo() {
        return RepositoryFactory.getRepository();
    }
        

    
}
