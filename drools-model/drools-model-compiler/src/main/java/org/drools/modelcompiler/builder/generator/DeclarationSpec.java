/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder.generator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.type.Type;
import org.drools.compiler.lang.descr.BehaviorDescr;
import org.drools.compiler.lang.descr.EntryPointDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.core.base.ClassObjectType;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.PatternExtractor;
import com.github.javaparser.ast.expr.Expression;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toClassOrInterfaceType;

public class DeclarationSpec {
    private final String bindingId;
    private final Class<?> declarationClass;
    private final Optional<PatternDescr> optPattern;
    private final Optional<Expression> declarationSource;
    private final Optional<String> variableName;
    private final Boolean isGlobal;

    private String boundVariable;
    private MethodCallExpr bindingExpr;
    private boolean boxed = false;

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

    public Type getBoxedType() {
        return DrlxParseUtil.classToReferenceType(this);
    }

    public Type getRawType() {
        return toClassOrInterfaceType(getDeclarationClass());
    }

    public Boolean isGlobal() {
        return isGlobal;
    }

    public Optional<String> getBoundVariable() {
        return Optional.ofNullable( boundVariable );
    }

    public void setBoundVariable( String boundVariable ) {
        this.boundVariable = boundVariable;
    }

    public MethodCallExpr getBindingExpr() {
        return bindingExpr;
    }

    public void setBindingExpr( MethodCallExpr bindingExpr ) {
        this.bindingExpr = bindingExpr;
    }

    @Override
    public String toString() {
        return "DeclarationSpec{" +
                "bindingId='" + bindingId + '\'' +
                ", declarationClass=" + declarationClass +
                ", isGlobal=" + isGlobal +
                ", boxed=" + boxed +
                '}';
    }

    public Declaration asDeclaration() {
        Declaration decl = new Declaration( bindingId, new PatternExtractor( new ClassObjectType( declarationClass ) ), null );
        decl.setDeclarationClass( declarationClass );
        return decl;
    }

    public void setBoxed(boolean boxed) {
        this.boxed = boxed;
    }

    public boolean isBoxed() {
        return boxed;
    }
}