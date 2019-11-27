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
                                .expr("exprA", mypackage.LambdaPredicate56AA696D2EE62C6ECE1B5C31777F66E5.INSTANCE,
                                      alphaIndexedBy(String.class, Index.ConstraintType.EQUAL, 1, mypackage.LambdaExtractor348CE7AD57410176025A46C477F09112.INSTANCE, "Mark"),
                                      reactOn("name", "age")),
                        pattern(olderV)
                                .expr("exprB", mypackage.LambdaPredicateD04955B52DA08C4828E78DD5EA434F76.INSTANCE,
                                      alphaIndexedBy(String.class, Index.ConstraintType.NOT_EQUAL, 1, mypackage.LambdaExtractor348CE7AD57410176025A46C477F09112.INSTANCE, "Mark"),
                                      reactOn("name"))
                                .expr("exprC", markV, mypackage.LambdaPredicateE1D438AAC3AEAAFEE61CB8AFB5512703.INSTANCE,
                                      betaIndexedBy(int.class, Index.ConstraintType.GREATER_THAN, 0, mypackage.LambdaExtractor53C3F3F580F089463A602E8110436AC5.INSTANCE, mypackage.LambdaExtractor53C3F3F580F089463A602E8110436AC5.INSTANCE),
                                      reactOn("age")),
                        on(olderV, markV).execute(mypackage.LambdaConsequenceB7E087B8C2A00873E6BF56F188C3DA78.INSTANCE)
                );
    }
}