package org.drools.modelcompiler.builder.generator;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.javaparser.ast.expr.Expression;

public class RuleContext {
    private final InternalKnowledgePackage pkg;
    private DRLIdGenerator exprIdGenerator;
    private final Optional<RuleDescr> ruleDescr;

    private List<DeclarationSpec> declarations = new ArrayList<>();
    private List<DeclarationSpec> ooPathDeclarations = new ArrayList<>();
    List<QueryParameter> queryParameters = new ArrayList<>();
    Deque<Consumer<Expression>> exprPointer = new LinkedList<>();
    List<Expression> expressions = new ArrayList<>();
    Set<String> queryName = new HashSet<>();
    Map<String, String> namedConsequences = new HashMap<>();

    private RuleDialect ruleDialect = RuleDialect.JAVA; // assumed is java by default as per Drools manual.
    public static enum RuleDialect {
        JAVA,
        MVEL;
    }

    BaseDescr parentDesc = null;

    public RuleContext(InternalKnowledgePackage pkg, DRLIdGenerator exprIdGenerator, Optional<RuleDescr> ruleDescr) {
        this.pkg = pkg;
        this.exprIdGenerator = exprIdGenerator;
        this.ruleDescr = ruleDescr;
        exprPointer.push( this.expressions::add );
    }

    public Optional<DeclarationSpec> getDeclarationById(String id) {
        return declarations.stream().filter(d -> d.getBindingId().equals(id)).findFirst();
    }

    public Optional<DeclarationSpec> getOOPathDeclarationById(String id) {
        return ooPathDeclarations.stream().filter(d -> d.getBindingId().equals(id)).findFirst();
    }

    public void addDeclaration(DeclarationSpec d) {
        // It would be probably be better to avoid putting the same declaration multiple times
        // instead of using Set semantic here
        if(!getDeclarationById(d.getBindingId()).isPresent()) {
            this.declarations.add(d);
        }
    }

    public void addOOPathDeclaration(DeclarationSpec d) {
        if(!getOOPathDeclarationById(d.getBindingId()).isPresent()) {
            this.ooPathDeclarations.add(d);
        }
    }

    public List<DeclarationSpec> getDeclarations() {
        return declarations;
    }

    public List<DeclarationSpec> getOOPathDeclarations() {
        return ooPathDeclarations;
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

    public String getOOPathId(Class<?> patternType, String drlConstraint) {
        return exprIdGenerator.getOOPathId(patternType, drlConstraint);
    }

    public void addNamedConsequence(String key, String value) {
        namedConsequences.put(key, value);
    }

    public Optional<RuleDescr> getRuleDescr() {
        return ruleDescr;
    }

    public RuleDialect getRuleDialect() {
        return ruleDialect;
    }

    public void setRuleDialect(RuleDialect ruleDialect) {
        this.ruleDialect = ruleDialect;
    }

}

