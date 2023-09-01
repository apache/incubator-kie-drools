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
package org.drools.compiler.integrationtests;

import java.util.ArrayList;
import java.util.Collection;

import org.drools.io.ByteArrayResource;
import org.drools.model.codegen.ExecutableModelProject;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;

import static org.assertj.core.api.Assertions.assertThat;

public class BigRuleSetCompilationTest {

    @Test
    public void test() {
        KieServices kieService = KieServices.Factory.get();
        KieFileSystem kfs = kieService.newKieFileSystem();

        ReleaseId rid = kieService.newReleaseId("org.drools.test", "big-rule-set", "1.0.0");
        kfs.generateAndWritePomXML(rid);

        // This test is used to check that compilation of large knowledge bases doesn't cause an Out Of Memory
        // We are not running the test with such a big kbase by default to avoid slowing down CI
        // generateResourcesToBeCompiled( 17, 1000 ).forEach( kfs::write );
        generateResourcesToBeCompiled( 2, 3 ).forEach( kfs::write );

        KieBuilder kbuilder = kieService.newKieBuilder(kfs);

        kbuilder.buildAll( ExecutableModelProject.class );
        assertThat(kbuilder.getResults().getMessages().isEmpty()).isTrue();
    }

    private Collection<Resource> generateResourcesToBeCompiled( int numberOfResources, int rulesPerResource) {
        Collection<Resource> resources = new ArrayList<>();
        for (int i = 0; i < numberOfResources; i++) {
            Resource resource = new ByteArrayResource( generateRules( "org.kie.kogito.codegen.test" + i, rulesPerResource ).getBytes() );
            resource.setResourceType( ResourceType.DRL );
            resource.setSourcePath( "org/kie/kogito/codegen/test" + i + "/rules.drl" );
            resources.add(resource);
        }
        return resources;
    }

    private String generateRules(String packageName, int n) {
        StringBuilder sb = new StringBuilder( "package " + packageName + "\n" );
        for (int i = 0; i < n; i++) {
            sb.append( generateRule( i ) );
        }
        return sb.toString();
    }

    private String generateRule(int seed) {
        return "rule R" + seed + " when\n" +
                "  $i: Integer( this == " + seed + " )\n" +
                "  $s: String( this == $i.toString() )\n" +
                "then\n" +
                "  System.out.println($s);\n" +
                "end\n";
    }
}
