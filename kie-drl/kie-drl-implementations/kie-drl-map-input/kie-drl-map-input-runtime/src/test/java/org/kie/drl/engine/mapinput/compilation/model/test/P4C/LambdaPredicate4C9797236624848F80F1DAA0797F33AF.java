package org.kie.drl.engine.mapinput.compilation.model.test.P4C;

@org.drools.compiler.kie.builder.MaterializedLambda()
public enum LambdaPredicate4C9797236624848F80F1DAA0797F33AF implements org.drools.model.functions.Predicate1<org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication>, org.drools.model.functions.HashedExpression {

    INSTANCE;

    public static final String EXPRESSION_HASH = "E50F996647B5319E5C55264DB7710D13";

    public String getExpressionHash() {
        return EXPRESSION_HASH;
    }

    @Override()
    public boolean test(org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication _this) throws Exception {
        return org.drools.modelcompiler.util.EvaluationUtil.lessOrEqualNumbers(_this.getAmount(), 2000);
    }

    @Override()
    public org.drools.model.functions.PredicateInformation predicateInformation() {
        org.drools.model.functions.PredicateInformation info = new org.drools.model.functions.PredicateInformation("amount <= 2000");
        info.addRuleNames("SmallDepositApprove", "org/kie/kogito/legacy/LoanRules.drl");
        return info;
    }
}
