package org.drools.model.codegen.execmodel.util.lambdareplace;

import org.drools.model.Index;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.model.codegen.execmodel.domain.Person;
import org.drools.model.codegen.execmodel.domain.Result;

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
                                .expr("exprA", mypackage.P2E.LambdaPredicate2E117EE3EED73A1ADDD4A912C0C0B783.INSTANCE,
                                        alphaIndexedBy(String.class, Index.ConstraintType.EQUAL, 1, mypackage.PA9.LambdaExtractorA9A653CF7BE68516CB7ED0704BF616BC.INSTANCE, "Mark"),
                                        reactOn("name", "age")),
                        pattern(olderV)
                                .expr("exprB", mypackage.P84.LambdaPredicate842926483D94135CE6EE74EDC5268A44.INSTANCE,
                                        alphaIndexedBy(String.class, Index.ConstraintType.NOT_EQUAL, 1, mypackage.PA9.LambdaExtractorA9A653CF7BE68516CB7ED0704BF616BC.INSTANCE, "Mark"),
                                        reactOn("name"))
                                .expr("exprC", markV,  mypackage.P43.LambdaPredicate438FF2D311E37560C570C464E6D07893.INSTANCE,
                                        betaIndexedBy(int.class, Index.ConstraintType.GREATER_THAN, 0, mypackage.PED.LambdaExtractorEDCC628863B64CE4E8B4BB53053E2DB3.INSTANCE, mypackage.PE0.LambdaExtractorE0AD67E092677C55BDBA104517818C34.INSTANCE),
                                        reactOn("age")),
                        on(olderV, markV).execute(mypackage.P11.LambdaConsequence11879E33AF18636A33E14C9D860D772D.INSTANCE)
                );
    }
}