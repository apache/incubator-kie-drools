package org.kie.drl.engine.mapinput.compilation.model.test.PE7;

@org.drools.compiler.kie.builder.MaterializedLambda()
public enum LambdaExtractorE7AC7861C0CAFC6F617FD43B3B32B4DC implements org.drools.model.functions.Function1<org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication, Integer>, org.drools.model.functions.HashedExpression {

    INSTANCE;

    public static final String EXPRESSION_HASH = "8ADD269A8469EDE03075457D4A9555A5";

    public String getExpressionHash() {
        return EXPRESSION_HASH;
    }

    @Override()
    public Integer apply(org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication _this) {
        return _this.getAmount();
    }
}
