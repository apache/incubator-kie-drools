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

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.base.base.ClassObjectType;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.accessor.PatternExtractor;
import org.drools.base.time.TimeUtils;
import org.drools.core.rule.BehaviorRuntime;
import org.drools.drl.ast.descr.BehaviorDescr;
import org.drools.drl.ast.descr.EntryPointDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.model.Variable;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.errors.UnknownDeclarationError;

import static org.drools.model.codegen.execmodel.PackageModel.DOMAIN_CLASSESS_METADATA_FILE_NAME;
import static org.drools.model.codegen.execmodel.PackageModel.DOMAIN_CLASS_METADATA_INSTANCE;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.classToReferenceType;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toClassOrInterfaceType;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toStringLiteral;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.DECLARATION_OF_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.ENTRY_POINT_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.WINDOW_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.createDslTopLevelMethod;
import static org.drools.modelcompiler.util.ClassUtil.asJavaSourceName;
import static org.drools.util.ClassUtils.rawType;

public class TypedDeclarationSpec implements DeclarationSpec {
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

    public TypedDeclarationSpec(String bindingId, Type declarationType) {
        this(bindingId, declarationType, Optional.empty(), Optional.empty(), Optional.empty(), false);
    }

    public TypedDeclarationSpec(String bindingId, Type declarationType, boolean isGlobal) {
        this(bindingId, declarationType, Optional.empty(), Optional.empty(), Optional.empty(), isGlobal);
    }

    TypedDeclarationSpec(String bindingId, Type declarationType, String variableName) {
        this(bindingId, declarationType, Optional.empty(), Optional.empty(), Optional.of(variableName), false);
    }

    TypedDeclarationSpec(String bindingId, Type declarationType, Expression declarationSource) {
        this(bindingId, declarationType, Optional.empty(), Optional.of(declarationSource), Optional.empty(), false);
    }

    TypedDeclarationSpec(String bindingId, Type declarationType, Optional<PatternDescr> pattern, Optional<Expression> declarationSource, Optional<String> variableName, boolean isGlobal) {
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

    @Override
    public String getBindingId() {
        return bindingId;
    }

    @Override
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

    @Override
    public Optional<String> getVariableName() {
        return variableName;
    }

    public com.github.javaparser.ast.type.Type getBoxedType() {
        return DrlxParseUtil.classToReferenceType(this);
    }

    public com.github.javaparser.ast.type.Type getRawType() {
        return toClassOrInterfaceType(getDeclarationClass());
    }

    @Override
    public boolean isGlobal() {
        return isGlobal;
    }

    public Optional<String> getBoundVariable() {
        return Optional.ofNullable( boundVariable );
    }

    public void setBoundVariable( String boundVariable ) {
        this.boundVariable = boundVariable;
    }

    @Override
    public MethodCallExpr getBindingExpr() {
        return bindingExpr;
    }

    @Override
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

    @Override
    public void registerOnPackage(PackageModel packageModel, RuleContext context, BlockStmt ruleBlock) {
        boolean domainClass = packageModel.registerDomainClass( getDeclarationClass() );
        if (!context.getGlobals().containsKey(getBindingId()) && context.getQueryParameterByName(getBindingId()).isEmpty()) {
            addVariable(ruleBlock, context, domainClass);
        }
    }

    private void addVariable(BlockStmt ruleBlock, RuleContext context, boolean domainClass) {
        if (getDeclarationClass() == null) {
            context.addCompilationError( new UnknownDeclarationError( getBindingId() ) );
            return;
        }

        MethodCallExpr declarationOfCall = createDslTopLevelMethod(DECLARATION_OF_CALL);
        declarationOfCall.addArgument(new ClassExpr(getBoxedType() ));

        if (domainClass) {
            String domainClassSourceName = asJavaSourceName( getDeclarationClass() );
            declarationOfCall.addArgument( DOMAIN_CLASSESS_METADATA_FILE_NAME + context.getPackageModel().getPackageUUID() + "." + domainClassSourceName + DOMAIN_CLASS_METADATA_INSTANCE );
        }

        declarationOfCall.addArgument(toStringLiteral(getVariableName().orElse(getBindingId())));

        getDeclarationSource().ifPresent(declarationOfCall::addArgument);

        getEntryPoint().ifPresent( ep -> {
            MethodCallExpr entryPointCall = createDslTopLevelMethod(ENTRY_POINT_CALL);
            entryPointCall.addArgument( toStringLiteral(ep ) );
            declarationOfCall.addArgument( entryPointCall );
        } );

        for ( BehaviorDescr behaviorDescr : getBehaviors() ) {
            MethodCallExpr windowCall = createDslTopLevelMethod(WINDOW_CALL);
            if ( BehaviorRuntime.BehaviorType.TIME_WINDOW.matches(behaviorDescr.getSubType()) ) {
                windowCall.addArgument( "org.drools.model.Window.Type.TIME" );
                windowCall.addArgument( "" + TimeUtils.parseTimeString(behaviorDescr.getParameters().get(0 ) ) );
            }
            if ( BehaviorRuntime.BehaviorType.LENGTH_WINDOW.matches(behaviorDescr.getSubType()) ) {
                windowCall.addArgument( "org.drools.model.Window.Type.LENGTH" );
                windowCall.addArgument( "" + Integer.valueOf( behaviorDescr.getParameters().get( 0 ) ) );
            }
            declarationOfCall.addArgument( windowCall );
        }

        ClassOrInterfaceType varType = toClassOrInterfaceType(Variable.class);
        varType.setTypeArguments( classToReferenceType(this) );
        VariableDeclarationExpr varExpr = new VariableDeclarationExpr(varType, context.getVar(getBindingId()), Modifier.finalModifier());

        AssignExpr varAssign = new AssignExpr(varExpr, declarationOfCall, AssignExpr.Operator.ASSIGN);
        if (!DrlxParseUtil.hasDuplicateExpr(ruleBlock, varAssign)) {
            ruleBlock.addStatement(varAssign);
        }
    }
}
