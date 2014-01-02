package org.drools.core.marshalling.impl;

import static org.junit.Assert.*;
import org.junit.Test;

public class VersionTest {

    @Test
    public void versionParsingTest() { 
       int [] version = PersisterHelper.getVersion("6.1.0");
       assertEquals( "Incorrect major version", 6, version[0]);
       assertEquals( "Incorrect minor version", 1, version[1]);
       assertEquals( "Incorrect patch version", 0, version[2]);
       
       version = PersisterHelper.getVersion("6.1.0-SNAPSHOT");
       assertEquals( "Incorrect major version", 6, version[0]);
       assertEquals( "Incorrect minor version", 1, version[1]);
       assertEquals( "Incorrect patch version", 0, version[2]);

       version = PersisterHelper.getVersion("6.1.0.Final");
       assertEquals( "Incorrect major version", 6, version[0]);
       assertEquals( "Incorrect minor version", 1, version[1]);
       assertEquals( "Incorrect patch version", 0, version[2]);

       // this should not throw an exception
       version = PersisterHelper.getVersion(PersisterHelper.PROJECT_VERSION);
    }
    
}
