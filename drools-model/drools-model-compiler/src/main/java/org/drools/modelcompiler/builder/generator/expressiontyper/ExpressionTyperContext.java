package org.drools.modelcompiler.builder.generator.expressiontyper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.javaparser.ast.expr.Expression;

public class ExpressionTyperContext {

    List<String> usedDeclarations = new ArrayList<>();
    Set<String> reactOnProperties = new HashSet<>();
    List<Expression> prefixExpresssions = new ArrayList<>();

    public List<String> getUsedDeclarations() {
        return usedDeclarations;
    }

    public Set<String> getReactOnProperties() {
        return reactOnProperties;
    }

    public List<Expression> getPrefixExpresssions() {
        return prefixExpresssions;
    }
}
