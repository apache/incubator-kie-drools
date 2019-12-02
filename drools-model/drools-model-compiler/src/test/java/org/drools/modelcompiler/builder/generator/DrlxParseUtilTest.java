package org.drools.modelcompiler.builder.generator;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.function.Function;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.drools.core.addon.ClassTypeResolver;
import org.drools.core.addon.TypeResolver;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil.RemoveRootNodeResult;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.util.ClassUtil;
import org.junit.Test;

import static java.util.Optional.of;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.THIS_PLACEHOLDER;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.findRemoveRootNodeViaScope;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getExpressionType;
import static org.junit.Assert.assertEquals;

public class DrlxParseUtilTest {

    @Test
    public void prependTest() {

        final Expression expr = StaticJavaParser.parseExpression("getAddressName().startsWith(\"M\")");
        final NameExpr nameExpr = new NameExpr(THIS_PLACEHOLDER);

        final Expression concatenated = DrlxParseUtil.prepend(nameExpr, expr);

        assertEquals(THIS_PLACEHOLDER + ".getAddressName().startsWith(\"M\")", concatenated.toString());
    }

    @Test(expected = IllegalStateException.class)
    public void throwExceptionWhenMissingNode() {

        final Expression expr = StaticJavaParser.parseExpression("this");

        DrlxParseUtil.prepend(null, expr);
    }

    final TypeResolver typeResolver = new ClassTypeResolver(new HashSet<>(), getClass().getClassLoader());

    @Test
    public void transformMethodExpressionToMethodCallExpressionTypeSafe() {

        final Expression expr = StaticJavaParser.parseExpression("address.city.startsWith(\"M\")");
        final Expression expr1 = StaticJavaParser.parseExpression("getAddress().city.startsWith(\"M\")");
        final Expression expr2 = StaticJavaParser.parseExpression("address.getCity().startsWith(\"M\")");

        final MethodCallExpr expected = StaticJavaParser.parseExpression("getAddress().getCity().startsWith(\"M\")");

        assertEquals(expected.toString(), DrlxParseUtil.toMethodCallWithClassCheck(null, expr, null, Person.class, typeResolver).getExpression().toString());
        assertEquals(expected.toString(), DrlxParseUtil.toMethodCallWithClassCheck(null, expr1, null, Person.class, typeResolver).getExpression().toString());
        assertEquals(expected.toString(), DrlxParseUtil.toMethodCallWithClassCheck(null, expr2, null, Person.class, typeResolver).getExpression().toString());
    }

    @Test
    public void getExpressionTypeTest() {
        assertEquals(Double.class, getExpressionType(null, typeResolver, StaticJavaParser.parseExpression("new Double[]{2.0d, 3.0d}[1]"), null));
        assertEquals(Float.class, getExpressionType(null, typeResolver, StaticJavaParser.parseExpression("new Float[]{2.0d, 3.0d}"), null));
        assertEquals(boolean.class, getExpressionType(null, typeResolver, new BooleanLiteralExpr(true), null));
        assertEquals(char.class, getExpressionType(null, typeResolver, new CharLiteralExpr('a'), null));
        assertEquals(double.class, getExpressionType(null, typeResolver, new DoubleLiteralExpr(2.0d), null));
        assertEquals(int.class, getExpressionType(null, typeResolver, new IntegerLiteralExpr(2), null));
        assertEquals(long.class, getExpressionType(null, typeResolver, new LongLiteralExpr(2l), null));
        assertEquals(ClassUtil.NullType.class, getExpressionType(null, typeResolver, new NullLiteralExpr(), null));
        assertEquals(String.class, getExpressionType(null, typeResolver, new StringLiteralExpr(""), null));
    }

    @Test
    public void test_forceCastForName() {
        Function<String, String> c = (String input) -> {
            Expression expr = StaticJavaParser.parseExpression(input);
            DrlxParseUtil.forceCastForName("$my", StaticJavaParser.parseType("Integer"), expr);
            return expr.toString();
        };
        assertEquals("ciao += ((Integer) $my)", c.apply("ciao += $my"));
        assertEquals("ciao.add(((Integer) $my))", c.apply("ciao.add($my)"));
        assertEquals("ciao.asd.add(((Integer) $my))", c.apply("ciao.asd.add($my)"));
    }

    @Test
    public void test_rescopeNamesToNewScope() {
        Function<String, String> c = (String input) -> {
            Expression expr = StaticJavaParser.parseExpression(input);
            DrlxParseUtil.rescopeNamesToNewScope(new NameExpr("nscope"), Arrays.asList("name", "surname"), expr);
            return expr.toString();
        };
        assertEquals("nscope.name = \"John\"", c.apply("name = \"John\" "));
        assertEquals("nscope.name = nscope.surname", c.apply("name = surname"));
    }

    @Test
    public void test_rescopeAlsoArgumentsToNewScope() {
        Function<String, String> c = (String input) -> {
            Expression expr = StaticJavaParser.parseExpression(input);
            DrlxParseUtil.rescopeNamesToNewScope(new NameExpr("nscope"), Collections.singletonList("total"), expr);
            return expr.toString();
        };
        assertEquals("new Integer(nscope.total)", c.apply("new Integer(total) "));
    }

    @Test
    public void removeRootNodeTest() {
        assertEquals(new RemoveRootNodeResult(of(expr("sum")), expr("sum"), expr("sum")), findRemoveRootNodeViaScope(expr("sum")));
        assertEquals(new RemoveRootNodeResult(of(expr("$a")), expr("getAge()"), expr("getAge()")), findRemoveRootNodeViaScope(expr("$a.getAge()")));
        assertEquals(new RemoveRootNodeResult(of(expr("$c")), expr("convert($length)"), expr("convert($length)")), findRemoveRootNodeViaScope(expr("$c.convert($length)")));
        assertEquals(new RemoveRootNodeResult(of(expr("$data")), expr("getValues().get(0)"), expr("getValues()")), findRemoveRootNodeViaScope(expr("$data.getValues().get(0)")));
        assertEquals(new RemoveRootNodeResult(of(expr("$data")), expr("getIndexes().getValues().get(0)"), expr("getIndexes()")), findRemoveRootNodeViaScope(expr("$data.getIndexes().getValues().get(0)")));
    }

    private Expression expr(String $a) {
        return DrlxParseUtil.parseExpression($a).getExpr();
    }
}