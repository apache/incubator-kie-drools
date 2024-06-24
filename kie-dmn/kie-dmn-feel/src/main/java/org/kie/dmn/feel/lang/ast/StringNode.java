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

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.util.StringEvalHelper;

public class StringNode extends BaseNode {
    private final String value;

    public StringNode(ParserRuleContext ctx) {
        super( ctx );
        this.value = StringEvalHelper.unescapeString(getText());
    }

    public StringNode(String text) {
        this.value = StringEvalHelper.unescapeString(text);
        this.setText(text);
    }

    @Override
    public Object evaluate(EvaluationContext ctx) {
        return getValue();
    }

    public String getValue() {
        return value;
    }

    @Override
    public Type getResultType() {
        return BuiltInType.STRING;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}
