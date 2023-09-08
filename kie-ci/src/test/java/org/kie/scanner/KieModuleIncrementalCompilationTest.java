/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.scanner;

import java.util.Collection;
import java.util.HashMap;

import org.drools.drl.parser.MessageImpl;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.InternalKieBuilder;

import static org.assertj.core.api.Assertions.assertThat;

public class KieModuleIncrementalCompilationTest extends AbstractKieCiTest {

    @Test
    public void testCheckMetaDataAfterIncrementalDelete() throws Exception {
        String drl1 = "package org.kie.scanner\n" +
                "rule R1 when\n" +
                "   String()\n" +
                "then\n" +
                "end\n";

        String drl2 = "package org.kie.scanner\n" +
                "rule R2_2 when\n" +
                "   String( )\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem()
                .write( "src/main/resources/r1.drl", drl1 )
                .write( "src/main/resources/r2.drl", drl2 );

        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();

        assertThat(getRuleNames(kieBuilder).get("org.kie.scanner").size()).isEqualTo(2);

        kfs.delete( "src/main/resources/r2.drl" );

        IncrementalResults addResults = ( (InternalKieBuilder) kieBuilder ).createFileSet( "src/main/resources/r2.drl" ).build();

        assertThat(getRuleNames(kieBuilder).get("org.kie.scanner").size()).isEqualTo(1);
    }

    private HashMap<String, Collection<String>> getRuleNames( KieBuilder kieBuilder ) {
        KieModuleMetaData kieModuleMetaData = getKieModuleMetaData( kieBuilder );
        HashMap<String, Collection<String>> ruleNames = new HashMap<String, Collection<String>>();

        for ( String packageName : kieModuleMetaData.getPackages() ) {
            ruleNames.put( packageName, kieModuleMetaData.getRuleNamesInPackage( packageName ) );
        }

        return ruleNames;
    }

    private KieModuleMetaData getKieModuleMetaData( KieBuilder kieBuilder ) {
        return KieModuleMetaData.Factory.newKieModuleMetaData( ( (InternalKieBuilder) kieBuilder ).getKieModuleIgnoringErrors() );
    }

    @Test
    public void testIncrementalCompilationFirstBuildHasErrors() throws Exception {
        KieServices ks = KieServices.Factory.get();

        //Malformed POM - No Version information
        ReleaseId releaseId = ks.newReleaseId( "org.kie", "incremental-test-with-invalid pom", "" );

        KieFileSystem kfs = createKieFileSystemWithKProject( ks );
        kfs.writePomXML( getPom( releaseId ) );

        //Valid
        String drl1 =
                "rule R1 when\n" +
                        "   $s : String()\n" +
                        "then\n" +
                        "end\n";

        //Invalid
        String drl2 =
                "rule R2 when\n" +
                        "   $s : Strin( )\n" +
                        "then\n" +
                        "end\n";

        //Write Rule 1 - No DRL errors, but POM is in error
        kfs.write( "src/main/resources/KBase1/r1.drl", drl1 );
        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        assertThat(kieBuilder.getResults().getMessages(org.kie.api.builder.Message.Level.ERROR).size()).isEqualTo(1);

        //Add file with error - expect 1 "added" error message
        kfs.write( "src/main/resources/KBase1/r2.drl", drl2 );
        IncrementalResults addResults = ( (InternalKieBuilder) kieBuilder ).createFileSet( "src/main/resources/KBase1/r2.drl" ).build();

        assertThat(addResults.getAddedMessages().size()).isEqualTo(1);
        assertThat(addResults.getRemovedMessages().size()).isEqualTo(0);
    }

    @Test
    public void checkIncrementalCompilationWithRuleFunctionRule() throws Exception {
        String rule_1 = "package org.kie.scanner\n" +
                "rule R1 when\n" +
                "   String()\n" +
                "then\n" +
                "end\n";

        String rule_2 = "package org.kie.scanner\n" +
                "rule R1 when\n" +
                "   String()\n" +
                "then\n" +
                "   System.out.println(MyFunction());\n" +
                "end\n";

        String function = "package org.kie.scanner\n" +
                "function int MyFunction() {\n" +
                "   return 1;\n" +
                "}\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write( "src/main/resources/org/kie/scanner/rule.drl", rule_1 );
        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        assertThat(kieBuilder.getResults().getMessages(org.kie.api.builder.Message.Level.ERROR).size()).isEqualTo(0);

        kfs.write( "src/main/resources/org/kie/scanner/function.drl", function );
        IncrementalResults addResults1 = ( (InternalKieBuilder) kieBuilder ).createFileSet( "src/main/resources/org/kie/scanner/function.drl" ).build();
        assertThat(addResults1.getAddedMessages().size()).isEqualTo(0);
        assertThat(addResults1.getRemovedMessages().size()).isEqualTo(0);

        kfs.write( "src/main/resources/org/kie/scanner/rule.drl", rule_2 );
        IncrementalResults addResults2 = ( (InternalKieBuilder) kieBuilder ).createFileSet( "src/main/resources/org/kie/scanner/rule.drl" ).build();
        assertThat(addResults2.getAddedMessages().size()).isEqualTo(0);
        assertThat(addResults2.getRemovedMessages().size()).isEqualTo(0);
    }

    @Test
    public void checkIncrementalCompilationWithRuleThenFunction() throws Exception {
        String rule = "package org.kie.scanner\n" +
                "rule R1 when\n" +
                "   String()\n" +
                "then\n" +
                "   System.out.println(MyFunction());\n" +
                "end\n";

        String function = "package org.kie.scanner\n" +
                "function int MyFunction() {\n" +
                "   return 1;\n" +
                "}\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write( "src/main/resources/org/kie/scanner/rule.drl", rule );
        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        assertThat(kieBuilder.getResults().getMessages(org.kie.api.builder.Message.Level.ERROR).size()).isEqualTo(1);

        kfs.write( "src/main/resources/org/kie/scanner/function.drl", function );
        IncrementalResults addResults1 = ( (InternalKieBuilder) kieBuilder ).createFileSet( "src/main/resources/org/kie/scanner/function.drl" ).build();
        assertThat(addResults1.getAddedMessages().size()).isEqualTo(0);
        assertThat(addResults1.getRemovedMessages().size()).isEqualTo(1);
    }

    @Test
    public void checkIncrementalCompilationWithFunctionThenRule() throws Exception {
        String rule = "package org.kie.scanner\n" +
                "rule R1 when\n" +
                "   String()\n" +
                "then\n" +
                "   System.out.println(MyFunction());\n" +
                "end\n";

        String function = "package org.kie.scanner\n" +
                "function int MyFunction() {\n" +
                "   return 1;\n" +
                "}\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write( "src/main/resources/org/kie/scanner/function.drl", function );
        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        assertThat(kieBuilder.getResults().getMessages(org.kie.api.builder.Message.Level.ERROR).size()).isEqualTo(0);

        kfs.write( "src/main/resources/org/kie/scanner/rule.drl", rule );
        IncrementalResults addResults = ( (InternalKieBuilder) kieBuilder ).createFileSet( "src/main/resources/org/kie/scanner/rule.drl" ).build();
        assertThat(addResults.getAddedMessages().size()).isEqualTo(0);
        assertThat(addResults.getRemovedMessages().size()).isEqualTo(0);
    }

    @Test
    public void checkIncrementalCompilationWithMultipleKieBases() throws Exception {
        String rule = "package org.kie.scanner\n" +
                      "rule R1 when\n" +
                      "then\n" +
                      "end\n";

        String invalidRule = "package org.kie.scanner\n" +
                             "rule R2 when\n" +
                             "   Cheese()\n" + // missing import
                             "then\n" +
                             "end\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = createKieFileSystemWithTwoKBases(ks);

        kfs.write("src/main/resources/org/kie/scanner/rule.drl",
                  rule);
        KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
        assertThat(kieBuilder.getResults().getMessages().size()).isEqualTo(0);

        kfs.write("src/main/resources/org/kie/scanner/invalidRule.drl",
                  invalidRule);
        IncrementalResults addResults = ((InternalKieBuilder) kieBuilder).createFileSet("src/main/resources/org/kie/scanner/invalidRule.drl").build();

        assertThat(addResults.getAddedMessages().size()).isEqualTo(2);
        addResults
                .getAddedMessages()
                .stream()
                .map(m -> (MessageImpl) m )
                .forEach(m -> assertThat(m.getKieBaseName()).isNotNull());
    }

    private KieFileSystem createKieFileSystemWithTwoKBases(final KieServices ks) {
        final KieModuleModel kproj = ks.newKieModuleModel();

        kproj.newKieBaseModel("default").setDefault(true)
             .setEqualsBehavior( EqualityBehaviorOption.EQUALITY )
             .setEventProcessingMode( EventProcessingOption.STREAM );

        kproj.newKieBaseModel("kbase1").setDefault(false)
             .setEqualsBehavior(EqualityBehaviorOption.EQUALITY)
             .setEventProcessingMode(EventProcessingOption.STREAM);

        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(kproj.toXML());
        return kfs;
    }
}
