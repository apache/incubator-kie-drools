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
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.DroolsQuery;
import org.drools.core.base.EnabledBoolean;
import org.drools.core.base.SalienceInteger;
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
import org.drools.model.DynamicValueSupplier;
import org.drools.model.EntryPoint;
import org.drools.model.From;
import org.drools.model.From0;
import org.drools.model.From1;
import org.drools.model.From2;
import org.drools.model.From3;
import org.drools.model.Global;
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
import org.drools.model.functions.Predicate1;
import org.drools.model.functions.accumulate.AccumulateFunction;
import org.drools.model.impl.DeclarationImpl;
import org.drools.model.impl.Exchange;
import org.drools.model.patterns.CompositePatterns;
import org.drools.model.patterns.EvalImpl;
import org.drools.model.patterns.ExistentialPatternImpl;
import org.drools.model.patterns.PatternImpl;
import org.drools.model.patterns.QueryCallPattern;
import org.drools.modelcompiler.attributes.LambdaEnabled;
import org.drools.modelcompiler.attributes.LambdaSalience;
import org.drools.modelcompiler.consequence.LambdaConsequence;
import org.drools.modelcompiler.consequence.MVELConsequence;
import org.drools.modelcompiler.constraints.AbstractConstraint;
import org.drools.modelcompiler.constraints.BindingEvaluator;
import org.drools.modelcompiler.constraints.BindingInnerObjectEvaluator;
import org.drools.modelcompiler.constraints.CombinedConstraint;
import org.drools.modelcompiler.constraints.ConstraintEvaluator;
import org.drools.modelcompiler.constraints.LambdaAccumulator;
import org.drools.modelcompiler.constraints.LambdaConstraint;
import org.drools.modelcompiler.constraints.LambdaDataProvider;
import org.drools.modelcompiler.constraints.LambdaEvalExpression;
import org.drools.modelcompiler.constraints.LambdaReadAccessor;
import org.drools.modelcompiler.constraints.TemporalConstraintEvaluator;
import org.drools.modelcompiler.constraints.UnificationConstraint;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.definition.rule.All;
import org.kie.api.definition.rule.Direct;
import org.kie.api.definition.rule.Propagation;
import org.kie.api.definition.type.Role;
import org.kie.internal.ruleunit.RuleUnitUtil;

import static java.util.stream.Collectors.toList;

import static org.drools.compiler.rule.builder.RuleBuilder.buildTimer;
import static org.drools.core.rule.GroupElement.AND;
import static org.drools.core.rule.Pattern.getReadAcessor;
import static org.drools.model.FlowDSL.declarationOf;
import static org.drools.model.FlowDSL.entryPoint;
import static org.drools.model.functions.FunctionUtils.toFunctionN;
import static org.drools.model.impl.NamesGenerator.generateName;
import static org.drools.modelcompiler.facttemplate.FactFactory.prototypeToFactTemplate;
import static org.drools.modelcompiler.util.MvelUtil.createMvelObjectExpression;
import static org.drools.modelcompiler.util.TypeDeclarationUtil.createTypeDeclaration;

public class KiePackagesBuilder {

    private static final ObjectType JAVA_CLASS_OBJECT_TYPE = new ClassObjectType( Object.class );

    private final RuleBaseConfiguration configuration;

    private final Map<String, InternalKnowledgePackage> packages = new HashMap<>();

    private final Map<String, ObjectType> objectTypeCache = new HashMap<>();

    private final Collection<Model> models;

    public KiePackagesBuilder(KieBaseConfiguration conf) {
        this(conf, new ArrayList<>());
    }

    public KiePackagesBuilder(KieBaseConfiguration conf, Collection<Model> models) {
        this.configuration = ((RuleBaseConfiguration) conf);
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
                pkg.addTypeDeclaration( createTypeDeclaration(metaType ) );
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
                RuleImpl ruleImpl = compileRule( pkg, rule );
                ruleImpl.setLoadOrder( ruleCounter++ );
                pkg.addRule( ruleImpl );
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

    private RuleImpl compileRule( KnowledgePackageImpl pkg, Rule rule ) {
        RuleImpl ruleImpl = new RuleImpl( rule.getName() );
        ruleImpl.setPackage( pkg.getName() );
        ruleImpl.setPackage( rule.getPackage() );
        if (rule.getUnit() != null) {
            ruleImpl.setRuleUnitClassName( rule.getUnit() );
            pkg.getRuleUnitDescriptionLoader().getDescription(ruleImpl );
        }
        RuleContext ctx = new RuleContext( this, pkg, ruleImpl );
        populateLHS( ctx, pkg, rule.getView() );
        processConsequences( ctx, rule );
        if (ctx.needsStreamMode()) {
            pkg.setNeedStreamMode();
        }
        setRuleAttributes( rule, ruleImpl, ctx );
        setRuleMetaAttributes( rule, ruleImpl );
        return ruleImpl;
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
                if (Propagation.Type.IMMEDIATE.toString().equals( kv.getValue() )) {
                    ruleImpl.setDataDriven(true);
                } else if (Propagation.Type.EAGER.toString().equals( kv.getValue() )) {
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
        return buildTimer(ruleImpl, timerExpr, null, expr -> createMvelObjectExpression( expr, ctx.getClassLoader(), ctx.getDeclarations() ), null);
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
            int index = i;
            LambdaReadAccessor accessor = new LambdaReadAccessor(index, args[index].getType(), obj -> ( (DroolsQuery) obj ).getElements()[index] );
            declarations[i] = new Declaration( args[i].getName(), accessor, pattern, false );
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
                ctx.getRule().setConsequence( new LambdaConsequence( consequence ) );
            } else if ("mvel".equals(consequence.getLanguage())) {
                ctx.getRule().setConsequence( new MVELConsequence( consequence, ctx ) );
            } else {
                throw new UnsupportedOperationException("Unknown script language for consequence: " + consequence.getLanguage());
            }
        } else {
            ctx.getRule().addNamedConsequence( name, new LambdaConsequence( consequence ) );
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
            lhs.addChild( addPatternForVariable( ctx, lhs, getUnitVariable( ctx, pkg, view ), Condition.Type.PATTERN ) );
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
            case PATTERN: {
                return buildPattern( ctx, group, condition );
            }
            case EVAL: {
                return buildEval( ctx, ( EvalImpl ) condition );
            }
            case ACCUMULATE: {
                AccumulatePattern accumulatePattern = (AccumulatePattern) condition;
                Pattern pattern = null;
                if (accumulatePattern.getAccumulateFunctions().length == 1) {
                    pattern = ctx.getPattern( accumulatePattern.getAccumulateFunctions()[0].getResult() );
                }
                boolean existingPattern = pattern != null;
                if (!existingPattern) {
                    pattern = new Pattern( 0, JAVA_CLASS_OBJECT_TYPE );
                }

                PatternImpl sourcePattern = (PatternImpl) accumulatePattern.getPattern();
                List<String> usedVariableName = new ArrayList<>();
                Binding binding = null;

                if (sourcePattern != null) {
                    for (Variable v : sourcePattern.getInputVariables()) {
                        usedVariableName.add( v.getName() );
                    }

                    if ( !sourcePattern.getBindings().isEmpty() ) {
                        binding = ( Binding ) sourcePattern.getBindings().iterator().next();
                        usedVariableName.add( binding.getBoundVariable().getName() );
                    }
                }

                RuleConditionElement source;
                if(accumulatePattern.isCompositePatterns()) {
                    CompositePatterns compositePatterns = (CompositePatterns) accumulatePattern.getCondition();
                    GroupElement allSubConditions = new GroupElement(conditionToGroupElementType( compositePatterns.getType() ));
                    for(Condition c : compositePatterns.getSubConditions()) {
                        recursivelyAddConditions(ctx, group, allSubConditions, c);
                    }
                    source = allSubConditions;
                } else {
                    source = buildPattern(ctx, group, condition );
                }

                pattern.setSource(buildAccumulate(ctx, accumulatePattern, source, pattern, usedVariableName, binding) );

                for(Variable v : accumulatePattern.getBoundVariables()) {
                    if(source instanceof Pattern) {
                        ctx.registerPattern(v, (Pattern) source);
                    }
                }

                return existingPattern ? null : pattern;
            }
            case QUERY:
                return buildQueryPattern( ctx, ( (QueryCallPattern) condition ) );
            case NOT:
            case EXISTS: {
                // existential pattern can have only one subcondition
                return new GroupElement( conditionToGroupElementType( condition.getType() ) )
                        .addChild( conditionToElement( ctx, group, condition.getSubConditions().get(0) ) );
            }
            case FORALL: {
                Condition innerCondition = condition.getSubConditions().get(0);
                if (innerCondition instanceof PatternImpl) {
                    return new GroupElement( GroupElement.Type.NOT )
                            .addChild( conditionToElement( ctx, group, (( PatternImpl ) innerCondition).negate() ) );
                }

                Constraint selfJoinConstraint = getForallSelfJoin( innerCondition );
                if (selfJoinConstraint != null) {
                    PatternImpl forallPattern = (PatternImpl) innerCondition.getSubConditions().get(0);
                    PatternImpl joinPattern = (PatternImpl) innerCondition.getSubConditions().get(1);
                    joinPattern.getConstraint().getChildren().remove( selfJoinConstraint );
                    forallPattern.addConstraint( joinPattern.negate().getConstraint().replaceVariable(joinPattern.getPatternVariable(), forallPattern.getPatternVariable()) );
                    return new GroupElement( GroupElement.Type.NOT ).addChild( conditionToElement( ctx, group, forallPattern ) );
                }

                List<Pattern> remainingPatterns = new ArrayList<>();
                Pattern basePattern = ( Pattern ) conditionToElement( ctx, group, innerCondition.getSubConditions().get( 0 ) );
                for (int i = 1; i < innerCondition.getSubConditions().size(); i++) {
                    remainingPatterns.add( ( Pattern ) conditionToElement( ctx, group, innerCondition.getSubConditions().get( i ) ) );
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
            buildCompositePatterns(ctx, group, allSubConditions, c);
        } else if (c instanceof ExistentialPatternImpl) {
            buildExistentialPatternImpl(ctx, group, allSubConditions, c);
        } else if (c instanceof PatternImpl) {
            allSubConditions.addChild(buildPattern(ctx, group, c));
        }
    }

    private EvalCondition buildEval(RuleContext ctx, EvalImpl eval) {
        Declaration[] declarations = Stream.of( eval.getExpr().getVariables() ).map( ctx::getDeclaration ).toArray( Declaration[]::new );
        EvalExpression evalExpr = new LambdaEvalExpression(declarations, eval.getExpr());
        return new EvalCondition(evalExpr, declarations);
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
            RuleConditionElement element = conditionToElement( ctx, ge, subCondition );
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
                    ctx.addInnerDeclaration( var, varDeclaration );
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

    private Pattern buildPattern(RuleContext ctx, GroupElement group, Condition condition) {
        org.drools.model.Pattern<?> modelPattern = (org.drools.model.Pattern) condition;
        Pattern pattern = addPatternForVariable( ctx, group, modelPattern.getPatternVariable(), condition.getType() );

        for (Binding binding : modelPattern.getBindings()) {
            Declaration declaration = new Declaration(binding.getBoundVariable().getName(),
                                                      new LambdaReadAccessor(binding.getBoundVariable().getType(), binding.getBindingFunction()),
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

        addConstraintsToPattern( ctx, pattern, modelPattern.getConstraint() );
        addFieldsToPatternWatchlist( pattern, modelPattern.getWatchedProps() );
        return pattern;
    }

    private void buildExistentialPatternImpl( RuleContext ctx, GroupElement group, GroupElement allSubConditions, Condition condition ) {
        ExistentialPatternImpl existentialPattern = (ExistentialPatternImpl) condition;
        recursivelyAddConditions(ctx, group, allSubConditions, existentialPattern.getSubConditions().iterator().next());
    }

    private void buildCompositePatterns( RuleContext ctx, GroupElement group, GroupElement allSubConditions, Condition condition ) {
        CompositePatterns compositePatterns = (CompositePatterns) condition;
        compositePatterns.getSubConditions().forEach(sc ->  recursivelyAddConditions(ctx, group, allSubConditions, sc));
    }

    private Accumulate buildAccumulate(RuleContext ctx, AccumulatePattern accPattern,
                                       RuleConditionElement source, Pattern pattern,
                                       List<String> usedVariableName, Binding binding) {

        AccumulateFunction[] accFunctions = accPattern.getAccumulateFunctions();
        BindingEvaluator bindingEvaluator = createBindingEvaluator(ctx, binding);
        Accumulate accumulate;

        if (accFunctions.length == 1) {
            final AccumulateFunction accFunction = accFunctions[0];
            final Accumulator accumulator = createAccumulator(usedVariableName, bindingEvaluator, accFunction);
            final Variable boundVar = accPattern.getBoundVariables()[0];
            final Declaration declaration = new Declaration(boundVar.getName(),
                                                            getReadAcessor( JAVA_CLASS_OBJECT_TYPE ),
                                                            pattern,
                                                            true);
            pattern.addDeclaration(declaration);

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

            accumulate = new SingleAccumulate(source, requiredDeclarations, accumulator);

        } else {
            InternalReadAccessor reader = new SelfReferenceClassFieldReader( Object[].class );
            Accumulator[] accumulators = new Accumulator[accFunctions.length];
            for (int i = 0; i < accFunctions.length; i++) {
                final Accumulator accumulator = createAccumulator(usedVariableName, bindingEvaluator, accFunctions[i]);

                Variable boundVar = accPattern.getBoundVariables()[i];
                pattern.addDeclaration( new Declaration( boundVar.getName(),
                                        new ArrayElementReader( reader, i, boundVar.getType() ),
                                        pattern,
                                        true ) );

                accumulators[i] = accumulator;
            }

            accumulate = new MultiAccumulate( source, new Declaration[0], accumulators );
        }

        for (Variable boundVar : accPattern.getBoundVariables()) {
            ctx.addAccumulateSource( boundVar, accumulate );
        }

        return accumulate;
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

        if(accFunction.getSource() instanceof Variable) {
            Pattern pattern = ctx.getPattern((Variable) accFunction.getSource());
            return pattern == null ? new Declaration[0] : new Declaration[] { pattern.getDeclaration() };
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

    private Accumulator createAccumulator(List<String> usedVariableName, BindingEvaluator binding, AccumulateFunction accFunction) {
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

    private Accumulator createLambdaAccumulator(List<String> usedVariableName, BindingEvaluator binding, org.kie.api.runtime.rule.AccumulateFunction function) {
        if (binding == null) {
            return new LambdaAccumulator.NotBindingAcc(function, usedVariableName);
        } else {
            return new LambdaAccumulator.BindingAcc(function, usedVariableName, binding);
        }
    }

    private Pattern addPatternForVariable( RuleContext ctx, GroupElement group, Variable patternVariable, Condition.Type type ) {
        Pattern pattern = new Pattern( ctx.getNextPatternIndex(),
                                       0, // offset will be set by ReteooBuilder
                                       getObjectType( patternVariable ),
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
                    DataProvider provider = createFromDataProvider( ctx, from );
                    org.drools.core.rule.From fromSource = new org.drools.core.rule.From(provider);
                    fromSource.setResultPattern(pattern);
                    pattern.setSource(fromSource);
                } else if ( decl.getSource() instanceof UnitData ) {
                    UnitData unitData = (UnitData ) decl.getSource();
                    pattern.setSource( new EntryPointId( ctx.getRule().getRuleUnitClassName() + "." + unitData.getName() ) );
                } else {
                    throw new UnsupportedOperationException( "Unknown source: " + decl.getSource() );
                }
            } else {
                Accumulate accSource = ctx.getAccumulateSource( patternVariable );
                if (accSource != null) {
                    for (RuleConditionElement element : group.getChildren()) {
                        if (element instanceof Pattern && (( Pattern ) element).getSource() == accSource) {
                            if (accSource instanceof MultiAccumulate ) {
                                (( Pattern ) element).getConstraints().forEach( pattern::addConstraint );
                                (( Pattern ) element).getDeclarations().values().forEach( d -> {
                                    pattern.addDeclaration(d);
                                    d.setPattern( pattern );
                                } );
                            }
                            group.getChildren().remove( element );
                            break;
                        }
                    }
                    pattern.setSource( accSource );
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
        throw new UnsupportedOperationException( "Unknown from type " + from );
    }

    private <T> void createWindowReference( RuleContext ctx, WindowReference<T> window ) {
        WindowDeclaration windowDeclaration = new WindowDeclaration( window.getName(), ctx.getPkg().getName() );
        Variable<T> variable = declarationOf( window.getPatternType() );
        Pattern windowPattern = new Pattern(0, getClassObjectType( window.getPatternType() ), variable.getName() );
        windowDeclaration.setPattern( windowPattern );

        if (window.getEntryPoint() != null) {
            windowPattern.setSource( new EntryPointId( window.getEntryPoint().getName() ) );
        }

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
                                         new LambdaConstraint( constraintEvaluator );
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

    private void addFieldsToPatternWatchlist( Pattern pattern, String... fields ) {
        if (fields != null && fields.length > 0) {
            Collection<String> watchlist = pattern.getListenedProperties();
            if ( watchlist == null ) {
                watchlist = new HashSet<>( );
                pattern.setListenedProperties( watchlist );
            }
            watchlist.addAll( Arrays.asList( fields ) );
        }
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
            if ((!name.startsWith( "java.lang" ) || packages.containsKey( patternClass.getPackage().getName() )) && !patternClass.isPrimitive()) {
                KnowledgePackageImpl pkg = (KnowledgePackageImpl) packages.computeIfAbsent( patternClass.getPackage().getName(), this::createKiePackage );
                TypeDeclaration typeDeclaration = pkg.getTypeDeclaration( patternClass );
                if ( typeDeclaration == null ) {
                    typeDeclaration = createTypeDeclaration( patternClass );
                    pkg.addTypeDeclaration( typeDeclaration );
                }
                isEvent = typeDeclaration.getRole() == Role.Type.EVENT;
            }
            return new ClassObjectType( patternClass, isEvent );
        } );
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
