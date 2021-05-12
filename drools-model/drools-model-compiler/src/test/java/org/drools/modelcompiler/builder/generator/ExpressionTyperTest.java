package org.drools.modelcompiler.builder.generator;

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
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.core.addon.ClassTypeResolver;
import org.drools.core.addon.TypeResolver;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.expressiontyper.ExpressionTyper;
import org.drools.modelcompiler.builder.generator.expressiontyper.TypedExpressionResult;
import org.drools.modelcompiler.domain.Overloaded;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.inlinecast.ICA;
import org.drools.modelcompiler.inlinecast.ICAbstractA;
import org.drools.modelcompiler.inlinecast.ICAbstractB;
import org.drools.modelcompiler.inlinecast.ICAbstractC;
import org.drools.modelcompiler.inlinecast.ICB;
import org.drools.modelcompiler.inlinecast.ICC;
import org.drools.mvel.parser.ast.expr.DrlxExpression;
import org.drools.mvel.parser.ast.expr.PointFreeExpr;
import org.junit.Before;
import org.junit.Test;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.THIS_PLACEHOLDER;
import static org.junit.Assert.assertEquals;

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
        ruleContext = new RuleContext(knowledgeBuilder, packageModel, typeResolver, ruleDescr);
        imports.add("org.drools.modelcompiler.domain.Person");
    }

    @Test
    public void toTypedExpressionTest() {
        assertEquals("$mark.getAge()", toTypedExpression("$mark.age", null, aPersonDecl("$mark")).getExpression().toString());
        assertEquals("$p.getName()", toTypedExpression("$p.name", null, aPersonDecl("$p")).getExpression().toString());

        assertEquals(THIS_PLACEHOLDER + ".getName().length()", toTypedExpression("name.length", Person.class).getExpression().toString());

        assertEquals(THIS_PLACEHOLDER + ".method(5, 9, \"x\")", toTypedExpression("method(5,9,\"x\")", Overloaded.class).getExpression().toString());
        assertEquals(THIS_PLACEHOLDER + ".getAddress().getCity().length()", toTypedExpression("address.getCity().length", Person.class).getExpression().toString());
    }

    @Test
    public void inlineCastTest() {
        String result = "((org.drools.modelcompiler.domain.Person) _this).getName()";
        assertEquals(result, toTypedExpression("this#Person.name", Object.class).getExpression().toString());
    }

    @Test
    public void inlineCastTest2() {
        addInlineCastImport();
        String result = "((org.drools.modelcompiler.inlinecast.ICC) ((org.drools.modelcompiler.inlinecast.ICB) _this.getSomeB()).getSomeC()).onlyConcrete()";
        assertEquals(result, toTypedExpression("someB#ICB.someC#ICC.onlyConcrete() ", ICA.class).getExpression().toString());
    }

    @Test
    public void inlineCastTest3() {
        addInlineCastImport();
        String result = "((org.drools.modelcompiler.inlinecast.ICB) _this.getSomeB()).onlyConcrete()";
        assertEquals(result, toTypedExpression("someB#ICB.onlyConcrete()", ICA.class).getExpression().toString());
    }

    @Test
    public void pointFreeTest() {
        final PointFreeExpr expression = new PointFreeExpr(null, new NameExpr("name"), NodeList.nodeList(new StringLiteralExpr("[A-Z]")), new SimpleName("matches"), false, null, null, null, null);
        TypedExpressionResult typedExpressionResult = new ExpressionTyper(ruleContext, Person.class, null, true).toTypedExpression(expression);
        final TypedExpression actual = typedExpressionResult.getTypedExpression().get();
        final TypedExpression expected = typedResult("D.eval(org.drools.model.operators.MatchesOperator.INSTANCE, _this.getName(), \"[A-Z]\")", String.class);
        assertEquals(expected, actual);
    }

    @Test
    public void testBigDecimalConstant() {
        final TypedExpression expected = typedResult("java.math.BigDecimal.ONE", BigDecimal.class);
        final TypedExpression actual = toTypedExpression("java.math.BigDecimal.ONE", null);
        assertEquals(expected, actual);
    }

    @Test
    public void testBigDecimalLiteral() {
        final TypedExpression expected = typedResult("13.111B", BigDecimal.class);
        final TypedExpression actual = toTypedExpression("13.111B", null);
        assertEquals(expected, actual);
    }

    @Test
    public void testBooleanComparison() {
        final TypedExpression expected = typedResult(THIS_PLACEHOLDER + ".getAge() == 18", int.class);
        final TypedExpression actual = toTypedExpression("age == 18", Person.class);
        assertEquals(expected, actual);
    }

    @Test
    public void testAssignment() {
        final TypedExpression expected = typedResult("total = total + $cheese.getPrice()", Integer.class);
        final TypedExpression actual = toTypedExpression("total = total + $cheese.price", Object.class,
                                                         new DeclarationSpec("$cheese", Cheese.class),
                                                         new DeclarationSpec("total", Integer.class));
        assertEquals(expected, actual);
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
        assertEquals(expected, actual);

        final TypedExpression expected2 = typedResult(THIS_PLACEHOLDER + ".getItems().get(((Integer)1))", Integer.class);
        final TypedExpression actual2 = toTypedExpression("items[(Integer)1]", Person.class);
        assertEquals(expected2, actual2);
    }

    @Test
    public void mapAccessExpr() {
        final TypedExpression expected3 = typedResult(THIS_PLACEHOLDER + ".get(\"type\")", Map.class);
        final TypedExpression actual3 = toTypedExpression("this[\"type\"]", Map.class);
        assertEquals(expected3, actual3);
    }

    @Test
    public void mapAccessExpr2() {
        final TypedExpression expected3 = typedResult("$p.getItems().get(\"type\")", Integer.class, "$p.items[\"type\"]");
        final TypedExpression actual3 = toTypedExpression("$p.items[\"type\"]", Object.class, new DeclarationSpec("$p", Person.class));
        assertEquals(expected3, actual3);
    }

    @Test
    public void mapAccessExpr3() {
        final TypedExpression expected = typedResult("$p.getItems().get(1)", Integer.class, "$p.items[1]");
        final TypedExpression actual = toTypedExpression("$p.items[1]", Object.class,
                                                         new DeclarationSpec("$p", Person.class));
        assertEquals(expected, actual);
    }

    @Test
    public void arrayAccessExprDeclaration() {
        final TypedExpression expected = typedResult("$data.getValues().get(0)", Integer.class, "$data.values[0]");
        final TypedExpression actual = toTypedExpression("$data.values[0]", Object.class,
                                                         new DeclarationSpec("$data", Data.class));
        assertEquals(expected, actual);
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
        assertEquals(THIS_PLACEHOLDER + ".getName().length()", toTypedExpression("name.length", Person.class).getExpression().toString());

    }

    @Test
    public void transformMethodExpressionToMethodCallExpressionTypeSafe() {

        final String expr = StaticJavaParser.parseExpression("address.city.startsWith(\"M\")").toString();
        final String expr1 = StaticJavaParser.parseExpression("getAddress().city.startsWith(\"M\")").toString();
        final String expr2 = StaticJavaParser.parseExpression("address.getCity().startsWith(\"M\")").toString();

        final MethodCallExpr expected = StaticJavaParser.parseExpression("_this.getAddress().getCity().startsWith(\"M\")");

        assertEquals(expected.toString(), toTypedExpression(expr, Person.class).getExpression().toString());
        assertEquals(expected.toString(), toTypedExpression(expr1, Person.class).getExpression().toString());
        assertEquals(expected.toString(), toTypedExpression(expr2, Person.class).getExpression().toString());
    }

    @Test
    public void transformMethodExpressionToMethodCallWithInlineCast() {
        typeResolver.addImport("org.drools.modelcompiler.domain.InternationalAddress");

        final DrlxExpression expr = DrlxParseUtil.parseExpression("address#InternationalAddress.state");
        final MethodCallExpr expected = StaticJavaParser.parseExpression("((org.drools.modelcompiler.domain.InternationalAddress)_this.getAddress()).getState()");

        assertEquals(expected.toString(),  toTypedExpression(expr.getExpr().toString(), Person.class).getExpression().toString());
    }

    private TypedExpression toTypedExpression(String inputExpression, Class<?> patternType, DeclarationSpec... declarations) {

        for(DeclarationSpec d : declarations) {
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

    private DeclarationSpec aPersonDecl(String $mark) {
        return new DeclarationSpec($mark, Person.class);
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
