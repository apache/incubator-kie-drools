/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.cdi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import org.drools.cdi.kproject.AbstractKnowledgeTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieBase;
import org.kie.api.cdi.KReleaseId;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.*;

@RunWith(CDITestRunner.class)
public class KieBaseDefaultInjectionTest {
    public static AbstractKnowledgeTest helper;
    
    @Inject
    private KieBase defaultClassPathKBase;

    @Inject
    @KReleaseId( groupId    = "jar1",
                 artifactId = "art1", 
                 version    = "1.0")    
    private KieBase defaultDynamicKBase;            
    
    @BeforeClass
    public static void beforeClass() {  
        helper = new AbstractKnowledgeTest();
        try {
            helper.setUp();
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
        try {
            helper.createKieModule( "jar1", true, "1.0" );
            helper.createKieModule( "jar2", true, "2.0" );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Unable to build dynamic KieModules:\n" + e.toString() );
        }

        CDITestRunner.setUp( helper.getFileManager().newFile( "jar2-2.0.jar" ) );
        CDITestRunner.weld = CDITestRunner.createWeld( KieBaseDefaultInjectionTest.class.getName() );

        CDITestRunner.container = CDITestRunner.weld.initialize();
    }

    @AfterClass
    public static void afterClass() {
        CDITestRunner.tearDown();
        
        try {
            helper.tearDown();
        } catch ( Exception e ) {
            fail( e.getMessage() );
        }
    }     
    
    @Test    
    public void tessDefaultClassPathKBase() throws IOException, ClassNotFoundException, InterruptedException {
        assertNotNull( defaultClassPathKBase );
        
        KieSession kSession = defaultClassPathKBase.newKieSession();
        List<String> list = new ArrayList<String>();
        kSession.setGlobal( "list", list );
        kSession.fireAllRules();
        
        assertEquals( 2, list.size() );
        assertTrue( list.get(0).endsWith( "2.0" ) );
        assertTrue( list.get(1).endsWith( "2.0" ) );        
    }
    
    @Test    
    public void tessDefaultDynamicKBase() throws IOException, ClassNotFoundException, InterruptedException {
        assertNotNull( defaultDynamicKBase );
        
        KieSession kSession = defaultDynamicKBase.newKieSession();
        List<String> list = new ArrayList<String>();
        kSession.setGlobal( "list", list );
        kSession.fireAllRules();
        
        assertEquals( 2, list.size() );
        assertTrue( list.get(0).endsWith( "1.0" ) );
        assertTrue( list.get(1).endsWith( "1.0" ) );        
    }  
          
}
