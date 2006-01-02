package org.drools.spi;

public interface LiteralExpressionConstraint {
    public boolean isAllowed(Object object,
                             BaseEvaluator comparator);
}
