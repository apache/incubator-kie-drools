package org.kie.dmn.feel.lang.impl;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.codegen.feel11.ProcessedExpression;
import org.kie.dmn.feel.lang.CompiledExpression;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.ast.ASTNode;
import org.kie.dmn.feel.lang.ast.FunctionInvocationNode;
import org.kie.dmn.feel.parser.feel11.profiles.KieExtendedFEELProfile;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class TemporalConstantFoldingParserTest {

    @Parameters(name = "{0}")
    public static Iterable<? extends Object> data() {
        return Arrays.asList("date(\"2021-02-13\")",
                             "date(2021, 2, 13)",
                             "date and time(\"2021-02-13T21:34:00\")",
                             "time(\"21:34:00\"",
                             "time(21, 34, 0)",
                             "duration(\"P1Y\")",
                             "duration(\"P2DT20H14M\")");
    }

    @Parameterized.Parameter(0)
    public String expression;

    static final FEEL FEEL_STRICT = FEEL.newInstance();
    static final FEEL FEEL_KIE = FEEL.newInstance(List.of(new KieExtendedFEELProfile()));

    @Test
    public void testStrict() {
        CompilerContext ctx = FEEL_STRICT.newCompilerContext();
        CompiledExpression compile = FEEL_STRICT.compile(expression, ctx);
        ASTNode ast = extractAST(compile);
        assertThat(ast).isInstanceOf(FunctionInvocationNode.class);
        assertThat(((FunctionInvocationNode) ast).getTcFolded()).isNotNull();
    }

    @Test
    public void testKie() {
        CompilerContext ctx = FEEL_KIE.newCompilerContext();
        CompiledExpression compile = FEEL_KIE.compile(expression, ctx);
        ASTNode ast = extractAST(compile);
        assertThat(ast).isInstanceOf(FunctionInvocationNode.class);
        assertThat(((FunctionInvocationNode) ast).getTcFolded()).isNotNull();
    }

    private ASTNode extractAST(CompiledExpression compile) {
        return ((ProcessedExpression) compile).getInterpreted().getASTNode();
    }

}
