package org.drools.modelcompiler.builder.generator;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.core.base.TypeResolver;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.javaparser.ast.expr.Expression;

public class RuleContext {
    private final InternalKnowledgePackage pkg;
    private DRLExprIdGenerator exprIdGenerator;
    private final RuleDescr ruleDescr;

    Map<String, DeclarationSpec> declarations = new HashMap<>();
    List<QueryParameter> queryParameters = new ArrayList<>();
    Deque<Consumer<Expression>> exprPointer = new LinkedList<>();
    List<Expression> expressions = new ArrayList<>();
    Set<String> queryName = new HashSet<>();
    Map<String, String> namedConsequences = new HashMap<>();

    BaseDescr parentDesc = null;

    public RuleContext(InternalKnowledgePackage pkg, DRLExprIdGenerator exprIdGenerator, RuleDescr ruleDescr) {
        this.pkg = pkg;
        this.exprIdGenerator = exprIdGenerator;
        this.ruleDescr = ruleDescr;
        exprPointer.push( this.expressions::add );
    }

    public void addExpression(Expression e) {
        exprPointer.peek().accept(e);
    }
    public void pushExprPointer(Consumer<Expression> p) {
        exprPointer.push(p);
    }
    public Consumer<Expression> popExprPointer() {
        return exprPointer.pop();
    }
    public int getExprPointerLevel() {
        return exprPointer.size();
    }

    public InternalKnowledgePackage getPkg() {
        return pkg;
    }

    public String getExprId(Class<?> patternType, String drlConstraint) {
        return exprIdGenerator.getExprId(patternType, drlConstraint);
    }

    public String getConditionId(Class<?> patternType, String drlConstraint) {
        return exprIdGenerator.getCondId(patternType, drlConstraint);
    }

    public void addNamedConsequence(String key, String value) {
        namedConsequences.put(key, value);
    }

    public TypeResolver getTypeResolver() {
        return pkg.getTypeResolver();
    }

    public Class<?> getClassFromContext(String className) {
        Class<?> patternType;
        try {
            patternType = getTypeResolver().resolveType(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException( e );
        }
        return patternType;
    }

    public RuleDescr getRuleDescr() {
        return ruleDescr;
    }
}

