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
    private final Class<?> declarationClass;
    private final Optional<PatternDescr> optPattern;
    private final Optional<Expression> declarationSource;
    private final Optional<String> variableName;

    public DeclarationSpec(String bindingId, Class<?> declarationClass) {
        this(bindingId, declarationClass, Optional.empty(), Optional.empty(), Optional.empty());
    }

    public DeclarationSpec(String bindingId, Class<?> declarationClass, String variableName) {
        this(bindingId, declarationClass, Optional.empty(), Optional.empty(), Optional.of(variableName));
    }

    public DeclarationSpec(String bindingId, Class<?> declarationClass, Expression declarationSource) {
        this(bindingId, declarationClass, Optional.empty(), Optional.of(declarationSource), Optional.empty());
    }

    public DeclarationSpec(String bindingId, Class<?> declarationClass, Optional<PatternDescr> pattern, Optional<Expression> declarationSource) {
        this(bindingId, declarationClass, pattern, declarationSource, Optional.empty());
    }

    public DeclarationSpec(String bindingId, Class<?> declarationClass, Optional<PatternDescr> pattern, Optional<Expression> declarationSource, Optional<String> variableName) {
        this.bindingId = bindingId;
        this.declarationClass = declarationClass;
        this.optPattern = pattern;
        this.declarationSource = declarationSource;
        this.variableName = variableName;
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

    public Optional<PatternDescr> getOptPattern() {
        return optPattern;
    }

    public Optional<Expression> getDeclarationSource() {
        return declarationSource;
    }

    public Optional<String> getVariableName() {
        return variableName;
    }

    public org.drools.javaparser.ast.type.Type getType() {
        return DrlxParseUtil.classToReferenceType(getDeclarationClass());
    }
}