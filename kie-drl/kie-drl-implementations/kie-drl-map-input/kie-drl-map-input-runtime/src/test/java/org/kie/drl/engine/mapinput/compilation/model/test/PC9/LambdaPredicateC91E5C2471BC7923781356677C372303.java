package org.kie.drl.engine.mapinput.compilation.model.test.PC9;

@org.drools.compiler.kie.builder.MaterializedLambda()
public enum LambdaPredicateC91E5C2471BC7923781356677C372303 implements org.drools.model.functions.Predicate2<org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication, Integer>, org.drools.model.functions.HashedExpression {

    INSTANCE;

    public static final String EXPRESSION_HASH = "A5967CA0F0C913D79CFE515CDC73480E";

    public String getExpressionHash() {
        return EXPRESSION_HASH;
    }

    @Override()
    public boolean test(org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication _this, Integer maxAmount) throws Exception {
        return org.drools.modelcompiler.util.EvaluationUtil.greaterThanNumbers(_this.getAmount(), maxAmount);
    }

    @Override()
    public org.drools.model.functions.PredicateInformation predicateInformation() {
        org.drools.model.functions.PredicateInformation info = new org.drools.model.functions.PredicateInformation("amount > maxAmount");
        info.addRuleNames("LargeDepositReject", "org/kie/kogito/legacy/LoanRules.drl");
        return info;
    }
}
