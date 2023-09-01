package org.kie.dmn.feel.lang.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.UnaryTest;

public class DashNode
        extends BaseNode {

    public DashNode(ParserRuleContext ctx) {
        super( ctx );
    }

    @Override
    public UnaryTest evaluate(EvaluationContext ctx) {
        // a dash is a unary test that always evaluates to true
        return DashUnaryTest.INSTANCE;
    }

    public static class DashUnaryTest implements UnaryTest {
        public static DashUnaryTest INSTANCE = new DashUnaryTest();

        private DashUnaryTest() {
        }

        @Override
        public Boolean apply(EvaluationContext evaluationContext, Object o) {
            return Boolean.TRUE;
        }

        @Override
        public String toString() {
            return "UnaryTest{-}";
        }
    }

    @Override
    public Type getResultType() {
        return BuiltInType.BOOLEAN;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}
