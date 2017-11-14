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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.DroolsQuery;
import org.drools.core.base.EnabledBoolean;
import org.drools.core.base.SalienceInteger;
import org.drools.core.base.extractors.ArrayElementReader;
import org.drools.core.base.extractors.SelfReferenceClassFieldReader;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.rule.Accumulate;
import org.drools.core.rule.Behavior;
import org.drools.core.rule.ConditionalBranch;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.EntryPointId;
import org.drools.core.rule.EvalCondition;
import org.drools.core.rule.Forall;
import org.drools.core.rule.GroupElement;
import org.drools.core.rule.MultiAccumulate;
import org.drools.core.rule.NamedConsequence;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.QueryArgument;
import org.drools.core.rule.QueryElement;
import org.drools.core.rule.QueryImpl;
import org.drools.core.rule.RuleConditionElement;
import org.drools.core.rule.SingleAccumulate;
import org.drools.core.rule.SlidingLengthWindow;
import org.drools.core.rule.SlidingTimeWindow;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.rule.WindowDeclaration;
import org.drools.core.rule.constraint.QueryNameConstraint;
import org.drools.core.ruleunit.RuleUnitUtil;
import org.drools.core.spi.Accumulator;
import org.drools.core.spi.DataProvider;
import org.drools.core.spi.EvalExpression;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.model.AccumulateFunction;
import org.drools.model.AccumulatePattern;
import org.drools.model.Argument;
import org.drools.model.Binding;
import org.drools.model.Condition;
import org.drools.model.Consequence;
import org.drools.model.Constraint;
import org.drools.model.EntryPoint;
import org.drools.model.From;
import org.drools.model.Global;
import org.drools.model.Model;
import org.drools.model.OOPath;
import org.drools.model.Query;
import org.drools.model.Rule;
import org.drools.model.SingleConstraint;
import org.drools.model.TypeMetaData;
import org.drools.model.Value;
import org.drools.model.Variable;
import org.drools.model.View;
import org.drools.model.WindowDefinition;
import org.drools.model.WindowReference;
import org.drools.model.consequences.ConditionalNamedConsequenceImpl;
import org.drools.model.consequences.NamedConsequenceImpl;
import org.drools.model.constraints.SingleConstraint1;
import org.drools.model.functions.Predicate1;
import org.drools.model.impl.DeclarationImpl;
import org.drools.model.patterns.QueryCallPattern;
import org.drools.modelcompiler.consequence.LambdaConsequence;
import org.drools.modelcompiler.consequence.MVELConsequence;
import org.drools.modelcompiler.constraints.ConstraintEvaluator;
import org.drools.modelcompiler.constraints.LambdaAccumulator;
import org.drools.modelcompiler.constraints.LambdaConstraint;
import org.drools.modelcompiler.constraints.LambdaDataProvider;
import org.drools.modelcompiler.constraints.LambdaEvalExpression;
import org.drools.modelcompiler.constraints.LambdaReadAccessor;
import org.drools.modelcompiler.constraints.TemporalConstraintEvaluator;
import org.drools.modelcompiler.constraints.UnificationConstraint;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.type.Role;
import org.kie.soup.project.datamodel.commons.types.ClassTypeResolver;
import org.kie.soup.project.datamodel.commons.types.TypeResolver;

import static org.drools.core.rule.GroupElement.AND;
import static org.drools.core.rule.Pattern.getReadAcessor;
import static org.drools.model.DSL.declarationOf;
import static org.drools.model.DSL.entryPoint;
import static org.drools.model.DSL.type;
import static org.drools.model.impl.NamesGenerator.generateName;
import static org.drools.modelcompiler.ModelCompilerUtil.conditionToGroupElementType;
import static org.drools.modelcompiler.util.TypeDeclarationUtil.createTypeDeclaration;

public class KiePackagesBuilder {

    private final RuleBaseConfiguration configuration;

    private Map<String, KiePackage> packages = new HashMap<>();

    private Set<Class<?>> patternClasses = new HashSet<>();

    private Map<Class<?>, ClassObjectType> objectTypeCache = new HashMap<>();

    public KiePackagesBuilder( KieBaseConfiguration conf ) {
        this.configuration = ( (RuleBaseConfiguration) conf );
    }

    public void addModel( Model model ) {
        for (TypeMetaData metaType : model.getTypeMetaDatas()) {
            KnowledgePackageImpl pkg = (KnowledgePackageImpl) packages.computeIfAbsent( metaType.getPackage(), this::createKiePackage );
            pkg.addTypeDeclaration( createTypeDeclaration(pkg, metaType) );
        }

        for (Global global : model.getGlobals()) {
            KnowledgePackageImpl pkg = (KnowledgePackageImpl) packages.computeIfAbsent( global.getPackage(), this::createKiePackage );
            pkg.addGlobal( global.getName(), global.getType().asClass() );
        }

        for (Query query : model.getQueries()) {
            KnowledgePackageImpl pkg = (KnowledgePackageImpl) packages.computeIfAbsent( query.getPackage(), this::createKiePackage );
            pkg.addRule( compileQuery( pkg, query ) );
        }

        int ruleCounter = 0;
        for (Rule rule : model.getRules()) {
            KnowledgePackageImpl pkg = (KnowledgePackageImpl) packages.computeIfAbsent( rule.getPackage(), this::createKiePackage );
            RuleImpl ruleImpl = compileRule( pkg, rule );
            ruleImpl.setLoadOrder( ruleCounter++ );
            pkg.addRule( ruleImpl );
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
        ruleImpl.setPackage( pkg.getName() );
        setRuleAttributes( rule, ruleImpl );
        ruleImpl.setPackage( rule.getPackage() );
        if (rule.getUnit() != null) {
            ruleImpl.setRuleUnitClassName( rule.getUnit() );
            pkg.getRuleUnitRegistry().getRuleUnitFor( ruleImpl );
        }
        RuleContext ctx = new RuleContext( this, pkg, ruleImpl );
        populateLHS( ctx, pkg, rule.getView() );
        processConsequences( ctx, rule );
        return ruleImpl;
    }

    private void setRuleAttributes( Rule rule, RuleImpl ruleImpl ) {
        Boolean noLoop = setAttribute( rule, Rule.Attribute.NO_LOOP, ruleImpl::setNoLoop );
        Boolean lockOnActive = setAttribute( rule, Rule.Attribute.LOCK_ON_ACTIVE, ruleImpl::setLockOnActive );
        setAttribute( rule, Rule.Attribute.AUTO_FOCUS, ruleImpl::setAutoFocus );

        setAttribute( rule, Rule.Attribute.ENABLED, e -> ruleImpl.setEnabled( new EnabledBoolean(e) ) );
        setAttribute( rule, Rule.Attribute.SALIENCE, s -> ruleImpl.setSalience( new SalienceInteger( s ) ) );
        String agendaGroup = setAttribute( rule, Rule.Attribute.AGENDA_GROUP, ruleImpl::setAgendaGroup );
        setAttribute( rule, Rule.Attribute.RULEFLOW_GROUP, rfg -> {
            ruleImpl.setRuleFlowGroup(rfg);
            if (agendaGroup == null) {
                ruleImpl.setAgendaGroup( rfg );
            }
        } );

        setAttribute( rule, Rule.Attribute.ACTIVATION_GROUP, ruleImpl::setActivationGroup );
        setAttribute( rule, Rule.Attribute.DURATION, t -> ruleImpl.setTimer( parseTimer( t ) ) );
        setAttribute( rule, Rule.Attribute.TIMER, t -> ruleImpl.setTimer( parseTimer( t ) ) );
        setAttribute( rule, Rule.Attribute.CALENDARS, ruleImpl::setCalendars );
        setAttribute( rule, Rule.Attribute.DATE_EFFECTIVE, ruleImpl::setDateEffective );
        setAttribute( rule, Rule.Attribute.DATE_EXPIRES, ruleImpl::setDateExpires );

        ruleImpl.setEager( ruleImpl.isEager() || noLoop != null || lockOnActive != null );
    }

    private <T> T setAttribute( Rule rule, Rule.Attribute<T> attribute, Consumer<T> consumer ) {
        T value = rule.getAttribute( attribute );
        if ( value != attribute.getDefaultValue() ) {
            consumer.accept( value );
            return value;
        }
        return null;
    }

    private org.drools.core.time.impl.Timer parseTimer( String s ) {
        throw new UnsupportedOperationException();
    }

    private QueryImpl compileQuery( KnowledgePackageImpl pkg, Query query ) {
        QueryImpl queryImpl = new QueryImpl( query.getName() );
        queryImpl.setPackage( query.getPackage() );
        RuleContext ctx = new RuleContext( this, pkg, queryImpl );
        addQueryPattern( query, queryImpl, ctx );
        populateLHS( ctx, pkg, query.getView() );
        return queryImpl;
    }

    private void addQueryPattern( Query query, QueryImpl queryImpl, RuleContext ctx ) {
        Pattern pattern = new Pattern( ctx.getNextPatternIndex(),
                                       0, // offset is 0 by default
                                       ClassObjectType.DroolsQuery_ObjectType,
                                       null );

        InternalReadAccessor extractor = new LambdaReadAccessor(DroolsQuery.class, q -> ((DroolsQuery)q).getName());
        QueryNameConstraint constraint = new QueryNameConstraint( extractor, query.getName() );
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

    private void processConsequences( RuleContext ctx, Rule rule ) {
        for (Map.Entry<String, Consequence> entry : rule.getConsequences().entrySet()) {
            processConsequence( ctx, entry.getValue(), entry.getKey() );
        }
    }

    private void processConsequence( RuleContext ctx, Consequence consequence, String name ) {
        if ( name.equals( RuleImpl.DEFAULT_CONSEQUENCE_NAME ) ) {
            if ("java".equals(consequence.getLanguage())) {
                ctx.getRule().setConsequence(new LambdaConsequence(consequence, ctx));
            } else if ("mvel".equals(consequence.getLanguage())) {
                ctx.getRule().setConsequence(new MVELConsequence(consequence, ctx));
            } else {
                throw new UnsupportedOperationException("Unknown script language for consequence: " + consequence.getLanguage());
            }
        } else {
            ctx.getRule().addNamedConsequence( name, new LambdaConsequence( consequence, ctx ) );
        }

        Variable[] consequenceVars = consequence.getDeclarations();
        String[] requiredDeclarations = new String[consequenceVars.length];
        for (int i = 0; i < consequenceVars.length; i++) {
            requiredDeclarations[i] = consequenceVars[i].getName();
        }

        ctx.getRule().setRequiredDeclarationsForConsequence( name, requiredDeclarations );
    }

    private void populateLHS( RuleContext ctx, KnowledgePackageImpl pkg, View view ) {
        GroupElement lhs = ctx.getRule().getLhs();
        if (ctx.getRule().getRuleUnitClassName() != null) {
            lhs.addChild( addPatternForVariable( ctx, getUnitVariable( ctx, pkg, view ) ) );
        }
        addSubConditions( ctx, lhs, view.getSubConditions());
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
            return addSubConditions( ctx, new GroupElement( conditionToGroupElementType( condition.getType() ) ), condition.getSubConditions() );
        }

        switch (condition.getType()) {
            case PATTERN: {
                return buildPattern( ctx, condition );
            }
            case ACCUMULATE: {
                Pattern source = buildPattern( ctx, condition );
                Pattern pattern = new Pattern( 0, getObjectType( Object.class ) );
                pattern.setSource( buildAccumulate( ctx, (AccumulatePattern) condition, source, pattern ) );
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
            case FORALL: {
                Condition innerCondition = condition.getSubConditions().get(0);
                Pattern basePattern = (Pattern) conditionToElement( ctx, innerCondition.getSubConditions().get(0) );
                List<Pattern> remainingPatterns = new ArrayList<>();
                for (int i = 1; i < innerCondition.getSubConditions().size(); i++) {
                    remainingPatterns.add( (Pattern) conditionToElement( ctx, innerCondition.getSubConditions().get(i) ) );
                }
                return new Forall(basePattern, remainingPatterns);
            }
            case CONSEQUENCE:
                if (condition instanceof NamedConsequenceImpl) {
                    NamedConsequenceImpl consequence = (NamedConsequenceImpl) condition;
                    return consequence.getName().equals( RuleImpl.DEFAULT_CONSEQUENCE_NAME ) ? null : new NamedConsequence( consequence.getName(), consequence.isBreaking() );
                } else if (condition instanceof ConditionalNamedConsequenceImpl) {
                    return buildConditionalConsequence(ctx, (ConditionalNamedConsequenceImpl) condition);
                }
        }
        throw new UnsupportedOperationException();
    }

    private ConditionalBranch buildConditionalConsequence(RuleContext ctx, ConditionalNamedConsequenceImpl consequence) {
        EvalCondition evalCondition;
        if (consequence.getExpr() != null) {
            Pattern pattern = ctx.getPattern(consequence.getExpr().getVariables()[0]);
            EvalExpression eval = new LambdaEvalExpression(pattern, consequence.getExpr());
            evalCondition = new EvalCondition(eval, pattern.getRequiredDeclarations());
        } else {
            evalCondition = new EvalCondition(LambdaEvalExpression.EMPTY, null);
        }
        return new ConditionalBranch( evalCondition,
                                      new NamedConsequence( consequence.getThenConsequence().getName(), consequence.getThenConsequence().isBreaking() ),
                                      consequence.getElseBranch() != null ? buildConditionalConsequence(ctx, consequence.getElseBranch()) : null );
    }

    private RuleConditionElement addSubConditions( RuleContext ctx, GroupElement ge, List<Condition> subconditions) {
        for (Condition subCondition : subconditions) {
            RuleConditionElement element = conditionToElement( ctx, subCondition );
            if (element != null) {
                ge.addChild( element );
            }
        }
        if (ge.getType() == AND && ge.getChildren().size() == 1) {
            return ge.getChildren().get(0);
        }
        return ge;
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
                                 queryPattern.isOpen(),
                                 false ); // TODO: query.isAbductive() );
    }

    private Pattern buildPattern( RuleContext ctx, Condition condition ) {
        org.drools.model.Pattern<Object> modelPattern = (org.drools.model.Pattern) condition;
        Pattern pattern = addPatternForVariable( ctx, modelPattern.getPatternVariable() );

        for (Binding binding : modelPattern.getBindings()) {
            Declaration declaration = new Declaration(binding.getBoundVariable().getName(),
                                                      new LambdaReadAccessor(binding.getBoundVariable().getType().asClass(),
                                                                             binding.getBindingFunction()),
                                                      pattern,
                                                      true);
            pattern.addDeclaration( declaration );
            if (binding.getReactOn() != null) {
                addFieldsToPatternWatchlist( pattern, binding.getReactOn() );
            }
            ctx.addInnerDeclaration(binding.getBoundVariable(), declaration);
        }

        Declaration queryArgDecl = ctx.getQueryDeclaration( modelPattern.getPatternVariable() );
        if (queryArgDecl != null) {
            pattern.addConstraint( new UnificationConstraint( queryArgDecl ) );
        }

        addConstraintsToPattern( ctx, pattern, modelPattern, modelPattern.getConstraint() );
        addFieldsToPatternWatchlist( pattern, modelPattern.getWatchedProps() );
        return pattern;
    }

    private Accumulate buildAccumulate( RuleContext ctx, AccumulatePattern accPattern, Pattern source, Pattern pattern ) {
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
                } else if ( decl.getSource() instanceof From ) {
                    From<?> from = (From) decl.getSource();
                    DataProvider provider = new LambdaDataProvider( ctx.getDeclaration( from.getVariable() ), from.getProvider(), from.isReactive() );
                    org.drools.core.rule.From fromSource = new org.drools.core.rule.From(provider);
                    fromSource.setResultPattern( pattern );
                    pattern.setSource( fromSource );
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
            Declaration[] declarations = getRequiredDeclarations(ctx, singleConstraint);

            if (singleConstraint.getVariables().length > 0) {
                ConstraintEvaluator constraintEvaluator = singleConstraint.isTemporal() ?
                                                          new TemporalConstraintEvaluator( declarations, pattern, singleConstraint ) :
                                                          new ConstraintEvaluator( declarations, pattern, singleConstraint );
                pattern.addConstraint( new LambdaConstraint( constraintEvaluator ) );
            }

        } else if (modelPattern.getConstraint().getType() == Constraint.Type.AND) {
            for (Constraint child : constraint.getChildren()) {
                addConstraintsToPattern(ctx, pattern, modelPattern, child);
            }
        }
    }

    private void addFieldsToPatternWatchlist( Pattern pattern, String... fields ) {
        if (fields != null && fields.length > 0) {
            Collection<String> watchlist = pattern.getListenedProperties();
            if ( watchlist == null ) {
                watchlist = new HashSet<>( );
                pattern.setListenedProperties( watchlist );
            }
            for (String field : fields) {
                watchlist.add( field );
            }
        }
    }

    private Declaration[] getRequiredDeclarations( RuleContext ctx, SingleConstraint singleConstraint ) {
        Variable[] vars = singleConstraint.getVariables();
        Declaration[] declarations = new Declaration[vars.length];
        for (int i = 0; i < vars.length; i++) {
            declarations[i] = ctx.getDeclaration( vars[i] );
        }
        return declarations;
    }

    public Collection<KiePackage> getKnowledgePackages() {
        return packages.values();
    }


    ClassObjectType getObjectType( Class<?> patternClass ) {
        return objectTypeCache.computeIfAbsent( patternClass, c -> {
            boolean isEvent = false;
            if (!patternClass.getName().startsWith( "java." )) {
                KnowledgePackageImpl pkg = (KnowledgePackageImpl) packages.computeIfAbsent( patternClass.getPackage().getName(), this::createKiePackage );
                TypeDeclaration typeDeclaration = pkg.getTypeDeclaration( patternClass );
                if ( typeDeclaration == null ) {
                    typeDeclaration = createTypeDeclaration( pkg, patternClass );
                    pkg.addTypeDeclaration( typeDeclaration );
                }
                isEvent = typeDeclaration.getRole() == Role.Type.EVENT;
            }
            return new ClassObjectType( c, isEvent );
        } );
    }
}
