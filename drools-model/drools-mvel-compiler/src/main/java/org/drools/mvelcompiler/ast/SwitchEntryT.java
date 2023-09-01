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
package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntry;

public class SwitchEntryT implements TypedExpression {

    private final NodeList<Expression> labels;
    private final List<TypedExpression> statements;

    public SwitchEntryT(NodeList<Expression> labels, List<TypedExpression> statements) {
        this.labels = labels;
        this.statements = statements;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.empty();
    }

    @Override
    public Node toJavaExpression() {
        SwitchEntry entry = new SwitchEntry();
        entry.setLabels(labels);
        entry.setStatements(NodeList.nodeList(statements.stream().map(TypedExpression::toJavaExpression)
                                                  .map(Statement.class::cast)
                                                  .collect(Collectors.toList())));
        return entry;
    }
}
