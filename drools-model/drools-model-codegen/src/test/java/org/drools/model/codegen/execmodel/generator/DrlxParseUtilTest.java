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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.function.Function;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.expr.*;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.generator.DrlxParseUtil.RemoveRootNodeResult;
import org.drools.mvelcompiler.CompiledBlockResult;
import org.drools.mvelcompiler.MvelCompilerException;
import org.drools.util.ClassTypeResolver;
import org.drools.util.MethodUtils;
import org.drools.util.TypeResolver;
import org.junit.jupiter.api.Test;

import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.THIS_PLACEHOLDER;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.findRemoveRootNodeViaScope;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.getExpressionType;

public class DrlxParseUtilTest {

    @Test
    public void prependTest() {

        final Expression expr = StaticJavaParser.parseExpression("getAddressName().startsWith(\"M\")");
        final NameExpr nameExpr = new NameExpr(THIS_PLACEHOLDER);

        final Expression concatenated = DrlxParseUtil.prepend(nameExpr, expr);

        assertThat(concatenated.toString()).isEqualTo(THIS_PLACEHOLDER + ".getAddressName().startsWith(\"M\")");
    }

    @Test
    public void prependTestWithCast() {

        final Expression expr = StaticJavaParser.parseExpression("((InternationalAddress) getAddress()).getState()");
        final NameExpr nameExpr = new NameExpr(THIS_PLACEHOLDER);

        final Expression concatenated = DrlxParseUtil.prepend(nameExpr, expr);

        assertThat(concatenated.toString()).isEqualTo("((InternationalAddress) _this.getAddress()).getState()");
    }

    @Test
    public void prependTestWithThis() {

        final Expression expr = StaticJavaParser.parseExpression("((Person) this).getName()");
        final NameExpr nameExpr = new NameExpr(THIS_PLACEHOLDER);

        final Expression concatenated = DrlxParseUtil.prepend(nameExpr, expr);

        assertThat(concatenated.toString()).isEqualTo("((Person) _this).getName()");
    }

    final TypeResolver typeResolver = new ClassTypeResolver(new HashSet<>(), getClass().getClassLoader());

    @Test
    public void getExpressionTypeTest() {
        assertThat(getExpressionType(null, typeResolver, StaticJavaParser.parseExpression("new Double[]{2.0d, 3.0d}[1]"), null)).isEqualTo(Double.class);
        assertThat(getExpressionType(null, typeResolver, StaticJavaParser.parseExpression("new Float[]{2.0d, 3.0d}"), null)).isEqualTo(Float.class);
        assertThat(getExpressionType(null, typeResolver, new BooleanLiteralExpr(true), null)).isEqualTo(boolean.class);
        assertThat(getExpressionType(null, typeResolver, new CharLiteralExpr('a'), null)).isEqualTo(char.class);
        assertThat(getExpressionType(null, typeResolver, new DoubleLiteralExpr(2.0d), null)).isEqualTo(double.class);
        assertThat(getExpressionType(null, typeResolver, new IntegerLiteralExpr(2), null)).isEqualTo(int.class);
        assertThat(getExpressionType(null, typeResolver, new LongLiteralExpr(2l), null)).isEqualTo(long.class);
        assertThat(getExpressionType(null, typeResolver, new NullLiteralExpr(), null)).isEqualTo(MethodUtils.NullType.class);
        assertThat(getExpressionType(null, typeResolver, new StringLiteralExpr(""), null)).isEqualTo(String.class);
    }

    @Test
    public void test_forceCastForName() {
        Function<String, String> c = (String input) -> {
            Expression expr = StaticJavaParser.parseExpression(input);
            DrlxParseUtil.forceCastForName("$my", StaticJavaParser.parseType("Integer"), expr);
            return expr.toString();
        };
        assertThat(c.apply("ciao += $my")).isEqualTo("ciao += ((Integer) $my)");
        assertThat(c.apply("ciao.add($my)")).isEqualTo("ciao.add(((Integer) $my))");
        assertThat(c.apply("ciao.asd.add($my)")).isEqualTo("ciao.asd.add(((Integer) $my))");
    }

    @Test
    public void test_rescopeNamesToNewScope() {
        Function<String, String> c = (String input) -> {
            Expression expr = StaticJavaParser.parseExpression(input);
            DrlxParseUtil.rescopeNamesToNewScope(new NameExpr("nscope"), Arrays.asList("name", "surname"), expr);
            return expr.toString();
        };
        assertThat(c.apply("name = \"John\" ")).isEqualTo("nscope.name = \"John\"");
        assertThat(c.apply("name = surname")).isEqualTo("nscope.name = nscope.surname");
    }

    @Test
    public void test_rescopeAlsoArgumentsToNewScope() {
        Function<String, String> c = (String input) -> {
            Expression expr = StaticJavaParser.parseExpression(input);
            DrlxParseUtil.rescopeNamesToNewScope(new NameExpr("nscope"), Collections.singletonList("total"), expr);
            return expr.toString();
        };
        assertThat(c.apply("new Integer(total) ")).isEqualTo("new Integer(nscope.total)");
    }

    @Test
    public void removeRootNodeTest() {
        assertThat(findRemoveRootNodeViaScope(expr("sum"))).isEqualTo(new RemoveRootNodeResult(of(expr("sum")), expr("sum"), expr("sum")));
        assertThat(findRemoveRootNodeViaScope(expr("$a.getAge()"))).isEqualTo(new RemoveRootNodeResult(of(expr("$a")), expr("getAge()"), expr("getAge()")));
        assertThat(findRemoveRootNodeViaScope(expr("$c.convert($length)"))).isEqualTo(new RemoveRootNodeResult(of(expr("$c")), expr("convert($length)"), expr("convert($length)")));
        assertThat(findRemoveRootNodeViaScope(expr("$data.getValues().get(0)"))).isEqualTo(new RemoveRootNodeResult(of(expr("$data")), expr("getValues().get(0)"), expr("getValues()")));
        assertThat(findRemoveRootNodeViaScope(expr("$data.getIndexes().getValues().get(0)"))).isEqualTo(new RemoveRootNodeResult(of(expr("$data")), expr("getIndexes().getValues().get(0)"), expr("getIndexes()")));
    }

    @Test
    public void test_transformDrlNameExprToNameExpr_maintainingMethodArgumentOrder(){
        Expression e = expr("UtilsClass.utilityMethodWithLotsOfArguments($arg1, $arg2.field1, $arg3.field2, $arg4.field3, $arg5.field4))");
        Expression transformed = DrlxParseUtil.transformDrlNameExprToNameExpr(e);
        assertThat(transformed.getChildNodes().size()).isEqualTo(7);
        // First two child nodes are UtilsClass and utilityMethodWithLotsOfArguments
        assertThat(transformed.asMethodCallExpr().getChildNodes().get(2) instanceof NameExpr).isTrue();
        assertThat(transformed.asMethodCallExpr().getChildNodes().get(2)).isEqualTo(new NameExpr("$arg1"));
        assertThat(transformed.asMethodCallExpr().getChildNodes().get(3) instanceof FieldAccessExpr).isTrue();
        assertThat(transformed.asMethodCallExpr().getChildNodes().get(3)).isEqualTo(new FieldAccessExpr(new NameExpr("$arg2"), "field1"));
        assertThat(transformed.asMethodCallExpr().getChildNodes().get(4) instanceof FieldAccessExpr).isTrue();
        assertThat(transformed.asMethodCallExpr().getChildNodes().get(4)).isEqualTo(new FieldAccessExpr(new NameExpr("$arg3"), "field2"));
        assertThat(transformed.asMethodCallExpr().getChildNodes().get(5) instanceof FieldAccessExpr).isTrue();
        assertThat(transformed.asMethodCallExpr().getChildNodes().get(5)).isEqualTo(new FieldAccessExpr(new NameExpr("$arg4"), "field3"));
        assertThat(transformed.asMethodCallExpr().getChildNodes().get(6) instanceof FieldAccessExpr).isTrue();
        assertThat(transformed.asMethodCallExpr().getChildNodes().get(6)).isEqualTo(new FieldAccessExpr(new NameExpr("$arg5"), "field4"));
    }

    private Expression expr(String $a) {
        return DrlxParseUtil.parseExpression($a).getExpr();
    }

    @Test
    public void createMvelCompiler_withDrools() {
        String mvelBlock = "{ drools.workingMemory.setGlobal(\"list\", new java.util.ArrayList()); }";
        RuleContext context = createFakeRuleContext();
        CompiledBlockResult compile;
        try {
            compile = DrlxParseUtil.createMvelCompiler(context, true).compileStatement(mvelBlock);
            assertThat(compile.statementResults().toString()).isEqualToIgnoringWhitespace("{ drools.getWorkingMemory().setGlobal(\"list\", new java.util.ArrayList()); }");
        } catch (MvelCompilerException e) {
            fail("Failed while compiling statement", e);
        }
    }

    private RuleContext createFakeRuleContext() {
        PackageModel packageModel = new PackageModel("org.example:mvel-compiler-test:1.0.0", "org.example", null, null, new DRLIdGenerator());
        TypeResolver typeResolver = new ClassTypeResolver(new HashSet<>(), getClass().getClassLoader());
        RuleContext context = new RuleContext(null, null, packageModel, typeResolver, null);
        return context;
    }
}