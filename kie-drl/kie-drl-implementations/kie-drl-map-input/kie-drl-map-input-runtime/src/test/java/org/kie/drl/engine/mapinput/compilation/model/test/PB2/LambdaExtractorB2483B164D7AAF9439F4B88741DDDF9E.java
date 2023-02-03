package org.kie.drl.engine.mapinput.compilation.model.test.PB2;

@org.drools.compiler.kie.builder.MaterializedLambda()
public enum LambdaExtractorB2483B164D7AAF9439F4B88741DDDF9E implements org.drools.model.functions.Function1<org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication, Integer>, org.drools.model.functions.HashedExpression {

    INSTANCE;

    public static final String EXPRESSION_HASH = "65EEA0D96D129EC652A80982CA536D1F";

    public String getExpressionHash() {
        return EXPRESSION_HASH;
    }

    @Override()
    public Integer apply(org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication _this) {
        return _this.getApplicant().getAge();
    }
}
