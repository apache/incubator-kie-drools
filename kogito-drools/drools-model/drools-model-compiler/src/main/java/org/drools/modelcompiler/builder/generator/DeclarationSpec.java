package org.drools.modelcompiler.builder.generator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.drools.compiler.lang.descr.BehaviorDescr;
import org.drools.compiler.lang.descr.EntryPointDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.javaparser.ast.expr.Expression;

public class DeclarationSpec {
    private final String bindingId;
    final Class<?> declarationClass;
    final Optional<PatternDescr> optPattern;
    final Optional<Expression> declarationSource;
    final Optional<String> variableName;

    public DeclarationSpec(String bindingId, Class<?> declarationClass) {
        this.bindingId = bindingId;
        this.declarationClass = declarationClass;
        this.optPattern = Optional.empty();
        this.declarationSource = Optional.empty();
        this.variableName = Optional.empty();
    }

    public DeclarationSpec(String bindingId, Class<?> declarationClass, String variableName) {
        this.bindingId = bindingId;
        this.declarationClass = declarationClass;
        this.optPattern = Optional.empty();
        this.declarationSource = Optional.empty();
        this.variableName = Optional.of(variableName);
    }

    public DeclarationSpec(String bindingId, Class<?> declarationClass, Expression declarationSource) {
        this.bindingId = bindingId;
        this.declarationClass = declarationClass;
        this.optPattern = Optional.empty();
        this.declarationSource = Optional.of(declarationSource);
        this.variableName = Optional.empty();
    }

    public DeclarationSpec(String bindingId, Class<?> declarationClass, PatternDescr pattern) {
        this.bindingId = bindingId;
        this.declarationClass = declarationClass;
        this.optPattern = Optional.of(pattern);
        this.declarationSource = Optional.empty();
        this.variableName = Optional.empty();
    }

    public DeclarationSpec(String bindingId, Class<?> declarationClass, Optional<PatternDescr> pattern, Optional<Expression> declarationSource) {
        this.bindingId = bindingId;
        this.declarationClass = declarationClass;
        this.optPattern = pattern;
        this.declarationSource = declarationSource;
        this.variableName = Optional.empty();
    }

    Optional<String> getEntryPoint() {
        return optPattern.flatMap(pattern -> pattern.getSource() instanceof EntryPointDescr ?
                Optional.of(((EntryPointDescr) pattern.getSource()).getEntryId()) :
                Optional.empty()
        );
    }

    public List<BehaviorDescr> getBehaviors() {
        return optPattern.map(PatternDescr::getBehaviors).orElse(Collections.emptyList());

    }

    public String getBindingId() {
        return bindingId;
    }

    public Class<?> getDeclarationClass() {
        return declarationClass;
    }
}