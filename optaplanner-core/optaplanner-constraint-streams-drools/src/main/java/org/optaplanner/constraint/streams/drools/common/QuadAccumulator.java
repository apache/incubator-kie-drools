package org.optaplanner.constraint.streams.drools.common;

import java.util.Objects;
import java.util.function.Function;

import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;
import org.drools.model.Variable;
import org.kie.api.runtime.rule.FactHandle;
import org.optaplanner.core.api.function.PentaFunction;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;

final class QuadAccumulator<A, B, C, D, ResultContainer_, Result_>
        extends AbstractAccumulator<ResultContainer_, Result_> {

    private final String varA;
    private final String varB;
    private final String varC;
    private final String varD;
    private final PentaFunction<ResultContainer_, A, B, C, D, Runnable> accumulator;

    private Function<BaseTuple, A> valueExtractorA;
    private Function<BaseTuple, B> valueExtractorB;
    private Function<BaseTuple, C> valueExtractorC;
    private Function<BaseTuple, D> valueExtractorD;

    public QuadAccumulator(Variable<A> varA, Variable<B> varB, Variable<C> varC, Variable<D> varD,
            QuadConstraintCollector<A, B, C, D, ResultContainer_, Result_> collector) {
        super(collector.supplier(), collector.finisher());
        this.accumulator = Objects.requireNonNull(collector.accumulator());
        this.varA = varA.getName();
        this.varB = varB.getName();
        this.varC = varC.getName();
        this.varD = varD.getName();
    }

    @Override
    protected Runnable accumulate(ResultContainer_ context, BaseTuple leftTuple, FactHandle handle,
            Declaration[] innerDeclarations) {
        A a = valueExtractorA.apply(leftTuple);
        B b = valueExtractorB.apply(leftTuple);
        C c = valueExtractorC.apply(leftTuple);
        D d = valueExtractorD.apply(leftTuple);
        return accumulator.apply(context, a, b, c, d);
    }

    @Override
    protected void initialize(BaseTuple leftTuple, Declaration[] innerDeclarations) {
        for (Declaration declaration : innerDeclarations) {
            if (declaration.getBindingName().equals(varA)) {
                valueExtractorA = getValueExtractor(declaration, leftTuple);
            } else if (declaration.getBindingName().equals(varB)) {
                valueExtractorB = getValueExtractor(declaration, leftTuple);
            } else if (declaration.getBindingName().equals(varC)) {
                valueExtractorC = getValueExtractor(declaration, leftTuple);
            } else if (declaration.getBindingName().equals(varD)) {
                valueExtractorD = getValueExtractor(declaration, leftTuple);
            }
        }
    }

}
