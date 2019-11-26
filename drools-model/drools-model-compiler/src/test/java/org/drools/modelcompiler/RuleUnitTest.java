/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler;

import java.util.ArrayList;
import java.util.List;

import org.drools.model.Model;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.model.impl.ModelImpl;
import org.drools.modelcompiler.builder.KieBaseBuilder;
import org.drools.modelcompiler.domain.Person;
import org.junit.Test;
import org.kie.api.KieBase;
import org.drools.ruleunit.DataSource;
import org.drools.ruleunit.RuleUnit;
import org.drools.ruleunit.RuleUnitExecutor;

import static java.util.Arrays.asList;

import static org.drools.model.FlowDSL.declarationOf;
import static org.drools.model.FlowDSL.expr;
import static org.drools.model.FlowDSL.on;
import static org.drools.model.FlowDSL.rule;
import static org.drools.model.FlowDSL.unitData;
import static org.junit.Assert.assertTrue;

public class RuleUnitTest {

    public static class AdultUnit implements RuleUnit {
        private List<String> results = new ArrayList<String>();
        private int adultAge = 18;
        private DataSource<Person> persons;

        public AdultUnit( ) { }

        public AdultUnit( DataSource<Person> persons ) {
            this.persons = persons;
        }

        public DataSource<Person> getPersons() {
            return persons;
        }

        public int getAdultAge() {
            return adultAge;
        }

        public List<String> getResults() {
            return results;
        }
    }

    @Test
    public void testRuleUnit() {
        List<String> result = new ArrayList<>();

        Variable<Person> adult = declarationOf( Person.class, unitData( "persons" ) );

        Rule rule = rule( "org.drools.retebuilder", "Adult" ).unit( AdultUnit.class )
                .build(
                        expr("$expr$1$", adult, p -> p.getAge() > 18),
                        on(adult).execute(p -> {
                            System.out.println( p.getName() );
                            result.add( p.getName() );
                        })
                );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        RuleUnitExecutor executor = RuleUnitExecutor.create().bind( kieBase );

        executor.newDataSource( "persons",
                new Person( "Mario", 43 ),
                new Person( "Marilena", 44 ),
                new Person( "Sofia", 5 ) );

        executor.run( AdultUnit.class );

        assertTrue( result.containsAll( asList("Mario", "Marilena") ) );
    }

    @Test
    public void testRuleUnitWithVarBinding() {
        Variable<AdultUnit> unit = declarationOf( AdultUnit.class );
        Variable<Person> adult = declarationOf( Person.class, unitData( "persons" ) );

        Rule rule = rule( "org.drools.retebuilder", "Adult" ).unit( RuleUnitTest.AdultUnit.class )
                .build(
                        expr("$expr$1$", adult, unit, (p, u) -> p.getAge() > u.getAdultAge()),
                        on(adult, unitData(List.class, "results")).execute((p, r) -> {
                            System.out.println( p.getName() );
                            r.add( p.getName() );
                        })
                );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        RuleUnitExecutor executor = RuleUnitExecutor.create().bind( kieBase );

        executor.newDataSource( "persons",
                new Person( "Mario", 43 ),
                new Person( "Marilena", 44 ),
                new Person( "Sofia", 5 ) );

        AdultUnit ruleUnit = new AdultUnit();
        executor.run( ruleUnit );

        assertTrue( ruleUnit.getResults().containsAll( asList("Mario", "Marilena") ) );
    }
}
