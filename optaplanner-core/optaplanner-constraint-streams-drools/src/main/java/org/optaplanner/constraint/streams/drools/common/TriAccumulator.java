package org.optaplanner.constraint.streams.drools.common;

import java.util.Objects;
import java.util.function.Function;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.reteoo.Tuple;
import org.drools.core.rule.Declaration;
import org.drools.model.Variable;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;

final class TriAccumulator<A, B, C, ResultContainer_, Result_> extends AbstractAccumulator<ResultContainer_, Result_> {

    private final String varA;
    private final String varB;
    private final String varC;
    private final QuadFunction<ResultContainer_, A, B, C, Runnable> accumulator;

    private Function<Tuple, A> valueExtractorA;
    private Function<Tuple, B> valueExtractorB;
    private Function<Tuple, C> valueExtractorC;

    public TriAccumulator(Variable<A> varA, Variable<B> varB, Variable<C> varC,
            TriConstraintCollector<A, B, C, ResultContainer_, Result_> collector) {
        super(collector.supplier(), collector.finisher());
        this.accumulator = Objects.requireNonNull(collector.accumulator());
        this.varA = varA.getName();
        this.varB = varB.getName();
        this.varC = varC.getName();
    }

    @Override
    protected Runnable accumulate(ResultContainer_ context, Tuple leftTuple, InternalFactHandle handle,
            Declaration[] innerDeclarations) {
        A a = valueExtractorA.apply(leftTuple);
        B b = valueExtractorB.apply(leftTuple);
        C c = valueExtractorC.apply(leftTuple);
        return accumulator.apply(context, a, b, c);
    }

    @Override
    protected void initialize(Tuple leftTuple, Declaration[] innerDeclarations) {
        for (Declaration declaration : innerDeclarations) {
            if (declaration.getBindingName().equals(varA)) {
                valueExtractorA = getValueExtractor(declaration, leftTuple);
            } else if (declaration.getBindingName().equals(varB)) {
                valueExtractorB = getValueExtractor(declaration, leftTuple);
            } else if (declaration.getBindingName().equals(varC)) {
                valueExtractorC = getValueExtractor(declaration, leftTuple);
            }
        }
    }

}
