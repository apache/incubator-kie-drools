package org.drools.modelcompiler;

import com.github.javaparser.JavaParser;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.PrettyPrinter;
import com.github.javaparser.printer.PrettyPrinterConfiguration;
import org.junit.Test;

public class PrettyPrinterTest {

    @Test
    public void prettyPrintTest() {

        CompilationUnit parse = getCu();

        PrettyPrinterConfiguration config = new PrettyPrinterConfiguration();
        config.setColumnAlignParameters( true );
        config.setColumnAlignFirstMethodChain( true );

        PrettyPrinter pp = new PrettyPrinter(config );
        pp.print(parse);

    }

    private CompilationUnit getCu() {
        final String input = "package org.drools.compiler.test;\n" +
                "\n" +
                "import java.util.*;\n" +
                "import org.drools.model.*;\n" +
                "import org.drools.modelcompiler.dsl.flow.D;\n" +
                "import org.drools.model.Index.ConstraintType;\n" +
                "import java.time.*;\n" +
                "import java.time.format.*;\n" +
                "import java.text.*;\n" +
                "import org.drools.core.util.*;\n" +
                "import java.util.List;\n" +
                "import org.drools.modelcompiler.domain.Person;\n" +
                "import java.util.ArrayList;\n" +
                "import static org.drools.compiler.test.Rules732ea64e339f4e8e92460eae94f6a241.*;\n" +
                "\n" +
                "public class Rules732ea64e339f4e8e92460eae94f6a241RuleMethods0 {\n" +
                "\n" +
                "    /**\n" +
                "     * Rule name: look\n" +
                "     */\n" +
                "    public static org.drools.model.Rule rule_look() {\n" +
                "        final org.drools.model.Variable<org.drools.modelcompiler.domain.Person> var_$pattern_Person$1$ = D.declarationOf(org.drools.modelcompiler.domain.Person.class, \"$pattern_Person$1$\");\n" +
                "        final org.drools.model.Variable<java.lang.String> var_$l = D.declarationOf(java.lang.String.class, \"$l\");\n" +
                "        org.drools.model.Rule rule = D.rule(\"org.drools.compiler.test\", \"look\").build(D.bind(var_$l).as(var_$pattern_Person$1$, (_this) -> _this.getLikes()).reactOn(\"likes\"), queryDef_isContainedIn.call(true, var_$l, D.valueOf(\"office\")), D.execute((drools) -> {\n" +
                "            ((org.drools.modelcompiler.consequence.DroolsImpl) drools).asKnowledgeHelper().insertLogical(\"blah\");\n" +
                "        }));\n" +
                "        return rule;\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * Rule name: go1\n" +
                "     */\n" +
                "    public static org.drools.model.Rule rule_go1() {\n" +
                "        final org.drools.model.Variable<java.lang.String> var_$pattern_String$1$ = D.declarationOf(java.lang.String.class, \"$pattern_String$1$\");\n" +
                "        org.drools.model.Rule rule = D.rule(\"org.drools.compiler.test\", \"go1\").build(D.expr(\"$expr$5$\", var_$pattern_String$1$, (_this) -> org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(_this, \"go1\")), D.on(var_list).execute((drools, list) -> {\n" +
                "            list.add(((org.drools.modelcompiler.consequence.DroolsImpl) drools).asKnowledgeHelper().getRule().getName());\n" +
                "            drools.insert(new Location(\"lamp\", \"desk\"));\n" +
                "        }));\n" +
                "        return rule;\n" +
                "    }\n" +
                "}\n";

        return StaticJavaParser.parse(input);
    }
}
