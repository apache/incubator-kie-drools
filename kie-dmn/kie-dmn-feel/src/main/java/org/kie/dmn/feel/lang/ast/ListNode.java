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
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;

public class ListNode
        extends BaseNode {

    private List<BaseNode> elements;

    public ListNode(ParserRuleContext ctx) {
        super( ctx );
        elements = new ArrayList<>();
    }

    public ListNode(ParserRuleContext ctx, List<BaseNode> elements) {
        super( ctx );
        this.elements = elements;
    }

    public ListNode(List<BaseNode> elements) {
        this.elements = elements;
    }

    public ListNode(List<BaseNode> elements, String text) {
        this.elements = elements;
        this.setText(text);
    }

    public List<BaseNode> getElements() {
        return elements;
    }

    public void setElements(List<BaseNode> elements) {
        this.elements = elements;
    }

    @Override
    public List evaluate(EvaluationContext ctx) {
        return elements.stream().map( e -> e != null ? e.evaluate( ctx ) : null ).collect( Collectors.toList() );
    }

    @Override
    public Type getResultType() {
        return BuiltInType.LIST;
    }

    @Override
    public ASTNode[] getChildrenNode() {
        return elements.toArray( new ASTNode[elements.size()] );
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

}
