package org.drools.model.codegen.execmodel.generator.expressiontyper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.github.javaparser.ast.expr.Expression;

import static org.drools.util.StringUtils.lcFirstForBean;

public class ExpressionTyperContext {

    private Set<String> usedDeclarations = new LinkedHashSet<>();
    private Set<String> reactOnProperties = new HashSet<>();
    private List<Expression> prefixExpresssions = new ArrayList<>();
    private Expression originalExpression;

    private boolean registerPropertyReactivity = true;

    private Optional<Expression> inlineCastExpression = Optional.empty();
    private List<Expression> nullSafeExpressions = new ArrayList<>();

    private Set<String> variablesFromDifferentPattern = new HashSet<>();

    public void addUsedDeclarations(String name) {
        usedDeclarations.add(name);
    }

    public Set<String> getUsedDeclarations() {
        return usedDeclarations;
    }

    public void addReactOnProperties(String prop) {
        if (registerPropertyReactivity) {
            reactOnProperties.add( lcFirstForBean( prop ) );
        }
    }

    public Set<String> getReactOnProperties() {
        return reactOnProperties;
    }

    public List<Expression> getPrefixExpresssions() {
        return prefixExpresssions;
    }

    public void addPrefixExpression(int index, Expression prefixExpresssion) {
        prefixExpresssions.add(index, prefixExpresssion);
    }

    public void addPrefixExpression(Expression prefixExpresssion) {
        prefixExpresssions.add(prefixExpresssion);
    }

    public void setRegisterPropertyReactivity( boolean registerPropertyReactivity ) {
        this.registerPropertyReactivity = registerPropertyReactivity;
    }

    public boolean isRegisterPropertyReactivity() {
        return registerPropertyReactivity;
    }

    public Optional<Expression> getInlineCastExpression() {
        return inlineCastExpression;
    }

    public void setInlineCastExpression(Optional<Expression> inlineCastExpression) {
        this.inlineCastExpression = inlineCastExpression;
    }

    public List<Expression> getNullSafeExpressions() {
        return nullSafeExpressions;
    }

    public void addNullSafeExpression(int index, Expression nullSafeExpression) {
        nullSafeExpressions.add(index, nullSafeExpression);
    }

    public Expression getOriginalExpression() {
        return originalExpression;
    }

    public void setOriginalExpression(Expression originalExpression) {
        this.originalExpression = originalExpression;
    }

    public Set<String> getVariablesFromDifferentPattern() {
        return variablesFromDifferentPattern;
    }

    public void addVariableFromDifferentPattern(String variable) {
        variablesFromDifferentPattern.add(variable);
    }
}
