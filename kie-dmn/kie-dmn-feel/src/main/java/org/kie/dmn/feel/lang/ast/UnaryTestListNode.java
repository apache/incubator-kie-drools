/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

package org.kie.dmn.feel.lang.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;

public class UnaryTestListNode
        extends BaseNode {

    public enum State {
        Positive,
        Negated
    }

    private List<BaseNode> elements;
    private State state;
    private BaseNode notNode;

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

    public boolean isNegated() {
        return state == State.Negated;
    }

    public State getState() {
        return state;
    }

    public List<BaseNode> getElements() {
        return elements;
    }

    public void setElements(List<BaseNode> elements) {
        this.elements = elements;
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
}
