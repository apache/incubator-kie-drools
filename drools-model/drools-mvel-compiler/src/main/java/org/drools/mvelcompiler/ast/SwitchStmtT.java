/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;

public class SwitchStmtT implements TypedExpression {

    private final TypedExpression selector;
    private final List<TypedExpression> entries;

    public SwitchStmtT(TypedExpression selector, List<TypedExpression> entries) {
        this.selector = selector;
        this.entries = entries;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.empty();
    }

    @Override
    public Node toJavaExpression() {
        SwitchStmt stmt = new SwitchStmt();
        stmt.setSelector((Expression) selector.toJavaExpression());

        stmt.setEntries(NodeList.nodeList(entries.stream().map(TypedExpression::toJavaExpression)
                                                 .map(SwitchEntry.class::cast)
                                                 .collect(Collectors.toList())));

        return stmt;
    }
}
