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

import org.drools.core.reteoo.AlphaNode;
import org.drools.model.Global;
import org.drools.model.Index.ConstraintType;
import org.drools.model.Model;
import org.drools.model.Query1;
import org.drools.model.Query2;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.model.impl.ModelImpl;
import org.drools.modelcompiler.builder.KieBaseBuilder;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;

import java.util.Collection;

import static org.drools.model.DSL.*;
import static org.junit.Assert.*;

public class FlowTest {

    public static class Result {
        private Object value;

        public Result() { }

        public Result(Object value) {
            this.value = value;
        }

        public Object getValue() {
            return value;
        }

        public void setValue( Object value ) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value.toString();
        }
    }

    @Test
    public void testBeta() {
        Result result = new Result();
        Variable<Person> markV = declarationOf( type( Person.class ) );
        Variable<Person> olderV = declarationOf( type( Person.class ) );

        Rule rule = rule( "beta" )
                .view(
                        expr("exprA", markV, p -> p.getName().equals("Mark"))
                                .indexedBy( String.class, ConstraintType.EQUAL, Person::getName, "Mark" )
                                .reactOn( "name", "age" ), // also react on age, see RuleDescr.lookAheadFieldsOfIdentifier
                        expr("exprB", olderV, p -> !p.getName().equals("Mark"))
                                .indexedBy( String.class, ConstraintType.NOT_EQUAL, Person::getName, "Mark" )
                                .reactOn( "name" ),
                        expr("exprC", olderV, markV, (p1, p2) -> p1.getAge() > p2.getAge())
                                .indexedBy( int.class, ConstraintType.GREATER_THAN, Person::getAge, Person::getAge )
                                .reactOn( "age" )
                     )
                .then(on(olderV, markV)
                            .execute((p1, p2) -> result.value = p1.getName() + " is older than " + p2.getName()));

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        Person mark = new Person("Mark", 37);
        Person edson = new Person("Edson", 35);
        Person mario = new Person("Mario", 40);

        FactHandle markFH = ksession.insert(mark);
        FactHandle edsonFH = ksession.insert(edson);
        FactHandle marioFH = ksession.insert(mario);

        ksession.fireAllRules();
        assertEquals("Mario is older than Mark", result.value);

        result.value = null;
        ksession.delete( marioFH );
        ksession.fireAllRules();
        assertNull(result.value);

        mark.setAge( 34 );
        ksession.update( markFH, mark, "age" );

        ksession.fireAllRules();
        assertEquals("Edson is older than Mark", result.value);
    }

    @Test
    public void testBetaWithResult() {
        Variable<Person> markV = declarationOf( type( Person.class ) );
        Variable<Person> olderV = declarationOf( type( Person.class ) );
        Variable<Result> resultV = declarationOf( type( Result.class ) );

        Rule rule = rule( "beta" )
                .view(
                        expr("exprA", markV, p -> p.getName().equals("Mark"))
                                .indexedBy( String.class, ConstraintType.EQUAL, Person::getName, "Mark" )
                                .reactOn( "name", "age" ), // also react on age, see RuleDescr.lookAheadFieldsOfIdentifier
                        expr("exprB", olderV, p -> !p.getName().equals("Mark"))
                                .indexedBy( String.class, ConstraintType.NOT_EQUAL, Person::getName, "Mark" )
                                .reactOn( "name" ),
                        expr("exprC", olderV, markV, (p1, p2) -> p1.getAge() > p2.getAge())
                                .indexedBy( int.class, ConstraintType.GREATER_THAN, Person::getAge, Person::getAge )
                                .reactOn( "age" )
                     )
                .then(on(olderV, markV, resultV)
                            .execute((p1, p2, r) -> r.setValue( p1.getName() + " is older than " + p2.getName()) ));

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        Result result = new Result();
        ksession.insert(result);

        Person mark = new Person("Mark", 37);
        Person edson = new Person("Edson", 35);
        Person mario = new Person("Mario", 40);

        FactHandle markFH = ksession.insert(mark);
        FactHandle edsonFH = ksession.insert(edson);
        FactHandle marioFH = ksession.insert(mario);

        ksession.fireAllRules();
        assertEquals( "Mario is older than Mark", result.getValue() );
    }

    @Test
    public void test3Patterns() {
        Result result = new Result();
        Variable<Person> personV = declarationOf( type( Person.class ) );
        Variable<Person> markV = declarationOf( type( Person.class ) );
        Variable<String> nameV = declarationOf( type( String.class ) );

        Rule rule = rule( "myrule" )
                .view(
                        expr("exprA", markV, p -> p.getName().equals("Mark")),
                        expr("exprB", personV, markV, (p1, p2) -> p1.getAge() > p2.getAge()),
                        expr("exprC", nameV, personV, (s, p) -> s.equals( p.getName() ))
                     )
                .then(on(nameV)
                            .execute(s -> result.value = s));

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        ksession.insert( "Mario" );
        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));
        ksession.fireAllRules();

        assertEquals("Mario", result.value);
    }

    @Test
    public void testOr() {
        Result result = new Result();
        Variable<Person> personV = declarationOf( type( Person.class ) );
        Variable<Person> markV = declarationOf( type( Person.class ) );
        Variable<String> nameV = declarationOf( type( String.class ) );

        Rule rule = rule( "or" )
                .view(
                        or(
                            expr("exprA", personV, p -> p.getName().equals("Mark")),
                            and(
                                    expr("exprA", markV, p -> p.getName().equals("Mark")),
                                    expr("exprB", personV, markV, (p1, p2) -> p1.getAge() > p2.getAge())
                               )
                          ),
                        expr("exprC", nameV, personV, (s, p) -> s.equals( p.getName() ))
                     )
                .then(on(nameV)
                            .execute(s -> result.value = s));

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        ksession.insert( "Mario" );
        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));
        ksession.fireAllRules();

        assertEquals("Mario", result.value);
    }

    @Test
    public void testNot() {
        Result result = new Result();
        Variable<Person> oldestV = declarationOf( type( Person.class ) );
        Variable<Person> otherV = declarationOf( type( Person.class ) );

        Rule rule = rule("not")
                .view(
                        not(otherV, oldestV, (p1, p2) -> p1.getAge() > p2.getAge())
                     )
                .then(on(oldestV)
                            .execute(p -> result.value = "Oldest person is " + p.getName()));

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();
        assertEquals("Oldest person is Mario", result.value);
    }

    @Test
    public void testAccumulate1() {
        Result result = new Result();
        Variable<Person> person = declarationOf( type( Person.class ) );
        Variable<Integer> resultSum = declarationOf( type( Integer.class ) );

        Rule rule = rule("accumulate")
                .view(
                        accumulate(expr(person, p -> p.getName().startsWith("M")),
                                   sum((Person p) -> p.getAge()).as(resultSum))
                     )
                .then( on(resultSum).execute(sum -> result.value = "total = " + sum) );


        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();
        assertEquals("total = 77", result.value);
    }

    @Test
    public void testAccumulate2() {
        Result result = new Result();
        Variable<Person> person = declarationOf( type( Person.class ) );
        Variable<Integer> resultSum = declarationOf( type( Integer.class ) );
        Variable<Double> resultAvg = declarationOf( type( Double.class ) );

        Rule rule = rule("accumulate")
                .view(
                        accumulate(expr(person, p -> p.getName().startsWith("M")),
                                   sum(Person::getAge).as(resultSum),
                                   average(Person::getAge).as(resultAvg))
                     )
                .then(
                        on(resultSum, resultAvg)
                                .execute((sum, avg) -> result.value = "total = " + sum + "; average = " + avg)
                     );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();
        assertEquals("total = 77; average = 38.5", result.value);
    }

    @Test
    public void testGlobalInConsequence() {
        Variable<Person> markV = declarationOf( type( Person.class ) );
        Global<Result> resultG = globalOf( type( Result.class ), "org.mypkg" );

        Rule rule = rule( "org.mypkg", "global" )
                .view(
                        expr("exprA", markV, p -> p.getName().equals("Mark"))
                                .indexedBy( String.class, ConstraintType.EQUAL, Person::getName, "Mark" )
                                .reactOn( "name" )
                     )
                .then(on(markV, resultG)
                              .execute((p, r) -> r.setValue( p.getName() + " is " + p.getAge() ) ) );

        Model model = new ModelImpl().addRule( rule ).addGlobal( resultG );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        Result result = new Result();
        ksession.setGlobal( resultG.getName(), result );

        Person mark = new Person("Mark", 37);
        Person edson = new Person("Edson", 35);
        Person mario = new Person("Mario", 40);

        ksession.insert(mark);
        ksession.insert(edson);
        ksession.insert(mario);

        ksession.fireAllRules();
        assertEquals("Mark is 37", result.value);
    }

    @Test
    public void testGlobalInConstraint() {
        Variable<Person> markV = declarationOf( type( Person.class ) );
        Global<Result> resultG = globalOf( type( Result.class ), "org.mypkg" );
        Global<String> nameG = globalOf( type( String.class ), "org.mypkg" );

        Rule rule = rule( "org.mypkg", "global" )
                .view(
                        expr("exprA", markV, nameG, (p, n) -> p.getName().equals(n))
                                .reactOn( "name" )
                     )
                .then(on(markV, resultG)
                              .execute((p, r) -> r.setValue( p.getName() + " is " + p.getAge() ) ) );

        Model model = new ModelImpl().addRule( rule ).addGlobal( nameG ).addGlobal( resultG );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        ksession.setGlobal( nameG.getName(), "Mark" );

        Result result = new Result();
        ksession.setGlobal( resultG.getName(), result );

        Person mark = new Person("Mark", 37);
        Person edson = new Person("Edson", 35);
        Person mario = new Person("Mario", 40);

        ksession.insert(mark);
        ksession.insert(edson);
        ksession.insert(mario);

        ksession.fireAllRules();
        assertEquals("Mark is 37", result.value);
    }

    @Test
    public void testNotEmptyPredicate() {
        Rule rule = rule("R")
                .view(not(input(declarationOf(type(Person.class)))))
                .then(execute((drools) -> drools.insert(new Result("ok")) ));

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        ReteDumper.checkRete(ksession, node -> !(node instanceof AlphaNode) );

        Person mario = new Person("Mario", 40);

        ksession.insert(mario);
        ksession.fireAllRules();

        assertTrue( ksession.getObjects(new ClassObjectFilter( Result.class ) ).isEmpty() );
    }

    @Test
    public void testQuery() {
        Variable<Person> personV = declarationOf( type( Person.class ), "$p" );
        Variable<Integer> ageV = declarationOf( type( Integer.class ) );

        Query1<Integer> query = query( "olderThan", ageV )
                .view( expr("exprA", personV, ageV, (p, a) -> p.getAge() > a) );

        Model model = new ModelImpl().addQuery( query );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        ksession.insert( new Person( "Mark", 39 ) );
        ksession.insert( new Person( "Mario", 41 ) );

        QueryResults results = ksession.getQueryResults( "olderThan", 40 );

        assertEquals( 1, results.size() );
        Person p = (Person) results.iterator().next().get( "$p" );
        assertEquals( "Mario", p.getName() );
    }

    @Test
    public void testQueryInRule() {
        Variable<Person> personV = declarationOf( type( Person.class ) );
        Variable<Integer> ageV = declarationOf( type( Integer.class ) );

        Query2<Person, Integer> query = query( "olderThan", personV, ageV )
                .view( expr("exprA", personV, ageV, (p, a) -> p.getAge() > a) );

        Rule rule = rule("R")
                .view( query.call(personV, valueOf(40)) )
                .then(on(personV)
                              .execute((drools, p) -> drools.insert(new Result(p.getName())) ));

        Model model = new ModelImpl().addQuery( query ).addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        ksession.insert( new Person( "Mark", 39 ) );
        ksession.insert( new Person( "Mario", 41 ) );

        ksession.fireAllRules();

        Collection<Result> results = (Collection<Result>) ksession.getObjects( new ClassObjectFilter( Result.class ) );
        assertEquals( 1, results.size() );
        assertEquals( "Mario", results.iterator().next().getValue() );
    }

    @Test
    public void testQueryInRuleWithDeclaration() {
        Variable<Person> personV = declarationOf( type( Person.class ) );
        Variable<Integer> ageV = declarationOf( type( Integer.class ) );

        Query2<Person, Integer> query = query( "olderThan", personV, ageV )
                .view( expr("exprA", personV, ageV, (p, a) -> p.getAge() > a) );

        Rule rule = rule("R")
                .view(
                        expr( "exprB", personV, p -> p.getName().startsWith( "M" ) ),
                        query.call(personV, valueOf(40))
                     )
                .then(on(personV)
                              .execute((drools, p) -> drools.insert(new Result(p.getName())) ));

        Model model = new ModelImpl().addQuery( query ).addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        ksession.insert( new Person( "Mark", 39 ) );
        ksession.insert( new Person( "Mario", 41 ) );
        ksession.insert( new Person( "Edson", 41 ) );

        ksession.fireAllRules();

        Collection<Result> results = (Collection<Result>) ksession.getObjects( new ClassObjectFilter( Result.class ) );
        assertEquals( 1, results.size() );
        assertEquals( "Mario", results.iterator().next().getValue() );
    }
}
