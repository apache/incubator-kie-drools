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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.impl.MapBackedType;

public class ContextTypeNode extends TypeNode {

    private final Map<String, TypeNode> gen;

    public ContextTypeNode(ParserRuleContext ctx, Map<String, TypeNode> gen) {
        super( ctx );
        this.gen = new HashMap<>(gen);
    }

    public ContextTypeNode(Map<String, TypeNode> gen, String text) {
        this.gen = gen;
        this.setText(text);
    }

    @Override
    public Type evaluate(EvaluationContext ctx) {
        return new MapBackedType("[anonymous]", evalTypes(ctx, gen));
    }

    public static Map<String, Type> evalTypes(EvaluationContext ctx, Map<String, TypeNode> gen) {
        Map<String, Type> fields = new HashMap<>();
        for (Entry<String, TypeNode> kv : gen.entrySet()) {
            fields.put(kv.getKey(), kv.getValue().evaluate(ctx));
        }
        return fields;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

    public Map<String, TypeNode> getGen() {
        return gen;
    }
}
