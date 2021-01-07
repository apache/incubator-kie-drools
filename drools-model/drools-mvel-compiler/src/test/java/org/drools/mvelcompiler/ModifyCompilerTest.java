package org.drools.mvelcompiler;

import java.util.function.Consumer;

import org.drools.Person;
import org.drools.mvelcompiler.context.MvelCompilerContext;
import org.junit.Test;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class ModifyCompilerTest implements CompilerTest {

    @Test
    public void testUncompiledMethod() {
        test("{modify( (List)$toEdit.get(0) ){ setEnabled( true ) }}",
             "{ { ((List) $toEdit.get(0)).setEnabled(true); } }",
             result -> assertThat(allUsedBindings(result), is(empty())));
    }

    @Test
    public void testModify() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{  modify($p) { setCanDrink(true); } }",
             "{ { ($p).setCanDrink(true); } update($p); }",
             result -> assertThat(allUsedBindings(result), containsInAnyOrder("$p")));
    }

    @Test
    public void testModifyWithLambda() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{  modify($p) {  setCanDrinkLambda(() -> true); } }",
             "{ { ($p).setCanDrinkLambda(() -> true); } update($p); }",
             result -> assertThat(allUsedBindings(result), containsInAnyOrder("$p")));
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
                         "{ " +
                         "  ($fact).setResult(\"FIRST\"); " +
                     "   } " +
                     "  update($fact); " +
                     "} " +
                     "} ",
             result -> assertThat(allUsedBindings(result), containsInAnyOrder("$fact")));
    }

    @Override
    public void test(Consumer<MvelCompilerContext> testFunction,
                      String inputExpression,
                      String expectedResult,
                      Consumer<ParsingResult> resultAssert) {
        ParsingResult compiled = new ModifyCompiler().compile(inputExpression);
        assertThat(compiled.resultAsString(), equalToIgnoringWhiteSpace(expectedResult));
        resultAssert.accept(compiled);
    }
}