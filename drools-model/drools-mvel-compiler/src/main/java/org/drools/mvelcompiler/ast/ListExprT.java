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

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import org.drools.mvel.parser.ast.expr.ListCreationLiteralExpression;
import org.drools.mvel.parser.ast.expr.ListCreationLiteralExpressionElement;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.github.javaparser.ast.NodeList.toNodeList;

public class ListExprT implements TypedExpression {

    private final ListCreationLiteralExpression listExpression;

    public ListExprT(final ListCreationLiteralExpression listExpression) {
        this.listExpression = listExpression;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.of(List.class);
    }

    @Override
    public Node toJavaExpression() {
        if (listExpression.getExpressions() == null || listExpression.getExpressions().isEmpty()) {
            return new MethodCallExpr(new NameExpr(Collections.class.getCanonicalName()), "emptyList");
        } else {
            return new MethodCallExpr(new NameExpr(List.class.getCanonicalName()), "of", getValueExpressionsFromListElements(listExpression.getExpressions()));
        }
    }

    private NodeList<Expression> getValueExpressionsFromListElements(final NodeList<Expression> listItems) {
        return listItems.stream()
                .filter(listItem -> listItem instanceof ListCreationLiteralExpressionElement)
                .map(listItem -> ((ListCreationLiteralExpressionElement) listItem).getValue())
                .collect(toNodeList());
    }
}
