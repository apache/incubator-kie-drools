package org.drools.modelcompiler.builder.generator;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.javaparser.ast.expr.Expression;

public class RuleContext {
    private final InternalKnowledgePackage pkg;
    private DRLIdGenerator idGenerator;
    private final Optional<RuleDescr> descr;

    private List<DeclarationSpec> declarations = new ArrayList<>();
    private List<DeclarationSpec> ooPathDeclarations = new ArrayList<>();
    Deque<Consumer<Expression>> exprPointer = new LinkedList<>();
    List<Expression> expressions = new ArrayList<>();
    Map<String, String> namedConsequences = new HashMap<>();

    List<QueryParameter> queryParameters = new ArrayList<>();
    Optional<String> queryName = Optional.empty();

    private RuleDialect ruleDialect = RuleDialect.JAVA; // assumed is java by default as per Drools manual.
    public static enum RuleDialect {
        JAVA,
        MVEL;
    }

    BaseDescr parentDesc = null;

    public RuleContext(InternalKnowledgePackage pkg, DRLIdGenerator exprIdGenerator, Optional<RuleDescr> ruleDescr) {
        this.pkg = pkg;
        this.idGenerator = exprIdGenerator;
        this.descr = ruleDescr;
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
        return idGenerator.getExprId(patternType, drlConstraint);
    }

    public String getConditionId(Class<?> patternType, String drlConstraint) {
        return idGenerator.getCondId(patternType, drlConstraint);
    }

    public String getOOPathId(Class<?> patternType, String drlConstraint) {
        return idGenerator.getOOPathId(patternType, drlConstraint);
    }

    public String getOrCreateUnificationId(String drlConstraint) {
        return idGenerator.getOrCreateUnificationVariable(drlConstraint);
    }

    public Optional<String> getUnificationId(String drlConstraint) {
        return idGenerator.getUnificationVariable(drlConstraint);
    }

    public void addNamedConsequence(String key, String value) {
        namedConsequences.put(key, value);
    }

    public Optional<RuleDescr> getRuleDescr() {
        return descr;
    }

    public RuleDialect getRuleDialect() {
        return ruleDialect;
    }

    public void setRuleDialect(RuleDialect ruleDialect) {
        this.ruleDialect = ruleDialect;
    }

    public Optional<QueryParameter> queryParameterWithName(Predicate<? super QueryParameter> predicate) {
        return queryParameters.stream().filter(predicate).findFirst();
    }

}

