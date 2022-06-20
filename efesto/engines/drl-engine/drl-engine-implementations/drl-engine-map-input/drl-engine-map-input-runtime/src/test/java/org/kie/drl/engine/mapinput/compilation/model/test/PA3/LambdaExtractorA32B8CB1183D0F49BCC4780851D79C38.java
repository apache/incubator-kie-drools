package org.kie.drl.engine.mapinput.compilation.model.test.PA3;

import static org.kie.drl.engine.mapinput.compilation.model.test.RulesED2A293F9C55BB1943AA9A6A1A8BF64C.*;
import org.kie.drl.engine.mapinput.compilation.model.test.*;
import org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication;
import org.drools.modelcompiler.dsl.pattern.D;

@org.drools.compiler.kie.builder.MaterializedLambda()
public enum LambdaExtractorA32B8CB1183D0F49BCC4780851D79C38 implements org.drools.model.functions.Function1<org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication, Integer>, org.drools.model.functions.HashedExpression {

    INSTANCE;

    public static final String EXPRESSION_HASH = "5F5777B2EF4333654A0381C33E47207C";

    public String getExpressionHash() {
        return EXPRESSION_HASH;
    }

    @Override()
    public Integer apply(org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication _this) {
        return _this.getDeposit();
    }
}
