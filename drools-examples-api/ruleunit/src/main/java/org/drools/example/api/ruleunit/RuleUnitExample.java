/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.example.api.ruleunit;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.drools.ruleunit.DataSource;
import org.drools.ruleunit.RuleUnitExecutor;

public class RuleUnitExample {

    public static void main(String[] args) {
        new RuleUnitExample().go();
    }

    public void go() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kcontainer = ks.getKieClasspathContainer();

        RuleUnitExecutor executor = RuleUnitExecutor.create().bind( kcontainer.getKieBase() );

        DataSource<Person> persons = executor.newDataSource( "persons",
                                                             new Person( "Mario", 42 ) );

        executor.run( PersonRuleUnit.class );

        persons.insert( new Person( "Sofia", 4 ) );
        executor.run( PersonRuleUnit.class );

        persons.insert( new Person( "Marilena", 44 ) );
        executor.run( PersonRuleUnit.class );
    }
}
