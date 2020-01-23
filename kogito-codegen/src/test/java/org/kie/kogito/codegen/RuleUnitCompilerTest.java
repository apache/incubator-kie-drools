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

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.api.time.SessionPseudoClock;
import org.kie.kogito.Application;
import org.kie.kogito.codegen.data.Person;
import org.kie.kogito.codegen.rules.multiunit.MultiUnit;
import org.kie.kogito.codegen.unit.AdultUnit;
import org.kie.kogito.codegen.unit.PersonsUnit;
import org.kie.kogito.rules.DataHandle;
import org.kie.kogito.rules.DataObserver;
import org.kie.kogito.rules.DataSource;
import org.kie.kogito.rules.DataStore;
import org.kie.kogito.rules.RuleUnit;
import org.kie.kogito.rules.RuleUnitInstance;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RuleUnitCompilerTest extends AbstractCodegenTest {

    @Test
    public void testRuleUnit() throws Exception {
        Application application = generateCodeRulesOnly("org/kie/kogito/codegen/unit/RuleUnit.drl");

        AdultUnit adults = new AdultUnit();

        adults.getPersons().add(new Person( "Mario", 45 ));
        adults.getPersons().add(new Person( "Marilena", 47 ));

        Person sofia = new Person( "Sofia", 7 );
        DataHandle dhSofia = adults.getPersons().add(sofia);

        RuleUnit<AdultUnit> unit = application.ruleUnits().create(AdultUnit.class);
        RuleUnitInstance<AdultUnit> instance = unit.createInstance(adults);

        assertTrue( instance.getClock() instanceof SessionPseudoClock );

        assertEquals(2, instance.fire() );
        assertTrue( adults.getResults().getResults().containsAll( asList("Mario", "Marilena") ) );

        sofia.setAge( 22 );
        adults.getPersons().update( dhSofia, sofia );
        assertEquals( 1, instance.fire() );
        assertTrue( adults.getResults().getResults().containsAll( asList("Mario", "Marilena", "Sofia") ) );
    }

    @Test
    public void testRuleUnitModify() throws Exception {
        Application application = generateCodeRulesOnly("org/kie/kogito/codegen/unit/RuleUnitModify.drl");

        AdultUnit adults = new AdultUnit();

        Person sofia = new Person( "Sofia", 7 );
        DataHandle dhSofia = adults.getPersons().add(sofia);

        RuleUnit<AdultUnit> unit = application.ruleUnits().create(AdultUnit.class);
        RuleUnitInstance<AdultUnit> instance = unit.createInstance(adults);

        assertEquals(2, instance.fire() );

        assertTrue( adults.getResults().getResults().containsAll( asList("Sofia") ) );
    }

    @Test
    public void testRuleUnitDelete() throws Exception {
        Application application = generateCodeRulesOnly("org/kie/kogito/codegen/unit/RuleUnitDelete.drl");

        AdultUnit adults = new AdultUnit();

        adults.getPersons().add(new Person( "Mario", 45 ));
        adults.getPersons().add(new Person( "Marilena", 47 ));
        adults.getPersons().add(new Person( "Sofia", 7 ));

        RuleUnit<AdultUnit> unit = application.ruleUnits().create(AdultUnit.class);
        RuleUnitInstance<AdultUnit> instance = unit.createInstance(adults);

        instance.fire();

        List<String> results = adults.getResults().getResults();
        assertEquals( 2, results.size() );
        assertTrue( results.containsAll( asList("Mario", "Marilena") ) );
    }

    @Test
    public void testRuleUnitQuery() throws Exception {
        Application application = generateCodeRulesOnly("org/kie/kogito/codegen/unit/RuleUnitQuery.drl");

        AdultUnit adults = new AdultUnit();

        adults.getPersons().add(new Person( "Mario", 45 ));
        adults.getPersons().add(new Person( "Marilena", 47 ));
        adults.getPersons().add(new Person( "Sofia", 7 ));

        RuleUnit<AdultUnit> unit = application.ruleUnits().create(AdultUnit.class);
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
        Application application = generateCodeRulesOnly("org/kie/kogito/codegen/unit/RuleUnitQuery.drl");

        AdultUnit adults = new AdultUnit();

        adults.getPersons().add(new Person( "Mario", 45 ));
        adults.getPersons().add(new Person( "Marilena", 47 ));
        adults.getPersons().add(new Person( "Sofia", 7 ));

        RuleUnit<AdultUnit> unit = application.ruleUnits().create(AdultUnit.class);
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
        Application application = generateCodeRulesOnly(
                "org/kie/kogito/codegen/unit/RuleUnit.drl",
                "org/kie/kogito/codegen/unit/PersonsUnit.drl");

        DataStore<Person> persons = DataSource.createStore();
        persons.add(new Person( "Mario", 45 ));
        persons.add(new Person( "Marilena", 17 ));
        persons.add(new Person( "Sofia", 7 ));

        RuleUnit<AdultUnit> adultUnit = application.ruleUnits().create(AdultUnit.class);

        AdultUnit adultData18 = new AdultUnit(persons, 18);
        RuleUnitInstance<AdultUnit> adultUnitInstance18 = adultUnit.createInstance(adultData18, "adult18");

        AdultUnit adultData21 = new AdultUnit(persons, 21);
        RuleUnitInstance<AdultUnit> adultUnitInstance21 = adultUnit.createInstance(adultData21, "adult21");

        RuleUnit<PersonsUnit> personsUnit = application.ruleUnits().create(PersonsUnit.class);
        personsUnit.createInstance( new PersonsUnit(persons) ).fire();

        assertEquals( 2, adultData18.getResults().getResults().size() );
        assertTrue( adultData18.getResults().getResults().containsAll( asList("Mario", "Marilena") ) );
        assertEquals( 1, adultData21.getResults().getResults().size() );
        assertTrue( adultData21.getResults().getResults().containsAll( asList("Mario") ) );
    }

    @Test
    public void generateSinglePackageSingleUnit() throws Exception {
        Application application = generateCodeRulesOnly(
                        "org/kie/kogito/codegen/rules/multiunit/MultiUnit.drl",
                        "org/kie/kogito/codegen/rules/multiunit/MultiUnit2.drl");

        ArrayList<String> strings = new ArrayList<>();

        RuleUnit<MultiUnit> mu = application.ruleUnits().create(MultiUnit.class);
        MultiUnit data = new MultiUnit();
        RuleUnitInstance<MultiUnit> instance = mu.createInstance(data);
        data.getValues().subscribe(DataObserver.of(v -> { if (v!=null) strings.add((String) v); }));
        data.getValues().add("start");
        instance.fire();

        assertEquals(asList("start", "middle", "done"), strings);

    }
}
