package org.drools.model;

import java.util.ArrayList;
import java.util.List;

import org.drools.model.datasources.DataSource;
import org.drools.model.engine.BruteForceEngine;
import org.drools.model.impl.DataSourceDefinitionImpl;
import org.junit.Test;

import static org.drools.model.DSL.declarationOf;
import static org.drools.model.DSL.expr;
import static org.drools.model.DSL.not;
import static org.drools.model.DSL.on;
import static org.drools.model.DSL.or;
import static org.drools.model.DSL.rule;
import static org.drools.model.DSL.storeOf;
import static org.drools.model.DSL.view;
import static org.junit.Assert.assertEquals;

public class FlowDSLTest {

    @Test
    public void testJoin() {

        DataSource<Person> persons = storeOf( new Person( "Mark", 37),
                                              new Person("Edson", 35),
                                              new Person("Mario", 40),
                                              new Person("Sofia", 3) );

        // $mark: Person(name == "Mark") in entry-point "persons"
        // $older: Person(name != "Mark" && age > $mark.age) in entry-point "persons"

        List<String> list = new ArrayList<>();
        Variable<Person> markV = declarationOf( Person.class, new DataSourceDefinitionImpl( "persons", false) );
        Variable<Person> olderV = declarationOf( Person.class, new DataSourceDefinitionImpl( "persons", false) );

        Rule rule = rule("join")
                .attribute(Rule.Attribute.SALIENCE, 10)
                .attribute(Rule.Attribute.AGENDA_GROUP, "myGroup")
                .build(
                        expr(markV, mark -> mark.getName().equals("Mark")),
                        expr(olderV, older -> !older.getName().equals("Mark")),
                        expr(olderV, markV, (older, mark) -> older.getAge() > mark.getAge()),
                        on(olderV, markV)
                            .execute((p1, p2) -> list.add(p1.getName() + " is older than " + p2.getName())));

        new BruteForceEngine().bind("persons", persons).evaluate(rule);
        assertEquals(1, list.size());
        assertEquals("Mario is older than Mark", list.get(0));

        assertEquals("join", rule.getName());
        assertEquals(10, (int) rule.getAttribute(Rule.Attribute.SALIENCE));
        assertEquals("myGroup", rule.getAttribute(Rule.Attribute.AGENDA_GROUP));
        assertEquals(false, rule.getAttribute(Rule.Attribute.NO_LOOP));
    }

    @Test
    public void testJoinDifferentConstraintOrder() {

        DataSource<Person> persons = storeOf( new Person("Mark", 37),
                                              new Person("Edson", 35),
                                              new Person("Mario", 40),
                                              new Person("Sofia", 3));

        // $mark: Person(name == "Mark") in entry-point "persons"
        // $older: Person(name != "Mark" && age > $mark.age) in entry-point "persons"

        Variable<Person> markV = declarationOf( Person.class, new DataSourceDefinitionImpl( "persons", false) );
        Variable<Person> olderV = declarationOf( Person.class, new DataSourceDefinitionImpl( "persons", false) );

        View view = view(
            expr(olderV, older -> !older.getName().equals("Mark")),
            expr(markV, mark -> mark.getName().equals("Mark")),
            expr(markV, olderV, (mark, older) -> mark.getAge() < older.getAge())
        );

        List<TupleHandle> result = new BruteForceEngine().bind("persons", persons).evaluate(view);
        assertEquals(1, result.size());
        TupleHandle tuple = result.get(0);
        assertEquals("Mark", tuple.get(markV).getName());
        assertEquals("Mario", tuple.get(olderV).getName());
    }

    @Test
    public void testOr() {
        DataSource<Person> persons = storeOf( new Person("Mark", 37),
                                              new Person("Edson", 35),
                                              new Person("Mario", 40),
                                              new Person("Sofia", 3));

        Variable<Person> markV = declarationOf( Person.class, new DataSourceDefinitionImpl( "persons", false) );
        Variable<Person> otherV = declarationOf( Person.class, new DataSourceDefinitionImpl( "persons", false) );

        View view = view(
                expr(markV, mark -> mark.getName().equals("Mark")),
                or( expr(otherV, markV, (other, mark) -> other.getAge() > mark.getAge()),
                    expr(otherV, markV, (other, mark) -> other.getName().compareToIgnoreCase(mark.getName()) > 0)
                )
        );

        List<TupleHandle> result = new BruteForceEngine().bind("persons", persons).evaluate(view);
        assertEquals(2, result.size());

        TupleHandle tuple = result.get(0);
        assertEquals("Mark", tuple.get(markV).getName());
        assertEquals("Mario", tuple.get(otherV).getName());

        tuple = result.get(1);
        assertEquals("Mark", tuple.get(markV).getName());
        assertEquals("Sofia", tuple.get(otherV).getName());
    }

    @Test
    public void testNot() {
        DataSource<Person> persons = storeOf( new Person("Mark", 37),
                                              new Person("Edson", 35),
                                              new Person("Mario", 40),
                                              new Person("Sofia", 3));

        // $oldest: Person()
        // not( Person(age > $oldest.age) )

        Variable<Person> oldestV = declarationOf( Person.class, new DataSourceDefinitionImpl( "persons", false) );
        Variable<Person> otherV = declarationOf( Person.class, new DataSourceDefinitionImpl( "persons", false) );

        View view = view(
                not(otherV, oldestV, (p1, p2) -> p1.getAge() > p2.getAge())
        );

        List<TupleHandle> result = new BruteForceEngine().bind("persons", persons).evaluate(view);
        assertEquals(1, result.size());
        TupleHandle tuple = result.get(0);
        assertEquals("Mario", tuple.get(oldestV).getName());
    }
}