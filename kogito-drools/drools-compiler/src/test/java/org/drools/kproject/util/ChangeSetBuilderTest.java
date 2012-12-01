package org.drools.kproject.util;

import java.util.Arrays;

import org.junit.Test;
import org.kie.ChangeSet;
import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieFactory;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KieJar;
import org.kie.builder.KieProject;
import org.kie.builder.KieServices;
import org.kie.builder.KieSessionModel;
import org.kie.conf.AssertBehaviorOption;
import org.kie.conf.EventProcessingOption;
import org.kie.io.ResourceFactory;
import org.kie.runtime.conf.ClockTypeOption;
import static org.mockito.Mockito.*;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.matchers.JUnitMatchers.*;

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

        KieJar kieJar1 = createKieJar( drl1, drl2 );
        KieJar kieJar2 = createKieJar( drl1, drl2 );

        ChangeSet changes = ChangeSetBuilder.build( kieJar1, kieJar2 );
        
        assertThat( changes.getResourcesAdded().size(), is(0));
        assertThat( changes.getResourcesRemoved().size(), is(0));
        assertThat( changes.getResourcesModified().size(), is(0));
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

        KieJar kieJar1 = createKieJar( drl1, drl2 );
        KieJar kieJar2 = createKieJar( drl1, drl3 );

        ChangeSet changes = ChangeSetBuilder.build( kieJar1, kieJar2 );
        
        assertThat( changes.getResourcesAdded().size(), is(0));
        assertThat( changes.getResourcesRemoved().size(), is(0));
        assertThat( changes.getResourcesModified().size(), is(1));
        assertThat( changes.getResourcesModified(), hasItem( ResourceFactory.newUrlResource( "jar:file://!/"+kieJar2.getFiles().get( 1 ) ) ) );
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

        KieJar kieJar1 = createKieJar( drl1, drl2 );
        KieJar kieJar2 = createKieJar( drl1 );

        ChangeSet changes = ChangeSetBuilder.build( kieJar1, kieJar2 );
        
        assertThat( changes.getResourcesAdded().size(), is(0));
        assertThat( changes.getResourcesRemoved().size(), is(1));
        assertThat( changes.getResourcesRemoved(), hasItem( ResourceFactory.newUrlResource( "jar:file://!/"+kieJar1.getFiles().get( 1 ) ) ) );
        assertThat( changes.getResourcesModified().size(), is(0));
    }
    
    private KieJar createKieJar( String... drls) {
        KieJar kieJar = mock( KieJar.class );

        String[] drlFs = new String[ drls.length ];
        
        for( int i=0; i<drlFs.length; i++ ) {
            drlFs[i] = "src/main/resoureces/org/pkg1/r"+i+".drl";
        }
        
        KieFactory kf = KieFactory.Factory.get();
        GAV gav = kf.newGav("org.kie", "hello-world", "1.0-SNAPSHOT");

        when( kieJar.getFiles() ).thenReturn( Arrays.asList( drlFs ) );
        for( int i = 0; i < drlFs.length; i++ ) {
            when( kieJar.getBytes( drlFs[i] ) ).thenReturn( drls[i].getBytes() );
        }
        when( kieJar.getBytes( KieProject.KPROJECT_JAR_PATH ) ).thenReturn( createKieProjectWithPackages(kf, gav).toXML().getBytes() );

        return kieJar;
    }
    
    private KieProject createKieProjectWithPackages(KieFactory kf, GAV gav) {
        KieProject kproj = kf.newKieProject()
                .setGroupArtifactVersion(gav);

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase1")
                .setEqualsBehavior( AssertBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM )
                .addPackage("org.pkg1");

        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel("KSession1")
                .setType( "stateful" )
                .setClockType( ClockTypeOption.get("realtime") );

        return kproj;
    }
    

}
