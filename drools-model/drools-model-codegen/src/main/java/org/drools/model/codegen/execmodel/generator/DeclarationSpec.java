/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.codegen.execmodel.generator;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.drools.base.base.ClassObjectType;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.accessor.PatternExtractor;
import org.drools.drl.ast.descr.BehaviorDescr;
import org.drools.drl.ast.descr.EntryPointDescr;
import org.drools.drl.ast.descr.PatternDescr;

import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toClassOrInterfaceType;
import static org.drools.util.ClassUtils.rawType;

public class DeclarationSpec {
    private final String bindingId;
    private final Type declarationType;
    private final Optional<PatternDescr> optPattern;
    private final Optional<Expression> declarationSource;
    private final Optional<String> variableName;
    private final boolean isGlobal;

    private String boundVariable;
    private MethodCallExpr bindingExpr;
    private boolean boxed = false;
    private Optional<PatternDescr> belongingPatternDescr = Optional.empty();

    public DeclarationSpec(String bindingId, Type declarationType) {
        this(bindingId, declarationType, Optional.empty(), Optional.empty(), Optional.empty(), false);
    }

    public DeclarationSpec(String bindingId, Type declarationType, boolean isGlobal) {
        this(bindingId, declarationType, Optional.empty(), Optional.empty(), Optional.empty(), isGlobal);
    }

    DeclarationSpec(String bindingId, Type declarationType, String variableName) {
        this(bindingId, declarationType, Optional.empty(), Optional.empty(), Optional.of(variableName), false);
    }

    DeclarationSpec(String bindingId, Type declarationType, Expression declarationSource) {
        this(bindingId, declarationType, Optional.empty(), Optional.of(declarationSource), Optional.empty(), false);
    }

    DeclarationSpec(String bindingId, Type declarationType, Optional<PatternDescr> pattern, Optional<Expression> declarationSource, Optional<String> variableName, boolean isGlobal) {
        this.bindingId = bindingId;
        this.declarationType = declarationType;
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
        return rawType( declarationType );
    }

    public Type getDeclarationType() {
        return declarationType;
    }

    public boolean isParametrizedType() {
        return declarationType instanceof ParameterizedType;
    }

    public Optional<Expression> getDeclarationSource() {
        return declarationSource;
    }

    public Optional<String> getVariableName() {
        return variableName;
    }

    public com.github.javaparser.ast.type.Type getBoxedType() {
        return DrlxParseUtil.classToReferenceType(this);
    }

    public com.github.javaparser.ast.type.Type getRawType() {
        return toClassOrInterfaceType(getDeclarationClass());
    }

    public boolean isGlobal() {
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
                ", declarationClass=" + declarationType +
                ", isGlobal=" + isGlobal +
                ", boxed=" + boxed +
                '}';
    }

    public Declaration asDeclaration() {
        Class<?> declarationClass = getDeclarationClass();
        Declaration decl = new Declaration( bindingId, new PatternExtractor( new ClassObjectType(declarationClass) ), null );
        decl.setDeclarationClass(declarationClass);
        return decl;
    }

    public void setBoxed(boolean boxed) {
        this.boxed = boxed;
    }

    public boolean isBoxed() {
        return boxed;
    }

    public Optional<PatternDescr> getBelongingPatternDescr() {
        return belongingPatternDescr;
    }

    public void setBelongingPatternDescr(Optional<PatternDescr> belongingPatternDescr) {
        this.belongingPatternDescr = belongingPatternDescr;
    }

}
