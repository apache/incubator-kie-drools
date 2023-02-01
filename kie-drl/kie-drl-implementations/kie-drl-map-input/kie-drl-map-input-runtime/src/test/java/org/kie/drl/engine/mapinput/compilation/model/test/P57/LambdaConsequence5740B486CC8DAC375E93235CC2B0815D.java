package org.kie.drl.engine.mapinput.compilation.model.test.P57;

@org.drools.compiler.kie.builder.MaterializedLambda()
public enum LambdaConsequence5740B486CC8DAC375E93235CC2B0815D implements org.drools.model.functions.Block2<java.util.List, org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication>, org.drools.model.functions.HashedExpression {

    INSTANCE;

    public static final String EXPRESSION_HASH = "CA34BCD33DDAA89E96848F4A310F97F3";

    public String getExpressionHash() {
        return EXPRESSION_HASH;
    }

    @Override()
    public void execute(java.util.List approvedApplications, org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication $l) throws Exception {
        approvedApplications.add($l);
    }
}
