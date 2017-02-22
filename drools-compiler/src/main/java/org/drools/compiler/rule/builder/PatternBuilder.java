/*
 * Copyright 2006 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.rule.builder;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.compiler.Dialect;
import org.drools.compiler.compiler.DrlExprParser;
import org.drools.compiler.compiler.DroolsErrorWrapper;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.compiler.DroolsWarningWrapper;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.MVELDumper;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.AtomicExprDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.BehaviorDescr;
import org.drools.compiler.lang.descr.BindingDescr;
import org.drools.compiler.lang.descr.ConnectiveType;
import org.drools.compiler.lang.descr.ConstraintConnectiveDescr;
import org.drools.compiler.lang.descr.ExprConstraintDescr;
import org.drools.compiler.lang.descr.ExpressionDescr;
import org.drools.compiler.lang.descr.FromDescr;
import org.drools.compiler.lang.descr.LiteralRestrictionDescr;
import org.drools.compiler.lang.descr.MVELExprDescr;
import org.drools.compiler.lang.descr.OperatorDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.PredicateDescr;
import org.drools.compiler.lang.descr.RelationalExprDescr;
import org.drools.compiler.lang.descr.ReturnValueRestrictionDescr;
import org.drools.compiler.rule.builder.XpathAnalysis.XpathPart;
import org.drools.compiler.rule.builder.dialect.DialectUtil;
import org.drools.compiler.rule.builder.dialect.java.JavaDialect;
import org.drools.compiler.rule.builder.dialect.mvel.MVELAnalysisResult;
import org.drools.compiler.rule.builder.dialect.mvel.MVELDialect;
import org.drools.core.base.ClassFieldReader;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.EvaluatorWrapper;
import org.drools.core.base.TypeResolver;
import org.drools.core.base.ValueType;
import org.drools.core.base.evaluators.EvaluatorDefinition.Target;
import org.drools.core.base.evaluators.IsAEvaluatorDefinition;
import org.drools.core.base.mvel.ActivationPropertyHandler;
import org.drools.core.base.mvel.MVELCompilationUnit;
import org.drools.core.base.mvel.MVELCompilationUnit.PropertyHandlerFactoryFixer;
import org.drools.core.base.mvel.MVELCompileable;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.factmodel.AnnotationDefinition;
import org.drools.core.factmodel.ClassDefinition;
import org.drools.core.factmodel.FieldDefinition;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.facttemplates.FactTemplate;
import org.drools.core.facttemplates.FactTemplateFieldExtractor;
import org.drools.core.facttemplates.FactTemplateObjectType;
import org.drools.core.reteoo.RuleTerminalNode.SortDeclarations;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.core.rule.Behavior;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.PatternSource;
import org.drools.core.rule.PredicateConstraint;
import org.drools.core.rule.QueryImpl;
import org.drools.core.rule.RuleConditionElement;
import org.drools.core.rule.SlidingLengthWindow;
import org.drools.core.rule.SlidingTimeWindow;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.rule.XpathBackReference;
import org.drools.core.rule.constraint.EvaluatorConstraint;
import org.drools.core.rule.constraint.MvelConstraint;
import org.drools.core.rule.constraint.NegConstraint;
import org.drools.core.rule.constraint.XpathConstraint;
import org.drools.core.spi.AcceptsClassObjectType;
import org.drools.core.spi.AcceptsReadAccessor;
import org.drools.core.spi.Constraint;
import org.drools.core.spi.Evaluator;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.ObjectType;
import org.drools.core.time.TimeUtils;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.MVELSafeHelper;
import org.drools.core.util.StringUtils;
import org.drools.core.util.index.IndexUtil;
import org.kie.api.definition.rule.Watch;
import org.kie.api.definition.type.Role;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.ResultSeverity;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import org.mvel2.integration.PropertyHandler;
import org.mvel2.integration.PropertyHandlerFactory;
import org.mvel2.util.PropertyTools;

import static org.drools.compiler.rule.builder.MVELConstraintBuilder.*;
import static org.drools.core.util.StringUtils.isIdentifier;

/**
 * A builder for patterns
 */
public class PatternBuilder
    implements
    RuleConditionBuilder<PatternDescr> {

    private static final java.util.regex.Pattern evalRegexp = java.util.regex.Pattern.compile( "^eval\\s*\\(",
                                                                                               java.util.regex.Pattern.MULTILINE );

    private static final java.util.regex.Pattern identifierRegexp = java.util.regex.Pattern.compile( "([\\p{L}_$][\\p{L}\\p{N}_$]*)" );

    private static final java.util.regex.Pattern getterRegexp = java.util.regex.Pattern.compile( "get([\\p{L}_][\\p{L}\\p{N}_]*)\\(\\s*\\)" );


    public PatternBuilder() {
    }

    public RuleConditionElement build( RuleBuildContext context,
                                       PatternDescr descr ) {
        boolean typeSafe = context.isTypesafe();
        try {
            return this.build( context,
                               descr,
                               null );
        } finally {
            context.setTypesafe( typeSafe );
        }
    }

    /**
     * Build a pattern for the given descriptor in the current
     * context and using the given utils object
     */
    public RuleConditionElement build( RuleBuildContext context,
                                       PatternDescr patternDescr,
                                       Pattern prefixPattern ) {
        if ( patternDescr.getObjectType() == null ) {
            lookupObjectType( context, patternDescr );
        }

        if ( patternDescr.getObjectType() == null || patternDescr.getObjectType().equals( "" ) ) {
            context.addError(new DescrBuildError(context.getParentDescr(),
                                                 patternDescr,
                                                 null,
                                                 "ObjectType not correctly defined"));
            return null;
        }

        ObjectType objectType = getObjectType(context, patternDescr);
        if ( objectType == null ) { // if the objectType doesn't exist it has to be query
            return buildQuery( context, patternDescr, patternDescr );
        }

        Pattern pattern = buildPattern( context, patternDescr, objectType );
        processClassObjectType( context, objectType, pattern );
        processAnnotations( context, patternDescr, pattern );
        processSource( context, patternDescr, pattern );
        processConstraintsAndBinds( context, patternDescr, pattern );
        processBehaviors( context, patternDescr, pattern );

        if ( !pattern.hasNegativeConstraint() && "on".equals( System.getProperty("drools.negatable") ) ) {
            // this is a non-negative pattern, so we must inject the constraint
            pattern.addConstraint( new NegConstraint(false) );
        }

        // poping the pattern
        context.getDeclarationResolver().popBuildStack();

        return pattern;
    }

    private void lookupObjectType( RuleBuildContext context, PatternDescr patternDescr ) {
        List<? extends BaseDescr> descrs = patternDescr.getConstraint().getDescrs();
        if (descrs.size() != 1 || !(descrs.get(0) instanceof ExprConstraintDescr)) {
            return;
        }

        ExprConstraintDescr descr = (ExprConstraintDescr)descrs.get(0);
        String expr = descr.getExpression();
        if (expr.charAt( 0 ) != '/') {
            return;
        }

        XpathAnalysis xpathAnalysis = XpathAnalysis.analyze(expr);
        if (xpathAnalysis.hasError()) {
            context.addError(new DescrBuildError(context.getParentDescr(),
                                                 descr,
                                                 null,
                                                 "Invalid xpath expression '" + expr + "': " + xpathAnalysis.getError()));
            return;
        }

        XpathPart firstXpathChunk = xpathAnalysis.getPart( 0 );
        String identifier = firstXpathChunk.getField();
        Declaration declr = context.getDeclarationResolver().getDeclaration( identifier );

        if (declr != null) {
            patternDescr.setXpathStartDeclaration( declr );
            patternDescr.setObjectType( declr.getExtractor().getExtractToClassName() );
            expr = patternDescr.getIdentifier() + ( patternDescr.isUnification() ? " := " : " : " ) + expr.substring( identifier.length() + 1 );
            descr.setExpression( expr );
        } else {
            patternDescr.setObjectType( findObjectType( context, firstXpathChunk, identifier ) );
            FromDescr fromDescr = new FromDescr();
            fromDescr.setDataSource( new MVELExprDescr( identifier ) );
            patternDescr.setSource( fromDescr );

            patternDescr.removeAllConstraint();
            firstXpathChunk.getConstraints()
                           .forEach( s -> patternDescr.addConstraint( new ExprConstraintDescr( s ) ) );
        }
    }

    private String findObjectType( RuleBuildContext context, XpathPart firstXpathChunk, String identifier ) {
        if ( firstXpathChunk.getInlineCast() != null ) {
            return firstXpathChunk.getInlineCast();
        }
        return context.getPkg().getRuleUnitRegistry()
                      .getRuleUnitFor( context.getRule() )
                      .flatMap( ruDescr -> ruDescr.getDatasourceType( identifier ) )
                      .map( Class::getCanonicalName )
                      .orElse( null );
    }

    private Pattern buildPattern( RuleBuildContext context, PatternDescr patternDescr, ObjectType objectType ) {
        String patternIdentifier = patternDescr.getIdentifier();
        boolean duplicateBindings = patternIdentifier != null && objectType instanceof ClassObjectType &&
                                    context.getDeclarationResolver().isDuplicated( context.getRule(),
                                                                                   patternIdentifier,
                                                                                   ((ClassObjectType) objectType).getClassName() );

        Pattern pattern;
        if ( !StringUtils.isEmpty( patternIdentifier ) && !duplicateBindings ) {

            pattern = new Pattern( context.getNextPatternId(),
                                   0, // offset is 0 by default
                                   objectType,
                                   patternIdentifier,
                                   patternDescr.isInternalFact(context) );
            if ( objectType instanceof ClassObjectType ) {
                // make sure PatternExtractor is wired up to correct ClassObjectType and set as a target for rewiring
                context.getPkg().getClassFieldAccessorStore().getClassObjectType( ((ClassObjectType) objectType),
                                                                                  (AcceptsClassObjectType) pattern.getDeclaration().getExtractor() );
            }
        } else {
            pattern = new Pattern( context.getNextPatternId(),
                                   0, // offset is 0 by default
                                   objectType,
                                   null );
        }
        pattern.setPassive(patternDescr.isPassive(context));

        if ( ClassObjectType.Match_ObjectType.isAssignableFrom( pattern.getObjectType() ) ) {
            PropertyHandler handler = PropertyHandlerFactory.getPropertyHandler( RuleTerminalNodeLeftTuple.class );
            if ( handler == null ) {
                PropertyHandlerFactoryFixer.getPropertyHandlerClass().put( RuleTerminalNodeLeftTuple.class,
                                                                           new ActivationPropertyHandler() );
            }
        }

        // adding the newly created pattern to the build stack this is necessary in case of local declaration usage
        context.getDeclarationResolver().pushOnBuildStack( pattern );

        if ( duplicateBindings ) {
            processDuplicateBindings( patternDescr.isUnification(), patternDescr, pattern, patternDescr, "this", patternDescr.getIdentifier(), context );
        }
        return pattern;
    }

    private void processClassObjectType( RuleBuildContext context, ObjectType objectType, Pattern pattern ) {
        if ( objectType instanceof ClassObjectType ) {
            // make sure the Pattern is wired up to correct ClassObjectType and set as a target for rewiring
            context.getPkg().getClassFieldAccessorStore().getClassObjectType( ((ClassObjectType) objectType), pattern );
            Class< ? > cls = ((ClassObjectType) objectType).getClassType();
            if ( cls.getPackage() != null && !cls.getPackage().getName().equals( "java.lang" ) ) {
                // register the class in its own package unless it is primitive or belongs to java.lang
                TypeDeclaration typeDeclr = context.getKnowledgeBuilder().getAndRegisterTypeDeclaration( cls, cls.getPackage().getName() );
                context.setTypesafe( typeDeclr == null || typeDeclr.isTypesafe() );
            } else {
                context.setTypesafe( true );
            }
        }
    }

    private ObjectType getObjectType(RuleBuildContext context, PatternDescr patternDescr) {
        return getObjectType(context, patternDescr, patternDescr.getObjectType());
    }

    private ObjectType getObjectType(RuleBuildContext context, PatternDescr patternDescr, String objectType) {
        final FactTemplate factTemplate = context.getPkg().getFactTemplate( objectType );
        if ( factTemplate != null ) {
            return new FactTemplateObjectType( factTemplate );
        } else {
            try {
                final Class<?> userProvidedClass = context.getDialect().getTypeResolver().resolveType( objectType );
                if ( !Modifier.isPublic(userProvidedClass.getModifiers()) ) {
                    context.addError(new DescrBuildError(context.getParentDescr(),
                                                         patternDescr,
                                                         null,
                                                         "The class '" + objectType + "' is not public"));
                    return null;
                }
                return new ClassObjectType( userProvidedClass, isEvent(context, userProvidedClass) );
            } catch ( final ClassNotFoundException e ) {
                // swallow as we'll do another check in a moment and then record the problem
            }
        }
        return null;
    }

    private boolean isEvent(RuleBuildContext context, Class<?> userProvidedClass) {
        TypeDeclaration typeDeclaration = getTypeDeclaration( context, userProvidedClass );

        if (typeDeclaration != null) {
            return typeDeclaration.getRole() == Role.Type.EVENT;
        }
        Role role = userProvidedClass.getAnnotation( Role.class );
        return role != null && role.value() == Role.Type.EVENT;
    }

    private TypeDeclaration getTypeDeclaration( RuleBuildContext context, Class<?> userProvidedClass ) {
        String packageName = ClassUtils.getPackage( userProvidedClass );
        KnowledgeBuilderImpl kbuilder = context.getKnowledgeBuilder();
        PackageRegistry pkgr = kbuilder.getPackageRegistry( packageName );
        TypeDeclaration typeDeclaration = pkgr != null ? pkgr.getPackage().getTypeDeclaration( userProvidedClass ) : null;
        if (typeDeclaration == null && kbuilder.getKnowledgeBase() != null) {
            // check if the type declaration is contained only in the already existing kbase (possible during incremental compilation)
            InternalKnowledgePackage pkg = kbuilder.getKnowledgeBase().getPackage( packageName );
            typeDeclaration = pkg != null ? pkg.getTypeDeclaration( userProvidedClass ) : null;
        }
        if (typeDeclaration == null) {
            typeDeclaration = context.getPkg().getTypeDeclaration( userProvidedClass );
        }
        return typeDeclaration;
    }

    private void processSource( RuleBuildContext context, PatternDescr patternDescr, Pattern pattern ) {
        if ( patternDescr.getSource() != null ) {
            // we have a pattern source, so build it
            RuleConditionBuilder builder = (RuleConditionBuilder) context.getDialect().getBuilder( patternDescr.getSource().getClass() );
            pattern.setSource( (PatternSource) builder.build( context, patternDescr.getSource(), pattern ) );
        }
    }

    private void processBehaviors( RuleBuildContext context, PatternDescr patternDescr, Pattern pattern ) {
        for ( BehaviorDescr behaviorDescr : patternDescr.getBehaviors() ) {
            if ( pattern.getObjectType().isEvent() ) {
                if ( Behavior.BehaviorType.TIME_WINDOW.matches( behaviorDescr.getSubType() ) ) {
                    SlidingTimeWindow window = new SlidingTimeWindow( TimeUtils.parseTimeString( behaviorDescr.getParameters().get( 0 ) ) );
                    pattern.addBehavior( window );
                } else if ( Behavior.BehaviorType.LENGTH_WINDOW.matches( behaviorDescr.getSubType() ) ) {
                    SlidingLengthWindow window = new SlidingLengthWindow( Integer.valueOf( behaviorDescr.getParameters().get( 0 ) ) );
                    pattern.addBehavior( window );
                }
                context.setNeedStreamMode();
            } else {
                // Some behaviors can only be assigned to patterns declared as events
                context.addError(new DescrBuildError(context.getParentDescr(),
                                                     patternDescr,
                                                     null,
                                                     "A Sliding Window can only be assigned to types declared with @role( event ). The type '" + pattern.getObjectType() + "' in '" + context.getRule().getName()
                                                     + "' is not declared as an Event."));
            }
        }
    }

    private RuleConditionElement buildQuery( RuleBuildContext context, PatternDescr descr, PatternDescr patternDescr ) {
        RuleConditionElement rce = null;
        // it might be a recursive query, so check for same names
        if ( context.getRule().getName().equals( patternDescr.getObjectType() ) ) {
            // it's a query so delegate to the QueryElementBuilder
            rce = buildQueryElement(context, descr, (QueryImpl) context.getRule());
        }

        if ( rce == null ) {
            // look up the query in the current package
            RuleImpl rule = context.getPkg().getRule( patternDescr.getObjectType() );
            if ( rule instanceof QueryImpl ) {
                // it's a query so delegate to the QueryElementBuilder
                rce = buildQueryElement(context, descr, (QueryImpl) rule);
            }
        }

        if ( rce == null ) {
            // the query may have been imported, so try package imports
            for ( String importName : context.getDialect().getTypeResolver().getImports() ) {
                importName = importName.trim();
                int pos = importName.indexOf( '*' );
                if ( pos >= 0 ) {
                    String pkgName = importName.substring( 0,
                                                           pos - 1 );
                    PackageRegistry pkgReg = context.getKnowledgeBuilder().getPackageRegistry( pkgName );
                    if ( pkgReg != null ) {
                        RuleImpl rule = pkgReg.getPackage().getRule( patternDescr.getObjectType() );
                        if ( rule instanceof QueryImpl) {
                            // it's a query so delegate to the QueryElementBuilder
                            rce = buildQueryElement(context, descr, (QueryImpl) rule);
                            break;
                        }
                    }
                }
            }
        }

        if ( rce == null ) {
            // this isn't a query either, so log an error
            context.addError(new DescrBuildError(context.getParentDescr(),
                                                 patternDescr,
                                                 null,
                                                 "Unable to resolve ObjectType '" + patternDescr.getObjectType() + "'"));
        }
        return rce;
    }

    private RuleConditionElement buildQueryElement(RuleBuildContext context, BaseDescr descr, QueryImpl rule) {
        if (context.getRule() != rule) {
            context.getRule().addUsedQuery(rule);
        }
        return QueryElementBuilder.getInstance().build( context, descr, rule );
    }

    protected void processDuplicateBindings( boolean isUnification,
                                             PatternDescr patternDescr,
                                             Pattern pattern,
                                             BaseDescr original,
                                             String leftExpression,
                                             String rightIdentifier,
                                             RuleBuildContext context ) {

        if ( isUnification ) {
            // rewrite existing bindings into == constraints, so it unifies
            build( context,
                   patternDescr,
                   pattern,
                   original,
                   leftExpression + " == " + rightIdentifier );
        } else {
            // This declaration already exists, so throw an Exception
            context.addError(new DescrBuildError(context.getParentDescr(),
                                                 patternDescr,
                                                 null,
                                                 "Duplicate declaration for variable '" + rightIdentifier + "' in the rule '" + context.getRule().getName() + "'"));
        }
    }

    protected void processAnnotations( final RuleBuildContext context,
                                       final PatternDescr patternDescr,
                                       final Pattern pattern ) {
        processListenedPropertiesAnnotation( context, patternDescr, pattern );
        processMetadataAnnotations( patternDescr, pattern, context.getDialect().getTypeResolver() );
    }

    protected void processMetadataAnnotations( PatternDescr patternDescr, Pattern pattern, TypeResolver typeResolver ) {
        for ( AnnotationDescr ann : patternDescr.getAnnotations() ) {
            String annFQN = ann.getFullyQualifiedName();
            if ( !Watch.class.getCanonicalName().equals(annFQN) ) {
                AnnotationDefinition def = buildAnnotationDef( ann, typeResolver );
                pattern.getAnnotations().put( annFQN, def );
            }
        }        
    }

    private AnnotationDefinition buildAnnotationDef( AnnotationDescr annotationDescr, TypeResolver resolver ) {
        try {
            return AnnotationDefinition.build( resolver.resolveType( annotationDescr.getFullyQualifiedName() ),
                                               annotationDescr.getValueMap(),
                                               resolver );
        } catch ( Exception e ) {
            e.printStackTrace();
            AnnotationDefinition annotationDefinition = new AnnotationDefinition( annotationDescr.getFullyQualifiedName() );
            for ( String propKey : annotationDescr.getValues().keySet() ) {
                Object value = annotationDescr.getValue( propKey );
                if ( value instanceof AnnotationDescr ) {
                    value = buildAnnotationDef( (AnnotationDescr) value, resolver );
                }
                annotationDefinition.getValues().put( propKey, new AnnotationDefinition.AnnotationPropertyVal( propKey, null, value, null ) );
            }
            return annotationDefinition;
        }
    }

    protected void processListenedPropertiesAnnotation(RuleBuildContext context, PatternDescr patternDescr, Pattern pattern) {
        String watchedValues = null;
        try {
            Watch watch = patternDescr.getTypedAnnotation(Watch.class);
            watchedValues = watch == null ? null : watch.value();
        } catch (Exception e) {
            context.addError( new DescrBuildError( context.getParentDescr(),
                                                   patternDescr,
                                                   null,
                                                   e.getMessage() ) );
        }

        if (watchedValues == null) {
            return;
        }

        List<String> settableProperties = getSettableProperties(context, patternDescr, pattern);

        List<String> listenedProperties = new ArrayList<String>();
        for (String propertyName : watchedValues.split(",")) {
            propertyName = propertyName.trim();
            if (propertyName.equals("*") || propertyName.equals("!*")) {
                if (listenedProperties.contains("*") || listenedProperties.contains("!*")) {
                    context.addError( new DescrBuildError( context.getParentDescr(),
                                                           patternDescr,
                                                           null,
                                                           "Duplicate usage of wildcard * in @" + Watch.class.getSimpleName() + " annotation" ) );
                } else {
                    listenedProperties.add(propertyName);
                }
                continue;
            }
            boolean isNegative = propertyName.startsWith("!");
            propertyName = isNegative ? propertyName.substring(1).trim() : propertyName;
            if (settableProperties != null && !settableProperties.contains(propertyName)) {
                context.addError( new DescrBuildError( context.getParentDescr(),
                                                       patternDescr,
                                                       null,
                                                       "Unknown property " + propertyName + " in @" + Watch.class.getSimpleName() + " annotation" ) );
            } else if (listenedProperties.contains(propertyName) || listenedProperties.contains("!" + propertyName)) {
                context.addError( new DescrBuildError( context.getParentDescr(),
                                                       patternDescr,
                                                       null,
                                                       "Duplicate property " + propertyName + " in @" + Watch.class.getSimpleName() + " annotation" ) );
            } else {
                listenedProperties.add(isNegative ? "!" + propertyName : propertyName);
            }
        }

        for ( Constraint constr : pattern.getConstraints() ) {
            if ( constr instanceof EvaluatorConstraint && ((EvaluatorConstraint) constr).isSelf() ) {
                EvaluatorConstraint ec = ((EvaluatorConstraint) constr );
                if ( ec.getEvaluator().getOperator() == IsAEvaluatorDefinition.ISA || ec.getEvaluator().getOperator() == IsAEvaluatorDefinition.NOT_ISA ) {
                    listenedProperties.add( TraitableBean.TRAITSET_FIELD_NAME );
                }
            }
        }
        pattern.setListenedProperties(listenedProperties);
    }

    protected List<String> getSettableProperties(RuleBuildContext context, PatternDescr patternDescr, Pattern pattern) {
        ObjectType patternType = pattern.getObjectType();
        if (!(patternType instanceof ClassObjectType)) {
            return null;
        }
        Class<?> patternClass = ((ClassObjectType)patternType).getClassType();
        TypeDeclaration typeDeclaration = getTypeDeclarationForPattern(context, pattern);
        if (!typeDeclaration.isPropertyReactive()) {
            context.addError( new DescrBuildError( context.getParentDescr(),
                                                   patternDescr,
                                                   null,
                                                   "Wrong usage of @" + Watch.class.getSimpleName() + " annotation on class " + patternClass.getName() + " that is not annotated as @PropertyReactive" ) );
        }
        typeDeclaration.setTypeClass(patternClass);
        return typeDeclaration.getAccessibleProperties();
    }

    private TypeDeclaration getTypeDeclarationForPattern(RuleBuildContext context, Pattern pattern) {
        ObjectType patternType = pattern.getObjectType();
        if (!(patternType instanceof ClassObjectType)) {
            return null;
        }
        Class<?> patternClass = ((ClassObjectType)patternType).getClassType();
        return context.getKnowledgeBuilder().getTypeDeclaration(patternClass);
    }

    /**
     * Process all constraints and bindings on this pattern
     */
    protected void processConstraintsAndBinds( final RuleBuildContext context,
                                             final PatternDescr patternDescr,
                                             final Pattern pattern ) {

        MVELDumper.MVELDumperContext mvelCtx = new MVELDumper.MVELDumperContext().setRuleContext(context);
        for ( BaseDescr b : patternDescr.getDescrs() ) {
            String expression;
            boolean isPositional = false;
            if ( b instanceof BindingDescr ) {
                BindingDescr bind = (BindingDescr) b;
                expression = bind.getVariable() + (bind.isUnification() ? " := " : " : ") + bind.getExpression();
            } else if ( b instanceof ExprConstraintDescr ) {
                ExprConstraintDescr descr = (ExprConstraintDescr) b;
                expression = descr.getExpression();
                isPositional = descr.getType() == ExprConstraintDescr.Type.POSITIONAL;
            } else {
                expression = b.getText();
            }

            ConstraintConnectiveDescr result = parseExpression( context,
                                                                patternDescr,
                                                                b,
                                                                expression );
            if ( result == null ) {
                return;
            }

            isPositional &= !( result.getDescrs().size() == 1 && result.getDescrs().get( 0 ) instanceof BindingDescr );

            if ( isPositional ) {
                processPositional(context,
                                  patternDescr,
                                  pattern,
                                  (ExprConstraintDescr) b);
            } else {
                // need to build the actual constraint
                List<Constraint> constraints = build( context, patternDescr, pattern, result, mvelCtx );
                pattern.addConstraints(constraints);
            }
        }

        combineConstraints(context, pattern, mvelCtx);
    }

    private void combineConstraints(RuleBuildContext context, Pattern pattern, MVELDumper.MVELDumperContext mvelCtx) {
        List<MvelConstraint> combinableConstraints = pattern.getCombinableConstraints();

        if (combinableConstraints == null || combinableConstraints.size() < 2) {
            return;
        }

        List<Declaration> declarations = new ArrayList<Declaration>();
        List<EvaluatorWrapper> operators = new ArrayList<EvaluatorWrapper>();
        Set<String> declarationNames = new HashSet<String>();

        boolean isFirst = true;
        Collection<String> packageNames = null;
        StringBuilder expressionBuilder = new StringBuilder(combinableConstraints.size() * 25);
        for (MvelConstraint constraint : combinableConstraints) {
            pattern.removeConstraint(constraint);
            if (isFirst) {
                packageNames = constraint.getPackageNames();
                isFirst = false;
            } else {
                expressionBuilder.append(" && ");
            }
            String constraintExpression = constraint.getExpression();
            boolean isComplex = constraintExpression.contains("&&") || constraintExpression.contains("||");
            if (isComplex) {
                expressionBuilder.append("( ");
            }
            expressionBuilder.append(constraintExpression);
            if (isComplex) {
                expressionBuilder.append(" )");
            }
            for (Declaration declaration : constraint.getRequiredDeclarations()) {
                if (declarationNames.add(declaration.getBindingName())) {
                    declarations.add(declaration);
                }
            }
            Collections.addAll( operators, constraint.getOperators() );
        }

        String expression = expressionBuilder.toString();
        MVELCompilationUnit compilationUnit = getConstraintBuilder( context ).buildCompilationUnit(context, pattern, expression, mvelCtx.getAliases());

        Constraint combinedConstraint = getConstraintBuilder( context ).buildMvelConstraint( packageNames,
                                                                                             expression,
                                                                                             declarations.toArray(new Declaration[declarations.size()]),
                                                                                             operators.toArray(new EvaluatorWrapper[operators.size()]),
                                                                                             compilationUnit,
                                                                                             IndexUtil.ConstraintType.UNKNOWN,
                                                                                             null, null, false );
        pattern.addConstraint(combinedConstraint);
    }

    protected void processPositional( final RuleBuildContext context,
                                    final PatternDescr patternDescr,
                                    final Pattern pattern,
                                    final ExprConstraintDescr descr ) {
        if ( descr.getType() == ExprConstraintDescr.Type.POSITIONAL && pattern.getObjectType() instanceof ClassObjectType ) {
            Class< ? > klazz = ((ClassObjectType) pattern.getObjectType()).getClassType();
            TypeDeclaration tDecl = context.getKnowledgeBuilder().getTypeDeclaration( klazz );

            if ( tDecl == null ) {
                context.addError(new DescrBuildError(context.getParentDescr(),
                                                     descr,
                                                     klazz,
                                                     "Unable to find @positional definitions for :" + klazz + "\n"));
                return;
            }

            ClassDefinition clsDef = tDecl.getTypeClassDef();
            if ( clsDef == null ) {
                context.addError(new DescrBuildError(context.getParentDescr(),
                                                     descr,
                                                     null,
                                                     "Unable to find @positional field " + descr.getPosition() + " for class " + tDecl.getTypeName() + "\n"));
                return;
            }

            FieldDefinition field = clsDef.getField( descr.getPosition() );
            if ( field == null ) {
                context.addError(new DescrBuildError(context.getParentDescr(),
                                                     descr,
                                                     null,
                                                     "Unable to find @positional field " + descr.getPosition() + " for class " + tDecl.getTypeName() + "\n"));
                return;
            }

            String expr = descr.getExpression();
            boolean isSimpleIdentifier = isIdentifier(expr);

            if ( isSimpleIdentifier ) {
                // create a binding
                BindingDescr binder = new BindingDescr();
                binder.setUnification( true );
                binder.setExpression( field.getName() );
                binder.setVariable( descr.getExpression() );
                buildRuleBindings( context, patternDescr, pattern, binder );
            } else {
                // create a constraint
                build( context, patternDescr, pattern, descr, field.getName() + " == " + descr.getExpression() );
            }
        }
    }

    protected void build( final RuleBuildContext context,
                          final PatternDescr patternDescr,
                          final Pattern pattern,
                          final BaseDescr original,
                          final String expr ) {

        ConstraintConnectiveDescr result = parseExpression(context, patternDescr, original, expr);
        if ( result == null ) {
            return;
        }

        result.copyLocation( original );
        MVELDumper.MVELDumperContext mvelCtx = new MVELDumper.MVELDumperContext().setRuleContext(context);
        List<Constraint> constraints = build( context, patternDescr, pattern, result, mvelCtx);
        pattern.addConstraints(constraints);
    }

    protected List<Constraint> build( RuleBuildContext context,
                                      PatternDescr patternDescr,
                                      Pattern pattern,
                                      ConstraintConnectiveDescr descr,
                                      MVELDumper.MVELDumperContext mvelCtx ) {

        List<Constraint> constraints = new ArrayList<Constraint>();

        List<BaseDescr> initialDescrs = new ArrayList<BaseDescr>( descr.getDescrs() );
        for ( BaseDescr d : initialDescrs ) {
            boolean isXPath = isXPathDescr(d);
            if (isXPath && pattern.hasXPath()) {
                context.addError( new DescrBuildError( context.getParentDescr(),
                                                       descr,
                                                       null,
                                                       "More than a single oopath constraint is not allowed in the same pattern" ) );
                return constraints;
            }
            Constraint constraint = isXPath ?
                                    buildXPathDescr(context, patternDescr, pattern, d, mvelCtx) :
                                    buildCcdDescr(context, patternDescr, pattern, d, descr, mvelCtx);
            if (constraint != null) {
                Declaration declCorrXpath = getDeclarationCorrespondingToXpath( pattern, isXPath, constraint );
                if (declCorrXpath == null) {
                    constraints.add(constraint);
                } else {
                    // A constraint is using a declration bound to an xpath in the same pattern
                    // Move the constraint inside the last chunk of the xpath defining this declaration, rewriting it as 'this'
                    constraint = buildCcdDescr(context, patternDescr, pattern,
                                               d.replaceVariable(declCorrXpath.getBindingName(), "this"), descr, mvelCtx);
                    if (constraint != null) {
                        pattern.getXpathConstraint().getChunks().getLast().addConstraint( constraint );
                    }
                }
            }
        }

        if ( descr.getDescrs().size() > initialDescrs.size() ) {
            // The initial build process may have generated other constraint descrs.
            // This happens when null-safe references or inline-casts are used
            // These additional constraints must be built, and added as
            List<BaseDescr> additionalDescrs = new ArrayList<BaseDescr>( descr.getDescrs() );
            additionalDescrs.removeAll( initialDescrs );

            if ( ! additionalDescrs.isEmpty() ) {
                List<Constraint> additionalConstraints = new ArrayList<Constraint>();
                for ( BaseDescr d : additionalDescrs ) {
                    Constraint constraint = buildCcdDescr(context, patternDescr, pattern, d, descr, mvelCtx);
                    if (constraint != null) {
                        additionalConstraints.add(constraint);
                    }
                }
                constraints.addAll(0, additionalConstraints);
            }
        }

        return constraints;
    }

    private Declaration getDeclarationCorrespondingToXpath( Pattern pattern, boolean isXPath, Constraint constraint ) {
        if (!isXPath && pattern.hasXPath()) {
            Declaration xPathDecl = pattern.getXPathDeclaration();
            if (xPathDecl != null) {
                for ( Declaration decl : constraint.getRequiredDeclarations() ) {
                    if (xPathDecl.equals( decl )) {
                        return decl;
                    }
                }
            }
        }
        return null;
    }

    private boolean isXPathDescr(BaseDescr descr) {
        return descr instanceof ExpressionDescr &&
               ( ((ExpressionDescr)descr).getExpression().startsWith("/") ||
                 ((ExpressionDescr)descr).getExpression().startsWith("?/") );
    }

    private Constraint buildXPathDescr( RuleBuildContext context,
                                        PatternDescr patternDescr,
                                        Pattern pattern,
                                        BaseDescr descr,
                                        MVELDumper.MVELDumperContext mvelCtx ) {

        String expression = ((ExpressionDescr)descr).getExpression();
        XpathAnalysis xpathAnalysis = XpathAnalysis.analyze(expression);

        if (xpathAnalysis.hasError()) {
            context.addError(new DescrBuildError(context.getParentDescr(),
                                                 descr,
                                                 null,
                                                 "Invalid xpath expression '" + expression + "': " + xpathAnalysis.getError()));
            return null;
        }

        XpathConstraint xpathConstraint = new XpathConstraint();
        ObjectType objectType = pattern.getObjectType();
        Class<?> patternClass = ((ClassObjectType) objectType).getClassType();

        List<Class<?>> backReferenceClasses = new ArrayList<Class<?>>();
        backReferenceClasses.add(patternClass);
        XpathBackReference backRef = new XpathBackReference( pattern, backReferenceClasses );
        pattern.setBackRefDeclarations( backRef );

        ObjectType originalType = pattern.getObjectType();
        ObjectType currentObjectType = originalType;
        mvelCtx.setInXpath( true );

        try {
            for ( XpathAnalysis.XpathPart part : xpathAnalysis ) {
                XpathConstraint.XpathChunk xpathChunk = xpathConstraint.addChunck( patternClass, part.getField(), part.getIndex(), part.isIterate(), part.isLazy() );

                // make sure the Pattern is wired up to correct ClassObjectType and set as a target for rewiring
                context.getPkg().getClassFieldAccessorStore().getClassObjectType( ((ClassObjectType) currentObjectType), xpathChunk );

                if ( xpathChunk == null ) {
                    context.addError( new DescrBuildError( context.getParentDescr(),
                                                           descr,
                                                           null,
                                                           "Invalid xpath expression '" + expression + "': cannot access " + part.getField() + " on " + patternClass ) );
                    pattern.setObjectType( originalType );
                    return null;
                }

                if ( part.getInlineCast() != null ) {
                    try {
                        patternClass = context.getDialect().getTypeResolver().resolveType( part.getInlineCast() );
                    } catch (ClassNotFoundException e) {
                        context.addError( new DescrBuildError( context.getParentDescr(),
                                                               descr,
                                                               null,
                                                               "Unknown class " + part.getInlineCast() + " in xpath expression '" + expression + "'" ) );
                        return null;
                    }
                    part.addInlineCastConstraint( patternClass );
                    currentObjectType = getObjectType(context, patternDescr, patternClass.getName());
                    xpathChunk.setReturnedType( (ClassObjectType) currentObjectType );
                } else {
                    patternClass = xpathChunk.getReturnedClass();
                    currentObjectType = getObjectType(context, patternDescr, patternClass.getName());
                }

                pattern.setObjectType( currentObjectType );
                backReferenceClasses.add( 0, patternClass );
                backRef.reset();

                for ( String constraint : part.getConstraints() ) {
                    ConstraintConnectiveDescr result = parseExpression( context, patternDescr, new ExprConstraintDescr( constraint ), constraint );
                    if ( result == null ) {
                        continue;
                    }

                    for ( Constraint c : build( context, patternDescr, pattern, result, mvelCtx ) ) {
                        xpathChunk.addConstraint( c );
                    }
                }
            }
        } finally {
            mvelCtx.setInXpath( false );
            pattern.setBackRefDeclarations( null );
            pattern.setObjectType( originalType );
        }

        xpathConstraint.setXpathStartDeclaration( patternDescr.getXpathStartDeclaration() );
        if (descr instanceof BindingDescr) {
            xpathConstraint.setDeclaration( pattern.addDeclaration( ((BindingDescr)descr).getVariable() ) );
        }

        return xpathConstraint;
    }

    protected Constraint buildCcdDescr( RuleBuildContext context,
                                        PatternDescr patternDescr,
                                        Pattern pattern,
                                        BaseDescr d,
                                        ConstraintConnectiveDescr ccd,
                                        MVELDumper.MVELDumperContext mvelCtx) {
        d.copyLocation( patternDescr );

        mvelCtx.clear();
        String expr = context.getCompilerFactory().getExpressionProcessor().dump( d, ccd, mvelCtx );
        Map<String, OperatorDescr> aliases = mvelCtx.getAliases();

        // create bindings
        for ( BindingDescr bind : mvelCtx.getBindings() ) {
            buildRuleBindings( context, patternDescr, pattern, bind );
        }

        if ( expr.length() == 0 ) {
            return null;
        }

        // check if it is an atomic expression
        Constraint constraint = processAtomicExpression( context, pattern, d, expr, aliases );
        // otherwise check if it is a simple expression
        return constraint != null ? constraint : buildExpression(context, pattern, d, expr, aliases);
    }


    protected Constraint buildExpression( final RuleBuildContext context,
                                  final Pattern pattern,
                                  final BaseDescr d,
                                  final String expr,
                                  final Map<String, OperatorDescr> aliases ) {
        if ( "_.neg".equals ( expr) ) {
            pattern.setHasNegativeConstraint( true );
            return new NegConstraint();
        } else if ( "!_.neg".equals ( expr) ) {
            pattern.setHasNegativeConstraint( true );
            return new NegConstraint(false);
        }

        RelationalExprDescr relDescr = d instanceof RelationalExprDescr ? (RelationalExprDescr) d : null;
        boolean simple = isSimpleExpr( relDescr );

        if ( simple && // simple means also relDescr is != null
                !ClassObjectType.Map_ObjectType.isAssignableFrom( pattern.getObjectType() ) &&
                !ClassObjectType.Match_ObjectType.isAssignableFrom( pattern.getObjectType() ) ) {
            String normalizedExpr = normalizeExpression( context, pattern, relDescr, expr );
            return buildRelationalExpression(context, pattern, relDescr, normalizedExpr, aliases);
        }

        // Either it's a complex expression, so do as predicate
        // Or it's a Map and we have to treat it as a special case
        return createAndBuildPredicate( context, pattern, d, rewriteOrExpressions(context, pattern, d, expr), aliases );
    }

    private String rewriteOrExpressions(RuleBuildContext context, Pattern pattern, BaseDescr d, String expr) {
        if ( d instanceof ConstraintConnectiveDescr && ( (ConstraintConnectiveDescr) d ).getConnective() == ConnectiveType.OR ) {
            String rewrittenExpr = rewriteCompositeExpressions( context, pattern, (ConstraintConnectiveDescr)d );
            if (rewrittenExpr != null) {
                return rewrittenExpr;
            }
        }
        return expr;
    }

    private String rewriteCompositeExpressions(RuleBuildContext context, Pattern pattern, ConstraintConnectiveDescr d) {
        int i = 0;
        StringBuilder sb = new StringBuilder();
        for (BaseDescr subDescr : d.getDescrs()) {
            if (subDescr instanceof BindingDescr) {
                continue;
            }
            if (i++ > 0) {
                sb.append( " " ).append( d.getConnective().getConnective() ).append( " " );
            }

            String normalizedExpr;
            if (subDescr instanceof RelationalExprDescr && isSimpleExpr( (RelationalExprDescr)subDescr )) {
                RelationalExprDescr relDescr = (RelationalExprDescr) subDescr;
                if (relDescr.getExpression() != null) {
                    normalizedExpr = normalizeExpression( context, pattern, relDescr, relDescr.getExpression() );
                } else {
                    i--;
                    normalizedExpr = "";
                }
            } else if (subDescr instanceof ConstraintConnectiveDescr) {
                String rewrittenExpr = rewriteCompositeExpressions(context, pattern, (ConstraintConnectiveDescr)subDescr);
                if (rewrittenExpr == null) {
                    return null;
                }
                normalizedExpr = "(" + rewrittenExpr + ")";
            } else if (subDescr instanceof AtomicExprDescr) {
                normalizedExpr = ( (AtomicExprDescr) subDescr ).getRewrittenExpression();
            } else {
                return null;
            }
            sb.append(normalizedExpr);
        }

        return sb.toString();
    }

    private String normalizeExpression( RuleBuildContext context, Pattern pattern, RelationalExprDescr subDescr, String subExpr ) {
        String leftValue = findLeftExpressionValue(subDescr);
        String operator = subDescr.getOperator();

        if ( isDateType( context, pattern, leftValue ) ) {
            String rightValue = findRightExpressionValue( subDescr );
            FieldValue fieldValue = getFieldValue(context, ValueType.DATE_TYPE, rightValue);
            return fieldValue != null ? normalizeDate( fieldValue, leftValue, operator ) : subExpr;
        }

        if (operator.equals( "str" )) {
            String rightValue = findRightExpressionValue( subDescr );
            return normalizeStringOperator( leftValue, rightValue, new LiteralRestrictionDescr( operator,
                                                                                                subDescr.isNegated(),
                                                                                                subDescr.getParameters(),
                                                                                                rightValue,
                                                                                                LiteralRestrictionDescr.TYPE_STRING ) );
        }

        // resolve ambiguity between mvel's "empty" keyword and constraints like: List(empty == ...)
        return normalizeEmptyKeyword( subExpr, operator );
    }

    private boolean isDateType( RuleBuildContext context, Pattern pattern, String leftValue ) {
        Declaration declaration = pattern.getDeclarations().get( leftValue );
        if (declaration != null && declaration.getExtractor() != null) {
            return declaration.getValueType() == ValueType.DATE_TYPE;
        }

        if (pattern.getObjectType() instanceof FactTemplateObjectType) {
            return ( (FactTemplateObjectType) pattern.getObjectType() ).getFactTemplate().getFieldTemplate( leftValue ).getValueType() == ValueType.DATE_TYPE;
        }

        Class<?> clazz = ((ClassObjectType) pattern.getObjectType()).getClassType();
        Class<?> fieldType = context.getPkg().getClassFieldAccessorStore().getFieldType(clazz, leftValue);
        return fieldType != null && ValueType.isDateType( fieldType );
    }

    protected Constraint buildRelationalExpression( final RuleBuildContext context,
                                                    final Pattern pattern,
                                                    final RelationalExprDescr relDescr,
                                                    final String expr,
                                                    final Map<String, OperatorDescr> aliases ) {
        String[] values = new String[2];
        boolean usesThisRef = findExpressionValues(relDescr, values);

        ExprBindings value1Expr = getExprBindings(context, pattern, values[0]);
        ExprBindings value2Expr = getExprBindings(context, pattern, values[1]);

        // build a predicate if it is a constant expression or at least has a constant on the left side
        // or as a fallback when the building of a constraint fails
        if (!usesThisRef && value1Expr.isConstant()) {
            return createAndBuildPredicate( context, pattern, relDescr, expr, aliases );
        }

        Constraint constraint = buildConstraintForPattern( context, pattern, relDescr, expr, values[0], values[1], value2Expr.isConstant(), aliases );
        return constraint != null ? constraint : createAndBuildPredicate( context, pattern, relDescr, expr, aliases );
    }

    private ExprBindings getExprBindings(RuleBuildContext context, Pattern pattern, String value) {
        ExprBindings value1Expr = new ExprBindings();
        setInputs( context,
                   value1Expr,
                   (pattern.getObjectType() instanceof ClassObjectType) ? ((ClassObjectType) pattern.getObjectType()).getClassType() : FactTemplate.class,
                   value);
        return value1Expr;
    }

    private boolean findExpressionValues(RelationalExprDescr relDescr, String[] values) {
        values[0] = findLeftExpressionValue( relDescr );
        values[1] = findRightExpressionValue(relDescr);

        return "this".equals( values[1] ) || values[1].startsWith("this.") || values[1].contains( ")this)." ) ||
               "this".equals( values[0] ) || values[0].startsWith("this.") || values[0].contains( ")this)." );
    }

    private String findLeftExpressionValue(RelationalExprDescr relDescr) {
        return relDescr.getLeft() instanceof AtomicExprDescr ?
               ((AtomicExprDescr) relDescr.getLeft()).getRewrittenExpression() :
               ((BindingDescr) relDescr.getLeft()).getExpression();
    }

    private String findRightExpressionValue(RelationalExprDescr relDescr) {
        return relDescr.getRight() instanceof AtomicExprDescr ?
               ((AtomicExprDescr) relDescr.getRight()).getRewrittenExpression().trim() :
               ((BindingDescr) relDescr.getRight()).getExpression().trim();
    }

    protected Constraint buildConstraintForPattern( final RuleBuildContext context,
                                                    final Pattern pattern,
                                                    final RelationalExprDescr relDescr,
                                                    String expr,
                                                    String value1,
                                                    String value2,
                                                    boolean isConstant,
                                                    Map<String, OperatorDescr> aliases) {

        InternalReadAccessor extractor = getFieldReadAccessor( context, relDescr, pattern, value1, null, true );
        if (extractor == null) {
            return null;
        }

        int dotPos = value1.indexOf('.');
        if (dotPos > 0) {
            String part0 = value1.substring(0, dotPos).trim();
            if ("this".equals( part0.trim() ) ) {
                value1 = value1.substring(dotPos + 1);
            } else if ( pattern.getDeclaration() != null && part0.equals( pattern.getDeclaration().getIdentifier() ) ) {
                value1 = value1.substring(dotPos + 1);
                expr = expr.substring(dotPos + 1);
            }
        }

        ValueType vtype = extractor.getValueType();
        String operator = relDescr.getOperator().trim();
        LiteralRestrictionDescr restrictionDescr = buildLiteralRestrictionDescr(context, relDescr, value2, operator, isConstant);

        if (restrictionDescr != null) {
            FieldValue field = getFieldValue(context, vtype, restrictionDescr.getText().trim());
            if (field != null) {
                Constraint constraint = getConstraintBuilder( context ).buildLiteralConstraint(context, pattern, vtype, field, expr, value1, operator, value2, extractor, restrictionDescr, aliases);
                if (constraint != null) {
                    return constraint;
                }
            }
        }

        value2 = context.getDeclarationResolver().normalizeValueForUnit( value2 );

        Declaration declr = null;
        if ( value2.indexOf( '(' ) < 0 && value2.indexOf( '.' ) < 0 && value2.indexOf( '[' ) < 0 ) {
            declr = context.getDeclarationResolver().getDeclaration( value2 );

            if ( declr == null ) {
                // trying to create implicit declaration
                final Pattern thisPattern = (Pattern) context.getDeclarationResolver().peekBuildStack();
                declr = createDeclarationObject( context, value2, thisPattern );
            }
        }

        Declaration[] declarations = null;
        if ( declr == null ) {
            String[] parts = value2.split( "\\." );
            if ( parts.length == 2 ) {
                if ( "this".equals( parts[0].trim() ) ) {
                    declr = createDeclarationObject(context, parts[1].trim(), (Pattern) context.getDeclarationResolver().peekBuildStack());
                    value2 = parts[1].trim();
                } else {
                    declr = context.getDeclarationResolver().getDeclaration( parts[0].trim() );
                    // if a declaration exists, then it may be a variable direct property access
                    if ( declr != null ) {
                        if ( declr.isPatternDeclaration() ) {
                            // TODO: no need to extract inner declaration when using mvel constraint
                            declarations = new Declaration[] { declr };
                            declr = createDeclarationObject(context, parts[1].trim(), declr.getPattern());
                            value2 = parts[1].trim();

                        } else {
                            // we will later fallback to regular predicates, so don't raise error
                            return null;
                        }
                    }
                }
            }
        }

        if (declarations == null) {
            if (declr != null) {
                declarations = new Declaration[] { declr };
            } else {
                declarations = getDeclarationsForReturnValue(context, relDescr, operator, value2);
                if (declarations == null) {
                    return null;
                }
            }
        }

        return getConstraintBuilder( context ).buildVariableConstraint(context, pattern, expr, declarations, value1, relDescr.getOperatorDescr(), value2, extractor, declr, relDescr, aliases);
    }

    private Declaration[] getDeclarationsForReturnValue(RuleBuildContext context, RelationalExprDescr relDescr, String operator, String value2) {
        Pattern pattern = (Pattern) context.getDeclarationResolver().peekBuildStack();
        ReturnValueRestrictionDescr returnValueRestrictionDescr = new ReturnValueRestrictionDescr( operator,
                                                                                                   relDescr.isNegated(),
                                                                                                   relDescr.getParametersText(),
                                                                                                   value2 );

        Class< ? > thisClass = pattern.getObjectType() instanceof ClassObjectType ?
                               ((ClassObjectType) pattern.getObjectType()).getClassType() :
                               null;

        AnalysisResult analysis = context.getDialect().analyzeExpression(context,
                                                                         returnValueRestrictionDescr,
                                                                         returnValueRestrictionDescr.getContent(),
                                                                         new BoundIdentifiers(pattern, context, null, thisClass));
        if ( analysis == null ) {
            // something bad happened
            return null;
        }
        final BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();

        final List<Declaration> tupleDeclarations = new ArrayList<Declaration>();
        final List<Declaration> factDeclarations = new ArrayList<Declaration>();
        for ( String id : usedIdentifiers.getDeclrClasses().keySet() ) {
            Declaration decl = context.getDeclarationResolver().getDeclaration( id );
            if ( decl.getPattern() == pattern ) {
                factDeclarations.add( decl );
            } else {
                tupleDeclarations.add( decl );
            }
        }
        createImplicitBindings( context, pattern, analysis.getNotBoundedIdentifiers(), usedIdentifiers, factDeclarations );

        final Declaration[] previousDeclarations = tupleDeclarations.toArray( new Declaration[tupleDeclarations.size()] );
        final Declaration[] localDeclarations = factDeclarations.toArray( new Declaration[factDeclarations.size()] );

        Arrays.sort( previousDeclarations, SortDeclarations.instance );
        Arrays.sort( localDeclarations, SortDeclarations.instance );

        final String[] requiredGlobals = usedIdentifiers.getGlobals().keySet().toArray( new String[usedIdentifiers.getGlobals().size()] );

        Declaration[] requiredDeclarations = new Declaration[previousDeclarations.length + localDeclarations.length];
        System.arraycopy( previousDeclarations,
                          0,
                          requiredDeclarations,
                          0,
                          previousDeclarations.length );
        System.arraycopy( localDeclarations,
                          0,
                          requiredDeclarations,
                          previousDeclarations.length,
                          localDeclarations.length );

        Declaration[] declarations = new Declaration[requiredDeclarations.length + requiredGlobals.length];
        int i = 0;
        for (Declaration requiredDeclaration : requiredDeclarations) {
            declarations[i++] = requiredDeclaration;
        }
        for (String requiredGlobal : requiredGlobals) {
            declarations[i++] = context.getDeclarationResolver().getDeclaration(requiredGlobal);
        }
        return declarations;
    }

    protected LiteralRestrictionDescr buildLiteralRestrictionDescr(RuleBuildContext context,
                                                                   RelationalExprDescr exprDescr,
                                                                   String rightValue,
                                                                   String operator,
                                                                   boolean isRightLiteral) {
        // is it a literal? Does not include enums
        if ( isRightLiteral ) {
            return new LiteralRestrictionDescr(operator, exprDescr.isNegated(), exprDescr.getParameters(), rightValue, LiteralRestrictionDescr.TYPE_STRING);
        }

        // is it an enum?
        int dotPos = rightValue.lastIndexOf( '.' );
        if ( dotPos >= 0 ) {
            final String mainPart = rightValue.substring( 0,
                                                     dotPos );
            String lastPart = rightValue.substring( dotPos + 1 );
            try {
                context.getDialect().getTypeResolver().resolveType( mainPart );
                if ( lastPart.indexOf( '(' ) < 0 && lastPart.indexOf( '.' ) < 0 && lastPart.indexOf( '[' ) < 0 ) {
                    return new LiteralRestrictionDescr(operator, exprDescr.isNegated(), exprDescr.getParameters(), rightValue, LiteralRestrictionDescr.TYPE_STRING );
                }
            } catch ( ClassNotFoundException e ) {
                // do nothing as this is just probing to see if it was a class, which we now know it isn't :)
            }  catch ( NoClassDefFoundError e ) {
                // do nothing as this is just probing to see if it was a class, which we now know it isn't :)
            }
        }

        return null;
    }

    protected Constraint processAtomicExpression( RuleBuildContext context,
                                                  Pattern pattern,
                                                  BaseDescr d,
                                                  String expr,
                                                  Map<String, OperatorDescr> aliases ) {
        if ( d instanceof AtomicExprDescr ) {
            Matcher m = evalRegexp.matcher( ((AtomicExprDescr) d).getExpression() );
            if ( m.find() ) {
                // MVELDumper already stripped the eval
                // this will build the eval using the specified dialect
                PredicateDescr pdescr = new PredicateDescr( context.getRuleDescr().getResource(), expr );
                pdescr.copyLocation(d);
                return buildEval( context, pattern, pdescr, aliases, expr, true );
            }
        }
        return null;
    }

    protected boolean isSimpleExpr( final RelationalExprDescr relDescr ) {
        boolean simple = false;
        if ( relDescr != null ) {
            if ( (relDescr.getLeft() instanceof AtomicExprDescr || relDescr.getLeft() instanceof BindingDescr) &&
                 (relDescr.getRight() instanceof AtomicExprDescr || relDescr.getRight() instanceof BindingDescr) ) {
                simple = true;
            }
        }
        return simple;
    }

    protected Constraint createAndBuildPredicate( RuleBuildContext context,
                                                  Pattern pattern,
                                                  BaseDescr base,
                                                  String expr,
                                                  Map<String, OperatorDescr> aliases ) {
        Dialect dialect = context.getDialect();
        MVELDialect mvelDialect = (MVELDialect) context.getDialect( "mvel" );
        context.setDialect( mvelDialect );

        PredicateDescr pdescr = new PredicateDescr( context.getRuleDescr().getResource(), expr );
        pdescr.copyParameters( base );
        pdescr.copyLocation( base );
        Constraint evalConstraint = buildEval(context, pattern, pdescr, aliases, expr, false);

        // fall back to original dialect
        context.setDialect(dialect);
        return evalConstraint;
    }

    protected void setInputs( RuleBuildContext context,
                            ExprBindings descrBranch,
                            Class< ? > thisClass,
                            String expr ) {
        MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" );
        ParserConfiguration conf = data.getParserConfiguration();

        conf.setClassLoader( context.getKnowledgeBuilder().getRootClassLoader() );

        final ParserContext pctx = new ParserContext( conf );
        pctx.setStrictTypeEnforcement(false);
        pctx.setStrongTyping( false );
        pctx.addInput( "this", thisClass );
        pctx.addInput( "empty", boolean.class ); // overrides the mvel empty label
        MVEL.COMPILER_OPT_ALLOW_NAKED_METH_CALL = true;
        MVEL.COMPILER_OPT_ALLOW_OVERRIDE_ALL_PROPHANDLING = true;
        MVEL.COMPILER_OPT_ALLOW_RESOLVE_INNERCLASSES_WITH_DOTNOTATION = true;
        MVEL.COMPILER_OPT_SUPPORT_JAVA_STYLE_CLASS_LITERALS = true;

        try {
            MVEL.analysisCompile( expr, pctx );
        } catch (Exception e) {
            // There is a problem in setting the inputs for this expression, but it will be
            // reported during expression analysis, so swallow it at the moment
            return;
        }

        if ( !pctx.getInputs().isEmpty() ) {
            for ( String v : pctx.getInputs().keySet() ) {
                // in the following if, we need to check that the expr actually contains a reference
                // to an "empty" property, or the if will evaluate to true even if it doesn't 
                if ( "this".equals( v ) || (PropertyTools.getFieldOrAccessor( thisClass,
                                                                               v ) != null && expr.matches( "(^|.*\\W)empty($|\\W.*)" )) ) {
                    descrBranch.getFieldAccessors().add( v );
                } else if ( "empty".equals( v ) ) {
                    // do nothing
                } else if ( !context.getPkg().getGlobals().containsKey( v ) ) {
                    descrBranch.getRuleBindings().add( v );
                } else {
                    descrBranch.getGlobalBindings().add( v );
                }
            }
        }
    }

    public static class ExprBindings {
        protected Set<String> globalBindings;
        protected Set<String> ruleBindings;
        protected Set<String> fieldAccessors;

        public ExprBindings() {
            this.globalBindings = new HashSet<String>();
            this.ruleBindings = new HashSet<String>();
            this.fieldAccessors = new HashSet<String>();
        }

        public Set<String> getGlobalBindings() {
            return globalBindings;
        }

        public Set<String> getRuleBindings() {
            return ruleBindings;
        }

        public Set<String> getFieldAccessors() {
            return fieldAccessors;
        }

        public boolean isConstant() {
            return this.globalBindings.isEmpty() && this.ruleBindings.isEmpty() && this.fieldAccessors.size() <= 1; // field accessors might contain the "this" reference
        }
    }

    protected void buildRuleBindings( final RuleBuildContext context,
                                      final PatternDescr patternDescr,
                                      final Pattern pattern,
                                      final BindingDescr fieldBindingDescr ) {

        if ( context.getDeclarationResolver().isDuplicated( context.getRule(),
                                                            fieldBindingDescr.getVariable(),
                                                            null ) ) {
            processDuplicateBindings( fieldBindingDescr.isUnification(),
                                      patternDescr,
                                      pattern,
                                      fieldBindingDescr,
                                      fieldBindingDescr.getBindingField(),
                                      fieldBindingDescr.getVariable(),
                                      context );
            if ( fieldBindingDescr.isUnification() ) {
                return;
            }
        }

        Declaration declr = pattern.addDeclaration( fieldBindingDescr.getVariable() );

        final InternalReadAccessor extractor = getFieldReadAccessor( context,
                                                                     fieldBindingDescr,
                                                                     pattern,
                                                                     fieldBindingDescr.getBindingField(),
                                                                     declr,
                                                                     true );

        if (extractor == null) {
            context.addError(new DescrBuildError(context.getParentDescr(),
                                                 patternDescr,
                                                 null,
                                                 "Field Reader does not exist for declaration '" + fieldBindingDescr.getVariable() + "' in '" + fieldBindingDescr + "' in the rule '" + context.getRule().getName() + "'"));
            return;
        }

        declr.setReadAccessor( extractor );

        if ( extractor instanceof ClassFieldReader ) {
            ObjectType patternType = pattern.getObjectType();
            if (patternType instanceof ClassObjectType) {
                Class<?> patternClass = ((ClassObjectType)patternType).getClassType();
                TypeDeclaration typeDeclaration = context.getKnowledgeBuilder().getTypeDeclaration(patternClass);

                String fieldName = (( ClassFieldReader) extractor ).getFieldName();
                if ( typeDeclaration.getAccessibleProperties().contains( fieldName ) ) {
                    List<String> watchlist = pattern.getListenedProperties();
                    if ( watchlist == null ) {
                        watchlist = new ArrayList<String>( );
                        pattern.setListenedProperties( watchlist );
                    }
                    watchlist.add( fieldName );
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected Constraint buildEval( final RuleBuildContext context,
                                    final Pattern pattern,
                                    final PredicateDescr predicateDescr,
                                    final Map<String, OperatorDescr> aliases,
                                    final String expr,
                                    final boolean isEvalExpression) {

        AnalysisResult analysis = buildAnalysis(context, pattern, predicateDescr, aliases );

        if ( analysis == null ) {
            // something bad happened
            return null;
        }

        Declaration[][] usedDeclarations = getUsedDeclarations(context, pattern, analysis);
        Declaration[] previousDeclarations = usedDeclarations[0];
        Declaration[] localDeclarations = usedDeclarations[1];

        BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();

        Arrays.sort( previousDeclarations, SortDeclarations.instance );
        Arrays.sort( localDeclarations, SortDeclarations.instance );

        boolean isJavaEval = isEvalExpression && context.getDialect() instanceof JavaDialect;

        if (isJavaEval) {
            final PredicateConstraint predicateConstraint = new PredicateConstraint(null,
                                                                                    previousDeclarations,
                                                                                    localDeclarations);

            final PredicateBuilder builder = context.getDialect().getPredicateBuilder();

            builder.build(context,
                          usedIdentifiers,
                          previousDeclarations,
                          localDeclarations,
                          predicateConstraint,
                          predicateDescr,
                          analysis);

            return predicateConstraint;
        }

        MVELCompilationUnit compilationUnit = getConstraintBuilder( context ).buildCompilationUnit( context,
                                                                                                    previousDeclarations,
                                                                                                    localDeclarations,
                                                                                                    predicateDescr,
                                                                                                    analysis);

        String[] requiredGlobals = usedIdentifiers.getGlobals().keySet().toArray( new String[usedIdentifiers.getGlobals().size()] );
        Declaration[] mvelDeclarations = new Declaration[previousDeclarations.length + localDeclarations.length + requiredGlobals.length];
        int i = 0;
        for (Declaration d : previousDeclarations) {
            mvelDeclarations[i++] = d;
        }
        for (Declaration d : localDeclarations) {
            mvelDeclarations[i++] = d;
        }
        for (String global : requiredGlobals) {
            mvelDeclarations[i++] = context.getDeclarationResolver().getDeclaration(global);
        }

        boolean isDynamic =
                !((ClassObjectType)pattern.getObjectType()).getClassType().isArray() &&
                !context.getKnowledgeBuilder().getTypeDeclaration(((ClassObjectType)pattern.getObjectType()).getClassType()).isTypesafe();

        return getConstraintBuilder( context ).buildMvelConstraint( context.getPkg().getName(), expr, mvelDeclarations, getOperators( usedIdentifiers.getOperators() ), compilationUnit, isDynamic );
    }

    protected static EvaluatorWrapper[] getOperators( Map<String, EvaluatorWrapper> operatorMap ) {
        EvaluatorWrapper[] operators = new EvaluatorWrapper[operatorMap.size()];
        int i = 0;
        for (Map.Entry<String, EvaluatorWrapper> entry : operatorMap.entrySet()) {
            EvaluatorWrapper evaluator = entry.getValue();
            evaluator.setBindingName( entry.getKey() );
            operators[i++] = evaluator;
        }
        return operators;
    }

    public static Declaration[][] getUsedDeclarations(RuleBuildContext context, Pattern pattern, AnalysisResult analysis) {
        BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();
        final List<Declaration> tupleDeclarations = new ArrayList<Declaration>();
        final List<Declaration> factDeclarations = new ArrayList<Declaration>();

        for ( String id : usedIdentifiers.getDeclrClasses().keySet() ) {
            Declaration decl = context.getDeclarationResolver().getDeclaration( id );
            if ( decl.getPattern() == pattern ) {
                factDeclarations.add( decl );
            } else {
                tupleDeclarations.add( decl );
            }
        }

        createImplicitBindings( context,
                                pattern,
                                analysis.getNotBoundedIdentifiers(),
                                analysis.getBoundIdentifiers(),
                                factDeclarations );

        Declaration[][] usedDeclarations = new Declaration[2][];
        usedDeclarations[0] = tupleDeclarations.toArray( new Declaration[tupleDeclarations.size()] );
        usedDeclarations[1] = factDeclarations.toArray( new Declaration[factDeclarations.size()] );
        return usedDeclarations;
    }

    public static AnalysisResult buildAnalysis(RuleBuildContext context, Pattern pattern, PredicateDescr predicateDescr, Map<String, OperatorDescr> aliases ) {
        Map<String, EvaluatorWrapper> operators = aliases == null ? new HashMap<String, EvaluatorWrapper>() : buildOperators(context, pattern, predicateDescr, aliases);

        Class< ? > thisClass = pattern.getObjectType() instanceof ClassObjectType ?
                               ((ClassObjectType) pattern.getObjectType()).getClassType() : null;

        return context.getDialect().analyzeExpression( context,
                                                        predicateDescr,
                                                        predicateDescr.getContent(),
                                                        new BoundIdentifiers( pattern,
                                                                              context,
                                                                              operators,
                                                                              thisClass ) );
    }

    protected static Map<String, EvaluatorWrapper> buildOperators(RuleBuildContext context,
                                                                  Pattern pattern,
                                                                  BaseDescr predicateDescr,
                                                                  Map<String, OperatorDescr> aliases) {
        if (aliases.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, EvaluatorWrapper> operators = new HashMap<String, EvaluatorWrapper>();
        for ( Map.Entry<String, OperatorDescr> entry : aliases.entrySet() ) {
            OperatorDescr op = entry.getValue();

            Declaration leftDecl = createDeclarationForOperator(context, pattern, op.getLeftString());
            Declaration rightDecl = createDeclarationForOperator(context, pattern, op.getRightString());

            Target left = leftDecl != null && leftDecl.isPatternDeclaration() ? Target.HANDLE : Target.FACT;
            Target right = rightDecl != null && rightDecl.isPatternDeclaration() ? Target.HANDLE : Target.FACT;

            op.setLeftIsHandle( left == Target.HANDLE );
            op.setRightIsHandle( right == Target.HANDLE );

            Evaluator evaluator = getConstraintBuilder( context ).getEvaluator( context,
                                                                                predicateDescr,
                                                                                ValueType.OBJECT_TYPE,
                                                                                op.getOperator(),
                                                                                false, // the rewrite takes care of negation
                                                                                op.getParametersText(),
                                                                                left,
                                                                                right);

            EvaluatorWrapper wrapper = getConstraintBuilder( context ).wrapEvaluator( evaluator, leftDecl, rightDecl );
            operators.put( entry.getKey(), wrapper );
        }
        return operators;
    }

    private static Declaration createDeclarationForOperator(RuleBuildContext context, Pattern pattern, String expr) {
        Declaration declaration;
        int dotPos = expr.indexOf('.');
        if (dotPos < 0) {
            if (!isIdentifier(expr)) {
                return null;
            }
            declaration = context.getDeclarationResolver().getDeclaration( expr );
            if ( declaration == null) {
                if ( "this".equals( expr ) ) {
                    declaration = createDeclarationObject( context, "this", pattern );
                } else {
                    declaration = new Declaration("this", pattern);
                    context.getPkg().getClassFieldAccessorStore().getReader( ((ClassObjectType) pattern.getObjectType()).getClassName(), expr, declaration );
                }
            }
        } else {
            String part1 = expr.substring(0, dotPos).trim();
            String part2 = expr.substring(dotPos+1).trim();
            if ( "this".equals( part1 ) ) {
                declaration = createDeclarationObject(context, part2, (Pattern) context.getDeclarationResolver().peekBuildStack());
            } else {
                declaration = context.getDeclarationResolver().getDeclaration( part1 );
                // if a declaration exists, then it may be a variable direct property access
                if ( declaration != null ) {
                    declaration = createDeclarationObject(context, part2, declaration.getPattern());
                }
            }
        }
        return declaration;
    }

    protected static ConstraintBuilder getConstraintBuilder( RuleBuildContext context ) {
        return context.getCompilerFactory().getConstraintBuilderFactoryService().newConstraintBuilder();
    }

    public static void createImplicitBindings( final RuleBuildContext context,
                                               final Pattern pattern,
                                               final Set<String> unboundIdentifiers,
                                               final BoundIdentifiers boundIdentifiers,
                                               final List<Declaration> factDeclarations ) {
        for ( Iterator<String> it = unboundIdentifiers.iterator(); it.hasNext(); ) {
            String identifier = it.next();
            Declaration declaration = createDeclarationObject( context,
                                                               identifier,
                                                               pattern );
            // the name may not be a local field, such as enums
            // maybe should have a safer way to detect this, as other issues may cause null too 
            // that we would need to know about
            if ( declaration != null ) {
                factDeclarations.add( declaration );
                // implicit bindings need to be added to "local" declarations, as they are nolonger unbound
                boundIdentifiers.getDeclrClasses().put( identifier,
                                                        declaration.getDeclarationClass() );
                it.remove();
            }
        }
    }

    /**
     * Creates a declaration object for the field identified by the given identifier
     * on the give pattern object
     */
    protected static Declaration createDeclarationObject( final RuleBuildContext context,
                                                          final String identifier,
                                                          final Pattern pattern ) {
        return createDeclarationObject( context, identifier, identifier, pattern );
    }

    protected static Declaration createDeclarationObject( final RuleBuildContext context,
                                                          final String identifier,
                                                          final String expr,
                                                          final Pattern pattern ) {
        final BindingDescr implicitBinding = new BindingDescr( identifier, expr );

        final Declaration declaration = new Declaration( identifier,
                                                         null,
                                                         pattern,
                                                         true );

        InternalReadAccessor extractor = getFieldReadAccessor( context,
                                                               implicitBinding,
                                                               pattern,
                                                               implicitBinding.getExpression(),
                                                               declaration,
                                                               false );

        if ( extractor == null ) {
            return null;
        }

        declaration.setReadAccessor( extractor );

        return declaration;
    }

    private FieldValue getFieldValue( RuleBuildContext context,
                                      ValueType vtype,
                                      String value) {
        try {
            MVEL.COMPILER_OPT_ALLOW_NAKED_METH_CALL = true;
            MVEL.COMPILER_OPT_ALLOW_OVERRIDE_ALL_PROPHANDLING = true;
            MVEL.COMPILER_OPT_ALLOW_RESOLVE_INNERCLASSES_WITH_DOTNOTATION = true;
            MVEL.COMPILER_OPT_SUPPORT_JAVA_STYLE_CLASS_LITERALS = true;

            MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" );
            ParserConfiguration pconf = data.getParserConfiguration();
            ParserContext pctx = new ParserContext( pconf );

            Object o = MVELSafeHelper.getEvaluator().executeExpression( MVEL.compileExpression( value, pctx ) );
            if ( o != null && vtype == null ) {
                // was a compilation problem else where, so guess valuetype so we can continue
                vtype = ValueType.determineValueType( o.getClass() );
            }

            return context.getCompilerFactory().getFieldFactory().getFieldValue(o, vtype);
        } catch ( final Exception e ) {
            // we will fallback to regular preducates, so don't raise an error
        }
        return null;
    }

    public static void registerReadAccessor( final RuleBuildContext context,
                                             final ObjectType objectType,
                                             final String fieldName,
                                             final AcceptsReadAccessor target ) {
        if ( !ValueType.FACTTEMPLATE_TYPE.equals( objectType.getValueType() ) ) {
            context.getPkg().getClassFieldAccessorStore().getReader( ((ClassObjectType) objectType).getClassName(), fieldName, target );
        }
    }

    public static InternalReadAccessor getFieldReadAccessor( final RuleBuildContext context,
                                                             final BaseDescr descr,
                                                             final Pattern pattern,
                                                             String fieldName,
                                                             final AcceptsReadAccessor target,
                                                             final boolean reportError ) {
        return getFieldReadAccessor( context, descr, pattern, pattern.getObjectType(), fieldName, target, reportError );
    }

    public static InternalReadAccessor getFieldReadAccessor( final RuleBuildContext context,
                                                             final BaseDescr descr,
                                                             final Pattern pattern,
                                                             final ObjectType objectType,
                                                             String fieldName,
                                                             final AcceptsReadAccessor target,
                                                             final boolean reportError ) {
        // reportError is needed as some times failure to build accessor is not a failure, just an indication that building is not possible so try something else.
        InternalReadAccessor reader;

        if ( ValueType.FACTTEMPLATE_TYPE.equals( objectType.getValueType() ) ) {
            //@todo use accessor cache            
            final FactTemplate factTemplate = ((FactTemplateObjectType) objectType).getFactTemplate();
            reader = new FactTemplateFieldExtractor( factTemplate, factTemplate.getFieldTemplateIndex( fieldName ) );
            if ( target != null ) {
                target.setReadAccessor( reader );
            }

            return reader;
        }

        boolean isGetter = getterRegexp.matcher( fieldName ).matches();
        if ( isGetter ) {
            fieldName = fieldName.substring(3, fieldName.indexOf('(')).trim();
        }

        if ( isGetter || identifierRegexp.matcher( fieldName ).matches() ) {
            Declaration decl = context.getDeclarationResolver().getDeclarations( context.getRule() ).get(fieldName);
            if (decl != null && decl.getExtractor() instanceof ClassFieldReader && "this".equals(((ClassFieldReader)decl.getExtractor()).getFieldName())) {
                return decl.getExtractor();
            }

            try {
                reader = context.getPkg().getClassFieldAccessorStore().getReader( ((ClassObjectType) objectType).getClassName(),
                                                                                  fieldName,
                                                                                  target );
            } catch ( final Exception e ) {
                if ( reportError && context.isTypesafe() ) {
                    DialectUtil.copyErrorLocation(e,
                                                  descr);
                    context.addError(new DescrBuildError(context.getParentDescr(),
                                                         descr,
                                                         e,
                                                         "Unable to create Field Extractor for '" + fieldName + "'" + e.getMessage()));
                }
                // if there was an error, set the reader back to null
                reader = null;
            } finally {

                if (reportError) {
                    Collection<KnowledgeBuilderResult> results = context.getPkg().getClassFieldAccessorStore()
                                                                        .getWiringResults( ( (ClassObjectType) objectType ).getClassType(), fieldName );
                    if (!results.isEmpty()) {
                        for (KnowledgeBuilderResult res : results) {
                            if (res.getSeverity() == ResultSeverity.ERROR) {
                                context.addError(new DroolsErrorWrapper(res));
                            } else {
                                context.addWarning(new DroolsWarningWrapper(res));
                            }
                        }
                    }
                }
            }
        } else {
            // we need MVEL extractor for expressions
            Dialect dialect = context.getDialect();
            try {
                MVELDialect mvelDialect = (MVELDialect) context.getDialect( "mvel" );
                context.setDialect( mvelDialect );

                final AnalysisResult analysis = context.getDialect().analyzeExpression( context,
                                                                                        descr,
                                                                                        fieldName,
                                                                                        new BoundIdentifiers( pattern, context, Collections.EMPTY_MAP,
                                                                                                              ((ClassObjectType) objectType).getClassType() ) );

                if ( analysis == null ) {
                    // something bad happened
                    if ( reportError ) {
                        context.addError(new DescrBuildError(context.getParentDescr(),
                                                             descr,
                                                             null,
                                                             "Unable to analyze expression '" + fieldName + "'"));
                    }
                    return null;
                }

                final BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();

                if ( !usedIdentifiers.getDeclrClasses().isEmpty() ) {
                    if ( reportError && descr instanceof BindingDescr ) {
                        context.addError(new DescrBuildError(context.getParentDescr(),
                                                             descr,
                                                             null,
                                                             "Variables can not be used inside bindings. Variable " + usedIdentifiers.getDeclrClasses().keySet() + " is being used in binding '" + fieldName + "'"));
                    }
                    return null;
                }

                reader = context.getPkg().getClassFieldAccessorStore().getMVELReader( context.getPkg().getName(),
                                                                                      ((ClassObjectType) objectType).getClassName(),
                                                                                      fieldName,
                                                                                      context.isTypesafe(),
                                                                                      ((MVELAnalysisResult)analysis).getReturnType() );

                MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" );
                ((MVELCompileable) reader).compile( data, context.getRule() );
                data.addCompileable( (MVELCompileable) reader );
            } catch ( final Exception e ) {
                int dotPos = fieldName.indexOf('.');
                String varName = dotPos > 0 ? fieldName.substring(0, dotPos).trim() : fieldName;
                if (context.getKnowledgeBuilder().getGlobals().containsKey(varName)) {
                    return null;
                }

                if ( reportError ) {
                    DialectUtil.copyErrorLocation(e, descr);
                    context.addError(new DescrBuildError(context.getParentDescr(),
                                                         descr,
                                                         e,
                                                         "Unable to create reader for '" + fieldName + "':" + e.getMessage()));
                }
                // if there was an error, set the reader back to null
                reader = null;
            } finally {
                context.setDialect( dialect );
            }
        }

        return reader;
    }

    protected ConstraintConnectiveDescr parseExpression( final RuleBuildContext context,
                                                         final PatternDescr patternDescr,
                                                         final BaseDescr original,
                                                         final String expression ) {
        DrlExprParser parser = new DrlExprParser( context.getConfiguration().getLanguageLevel() );
        ConstraintConnectiveDescr result = parser.parse( expression );
        result.setResource(patternDescr.getResource());

        if (result == null) {
            context.addError(new DescrBuildError(context.getParentDescr(),
                                                 patternDescr,
                                                 null,
                                                 "Unable to parse pattern expression:\n" + expression));
            return null;
        }

        result.copyLocation( original );
        if ( parser.hasErrors() ) {
            for ( DroolsParserException error : parser.getErrors() ) {
                context.addError(new DescrBuildError(context.getParentDescr(),
                                                     patternDescr,
                                                     null,
                                                     "Unable to parse pattern expression:\n" + error.getMessage()));
            }
            return null;
        }
        return result;
    }
}
