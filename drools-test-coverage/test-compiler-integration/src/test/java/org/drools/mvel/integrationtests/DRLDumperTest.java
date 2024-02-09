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
package org.drools.mvel.integrationtests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.drl.parser.DrlParser;
import org.drools.drl.parser.DroolsError;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.io.InputStreamResource;
import org.drools.mvel.DrlDumper;
import org.drools.mvel.compiler.Cheese;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.conf.LanguageLevelOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(Parameterized.class)
public class DRLDumperTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public DRLDumperTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    private static Logger logger = LoggerFactory.getLogger( DRLDumperTest.class );

    @Test
    public void testDumpers() throws Exception {
        final DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        final Resource resource = new InputStreamResource(getClass().getResourceAsStream("drl/test_Dumpers.drl"));
        final PackageDescr pkg = parser.parse(resource);

        if (parser.hasErrors()) {
            for (final DroolsError error : parser.getErrors()) {
                logger.warn(error.toString());
            }
            fail(parser.getErrors().toString());
        }

        final Resource descrResource = KieServices.Factory.get().getResources().newDescrResource(pkg);
        descrResource.setSourcePath(TestConstants.TEST_RESOURCES_FOLDER + "test_Dumpers.descr");
        KieBase kbase = KieBaseUtil.getKieBaseFromResources(kieBaseTestConfiguration, descrResource);
        KieSession ksession = kbase.newKieSession();

        List list = new ArrayList();
        ksession.setGlobal("list", list);

        final Cheese brie = new Cheese("brie", 12);
        ksession.insert(brie);

        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(3);
        assertThat(list.get(0)).isEqualTo("3 1");
        assertThat(list.get(1)).isEqualTo("MAIN");
        assertThat(list.get(2)).isEqualTo("1 1");

        //---------------------------
        
        final DrlDumper drlDumper = new DrlDumper();
        final String drlResult = drlDumper.dump(pkg);

        System.out.println(drlResult);

        kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drlResult);
        ksession = kbase.newKieSession();

        list = new ArrayList();
        ksession.setGlobal("list", list);

        ksession.insert(brie);

        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(3);
        assertThat(list.get(0)).isEqualTo("3 1");
        assertThat(list.get(1)).isEqualTo("MAIN");
        assertThat(list.get(2)).isEqualTo("1 1");
    }

}
