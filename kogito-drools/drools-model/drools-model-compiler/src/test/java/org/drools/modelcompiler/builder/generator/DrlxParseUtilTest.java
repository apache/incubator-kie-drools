package org.drools.modelcompiler.builder.generator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Function;

import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.expr.BooleanLiteralExpr;
import org.drools.javaparser.ast.expr.CharLiteralExpr;
import org.drools.javaparser.ast.expr.DoubleLiteralExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.IntegerLiteralExpr;
import org.drools.javaparser.ast.expr.LongLiteralExpr;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.javaparser.ast.expr.NullLiteralExpr;
import org.drools.javaparser.ast.expr.StringLiteralExpr;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.util.ClassUtil;
import org.junit.Test;
import org.kie.soup.project.datamodel.commons.types.ClassTypeResolver;
import org.kie.soup.project.datamodel.commons.types.TypeResolver;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getExpressionType;
import static org.junit.Assert.assertEquals;

public class DrlxParseUtilTest {

    @Test
    public void prependTest() {

        final Expression expr = JavaParser.parseExpression("getAddressName().startsWith(\"M\")");
        final NameExpr nameExpr = new NameExpr("_this");


        final Expression concatenated = DrlxParseUtil.prepend(nameExpr, expr);

        assertEquals("_this.getAddressName().startsWith(\"M\")", concatenated.toString());

    }

    @Test(expected = UnsupportedOperationException.class)
    public void throwExceptionWhenMissingNode() {

        final Expression expr = JavaParser.parseExpression("this");

        DrlxParseUtil.prepend(null, expr);

    }

    final TypeResolver typeResolver = new ClassTypeResolver(new HashSet<>(), getClass().getClassLoader());

    @Test
    public void transformMethodExpressionToMethodCallExpressionTypeSafe() {

        final Expression expr = JavaParser.parseExpression("address.city.startsWith(\"M\")");
        final Expression expr1 = JavaParser.parseExpression("getAddress().city.startsWith(\"M\")");
        final Expression expr2 = JavaParser.parseExpression("address.getCity().startsWith(\"M\")");

        final MethodCallExpr expected = JavaParser.parseExpression("getAddress().getCity().startsWith(\"M\")");

        assertEquals(expected.toString(), DrlxParseUtil.toMethodCallWithClassCheck(null, expr, Person.class, typeResolver).getExpression().toString());
        assertEquals(expected.toString(), DrlxParseUtil.toMethodCallWithClassCheck(null, expr1, Person.class, typeResolver).getExpression().toString());
        assertEquals(expected.toString(), DrlxParseUtil.toMethodCallWithClassCheck(null, expr2, Person.class, typeResolver).getExpression().toString());
    }

    @Test
    public void getExpressionTypeTest() {
        assertEquals(Double.class, getExpressionType(null, typeResolver, JavaParser.parseExpression("new Double[]{2.0d, 3.0d}[1]"), null));
        assertEquals(Float.class, getExpressionType(null, typeResolver, JavaParser.parseExpression("new Float[]{2.0d, 3.0d}"), null));
        assertEquals(Boolean.class, getExpressionType(null, typeResolver, new BooleanLiteralExpr(true), null));
        assertEquals(Character.class, getExpressionType(null, typeResolver, new CharLiteralExpr('a'), null));
        assertEquals(Double.class, getExpressionType(null, typeResolver, new DoubleLiteralExpr(2.0d), null));
        assertEquals(Integer.class, getExpressionType(null, typeResolver, new IntegerLiteralExpr(2), null));
        assertEquals(Long.class, getExpressionType(null, typeResolver, new LongLiteralExpr(2l), null));
        assertEquals(ClassUtil.NullType.class, getExpressionType(null, typeResolver, new NullLiteralExpr(), null));
        assertEquals(String.class, getExpressionType(null, typeResolver, new StringLiteralExpr(""), null));
    }

    @Test
    public void test_forceCastForName() {
        Function<String, String> c = (String input) -> {
            Expression expr = JavaParser.parseExpression(input);
            DrlxParseUtil.forceCastForName("$my", JavaParser.parseType("Integer"), expr);
            return expr.toString();
        };
        assertEquals("ciao += (Integer) $my", c.apply("ciao += $my"));
        assertEquals("ciao.add((Integer) $my)", c.apply("ciao.add($my)"));
        assertEquals("ciao.asd.add((Integer) $my)", c.apply("ciao.asd.add($my)"));
    }

    @Test
    public void test_rescopeNamesToNewScope() {
        Function<String, String> c = (String input) -> {
            Expression expr = JavaParser.parseExpression(input);
            DrlxParseUtil.rescopeNamesToNewScope(new NameExpr("nscope"), Arrays.asList("name", "surname"), expr);
            return expr.toString();
        };
        assertEquals("nscope.name = \"John\"", c.apply("name = \"John\" "));
        assertEquals("nscope.name = nscope.surname", c.apply("name = surname"));
    }
}