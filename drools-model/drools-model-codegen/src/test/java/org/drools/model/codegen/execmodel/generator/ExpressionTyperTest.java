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

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.domain.InternationalAddress;
import org.drools.model.codegen.execmodel.domain.Overloaded;
import org.drools.model.codegen.execmodel.domain.Person;
import org.drools.model.codegen.execmodel.generator.expressiontyper.CannotTypeExpressionException;
import org.drools.model.codegen.execmodel.generator.expressiontyper.ExpressionTyper;
import org.drools.model.codegen.execmodel.generator.expressiontyper.TypedExpressionResult;
import org.drools.model.codegen.execmodel.inlinecast.ICA;
import org.drools.model.codegen.execmodel.inlinecast.ICAbstractA;
import org.drools.model.codegen.execmodel.inlinecast.ICAbstractB;
import org.drools.model.codegen.execmodel.inlinecast.ICAbstractC;
import org.drools.model.codegen.execmodel.inlinecast.ICB;
import org.drools.model.codegen.execmodel.inlinecast.ICC;
import org.drools.mvel.parser.ast.expr.DrlxExpression;
import org.drools.mvel.parser.ast.expr.PointFreeExpr;
import org.drools.mvel.parser.printer.PrintUtil;
import org.drools.util.ClassTypeResolver;
import org.drools.util.TypeResolver;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.THIS_PLACEHOLDER;

public class ExpressionTyperTest {

    private HashSet<String> imports;
    private PackageModel packageModel;
    private TypeResolver typeResolver;
    private RuleContext ruleContext;
    private KnowledgeBuilderImpl knowledgeBuilder = new KnowledgeBuilderImpl();
    private RuleDescr ruleDescr = new RuleDescr("testRule");

    @Before
    public void setUp() throws Exception {
        imports = new HashSet<>();
        packageModel = new PackageModel("", "", null, null, new DRLIdGenerator());
        typeResolver = new ClassTypeResolver(imports, getClass().getClassLoader());
        ruleContext = new RuleContext(knowledgeBuilder, knowledgeBuilder, packageModel, typeResolver, ruleDescr);
        imports.add(Person.class.getCanonicalName());
    }

    @Test
    public void toTypedExpressionTest() {
        assertThat(toTypedExpression("$mark.age", null, aPersonDecl("$mark")).getExpression().toString()).isEqualTo("$mark.getAge()");
        assertThat(toTypedExpression("$p.name", null, aPersonDecl("$p")).getExpression().toString()).isEqualTo("$p.getName()");

        assertThat(toTypedExpression("name.length", Person.class).getExpression().toString()).isEqualTo(THIS_PLACEHOLDER + ".getName().length()");

        assertThat(toTypedExpression("method(5,9,\"x\")", Overloaded.class).getExpression().toString()).isEqualTo(THIS_PLACEHOLDER + ".method(5, 9, \"x\")");
        assertThat(toTypedExpression("address.getCity().length", Person.class).getExpression().toString()).isEqualTo(THIS_PLACEHOLDER + ".getAddress().getCity().length()");
    }

    @Test
    public void inlineCastTest() {
        String result = "((" + Person.class.getCanonicalName() + ") _this).getName()";
        assertThat(toTypedExpression("this#Person.name", Object.class).getExpression().toString()).isEqualTo(result);
    }

    @Test
    public void inlineCastTest2() {
        addInlineCastImport();
        String result = "((" + ICC.class.getCanonicalName() + ") ((" + ICB.class.getCanonicalName() + ") _this.getSomeB()).getSomeC()).onlyConcrete()";
        assertThat(toTypedExpression("someB#ICB.someC#ICC.onlyConcrete() ", ICA.class).getExpression().toString()).isEqualTo(result);
    }

    @Test
    public void inlineCastTest3() {
        addInlineCastImport();
        String result = "((" + ICB.class.getCanonicalName() + ") _this.getSomeB()).onlyConcrete()";
        assertThat(toTypedExpression("someB#ICB.onlyConcrete()", ICA.class).getExpression().toString()).isEqualTo(result);
    }

    @Test
    public void pointFreeTest() {
        final PointFreeExpr expression = new PointFreeExpr(null, new NameExpr("name"), NodeList.nodeList(new StringLiteralExpr("[A-Z]")), new SimpleName("matches"), false, null, null, null, null);
        TypedExpressionResult typedExpressionResult = new ExpressionTyper(ruleContext, Person.class, null, true).toTypedExpression(expression);
        final TypedExpression actual = typedExpressionResult.getTypedExpression().get();
        final TypedExpression expected = typedResult("D.eval(org.drools.model.operators.MatchesOperator.INSTANCE, _this.getName(), \"[A-Z]\")", String.class);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testBigDecimalConstant() {
        final TypedExpression expected = typedResult("java.math.BigDecimal.ONE", BigDecimal.class);
        final TypedExpression actual = toTypedExpression("java.math.BigDecimal.ONE", null);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testBigDecimalLiteral() {
        final TypedExpression expected = typedResult("13.111B", BigDecimal.class);
        final TypedExpression actual = toTypedExpression("13.111B", null);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testBooleanComparison() {
        final TypedExpression expected = typedResult(THIS_PLACEHOLDER + ".getAge() == 18", int.class);
        final TypedExpression actual = toTypedExpression("age == 18", Person.class);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testAssignment() {
        final TypedExpression expected = typedResult("total = total + $cheese.getPrice()", Integer.class);
        final TypedExpression actual = toTypedExpression("total = total + $cheese.price", Object.class,
                                                         new TypedDeclarationSpec("$cheese", Cheese.class),
                                                         new TypedDeclarationSpec("total", Integer.class));
        assertThat(actual).isEqualTo(expected);
    }

    public static class Cheese {
        private Integer price;

        public Integer getPrice() {
            return price;
        }
    }

    @Test
    public void arrayAccessExpr() {
        final TypedExpression expected = typedResult(THIS_PLACEHOLDER + ".getItems().get(1)", Integer.class);
        final TypedExpression actual = toTypedExpression("items[1]", Person.class);
        assertThat(actual).isEqualTo(expected);

        final TypedExpression expected2 = typedResult(THIS_PLACEHOLDER + ".getItems().get(((Integer)1))", Integer.class);
        final TypedExpression actual2 = toTypedExpression("items[(Integer)1]", Person.class);
        assertThat(actual2).isEqualTo(expected2);
    }

    @Test
    public void mapAccessExpr() {
        final TypedExpression expected3 = typedResult(THIS_PLACEHOLDER + ".get(\"type\")", Object.class);
        final TypedExpression actual3 = toTypedExpression("this[\"type\"]", Map.class);
        assertThat(actual3).isEqualTo(expected3);
    }

    @Test
    public void mapAccessExpr2() {
        final TypedExpression expected3 = typedResult("$p.getItems().get(\"type\")", Integer.class, "$p.items[\"type\"]");
        final TypedExpression actual3 = toTypedExpression("$p.items[\"type\"]", Object.class, new TypedDeclarationSpec("$p", Person.class));
        assertThat(actual3).isEqualTo(expected3);
    }

    @Test
    public void mapAccessExpr3() {
        final TypedExpression expected = typedResult("$p.getItems().get(1)", Integer.class, "$p.items[1]");
        final TypedExpression actual = toTypedExpression("$p.items[1]", Object.class,
                                                         new TypedDeclarationSpec("$p", Person.class));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void arrayAccessExprDeclaration() {
        final TypedExpression expected = typedResult("$data.getValues().get(0)", Integer.class, "$data.values[0]");
        final TypedExpression actual = toTypedExpression("$data.values[0]", Object.class,
                                                         new TypedDeclarationSpec("$data", Data.class));
        assertThat(actual).isEqualTo(expected);
    }

    public static class Data {
        private List<Integer> values;

        public Data(List<Integer> values) {
            this.values = values;
        }

        public List<Integer> getValues() {
            return values;
        }
    }


    @Test
    public void testAssignment2() {
        assertThat(toTypedExpression("name.length", Person.class).getExpression().toString()).isEqualTo(THIS_PLACEHOLDER + ".getName().length()");

    }

    @Test
    public void transformMethodExpressionToMethodCallExpressionTypeSafe() {

        final String expr = StaticJavaParser.parseExpression("address.city.startsWith(\"M\")").toString();
        final String expr1 = StaticJavaParser.parseExpression("getAddress().city.startsWith(\"M\")").toString();
        final String expr2 = StaticJavaParser.parseExpression("address.getCity().startsWith(\"M\")").toString();

        final MethodCallExpr expected = StaticJavaParser.parseExpression("_this.getAddress().getCity().startsWith(\"M\")");

        assertThat(toTypedExpression(expr, Person.class).getExpression().toString()).isEqualTo(expected.toString());
        assertThat(toTypedExpression(expr1, Person.class).getExpression().toString()).isEqualTo(expected.toString());
        assertThat(toTypedExpression(expr2, Person.class).getExpression().toString()).isEqualTo(expected.toString());
    }

    @Test
    public void transformMethodExpressionToMethodCallWithInlineCast() {
        typeResolver.addImport(InternationalAddress.class.getCanonicalName());

        final DrlxExpression expr = DrlxParseUtil.parseExpression("address#InternationalAddress.state");
        final MethodCallExpr expected = StaticJavaParser.parseExpression("((" + InternationalAddress.class.getCanonicalName() + ")_this.getAddress()).getState()");

        assertThat(toTypedExpression(PrintUtil.printNode(expr.getExpr()), Person.class).getExpression().toString()).isEqualTo(PrintUtil.printNode(expected));
    }

    @Test
    public void halfBinaryOrAndAmpersand() {
        String expected = "_this.getAge() < 15 || _this.getAge() > 20 && _this.getAge() < 30";
        assertThat(toTypedExpression("age < 15 || > 20 && < 30", Person.class).getExpression().toString()).isEqualTo(expected);
    }

    @Test(expected = CannotTypeExpressionException.class)
    public void invalidHalfBinary() {
        toTypedExpression("> 20 && < 30", Person.class).getExpression();
    }

    @Test
    public void halfPointFreeOrAndAmpersand() {
        String expected = "D.eval(org.drools.model.operators.StringStartsWithOperator.INSTANCE, _this.getName(), \"M\") || D.eval(org.drools.model.operators.StringEndsWithOperator.INSTANCE, _this.getName(), \"a\") && D.eval(org.drools.model.operators.StringLengthWithOperator.INSTANCE, _this.getName(), 4)";
        assertThat(toTypedExpression("name str[startsWith] \"M\" || str[endsWith] \"a\" && str[length] 4", Person.class).getExpression().toString()).isEqualTo(expected);
    }

    @Test(expected = CannotTypeExpressionException.class)
    public void invalidHalfPointFree() {
        toTypedExpression("str[endsWith] \"a\" && str[length] 4", Person.class).getExpression();
    }

    @Test
    public void parseIntStringConcatenation() {
        TypedExpression typedExpression = toTypedExpression("Integer.parseInt('1' + this) > 3", String.class);
        assertThat(ruleContext.hasCompilationError()).isFalse();
        String expected = "Integer.parseInt('1' + _this) > 3";
        assertThat(typedExpression.getExpression().toString()).isEqualTo(expected);
    }

    @Test
    public void coercionInMethodArgument() {
        TypedExpression typedExpression = toTypedExpression("identityBigDecimal(money - 1)", Person.class);
        assertThat(ruleContext.hasCompilationError()).isFalse();
        String expected = "_this.identityBigDecimal(_this.getMoney().subtract(new java.math.BigDecimal(1), java.math.MathContext.DECIMAL128))";
        assertThat(typedExpression.getExpression().toString()).isEqualTo(expected);
    }

    @Test
    public void thisWithGetterReactivity() {
        Expression expression = DrlxParseUtil.parseExpression("_this.getAge() > 20").getExpr();
        TypedExpressionResult result = new ExpressionTyper(ruleContext, Person.class, null, true).toTypedExpression(expression);
        assertThat(result.getReactOnProperties()).containsExactly("age");
    }

    private TypedExpression toTypedExpression(String inputExpression, Class<?> patternType, TypedDeclarationSpec... declarations) {

        for(TypedDeclarationSpec d : declarations) {
            ruleContext.addDeclaration(d);
        }
        Expression expression = DrlxParseUtil.parseExpression(inputExpression).getExpr();
        return new ExpressionTyper(ruleContext, patternType, null, true).toTypedExpression(expression).getTypedExpression().get();
    }


    private TypedExpression typedResult(String expressionResult, Class<?> classResult) {
        Expression resultExpression = DrlxParseUtil.parseExpression(expressionResult).getExpr();
        return new TypedExpression(resultExpression, classResult);
    }

    private TypedExpression typedResult(String expressionResult, Class<?> classResult, String fieldName) {
        Expression resultExpression = DrlxParseUtil.parseExpression(expressionResult).getExpr();
        return new TypedExpression(resultExpression, classResult, fieldName);
    }

    private TypedDeclarationSpec aPersonDecl(String $mark) {
        return new TypedDeclarationSpec($mark, Person.class);
    }

    private void addInlineCastImport() {
        imports.add(ICAbstractA.class.getCanonicalName());
        imports.add(ICAbstractB.class.getCanonicalName());
        imports.add(ICAbstractC.class.getCanonicalName());
        imports.add(ICA.class.getCanonicalName());
        imports.add(ICB.class.getCanonicalName());
        imports.add(ICC.class.getCanonicalName());
    }

}
