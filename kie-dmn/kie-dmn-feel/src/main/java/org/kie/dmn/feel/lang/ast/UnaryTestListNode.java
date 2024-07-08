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
package org.kie.dmn.feel.lang.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.UnaryTest;

public class UnaryTestListNode
        extends BaseNode {

    public enum State {
        Positive,
        Negated
    }

    private List<BaseNode> elements;
    private State state;
    private UnaryTestNode notNode;

    public UnaryTestListNode(ParserRuleContext ctx) {
        this(ctx, new ArrayList<>(), State.Positive);
    }

    public UnaryTestListNode(ParserRuleContext ctx, List<BaseNode> elements, State state) {
        super(ctx);
        this.elements = elements;
        this.state = state;
        if (isNegated()) {
            notNode = ASTBuilderFactory.newUnaryTestNode(ctx, "not",
                                                         ASTBuilderFactory.newListNode(ctx, elements));
        }
    }

    public UnaryTestListNode(List<BaseNode> elements, State state) {
        super();
        this.elements = elements;
        this.state = state;
        if (isNegated()) {
            notNode = new UnaryTestNode("not", new ListNode(elements));
        }
    }

    public UnaryTestListNode(List<BaseNode> elements, State state, String text) {
        this(elements, state);
        this.setText(text);
    }

    public boolean isNegated() {
        return state == State.Negated;
    }

    public State getState() {
        return state;
    }

    public List<BaseNode> getElements() {
        return elements;
    }

    public void setElements(List<UnaryTestNode> elements) {
        this.elements = elements.stream().map(UnaryTestNode.class::cast).collect(Collectors.toList());
    }


    @Override
    public List evaluate(EvaluationContext ctx) {
        if (notNode != null) {
            return Collections.singletonList(notNode.evaluate(ctx));
        } else {
            return elements.stream().map(e -> e != null ? e.evaluate(ctx) : null).collect(Collectors.toList());
        }
    }

    @Override
    public Type getResultType() {
        return BuiltInType.LIST;
    }

    @Override
    public ASTNode[] getChildrenNode() {
        return elements.toArray(new ASTNode[elements.size()]);
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

    public List<UnaryTest> getCompiledUnaryTests() {
        return notNode != null ? Collections.singletonList(getUnaryTest(notNode)) :
                elements.stream()
                        .filter(baseNode -> baseNode instanceof UnaryTestNode || baseNode instanceof DashNode)
                        .map(this::getUnaryTest).toList();
    }

    private UnaryTest getUnaryTest(BaseNode baseNode) {
        if (baseNode instanceof UnaryTestNode) {
            return ((UnaryTestNode) baseNode).getUnaryTest();
        } else if (baseNode instanceof DashNode) {
            return DashNode.DashUnaryTest.INSTANCE;
        } else {
            throw new RuntimeException("Unexpected node type: " + baseNode.getClass().getSimpleName());
        }
    }

}
