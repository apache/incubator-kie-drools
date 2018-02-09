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

import org.assertj.core.api.Assertions;
import org.drools.model.Global;
import org.drools.model.Model;
import org.drools.model.QueryDef;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.model.impl.ModelImpl;
import org.drools.modelcompiler.builder.KieBaseBuilder;
import org.drools.modelcompiler.domain.Child;
import org.drools.modelcompiler.domain.InternationalAddress;
import org.drools.modelcompiler.domain.Man;
import org.drools.modelcompiler.domain.Toy;
import org.drools.modelcompiler.domain.Woman;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;

import static org.drools.model.DSL.accFunction;
import static org.drools.model.DSL.accumulate;
import static org.drools.model.DSL.and;
import static org.drools.model.DSL.declarationOf;
import static org.drools.model.DSL.expr;
import static org.drools.model.DSL.from;
import static org.drools.model.DSL.globalOf;
import static org.drools.model.DSL.input;
import static org.drools.model.DSL.on;
import static org.drools.model.DSL.query;
import static org.drools.model.DSL.reactiveFrom;
import static org.drools.model.DSL.rule;
import static org.junit.Assert.assertEquals;

public class OOPathFlowTest {

    @Test
    public void testOOPath() {
        Global<List> listG = globalOf(List.class, "defaultpkg", "list");
        Variable<Man> manV = declarationOf( Man.class );
        Variable<Woman> wifeV = declarationOf( Woman.class, reactiveFrom( manV, Man::getWife ) );
        Variable<Child> childV = declarationOf( Child.class, reactiveFrom( wifeV, Woman::getChildren ) );

        Rule rule = rule( "oopath" )
                .build(
                        expr("exprA", childV, c -> c.getAge() > 10),
                        on(manV, listG).execute( (t, l) -> l.add(t.getName()) )
                );

        Model model = new ModelImpl().addGlobal( listG ).addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );
        KieSession ksession = kieBase.newKieSession();

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Woman alice = new Woman( "Alice", 38 );
        final Man bob = new Man( "Bob", 40 );
        final Man carl = new Man( "Bob", 42 );
        bob.setWife( alice );

        final Child charlie = new Child( "Charles", 12 );
        final Child debbie = new Child( "Debbie", 10 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        ksession.insert( bob );
        ksession.insert( carl );
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("Bob");

    }

    @Test
    public void testReactiveOOPath() {
        Global<List> listG = globalOf(List.class, "defaultpkg", "list");
        Variable<Man> manV = declarationOf( Man.class );
        Variable<Woman> wifeV = declarationOf( Woman.class, reactiveFrom( manV, Man::getWife ) );
        Variable<Child> childV = declarationOf( Child.class, reactiveFrom( wifeV, Woman::getChildren ) );
        Variable<Toy> toyV = declarationOf( Toy.class, reactiveFrom( childV, Child::getToys ) );

        Rule rule = rule( "oopath" )
                .build(
                        expr("exprA", childV, c -> c.getAge() > 10),
                        on(toyV, listG).execute( (t, l) -> l.add(t.getName()) )
                );

        Model model = new ModelImpl().addGlobal( listG ).addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );
        KieSession ksession = kieBase.newKieSession();

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Woman alice = new Woman( "Alice", 38 );
        final Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        final Child charlie = new Child( "Charles", 12 );
        final Child debbie = new Child( "Debbie", 10 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("car", "ball");

        list.clear();
        debbie.setAge( 11 );
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("doll");
    }

    @Test
    public void testBackReferenceConstraint() {
        Global<List> listG = globalOf(List.class, "defaultpkg", "list");
        Variable<Man> manV = declarationOf( Man.class );
        Variable<Woman> wifeV = declarationOf( Woman.class, reactiveFrom( manV, Man::getWife ) );
        Variable<Child> childV = declarationOf( Child.class, reactiveFrom( wifeV, Woman::getChildren ) );
        Variable<Toy> toyV = declarationOf( Toy.class, reactiveFrom( childV, Child::getToys ) );

        Rule rule = rule( "oopath" )
                .build(
                        expr("exprA", toyV, childV, (t, c) -> t.getName().length() == c.getName().length()),
                        on(toyV, listG).execute( (t, l) -> l.add(t.getName()) )
                );

        Model model = new ModelImpl().addGlobal( listG ).addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );
        KieSession ksession = kieBase.newKieSession();

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Woman alice = new Woman( "Alice", 38 );
        final Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        final Child charlie = new Child( "Carl", 12 );
        final Child debbie = new Child( "Debbie", 8 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );
        debbie.addToy( new Toy( "guitar" ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("ball", "guitar");
    }

    @Test
    public void testOOPathCast() {
        Global<List> listG = globalOf(List.class, "defaultpkg", "list");
        Variable<Man> manV = declarationOf(Man.class, "$man");
        // Drools doc: In this way subsequent constraints can also safely access to properties declared only in that subclass 
        Variable<InternationalAddress> addressV = declarationOf(InternationalAddress.class, "$italy", reactiveFrom(manV, Man::getAddress));

        Rule rule = rule("oopath")
                                  .build(
                                         expr("exprA", addressV, c -> c.getState().equals("Italy")),
                                         on(manV, listG).execute((t, l) -> l.add(t.getName())));

        Model model = new ModelImpl().addGlobal(listG).addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();

        final List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        final Man bob = new Man("Bob", 40);
        bob.setAddress(new InternationalAddress("Via Verdi", "Italy"));
        ksession.insert(bob);
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("Bob");

    }

    @Test
    public void testQueryOOPathAccumulate() {
        QueryDef queryDef_listSafeCities = query("listSafeCities");
        Variable<java.util.List> var_$cities = declarationOf(java.util.List.class, "$cities");
        Variable<org.drools.modelcompiler.oopathdtables.Person> var_$p = declarationOf(org.drools.modelcompiler.oopathdtables.Person.class,
                "$p");
        Variable<org.drools.modelcompiler.oopathdtables.InternationalAddress> var_$a = declarationOf(org.drools.modelcompiler.oopathdtables.InternationalAddress.class,
                "$a",
                from(var_$p,
                        (_this) -> _this.getAddress()));
        Variable<String> var_$city = declarationOf(String.class, "$city",
                from(var_$a,
                        (_this) -> _this.getCity()));
        org.drools.model.Query listSafeCities_build = queryDef_listSafeCities.build(accumulate(and(input(var_$p),
                expr("$expr$2$",
                        var_$a,
                        (_this) -> org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(_this.getState(),
                                "Safecountry")).indexedBy(java.lang.String.class,
                        org.drools.model.Index.ConstraintType.EQUAL,
                        0,
                        _this -> _this.getState(),
                        "Safecountry")
                        .reactOn("state"),
                expr(var_$city)),
                accFunction(org.drools.core.base.accumulators.CollectListAccumulateFunction.class,
                        var_$city).as(var_$cities)));

        Model model = new ModelImpl().addQuery( listSafeCities_build );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        org.drools.modelcompiler.oopathdtables.Person person = new org.drools.modelcompiler.oopathdtables.Person();
        person.setAddress(new org.drools.modelcompiler.oopathdtables.InternationalAddress("", 1, "Milan", "Safecountry"));
        ksession.insert(person);

        org.drools.modelcompiler.oopathdtables.Person person2 = new org.drools.modelcompiler.oopathdtables.Person();
        person2.setAddress(new org.drools.modelcompiler.oopathdtables.InternationalAddress("", 1, "Rome", "Unsafecountry"));
        ksession.insert(person2);

        QueryResults results = ksession.getQueryResults( "listSafeCities");

        List cities = (List) results.iterator().next().get("$cities");
        assertEquals(1, cities.size());
        assertEquals("Milan", cities.get(0));
    }
}
