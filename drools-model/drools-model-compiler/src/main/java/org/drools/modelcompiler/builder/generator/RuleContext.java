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

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.UnknownType;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.BaseKnowledgeBuilderResultImpl;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.AttributeDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.ConditionalElementDescr;
import org.drools.compiler.lang.descr.ForallDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.core.addon.TypeResolver;
import org.drools.core.base.evaluators.EvaluatorDefinition;
import org.drools.core.ruleunit.RuleUnitDescriptionLoader;
import org.drools.core.util.Bag;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.errors.UnknownDeclarationException;
import org.drools.modelcompiler.builder.errors.UnknownRuleUnitException;
import org.kie.api.definition.type.ClassReactive;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.ResultSeverity;
import org.kie.internal.builder.conf.PropertySpecificOption;
import org.kie.internal.ruleunit.RuleUnitDescription;
import org.kie.internal.ruleunit.RuleUnitVariable;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.drools.modelcompiler.builder.generator.QueryGenerator.toQueryArg;
import static org.kie.internal.ruleunit.RuleUnitUtil.isLegacyRuleUnit;

public class RuleContext {

    private final KnowledgeBuilderImpl kbuilder;
    private final PackageModel packageModel;
    private final TypeResolver typeResolver;
    private final RuleDescr ruleDescr;
    private final int ruleIndex;

    private final DRLIdGenerator idGenerator;

    private final Map<String, DeclarationSpec> allDeclarations = new LinkedHashMap<>();
    private final Map<String, DeclarationSpec> scopedDeclarations = new LinkedHashMap<>();
    private final List<DeclarationSpec> ooPathDeclarations = new ArrayList<>();
    private final Deque<Consumer<Expression>> exprPointer = new ArrayDeque<>();
    private final List<Expression> expressions = new ArrayList<>();
    private final Map<String, String> namedConsequences = new HashMap<>();
    private Map<String, MethodCallExpr> ooPathBindingPatternExprs;
    private final BlockStmt ruleVariablesBlock = new BlockStmt();

    private final List<QueryParameter> queryParameters = new ArrayList<>();
    private Optional<String> queryName = empty();

    private RuleUnitDescription ruleUnitDescr;
    private final Map<String, Class<?>> ruleUnitVars = new HashMap<>();
    private final Map<String, Class<?>> ruleUnitVarsOriginalType = new HashMap<>();

    private final Map<AggregateKey, String> aggregatePatternMap = new HashMap<>();

    /* These are used to check if some binding used in an OR expression is used in every branch */
    private boolean isNestedInsideOr = false;
    private final Bag<String> bindingOr = new Bag<>();
    private final Set<String> unusableOrBinding = new HashSet<>();

    private RuleDialect ruleDialect = RuleDialect.JAVA; // assumed is java by default as per Drools manual.

    private int scopeCounter = 1;
    private Scope currentScope = new Scope();
    private final Deque<Scope> scopesStack = new LinkedList<>();
    private final Map<String, String> definedVars = new HashMap<>();

    private final Map<String, Type> explicitCastType = new HashMap<>();

    // These are used for indexing see PatternBuilder:1198
    private final Set<String> patternBindings = new HashSet<>();

    private int legacyAccumulateCounter = 0;

    private Optional<BaseDescr> currentConstraintDescr = empty();

    private boolean hasCompilationError;

    public enum RuleDialect {
        JAVA, MVEL
    }

    private AndDescr parentDescr;

    public RuleContext(KnowledgeBuilderImpl kbuilder, PackageModel packageModel, TypeResolver typeResolver, RuleDescr ruleDescr) {
        this(kbuilder, packageModel, typeResolver, ruleDescr, -1);
    }

    public RuleContext(KnowledgeBuilderImpl kbuilder, PackageModel packageModel, TypeResolver typeResolver, RuleDescr ruleDescr, int ruleIndex) {
        this.kbuilder = kbuilder;
        this.packageModel = packageModel;
        this.idGenerator = packageModel.getExprIdGenerator();
        exprPointer.push( this.expressions::add );
        this.typeResolver = typeResolver;
        this.ruleDescr = ruleDescr;
        processUnitData();
        this.ruleIndex = ruleIndex;
    }

    private void findUnitDescr() {
        if (ruleDescr == null) {
            return;
        }

        boolean useNamingConvention = false;
        String unitName;
        AnnotationDescr unitAnn = ruleDescr.getAnnotation( "Unit" );
        if (unitAnn != null) {
            unitName = ( String ) unitAnn.getValue();
            unitName = unitName.substring( 0, unitName.length() - ".class".length() );
        } else if (ruleDescr.getUnit() != null) {
            unitName = ruleDescr.getUnit().getTarget();
        } else {
            if (ruleDescr.getResource() == null) {
                return;
            }
            String drlFile = ruleDescr.getResource().getSourcePath();
            if (drlFile != null) {
                String drlFileName = drlFile.substring(drlFile.lastIndexOf('/')+1);
                unitName = packageModel.getName() + '.' + drlFileName.substring(0, drlFileName.length() - ".drl".length());
                useNamingConvention = true;
            } else {
                return;
            }
        }

        RuleUnitDescriptionLoader ruleUnitDescriptionLoader = kbuilder.getPackageRegistry(packageModel.getName() ).getPackage().getRuleUnitDescriptionLoader();
        Optional<RuleUnitDescription> ruDescr = ruleUnitDescriptionLoader.getDescription(unitName );
        if (ruDescr.isPresent()) {
            ruleUnitDescr = ruDescr.get();
        } else if (!useNamingConvention) {
            throw new UnknownRuleUnitException( unitName );
        }
    }

    private void processUnitData() {
        findUnitDescr();
        if (ruleUnitDescr != null && !isLegacyRuleUnit()) {
            for (RuleUnitVariable unitVar : ruleUnitDescr.getUnitVarDeclarations()) {
                String unitVarName = unitVar.getName();
                Class<?> resolvedType = unitVar.isDataSource() ? unitVar.getDataSourceParameterType() : unitVar.getType();
                addRuleUnitVar( unitVarName, resolvedType );

                getPackageModel().addGlobal( unitVarName, unitVar.getType() );
                if ( unitVar.isDataSource() ) {
                    getPackageModel().addEntryPoint( unitVarName );
                }
            }
        }
    }

    public RuleUnitDescription getRuleUnitDescr() {
        return ruleUnitDescr;
    }

    public KnowledgeBuilderImpl getKbuilder() {
        return kbuilder;
    }

    public int getRuleIndex() {
        return ruleIndex;
    }

    public EvaluatorDefinition getEvaluatorDefinition(String opName) {
        return kbuilder.getBuilderConfiguration().getEvaluatorRegistry().getEvaluatorDefinition( opName );
    }

    public void addCompilationError( KnowledgeBuilderResult error ) {
        hasCompilationError = true;
        if ( error instanceof BaseKnowledgeBuilderResultImpl ) {
            (( BaseKnowledgeBuilderResultImpl ) error).setResource( ruleDescr.getResource() );
        }
        synchronized (kbuilder) {
            kbuilder.addBuilderResult(error);
        }
    }

    public boolean hasCompilationError() {
        return hasCompilationError;
    }

    public boolean hasErrors() {
        return kbuilder.hasResults( ResultSeverity.ERROR );
    }

    public void addInlineCastType(String field, Type type) {
        explicitCastType.put(field, type);
    }

    public Optional<Type> explicitCastType(String field) {
        return ofNullable(explicitCastType.get(field));
    }

    public Optional<DeclarationSpec> getDeclarationById(String id) {
        DeclarationSpec spec = scopedDeclarations.get( getDeclarationKey( id ));
        if (spec == null) {
            Class<?> unitVarType = ruleUnitVarsOriginalType.get( id );
            if(unitVarType == null) {
                unitVarType = ruleUnitVars.get(id);
            }
            if (unitVarType != null) {
                spec = new DeclarationSpec(id, unitVarType);
            }
        }
        return ofNullable( spec );
    }

    public DeclarationSpec getDeclarationByIdWithException(String id) {
        return getDeclarationById(id).orElseThrow(() -> new UnknownDeclarationException("Unknown declaration: " + id));
    }

    private String getDeclarationKey( String id ) {
        String var = definedVars.get(id);
        return var != null ? var : id;
    }

    public void removeDeclarationById(String id) {
        String declId = getDeclarationKey( id );
        scopedDeclarations.remove( declId );
        this.allDeclarations.remove( declId );
    }

    public boolean hasDeclaration(String id) {
        return getDeclaration( id ) != null;
    }

    private DeclarationSpec getDeclaration(String id) {
        return scopedDeclarations.get( getDeclarationKey( id ));
    }

    public void registerBindingExpression( String boundVar, MethodCallExpr bidingExpr ) {
        DeclarationSpec dec = getDeclaration(boundVar);
        if (dec != null) {
            dec.setBindingExpr( bidingExpr );
        }
    }

    public Optional<MethodCallExpr> findBindingExpression( String boundVar ) {
        DeclarationSpec dec = getDeclaration(boundVar);
        return dec == null ? empty() : ofNullable( dec.getBindingExpr() );
    }

    public void addGlobalDeclarations(Map<String, Class<?>> globals) {
        for(Map.Entry<String, Class<?>> ks : globals.entrySet()) {
            definedVars.put(ks.getKey(), ks.getKey());
            addDeclaration(new DeclarationSpec(ks.getKey(), ks.getValue(), true));
        }
    }

    public Optional<DeclarationSpec> getOOPathDeclarationById(String id) {
        return ooPathDeclarations.stream().filter(d -> d.getBindingId().equals(id)).findFirst();
    }

    public void addRuleUnitVar(String name, Class<?> type) {
        ruleUnitVars.put( name, type );
    }

    public void addRuleUnitVarOriginalType(String name, Class<?> type) {
        ruleUnitVarsOriginalType.put( name, type );
    }

    public Class<?> getRuleUnitVarType(String name) {
        Class<?> varType = ruleUnitVars.get( name );
        if (varType != null) {
            return varType;
        }
        DeclarationSpec decl = scopedDeclarations.get( "$p" );
        return decl != null ? decl.getDeclarationClass() : null;
    }

    public DeclarationSpec addDeclaration(String bindingId, Class<?> declarationClass) {
        return addDeclaration(new DeclarationSpec(defineVar(bindingId), declarationClass));
    }

    public DeclarationSpec addDeclaration( String bindingId, Class<?> declarationClass, Optional<PatternDescr> pattern, Optional<Expression> declarationSource) {
        return addDeclaration(new DeclarationSpec(defineVar(bindingId), declarationClass, pattern, declarationSource, Optional.empty(), false));
    }

    public DeclarationSpec addDeclaration(String bindingId, Class<?> declarationClass, String variableName) {
        return addDeclaration(new DeclarationSpec(defineVar(bindingId), declarationClass, variableName));
    }

    public DeclarationSpec addDeclaration(String bindingId, Class<?> declarationClass, Expression declarationSource) {
        return addDeclaration(new DeclarationSpec(defineVar(bindingId), declarationClass, declarationSource));
    }

    private String defineVar(String var) {
        String bindingId = currentScope.getBindingVar( var );
        definedVars.put(var, bindingId);
        currentScope.vars.add(var);
        return bindingId;
    }

    public String getCurrentScopeSuffix() {
        return currentScope.id;
    }

    public DeclarationSpec addDeclaration(DeclarationSpec d) {
        this.scopedDeclarations.putIfAbsent( d.getBindingId(), d );
        this.allDeclarations.putIfAbsent( d.getBindingId(), d );
        return d;
    }

    public void addDeclarationReplacing(DeclarationSpec d) {
        final String bindingId = d.getBindingId();
        final Optional<DeclarationSpec> declarationById = getDeclarationById(bindingId);
        if (declarationById.isPresent()) {
            removeDeclarationById(bindingId);
        }
        this.scopedDeclarations.put(d.getBindingId(), d);
        this.allDeclarations.put(d.getBindingId(), d);
        definedVars.put(bindingId, bindingId);
    }

    public void addOOPathDeclaration(DeclarationSpec d) {
        if(!getOOPathDeclarationById(d.getBindingId()).isPresent()) {
            this.ooPathDeclarations.add(d);
        }
    }

    public Collection<DeclarationSpec> getAllDeclarations() {
        return allDeclarations.values();
    }

    public Collection<String> getAvailableBindings() {
        return scopedDeclarations.keySet();
    }

    public List<DeclarationSpec> getOOPathDeclarations() {
        return ooPathDeclarations;
    }

    public void addExpression(Expression e) {
        exprPointer.peek().accept(e);
    }

    public void registerOOPathPatternExpr(String binding, MethodCallExpr patternExpr) {
        if (ooPathBindingPatternExprs == null) {
            ooPathBindingPatternExprs = new HashMap<>();
        }
        ooPathBindingPatternExprs.put( binding, patternExpr );
    }

    public void clearOOPathPatternExpr() {
        ooPathBindingPatternExprs = null;
    }

    public MethodCallExpr getOOPathPatternExpr(String binding) {
        return ooPathBindingPatternExprs == null ? null : ooPathBindingPatternExprs.get(binding);
    }

    public void pushExprPointer(Consumer<Expression> p) {
        exprPointer.push(p);
    }

    public Consumer<Expression> popExprPointer() {
        return exprPointer.pop();
    }

    public Consumer<Expression> peekExprPointer() {
        return exprPointer.peek();
    }

    public int getExprPointerLevel() {
        return exprPointer.size();
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

    public String getOrCreateAccumulatorBindingId(String drlConstraint) {
        return idGenerator.getOrCreateAccumulateBindingId(drlConstraint);
    }

    public void addNamedConsequence(String key, String value) {
        namedConsequences.put(key, value);
    }

    public RuleDescr getRuleDescr() {
        return ruleDescr;
    }

    public String getRuleName() {
        return ruleDescr.getName();
    }

    public RuleDialect getRuleDialect() {
        return ruleDialect;
    }

    public void setRuleDialect(RuleDialect ruleDialect) {
        this.ruleDialect = ruleDialect;
    }

    public Optional<QueryParameter> getQueryParameterByName(String name) {
        return queryParameters.stream().filter(p -> p.getName().equals( name )).findFirst();
    }

    public List<QueryParameter> getQueryParameters() {
        return queryParameters;
    }

    public void addQueryParameter(QueryParameter queryParameter) {
        queryParameters.add(queryParameter);
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    public Optional<String> getQueryName() {
        return queryName;
    }

    public void setQueryName(Optional<String> queryName) {
        this.queryName = queryName;
    }

    public boolean isRecurisveQuery(String queryName) {
        return this.queryName.isPresent() && this.queryName.get().equals(queryName);
    }

    public boolean isQuery() {
        return queryName.isPresent();
    }

    public Map<String, String> getNamedConsequences() {
        return namedConsequences;
    }

    public Map<AggregateKey, String> getAggregatePatternMap() {
        return aggregatePatternMap;
    }

    public PackageModel getPackageModel() {
        return packageModel;
    }

    public TypeResolver getTypeResolver() {
        return typeResolver;
    }

    public boolean isPropertyReactive( Class<?> patternClass) {
        PropertySpecificOption propertySpecificOption = kbuilder.getBuilderConfiguration().getPropertySpecificOption();
        return propertySpecificOption.isPropSpecific( patternClass.getAnnotation( PropertyReactive.class ) != null,
                                                      patternClass.getAnnotation( ClassReactive.class ) != null );
    }

    public Optional<FunctionType> getFunctionType(String name) {
        Method m = packageModel.getStaticMethod(name);
        if (m != null) {
            return of(new FunctionType(m.getReturnType(), Arrays.asList(m.getParameterTypes())));
        }

        return packageModel.getFunctions().stream()
                .filter( method -> method.getNameAsString().equals( name ) )
                .findFirst()
                .flatMap( method -> {
                    Optional<Class<?>> returnType = resolveType(method.getType().asString());

                    List<String> parametersType = method.getParameters().stream().map(Parameter::getType).map(Type::asString).collect(toList());
                    List<Class<?>> resolvedParameterTypes = parametersType.stream()
                            .map(this::resolveType)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .collect(toList());
                    return returnType.map(t -> new FunctionType(t, resolvedParameterTypes));
                });
    }

    public static class FunctionType {
        private final Class<?> returnType;
        private final List<Class<?>> argumentsType;

        public FunctionType(Class<?> returnType, List<Class<?>> argumentsType) {
            this.returnType = returnType;
            this.argumentsType = argumentsType;
        }

        public Class<?> getReturnType() {
            return returnType;
        }

        public List<Class<?>> getArgumentsType() {
            return argumentsType;
        }
    }

    public Optional<Class<?>> resolveType(String name) {
        try {
            return of( typeResolver.resolveType( name ) );
        } catch(ClassNotFoundException e) {
            return empty();
        }
    }

    public boolean isNestedInsideOr() {
        return isNestedInsideOr;
    }

    public void setNestedInsideOr(boolean nestedInsideOr) {
        isNestedInsideOr = nestedInsideOr;
    }

    public Bag<String> getBindingOr() {
        return bindingOr;
    }

    public Set<String> getUnusableOrBinding() {
        return unusableOrBinding;
    }

    public String fromVar(String key) {
        return key.substring( "var_".length(), key.length() - currentScope.id.length() );
    }

    public String getOutOfScopeVar( String x ) {
        return x == null ? null : x + scopesStack.getLast().id;
    }

    public Expression getVarExpr(String x) {
        return getVarExpr( x, getVar( x ) );
    }

    public Expression getVarExpr( String x, String var ) {
        if (!isQuery()) {
            new NameExpr( var );
        }

        Optional<QueryParameter> optQueryParameter = getQueryParameterByName(x);
        return optQueryParameter.map(qp -> {

            final String queryDef = getQueryName().orElseThrow(RuntimeException::new);

            final int queryParameterIndex = getQueryParameters().indexOf(qp) + 1;
            return (Expression)new MethodCallExpr(new NameExpr(queryDef), toQueryArg(queryParameterIndex));

        }).orElse(new NameExpr( var ));
    }

    public String getVar( String x ) {
        if ( idGenerator.isGenerated( x ) || ruleUnitVars.containsKey( x ) ) {
            return DrlxParseUtil.toVar( x );
        }
        String var = x.endsWith( "sCoPe" ) ? x : definedVars.get(x);
        return DrlxParseUtil.toVar(var != null ? var : x + currentScope.id);
    }

    public void pushScope(ConditionalElementDescr scopeElement) {
        scopesStack.addLast( currentScope );
        currentScope = new Scope(scopeElement);
    }

    public void popScope() {
        currentScope.clear();
        currentScope = scopesStack.removeLast();
    }

    public String getForallFirstIdentifier() {
        return currentScope.forallFirstIdentifier;
    }

    private class Scope {
        private final String id;
        private final String forallFirstIdentifier;
        private final List<String> vars = new ArrayList<>();

        private Scope() {
            this( "", null );
        }

        private Scope( ConditionalElementDescr scopeElement ) {
            this( "_" + scopeCounter++ + "_sCoPe", scopeElement );
        }

        private Scope( String id, ConditionalElementDescr scopeElement ) {
            this.id = id;
            this.forallFirstIdentifier =
                (scopeElement instanceof ForallDescr && scopeElement.getDescrs().size() == 2 && scopeElement.getDescrs().get( 0 ) instanceof PatternDescr) ?
                (( PatternDescr ) scopeElement.getDescrs().get( 0 )).getIdentifier() : null;
        }

        private void clear() {
            vars.forEach( v -> {
                definedVars.remove( v );
                scopedDeclarations.remove( getBindingVar( v ) );
            } );
        }

        private String getBindingVar( String var ) {
            return idGenerator.isGenerated( var ) ? var : var + id;
        }

        @Override
        public String toString() {
            return "Scope: " + id;
        }
    }

    public void setDialectFromAttributes(Collection<AttributeDescr> attributes) {
        for (AttributeDescr a : attributes) {
            if (a.getName().equals("dialect")) {
                if (a.getValue().equals("mvel")) {
                    setRuleDialect(RuleDialect.MVEL);
                }
                return;
            }
        }
    }

    public int getLegacyAccumulateCounter() {
        return legacyAccumulateCounter;
    }

    public void increaseLegacyAccumulateCounter() {
        legacyAccumulateCounter++;
    }

    public Type getDelarationType(String variableName) {
        return getDeclarationById(variableName).map(DeclarationSpec::getBoxedType)
                                               .orElseGet(UnknownType::new);
    }

    public void addPatternBinding(String patternBinding) {
        patternBindings.add(patternBinding);
    }

    public boolean isPatternBinding(String patternBinding) {
        return patternBindings.contains(patternBinding);
    }

    public Optional<BaseDescr> getCurrentConstraintDescr() {
        return currentConstraintDescr;
    }

    public void setCurrentConstraintDescr(Optional<BaseDescr> currentConstraintDescr) {
        this.currentConstraintDescr = currentConstraintDescr;
    }

    public void resetCurrentConstraintDescr() {
        this.currentConstraintDescr = empty();
    }

    public void setParentDescr( AndDescr parentDescr ) {
        this.parentDescr = parentDescr;
    }

    public AndDescr getParentDescr() {
        return parentDescr;
    }

    public BlockStmt getRuleVariablesBlock() {
        return ruleVariablesBlock;
    }

    @Override
    public String toString() {
        return "RuleContext for " + ruleDescr.getNamespace() + "." + ruleDescr.getName();
    }
}

