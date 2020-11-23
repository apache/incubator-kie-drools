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
import java.util.ArrayList;
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
import java.util.function.Predicate;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.UnknownType;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.BaseKnowledgeBuilderResultImpl;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.AttributeDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.ConditionalElementDescr;
import org.drools.compiler.lang.descr.ForallDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.core.addon.TypeResolver;
import org.drools.core.ruleunit.RuleUnitDescriptionLoader;
import org.drools.core.util.Bag;
import org.drools.modelcompiler.builder.PackageModel;
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

import static org.drools.modelcompiler.builder.generator.QueryGenerator.toQueryArg;
import static org.kie.internal.ruleunit.RuleUnitUtil.isLegacyRuleUnit;

public class RuleContext {

    private final KnowledgeBuilderImpl kbuilder;
    private final PackageModel packageModel;
    private final TypeResolver typeResolver;
    private DRLIdGenerator idGenerator;
    private RuleDescr descr;
    private final boolean generatePatternDSL;

    private List<DeclarationSpec> allDeclarations = new ArrayList<>();
    private Map<String, DeclarationSpec> scopedDeclarations = new LinkedHashMap<>();
    private List<DeclarationSpec> ooPathDeclarations = new ArrayList<>();
    private Deque<Consumer<Expression>> exprPointer = new LinkedList<>();
    private List<Expression> expressions = new ArrayList<>();
    private Map<String, String> namedConsequences = new HashMap<>();

    private List<QueryParameter> queryParameters = new ArrayList<>();
    private Optional<String> queryName = empty();

    private RuleUnitDescription ruleUnitDescr;
    private Map<String, Class<?>> ruleUnitVars = new HashMap<>();
    private Map<String, Class<?>> ruleUnitVarsOriginalType = new HashMap<>();

    private Map<AggregateKey, String> aggregatePatternMap = new HashMap<>();

    /* These are used to check if some binding used in an OR expression is used in every branch */
    private Boolean isNestedInsideOr = false;
    private Bag<String> bindingOr = new Bag<>();
    private Set<String> unusableOrBinding = new HashSet<>();

    private RuleDialect ruleDialect = RuleDialect.JAVA; // assumed is java by default as per Drools manual.

    private Scope currentScope = new Scope();
    private Deque<Scope> scopesStack = new LinkedList<>();
    private Map<String, String> definedVars = new HashMap<>();

    private Map<String, Type> explicitCastType = new HashMap<>();

    // These are used for indexing see PatternBuilder:1198
    private Set<String> patternBindings = new HashSet<>();

    private int legacyAccumulateCounter = 0;

    private Optional<BaseDescr> currentConstraintDescr = empty();

    public enum RuleDialect {
        JAVA,
        MVEL;
    }

    public BaseDescr parentDesc = null;

    public RuleContext(KnowledgeBuilderImpl kbuilder, PackageModel packageModel, TypeResolver typeResolver, boolean generatePatternDSL) {
        this.kbuilder = kbuilder;
        this.packageModel = packageModel;
        this.idGenerator = packageModel.getExprIdGenerator();
        exprPointer.push( this.expressions::add );
        this.typeResolver = typeResolver;
        this.generatePatternDSL = generatePatternDSL;
    }

    private void findUnitDescr() {
        if (descr == null) {
            return;
        }

        boolean useNamingConvention = false;
        String unitName = null;
        AnnotationDescr unitAnn = descr.getAnnotation( "Unit" );
        if (unitAnn != null) {
            unitName = ( String ) unitAnn.getValue();
            unitName = unitName.substring( 0, unitName.length() - ".class".length() );
        } else if (descr.getUnit() != null) {
            unitName = descr.getUnit().getTarget();
        } else {
            if (descr.getResource() == null) {
                return;
            }
            String drlFile = descr.getResource().getSourcePath();
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

    public boolean isPatternDSL() {
        return generatePatternDSL;
    }

    public RuleUnitDescription getRuleUnitDescr() {
        return ruleUnitDescr;
    }

    public KnowledgeBuilderImpl getKbuilder() {
        return kbuilder;
    }

    public void addCompilationError( KnowledgeBuilderResult error ) {
        if ( error instanceof BaseKnowledgeBuilderResultImpl ) {
            (( BaseKnowledgeBuilderResultImpl ) error).setResource( descr.getResource() );
        }
        kbuilder.addBuilderResult( error );
    }

    public boolean hasErrors() {
        return kbuilder.hasResults( ResultSeverity.ERROR );
    }

    public RuleContext addInlineCastType(String field, Type type) {
        explicitCastType.put(field, type);
        return this;
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
        return getDeclarationById(id).orElseThrow(() -> new RuntimeException(id));
    }

    private String getDeclarationKey( String id ) {
        String var = definedVars.get(id);
        return var != null ? var : id;
    }

    public void removeDeclarationById(String id) {
        scopedDeclarations.remove( getDeclarationKey( id ) );
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
        String bindingId = currentScope.id + var;
        definedVars.put(var, bindingId);
        currentScope.vars.add(var);
        return bindingId;
    }

    public DeclarationSpec addDeclaration(DeclarationSpec d) {
        scopedDeclarations.putIfAbsent( d.getBindingId(), d );
        return d;
    }

    public void addDeclarationReplacing(DeclarationSpec d) {
        final String bindingId = d.getBindingId();
        final Optional<DeclarationSpec> declarationById = getDeclarationById(bindingId);
        if (declarationById.isPresent()) {
            removeDeclarationById(bindingId);
        }
        this.scopedDeclarations.put(d.getBindingId(), d);
    }

    public void addOOPathDeclaration(DeclarationSpec d) {
        if(!getOOPathDeclarationById(d.getBindingId()).isPresent()) {
            this.ooPathDeclarations.add(d);
        }
    }

    public Collection<DeclarationSpec> getAllDeclarations() {
        if (allDeclarations.isEmpty()) {
            return scopedDeclarations.values();
        }
        List declrs = new ArrayList( scopedDeclarations.values() );
        declrs.addAll( allDeclarations );
        return declrs;
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
        return descr;
    }

    public void setDescr(RuleDescr descr) {
        this.descr = descr;
        processUnitData();
    }

    public String getRuleName() {
        return descr.getName();
    }

    public RuleDialect getRuleDialect() {
        return ruleDialect;
    }

    public void setRuleDialect(RuleDialect ruleDialect) {
        this.ruleDialect = ruleDialect;
    }

    public Optional<QueryParameter> queryParameterWithName(Predicate<? super QueryParameter> predicate) {
        return queryParameters.stream().filter(predicate).findFirst();
    }

    public List<QueryParameter> getQueryParameters() {
        return queryParameters;
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

    public Optional<Class<?>> getFunctionType(String name) {
        Method m = packageModel.getStaticMethod(name);
        if (m != null) {
            return of(m.getReturnType());
        }

        return packageModel.getFunctions().stream()
                .filter( method -> method.getNameAsString().equals( name ) )
                .findFirst()
                .flatMap( method -> resolveType( method.getType().asString() ) );
    }

    public Optional<Class<?>> resolveType(String name) {
        try {
            return of( typeResolver.resolveType( name ) );
        } catch(ClassNotFoundException e) {
            return empty();
        }
    }

    public Boolean isNestedInsideOr() {
        return isNestedInsideOr;
    }

    public void setNestedInsideOr(Boolean nestedInsideOr) {
        isNestedInsideOr = nestedInsideOr;
    }

    public Bag<String> getBindingOr() {
        return bindingOr;
    }

    public Set<String> getUnusableOrBinding() {
        return unusableOrBinding;
    }

    public Expression getVarExpr(String x) {
        if (!isQuery()) {
            new NameExpr( getVar( x ) );
        }

        Optional<QueryParameter> optQueryParameter = queryParameterWithName(p -> p.name.equals(x));
        return optQueryParameter.map(qp -> {

            final String queryDef = getQueryName().orElseThrow(RuntimeException::new);

            final int queryParameterIndex = getQueryParameters().indexOf(qp) + 1;
            return (Expression)new MethodCallExpr(new NameExpr(queryDef), toQueryArg(queryParameterIndex));

        }).orElse(new NameExpr( getVar( x ) ));
    }

    public String getVar( String x ) {
        String var = x.startsWith( "sCoPe" ) ? x : definedVars.get(x);
        return DrlxParseUtil.toVar(var != null ? var : currentScope.id + x);
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

    private static int scopeCounter = 1;
    private class Scope {
        private final String id;
        private final ConditionalElementDescr scopeElement;
        private final String forallFirstIdentifier;
        private List<String> vars = new ArrayList<>();

        private Scope() {
            this( "", null );
        }

        private Scope( ConditionalElementDescr scopeElement ) {
            this( "sCoPe" + scopeCounter++ + "_", scopeElement );
        }

        private Scope( String id, ConditionalElementDescr scopeElement ) {
            this.id = id;
            this.scopeElement = scopeElement;
            forallFirstIdentifier =
                (scopeElement instanceof ForallDescr && scopeElement.getDescrs().size() == 2 && scopeElement.getDescrs().get( 0 ) instanceof PatternDescr) ?
                (( PatternDescr ) scopeElement.getDescrs().get( 0 )).getIdentifier() : null;
        }

        private void clear() {
            vars.forEach( v -> {
                definedVars.remove(v);
                allDeclarations.add( scopedDeclarations.remove( id + v ) );
            } );
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

    @Override
    public String toString() {
        return "RuleContext for " + descr.getNamespace() + "." + descr.getName();
    }
}

