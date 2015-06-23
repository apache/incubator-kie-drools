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

package org.drools.compiler.kie.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.builder.model.KieSessionModel.KieSessionType;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.builder.ChangeType;
import org.kie.internal.builder.ResourceChange;
import org.kie.internal.builder.ResourceChangeSet;
import org.kie.internal.builder.ResourceChange.Type;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChangeSetBuilderTest {

    @Test
    public void testNoChanges() {
        String drl1 = "package org.drools\n" +
                "rule R1 when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        String drl2 = "package org.drools\n" +
                "rule R2 when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        InternalKieModule kieJar1 = createKieJar( drl1, drl2 );
        InternalKieModule kieJar2 = createKieJar( drl1, drl2 );

        ChangeSetBuilder builder = new ChangeSetBuilder();
        KieJarChangeSet changes = builder.build( kieJar1, kieJar2 );
        
        assertThat( changes.getChanges().size(), is(0));
    }

    @Test
    public void testModified() {
        String drl1 = "package org.drools\n" +
                "rule R1 when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        String drl2 = "package org.drools\n" +
                "rule R2 when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        String drl3 = "package org.drools\n" +
                "rule R3 when\n" +
                "   $m : Message( message == \"Good bye World\" )\n" +
                "then\n" +
                "end\n";

        InternalKieModule kieJar1 = createKieJar( drl1, drl2 );
        InternalKieModule kieJar2 = createKieJar( drl1, drl3 );

        KieJarChangeSet changes = new ChangeSetBuilder().build( kieJar1, kieJar2 );
        
        String modifiedFile = (String) kieJar2.getFileNames().toArray()[1];
        
        assertThat( changes.getChanges().size(), is(1));
        ResourceChangeSet cs = changes.getChanges().get( modifiedFile );
        assertThat( cs, not( nullValue() ) );
        assertThat( cs.getChangeType(), is( ChangeType.UPDATED ) );
        assertThat( cs.getChanges().size(), is(2) );
        assertThat( cs.getChanges().get( 1 ), is( new ResourceChange(ChangeType.ADDED, Type.RULE, "R3") ) );
        assertThat( cs.getChanges().get( 0 ), is( new ResourceChange(ChangeType.REMOVED, Type.RULE, "R2") ) );
        
//        ChangeSetBuilder builder = new ChangeSetBuilder();
//        System.out.println( builder.toProperties( changes ) );
    }

    @Test
    public void testRemoved() {
        String drl1 = "package org.drools\n" +
                "rule R1 when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        String drl2 = "package org.drools\n" +
                "rule R2 when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        InternalKieModule kieJar1 = createKieJar( drl1, drl2 );
        InternalKieModule kieJar2 = createKieJar( drl1 );

        KieJarChangeSet changes = new ChangeSetBuilder().build( kieJar1, kieJar2 );

        String removedFile = (String) kieJar1.getFileNames().toArray()[1];
        
        assertThat( changes.getChanges().size(), is(1));
        ResourceChangeSet cs = changes.getChanges().get( removedFile );
        assertThat( cs, not( nullValue() ) );
        assertThat( cs.getChangeType(), is( ChangeType.REMOVED ) );
    }
    
    @Test
    public void testModified2() {
        String drl1 = "package org.drools\n" +
                "rule \"Rule 1\" when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n" +
                "rule \"An updated rule\" when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n" +
                "rule \"A removed rule\" when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";
                

        String drl1_5 = "package org.drools\n" +
                "rule \"Rule 1\" when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n" +
                "rule \"An updated rule\" when\n" +
                "   $m : Message( message == \"Good Bye World\" )\n" +
                "then\n" +
                "end\n" +
                "rule \"An added rule\" when\n" +
                "   $m : Message( message == \"Good Bye World\" )\n" +
                "then\n" +
                "end\n";

        String drl2 = "package org.drools\n" +
                "rule \"This is the name of rule 3\" when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        String drl3 = "package org.drools\n" +
                "rule \"Another dumb rule\" when\n" +
                "   $m : Message( message == \"Good bye World\" )\n" +
                "then\n" +
                "end\n";

        InternalKieModule kieJar1 = createKieJar( drl1, drl2 );
        InternalKieModule kieJar2 = createKieJar( drl1_5, null, drl3 );

        ChangeSetBuilder builder = new ChangeSetBuilder();
        KieJarChangeSet changes = builder.build( kieJar1, kieJar2 );
        
//        System.out.println( builder.toProperties( changes ) );

        String modifiedFile = (String) kieJar2.getFileNames().toArray()[0];
        String addedFile = (String) kieJar2.getFileNames().toArray()[1];
        String removedFile = (String) kieJar1.getFileNames().toArray()[1];
        
        assertThat( changes.getChanges().size(), is(3));

        ResourceChangeSet cs = changes.getChanges().get( removedFile );
        assertThat( cs, not( nullValue() ) );
        assertThat( cs.getChangeType(), is( ChangeType.REMOVED) );
        assertThat( cs.getChanges().size(), is(0) );

        cs = changes.getChanges().get( addedFile );
        assertThat( cs, not( nullValue() ) );
        assertThat( cs.getChangeType(), is( ChangeType.ADDED ) );
        assertThat( cs.getChanges().size(), is(0) );

        cs = changes.getChanges().get( modifiedFile );
        assertThat( cs, not( nullValue() ) );
        assertThat( cs.getChangeType(), is( ChangeType.UPDATED ) );
//        assertThat( cs.getChanges().size(), is(3) );
//        assertThat( cs.getChanges().get( 0 ), is( new ResourceChange(ChangeType.ADDED, Type.RULE, "An added rule") ) );
//        assertThat( cs.getChanges().get( 1 ), is( new ResourceChange(ChangeType.REMOVED, Type.RULE, "A removed rule") ) );
//        assertThat( cs.getChanges().get( 2 ), is( new ResourceChange(ChangeType.UPDATED, Type.RULE, "An updated rule") ) );
    }
    
    @Test
    public void testRuleRemoval() throws Exception {
        String drl1 = "package org.drools.compiler\n" +
                      "rule R1 when\n" +
                      "   $m : Message()\n" +
                      "then\n" +
                      "end\n";

        String drl2 = "rule R2 when\n" +
                      "   $m : Message( message == \"Hi Universe\" )\n" +
                      "then\n" +
                      "end\n";

        String drl3 = "rule R3 when\n" +
                      "   $m : Message( message == \"Hello World\" )\n" +
                      "then\n" +
                      "end\n";

        InternalKieModule kieJar1 = createKieJar( drl1 + drl2 + drl3 );
        InternalKieModule kieJar2 = createKieJar( drl1 + drl3 );

        ChangeSetBuilder builder = new ChangeSetBuilder();
        KieJarChangeSet changes = builder.build( kieJar1, kieJar2 );
        assertEquals( 1, changes.getChanges().size() );

        ResourceChangeSet rcs = changes.getChanges().values().iterator().next();
        assertEquals( 1, rcs.getChanges().size()  );
        assertEquals( ChangeType.REMOVED, rcs.getChanges().get(0).getChangeType() );
    }    
    
    private InternalKieModule createKieJar( String... drls) {
        InternalKieModule kieJar = mock( InternalKieModule.class );
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "hello-world", "1.0-SNAPSHOT");

        List<String> drlFs = new ArrayList<String>();
        
        for( int i=0; i<drls.length; i++ ) {
            if( drls[i] != null ) {
                String fileName = "src/main/resources/org/pkg1/drlFile"+i+".drl";
                drlFs.add( fileName );
                when( kieJar.getBytes( fileName ) ).thenReturn( drls[i].getBytes() );
            }
        }
        when( kieJar.getBytes( KieModuleModelImpl.KMODULE_JAR_PATH ) ).thenReturn( createKieProjectWithPackages(ks, releaseId).toXML().getBytes() );
        when( kieJar.getFileNames() ).thenReturn( drlFs );
        return ( InternalKieModule ) kieJar;
    }
    
    private KieModuleModel createKieProjectWithPackages(KieServices ks, ReleaseId releaseId) {
        KieModuleModel kproj = ks.newKieModuleModel();

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase1")
                .setEqualsBehavior( EqualityBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM )
                .addPackage("org.pkg1");

        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel("KSession1")
                .setType( KieSessionType.STATEFUL )
                .setClockType( ClockTypeOption.get("realtime") );

        return kproj;
    }
    

}
