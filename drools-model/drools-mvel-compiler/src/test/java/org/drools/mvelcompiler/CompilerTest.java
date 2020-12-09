package org.drools.mvelcompiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.drools.Gender;
import org.drools.Person;
import org.drools.core.addon.ClassTypeResolver;
import org.drools.core.addon.TypeResolver;
import org.drools.mvelcompiler.context.MvelCompilerContext;
import org.hamcrest.MatcherAssert;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;

interface CompilerTest {

    default void test(Consumer<MvelCompilerContext> testFunction,
                      String actualExpression,
                      String expectedResult,
                      Consumer<ParsingResult> resultAssert) {
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
        ParsingResult compiled = new MvelCompiler(mvelCompilerContext).compile(actualExpression);
        verifyBodyWithBetterDiff(expectedResult, compiled.resultAsString());
        resultAssert.accept(compiled);
    }

    default void verifyBodyWithBetterDiff(Object expected, Object actual) {
        try {
            MatcherAssert.assertThat(actual.toString(), equalToIgnoringWhiteSpace(expected.toString()));
        } catch (AssertionError e) {
            MatcherAssert.assertThat(actual, equalTo(expected));
        }
    }

    default void test(String actualExpression,
                      String expectedResult,
                      Consumer<ParsingResult> resultAssert) {
        test(id -> {
        }, actualExpression, expectedResult, resultAssert);
    }

    default void test(Consumer<MvelCompilerContext> testFunction,
                      String actualExpression,
                      String expectedResult) {
        test(testFunction, actualExpression, expectedResult, t -> {
        });
    }

    default void test(String actualExpression,
                      String expectedResult) {
        test(d -> {
        }, actualExpression, expectedResult, t -> {
        });
    }

    default Collection<String> allUsedBindings(ParsingResult result) {
        return new ArrayList<>(result.getUsedBindings());
    }
}
