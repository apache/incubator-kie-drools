package org.drools.mvelcompiler;

import java.util.function.Consumer;

import org.drools.Person;
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
    public void testModify() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{  modify($p) { setCanDrink(true); } }",
             "{ ($p).setCanDrink(true); update($p); }",
             result -> assertThat(allUsedBindings(result)).containsExactlyInAnyOrder("$p"));
    }

    @Test
    public void testModifyWithLambda() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{  modify($p) {  setCanDrinkLambda(() -> true); } }",
             "{ ($p).setCanDrinkLambda(() -> true); update($p); }",
             result -> assertThat(allUsedBindings(result)).containsExactlyInAnyOrder("$p"));
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