package org.drools;

import java.util.function.Consumer;
import java.util.function.Function;

import org.drools.mvelcompiler.MvelCompiler;
import org.drools.mvelcompiler.ParsingResult;
import org.drools.mvelcompiler.context.MvelCompilerContext;
import org.junit.Ignore;
import org.junit.Test;

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
    public void testStringLength() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ $p.name.length; }",
             "{ $p.getName().length(); }");
    }

    @Test
    public void testAssignment() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ Person np = $p; }",
             "{ org.drools.Person np = $p; }");
    }

    @Test
    public void testMultiLineAssignment() {
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
    @Ignore
    public void testModify() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{ modify ( $p )  { name = \"Luca\", age = \"35\" }; }",
             "{ $p.setName(\"Luca\"); $p.setAge(35); }",
             result -> {
                 // check it has "name" and "age" as property
             });
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
}