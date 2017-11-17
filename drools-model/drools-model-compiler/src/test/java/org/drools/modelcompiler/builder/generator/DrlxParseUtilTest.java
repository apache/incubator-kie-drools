package org.drools.modelcompiler.builder.generator;

import java.util.HashSet;
import java.util.Optional;

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
import static org.junit.Assert.*;

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

        final Expression expr = JavaParser.parseExpression("address.addressName.startsWith(\"M\")");
        final Expression expr1 = JavaParser.parseExpression("getAddress().addressName.startsWith(\"M\")");
        final Expression expr2 = JavaParser.parseExpression("address.getAddressName().startsWith(\"M\")");

        final MethodCallExpr expected = JavaParser.parseExpression("getAddress().getAddressName().startsWith(\"M\")");

        assertEquals(expected.toString(), DrlxParseUtil.toMethodCallWithClassCheck(expr, Person.class, typeResolver).getExpression().toString());
        assertEquals(expected.toString(), DrlxParseUtil.toMethodCallWithClassCheck(expr1, Person.class, typeResolver).getExpression().toString());
        assertEquals(expected.toString(), DrlxParseUtil.toMethodCallWithClassCheck(expr2, Person.class, typeResolver).getExpression().toString());
    }

    @Test
    public void findBindingFromFunctionCallExpression() {

        final Optional<String> bindingId = DrlxParseUtil.findBindingIdFromFunctionCallExpression("isFortyYearsOld($p)");
        assertEquals(Optional.of("$p"), bindingId);

        final Optional<String> bindingId2 = DrlxParseUtil.findBindingIdFromFunctionCallExpression("isFortyYearsOld(function2($p))");
        assertEquals(Optional.of("$p"), bindingId2);

        final Optional<String> bindingId3 = DrlxParseUtil.findBindingIdFromFunctionCallExpression("isCool($p.getValue())");
        assertEquals(Optional.of("$p"), bindingId3);

    }

    @Test
    public void getExpressionTypeTest() {
        assertEquals(Double.class, getExpressionType(typeResolver, JavaParser.parseExpression("new Double[]{2.0d, 3.0d}[1]")));
        assertEquals(Float.class, getExpressionType(typeResolver, JavaParser.parseExpression("new Float[]{2.0d, 3.0d}")));
        assertEquals(Boolean.class, getExpressionType(typeResolver, new BooleanLiteralExpr(true)));
        assertEquals(Character.class, getExpressionType(typeResolver, new CharLiteralExpr('a')));
        assertEquals(Double.class, getExpressionType(typeResolver, new DoubleLiteralExpr(2.0d)));
        assertEquals(Integer.class, getExpressionType(typeResolver, new IntegerLiteralExpr(2)));
        assertEquals(Long.class, getExpressionType(typeResolver, new LongLiteralExpr(2l)));
        assertEquals(ClassUtil.NullType.class, getExpressionType(typeResolver, new NullLiteralExpr()));
        assertEquals(String.class, getExpressionType(typeResolver, new StringLiteralExpr("")));
//        assertEquals(Locale.class, getExpressionType(typeResolver, new FieldAccessExpr(new NameExpr("Locale"), "US")));
    }


}