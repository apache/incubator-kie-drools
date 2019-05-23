package org.drools.modelcompiler.builder.generator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.drools.compiler.lang.descr.BehaviorDescr;
import org.drools.compiler.lang.descr.EntryPointDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.core.base.ClassObjectType;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.PatternExtractor;
import com.github.javaparser.ast.expr.Expression;

public class DeclarationSpec {
    private final String bindingId;
    private final Class<?> declarationClass;
    private final Optional<PatternDescr> optPattern;
    private final Optional<Expression> declarationSource;
    private final Optional<String> variableName;
    private final Boolean isGlobal;

    public DeclarationSpec(String bindingId, Class<?> declarationClass) {
        this(bindingId, declarationClass, Optional.empty(), Optional.empty(), Optional.empty(), false);
    }

    public DeclarationSpec(String bindingId, Class<?> declarationClass, Boolean isGlobal) {
        this(bindingId, declarationClass, Optional.empty(), Optional.empty(), Optional.empty(), isGlobal);
    }

    DeclarationSpec(String bindingId, Class<?> declarationClass, String variableName) {
        this(bindingId, declarationClass, Optional.empty(), Optional.empty(), Optional.of(variableName), false);
    }

    DeclarationSpec(String bindingId, Class<?> declarationClass, Expression declarationSource) {
        this(bindingId, declarationClass, Optional.empty(), Optional.of(declarationSource), Optional.empty(), false);
    }

    DeclarationSpec(String bindingId, Class<?> declarationClass, Optional<PatternDescr> pattern, Optional<Expression> declarationSource, Optional<String> variableName, Boolean isGlobal) {
        this.bindingId = bindingId;
        this.declarationClass = declarationClass;
        this.optPattern = pattern;
        this.declarationSource = declarationSource;
        this.variableName = variableName;
        this.isGlobal = isGlobal;
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

    public Optional<Expression> getDeclarationSource() {
        return declarationSource;
    }

    public Optional<String> getVariableName() {
        return variableName;
    }

    public com.github.javaparser.ast.type.Type getBoxedType() {
        return DrlxParseUtil.classToReferenceType(getDeclarationClass());
    }

    public com.github.javaparser.ast.type.Type getRawType() {
        return DrlxParseUtil.toType(getDeclarationClass());
    }

    public Boolean isGlobal() {
        return isGlobal;
    }

    @Override
    public String toString() {
        return "DeclarationSpec{" +
                "bindingId='" + bindingId + '\'' +
                ", declarationClass=" + declarationClass +
                ", isGlobal=" + isGlobal +
                '}';
    }

    public Declaration asDeclaration() {
        Declaration decl = new Declaration( bindingId, new PatternExtractor( new ClassObjectType( declarationClass ) ), null );
        decl.setDeclarationClass( declarationClass );
        return decl;
    }
}