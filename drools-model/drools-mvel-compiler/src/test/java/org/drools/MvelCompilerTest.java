package org.drools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.drools.mvelcompiler.MvelCompiler;
import org.drools.mvelcompiler.ParsingResult;
import org.drools.mvelcompiler.context.MvelCompilerContext;
import org.junit.Test;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.*;

public class MvelCompilerTest {

    @Test
    public void testConvertPropertyToAccessor() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ $p.parent.getParent().name; } ",
             "{ $p.getParent().getParent().getName(); }");

        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ $p.getParent().parent.name; } ",
             "{ $p.getParent().getParent().getName(); }");

        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ $p.parent.parent.name; } ",
             "{ $p.getParent().getParent().getName(); }");

        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ $p.getParent().getParent().getName(); } ",
             "{ $p.getParent().getParent().getName(); }");
    }

    @Test
    public void testAccessorInArguments() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ insert(\"Modified person age to 1 for: \" + $p.name); }",
             "{ insert(\"Modified person age to 1 for: \" + $p.getName()); } ");
    }

    @Test
    public void testPublicField() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ $p.parentPublic.getParent().name; } ",
             "{ $p.parentPublic.getParent().getName(); }");

        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ $p.getParent().parentPublic.name; } ",
             "{ $p.getParent().parentPublic.getName(); }");
    }

    @Test
    public void testUncompiledMethod() {
        test("{ System.out.println(\"Hello World\"); }",
             "{ System.out.println(\"Hello World\"); }");
    }


    @Test
    public void testStringLength() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ $p.name.length; }",
             "{ $p.getName().length(); }");
    }

    @Test
    public void testAssignment() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ Person np = $p; np = $p; }",
             "{ org.drools.Person np = $p; np = $p; }");
    }

    @Test
    public void testSetter() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ $p.name = \"Luca\"; }",
             "{ $p.setName(\"Luca\"); }");
    }

    @Test
    public void testModify() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ modify ( $p )  { name = \"Luca\", age = 35 }; }",
             "{ $p.setName(\"Luca\"); $p.setAge(35); }",
             result -> assertThat(allModifiedProperties(result), containsInAnyOrder("name", "age")));
    }

    @Test
    public void testModifySemiColon() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ modify($p) { setAge(1); }; }",
             "{ $p.setAge(1); }",
             result -> assertThat(allModifiedProperties(result), containsInAnyOrder("age")));
    }

    @Test
    public void testModifyWithAssignment() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ modify($p) { age = $p.age+1 }; }",
             "{ $p.setAge($p.getAge() + 1); }",
             result -> assertThat(allModifiedProperties(result), containsInAnyOrder("age")));
    }

    private void test(Function<MvelCompilerContext, MvelCompilerContext> testFunction,
                      String actualExpression,
                      String expectedResult,
                      Consumer<ParsingResult> resultAssert) {
        MvelCompilerContext mvelCompilerContext = new MvelCompilerContext();
        testFunction.apply(mvelCompilerContext);
        ParsingResult compiled = new MvelCompiler(mvelCompilerContext).compile(actualExpression);
        assertThat(compiled.resultAsString(), equalToIgnoringWhiteSpace(expectedResult));
        resultAssert.accept(compiled);
    }

    private void test(Function<MvelCompilerContext, MvelCompilerContext> testFunction,
                      String actualExpression,
                      String expectedResult) {
        test(testFunction, actualExpression, expectedResult, t -> {});
    }

    private void test(String actualExpression,
                      String expectedResult) {
        test(d -> d, actualExpression, expectedResult, t -> {});
    }

    private Collection<String> allModifiedProperties(ParsingResult result) {
        List<String> results = new ArrayList<>();
        for(Set<String> values : result.getModifyProperties().values()) {
            results.addAll(values);
        }
        return results;

    }
}