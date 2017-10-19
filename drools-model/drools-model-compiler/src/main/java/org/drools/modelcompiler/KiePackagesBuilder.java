/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.ClassTypeResolver;
import org.drools.core.base.DroolsQuery;
import org.drools.core.base.TypeResolver;
import org.drools.core.base.extractors.ArrayElementReader;
import org.drools.core.base.extractors.SelfReferenceClassFieldReader;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.rule.Accumulate;
import org.drools.core.rule.Behavior;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.EntryPointId;
import org.drools.core.rule.GroupElement;
import org.drools.core.rule.MultiAccumulate;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.QueryArgument;
import org.drools.core.rule.QueryElement;
import org.drools.core.rule.QueryImpl;
import org.drools.core.rule.RuleConditionElement;
import org.drools.core.rule.SingleAccumulate;
import org.drools.core.rule.SlidingLengthWindow;
import org.drools.core.rule.SlidingTimeWindow;
import org.drools.core.rule.WindowDeclaration;
import org.drools.core.rule.constraint.QueryNameConstraint;
import org.drools.core.ruleunit.RuleUnitUtil;
import org.drools.core.spi.Accumulator;
import org.drools.core.spi.GlobalExtractor;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.model.AccumulateFunction;
import org.drools.model.AccumulatePattern;
import org.drools.model.Argument;
import org.drools.model.Condition;
import org.drools.model.Consequence;
import org.drools.model.Constraint;
import org.drools.model.EntryPoint;
import org.drools.model.Global;
import org.drools.model.Model;
import org.drools.model.OOPath;
import org.drools.model.Query;
import org.drools.model.Rule;
import org.drools.model.SingleConstraint;
import org.drools.model.Value;
import org.drools.model.Variable;
import org.drools.model.View;
import org.drools.model.WindowDefinition;
import org.drools.model.WindowReference;
import org.drools.model.constraints.SingleConstraint1;
import org.drools.model.functions.Predicate1;
import org.drools.model.impl.DeclarationImpl;
import org.drools.model.patterns.QueryCallPattern;
import org.drools.modelcompiler.consequence.LambdaConsequence;
import org.drools.modelcompiler.constraints.ConstraintEvaluator;
import org.drools.modelcompiler.constraints.LambdaAccumulator;
import org.drools.modelcompiler.constraints.LambdaConstraint;
import org.drools.modelcompiler.constraints.LambdaReadAccessor;
import org.drools.modelcompiler.constraints.TemporalConstraintEvaluator;
import org.drools.modelcompiler.constraints.UnificationConstraint;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Role.Type;

import static org.drools.core.rule.Pattern.getReadAcessor;
import static org.drools.model.DSL.*;
import static org.drools.model.impl.NamesGenerator.generateName;
import static org.drools.modelcompiler.ModelCompilerUtil.conditionToGroupElementType;

public class KiePackagesBuilder {

    private final RuleBaseConfiguration configuration;

    private Map<String, KiePackage> packages = new HashMap<>();

    private Set<Class<?>> patternClasses = new HashSet<>();

    public KiePackagesBuilder( KieBaseConfiguration conf ) {
        this.configuration = ( (RuleBaseConfiguration) conf );
    }

    public void addModel( Model model ) {
        for (Global global : model.getGlobals()) {
            KnowledgePackageImpl pkg = (KnowledgePackageImpl) packages.computeIfAbsent( global.getPackage(), this::createKiePackage );
            pkg.addGlobal( global.getName(), global.getType().asClass() );
        }
        for (Query query : model.getQueries()) {
            KnowledgePackageImpl pkg = (KnowledgePackageImpl) packages.computeIfAbsent( query.getPackage(), this::createKiePackage );
            pkg.addRule( compileQuery( pkg, query ) );
        }
        for (Rule rule : model.getRules()) {
            KnowledgePackageImpl pkg = (KnowledgePackageImpl) packages.computeIfAbsent( rule.getPackage(), this::createKiePackage );
            pkg.addRule( compileRule( pkg, rule ) );
        }
    }

    private KnowledgePackageImpl createKiePackage(String name) {
        KnowledgePackageImpl kpkg = new KnowledgePackageImpl( name );
        kpkg.setClassFieldAccessorCache(new ClassFieldAccessorCache( configuration.getClassLoader() ) );
        TypeResolver typeResolver = new ClassTypeResolver( new HashSet<>( kpkg.getImports().keySet() ),
                                                           configuration.getClassLoader(),
                                                           name );
        typeResolver.addImport( name + ".*" );
        kpkg.setTypeResolver(typeResolver);
        return kpkg;
    }

    public Collection<Class<?>> getPatternClasses() {
        return patternClasses;
    }

    private RuleImpl compileRule( KnowledgePackageImpl pkg, Rule rule ) {
        RuleImpl ruleImpl = new RuleImpl( rule.getName() );
        ruleImpl.setPackage( rule.getPackage() );
        if (rule.getUnit() != null) {
            ruleImpl.setRuleUnitClassName( rule.getUnit() );
            pkg.getRuleUnitRegistry().getRuleUnitFor( ruleImpl );
        }
        RuleContext ctx = new RuleContext( pkg, ruleImpl );
        populateLHS( ctx, pkg, rule.getView() );
        processConsequence( ctx, rule.getConsequence() );
        return ruleImpl;
    }

    private QueryImpl compileQuery( KnowledgePackageImpl pkg, Query query ) {
        QueryImpl queryImpl = new QueryImpl( query.getName() );
        queryImpl.setPackage( query.getPackage() );
        RuleContext ctx = new RuleContext( pkg, queryImpl );
        addQueryPattern( query, queryImpl, ctx );
        populateLHS( ctx, pkg, query.getView() );
        return queryImpl;
    }

    private void addQueryPattern( Query query, QueryImpl queryImpl, RuleContext ctx ) {
        Pattern pattern = new Pattern( ctx.getNextPatternIndex(),
                                       0, // offset is 0 by default
                                       ClassObjectType.DroolsQuery_ObjectType,
                                       null );
        QueryNameConstraint constraint = new QueryNameConstraint( null, query.getName() );
        pattern.addConstraint( constraint );
        queryImpl.getLhs().addChild(pattern);

        Variable<?>[] args = query.getArguments();
        Declaration[] declarations = new Declaration[args.length];
        for (int i = 0; i < args.length; i++) {
            declarations[i] = new Declaration( args[i].getName(), pattern );
            int index = i;
            declarations[i].setReadAccessor( new LambdaReadAccessor(index, args[index].getType().asClass(),
                                                                    obj -> ( (DroolsQuery) obj ).getElements()[index] ) );
            pattern.addDeclaration( declarations[i] );
            ctx.addQueryDeclaration( args[i], declarations[i] );
        }

        queryImpl.setParameters( declarations );
    }

    private void processConsequence( RuleContext ctx, Consequence consequence ) {
        ctx.getRule().setConsequence( new LambdaConsequence( consequence, ctx ) );

        Variable[] consequenceVars = consequence.getDeclarations();
        String[] requiredDeclarations = new String[consequenceVars.length];
        for (int i = 0; i < consequenceVars.length; i++) {
            requiredDeclarations[i] = consequenceVars[i].getName();
        }

        ctx.getRule().setRequiredDeclarationsForConsequence( RuleImpl.DEFAULT_CONSEQUENCE_NAME, requiredDeclarations );
    }

    private void populateLHS( RuleContext ctx, KnowledgePackageImpl pkg, View view ) {
        GroupElement lhs = ctx.getRule().getLhs();
        if (ctx.getRule().getRuleUnitClassName() != null) {
            lhs.addChild( addPatternForVariable( ctx, getUnitVariable( ctx, pkg, view ) ) );
        }
        view.getSubConditions().forEach( condition -> lhs.addChild( conditionToElement(ctx, condition) ) );
        if (requiresLeftActivation(lhs)) {
            lhs.addChild( 0, new Pattern( 0, ClassObjectType.InitialFact_ObjectType ) );
        }
    }

    private boolean requiresLeftActivation( RuleConditionElement rce ) {
        if (rce instanceof GroupElement) {
            GroupElement and = (GroupElement) rce;
            return and.getChildren().isEmpty() || requiresLeftActivation( and.getChildren().get( 0 ) );
        }
        return rce instanceof QueryElement;
    }

    private Variable getUnitVariable( RuleContext ctx, KnowledgePackageImpl pkg, View view ) {
        String unitClassName = ctx.getRule().getRuleUnitClassName();
        for (Variable<?> var : view.getBoundVariables()) {
            if ( var instanceof DeclarationImpl && var.getType().asClass().getName().equals( unitClassName ) ) {
                return ( (DeclarationImpl) var ).setSource( entryPoint( RuleUnitUtil.RULE_UNIT_ENTRY_POINT ) );
            }
        }
        try {
            return declarationOf( type( pkg.getTypeResolver().resolveType( unitClassName ) ), entryPoint( RuleUnitUtil.RULE_UNIT_ENTRY_POINT ) );
        } catch (ClassNotFoundException e) {
            throw new RuntimeException( e );
        }
    }

    private RuleConditionElement conditionToElement( RuleContext ctx, Condition condition ) {
        if (condition.getType().isComposite()) {
            GroupElement ge = new GroupElement( conditionToGroupElementType( condition.getType() ) );
            for (Condition subCondition : condition.getSubConditions()) {
                ge.addChild( conditionToElement( ctx, subCondition ) );
            }
            return ge;
        }

        switch (condition.getType()) {
            case PATTERN: {
                return buildPattern( ctx, condition );
            }
            case ACCUMULATE: {
                Pattern source = buildPattern( ctx, condition );
                Pattern pattern = new Pattern( 0, getObjectType( Object.class ) );
                pattern.setSource( buildAccumulate( (AccumulatePattern) condition, source, pattern ) );
                return pattern;
            }
            case OOPATH: {
                OOPath ooPath = (OOPath) condition;
                Pattern pattern = buildPattern( ctx, ooPath.getFirstCondition() );
                pattern.setSource( new EntryPointId( ctx.getRule().getRuleUnitClassName() + "." + ooPath.getSource().getName() ) );
                return pattern;
            }
            case QUERY:
                return buildQueryPattern( ctx, ( (QueryCallPattern) condition ) );
            case NOT:
            case EXISTS: {
                GroupElement ge = new GroupElement( conditionToGroupElementType( condition.getType() ) );
                // existential pattern can have only one subcondition
                ge.addChild( conditionToElement( ctx, condition.getSubConditions().get(0) ) );
                return ge;
            }
        }
        throw new UnsupportedOperationException();
    }

    private RuleConditionElement buildQueryPattern( RuleContext ctx, QueryCallPattern queryPattern ) {
        Pattern pattern = new Pattern( ctx.getNextPatternIndex(),
                                       0,
                                       ClassObjectType.ObjectArray_ObjectType,
                                       null );

        InternalReadAccessor arrayReader = new SelfReferenceClassFieldReader( Object[].class );
        QueryArgument[] arguments = new QueryArgument[queryPattern.getArguments().length];
        List<Integer> varIndexList = new ArrayList<>();
        List<Declaration> requiredDeclarations = new ArrayList<>();

        for (int i = 0; i < queryPattern.getArguments().length; i++) {
            Argument arg = queryPattern.getArguments()[i];
            if (arg instanceof Variable ) {
                Variable var = ( (Variable) arg );
                Declaration decl = ctx.getDeclaration( var );
                if (decl != null) {
                    requiredDeclarations.add( decl );
                    arguments[i] = new QueryArgument.Declr(decl);
                } else {
                    ArrayElementReader reader = new ArrayElementReader( arrayReader,
                                                                        i,
                                                                        arg.getType().asClass() );
                    pattern.addDeclaration( var.getName() ).setReadAccessor( reader );
                    arguments[i] = QueryArgument.VAR;
                    varIndexList.add( i );
                }
            } else if (arg instanceof Value ) {
                arguments[i] = new QueryArgument.Literal( ( (Value) arg ).getValue() );
            } else {
                throw new UnsupportedOperationException();
            }
        }
        return new QueryElement( pattern,
                                 queryPattern.getQuery().getName(),
                                 arguments,
                                 varIndexList.stream().mapToInt(i->i).toArray(),
                                 requiredDeclarations.toArray( new Declaration[requiredDeclarations.size()] ),
                                 true, // TODO: openQuery
                                 false ); // TODO: query.isAbductive() );
    }

    private Pattern buildPattern( RuleContext ctx, Condition condition ) {
        org.drools.model.Pattern modelPattern = (org.drools.model.Pattern) condition;
        Pattern pattern = addPatternForVariable( ctx, modelPattern.getPatternVariable() );

        Declaration queryArgDecl = ctx.getQueryDeclaration( modelPattern.getPatternVariable() );
        if (queryArgDecl != null) {
            pattern.addConstraint( new UnificationConstraint( queryArgDecl ) );
        }

        addConstraintsToPattern( ctx, pattern, modelPattern, modelPattern.getConstraint() );
        return pattern;
    }

    private Accumulate buildAccumulate( AccumulatePattern accPattern, Pattern source, Pattern pattern ) {
        AccumulateFunction<?, ?, ?>[] accFunc = accPattern.getFunctions();

        if (accFunc.length == 1) {
            pattern.addDeclaration( new Declaration(accPattern.getBoundVariables()[0].getName(),
                                                    getReadAcessor( getObjectType( Object.class ) ),
                                                    pattern,
                                                    true) );
            return new SingleAccumulate( source, new Declaration[0], new LambdaAccumulator( accPattern.getFunctions()[0]));
        }

        InternalReadAccessor reader = new SelfReferenceClassFieldReader( Object[].class );
        Accumulator[] accumulators = new Accumulator[accFunc.length];
        for (int i = 0; i < accPattern.getFunctions().length; i++) {
            Variable accVar = accPattern.getBoundVariables()[i];
            pattern.addDeclaration( new Declaration(accVar.getName(),
                                                    new ArrayElementReader( reader, i, accVar.getType().asClass()),
                                                    pattern,
                                                    true) );
            accumulators[i] = new LambdaAccumulator( accFunc[i] );
        }
        return new MultiAccumulate( source, new Declaration[0], accumulators);
    }

    private Pattern addPatternForVariable( RuleContext ctx, Variable patternVariable ) {
        Class<?> patternClass = patternVariable.getType().asClass();
        patternClasses.add( patternClass );
        Pattern pattern = new Pattern( ctx.getNextPatternIndex(),
                                       0, // offset will be set by ReteooBuilder
                                       getObjectType( patternClass ),
                                       patternVariable.getName(),
                                       true );

        if ( patternVariable instanceof org.drools.model.Declaration ) {
            org.drools.model.Declaration decl = (org.drools.model.Declaration) patternVariable;
            if ( decl.getSource() != null ) {
                if ( decl.getSource() instanceof EntryPoint ) {
                    pattern.setSource( new EntryPointId( ( (EntryPoint) decl.getSource() ).getName() ) );
                } else if ( decl.getSource() instanceof WindowReference ) {
                    WindowReference<?> window = (WindowReference) decl.getSource();
                    if ( !ctx.getPkg().getWindowDeclarations().containsKey( window.getName() ) ) {
                        createWindowReference( ctx, window );
                    }
                    pattern.setSource( new org.drools.core.rule.WindowReference( window.getName() ) );
                } else {
                    throw new UnsupportedOperationException( "Unknown source: " + decl.getSource() );
                }
            }
            if ( decl.getWindow() != null ) {
                pattern.addBehavior( createWindow( decl.getWindow() ) );
            }
        }
        ctx.registerPattern( patternVariable, pattern );
        return pattern;
    }

    private <T> void createWindowReference( RuleContext ctx, WindowReference<T> window ) {
        WindowDeclaration windowDeclaration = new WindowDeclaration( window.getName(), ctx.getPkg().getName() );
        Variable<T> variable = declarationOf( type( window.getPatternType() ) );
        Pattern windowPattern = new Pattern(0, getObjectType( window.getPatternType() ), variable.getName() );
        windowDeclaration.setPattern( windowPattern );
        for ( Predicate1<T> predicate : window.getPredicates()) {
            SingleConstraint singleConstraint = new SingleConstraint1<>( generateName("expr"), variable, predicate );
            ConstraintEvaluator constraintEvaluator = new ConstraintEvaluator( windowPattern, singleConstraint );
            windowPattern.addConstraint( new LambdaConstraint( constraintEvaluator ) );
        }
        windowPattern.addBehavior( createWindow( window ) );
        ctx.getPkg().addWindowDeclaration(windowDeclaration);
    }

    private Behavior createWindow( WindowDefinition window ) {
        switch (window.getType()) {
            case LENGTH:
                return new SlidingLengthWindow( (int) window.getValue() );
            case TIME:
                return new SlidingTimeWindow( window.getValue() );
        }
        throw new IllegalArgumentException( "Unknown window type: " + window.getType() );
    }

    private void addConstraintsToPattern( RuleContext ctx, Pattern pattern, org.drools.model.Pattern modelPattern, Constraint constraint ) {
        if (constraint.getType() == Constraint.Type.SINGLE) {
            SingleConstraint singleConstraint = (SingleConstraint) constraint;
            Declaration[] declarations = getRequiredDeclaration(ctx, singleConstraint);

            if (singleConstraint.getVariables().length > 0) {
                ConstraintEvaluator constraintEvaluator = singleConstraint.isTemporal() ?
                                                          new TemporalConstraintEvaluator( declarations, pattern, singleConstraint ) :
                                                          new ConstraintEvaluator( declarations, pattern, singleConstraint );
                pattern.addConstraint( new LambdaConstraint( constraintEvaluator ) );
                addFieldsToPatternWatchlist( pattern, singleConstraint.getReactiveProps() );
            }

        } else if (modelPattern.getConstraint().getType() == Constraint.Type.AND) {
            for (Constraint child : constraint.getChildren()) {
                addConstraintsToPattern(ctx, pattern, modelPattern, child);
            }
        }
    }

    private void addFieldsToPatternWatchlist( Pattern pattern, String[] fields ) {
        if (fields != null && fields.length > 0) {
            Collection<String> watchlist = pattern.getListenedProperties();
            if ( watchlist == null ) {
                watchlist = new HashSet<>( );
                pattern.setListenedProperties( watchlist );
            }
            watchlist.addAll( Arrays.asList( fields ) );
        }
    }

    private Declaration[] getRequiredDeclaration( RuleContext ctx, SingleConstraint singleConstraint ) {
        Variable[] vars = singleConstraint.getVariables();
        Declaration[] declarations = new Declaration[vars.length];
        for (int i = 0; i < vars.length; i++) {
            if (vars[i].isFact()) {
                declarations[i] = ctx.getDeclaration( vars[i] );
            } else {
                Global global = ( (Global) vars[i] );
                ClassObjectType objectType = getObjectType( global.getType().asClass() );
                InternalReadAccessor globalExtractor = new GlobalExtractor( global.getName(), objectType);
                declarations[i] = new Declaration( global.getName(), globalExtractor, new Pattern( 0, objectType ) );
            }
        }
        return declarations;
    }

    public Collection<KiePackage> getKnowledgePackages() {
        return packages.values();
    }

    private Map<Class<?>, ClassObjectType> objectTypeCache = new HashMap<>();
    private ClassObjectType getObjectType( Class<?> patternClass ) {
        return objectTypeCache.computeIfAbsent( patternClass, c -> new ClassObjectType( c, isEvent( c ) ) );
    }

    private boolean isEvent( Class<?> patternClass ) {
        Role role = patternClass.getAnnotation( Role.class );
        return role != null && role.value() == Type.EVENT;
    }
}
