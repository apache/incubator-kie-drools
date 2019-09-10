/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.codegen;

import java.util.List;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;
import org.kie.kogito.Executor;
import org.kie.kogito.codegen.data.AdultUnit;
import org.kie.kogito.codegen.data.Person;
import org.kie.kogito.rules.DataHandle;
import org.kie.kogito.rules.RuleUnit;
import org.kie.kogito.rules.RuleUnitInstance;
import org.kie.kogito.rules.impl.RuleUnitRegistry;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RuleUnitCompilerTest extends AbstractCodegenTest {

    @Test
    public void testRuleUnit() throws Exception {
        generateCodeRulesOnly("org/kie/kogito/codegen/data/RuleUnit.drl");

        AdultUnit adults = new AdultUnit();

        adults.getPersons().add(new Person( "Mario", 45 ));
        adults.getPersons().add(new Person( "Marilena", 47 ));

        Person sofia = new Person( "Sofia", 7 );
        DataHandle dhSofia = adults.getPersons().add(sofia);

        RuleUnit<AdultUnit> unit = RuleUnitRegistry.create(AdultUnit.class);
        RuleUnitInstance<AdultUnit> instance = unit.createInstance(adults);

        assertEquals(2, instance.fire() );
        assertTrue( adults.getResults().getResults().containsAll( asList("Mario", "Marilena") ) );

        sofia.setAge( 22 );
        adults.getPersons().update( dhSofia, sofia );
        assertEquals( 1, instance.fire() );
        assertTrue( adults.getResults().getResults().containsAll( asList("Mario", "Marilena", "Sofia") ) );
    }

    @Test
    public void testRuleUnitModify() throws Exception {
        generateCodeRulesOnly("org/kie/kogito/codegen/data/RuleUnitModify.drl");

        AdultUnit adults = new AdultUnit();

        Person sofia = new Person( "Sofia", 7 );
        DataHandle dhSofia = adults.getPersons().add(sofia);

        RuleUnit<AdultUnit> unit = RuleUnitRegistry.create(AdultUnit.class);
        RuleUnitInstance<AdultUnit> instance = unit.createInstance(adults);

        assertEquals(2, instance.fire() );

        assertTrue( adults.getResults().getResults().containsAll( asList("Sofia") ) );
    }

    @Test
    public void testRuleUnitQuery() throws Exception {
        generateCodeRulesOnly("org/kie/kogito/codegen/data/RuleUnitQuery.drl");

        AdultUnit adults = new AdultUnit();

        adults.getPersons().add(new Person( "Mario", 45 ));
        adults.getPersons().add(new Person( "Marilena", 47 ));
        adults.getPersons().add(new Person( "Sofia", 7 ));

        RuleUnit<AdultUnit> unit = RuleUnitRegistry.create(AdultUnit.class);
        RuleUnitInstance<AdultUnit> instance = unit.createInstance(adults);

        List<String> results = instance.executeQuery( "FindAdults" )
                .stream()
                .map( m -> m.get("$name") )
                .map( String.class::cast )
                .collect( toList() );

        assertEquals( 2, results.size() );
        assertTrue( results.containsAll( asList("Mario", "Marilena") ) );
    }

    @Test
    public void testRuleUnitQueryOnPrimitive() throws Exception {
        generateCodeRulesOnly("org/kie/kogito/codegen/data/RuleUnitQuery.drl");

        AdultUnit adults = new AdultUnit();

        adults.getPersons().add(new Person( "Mario", 45 ));
        adults.getPersons().add(new Person( "Marilena", 47 ));
        adults.getPersons().add(new Person( "Sofia", 7 ));

        RuleUnit<AdultUnit> unit = RuleUnitRegistry.create(AdultUnit.class);
        RuleUnitInstance<AdultUnit> instance = unit.createInstance(adults);

        List<Integer> results = instance.executeQuery( "FindAdultsAge" )
                .stream()
                .map( m -> m.get("$age") )
                .map( Integer.class::cast )
                .collect( toList() );

        assertEquals( 2, results.size() );
        assertTrue( results.containsAll( asList(45, 47) ) );
    }

    @Test
    public void testRuleUnitExecutor() throws Exception {
        generateCodeRulesOnly("org/kie/kogito/codegen/data/RuleUnit.drl");

        AdultUnit adults = new AdultUnit();

        adults.getPersons().add(new Person( "Mario", 45 ));
        adults.getPersons().add(new Person( "Marilena", 47 ));
        adults.getPersons().add(new Person( "Sofia", 7 ));

        RuleUnitInstance<AdultUnit> instance = RuleUnitRegistry.instance(adults);
        Executor executor = Executor.create();
        Future<Integer> done = executor.submit(instance);

        assertEquals(2, done.get().intValue() );

        assertTrue( adults.getResults().getResults().containsAll( asList("Mario", "Marilena") ) );
    }
}
