/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.conf.PropertySpecificOption;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class PropertyReactivityTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public PropertyReactivityTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testDisablePropertyReactivity() {
        // DROOLS-5746
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "rule R1 when\n" +
                "    Person( $name: name == \"Mario\" )\n" +
                "    String( this == $name )\n" +
                "then\n" +
                "end\n";


        final ReleaseId releaseId1 = KieServices.get().newReleaseId( "org.kie", "test", "1" );
        final Map<String, String> kieModuleConfigurationProperties = new HashMap<>();
        kieModuleConfigurationProperties.put( PropertySpecificOption.PROPERTY_NAME, PropertySpecificOption.ALLOWED.toString() );

        final KieModule kieModule = KieUtil.getKieModuleFromDrls( releaseId1,
                                                                  kieBaseTestConfiguration,
                                                                  KieSessionTestConfiguration.STATEFUL_REALTIME,
                                                                  kieModuleConfigurationProperties,
                                                                  drl );

        final KieContainer kieContainer = KieServices.get().newKieContainer(kieModule.getReleaseId());
        final KieBase kbase = kieContainer.getKieBase();
        final KieSession ksession = kbase.newKieSession();

        ksession.insert( "Mario" );

        Person me = new Person("Mario", 45);
        FactHandle meFh = ksession.insert( me );
        assertEquals(1, ksession.fireAllRules());

        me.setAge( 46 );
        ksession.update( meFh, me, "age" );
        assertEquals(1, ksession.fireAllRules());
    }
}
