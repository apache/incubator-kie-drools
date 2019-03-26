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
 *
 */

package org.drools.example.api.reactivekiesession;

import org.drools.example.api.ruleunit.Person;
import org.drools.example.api.ruleunit.PersonRuleUnit;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.rule.DataSource;
import org.kie.api.runtime.rule.RuleUnitExecutor;

import static org.junit.Assert.assertEquals;

public class RuleUnitExampleTest {

    @Test
    public void testGo() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kcontainer = ks.getKieClasspathContainer();
        RuleUnitExecutor executor = RuleUnitExecutor.create().bind( kcontainer.getKieBase() );

        DataSource<Person> persons = executor.newDataSource( "persons",
                                                             new Person( "Mario", 42 ) );

        assertEquals(1, executor.run( PersonRuleUnit.class ) );

        persons.insert( new Person( "Sofia", 4 ) );
        assertEquals(0, executor.run( PersonRuleUnit.class ));

        persons.insert( new Person( "Marilena", 44 ) );
        assertEquals(1, executor.run( PersonRuleUnit.class ));
    }
}
