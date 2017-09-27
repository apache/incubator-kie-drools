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

import org.assertj.core.api.Assertions;
import org.drools.model.Global;
import org.drools.model.Model;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.model.impl.ModelImpl;
import org.drools.modelcompiler.builder.KieBaseBuilder;
import org.drools.modelcompiler.domain.Child;
import org.drools.modelcompiler.domain.Man;
import org.drools.modelcompiler.domain.Toy;
import org.drools.modelcompiler.domain.Woman;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import java.util.ArrayList;
import java.util.List;

import static org.drools.model.DSL.*;

public class OOPathFlowTest {

    @Test
    public void testReactiveOOPath() {
        Global<List> listG = globalOf(type(List.class), "defaultpkg", "list");
        Variable<Man> manV = declarationOf( type( Man.class ) );
        Variable<Woman> wifeV = declarationOf( type( Woman.class ), reactiveFrom( manV, Man::getWife ) );
        Variable<Child> childV = declarationOf( type( Child.class ), reactiveFrom( wifeV, Woman::getChildren ) );
        Variable<Toy> toyV = declarationOf( type( Toy.class ), reactiveFrom( childV, Child::getToys ) );

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
        Global<List> listG = globalOf(type(List.class), "defaultpkg", "list");
        Variable<Man> manV = declarationOf( type( Man.class ) );
        Variable<Woman> wifeV = declarationOf( type( Woman.class ), reactiveFrom( manV, Man::getWife ) );
        Variable<Child> childV = declarationOf( type( Child.class ), reactiveFrom( wifeV, Woman::getChildren ) );
        Variable<Toy> toyV = declarationOf( type( Toy.class ), reactiveFrom( childV, Child::getToys ) );

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
}
