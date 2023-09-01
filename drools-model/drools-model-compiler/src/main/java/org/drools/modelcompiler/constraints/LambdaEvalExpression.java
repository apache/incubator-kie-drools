package org.drools.modelcompiler.constraints;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.accessor.EvalExpression;
import org.drools.model.SingleConstraint;

public class LambdaEvalExpression implements EvalExpression {

    private final ConstraintEvaluator evaluator;

    public LambdaEvalExpression(ConstraintEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    public LambdaEvalExpression(Declaration[] declarations, SingleConstraint constraint) {
        this( new ConstraintEvaluator(declarations, constraint) );
    }

    public LambdaEvalExpression(Pattern pattern, SingleConstraint constraint) {
        this( new ConstraintEvaluator(pattern, constraint) );
    }

    @Override
    public Object createContext() {
        return null;
    }

    @Override
    public boolean evaluate(BaseTuple tuple, Declaration[] requiredDeclarations, ValueResolver valueResolver, Object context) throws Exception {
        return evaluator.evaluate(tuple.getFactHandle(), tuple, valueResolver);
    }

    @Override
    public void replaceDeclaration(Declaration declaration, Declaration resolved) {
        evaluator.replaceDeclaration(declaration, resolved);
    }

    @Override
    public EvalExpression clone() {
        return new LambdaEvalExpression( evaluator.clone() );
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other != null && getClass() == other.getClass() && evaluator.equals( (( LambdaEvalExpression ) other).evaluator );
    }

    @Override
    public int hashCode() {
        return evaluator.hashCode();
    }

    public static final EvalExpression EMPTY = new EvalExpression() {
        @Override
        public Object createContext() {
            return null;
        }

        @Override
        public boolean evaluate(BaseTuple tuple, Declaration[] requiredDeclarations, ValueResolver valueResolver, Object context) throws Exception {
            return true;
        }

        @Override
        public void replaceDeclaration(Declaration declaration, Declaration resolved) { }

        @Override
        public EvalExpression clone() {
            return this;
        }
    };
}
