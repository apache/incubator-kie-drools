/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.models.guided.scorecard.backend.test2;

import java.util.List;

import org.drools.workbench.models.guided.scorecard.backend.base.Helper;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.InternalKieBuilder;

import static org.junit.Assert.*;

public class GuidedScoreCardIntegrationJavaClassesAddedToKieFileSystemTest {

    @Test
    public void testEmptyScoreCardCompilation() throws Exception {
        String xml1 = Helper.createEmptyGuidedScoreCardXML();

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( "pom.xml",
                   Helper.getPom() );
        kfs.write( "src/main/resources/META-INF/kmodule.xml",
                   Helper.getKModule() );
        kfs.write( "src/main/java/org/drools/workbench/models/guided/scorecard/backend/test2/Applicant.java",
                   Helper.getApplicant() );
        kfs.write( "src/main/java/org/drools/workbench/models/guided/scorecard/backend/test2/ApplicantAttribute.java",
                   Helper.getApplicantAttribute() );
        kfs.write( "src/main/resources/org/drools/workbench/models/guided/scorecard/backend/test2/sc1.scgd",
                   xml1 );

        //Add complete Score Card
        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        final List<Message> messages = kieBuilder.getResults().getMessages();
        Helper.dumpMessages( messages );
        assertEquals( 0,
                      messages.size() );
    }

    @Test
    public void testCompletedScoreCardCompilation() throws Exception {
        String xml1 = Helper.createGuidedScoreCardXML();

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( "pom.xml",
                   Helper.getPom() );
        kfs.write( "src/main/resources/META-INF/kmodule.xml",
                   Helper.getKModule() );
        kfs.write( "src/main/java/org/drools/workbench/models/guided/scorecard/backend/test2/Applicant.java",
                   Helper.getApplicant() );
        kfs.write( "src/main/java/org/drools/workbench/models/guided/scorecard/backend/test2/ApplicantAttribute.java",
                   Helper.getApplicantAttribute() );
        kfs.write( "src/main/resources/org/drools/workbench/models/guided/scorecard/test2/backend/sc1.scgd",
                   xml1 );

        //Add complete Score Card
        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        final List<Message> messages = kieBuilder.getResults().getMessages();
        Helper.dumpMessages( messages );
        assertEquals( 0,
                      messages.size() );
    }

    @Test
    public void testIncrementalCompilation() throws Exception {
        String xml1_1 = Helper.createEmptyGuidedScoreCardXML();
        String xml1_2 = Helper.createGuidedScoreCardXML();

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( "pom.xml",
                   Helper.getPom() );
        kfs.write( "src/main/resources/META-INF/kmodule.xml",
                   Helper.getKModule() );
        kfs.write( "src/main/java/org/drools/workbench/models/guided/scorecard/backend/test2/Applicant.java",
                   Helper.getApplicant() );
        kfs.write( "src/main/java/org/drools/workbench/models/guided/scorecard/backend/test2/ApplicantAttribute.java",
                   Helper.getApplicantAttribute() );
        kfs.write( "src/main/resources/org/drools/workbench/models/guided/scorecard/backend/test2/sc1.scgd",
                   xml1_1 );

        //Add empty Score Card
        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        final List<Message> messages = kieBuilder.getResults().getMessages();
        Helper.dumpMessages( messages );
        assertEquals( 0,
                      messages.size() );

        //Update with complete Score Card
        kfs.write( "src/main/resources/sc1.scgd",
                   xml1_2 );
        IncrementalResults results = ( (InternalKieBuilder) kieBuilder ).incrementalBuild();

        final List<Message> addedMessages = results.getAddedMessages();
        final List<Message> removedMessages = results.getRemovedMessages();
        Helper.dumpMessages( addedMessages );
        assertEquals( 0,
                      addedMessages.size() );
        Helper.dumpMessages( removedMessages );
        assertEquals( 0,
                      removedMessages.size() );
    }

}
