package org.kie.dmn.feel.runtime;

import org.kie.dmn.feel.lang.EvaluationContext;

public class UnaryTestImpl implements UnaryTest {
    private final String text;
    private final UnaryTest delegate;
   
    public UnaryTestImpl(UnaryTest delegate, String text) {
        this.text = text;
        this.delegate = delegate;
    }
   
    @Override
    public String toString() {
        return text;
    }
   
    @Override
    public Boolean apply(EvaluationContext ctx, Object obj) {
        return delegate.apply(ctx, obj);
    }
}
