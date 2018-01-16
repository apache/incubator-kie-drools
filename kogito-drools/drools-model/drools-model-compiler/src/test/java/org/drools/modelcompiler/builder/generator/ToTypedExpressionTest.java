package org.drools.modelcompiler.builder.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.drools.drlx.DrlxParser;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.modelcompiler.domain.Overloaded;
import org.drools.modelcompiler.domain.Person;
import org.junit.Assert;
import org.junit.Test;
import org.kie.soup.project.datamodel.commons.types.ClassTypeResolver;
import org.kie.soup.project.datamodel.commons.types.TypeResolver;

import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertEquals;

public class ToTypedExpressionTest {

    private final HashSet<String> imports = new HashSet<>();

    {
        imports.add("org.drools.modelcompiler.domain.Person");
    }

    final TypeResolver typeResolver = new ClassTypeResolver(imports, getClass().getClassLoader());

    @Test
    public void toTypedExpressionTest() {
        assertEquals(typedResult("$mark.getAge()", int.class), toTypedExpression("$mark.age", null, aPersonDecl("$mark")));
        assertEquals(typedResult("$p.getName()", String.class), toTypedExpression("$p.name", null, aPersonDecl("$p")));

        final Set<String> reactOnProperties = new HashSet<>();
        assertEquals(typedResult("_this.getName().length()", int.class), toTypedExpression("name.length", Person.class, reactOnProperties));
        Assert.assertThat(reactOnProperties, hasItem("name"));

        assertEquals(typedResult("_this.method(5,9,\"x\")", int.class), toTypedExpression("method(5,9,\"x\")", Overloaded.class));
        assertEquals(typedResult("_this.getAddress().getAddressName().length()", int.class), toTypedExpression("address.getAddressName().length", Person.class));

        TypedExpression inlineCastResult = typedResult("((org.drools.modelcompiler.domain.Person) _this).getName()", String.class)
                .setPrefixExpression(DrlxParser.parseExpression("_this instanceof org.drools.modelcompiler.domain.Person").getExpr());
        assertEquals(inlineCastResult, toTypedExpression("this#Person.name", Object.class));

    }

    private TypedExpression toTypedExpression(String inputExpression, Class<?> patternType, Set<String> reactOnProperties, DeclarationSpec... declarations) {
        RuleContext ruleContext = new RuleContext(null, null, null, null);

        for(DeclarationSpec d : declarations) {
            ruleContext.addDeclaration(d);
        }
        Expression expression = DrlxParser.parseExpression(inputExpression).getExpr();
        return DrlxParseUtil.toTypedExpressionFromMethodCallOrField(ruleContext, patternType, expression, new ArrayList<>(), reactOnProperties, typeResolver);
    }

    private TypedExpression toTypedExpression(String inputExpression, Class<?> patternType, DeclarationSpec... declarations) {
        return toTypedExpression(inputExpression, patternType, new HashSet<>(), declarations);
    }

    private TypedExpression typedResult(String expressionResult, Class<?> classResult) {
        Expression resultExpression = DrlxParser.parseExpression(expressionResult).getExpr();
        return new TypedExpression(resultExpression, classResult);
    }

    private DeclarationSpec aPersonDecl(String $mark) {
        return new DeclarationSpec($mark, Person.class);
    }

}
