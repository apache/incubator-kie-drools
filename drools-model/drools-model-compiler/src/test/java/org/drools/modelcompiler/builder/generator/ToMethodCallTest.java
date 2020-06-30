/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.modelcompiler.builder.generator;

import java.util.HashSet;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.drools.core.addon.ClassTypeResolver;
import org.drools.core.addon.TypeResolver;
import org.drools.modelcompiler.domain.Person;
import org.drools.mvel.parser.ast.expr.DrlxExpression;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ToMethodCallTest {

    final TypeResolver typeResolver = new ClassTypeResolver(new HashSet<>(), getClass().getClassLoader());

    @Test
    public void transformMethodExpressionToMethodCallExpressionTypeSafe() {

        final Expression expr = StaticJavaParser.parseExpression("address.city.startsWith(\"M\")");
        final Expression expr1 = StaticJavaParser.parseExpression("getAddress().city.startsWith(\"M\")");
        final Expression expr2 = StaticJavaParser.parseExpression("address.getCity().startsWith(\"M\")");

        final MethodCallExpr expected = StaticJavaParser.parseExpression("getAddress().getCity().startsWith(\"M\")");

        assertEquals(expected.toString(), new ToMethodCall(typeResolver).toMethodCallWithClassCheck(expr, null, Person.class).getExpression().toString());
        assertEquals(expected.toString(), new ToMethodCall(typeResolver).toMethodCallWithClassCheck(expr1, null, Person.class).getExpression().toString());
        assertEquals(expected.toString(), new ToMethodCall(typeResolver).toMethodCallWithClassCheck(expr2, null, Person.class).getExpression().toString());
    }

    @Test
    public void transformMethodExpressionToMethodCallWithInlineCast() {
        typeResolver.addImport("org.drools.modelcompiler.domain.InternationalAddress");

        final DrlxExpression expr = DrlxParseUtil.parseExpression("address#InternationalAddress.state");
        final MethodCallExpr expected = StaticJavaParser.parseExpression("((InternationalAddress)getAddress()).getState()");

        assertEquals(expected.toString(), new ToMethodCall(typeResolver).toMethodCallWithClassCheck(expr.getExpr(), null, Person.class).getExpression().toString());
    }
}