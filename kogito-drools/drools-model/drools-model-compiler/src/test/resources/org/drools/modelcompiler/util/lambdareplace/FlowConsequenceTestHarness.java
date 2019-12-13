package org.drools.modelcompiler.util.lambdareplace;

import org.drools.model.Drools;
import org.drools.model.FlowDSL;
import org.drools.model.Index;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Result;

import static org.drools.model.DSL.declarationOf;
import static org.drools.model.DSL.expr;
import static org.drools.model.DSL.on;
import static org.drools.model.FlowDSL.bind;
import static org.drools.model.PatternDSL.alphaIndexedBy;
import static org.drools.model.PatternDSL.betaIndexedBy;
import static org.drools.model.PatternDSL.pattern;
import static org.drools.model.PatternDSL.reactOn;
import static org.drools.model.PatternDSL.rule;

class FlowTestHarness {

    public void inputMethodNotConverted() {
        Variable<Person> markV = declarationOf(Person.class);
        Variable<Integer> markAge = declarationOf(Integer.class);
        Variable<Person> olderV = declarationOf(Person.class);

        Rule rule = FlowDSL.rule("beta")
                .build(expr("exprA", markV, (Person p) -> p.getName().equals("Mark"))
                               .indexedBy(String.class, Index.ConstraintType.EQUAL, 1, (Person person) -> person.getName(), "Mark")
                               .reactOn("name"), // also react on age, see RuleDescr.lookAheadFieldsOfIdentifier
                       bind(markAge).as(markV, (Person person) -> person.getAge()).reactOn("age"),
                       expr("exprB", olderV, (Person p) -> !p.getName().equals("Mark"))
                               .indexedBy(String.class, Index.ConstraintType.NOT_EQUAL, 1, (Person person2) -> person2.getName(), "Mark")
                               .reactOn("name"),
                       expr("exprC", olderV, markAge, (Person p1, Integer age) -> p1.getAge() > age)
                               .indexedBy(int.class, Index.ConstraintType.GREATER_THAN, 0, (Person person1) -> person1.getAge(), int.class::cast)
                               .reactOn("age"),
                       on(olderV, markV).execute((Drools drools, Person p1, Person p2) -> drools.insert(p1.getName() + " is older than " + p2.getName()))
                );
    }

    public void expectedOutputNotConverted() {
        Variable<Person> markV = declarationOf(Person.class);
        Variable<Integer> markAge = declarationOf(Integer.class);
        Variable<Person> olderV = declarationOf(Person.class);

        Rule rule = FlowDSL.rule("beta")
                .build(expr("exprA", markV, mypackage.LambdaPredicate56AA696D2EE62C6ECE1B5C31777F66E5.INSTANCE)
                               .indexedBy(String.class, Index.ConstraintType.EQUAL, 1, mypackage.LambdaExtractor846E23768399BF25B413BAD5FE67FDDE.INSTANCE, "Mark")
                               .reactOn("name"), // also react on age, see RuleDescr.lookAheadFieldsOfIdentifier
                       bind(markAge).as(markV, (Person person) -> person.getAge()).reactOn("age"),
                       expr("exprB", olderV, mypackage.LambdaPredicateD04955B52DA08C4828E78DD5EA434F76.INSTANCE)
                               .indexedBy(String.class, Index.ConstraintType.NOT_EQUAL, 1, mypackage.LambdaExtractor567C73E592F0273030C30289C142FA39.INSTANCE, "Mark")
                               .reactOn("name"),
                       expr("exprC", olderV, markAge, mypackage.LambdaPredicateD27508B746925618D36E184D99E8CFF4.INSTANCE)
                               .indexedBy(int.class, Index.ConstraintType.GREATER_THAN, 0, mypackage.LambdaExtractor57148B6C957469EB75DC3BF92FE9F648.INSTANCE, int.class::cast)
                               .reactOn("age"),
                       on(olderV, markV).execute((Drools drools, Person p1, Person p2) -> drools.insert(p1.getName() + " is older than " + p2.getName()))
                );
    }

    public void inputMethodConverted() {
        Variable<Person> markV = declarationOf(Person.class);
        Variable<Integer> markAge = declarationOf(Integer.class);
        Variable<Person> olderV = declarationOf(Person.class);

        Rule rule = FlowDSL.rule("beta")
                .build(expr("exprA", markV, (Person p) -> p.getName().equals("Mark"))
                               .indexedBy(String.class, Index.ConstraintType.EQUAL, 1, (Person person) -> person.getName(), "Mark")
                               .reactOn("name"), // also react on age, see RuleDescr.lookAheadFieldsOfIdentifier
                       bind(markAge).as(markV, (Person person) -> person.getAge()).reactOn("age"),
                       expr("exprB", olderV, (Person p) -> !p.getName().equals("Mark"))
                               .indexedBy(String.class, Index.ConstraintType.NOT_EQUAL, 1, (Person person2) -> person2.getName(), "Mark")
                               .reactOn("name"),
                       expr("exprC", olderV, markAge, (Person p1, Integer age) -> p1.getAge() > age)
                               .indexedBy(int.class, Index.ConstraintType.GREATER_THAN, 0, (Person person1) -> person1.getAge(), int.class::cast)
                               .reactOn("age"),
                       on(olderV, markV).execute((Person p1, Person p2) -> System.out.println(p1.getName() + " is older than " + p2.getName()))
                );
    }

    public void expectedOutputConverted() {
        Variable<Person> markV = declarationOf(Person.class);
        Variable<Integer> markAge = declarationOf(Integer.class);
        Variable<Person> olderV = declarationOf(Person.class);

        Rule rule = FlowDSL.rule("beta")
                .build(expr("exprA", markV, mypackage.LambdaPredicate56AA696D2EE62C6ECE1B5C31777F66E5.INSTANCE)
                               .indexedBy(String.class, Index.ConstraintType.EQUAL, 1, mypackage.LambdaExtractor846E23768399BF25B413BAD5FE67FDDE.INSTANCE, "Mark")
                               .reactOn("name"), // also react on age, see RuleDescr.lookAheadFieldsOfIdentifier
                       bind(markAge).as(markV, (Person person) -> person.getAge()).reactOn("age"),
                       expr("exprB", olderV, mypackage.LambdaPredicateD04955B52DA08C4828E78DD5EA434F76.INSTANCE)
                               .indexedBy(String.class, Index.ConstraintType.NOT_EQUAL, 1, mypackage.LambdaExtractor567C73E592F0273030C30289C142FA39.INSTANCE, "Mark")
                               .reactOn("name"),
                       expr("exprC", olderV, markAge, mypackage.LambdaPredicateD27508B746925618D36E184D99E8CFF4.INSTANCE)
                               .indexedBy(int.class, Index.ConstraintType.GREATER_THAN, 0, mypackage.LambdaExtractor57148B6C957469EB75DC3BF92FE9F648.INSTANCE, int.class::cast)
                               .reactOn("age"),
                       on(olderV, markV).execute(mypackage.LambdaConsequenceF05A49D9F4440F84A71770B1FFB3FB3B.INSTANCE)
                );
    }

}