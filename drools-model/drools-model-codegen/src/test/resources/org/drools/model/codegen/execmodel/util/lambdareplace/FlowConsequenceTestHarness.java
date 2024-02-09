/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.codegen.execmodel.util.lambdareplace;

import org.drools.model.Drools;
import org.drools.model.FlowDSL;
import org.drools.model.Index;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.model.codegen.execmodel.domain.Person;

import static org.drools.model.DSL.declarationOf;
import static org.drools.model.DSL.expr;
import static org.drools.model.DSL.on;
import static org.drools.model.FlowDSL.bind;
import static org.drools.model.PatternDSL.betaIndexedBy;
import static org.drools.model.PatternDSL.pattern;
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
                .build(expr("exprA", markV, mypackage.LambdaPredicate3BC479A1F3E07E22E6B04B826FEF3203.INSTANCE)
                               .indexedBy(String.class, Index.ConstraintType.EQUAL, 1, mypackage.LambdaExtractor78CC335A6E209E33128956DF6E4B90ED.INSTANCE, "Mark")
                               .reactOn("name"), // also react on age, see RuleDescr.lookAheadFieldsOfIdentifier
                       bind(markAge).as(markV, (Person person) -> person.getAge()).reactOn("age"),
                       expr("exprB", olderV, mypackage.LambdaPredicate3EA0E684DE1924A84F80BB5426A04F64.INSTANCE)
                               .indexedBy(String.class, Index.ConstraintType.NOT_EQUAL, 1, mypackage.LambdaExtractorD03FF943D0CED6BB0500A27A09B6EAC2.INSTANCE, "Mark")
                               .reactOn("name"),
                       expr("exprC", olderV, markAge, mypackage.LambdaPredicateA3D5CA75190A6BB79FD366E1965112D9.INSTANCE)
                               .indexedBy(int.class, Index.ConstraintType.GREATER_THAN, 0, mypackage.LambdaExtractor049CBA16FC61AA14BE778D120A8067C6.INSTANCE, int.class::cast)
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
                .build(expr("exprA", markV, mypackage.LambdaPredicate3BC479A1F3E07E22E6B04B826FEF3203.INSTANCE)
                               .indexedBy(String.class, Index.ConstraintType.EQUAL, 1, mypackage.LambdaExtractor78CC335A6E209E33128956DF6E4B90ED.INSTANCE, "Mark")
                               .reactOn("name"), // also react on age, see RuleDescr.lookAheadFieldsOfIdentifier
                       bind(markAge).as(markV, (Person person) -> person.getAge()).reactOn("age"),
                       expr("exprB", olderV, mypackage.LambdaPredicate3EA0E684DE1924A84F80BB5426A04F64.INSTANCE)
                               .indexedBy(String.class, Index.ConstraintType.NOT_EQUAL, 1, mypackage.LambdaExtractorD03FF943D0CED6BB0500A27A09B6EAC2.INSTANCE, "Mark")
                               .reactOn("name"),
                       expr("exprC", olderV, markAge, mypackage.LambdaPredicateA3D5CA75190A6BB79FD366E1965112D9.INSTANCE)
                               .indexedBy(int.class, Index.ConstraintType.GREATER_THAN, 0, mypackage.LambdaExtractor049CBA16FC61AA14BE778D120A8067C6.INSTANCE, int.class::cast)
                               .reactOn("age"),
                       on(olderV, markV).execute(mypackage.LambdaConsequenceCA6B859E4B5D8DD93F77DEA7176C8970.INSTANCE)
                );
    }

}