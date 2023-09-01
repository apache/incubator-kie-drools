package org.drools.mvelcompiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.drools.Gender;
import org.drools.Person;
import org.drools.mvelcompiler.context.MvelCompilerContext;
import org.drools.util.ClassTypeResolver;
import org.drools.util.TypeResolver;

import static org.assertj.core.api.Assertions.assertThat;

interface CompilerTest {

    default void test(Consumer<MvelCompilerContext> testFunction,
                      String inputExpression,
                      String expectedResult,
                      Consumer<CompiledBlockResult> resultAssert) {
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
        CompiledBlockResult compiled = new MvelCompiler(mvelCompilerContext).compileStatement(inputExpression);
        verifyBodyWithBetterDiff(expectedResult, compiled.resultAsString());
        resultAssert.accept(compiled);
    }

    default void verifyBodyWithBetterDiff(Object expected, Object actual) {
        try {
            assertThat(actual).asString().isEqualToIgnoringWhitespace(expected.toString());
        } catch (AssertionError e) {
            assertThat(actual).isEqualTo(expected);
        }
    }

    default void test(String inputExpression,
                      String expectedResult,
                      Consumer<CompiledBlockResult> resultAssert) {
        test(id -> {
        }, inputExpression, expectedResult, resultAssert);
    }

    default void test(Consumer<MvelCompilerContext> testFunction,
                      String inputExpression,
                      String expectedResult) {
        test(testFunction, inputExpression, expectedResult, t -> {
        });
    }

    default void test(String inputExpression,
                      String expectedResult) {
        test(d -> {
        }, inputExpression, expectedResult, t -> {
        });
    }

    default Collection<String> allUsedBindings(CompiledBlockResult result) {
        return new ArrayList<>(result.getUsedBindings());
    }
}
