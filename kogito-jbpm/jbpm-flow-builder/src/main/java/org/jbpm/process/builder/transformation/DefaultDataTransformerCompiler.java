/*
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
package org.jbpm.process.builder.transformation;

import java.util.Collections;
import java.util.List;

import org.jbpm.process.core.impl.DataTransformerRegistry;
import org.jbpm.workflow.core.impl.DataDefinition;
import org.jbpm.workflow.core.node.Transformation;
import org.kie.kogito.internal.utils.ConversionUtils;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

public class DefaultDataTransformerCompiler implements DataTransformerCompiler {

    @Override
    public String[] dialects() {
        return new String[] { "http://www.mvel.org/2.0" };
    }

    @Override
    public Expression compile(List<DataDefinition> inputs, List<DataDefinition> outputs, Transformation transformation) {
        Expression expr = null;
        expr = new FieldAccessExpr(new NameExpr(DataTransformerRegistry.class.getPackageName()), DataTransformerRegistry.class.getSimpleName());
        expr = new MethodCallExpr(expr, "get");
        expr = new MethodCallExpr(expr, "find", NodeList.nodeList(new StringLiteralExpr(transformation.getLanguage())));

        Expression emptyCollection = new MethodCallExpr(new FieldAccessExpr(new NameExpr(Collections.class.getPackageName()), Collections.class.getSimpleName()), "emptyMap");

        expr = new MethodCallExpr(expr, "compile", NodeList.<Expression> nodeList(
                new StringLiteralExpr(ConversionUtils.sanitizeString(transformation.getExpression())), emptyCollection));
        return expr;
    }

}
