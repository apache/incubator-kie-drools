package org.drools.modelcompiler.builder.generator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.drools.compiler.lang.descr.BehaviorDescr;
import org.drools.compiler.lang.descr.EntryPointDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.PatternSourceDescr;

public class DeclarationSpec {
    final Class<?> declarationClass;
    final Optional<PatternDescr> optPattern;
    final Optional<PatternSourceDescr> optSource;

    public DeclarationSpec(Class<?> declarationClass, PatternDescr pattern) {
        this.declarationClass = declarationClass;
        this.optPattern = Optional.of(pattern);
        this.optSource = Optional.ofNullable(pattern.getSource());
    }

    public DeclarationSpec( Class<?> declarationClass ) {
        this.declarationClass = declarationClass;
        this.optPattern = Optional.empty();
        this.optSource = Optional.empty();
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