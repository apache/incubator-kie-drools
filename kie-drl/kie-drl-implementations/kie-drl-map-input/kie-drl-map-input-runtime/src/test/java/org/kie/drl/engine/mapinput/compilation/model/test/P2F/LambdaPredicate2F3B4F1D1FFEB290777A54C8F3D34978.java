package org.kie.drl.engine.mapinput.compilation.model.test.P2F;

@org.drools.compiler.kie.builder.MaterializedLambda()
public enum LambdaPredicate2F3B4F1D1FFEB290777A54C8F3D34978 implements org.drools.model.functions.Predicate1<org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication>, org.drools.model.functions.HashedExpression {

    INSTANCE;

    public static final String EXPRESSION_HASH = "C54DA0C38F6F39AF9D91CEBBC8B711BD";

    public String getExpressionHash() {
        return EXPRESSION_HASH;
    }

    @Override()
    public boolean test(org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication _this) throws Exception {
        return org.drools.modelcompiler.util.EvaluationUtil.greaterOrEqualNumbers(_this.getDeposit(), 1000);
    }

    @Override()
    public org.drools.model.functions.PredicateInformation predicateInformation() {
        org.drools.model.functions.PredicateInformation info = new org.drools.model.functions.PredicateInformation("deposit >= 1000");
        info.addRuleNames("LargeDepositApprove", "org/kie/kogito/legacy/LoanRules.drl", "LargeDepositReject", "org/kie/kogito/legacy/LoanRules.drl");
        return info;
    }
}
