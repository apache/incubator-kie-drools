package org.drools.modelcompiler.util.lambdareplace;

import org.drools.model.Index;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Result;

import static org.drools.model.DSL.declarationOf;
import static org.drools.model.DSL.on;
import static org.drools.model.PatternDSL.alphaIndexedBy;
import static org.drools.model.PatternDSL.betaIndexedBy;
import static org.drools.model.PatternDSL.pattern;
import static org.drools.model.PatternDSL.reactOn;
import static org.drools.model.PatternDSL.rule;

class PatternTestHarness {

    public void inputMethod() {
        Result result = new Result();
        Variable<Person> markV = declarationOf(Person.class);
        Variable<Person> olderV = declarationOf(Person.class);

        Rule rule = rule("beta")
                .build(
                        pattern(markV)
                                .expr("exprA", (Person p) -> p.getName().equals("Mark"),
                                      alphaIndexedBy(String.class, Index.ConstraintType.EQUAL, 1, (Person p) -> p.getName(), "Mark"),
                                      reactOn("name", "age")),
                        pattern(olderV)
                                .expr("exprB", (Person p) -> !p.getName().equals("Mark"),
                                      alphaIndexedBy(String.class, Index.ConstraintType.NOT_EQUAL, 1, (Person p) -> p.getName(), "Mark"),
                                      reactOn("name"))
                                .expr("exprC", markV, (Person p1, Person p2) -> p1.getAge() > p2.getAge(),
                                      betaIndexedBy(int.class, Index.ConstraintType.GREATER_THAN, 0, (Person p) -> p.getAge(), (Person p) -> p.getAge()),
                                      reactOn("age")),
                        on(olderV, markV).execute((Person p1, Person p2) -> result.setValue(p1.getName() + " is older than " + p2.getName()))
                );
    }

    public void expectedOutput() {
        Result result = new Result();
        Variable<Person> markV = declarationOf(Person.class);
        Variable<Person> olderV = declarationOf(Person.class);

        Rule rule = rule("beta")
                .build(
                        pattern(markV)
                                .expr("exprA", mypackage.PB4.LambdaPredicateB4BD3C13169578B6FA8E952113BF43FE.INSTANCE,
                                      alphaIndexedBy(String.class, Index.ConstraintType.EQUAL, 1, mypackage.PF1.LambdaExtractorF1957A9B73DCC850FB61E5A94549014A.INSTANCE, "Mark"),
                                      reactOn("name", "age")),
                        pattern(olderV)
                                .expr("exprB", mypackage.PAA.LambdaPredicateAA0174191F4A6806B804030B95638BF9.INSTANCE,
                                      alphaIndexedBy(String.class, Index.ConstraintType.NOT_EQUAL, 1, mypackage.PF1.LambdaExtractorF1957A9B73DCC850FB61E5A94549014A.INSTANCE, "Mark"),
                                      reactOn("name"))
                                .expr("exprC", markV,  mypackage.PC0.LambdaPredicateC06E24C125614C09F9319B8C2B0D7A08.INSTANCE,
                                      betaIndexedBy(int.class, Index.ConstraintType.GREATER_THAN, 0, mypackage.P6F.LambdaExtractor6F66D90C869211C6AEC2E67F99D874D8.INSTANCE, mypackage.P6F.LambdaExtractor6F66D90C869211C6AEC2E67F99D874D8.INSTANCE),
                                      reactOn("age")),
                        on(olderV, markV).execute(mypackage.P73.LambdaConsequence73B957CDB8CD5D5A17BDA94A7D62ED86.INSTANCE)
                );
    }
}