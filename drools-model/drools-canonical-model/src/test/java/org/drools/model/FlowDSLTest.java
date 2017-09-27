package org.drools.model;

import org.drools.model.datasources.DataSource;
import org.drools.model.engine.BruteForceEngine;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.drools.model.DSL.*;
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
        Variable<Person> markV = any(Person.class);
        Variable<Person> olderV = any(Person.class);

        Rule rule = rule("join")
                .attribute(Rule.Attribute.SALIENCE, 10)
                .attribute(Rule.Attribute.AGENDA_GROUP, "myGroup")
                .view(
                        input(markV, "persons"),
                        input(olderV, "persons"),
                        expr(markV, mark -> mark.getName().equals("Mark")),
                        expr(olderV, older -> !older.getName().equals("Mark")),
                        expr(olderV, markV, (older, mark) -> older.getAge() > mark.getAge())
                     )
                .then(on(olderV, markV)
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

        Variable<Person> markV = any(Person.class);
        Variable<Person> olderV = any(Person.class);

        View view = view(
            input(olderV, "persons"),
            expr(olderV, older -> !older.getName().equals("Mark")),
            input(markV, "persons"),
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

        Variable<Person> markV = any(Person.class);
        Variable<Person> otherV = any(Person.class);

        View view = view(
                input(markV, "persons"),
                input(otherV, "persons"),
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

        Variable<Person> oldestV = any(Person.class);
        Variable<Person> otherV = any(Person.class);

        View view = view(
                input(oldestV, "persons"),
                input(otherV, "persons"),
                not(otherV, oldestV, (p1, p2) -> p1.getAge() > p2.getAge())
        );

        List<TupleHandle> result = new BruteForceEngine().bind("persons", persons).evaluate(view);
        assertEquals(1, result.size());
        TupleHandle tuple = result.get(0);
        assertEquals("Mario", tuple.get(oldestV).getName());
    }
}