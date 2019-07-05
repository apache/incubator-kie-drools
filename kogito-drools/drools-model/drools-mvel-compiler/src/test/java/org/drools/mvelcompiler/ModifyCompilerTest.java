package org.drools.mvelcompiler;

import java.util.function.Consumer;

import org.drools.mvelcompiler.context.MvelCompilerContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class ModifyCompilerTest implements CompilerTest {

    @Test
    public void testUncompiledMethod() {
        test("{modify( (List)$toEdit.get(0) ){ setEnabled( true ) }}",
             "{ ((List) $toEdit.get(0)).setEnabled(true); }",
             result -> assertThat(allUsedBindings(result)).isEmpty());
    }

    @Test
    public void testNestedModify() {
        test("{    if ($fact.getResult() != null) {\n" +
                     "        $fact.setResult(\"OK\");\n" +
                     "    } else {\n" +
                     "        modify ($fact) {\n" +
                     "            setResult(\"FIRST\")\n" +
                     "        }\n" +
                                 "    }}",
             " { " +
                     "if ($fact.getResult() != null) { " +
                     "  $fact.setResult(\"OK\"); " +
                     "} else { " +
                         "($fact).setResult(\"FIRST\"); " +
                         "update($fact); " +
                     "} " +
                     "} ",
             result -> assertThat(allUsedBindings(result)).containsExactlyInAnyOrder("$fact"));
    }

    @Override
    public void test(Consumer<MvelCompilerContext> testFunction,
                      String actualExpression,
                      String expectedResult,
                      Consumer<ParsingResult> resultAssert) {
        ParsingResult compiled = new ModifyCompiler().compile(actualExpression);
        assertThat(compiled.resultAsString()).isEqualToIgnoringWhitespace(expectedResult);
        resultAssert.accept(compiled);
    }
}