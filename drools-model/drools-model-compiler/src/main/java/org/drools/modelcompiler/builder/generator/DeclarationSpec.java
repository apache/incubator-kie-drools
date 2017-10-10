package org.drools.modelcompiler.builder.generator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.drools.compiler.lang.descr.BehaviorDescr;
import org.drools.compiler.lang.descr.EntryPointDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.javaparser.ast.expr.Expression;

public class DeclarationSpec {
    final String bindingId;
    final Class<?> declarationClass;
    final Optional<PatternDescr> optPattern;
    final Optional<Expression> optionalReactiveFrom;

    public DeclarationSpec(String bindingId, Class<?> declarationClass) {
        this.bindingId = bindingId;
        this.declarationClass = declarationClass;
        this.optPattern = Optional.empty();
        this.optionalReactiveFrom = Optional.empty();
    }

    public DeclarationSpec(String bindingId, Class<?> declarationClass, Expression optionalReactiveFrom) {
        this.bindingId = bindingId;
        this.declarationClass = declarationClass;
        this.optPattern = Optional.empty();
        this.optionalReactiveFrom = Optional.of(optionalReactiveFrom);
    }

    public DeclarationSpec(String bindingId, Class<?> declarationClass, PatternDescr pattern) {
        this.bindingId = bindingId;
        this.declarationClass = declarationClass;
        this.optPattern = Optional.of(pattern);
        this.optionalReactiveFrom = Optional.empty();
    }

    public DeclarationSpec(String bindingId, Class<?> declarationClass, PatternDescr pattern, Optional<Expression> optionalReactiveFrom) {
        this.bindingId = bindingId;
        this.declarationClass = declarationClass;
        this.optPattern = Optional.of(pattern);
        this.optionalReactiveFrom = optionalReactiveFrom;
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