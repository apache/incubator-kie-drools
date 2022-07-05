package org.kie.drl.engine.mapinput.compilation.model.test.P89;

import static org.kie.drl.engine.mapinput.compilation.model.test.RulesED2A293F9C55BB1943AA9A6A1A8BF64C.*;
import org.kie.drl.engine.mapinput.compilation.model.test.*;
import org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication;
import org.drools.modelcompiler.dsl.pattern.D;

@org.drools.compiler.kie.builder.MaterializedLambda()
public enum LambdaPredicate896F205BB6DAC489283E534C8D4BF758 implements org.drools.model.functions.Predicate1<org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication>, org.drools.model.functions.HashedExpression {

    INSTANCE;

    public static final String EXPRESSION_HASH = "5AD734508C62D1F0E4299486F1DDA7B7";

    public String getExpressionHash() {
        return EXPRESSION_HASH;
    }

    @Override()
    public boolean test(org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication _this) throws Exception {
        return org.drools.modelcompiler.util.EvaluationUtil.lessThanNumbers(_this.getDeposit(), 1000);
    }

    @Override()
    public org.drools.model.functions.PredicateInformation predicateInformation() {
        org.drools.model.functions.PredicateInformation info = new org.drools.model.functions.PredicateInformation("deposit < 1000");
        info.addRuleNames("SmallDepositApprove", "org/kie/kogito/legacy/LoanRules.drl", "SmallDepositReject", "org/kie/kogito/legacy/LoanRules.drl");
        return info;
    }
}
