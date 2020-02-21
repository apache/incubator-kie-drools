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
                                .expr("exprA", mypackage.LambdaPredicateB79EF47A20EF5A14907461DD258F6B5B.INSTANCE,
                                      alphaIndexedBy(String.class, Index.ConstraintType.EQUAL, 1, mypackage.LambdaExtractorC0580DCF55156DB718A1BF6A277561D8.INSTANCE, "Mark"),
                                      reactOn("name", "age")),
                        pattern(olderV)
                                .expr("exprB", mypackage.LambdaPredicateF94E637E0793B5BA5EC5501FC8A76BE3.INSTANCE,
                                      alphaIndexedBy(String.class, Index.ConstraintType.NOT_EQUAL, 1, mypackage.LambdaExtractorC0580DCF55156DB718A1BF6A277561D8.INSTANCE, "Mark"),
                                      reactOn("name"))
                                .expr("exprC", markV, mypackage.LambdaPredicate990437E290E3F025EA5B68860D6AEBF7.INSTANCE,
                                      betaIndexedBy(int.class, Index.ConstraintType.GREATER_THAN, 0, mypackage.LambdaExtractorAA53F96629A18E77855D21BD98CF5095.INSTANCE, mypackage.LambdaExtractorAA53F96629A18E77855D21BD98CF5095.INSTANCE),
                                      reactOn("age")),
                        on(olderV, markV).execute(mypackage.LambdaConsequence5B6E6A75E5A49DF713DD9058863AA384.INSTANCE)
                );
    }
}