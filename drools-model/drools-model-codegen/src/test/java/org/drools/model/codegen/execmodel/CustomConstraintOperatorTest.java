/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.codegen.execmodel;

import java.util.function.BiPredicate;

import org.drools.base.common.NetworkNode;
import org.drools.base.prototype.PrototypeObjectType;
import org.drools.base.rule.IndexableConstraint;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.model.ConstraintOperator;
import org.drools.model.Index;
import org.drools.model.Model;
import org.drools.model.Rule;
import org.drools.model.codegen.execmodel.domain.Result;
import org.drools.model.impl.ModelImpl;
import org.drools.model.prototype.PrototypeVariable;
import org.drools.modelcompiler.KieBaseBuilder;
import org.drools.modelcompiler.constraints.LambdaConstraint;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.prototype.PrototypeFact;
import org.kie.api.prototype.PrototypeFactInstance;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.model.DSL.on;
import static org.drools.model.PatternDSL.rule;
import static org.drools.model.prototype.PrototypeDSL.protoPattern;
import static org.drools.model.prototype.PrototypeDSL.variable;
import static org.drools.model.prototype.PrototypeExpression.fixedValue;
import static org.drools.model.prototype.PrototypeExpression.prototypeField;
import static org.kie.api.prototype.PrototypeBuilder.prototype;

public class CustomConstraintOperatorTest {

    static class CustomConstraintOperator implements ConstraintOperator {

        public int counter = 0;

        @Override
        public <T, V> BiPredicate<T, V> asPredicate() {
            return (t, v) -> {
                counter++;
                return t.equals(v);
            };
        }

        @Override
        public boolean containsConstraintType() {
            return true;
        }

        @Override
        public Index.ConstraintType getConstraintType() {
            return Index.ConstraintType.EQUAL;
        }

        @Override
        public String toString() {
            return Index.ConstraintType.EQUAL.toString();
        }
    }

    @Test
    public void alphaIndexIneffective() {
        CustomConstraintOperator customConstraintOperator = new CustomConstraintOperator();

        PrototypeFact testPrototype = prototype("test").asFact();
        PrototypeVariable testV = variable(testPrototype);

        Rule rule1 = rule("alpha1")
                .build(
                        protoPattern(testV)
                                .expr(prototypeField("fieldA"), customConstraintOperator, fixedValue(1)),
                        on(testV).execute((drools, x) ->
                                                  drools.insert(new Result("Found"))
                        )
                );

        Model model = new ModelImpl().addRule(rule1);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();

        PrototypeFactInstance testFact = testPrototype.newInstance();
        testFact.put("fieldA", 1);

        ksession.insert(testFact);
        assertThat(ksession.fireAllRules()).isEqualTo(1);

        // Index is created, but actual alpha index hashing works only with more than 3 nodes
        Index index = getFirstAlphaNodeIndex((InternalKnowledgeBase) kieBase, testPrototype);
        assertThat(index.getIndexType()).isEqualTo(Index.IndexType.ALPHA);
        assertThat(index.getConstraintType()).isEqualTo(Index.ConstraintType.EQUAL);

        // alpha index hashing is not effective, so the predicated is called
        assertThat(customConstraintOperator.counter).isEqualTo(1);
    }

    @Test
    public void alphaIndexEffective() {
        CustomConstraintOperator customConstraintOperator = new CustomConstraintOperator();

        PrototypeFact testPrototype = prototype("test").asFact();
        PrototypeVariable testV = variable(testPrototype);

        Rule rule1 = rule("alpha1")
                .build(
                        protoPattern(testV)
                                .expr(prototypeField("fieldA"), customConstraintOperator, fixedValue(1)),
                        on(testV).execute((drools, x) ->
                                                  drools.insert(new Result("Found"))
                        )
                );
        Rule rule2 = rule("alpha2")
                .build(
                        protoPattern(testV)
                                .expr(prototypeField("fieldA"), customConstraintOperator, fixedValue(2)),
                        on(testV).execute((drools, x) ->
                                                  drools.insert(new Result("Found"))
                        )
                );
        Rule rule3 = rule("alpha3")
                .build(
                        protoPattern(testV)
                                .expr(prototypeField("fieldA"), customConstraintOperator, fixedValue(3)),
                        on(testV).execute((drools, x) ->
                                                  drools.insert(new Result("Found"))
                        )
                );

        Model model = new ModelImpl().addRule(rule1).addRule(rule2).addRule(rule3);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();

        PrototypeFactInstance testFact = testPrototype.newInstance();
        testFact.put("fieldA", 1);

        ksession.insert(testFact);
        assertThat(ksession.fireAllRules()).isEqualTo(1);

        Index index = getFirstAlphaNodeIndex((InternalKnowledgeBase) kieBase, testPrototype);
        assertThat(index.getIndexType()).isEqualTo(Index.IndexType.ALPHA);
        assertThat(index.getConstraintType()).isEqualTo(Index.ConstraintType.EQUAL);

        // alpha index hashing is effective, so the predicated is not called
        assertThat(customConstraintOperator.counter).isZero();
    }

    private static Index getFirstAlphaNodeIndex(InternalKnowledgeBase kieBase, PrototypeFact testPrototype) {
        EntryPointNode epn = kieBase.getRete().getEntryPointNodes().values().iterator().next();
        ObjectTypeNode otn = epn.getObjectTypeNodes().get(new PrototypeObjectType(testPrototype));
        AlphaNode alphaNode = (AlphaNode) otn.getObjectSinkPropagator().getSinks()[0];
        IndexableConstraint constraint = (IndexableConstraint) alphaNode.getConstraint();
        return ((LambdaConstraint) constraint).getEvaluator().getIndex();
    }

    @Test
    public void betaIndex() {
        CustomConstraintOperator customConstraintOperator = new CustomConstraintOperator();

        Result result = new Result();

        PrototypeFact personFact = prototype("org.drools.Person").withField("name").withField("age").asFact();

        PrototypeVariable markV = variable(personFact);
        PrototypeVariable ageMateV = variable(personFact);

        Rule rule = rule("beta")
                .build(
                        protoPattern(markV)
                                .expr("name", Index.ConstraintType.EQUAL, "Mark"),
                        protoPattern(ageMateV)
                                .expr("name", Index.ConstraintType.NOT_EQUAL, "Mark")
                                .expr("age", customConstraintOperator, markV, "age"),
                        on(ageMateV, markV).execute((p1, p2) -> result.setValue(p1.get("name") + " is the same age as " + p2.get("name")))
                );

        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);

        KieSession ksession = kieBase.newKieSession();

        PrototypeFactInstance mark = personFact.newInstance();
        mark.put("name", "Mark");
        mark.put("age", 37);

        PrototypeFactInstance john = personFact.newInstance();
        john.put("name", "John");
        john.put("age", 39);

        PrototypeFactInstance paul = personFact.newInstance();
        paul.put("name", "Paul");
        paul.put("age", 37);

        ksession.insert(mark);
        ksession.insert(john);
        ksession.insert(paul);

        ksession.fireAllRules();
        assertThat(result.getValue()).isEqualTo("Paul is the same age as Mark");

        Index index = getFirstBetaNodeIndex((InternalKnowledgeBase) kieBase, personFact);
        assertThat(index.getIndexType()).isEqualTo(Index.IndexType.BETA);
        assertThat(index.getConstraintType()).isEqualTo(Index.ConstraintType.EQUAL);

        // When beta index is used, the predicate in the custom operator is not actually called
        assertThat(customConstraintOperator.counter).isZero();
    }

    private static Index getFirstBetaNodeIndex(InternalKnowledgeBase kieBase, PrototypeFact testPrototype) {
        EntryPointNode epn = kieBase.getRete().getEntryPointNodes().values().iterator().next();
        ObjectTypeNode otn = epn.getObjectTypeNodes().get(new PrototypeObjectType(testPrototype));
        NetworkNode[] sinks = otn.getObjectSinkPropagator().getSinks();
        BetaNode betaNode = findBetaNode(sinks);

        IndexableConstraint constraint = (IndexableConstraint) betaNode.getConstraints()[0];
        return ((LambdaConstraint) constraint).getEvaluator().getIndex();
    }

    private static BetaNode findBetaNode(NetworkNode[] sinks) {
        for (NetworkNode sink : sinks) {
            if (sink instanceof BetaNode) {
                return (BetaNode) sink;
            } else {
                BetaNode betaNode = findBetaNode(sink.getSinks());
                if (betaNode != null) {
                    return betaNode;
                }
            }
        }
        return null;
    }
}
