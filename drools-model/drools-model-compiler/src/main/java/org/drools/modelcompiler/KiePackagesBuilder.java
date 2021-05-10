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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.DroolsQuery;
import org.drools.core.base.EnabledBoolean;
import org.drools.core.base.SalienceInteger;
import org.drools.core.base.accumulators.CountAccumulateFunction;
import org.drools.core.base.extractors.ArrayElementReader;
import org.drools.core.base.extractors.SelfReferenceClassFieldReader;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.facttemplates.FactTemplateObjectType;
import org.drools.core.rule.Accumulate;
import org.drools.core.rule.AsyncReceive;
import org.drools.core.rule.AsyncSend;
import org.drools.core.rule.Behavior;
import org.drools.core.rule.ConditionalBranch;
import org.drools.core.rule.ConditionalElement;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.EntryPointId;
import org.drools.core.rule.EvalCondition;
import org.drools.core.rule.Forall;
import org.drools.core.rule.GroupElement;
import org.drools.core.rule.MultiAccumulate;
import org.drools.core.rule.NamedConsequence;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.PatternSource;
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
import org.drools.core.spi.Accumulator;
import org.drools.core.spi.DataProvider;
import org.drools.core.spi.Enabled;
import org.drools.core.spi.EvalExpression;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.ObjectType;
import org.drools.core.spi.Salience;
import org.drools.model.AccumulatePattern;
import org.drools.model.Argument;
import org.drools.model.Binding;
import org.drools.model.Condition;
import org.drools.model.Consequence;
import org.drools.model.Constraint;
import org.drools.model.DomainClassMetadata;
import org.drools.model.DynamicValueSupplier;
import org.drools.model.EntryPoint;
import org.drools.model.From;
import org.drools.model.From0;
import org.drools.model.From1;
import org.drools.model.From2;
import org.drools.model.From3;
import org.drools.model.From4;
import org.drools.model.Global;
import org.drools.model.GroupByPattern;
import org.drools.model.Index;
import org.drools.model.Model;
import org.drools.model.Prototype;
import org.drools.model.PrototypeVariable;
import org.drools.model.Query;
import org.drools.model.Rule;
import org.drools.model.SingleConstraint;
import org.drools.model.TypeMetaData;
import org.drools.model.UnitData;
import org.drools.model.Value;
import org.drools.model.Variable;
import org.drools.model.View;
import org.drools.model.WindowDefinition;
import org.drools.model.WindowReference;
import org.drools.model.consequences.ConditionalNamedConsequenceImpl;
import org.drools.model.consequences.NamedConsequenceImpl;
import org.drools.model.constraints.AbstractSingleConstraint;
import org.drools.model.constraints.SingleConstraint1;
import org.drools.model.functions.Function0;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Predicate1;
import org.drools.model.functions.accumulate.AccumulateFunction;
import org.drools.model.impl.DeclarationImpl;
import org.drools.model.impl.Exchange;
import org.drools.model.patterns.CompositePatterns;
import org.drools.model.patterns.EvalImpl;
import org.drools.model.patterns.ExistentialPatternImpl;
import org.drools.model.patterns.GroupByPatternImpl;
import org.drools.model.patterns.PatternImpl;
import org.drools.model.patterns.QueryCallPattern;
import org.drools.model.view.SelfPatternBiding;
import org.drools.modelcompiler.attributes.LambdaEnabled;
import org.drools.modelcompiler.attributes.LambdaSalience;
import org.drools.modelcompiler.consequence.LambdaConsequence;
import org.drools.modelcompiler.constraints.AbstractConstraint;
import org.drools.modelcompiler.constraints.BindingEvaluator;
import org.drools.modelcompiler.constraints.BindingInnerObjectEvaluator;
import org.drools.modelcompiler.constraints.CombinedConstraint;
import org.drools.modelcompiler.constraints.ConstraintEvaluator;
import org.drools.modelcompiler.constraints.LambdaAccumulator;
import org.drools.modelcompiler.constraints.LambdaConstraint;
import org.drools.modelcompiler.constraints.LambdaDataProvider;
import org.drools.modelcompiler.constraints.LambdaEvalExpression;
import org.drools.modelcompiler.constraints.LambdaGroupByAccumulate;
import org.drools.modelcompiler.constraints.LambdaReadAccessor;
import org.drools.modelcompiler.constraints.TemporalConstraintEvaluator;
import org.drools.modelcompiler.constraints.UnificationConstraint;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.definition.rule.All;
import org.kie.api.definition.rule.Direct;
import org.kie.api.definition.rule.Propagation;
import org.kie.api.definition.type.Role;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.conf.PropertySpecificOption;
import org.kie.internal.ruleunit.RuleUnitUtil;

import static java.util.stream.Collectors.toList;

import static org.drools.compiler.rule.builder.RuleBuilder.buildTimer;
import static org.drools.core.rule.GroupElement.AND;
import static org.drools.core.rule.Pattern.getReadAcessor;
import static org.drools.model.DSL.declarationOf;
import static org.drools.model.DSL.entryPoint;
import static org.drools.model.bitmask.BitMaskUtil.calculatePatternMask;
import static org.drools.model.functions.FunctionUtils.toFunctionN;
import static org.drools.model.impl.NamesGenerator.generateName;
import static org.drools.modelcompiler.facttemplate.FactFactory.prototypeToFactTemplate;
import static org.drools.modelcompiler.util.EvaluationUtil.adaptBitMask;
import static org.drools.modelcompiler.util.TimerUtil.buildTimerExpression;
import static org.drools.modelcompiler.util.TypeDeclarationUtil.createTypeDeclaration;
import static org.kie.internal.ruleunit.RuleUnitUtil.isLegacyRuleUnit;

public class KiePackagesBuilder {

    private static final ObjectType JAVA_CLASS_ARRAY_TYPE = new ClassObjectType( Object[].class );

    private final RuleBaseConfiguration configuration;
    private final KnowledgeBuilderConfiguration builderConf;

    private final Map<String, InternalKnowledgePackage> packages = new HashMap<>();

    private final Map<String, ObjectType> objectTypeCache = new HashMap<>();

    private final Collection<Model> models;

    public KiePackagesBuilder(KieBaseConfiguration conf) {
        this(conf, null, new ArrayList<>());
    }

    public KiePackagesBuilder( KieBaseConfiguration conf, KnowledgeBuilderConfiguration builderConf, Collection<Model> models) {
        this.configuration = ((RuleBaseConfiguration) conf);
        this.builderConf = builderConf;
        this.models = models;
    }

    public void addModel( Model model ) {
        models.add(model);
    }

    public CanonicalKiePackages build() {
        for (Model model : models) {
            for (EntryPoint entryPoint : model.getEntryPoints()) {
                KnowledgePackageImpl pkg = ( KnowledgePackageImpl ) packages.computeIfAbsent( model.getName(), this::createKiePackage );
                pkg.addEntryPointId( entryPoint.getName() );
            }

            for (TypeMetaData metaType : model.getTypeMetaDatas()) {
                KnowledgePackageImpl pkg = ( KnowledgePackageImpl ) packages.computeIfAbsent( metaType.getPackage(), this::createKiePackage );
                pkg.addTypeDeclaration( createTypeDeclaration( metaType, getPropertySpecificOption(), pkg.getTypeResolver() ) );
            }

            for (Global global : model.getGlobals()) {
                KnowledgePackageImpl pkg = ( KnowledgePackageImpl ) packages.computeIfAbsent( global.getPackage(), this::createKiePackage );
                pkg.addGlobal( global.getName(), global.getType() );
            }

            for (Query query : model.getQueries()) {
                KnowledgePackageImpl pkg = ( KnowledgePackageImpl ) packages.computeIfAbsent( query.getPackage(), this::createKiePackage );
                pkg.addRule( compileQuery( pkg, query ) );
            }

            int ruleCounter = 0;
            for (Rule rule : model.getRules()) {
                KnowledgePackageImpl pkg = ( KnowledgePackageImpl ) packages.computeIfAbsent( rule.getPackage(), this::createKiePackage );
                for (RuleImpl ruleImpl : compileRule( pkg, rule ) ) {
                    ruleImpl.setLoadOrder( ruleCounter++ );
                    pkg.addRule( ruleImpl );
                }
            }
        }
        return new CanonicalKiePackages(packages);
    }

    public ClassLoader getClassLoader() {
        return configuration.getClassLoader();
    }

    private KnowledgePackageImpl createKiePackage(String name) {
        KnowledgePackageImpl kpkg = new KnowledgePackageImpl( name );
        kpkg.setClassFieldAccessorCache(new ClassFieldAccessorCache( getClassLoader() ) );
        kpkg.setClassLoader( getClassLoader() );
        return kpkg;
    }

    private List<RuleImpl> compileRule( KnowledgePackageImpl pkg, Rule rule ) {
        RuleImpl ruleImpl = new RuleImpl( rule.getName() );
        ruleImpl.setPackage( pkg.getName() );
        ruleImpl.setPackage( rule.getPackage() );
        if (rule.getUnit() != null) {
            ruleImpl.setRuleUnitClassName( rule.getUnit() );
            pkg.getRuleUnitDescriptionLoader().getDescription(ruleImpl );
        }
        RuleContext ctx = new RuleContext( this, pkg, ruleImpl );
        populateLHS( ctx, pkg, rule.getView() );
        processConsequences( ctx, rule.getConsequences() );
        if (ctx.needsStreamMode()) {
            pkg.setNeedStreamMode();
        }
        setRuleAttributes( rule, ruleImpl, ctx );
        setRuleMetaAttributes( rule, ruleImpl );

        return Collections.singletonList( ruleImpl );
    }

    private void setRuleAttributes( Rule rule, RuleImpl ruleImpl, RuleContext ctx ) {
        Boolean noLoop = setAttribute( rule, Rule.Attribute.NO_LOOP, ruleImpl::setNoLoop );
        Boolean lockOnActive = setAttribute( rule, Rule.Attribute.LOCK_ON_ACTIVE, ruleImpl::setLockOnActive );
        setAttribute( rule, Rule.Attribute.AUTO_FOCUS, ruleImpl::setAutoFocus );

        setDynamicAttribute( rule, Rule.Attribute.ENABLED, e -> ruleImpl.setEnabled( createEnabled(e) ) );
        setDynamicAttribute( rule, Rule.Attribute.SALIENCE, s -> ruleImpl.setSalience( createSalience( s ) ) );
        String agendaGroup = setAttribute( rule, Rule.Attribute.AGENDA_GROUP, ruleImpl::setAgendaGroup );
        setAttribute( rule, Rule.Attribute.RULEFLOW_GROUP, rfg -> {
            ruleImpl.setRuleFlowGroup(rfg);
            if (agendaGroup == null) {
                ruleImpl.setAgendaGroup( rfg );
            }
        } );

        setAttribute( rule, Rule.Attribute.ACTIVATION_GROUP, ruleImpl::setActivationGroup );
        setAttribute( rule, Rule.Attribute.DURATION, t -> ruleImpl.setTimer( parseTimer( ruleImpl, t, ctx ) ) );
        setAttribute( rule, Rule.Attribute.TIMER, t -> ruleImpl.setTimer( parseTimer( ruleImpl, t, ctx ) ) );
        setAttribute( rule, Rule.Attribute.CALENDARS, ruleImpl::setCalendars );
        setAttribute( rule, Rule.Attribute.DATE_EFFECTIVE, ruleImpl::setDateEffective );
        setAttribute( rule, Rule.Attribute.DATE_EXPIRES, ruleImpl::setDateExpires );

        ruleImpl.setEager( ruleImpl.isEager() || noLoop != null || lockOnActive != null );
    }

    private Salience createSalience( Object s ) {
        if (s instanceof Integer) {
            return new SalienceInteger( (int) s );
        }
        if (s instanceof DynamicValueSupplier) {
            return new LambdaSalience( (DynamicValueSupplier<Integer>) s );
        }
        throw new UnsupportedOperationException( "Unknown salience type: " + s.getClass().getCanonicalName() );
    }

    private Enabled createEnabled( Object e ) {
        if (e instanceof Boolean) {
            return new EnabledBoolean( (boolean) e );
        }
        if (e instanceof DynamicValueSupplier) {
            return new LambdaEnabled( (DynamicValueSupplier<Boolean>) e );
        }
        throw new UnsupportedOperationException( "Unknown salience type: " + e.getClass().getCanonicalName() );
    }

    private <T> T setAttribute( Rule rule, Rule.Attribute<T> attribute, Consumer<T> consumer ) {
        T value = rule.getAttribute( attribute );
        if ( value != attribute.getDefaultValue() ) {
            consumer.accept( value );
            return value;
        }
        return null;
    }

    private void setDynamicAttribute( Rule rule, Rule.Attribute<?> attribute, Consumer<Object> consumer ) {
        Object value = rule.getAttribute( attribute );
        if ( value != attribute.getDefaultValue() ) {
            consumer.accept( value );
        }
    }

    private void setRuleMetaAttributes(Rule rule, RuleImpl ruleImpl) {
        for (Entry<String, Object> kv : rule.getMetaData().entrySet()) {
            ruleImpl.addMetaAttribute(kv.getKey(), kv.getValue());
            if (kv.getKey().equals( Propagation.class.getName() ) || kv.getKey().equals( Propagation.class.getSimpleName() )) {
                // to support backward compatibility of executable model code generated before DROOLS-5678, we still support the Propagation=<value> to be a String representation --rather than correctly the enum value (since DROOLS-5678)
                if (Propagation.Type.IMMEDIATE.toString().equals(kv.getValue()) || Propagation.Type.IMMEDIATE == kv.getValue()) {
                    ruleImpl.setDataDriven(true);
                } else if (Propagation.Type.EAGER.toString().equals(kv.getValue()) || Propagation.Type.EAGER == kv.getValue()) {
                    ruleImpl.setEager(true);
                }
            } else if (kv.getKey().equals( All.class.getName() ) || kv.getKey().equals( All.class.getSimpleName() )) {
                ruleImpl.setAllMatches(true);
            } else if (kv.getKey().equals( Direct.class.getName() ) || kv.getKey().equals( Direct.class.getSimpleName() )) {
                ruleImpl.setActivationListener("direct");
            }
        }
    }

    private org.drools.core.time.impl.Timer parseTimer( RuleImpl ruleImpl, String timerExpr, RuleContext ctx ) {
        return buildTimer(ruleImpl, timerExpr, null, expr -> buildTimerExpression( expr, ctx.getDeclarations() ), e -> {
            throw new IllegalArgumentException("Invalid timer expression: '" + e + "' in rule " + ruleImpl.getName());
        });
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
                                       0, // tupleIndex is 0 by default
                                       0, // patternIndex is 0 by default
                                       ClassObjectType.DroolsQuery_ObjectType,
                                       null );

        InternalReadAccessor extractor = new LambdaReadAccessor(DroolsQuery.class, q -> ((DroolsQuery)q).getName());
        QueryNameConstraint constraint = new QueryNameConstraint( extractor, query.getName() );
        pattern.addConstraint( constraint );
        queryImpl.getLhs().addChild(pattern);

        Variable<?>[] args = query.getArguments();
        Declaration[] declarations = new Declaration[args.length];
        for (int i = 0; i < args.length; i++) {
            int index = i;
            LambdaReadAccessor accessor = new LambdaReadAccessor(index, args[index].getType(), obj -> ( (DroolsQuery) obj ).getElements()[index] );
            declarations[i] = new Declaration( args[i].getName(), accessor, pattern, false );
            pattern.addDeclaration( declarations[i] );
            ctx.addQueryDeclaration( args[i], declarations[i] );
        }

        queryImpl.setParameters( declarations );
    }

    private void processConsequences( RuleContext ctx, Map<String, Consequence> consequences ) {
        for (Map.Entry<String, Consequence> entry : consequences.entrySet()) {
            processConsequence( ctx, entry.getValue(), entry.getKey() );
        }
    }

    private void processConsequence( RuleContext ctx, Consequence consequence, String name ) {
        // @TODO this might be wasteful, as we calculating this else where too, and now I only need the boolean (mdp).
        // This is changed, because we must use the Declarations provided by the RTN, otherwise tuple indexes are not set.
        Declaration[] requiredDeclarations = getRequiredDeclarationsIfPossible( ctx, consequence, name );
        boolean enabledTupleOptimization = requiredDeclarations != null && requiredDeclarations.length > 0;

        if ( name.equals( RuleImpl.DEFAULT_CONSEQUENCE_NAME ) ) {
            if ("java".equals(consequence.getLanguage())) {
                ctx.getRule().setConsequence( new LambdaConsequence( consequence, enabledTupleOptimization ) );
            } else {
                throw new UnsupportedOperationException("Unknown script language for consequence: " + consequence.getLanguage());
            }
        } else {
            ctx.getRule().addNamedConsequence( name, new LambdaConsequence( consequence, enabledTupleOptimization ) );
        }
    }

    private Declaration[] getRequiredDeclarationsIfPossible( RuleContext ctx, Consequence consequence, String name ) {
        // Retrieving the required declarations for the consequence at build time allows to extract from the activation
        // tuple the arguments to be passed to the consequence in linear time by traversing the tuple only once.
        // If there's an OR in the rule the fired tuple hasn't fixed structure and size because it dependens
        // on which branch of the OR gets activated. In this case no optimization is possible and it's usless
        // to precalculate the declartions at build time.
        boolean ruleHasFirstLevelOr = ruleHasFirstLevelOr( ctx.getRule());

        Variable[] consequenceVars = consequence.getDeclarations();
        String[] requiredDeclarationNames = new String[consequenceVars.length];
        Declaration[] requiredDeclarations = ruleHasFirstLevelOr ? null : new Declaration[consequenceVars.length];
        for (int i = 0; i < consequenceVars.length; i++) {
            requiredDeclarationNames[i] = consequenceVars[i].getName();
            if (!ruleHasFirstLevelOr) {
                requiredDeclarations[i] = ctx.getDeclaration( consequenceVars[i] );
            }
        }

        ctx.getRule().setRequiredDeclarationsForConsequence( name, requiredDeclarationNames );
        return requiredDeclarations;
    }

    private boolean ruleHasFirstLevelOr(RuleImpl rule) {
        GroupElement lhs = rule.getLhs();
        if (lhs.getType() == GroupElement.Type.OR) {
            return true;
        }
        if (lhs.getType() == GroupElement.Type.AND) {
            for (RuleConditionElement child : lhs.getChildren()) {
                if ( child instanceof GroupElement && (( GroupElement ) child).getType() == GroupElement.Type.OR ) {
                    return true;
                }
            }
        }
        return false;
    }

    private void populateLHS( RuleContext ctx, KnowledgePackageImpl pkg, View view ) {
        GroupElement lhs = ctx.getRule().getLhs();
        if (isLegacyRuleUnit() && ctx.getRule().getRuleUnitClassName() != null) {
            lhs.addChild( addPatternForVariable( ctx, lhs, getUnitVariable( ctx, pkg, view ), Condition.Type.PATTERN ) );
        }
        addSubConditions( ctx, lhs, view.getSubConditions());
        if (requiresLeftActivation(lhs)) {
            lhs.addChild( 0, new Pattern( ctx.getNextPatternIndex(), ClassObjectType.InitialFact_ObjectType ) );
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
            if ( var instanceof DeclarationImpl && var.getType().getName().equals( unitClassName ) ) {
                return ( (DeclarationImpl) var ).setSource( entryPoint( RuleUnitUtil.RULE_UNIT_ENTRY_POINT ) );
            }
        }
        try {
            return declarationOf( pkg.getTypeResolver().resolveType( unitClassName ), entryPoint( RuleUnitUtil.RULE_UNIT_ENTRY_POINT ) );
        } catch (ClassNotFoundException e) {
            throw new RuntimeException( e );
        }
    }

    private RuleConditionElement conditionToElement( RuleContext ctx, GroupElement group, Condition condition ) {
        if (condition.getType().isComposite()) {
            return addSubConditions( ctx, new GroupElement( conditionToGroupElementType( condition.getType() ) ), condition.getSubConditions() );
        }

        switch (condition.getType()) {
            case SENDER:
            case RECEIVER:
            case PATTERN:
                RuleConditionElement rce = buildPattern( ctx, group, (org.drools.model.Pattern<?>) condition );
                if ( rce instanceof Pattern) {
                    // sometimes returns an eval
                    Pattern pattern = (Pattern) rce;
                    // Do not add the pattern if it already exists. This is the case for additional accumulate constraits.
                    return !group.getChildren().contains(pattern) ? pattern : null;
                }

                return rce;
            case EVAL:
                return buildEval( ctx, ( EvalImpl ) condition );

            case ACCUMULATE:
            case GROUP_BY:
                return buildAccumulate( ctx, group, (AccumulatePattern) condition );

            case QUERY:
                return buildQueryPattern( ctx, ( (QueryCallPattern) condition ) );

            case NOT:
            case EXISTS: {
                // existential pattern can have only one subcondition
                return new GroupElement( conditionToGroupElementType( condition.getType() ) )
                        .addChild( conditionToElement( ctx, group, condition.getSubConditions().get(0) ) );
            }
            case FORALL: {
                return buildForAll( ctx, group, condition );
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

    private ConditionalElement buildForAll( RuleContext ctx, GroupElement group, Condition condition ) {
        Condition innerCondition = condition.getSubConditions().get(0);
        if (innerCondition instanceof PatternImpl) {
            return new GroupElement( GroupElement.Type.NOT )
                    .addChild( conditionToElement( ctx, group, (( PatternImpl ) innerCondition).negate() ) );
        }

        Constraint selfJoinConstraint = getForallSelfJoin( innerCondition );
        if (selfJoinConstraint != null) {
            return buildSelfJoinForAll( ctx, group, innerCondition, selfJoinConstraint );
        }

        List<Pattern> remainingPatterns = new ArrayList<>();
        Pattern basePattern = ( Pattern ) conditionToElement( ctx, group, innerCondition.getSubConditions().get( 0 ) );
        for (int i = 1; i < innerCondition.getSubConditions().size(); i++) {
            remainingPatterns.add( ( Pattern ) conditionToElement( ctx, group, innerCondition.getSubConditions().get( i ) ) );
        }
        return new Forall(basePattern, remainingPatterns);
    }

    private GroupElement buildSelfJoinForAll( RuleContext ctx, GroupElement group, Condition innerCondition, Constraint selfJoinConstraint ) {
        // transforms a self join forall in the form
        // forall( $t : Type( constraints1 ) Type( this == $t, constraints2 ) )
        // into
        // exists( Type( constraints1 ) ) and not( Type( constraints1, !constraints2 ) )

        PatternImpl forallPattern = ( PatternImpl ) innerCondition.getSubConditions().get( 0 );
        PatternImpl joinPattern = ( PatternImpl ) innerCondition.getSubConditions().get( 1 );

        GroupElement transformedForall = new GroupElement( GroupElement.Type.AND );
        transformedForall.addChild( new GroupElement( GroupElement.Type.EXISTS ).addChild( conditionToElement( ctx, group, forallPattern ) ) );

        joinPattern.getConstraint().getChildren().remove( selfJoinConstraint );
        forallPattern.addConstraint( joinPattern.negate().getConstraint().replaceVariable( joinPattern.getPatternVariable(), forallPattern.getPatternVariable() ) );
        transformedForall.addChild( new GroupElement( GroupElement.Type.NOT ).addChild( conditionToElement( ctx, group, forallPattern ) ) );

        return transformedForall;
    }

    private RuleConditionElement buildAccumulate( RuleContext ctx, GroupElement group, AccumulatePattern accumulatePattern ) {
        Pattern pattern = null;
        boolean isGroupBy = accumulatePattern instanceof GroupByPattern;
        if (accumulatePattern.getAccumulateFunctions() != null) {
            if (!isGroupBy && accumulatePattern.getAccumulateFunctions().length == 1) {
                // non groupby with single accumulates can be optimized to directly return the result, rather than place in an array of 1
                pattern = ctx.getPattern(accumulatePattern.getAccumulateFunctions()[0].getResult());
            } else if (accumulatePattern.getAccumulateFunctions().length > 0 &&
                       ctx.getPattern(accumulatePattern.getAccumulateFunctions()[0].getResult()) != null) {
                // Illegal executable model. Cannot have groupby or multi accumulate mapped to a single result object.
                throw new RuntimeException("Only single accumulate functions, with no group by can optimize the result pattern to be the function return value");
            }
        }

        boolean existingPattern = pattern != null;
        if (!existingPattern) {
            ObjectType type = !isGroupBy && accumulatePattern.getAccumulateFunctions().length == 1 ?
                new ClassObjectType(accumulatePattern.getAccumulateFunctions()[0].getResult().getType()) :
                JAVA_CLASS_ARRAY_TYPE; // groupby or multi function accumulate
            pattern = new Pattern( ctx.getNextPatternIndex(), type );
        }

        PatternImpl<?> sourcePattern = (PatternImpl<?>) accumulatePattern.getPattern();
        Set<String> usedVariableName = new LinkedHashSet<>();

        if (sourcePattern != null) {
            for (Variable v : sourcePattern.getInputVariables()) {
                usedVariableName.add( v.getName() );
            }
        }

        RuleConditionElement source;
        if (accumulatePattern.isQuerySource()) {
            source = buildQueryPattern( ctx, (( QueryCallPattern ) accumulatePattern.getCondition()) );
        } else if (accumulatePattern.isCompositePatterns()) {
            CompositePatterns compositePatterns = (CompositePatterns) accumulatePattern.getCondition();
            GroupElement allSubConditions = new GroupElement(conditionToGroupElementType( compositePatterns.getType() ));
            for (Condition c : compositePatterns.getSubConditions()) {
                recursivelyAddConditions( ctx, group, allSubConditions, c);
            }
            source = allSubConditions.getChildren().size() == 1 ? allSubConditions.getChildren().get(0) : allSubConditions;
        } else {
            source = buildPattern( ctx, group, accumulatePattern );
        }

        Collection<Binding> bindings = new ArrayList<>();
        if (sourcePattern != null) {
            bindings.addAll( sourcePattern.getBindings() );
            bindings.add( new SelfPatternBiding<>( sourcePattern.getPatternVariable() ) );
        }

        pattern.setSource(buildAccumulate( ctx, accumulatePattern, source, pattern, usedVariableName, bindings ));

        return existingPattern ? null : pattern;
    }

    private Constraint getForallSelfJoin(Condition condition) {
        if (condition instanceof CompositePatterns && condition.getSubConditions().size() == 2 &&
                condition.getSubConditions().get(0) instanceof PatternImpl && condition.getSubConditions().get(1) instanceof PatternImpl) {
            PatternImpl joinPattern = (PatternImpl) condition.getSubConditions().get(1);
            for (Constraint constraint : joinPattern.getConstraint().getChildren()) {
                if (constraint instanceof AbstractSingleConstraint) {
                    Index index = (( AbstractSingleConstraint ) constraint).getIndex();
                    if (index != null && index.getConstraintType() == Index.ConstraintType.FORALL_SELF_JOIN) {
                        return constraint;
                    }
                }
            }
        }
        return null;
    }

    private void recursivelyAddConditions(RuleContext ctx, GroupElement group, GroupElement allSubConditions, Condition c) {
        if (c instanceof CompositePatterns) {
            c.getSubConditions().forEach(sc -> recursivelyAddConditions(ctx, group, allSubConditions, sc));
        } else if (c instanceof ExistentialPatternImpl) {
            if ( c.getType() == Condition.Type.FORALL ) {
                allSubConditions.addChild( buildForAll( ctx, allSubConditions, c ) );
            } else {
                GroupElement existGroupElement = new GroupElement( conditionToGroupElementType( c.getType() ) );
                allSubConditions.addChild( existGroupElement );
                recursivelyAddConditions( ctx, existGroupElement, existGroupElement, c.getSubConditions().iterator().next() );
            }
        } else if (c instanceof PatternImpl) {
            org.drools.model.Pattern pattern = (org.drools.model.Pattern<?>) c;
            RuleConditionElement rce = buildPattern( ctx, allSubConditions, pattern );
            if (ctx.getAccumulateSource( pattern.getPatternVariable() ) == null) {
                allSubConditions.addChild( rce );
            }
        } else if (c instanceof AccumulatePattern) {
            RuleConditionElement rce = buildAccumulate( ctx, group, (AccumulatePattern) c );
            if (rce != null) {
                allSubConditions.addChild( rce );
            }
        } else if (c instanceof EvalImpl) {
            allSubConditions.addChild( buildEval( ctx, ( EvalImpl ) c ) );
        }
    }

    private EvalCondition buildEval(RuleContext ctx, EvalImpl eval) {
        Declaration[] declarations = Stream.of( eval.getExpr().getVariables() ).map( ctx::getDeclaration ).toArray( Declaration[]::new );
        EvalExpression evalExpr = new LambdaEvalExpression(declarations, eval.getExpr());
        return new EvalCondition(evalExpr, declarations);
    }

    private ConditionalBranch buildConditionalConsequence(RuleContext ctx, ConditionalNamedConsequenceImpl consequence) {
        // This makes the assumption the variable is for a pattern (not field) binding, and this binding must already exist.
        // It's used for the implicit bindings

        // The main issue with all of this, is it doesn't allow access to other previous variables. And it should work with
        // with pattern bindings and field bindings. What if getVariables() returns an array.length > 1?
        EvalCondition evalCondition = null;
        if (consequence.getExpr() != null) {
            Pattern pattern = ctx.getPattern(consequence.getExpr().getVariables()[0]);

            if ( pattern.getDeclaration() != null) {
                // pattern has a root binding, so use that.
                EvalExpression eval = new LambdaEvalExpression(pattern, consequence.getExpr());
                evalCondition = new EvalCondition(eval, new Declaration[]{pattern.getDeclaration()});
            } else {
                // Pattern does not have root binding, so use field bindings.
                Declaration[] declarations = pattern.getDeclarations().values().toArray(new Declaration[pattern.getDeclarations().size()]);
                EvalExpression eval = new LambdaEvalExpression(declarations, consequence.getExpr());
                evalCondition = new EvalCondition(eval, declarations);
            }
        } else {
            evalCondition = new EvalCondition(LambdaEvalExpression.EMPTY, null);
        }
        return new ConditionalBranch( evalCondition,
                                      new NamedConsequence( consequence.getThenConsequence().getName(), consequence.getThenConsequence().isBreaking() ),
                                      consequence.getElseBranch() != null ? buildConditionalConsequence(ctx, consequence.getElseBranch()) : null );
    }

    private RuleConditionElement addSubConditions( RuleContext ctx, GroupElement ge, List<Condition> subconditions ) {
        for (int i = 0; i < subconditions.size(); i++) {
            RuleConditionElement element = conditionToElement( ctx, ge, subconditions.get(i) );
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
                                       0, // tupleIndex is 0 by default
                                       0, // patternIndex is 0 by default
                                       ClassObjectType.ObjectArray_ObjectType,
                                       null );

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
                    ArrayElementReader reader = new ArrayElementReader( new SelfReferenceClassFieldReader( Object[].class ),
                                                                        i,
                                                                        arg.getType() );
                    Declaration varDeclaration = pattern.addDeclaration( var.getName() );
                    varDeclaration.setReadAccessor( reader );
                    ctx.addDeclaration( var, varDeclaration );
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

    private RuleConditionElement buildPattern(RuleContext ctx, GroupElement group, org.drools.model.Pattern<?> modelPattern) {
        Variable patternVariable = modelPattern.getPatternVariable();

        Pattern pattern = addPatternForVariable( ctx, group, patternVariable, modelPattern.getType() );

        Arrays.stream( modelPattern.getWatchedProps() ).forEach( pattern::addWatchedProperty );
        pattern.setPassive( modelPattern.isPassive() );

        for (Binding binding : modelPattern.getBindings()) {
            // FIXME this is returning null for BindViewItem2, BindViewItem3 etc (mdp)
            Function1 f1 = getBindingFunction( ctx, patternVariable, binding );
            Declaration declaration = new Declaration(binding.getBoundVariable().getName(),
                                                      new LambdaReadAccessor(binding.getBoundVariable().getType(), f1),
                                                      pattern,
                                                      true);
            pattern.addDeclaration( declaration );
            if (binding.getReactOn() != null) {
                Arrays.stream( binding.getReactOn() ).forEach( pattern::addBoundProperty );
            }
            ctx.addDeclaration(binding.getBoundVariable(), declaration);
        }

        Declaration queryArgDecl = ctx.getQueryDeclaration( patternVariable );
        if (queryArgDecl != null) {
            pattern.addConstraint( new UnificationConstraint( queryArgDecl ) );
        }

        addConstraintsToPattern( ctx, pattern, modelPattern.getConstraint() );
        addReactiveMasksToPattern( pattern, modelPattern.getPatternClassMetadata() );
        return pattern;
    }

    private Function1 getBindingFunction( RuleContext ctx, Variable patternVariable, Binding binding ) {
        Declaration declr = ctx.getDeclaration( patternVariable );
        if ( !declr.isPatternDeclaration()) {
            // The direct pattern binding is a delegate, that already exists,
            // So first resolve that to pass to the next resolver.
            InternalReadAccessor reader = declr.getExtractor();
            return (o) -> binding.getBindingFunction().apply(reader.getValue(o));
        }
        return binding.getBindingFunction();
    }

    // this method sets the property reactive masks on the pattern and it's strictly necessary for native compilation
    // before changing this check DroolsTestIT in kogito-quarkus-integration-test-legacy module
    private void addReactiveMasksToPattern( Pattern pattern, DomainClassMetadata patternMetadata ) {
        if (pattern.getListenedProperties() != null && patternMetadata != null) {
            String[] listenedProperties = pattern.getListenedProperties().toArray( new String[pattern.getListenedProperties().size()] );
            pattern.setPositiveWatchMask( adaptBitMask( calculatePatternMask( patternMetadata, true, listenedProperties ) ) );
            pattern.setNegativeWatchMask( adaptBitMask( calculatePatternMask( patternMetadata, false, listenedProperties ) ) );
        }
    }

    private Accumulate buildAccumulate(RuleContext ctx, AccumulatePattern accPattern,
                                       RuleConditionElement source, Pattern pattern,
                                       Set<String> usedVariableName, Collection<Binding> bindings) {
        boolean isGroupBy = accPattern instanceof GroupByPattern;
        AccumulateFunction[] accFunctions = accPattern.getAccumulateFunctions();
        Declaration groupByDeclaration  = null;
        Class selfType = (isGroupBy || accFunctions.length > 1 ) ? Object[].class : accFunctions[0].getResult().getType();
        InternalReadAccessor selfReader = new SelfReferenceClassFieldReader( selfType );
        int arrayIndexOffset = 0;
        if (isGroupBy) {
            if (accFunctions.length == 0) {
                // In this situation the result is anonymous, but it still uses element position 0.
                // For this reason the i used to populate hte array index must be offset by 1.
                accFunctions = new AccumulateFunction[]{new AccumulateFunction( null, CountAccumulateFunction::new )};
                arrayIndexOffset = 1;
            }

            // GroupBy  key is always the last element in the result array
            Variable groupVar = (( GroupByPattern<?, ?> ) accPattern).getVarKey();
            groupByDeclaration  = new Declaration(groupVar.getName(),
                                                  new ArrayElementReader( selfReader, accFunctions.length, groupVar.getType() ),
                                                  pattern, true);
            pattern.addDeclaration( groupByDeclaration );
        }
        Accumulate accumulate;

        Accumulator[] accumulators = new Accumulator[accFunctions.length];
        List<Declaration> requiredDeclarationList = new ArrayList<>();
        for (int i = 0; i < accFunctions.length; i++) {
            Variable boundVar = processFunctions(ctx, accPattern, source, pattern, usedVariableName,
                             bindings,
                             isGroupBy, accFunctions[i],
                             selfReader, accumulators, requiredDeclarationList, arrayIndexOffset, i);
            if (isGroupBy) {
                ctx.addGroupByDeclaration(((GroupByPattern) accPattern).getVarKey(), groupByDeclaration);
            }
        }

        if (accFunctions.length == 1) {
            accumulate = new SingleAccumulate(source,
                                              requiredDeclarationList.toArray(new Declaration[requiredDeclarationList.size()]),
                                              accumulators[0]);
        } else {
            if (source instanceof Pattern) {
                requiredDeclarationList.forEach( (( Pattern ) source)::addDeclaration );
            }
            accumulate = new MultiAccumulate( source, new Declaration[0], accumulators,
                                              accumulators.length + ((isGroupBy) ? 1 : 0) ); // if this is a groupby +1 for the key
        }

        if (isGroupBy) {
            GroupByPatternImpl groupBy = ( GroupByPatternImpl ) accPattern;
            Declaration[] groupingDeclarations = new Declaration[groupBy.getVars().length];
            for (int i = 0; i < groupBy.getVars().length; i++) {
                groupingDeclarations[i] = ctx.getDeclaration( groupBy.getVars()[i] );
            }
            accumulate = new LambdaGroupByAccumulate(accumulate, groupingDeclarations, groupBy.getGroupingFunction());
        }

        for (Variable boundVar : accPattern.getBoundVariables()) {
            ctx.addAccumulateSource( boundVar, accumulate );
        }

        return accumulate;
    }

    private Variable processFunctions(RuleContext ctx, AccumulatePattern accPattern, RuleConditionElement source, Pattern pattern,
                                  Set<String> usedVariableName, Collection<Binding> bindings, boolean isGroupBy, AccumulateFunction accFunction,
                                  InternalReadAccessor selfReader, Accumulator[] accumulators,
                                  List<Declaration> requiredDeclarationList, int arrayIndexOffset, int i) {
        Binding binding = findBindingForAccumulate(bindings, accFunction);
        if (binding != null) {
            for (Variable var : binding.getInputVariables()) {
                usedVariableName.add( var.getName() );
            }
        }
        final BindingEvaluator bindingEvaluator = createBindingEvaluator(ctx, binding);
        final Accumulator      accumulator      = createAccumulator(usedVariableName, bindingEvaluator, accFunction);

        Variable boundVar = accPattern.getBoundVariables()[i];
        Declaration declaration;
        if (!isGroupBy && accumulators.length == 1) {
            declaration = new Declaration(boundVar.getName(), getReadAcessor( new ClassObjectType(boundVar.getType()) ),
                                        pattern, true);
        } else {
            // GroupBy or multi-accumulate always return an array
            // If GroupBy has no bound function, it uses an anonymous one.
            // The result is still in element 0 so must be offset using arrayIndexOffset
            declaration = new Declaration(boundVar.getName(), new ArrayElementReader(selfReader, i+arrayIndexOffset, boundVar.getType()),
                                          pattern, true);
        }
        pattern.addDeclaration( declaration );
        ctx.addDeclaration( boundVar, declaration );
        accumulators[i] = accumulator;

        Declaration[] requiredDeclarations = getRequiredDeclarationsForAccumulate(ctx, source, accFunction, binding, bindingEvaluator);
        requiredDeclarationList.addAll(Arrays.asList(requiredDeclarations));
        return boundVar;
    }

    private Binding findBindingForAccumulate( Collection<Binding> bindings, AccumulateFunction accFunction ) {
        return bindings.stream().filter( b -> b.getBoundVariable() == accFunction.getSource() ).findFirst().orElse( null );
    }

    private Declaration[] getRequiredDeclarationsForAccumulate( RuleContext ctx, RuleConditionElement source, AccumulateFunction accFunction, Binding binding, BindingEvaluator bindingEvaluator ) {
        Declaration[] requiredDeclarations = getRequiredDeclarationsForAccumulate( ctx, binding, accFunction );
        if (requiredDeclarations.length == 0 && source instanceof Pattern && bindingEvaluator != null && bindingEvaluator.getDeclarations() != null) {
            List<Declaration> previousDecl = new ArrayList<>();
            Pattern patternSource = ( Pattern ) source;
            patternSource.resetDeclarations();
            for (Declaration d : bindingEvaluator.getDeclarations()) {
                if (d.getIdentifier().equals( patternSource.getDeclaration().getIdentifier() )) {
                    patternSource.addDeclaration( d );
                } else {
                    previousDecl.add( d );
                }
            }
            requiredDeclarations = previousDecl.toArray( new Declaration[previousDecl.size()] );
        }
        return requiredDeclarations;
    }

    private Declaration[] getRequiredDeclarationsForAccumulate( RuleContext ctx, Binding binding, AccumulateFunction accFunction ) {
        if (binding != null || accFunction.getSource() == null) {
            if (accFunction.getExternalVars() != null) {
                Variable[] extVars = accFunction.getExternalVars();
                Declaration[] bindingDeclaration = new Declaration[extVars.length];
                for (int i = 0; i < extVars.length; i++) {
                    bindingDeclaration[i] = ctx.getDeclaration( extVars[i] );
                }
                return bindingDeclaration;
            } else {
                return new Declaration[0];
            }
        }

        if (accFunction.getSource() instanceof Variable) {
            Pattern pattern = ctx.getPattern((Variable) accFunction.getSource());
            return pattern == null || pattern.getDeclaration() == null ? new Declaration[0] : new Declaration[] { pattern.getDeclaration() };
        } else {
            return new Declaration[0];
        }
    }

    private BindingEvaluator createBindingEvaluator(RuleContext ctx, Binding binding) {
        if (binding == null) {
            return null;
        }
        Variable[] inputs = binding.getInputVariables();
        if (inputs.length == 1) {
            return new BindingInnerObjectEvaluator( binding );
        }
        Declaration[] declarations = new Declaration[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            declarations[i] = ctx.getDeclaration( inputs[i] );
        }
        return new BindingEvaluator( declarations, binding );
    }

    private Accumulator createAccumulator(Collection<String> usedVariableName, BindingEvaluator binding, AccumulateFunction accFunction) {
        final Object functionObject = accFunction.createFunctionObject();
        if (accFunction.isFixedValue()) {
            return new LambdaAccumulator.FixedValueAcc((org.kie.api.runtime.rule.AccumulateFunction) functionObject, (( Value ) accFunction.getSource()).getValue());
        }
        if (functionObject instanceof org.kie.api.runtime.rule.AccumulateFunction) {
            return createLambdaAccumulator(usedVariableName, binding, (org.kie.api.runtime.rule.AccumulateFunction) functionObject);
        }
        if (functionObject instanceof Accumulator) {
            return (Accumulator) functionObject;
        }
        throw new RuntimeException("Unknown functionClass" + functionObject.getClass().getCanonicalName());
    }

    private Accumulator createLambdaAccumulator(Collection<String> usedVariableName, BindingEvaluator binding, org.kie.api.runtime.rule.AccumulateFunction function) {
        if (binding == null) {
            return new LambdaAccumulator.NotBindingAcc(function);
        } else {
            return new LambdaAccumulator.BindingAcc(function, usedVariableName, binding);
        }
    }

    private Pattern addPatternForVariable( RuleContext ctx, GroupElement group, Variable patternVariable, Condition.Type type ) {
        Pattern pattern = null;

        // If the variable is already bound to the result of previous accumulate result pattern, then find it.
        if ( patternVariable instanceof org.drools.model.Declaration ) {
            org.drools.model.Declaration decl = (org.drools.model.Declaration) patternVariable;
            if ( decl.getSource() == null ) {
                Accumulate accSource = ctx.getAccumulateSource( patternVariable );
                if (accSource != null) {
                    for (RuleConditionElement element : group.getChildren()) {
                        if (element instanceof Pattern && (( Pattern ) element).getSource() == accSource) {
                            pattern = (Pattern) element;
                            break;
                        }
                    }
                }
            }
        }

        PatternSource priorSource = null;
        if ( pattern != null && type == Condition.Type.ACCUMULATE) {
            // variable was previous bound and now it's being used for the inner pattern of the next accumulate.
            // if it was a single accumulate, then rewrite to nest. This is to support an OptaPlanner use case.
            // I have only done this for single (mdp) because the current semantics involve a single binding,
            // so it would be potentially tricky if this was a multi var, with multiple bindings.
            if (pattern.getSource() instanceof SingleAccumulate ) {
                group.getChildren().remove(pattern);
                priorSource = pattern.getSource();
                pattern = null;
            }
        }

        if ( pattern == null) {
            pattern = new Pattern( ctx.getNextPatternIndex(),
                                 0, // tupleIndex will be set by ReteooBuilder
                                 0, // tupleIndex will be set by ReteooBuilder
                                 getObjectType( patternVariable ),
                                 patternVariable.getName(),
                                 true );
            pattern.setSource(priorSource);
        }

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
                    pattern.setSource( buildFrom( ctx, pattern, (From) decl.getSource() ) );
                } else if ( decl.getSource() instanceof UnitData ) {
                    UnitData unitData = (UnitData ) decl.getSource();
                    pattern.setSource( new EntryPointId( ctx.getRule().getRuleUnitClassName() + "." + unitData.getName() ) );
                } else {
                    throw new UnsupportedOperationException( "Unknown source: " + decl.getSource() );
                }
            }

            if ( decl.getWindow() != null ) {
                pattern.addBehavior( createWindow( decl.getWindow() ) );
                ctx.setNeedStreamMode();
            }

        } else if ( patternVariable instanceof Exchange ) {
            if ( type == Condition.Type.SENDER) {
                Function0 supplier = (( Exchange ) patternVariable).getMessageSupplier();
                DataProvider provider = new LambdaDataProvider( x -> supplier.apply(), false );
                pattern.setSource( new AsyncSend( pattern, patternVariable.getName(), provider ) );
            } else if ( type == Condition.Type.RECEIVER) {
                pattern.setSource( new AsyncReceive( pattern, patternVariable.getName() ) );
            } else {
                throw new UnsupportedOperationException();
            }
        }

        ctx.registerPattern( patternVariable, pattern );
        return pattern;
    }

    private org.drools.core.rule.From buildFrom( RuleContext ctx, Pattern pattern, From<?> from ) {
        DataProvider provider = createFromDataProvider( ctx, from );
        org.drools.core.rule.From fromSource = new org.drools.core.rule.From(provider);
        fromSource.setResultPattern( pattern );
        return fromSource;
    }

    private DataProvider createFromDataProvider( RuleContext ctx, From<?> from ) {
        if (from instanceof From0 ) {
            return new LambdaDataProvider( toFunctionN( (( From0 ) from).getProvider() ), from.isReactive() );
        }
        if (from instanceof From1) {
            return new LambdaDataProvider( toFunctionN( (( From1 ) from).getProvider() ), from.isReactive(), ctx.getDeclaration( from.getVariable() ) );
        }
        if (from instanceof From2 ) {
            return new LambdaDataProvider( toFunctionN( (( From2 ) from).getProvider() ), from.isReactive(), ctx.getDeclaration( from.getVariable() ), ctx.getDeclaration( (( From2 ) from).getVariable2() ) );
        }
        if (from instanceof From3 ) {
            return new LambdaDataProvider( toFunctionN( (( From3 ) from).getProvider() ), from.isReactive(), ctx.getDeclaration( from.getVariable() ), ctx.getDeclaration( (( From3 ) from).getVariable2() ), ctx.getDeclaration( (( From3 ) from).getVariable3() ) );
        }
        if (from instanceof From4) {
            return new LambdaDataProvider( toFunctionN( (( From4 ) from).getProvider() ), from.isReactive(), ctx.getDeclaration( from.getVariable() ), ctx.getDeclaration( (( From4 ) from).getVariable2() ), ctx.getDeclaration( (( From4 ) from).getVariable3() ), ctx.getDeclaration( (( From4 ) from).getVariable4() ) );
        }
        throw new UnsupportedOperationException( "Unknown from type " + from );
    }

    private <T> void createWindowReference( RuleContext ctx, WindowReference<T> window ) {
        WindowDeclaration windowDeclaration = new WindowDeclaration( window.getName(), ctx.getPkg().getName() );
        Variable<T> variable = declarationOf( window.getPatternType() );
        Pattern windowPattern = new Pattern(ctx.getNextPatternIndex(), getClassObjectType( window.getPatternType() ), variable.getName() );
        windowDeclaration.setPattern( windowPattern );

        if (window.getEntryPoint() != null) {
            windowPattern.setSource( new EntryPointId( window.getEntryPoint().getName() ) );
        }

        for ( Predicate1<T> predicate : window.getPredicates()) {
            SingleConstraint singleConstraint = new SingleConstraint1<>( generateName("expr"), variable, predicate );
            ConstraintEvaluator constraintEvaluator = new ConstraintEvaluator( windowPattern, singleConstraint );
            windowPattern.addConstraint( new LambdaConstraint( constraintEvaluator, singleConstraint.predicateInformation()) );
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

    private void addConstraintsToPattern( RuleContext ctx, Pattern pattern, Constraint constraint ) {
          if (constraint.getType() == Constraint.Type.MULTIPLE ) {
            for (Constraint child : constraint.getChildren()) {
                addConstraintsToPattern(ctx, pattern, child);
            }
            return;
        }

        createConstraint( ctx, pattern, constraint ).ifPresent( pattern::addConstraint );
    }

    private Optional<org.drools.core.spi.Constraint> createConstraint( RuleContext ctx, Pattern pattern, Constraint constraint ) {
        if (constraint.getType() == Constraint.Type.SINGLE) {
            SingleConstraint singleConstraint = (SingleConstraint) constraint;
            return singleConstraint.getVariables().length > 0 ? Optional.of( createSingleConstraint( ctx, pattern, singleConstraint ) ) : Optional.empty();
        } else {
            List<AbstractConstraint> constraints = constraint.getChildren().stream().map( child -> createConstraint( ctx, pattern, child ) )
                    .filter( Optional::isPresent ).map( Optional::get ).map( AbstractConstraint.class::cast ).collect( toList() );
            return Optional.of( new CombinedConstraint( constraint.getType(), constraints ) );
        }
    }

    private org.drools.core.spi.Constraint createSingleConstraint( RuleContext ctx, Pattern pattern, SingleConstraint singleConstraint ) {
        Variable[] vars = singleConstraint.getVariables();
        Declaration[] declarations = new Declaration[vars.length];
        Declaration unificationDeclaration = collectConstraintDeclarations( ctx, pattern, singleConstraint, vars, declarations );

        ConstraintEvaluator constraintEvaluator = singleConstraint.isTemporal() ?
                                                  new TemporalConstraintEvaluator( declarations, pattern, singleConstraint ) :
                                                  new ConstraintEvaluator( declarations, pattern, singleConstraint );
        return unificationDeclaration != null ?
                                         new UnificationConstraint(unificationDeclaration, constraintEvaluator) :
                                         new LambdaConstraint( constraintEvaluator, singleConstraint.predicateInformation());
    }

    private Declaration collectConstraintDeclarations( RuleContext ctx, Pattern pattern, SingleConstraint singleConstraint, Variable[] vars, Declaration[] declarations ) {
        Declaration unificationDeclaration = null;
        boolean isEqual = singleConstraint.getIndex() != null && singleConstraint.getIndex().getConstraintType() == Index.ConstraintType.EQUAL;
        for (int i = 0; i < vars.length; i++) {
            declarations[i] = ctx.getDeclaration( vars[i] );
            if ( isEqual && declarations[i].getPattern().getObjectType().equals( ClassObjectType.DroolsQuery_ObjectType ) ) {
                unificationDeclaration = declarations[i];
            } else if ( pattern.getSource() instanceof MultiAccumulate ) {
                Declaration accDeclaration = pattern.getDeclarations().get( declarations[i].getBindingName() );
                if (accDeclaration != null) {
                    declarations[i].setReadAccessor( accDeclaration.getExtractor() );
                }
            }
        }
        return unificationDeclaration;
    }

    Collection<InternalKnowledgePackage> getKiePackages() {
        return packages.values();
    }

    ObjectType getObjectType( Variable patternVariable ) {
        return patternVariable instanceof PrototypeVariable ?
                getPrototypeObjectType( (( PrototypeVariable ) patternVariable).getPrototype() ) :
                getClassObjectType( patternVariable.getType() );
    }

    private ObjectType getPrototypeObjectType( Prototype prototype ) {
        return objectTypeCache.computeIfAbsent( prototype.getFullName(), name -> {
            KnowledgePackageImpl pkg = (KnowledgePackageImpl) packages.computeIfAbsent( prototype.getPackage(), this::createKiePackage );
            return new FactTemplateObjectType(prototypeToFactTemplate( prototype, pkg ));
        } );
    }

    private ObjectType getClassObjectType( Class<?> patternClass ) {
        return objectTypeCache.computeIfAbsent( patternClass.getCanonicalName(), name -> {
            boolean isEvent = false;
            if (patternClass.getPackage() != null && !patternClass.isPrimitive() &&
                (!name.startsWith( "java.lang" ) || packages.containsKey( patternClass.getPackage().getName() ))) {
                KnowledgePackageImpl pkg = (KnowledgePackageImpl) packages.computeIfAbsent( patternClass.getPackage().getName(), this::createKiePackage );
                TypeDeclaration typeDeclaration = pkg.getTypeDeclaration( patternClass );
                if ( typeDeclaration == null ) {
                    typeDeclaration = createTypeDeclaration( patternClass, getPropertySpecificOption() );
                    pkg.addTypeDeclaration( typeDeclaration );
                }
                isEvent = typeDeclaration.getRole() == Role.Type.EVENT;
            }
            return new ClassObjectType( patternClass, isEvent );
        } );
    }

    private PropertySpecificOption getPropertySpecificOption() {
        return builderConf != null ? (( KnowledgeBuilderConfigurationImpl ) builderConf).getPropertySpecificOption() : PropertySpecificOption.ALWAYS;
    }

    private static GroupElement.Type conditionToGroupElementType( Condition.Type type ) {
        switch (type) {
            case AND: return GroupElement.Type.AND;
            case OR: return GroupElement.Type.OR;
            case EXISTS: return GroupElement.Type.EXISTS;
            case NOT: return GroupElement.Type.NOT;
        }
        throw new UnsupportedOperationException();
    }
}
