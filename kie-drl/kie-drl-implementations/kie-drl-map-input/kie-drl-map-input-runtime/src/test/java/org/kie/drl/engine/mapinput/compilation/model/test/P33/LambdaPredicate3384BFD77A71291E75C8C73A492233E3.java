package org.kie.drl.engine.mapinput.compilation.model.test.P33;

@org.drools.compiler.kie.builder.MaterializedLambda()
public enum LambdaPredicate3384BFD77A71291E75C8C73A492233E3 implements org.drools.model.functions.Predicate1<org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication>, org.drools.model.functions.HashedExpression {

    INSTANCE;

    public static final String EXPRESSION_HASH = "7957E1BDA895714016778CACEE79F458";

    public String getExpressionHash() {
        return EXPRESSION_HASH;
    }

    @Override()
    public boolean test(org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication _this) throws Exception {
        return org.drools.modelcompiler.util.EvaluationUtil.greaterOrEqualNumbers(_this.getApplicant().getAge(), 20);
    }

    @Override()
    public org.drools.model.functions.PredicateInformation predicateInformation() {
        org.drools.model.functions.PredicateInformation info = new org.drools.model.functions.PredicateInformation("applicant.age >= 20");
        info.addRuleNames("LargeDepositApprove", "org/kie/kogito/legacy/LoanRules.drl", "LargeDepositReject", "org/kie/kogito/legacy/LoanRules.drl", "SmallDepositApprove", "org/kie/kogito/legacy/LoanRules.drl", "SmallDepositReject", "org/kie/kogito/legacy/LoanRules.drl");
        return info;
    }
}
