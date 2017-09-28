package org.drools.modelcompiler.builder.generator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.drools.compiler.lang.descr.BehaviorDescr;
import org.drools.compiler.lang.descr.EntryPointDescr;
import org.drools.compiler.lang.descr.PatternDescr;

public class DeclarationSpec {
    final Class<?> declarationClass;
    final Optional<PatternDescr> optPattern;

    public DeclarationSpec( Class<?> declarationClass, PatternDescr pattern ) {
        this.declarationClass = declarationClass;
        this.optPattern = Optional.of(pattern);
    }

    public DeclarationSpec( Class<?> declarationClass ) {
        this.declarationClass = declarationClass;
        this.optPattern = Optional.empty();
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
}