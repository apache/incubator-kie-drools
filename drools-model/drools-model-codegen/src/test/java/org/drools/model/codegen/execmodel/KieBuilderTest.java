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
package org.drools.model.codegen.execmodel;

import org.drools.compiler.kie.builder.impl.DrlProject;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.base.rule.constraint.AlphaNodeFieldConstraint;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.model.codegen.ExecutableModelProject;
import org.drools.modelcompiler.constraints.LambdaConstraint;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class KieBuilderTest {

    @Test
    public void testDrlBuild() throws Exception {
        KieSession ksession = checkKieSession( DrlProject.class );
//        assertTrue( getAlphaConstraint( ksession ) instanceof MVELConstraint );
    }

    @Test
    public void testPatternModelBuild() throws Exception {
        KieSession ksession = checkKieSession( ExecutableModelProject.class );
        assertThat(getAlphaConstraint(ksession) instanceof LambdaConstraint).isTrue();
    }

    private KieSession checkKieSession(Class<? extends KieBuilder.ProjectType> projectClass) {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", createDrl( "R1" ) );
        KieBuilder kieBuilder = ks.newKieBuilder( kfs );

        kieBuilder.buildAll( projectClass );

        KieSession ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();
        ksession.insert("Hello World");

        int count = ksession.fireAllRules();

        assertThat(count).isEqualTo(1);
        return ksession;
    }

    private AlphaNodeFieldConstraint getAlphaConstraint( KieSession ksession ) {
        EntryPointNode epn = ((InternalKnowledgeBase) ksession.getKieBase()).getRete().getEntryPointNodes().values().iterator().next();
        for (ObjectTypeNode otn : epn.getObjectTypeNodes().values()) {
            if (otn.getObjectType().isAssignableFrom( String.class )) {
                AlphaNode alpha = (AlphaNode) otn.getObjectSinkPropagator().getSinks()[0];
                return alpha.getConstraint();
            }
        }
        throw new RuntimeException( "fail" );
    }

    public String createDrl(String ruleName) {
        return "package org.drools.modelcompiler\n" +
                "rule " + ruleName + " when\n" +
                "   String( this == \"Hello World\" )\n" +
                "then\n" +
                "end\n";
    }

    protected KieModuleModel getDefaultKieModuleModel( KieServices ks ) {
        KieModuleModel kproj = ks.newKieModuleModel();
        kproj.newKieBaseModel( "kbase" ).setDefault( true ).newKieSessionModel( "ksession" ).setDefault( true );
        return kproj;
    }
}
