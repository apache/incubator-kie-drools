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
                                .expr("exprA", mypackage.P06.LambdaPredicate06D9CAF62B22E20D54F00BF80DDF9252.INSTANCE,
                                      alphaIndexedBy(String.class, Index.ConstraintType.EQUAL, 1, mypackage.P64.LambdaExtractor6446CF9E10395E110FFAA37D031CBA03.INSTANCE, "Mark"),
                                      reactOn("name", "age")),
                        pattern(olderV)
                                .expr("exprB", mypackage.P59.LambdaPredicate59838F66EFE76B3126D8A0D6BD142D43.INSTANCE,
                                      alphaIndexedBy(String.class, Index.ConstraintType.NOT_EQUAL, 1, mypackage.P64.LambdaExtractor6446CF9E10395E110FFAA37D031CBA03.INSTANCE, "Mark"),
                                      reactOn("name"))
                                .expr("exprC", markV,  mypackage.P9E.LambdaPredicate9E0C6EADC0002D44C5EA869FEDF457E8.INSTANCE,
                                      betaIndexedBy(int.class, Index.ConstraintType.GREATER_THAN, 0, mypackage.P56.LambdaExtractor569F9C26B18A579C8E3D1731B99987B2.INSTANCE, mypackage.PEE.LambdaExtractorEEDD1329065F688E601215AECCA7B47A.INSTANCE),
                                      reactOn("age")),
                        on(olderV, markV).execute(mypackage.P7F.LambdaConsequence7F5BC5726F4483FE1FC85E73B94C2AA5.INSTANCE)
                );
    }
}