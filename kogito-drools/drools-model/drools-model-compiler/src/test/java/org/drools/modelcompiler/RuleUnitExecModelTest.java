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

import java.util.List;

import org.drools.model.Global;
import org.drools.model.Model;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.model.impl.ModelImpl;
import org.drools.modelcompiler.builder.KieBaseBuilder;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.ruleunit.AdultUnit;
import org.drools.modelcompiler.ruleunit.AdultUnitInstance;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.kogito.rules.DataSource;
import org.kie.kogito.rules.impl.ListDataStream;

import static java.util.Arrays.asList;

import static org.drools.model.DSL.globalOf;
import static org.drools.model.FlowDSL.declarationOf;
import static org.drools.model.FlowDSL.expr;
import static org.drools.model.FlowDSL.on;
import static org.drools.model.FlowDSL.rule;
import static org.drools.model.FlowDSL.unitData;
import static org.junit.Assert.assertTrue;

public class RuleUnitExecModelTest {

    @Test
    public void testRuleUnit() {

        Global<List> var_results = globalOf(List.class,
                                            "org.drools.modelcompiler.ruleunit",
                                            "results");

        Variable<Person> adult = declarationOf( Person.class, unitData( "persons" ) );

        Rule rule = rule( "org.drools.modelcompiler.ruleunit", "Adult" ).unit( AdultUnit.class )
                .build(
                        expr("$expr$1$", adult, p -> p.getAge() > 18),
                        on(adult, var_results).execute((p, results) -> {
                            System.out.println( p.getName() );
                            results.add( p.getName() );
                        })
                );

        Model model = new ModelImpl().addRule( rule ).addGlobal( var_results );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        DataSource<Person> persons = new ListDataStream<>(
                new Person( "Mario", 43 ),
                new Person( "Marilena", 44 ),
                new Person( "Sofia", 5 ) );

        AdultUnit unit = new AdultUnit( persons );

        AdultUnitInstance unitInstance = new AdultUnitInstance( unit, kieBase.newKieSession() );

        unitInstance.fire();

        assertTrue( unit.getResults().containsAll( asList("Mario", "Marilena") ) );
    }

}
