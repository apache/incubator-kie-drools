package org.drools.mvelcompiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.drools.mvelcompiler.context.MvelCompilerContext;
import org.kie.soup.project.datamodel.commons.types.ClassTypeResolver;
import org.kie.soup.project.datamodel.commons.types.TypeResolver;

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.*;

interface AbstractCompilerTest {

    default void test(Function<MvelCompilerContext, MvelCompilerContext> testFunction,
                      String actualExpression,
                      String expectedResult,
                      Consumer<ParsingResult> resultAssert) {
        Set<String> imports = new HashSet<>();
        // TODO: find which are the mvel implicit imports
        imports.add("java.util.List");
        imports.add("java.util.ArrayList");
        imports.add("java.util.HashMap");
        imports.add("java.util.Map");
        TypeResolver typeResolver = new ClassTypeResolver(imports, this.getClass().getClassLoader());
        MvelCompilerContext mvelCompilerContext = new MvelCompilerContext(typeResolver);
        testFunction.apply(mvelCompilerContext);
        ParsingResult compiled = new MvelCompiler(mvelCompilerContext).compile(actualExpression);
        assertThat(compiled.resultAsString(), equalToIgnoringWhiteSpace(expectedResult));
        resultAssert.accept(compiled);
    }

    default void test(String actualExpression,
                      String expectedResult,
                      Consumer<ParsingResult> resultAssert) {
        test(id -> id, actualExpression, expectedResult, resultAssert);
    }

    default void test(Function<MvelCompilerContext, MvelCompilerContext> testFunction,
                      String actualExpression,
                      String expectedResult) {
        test(testFunction, actualExpression, expectedResult, t -> {
        });
    }

    default void test(String actualExpression,
                      String expectedResult) {
        test(d -> d, actualExpression, expectedResult, t -> {
        });
    }

    default Collection<String> allModifiedProperties(ParsingResult result) {
        List<String> results = new ArrayList<>();
        for (Set<String> values : result.getModifyProperties().values()) {
            results.addAll(values);
        }
        return results;
    }
}
