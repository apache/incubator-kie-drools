/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.cdi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.drools.compiler.kproject.AbstractKnowledgeTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieServices;
import org.kie.api.cdi.KReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

@RunWith(CDITestRunner.class)
public class KieContainerInjectionTest {
    public static AbstractKnowledgeTest helper;

    @Inject
    private KieContainer                kContainer;

    @Inject
    @KReleaseId(groupId = "jar1",
                artifactId = "art1",
                version = "1.1")
    private KieContainer                kContainerv11;

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
            helper.createKieModule( "jar1", true, "1.1" );
            helper.createKieModule( "jar2", true, "2.0" );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Unable to build dynamic KieModules:\n" + e.toString() );
        }

        CDITestRunner.setUp( helper.getFileManager().newFile( "jar2-2.0.jar" ) );

        CDITestRunner.weld = CDITestRunner.createWeld( KieContainerInjectionTest.class.getName() );

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

    public void testDefaultKieContainer() throws IOException,
                                         ClassNotFoundException,
                                         InterruptedException {
        assertSame( kContainer,
                    KieServices.Factory.get().getKieClasspathContainer() );

        KieSession kSession = kContainer.newKieSession( "jar2.KSession2" );
        List<String> list = new ArrayList<String>();
        kSession.setGlobal( "list",
                            list );
        kSession.fireAllRules();

        assertEquals( 2,
                      list.size() );
        assertTrue( list.get( 0 ).endsWith( "2.0" ) );
        assertTrue( list.get( 1 ).endsWith( "2.0" ) );
    }

    @Test
    public void testDynamicKieContainerWithReleaseId() throws IOException,
                                                      ClassNotFoundException,
                                                      InterruptedException {
        assertNotSame( kContainerv11,
                       KieServices.Factory.get().getKieClasspathContainer() );

        KieSession kSession = kContainerv11.newKieSession( "jar1.KSession2" );
        List<String> list = new ArrayList<String>();
        kSession.setGlobal( "list",
                            list );
        kSession.fireAllRules();

        assertEquals( 2,
                      list.size() );
        assertTrue( list.get( 0 ).endsWith( "1.1" ) );
        assertTrue( list.get( 1 ).endsWith( "1.1" ) );
    }

}
