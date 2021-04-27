package org.drools.mvelcompiler;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import org.drools.Gender;
import org.drools.Person;
import org.drools.core.addon.ClassTypeResolver;
import org.drools.core.addon.TypeResolver;
import org.drools.mvelcompiler.context.MvelCompilerContext;
import org.junit.Test;

public class ConstraintCompilerTest implements CompilerTest {

    @Test
    public void testBigDecimalPromotion() {
        testExpression(c -> {
                           c.setRootPattern(Person.class);
                           c.setRootTypePrefix("_this");
                       }, "salary + salary",
                       "_this.getSalary().add(_this.getSalary())");
    }

    public void testExpression(Consumer<MvelCompilerContext> testFunction,
                               String inputExpression,
                               String expectedResult,
                               Consumer<CompiledExpressionResult> resultAssert) {
        Set<String> imports = new HashSet<>();
        imports.add("java.util.List");
        imports.add("java.util.ArrayList");
        imports.add("java.util.HashMap");
        imports.add("java.util.Map");
        imports.add("java.math.BigDecimal");
        imports.add("org.drools.Address");
        imports.add(Person.class.getCanonicalName());
        imports.add(Gender.class.getCanonicalName());
        TypeResolver typeResolver = new ClassTypeResolver(imports, this.getClass().getClassLoader());
        MvelCompilerContext mvelCompilerContext = new MvelCompilerContext(typeResolver);
        testFunction.accept(mvelCompilerContext);
        CompiledExpressionResult compiled = new ConstraintCompiler(mvelCompilerContext).compileExpression(inputExpression);
        verifyBodyWithBetterDiff(expectedResult, compiled.resultAsString());
        resultAssert.accept(compiled);
    }

    void testExpression(Consumer<MvelCompilerContext> testFunction,
                        String inputExpression,
                        String expectedResult) {
        testExpression(testFunction, inputExpression, expectedResult, t -> {
        });
    }
}