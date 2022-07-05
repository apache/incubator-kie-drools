package org.kie.drl.engine.mapinput.compilation.model.test.PF3;

import static org.kie.drl.engine.mapinput.compilation.model.test.RulesED2A293F9C55BB1943AA9A6A1A8BF64C.*;
import org.kie.drl.engine.mapinput.compilation.model.test.*;
import org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication;
import org.drools.modelcompiler.dsl.pattern.D;

@org.drools.compiler.kie.builder.MaterializedLambda()
public enum LambdaPredicateF388D9370A499303354D5F588D65FFF8 implements org.drools.model.functions.Predicate2<org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication, Integer>, org.drools.model.functions.HashedExpression {

    INSTANCE;

    public static final String EXPRESSION_HASH = "886720A3084F72AADDAA4E2E65824CBD";

    public String getExpressionHash() {
        return EXPRESSION_HASH;
    }

    @Override()
    public boolean test(org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication _this, Integer maxAmount) throws Exception {
        return org.drools.modelcompiler.util.EvaluationUtil.lessOrEqualNumbers(_this.getAmount(), maxAmount);
    }

    @Override()
    public org.drools.model.functions.PredicateInformation predicateInformation() {
        org.drools.model.functions.PredicateInformation info = new org.drools.model.functions.PredicateInformation("amount <= maxAmount");
        info.addRuleNames("LargeDepositApprove", "org/kie/kogito/legacy/LoanRules.drl");
        return info;
    }
}
