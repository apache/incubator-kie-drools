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
package org.drools.model.codegen.execmodel.generator;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.drools.util.TypeResolver;
import org.junit.Test;

import static com.github.javaparser.ast.NodeList.nodeList;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.model.codegen.execmodel.generator.expressiontyper.FlattenScope.flattenScope;

public class FlattenScopeTest {

    @Test
    public void flattenUnaryExpression() {
        List<Node> actual = flattenScope(MockTypeResolver.INSTANCE, expr("getMessageId"));
        List<Node> expected = Collections.singletonList(new NameExpr("getMessageId"));
        compareArrays(actual, expected);
    }

    @Test
    public void flattenFields() {
        List<Node> actual = flattenScope(MockTypeResolver.INSTANCE, expr("Field.INT"));
        List<Node> expected = asList(new NameExpr("Field"), new SimpleName("INT"));

        compareArrays(actual, expected);
    }

    @Test
    public void flattenMethodCall() {
        List<Node> actual = flattenScope(MockTypeResolver.INSTANCE, expr("name.startsWith(\"M\")"));
        MethodCallExpr methodCallExpr = new MethodCallExpr(new NameExpr("name"), "startsWith",
                                                           nodeList(new StringLiteralExpr("M")));
        methodCallExpr.setTypeArguments(NodeList.nodeList());
        List<Node> expected = asList(new NameExpr("name"), methodCallExpr);
        compareArrays(actual, expected);
    }

    @Test
    public void flattenArrayAccess() {
        List<Node> actual = flattenScope(MockTypeResolver.INSTANCE, expr("$p.getChildrenA()[0]"));

        NameExpr name = new NameExpr("$p");
        final MethodCallExpr mc = new MethodCallExpr(name, "getChildrenA", nodeList());
        mc.setTypeArguments(NodeList.nodeList());
        List<Node> expected = asList(name, mc, new ArrayAccessExpr(mc, new IntegerLiteralExpr(0)));
        compareArrays(actual, expected);
    }

    private Expression expr(String inputExpr) {
        Expression expr = DrlxParseUtil.parseExpression(inputExpr).getExpr();
        // This is because parsing doesn't set type arguments.
        expr.ifMethodCallExpr(m -> m.setTypeArguments(nodeList()));
        expr.getChildNodes().forEach((Node e) -> {
            if (e instanceof Expression) {
                ((Expression) e).ifMethodCallExpr(m -> m.setTypeArguments(nodeList()));
            }
        });
        return expr;
    }

    private void compareArrays(List<Node> actual, List<Node> expected) {
        actual = actual.stream().map(DrlxParseUtil::transformDrlNameExprToNameExpr).collect(Collectors.toList());
        expected = expected.stream().map(DrlxParseUtil::transformDrlNameExprToNameExpr).collect(Collectors.toList());
        assertThat(actual).isEqualTo(expected);
    }

    public static class MockTypeResolver implements TypeResolver {
        public static final MockTypeResolver INSTANCE = new MockTypeResolver();

        @Override
        public Set<String> getImports() {
            return Collections.emptySet();
        }

        @Override
        public void addImport( String importEntry ) {
        }

        @Override
        public void addImplicitImport( String importEntry ) {
        }

        @Override
        public Class<?> resolveType( String className ) throws ClassNotFoundException {
            return getClassLoader().loadClass( className );
        }

        @Override
        public Class<?> resolveType( String className, ClassFilter classFilter ) throws ClassNotFoundException {
            return resolveType( className );
        }

        @Override
        public void registerClass( String className, Class<?> clazz ) {
        }

        @Override
        public String getFullTypeName( String shortName ) throws ClassNotFoundException {
            return resolveType( shortName ).getCanonicalName();
        }

        @Override
        public ClassLoader getClassLoader() {
            return MockTypeResolver.class.getClassLoader();
        }
    }
}