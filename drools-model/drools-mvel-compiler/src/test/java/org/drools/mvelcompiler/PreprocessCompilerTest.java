package org.drools.mvelcompiler;

import java.util.function.Consumer;

import org.drools.Person;
import org.drools.mvelcompiler.context.MvelCompilerContext;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class PreprocessCompilerTest implements CompilerTest {

    @Test
    public void testUncompiledMethod() {
        test("{modify( (List)$toEdit.get(0) ){ setEnabled( true ) }}",
             "{ { ((List) $toEdit.get(0)).setEnabled(true); } }",
             result -> assertThat(allUsedBindings(result)).isEmpty());
    }

    @Test
    public void testModify() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{  modify($p) { setCanDrink(true); } }",
             "{ { ($p).setCanDrink(true); } update($p); }",
             result -> assertThat(allUsedBindings(result)).containsExactlyInAnyOrder("$p"));
    }

    @Test
    public void testModifyWithLambda() {
        test(ctx -> ctx.addDeclaration("$p", Person.class),
             "{  modify($p) {  setCanDrinkLambda(() -> true); } }",
             "{ { ($p).setCanDrinkLambda(() -> true); } update($p); }",
             result ->assertThat(allUsedBindings(result)).containsExactlyInAnyOrder("$p"));
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
             result -> assertThat(allUsedBindings(result)).containsExactlyInAnyOrder("$fact"));
    }

    @Test
    public void testMultiLineStringLiteral() {
        test(" { java.lang.String s = \"\"\"\n" +
                     "                      Pikachu\n" +
                     "                      Is\n" +
                     "                      Yellow\n" +
                     "                      \"\"\"; " +
                     "}",
             " { java.lang.String s = \"Pikachu\\nIs\\nYellow\\n\"; }");
    }

    @Test
    public void testMultiLineStringLiteralAsMethodCallExpr() {
        test(" { java.lang.String s = \"\"\"\n" +
                     "                      Charmander\n" +
                     "                      Is\n" +
                     "                      Red\n" +
                     "                      \"\"\"" +
                     ".formatted(2); " +
                     "        " +
                     "}",
             " { java.lang.String s = \"Charmander\\nIs\\nRed\\n\".formatted(2); }");
    }

    @Test
    public void testMultiLineStringWithStringCharacterInside() {
        test(" { java.lang.String s = \"\"\"\n" +
                     "                      Bulbasaur\n" +
                     "                      Is\n" +
                     "                      \"Green\"\n" +
                     "                      \"\"\";\n" +
                     "}",
             " { java.lang.String s = \"Bulbasaur\\nIs\\n\\\"Green\\\"\\n\"; }");
    }

    @Override
    public void test(Consumer<MvelCompilerContext> testFunction,
                      String inputExpression,
                      String expectedResult,
                      Consumer<CompiledBlockResult> resultAssert) {
        CompiledBlockResult compiled = new PreprocessCompiler().compile(inputExpression);
        assertThat(compiled.resultAsString()).isEqualToIgnoringWhitespace(expectedResult);
        resultAssert.accept(compiled);
    }
}