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
import org.drools.core.common.ProjectClassLoader;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.facttemplates.FactTemplateObjectType;
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
import org.drools.core.spi.ObjectType;
import org.drools.model.AccumulatePattern;
import org.drools.model.Argument;
import org.drools.model.Binding;
import org.drools.model.Condition;
import org.drools.model.Consequence;
import org.drools.model.Constraint;
import org.drools.model.EntryPoint;
import org.drools.model.From;
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
import org.drools.model.constraints.SingleConstraint1;
import org.drools.model.functions.Predicate1;
import org.drools.model.functions.accumulate.AccumulateFunction;
import org.drools.model.impl.DeclarationImpl;
import org.drools.model.patterns.CompositePatterns;
import org.drools.model.patterns.EvalImpl;
import org.drools.model.patterns.PatternImpl;
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
import org.kie.api.definition.rule.All;
import org.kie.api.definition.rule.Direct;
import org.kie.api.definition.rule.Propagation;
import org.kie.api.definition.type.Role;
import org.kie.internal.utils.ChainedProperties;
import org.kie.soup.project.datamodel.commons.types.ClassTypeResolver;
import org.kie.soup.project.datamodel.commons.types.TypeResolver;

import static org.drools.compiler.lang.descr.ForallDescr.BASE_IDENTIFIER;
import static org.drools.core.rule.GroupElement.AND;
import static org.drools.core.rule.Pattern.getReadAcessor;
import static org.drools.model.FlowDSL.declarationOf;
import static org.drools.model.FlowDSL.entryPoint;
import static org.drools.model.impl.NamesGenerator.generateName;
import static org.drools.modelcompiler.facttemplate.FactFactory.prototypeToFactTemplate;
import static org.drools.modelcompiler.util.TypeDeclarationUtil.createTypeDeclaration;

public class KiePackagesBuilder {

    private static final ObjectType JAVA_CLASS_OBJECT_TYPE = new ClassObjectType( Object.class );

    private final RuleBaseConfiguration configuration;

    private final Map<String, KiePackage> packages = new HashMap<>();

    private final Map<String, ObjectType> objectTypeCache = new HashMap<>();

    private final Collection<Model> models;
    private final ChainedProperties chainedProperties;

    public KiePackagesBuilder(KieBaseConfiguration conf, ProjectClassLoader moduleClassLoader) {
        this(conf, new ArrayList<>(), moduleClassLoader);
    }

    public KiePackagesBuilder(KieBaseConfiguration conf, Collection<Model> models, ProjectClassLoader moduleClassLoader) {
        this.configuration = ((RuleBaseConfiguration) conf);
        this.models = models;
        this.chainedProperties = ChainedProperties.getChainedProperties( moduleClassLoader.getTypesClassLoader() );
    }

    public void addModel( Model model ) {
        models.add(model);
    }

    public CanonicalKiePackages build() {
        for (Model model : models) {
            for (TypeMetaData metaType : model.getTypeMetaDatas()) {
                KnowledgePackageImpl pkg = ( KnowledgePackageImpl ) packages.computeIfAbsent( metaType.getPackage(), this::createKiePackage );
                pkg.addTypeDeclaration( createTypeDeclaration( pkg, metaType ) );
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

    private RuleImpl compileRule( KnowledgePackageImpl pkg, Rule rule ) {
        RuleImpl ruleImpl = new RuleImpl( rule.getName() );
        ruleImpl.setPackage( pkg.getName() );
        setRuleAttributes( rule, ruleImpl );
        setRuleMetaAttributes(rule, ruleImpl);
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
            int index = i;
            LambdaReadAccessor accessor = new LambdaReadAccessor(index, args[index].getType(), obj -> ( (DroolsQuery) obj ).getElements()[index] );
            declarations[i] = new Declaration( args[i].getName(), accessor, pattern, true );
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
            lhs.addChild( addPatternForVariable( ctx, lhs, getUnitVariable( ctx, pkg, view ) ) );
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
                    pattern = ctx.getPattern( accumulatePattern.getAccumulateFunctions()[0].getVariable() );
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
                    GroupElement ge = new GroupElement(conditionToGroupElementType( compositePatterns.getType() ));
                    for(Condition c : compositePatterns.getSubConditions()) {
                        ge.addChild(buildPattern(ctx, group, c));
                    }
                    source = ge;
                } else {
                    source = buildPattern(ctx, group, condition );
                }

                pattern.setSource(buildAccumulate(ctx, accumulatePattern, source, pattern, usedVariableName, binding) );
                return existingPattern ? null : pattern;
            }
            case QUERY:
                return buildQueryPattern( ctx, ( (QueryCallPattern) condition ) );
            case NOT:
            case EXISTS: {
                GroupElement ge = new GroupElement( conditionToGroupElementType( condition.getType() ) );
                // existential pattern can have only one subcondition
                ge.addChild( conditionToElement( ctx, group, condition.getSubConditions().get(0) ) );
                return ge;
            }
            case FORALL: {
                Condition innerCondition = condition.getSubConditions().get(0);
                Pattern basePattern;
                List<Pattern> remainingPatterns = new ArrayList<>();
                if (innerCondition instanceof PatternImpl) {
                    basePattern = new Pattern( ctx.getNextPatternIndex(),
                                               0, // offset will be set by ReteooBuilder
                                               getObjectType( (( PatternImpl ) innerCondition).getPatternVariable() ),
                                               BASE_IDENTIFIER,
                                               true );
                    remainingPatterns.add( (Pattern) conditionToElement( ctx, group, innerCondition ) );
                } else {
                    basePattern = ( Pattern ) conditionToElement( ctx, group, innerCondition.getSubConditions().get( 0 ) );
                    for (int i = 1; i < innerCondition.getSubConditions().size(); i++) {
                        remainingPatterns.add( ( Pattern ) conditionToElement( ctx, group, innerCondition.getSubConditions().get( i ) ) );
                    }
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
                                                                        arg.getType() );
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

    private Pattern buildPattern( RuleContext ctx, GroupElement group, Condition condition ) {
        org.drools.model.Pattern<?> modelPattern = (org.drools.model.Pattern) condition;
        Pattern pattern = addPatternForVariable( ctx, group, modelPattern.getPatternVariable() );

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

        addConstraintsToPattern( ctx, pattern, modelPattern, modelPattern.getConstraint() );
        addFieldsToPatternWatchlist( pattern, modelPattern.getWatchedProps() );
        return pattern;
    }

    private Accumulate buildAccumulate(RuleContext ctx, AccumulatePattern accPattern,
                                       RuleConditionElement source, Pattern pattern,
                                       List<String> usedVariableName, Binding binding) {

        AccumulateFunction[] accFunctions = accPattern.getAccumulateFunctions();
        Accumulate accumulate;

        if (accFunctions.length == 1) {
            final Class<?> functionClass = accFunctions[0].getFunctionClass();
            final Accumulator accumulator = createAccumulator(usedVariableName, binding, functionClass);
            final Variable boundVar = accPattern.getBoundVariables()[0];
            final Declaration declaration = new Declaration(boundVar.getName(),
                                                            getReadAcessor( JAVA_CLASS_OBJECT_TYPE ),
                                                            pattern,
                                                            true);
            pattern.addDeclaration(declaration);

            Declaration[] bindingDeclaration = binding != null ? new Declaration[0] : new Declaration[] { ctx.getPattern( accFunctions[0].getSource() ).getDeclaration() };
            accumulate = new SingleAccumulate(source, bindingDeclaration, accumulator);

        } else {
            InternalReadAccessor reader = new SelfReferenceClassFieldReader( Object[].class );
            Accumulator[] accumulators = new Accumulator[accFunctions.length];
            for (int i = 0; i < accFunctions.length; i++) {
                final Class<?> functionClass = accFunctions[i].getFunctionClass();
                final Accumulator accumulator = createAccumulator(usedVariableName, binding, functionClass);

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

    private Accumulator createAccumulator(List<String> usedVariableName, Binding binding, Class<?> functionClass) {
        if (org.kie.api.runtime.rule.AccumulateFunction.class.isAssignableFrom(functionClass)) {
            return createLambdaAccumulator(usedVariableName, binding, functionClass);
        } else if (Accumulator.class.isAssignableFrom(functionClass)) {
            return createLegacyAccumulator(functionClass);
        } else {
            throw new RuntimeException("Unknown functionClass" + functionClass);
        }
    }

    private Accumulator createLegacyAccumulator(Class<?> functionClass) {
        Accumulator accumulator;
        try {
            accumulator = (Accumulator) functionClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return accumulator;
    }

    private Accumulator createLambdaAccumulator(List<String> usedVariableName, Binding binding, Class<?> functionClass) {
        final org.kie.api.runtime.rule.AccumulateFunction accFunction1;
        try {
            accFunction1 = (org.kie.api.runtime.rule.AccumulateFunction) (functionClass).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if (binding == null) {
            return new LambdaAccumulator.NotBindingAcc(accFunction1, usedVariableName);
        } else {
            return new LambdaAccumulator.BindingAcc(accFunction1, usedVariableName, binding);
        }
    }

    private Pattern addPatternForVariable( RuleContext ctx, GroupElement group, Variable patternVariable ) {
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
                    DataProvider provider = new LambdaDataProvider( ctx.getDeclaration( from.getVariable() ), from.getProvider(), from.isReactive() );
                    org.drools.core.rule.From fromSource = new org.drools.core.rule.From(provider);
                    fromSource.setResultPattern( pattern );
                    pattern.setSource( fromSource );
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
            }
        }
        ctx.registerPattern( patternVariable, pattern );
        return pattern;
    }

    private <T> void createWindowReference( RuleContext ctx, WindowReference<T> window ) {
        WindowDeclaration windowDeclaration = new WindowDeclaration( window.getName(), ctx.getPkg().getName() );
        Variable<T> variable = declarationOf( window.getPatternType() );
        Pattern windowPattern = new Pattern(0, getClassObjectType( window.getPatternType() ), variable.getName() );
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
            boolean isEqual = singleConstraint.getIndex() != null && singleConstraint.getIndex().getConstraintType() == Index.ConstraintType.EQUAL;

            if (singleConstraint.getVariables().length > 0) {
                Variable[] vars = singleConstraint.getVariables();
                Declaration[] declarations = new Declaration[vars.length];
                Declaration unificationDeclaration = null;
                for (int i = 0; i < vars.length; i++) {
                    declarations[i] = ctx.getDeclaration( vars[i] );
                    if ( isEqual && declarations[i].getPattern().getObjectType().equals( ClassObjectType.DroolsQuery_ObjectType ) ) {
                        unificationDeclaration = declarations[i];
                    } else if ( pattern.getSource() instanceof MultiAccumulate) {
                        Declaration accDeclaration = pattern.getDeclarations().get( declarations[i].getBindingName() );
                        if (accDeclaration != null) {
                            declarations[i].setReadAccessor( accDeclaration.getExtractor() );
                        }
                    }
                }

                ConstraintEvaluator constraintEvaluator = singleConstraint.isTemporal() ?
                                                          new TemporalConstraintEvaluator( declarations, pattern, singleConstraint ) :
                                                          new ConstraintEvaluator( declarations, pattern, singleConstraint );
                org.drools.core.spi.Constraint droolsConstraint = unificationDeclaration != null ?
                                                                  new UnificationConstraint(unificationDeclaration, constraintEvaluator) :
                                                                  new LambdaConstraint( constraintEvaluator );
                pattern.addConstraint( droolsConstraint );
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
            watchlist.addAll( Arrays.asList( fields ) );
        }
    }

    Collection<KiePackage> getKiePackages() {
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
            if (!name.startsWith( "java.lang" ) && !patternClass.isPrimitive()) {
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
