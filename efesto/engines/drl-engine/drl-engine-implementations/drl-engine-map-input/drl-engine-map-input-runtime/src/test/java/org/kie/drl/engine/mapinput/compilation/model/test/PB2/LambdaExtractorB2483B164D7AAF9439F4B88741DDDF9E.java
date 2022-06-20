package org.kie.drl.engine.mapinput.compilation.model.test.PB2;

import static org.kie.drl.engine.mapinput.compilation.model.test.RulesED2A293F9C55BB1943AA9A6A1A8BF64C.*;
import org.kie.drl.engine.mapinput.compilation.model.test.*;
import org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication;
import org.drools.modelcompiler.dsl.pattern.D;

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
