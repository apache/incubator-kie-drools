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

import java.util.Collection;

import org.drools.compiler.kie.builder.impl.DrlProject;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.model.Result;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.io.KieResources;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class MultiSheetsTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public MultiSheetsTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testNoSheet() {
        check(null, "Mario can drink");
    }

    @Test
    public void testSheet1() {
        check("Sheet1", "Mario can drink");
    }

    @Test
    public void testSheet2() {
        check("Sheet2", "Mario can drive");
    }

    @Test
    public void testSheet12() {
        check("Sheet1,Sheet2", "Mario can drink", "Mario can drive");
    }

    private void check(String sheets, String... results) {
        KieServices ks = KieServices.get();
        KieResources kr = ks.getResources();

        KieFileSystem kfs = ks.newKieFileSystem()
                .write( "src/main/resources/org/drools/simple/candrink/CanDrink.drl.xls",
                        kr.newFileSystemResource( "src/test/resources/data/CanDrinkAndDrive.drl.xls" ) )
                .write( "src/main/resources/org/drools/simple/candrink/CanDrink.drl.xls.properties",
                        sheets != null ? "sheets="+sheets : "" );

        KieModuleModel kproj = ks.newKieModuleModel();
        kproj.newKieBaseModel("dtblaleKB")
                .addPackage("org.drools.simple.candrink")
                .newKieSessionModel("dtable");

        kfs.writeKModuleXML( kproj.toXML() );

        KieBuilder kb = buildDTable( ks, kfs );
        KieContainer kc = ks.newKieContainer(kb.getKieModule().getReleaseId());

        KieSession sessionDtable = kc.newKieSession( "dtable" );
        Result result = new Result();
        sessionDtable.insert( result );
        sessionDtable.insert( new Person("Mario", 45) );
        sessionDtable.fireAllRules();
        for (String r : results) {
            assertThat(result.toString().contains(r)).isTrue();
        }
    }

    private KieBuilder buildDTable( KieServices ks, KieFileSystem kfs ) {
        if (kieBaseTestConfiguration.getExecutableModelProjectClass().isPresent()) {
            return ks.newKieBuilder( kfs ).buildAll(kieBaseTestConfiguration.getExecutableModelProjectClass().get());
        } else {
            return ks.newKieBuilder( kfs ).buildAll( DrlProject.class);
        }
    }
}
