package org.drools.mvel;

public class ExpressionCompiler {

    private String ex;

    public ExpressionCompiler(String ex) {
        this.ex = ex;
    }

    public ExpressionCompiler(String ex, ParserContext parserContext) {
        this.ex = ex;
    }

    public CompiledExpression compile() {
        return new CompiledExpression(ex);
    }
}
