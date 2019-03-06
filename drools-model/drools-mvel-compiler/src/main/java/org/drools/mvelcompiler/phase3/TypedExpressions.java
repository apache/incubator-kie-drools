package org.drools.mvelcompiler.phase3;

import java.util.List;
import java.util.stream.Collectors;

public class TypedExpressions {

    List<TypedExpression> typedExpressions;

    public TypedExpressions(List<TypedExpression> typedExpressions) {
        this.typedExpressions = typedExpressions;
    }

    public TypedExpression last() {
        return typedExpressions.get(typedExpressions.size() - 1);
    }

    @Override
    public String toString() {
        return "TypedExpressions{" +
                "typedExpressions=" + typedExpressions.stream().map(Object::toString).collect(Collectors.joining("\n\t")) +
                '}';
    }
}
