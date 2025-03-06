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

import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.util.StringEvalHelper;

/**
 * A name is defined either as a sequence of
 * tokens or as a String. This class supports
 * both, although they should not be used
 * interchangeably.
 */
public class NameDefNode
        extends BaseNode {

    private List<String> parts;
    private String name;

    public NameDefNode(ParserRuleContext ctx, List<String> parts) {
        super( ctx );
        this.parts = parts;
    }

    public NameDefNode(ParserRuleContext ctx, String name) {
        super( ctx );
        this.name = name;
    }

    public NameDefNode(List<String> parts, String name, String text) {
        this.parts = parts;
        this.name = name;
        this.setText(text);
    }

    public List<String> getParts() {
        return parts;
    }

    public void setParts(List<String> parts) {
        this.parts = parts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String evaluate(EvaluationContext ctx) {
        return StringEvalHelper.normalizeVariableName(getText() );
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

}
