package org.drools.modelcompiler.builder.generator.expressiontyper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.javaparser.ast.expr.Expression;

import static org.drools.core.util.StringUtils.lcFirst;

public class ExpressionTyperContext {

    private List<String> usedDeclarations = new ArrayList<>();
    private Set<String> reactOnProperties = new HashSet<>();
    private List<Expression> prefixExpresssions = new ArrayList<>();

    public List<String> getUsedDeclarations() {
        return usedDeclarations;
    }

    public void addReactOnProperties(String prop) {
        reactOnProperties.add(lcFirst(prop));
    }

    public Set<String> getReactOnProperties() {
        return reactOnProperties;
    }

    public List<Expression> getPrefixExpresssions() {
        return prefixExpresssions;
    }
}
