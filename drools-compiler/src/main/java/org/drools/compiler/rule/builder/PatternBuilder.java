/*
 * Copyright 2006 JBoss Inc
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
import org.drools.compiler.lang.descr.LiteralRestrictionDescr;
import org.drools.compiler.lang.descr.OperatorDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.PredicateDescr;
import org.drools.compiler.lang.descr.RelationalExprDescr;
import org.drools.compiler.lang.descr.ReturnValueRestrictionDescr;
import org.drools.compiler.rule.builder.dialect.DialectUtil;
import org.drools.compiler.rule.builder.dialect.java.JavaDialect;
import org.drools.compiler.rule.builder.dialect.mvel.MVELAnalysisResult;
import org.drools.compiler.rule.builder.dialect.mvel.MVELDialect;
import org.drools.core.base.ClassFieldReader;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.EvaluatorWrapper;
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
import org.drools.core.rule.From;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.PatternSource;
import org.drools.core.rule.PredicateConstraint;
import org.drools.core.rule.QueryImpl;
import org.drools.core.rule.RuleConditionElement;
import org.drools.core.rule.SlidingLengthWindow;
import org.drools.core.rule.SlidingTimeWindow;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.rule.constraint.EvaluatorConstraint;
import org.drools.core.rule.constraint.MvelConstraint;
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
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.ResultSeverity;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import org.mvel2.integration.PropertyHandler;
import org.mvel2.integration.PropertyHandlerFactory;
import org.mvel2.util.PropertyTools;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import static org.drools.compiler.rule.builder.MVELConstraintBuilder.normalizeMVELLiteralExpression;
import static org.drools.core.util.StringUtils.isIdentifier;

/**
 * A builder for patterns
 */
public class PatternBuilder
    implements
    RuleConditionBuilder {

    private static final java.util.regex.Pattern evalRegexp = java.util.regex.Pattern.compile( "^eval\\s*\\(",
                                                                                               java.util.regex.Pattern.MULTILINE );

    private static final java.util.regex.Pattern identifierRegexp = java.util.regex.Pattern.compile( "([\\p{L}_$][\\p{L}\\p{N}_$]*)" );


    public PatternBuilder() {
    }

    public RuleConditionElement build( RuleBuildContext context,
                                       BaseDescr descr ) {
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
     *
     * @param context
     * @param descr
     * @param prefixPattern
     * @return
     */
    public RuleConditionElement build( RuleBuildContext context,
                                       BaseDescr descr,
                                       Pattern prefixPattern ) {

        final PatternDescr patternDescr = (PatternDescr) descr;

        if ( patternDescr.getObjectType() == null || patternDescr.getObjectType().equals( "" ) ) {
            context.addError(new DescrBuildError(context.getParentDescr(),
                    patternDescr,
                    null,
                    "ObjectType not correctly defined"));
            return null;
        }

        ObjectType objectType = null;

        final FactTemplate factTemplate = context.getPkg().getFactTemplate( patternDescr.getObjectType() );

        if ( factTemplate != null ) {
            objectType = new FactTemplateObjectType( factTemplate );
        } else {
            try {
                final Class< ? > userProvidedClass = context.getDialect().getTypeResolver().resolveType( patternDescr.getObjectType() );
                if ( !Modifier.isPublic(userProvidedClass.getModifiers()) ) {
                    context.addError(new DescrBuildError(context.getParentDescr(),
                                                         patternDescr,
                                                         null,
                                                         "The class '" + patternDescr.getObjectType() + "' is not public"));
                    return null;
                }
                PackageRegistry pkgr = context.getKnowledgeBuilder().getPackageRegistry( ClassUtils.getPackage( userProvidedClass ) );
                InternalKnowledgePackage pkg = pkgr == null ? context.getPkg() : pkgr.getPackage();
                final boolean isEvent = pkg.isEvent( userProvidedClass );
                objectType = new ClassObjectType( userProvidedClass,
                                                  isEvent );
            } catch ( final ClassNotFoundException e ) {
                // swallow as we'll do another check in a moment and then record the problem
            }
        }

        // lets see if it maps to a query
        if ( objectType == null ) {
            RuleConditionElement rce = null;
            // it might be a recursive query, so check for same names
            if ( context.getRule().getName().equals( patternDescr.getObjectType() ) ) {
                // it's a query so delegate to the QueryElementBuilder
                QueryElementBuilder qeBuilder = QueryElementBuilder.getInstance();
                rce = qeBuilder.build( context,
                                        descr,
                                        prefixPattern,
                                        (QueryImpl) context.getRule() );
            }

            if ( rce == null ) {
                RuleImpl rule = context.getPkg().getRule( patternDescr.getObjectType() );
                if ( rule instanceof QueryImpl) {
                    // it's a query so delegate to the QueryElementBuilder
                    QueryElementBuilder qeBuilder = QueryElementBuilder.getInstance();
                    rce = qeBuilder.build( context,
                                           descr,
                                           prefixPattern,
                                           (QueryImpl) rule );
                }

                // try package imports
                for ( String importName : context.getDialect().getTypeResolver().getImports() ) {
                    importName = importName.trim();
                    int pos = importName.indexOf( '*' );
                    if ( pos >= 0 ) {
                        String pkgName = importName.substring( 0,
                                                               pos - 1 );
                        PackageRegistry pkgReg = context.getKnowledgeBuilder().getPackageRegistry( pkgName );
                        if ( pkgReg != null ) {
                            rule = pkgReg.getPackage().getRule( patternDescr.getObjectType() );
                            if ( rule instanceof QueryImpl) {
                                // it's a query so delegate to the QueryElementBuilder
                                QueryElementBuilder qeBuilder = QueryElementBuilder.getInstance();
                                rce = qeBuilder.build( context,
                                                       descr,
                                                       prefixPattern,
                                                       (QueryImpl) rule );
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

        Pattern pattern;

        boolean duplicateBindings = context.getDeclarationResolver().isDuplicated( context.getRule(),
                                                                                   patternDescr.getIdentifier(),
                                                                                   ((ClassObjectType) objectType).getClassName() );

        if ( !StringUtils.isEmpty( patternDescr.getIdentifier() ) && !duplicateBindings ) {

            pattern = new Pattern( context.getNextPatternId(),
                                   0, // offset is 0 by default
                                   objectType,
                                   patternDescr.getIdentifier(),
                                   patternDescr.isInternalFact() );
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

        if ( ClassObjectType.Match_ObjectType.isAssignableFrom( pattern.getObjectType() ) ) {
            PropertyHandler handler = PropertyHandlerFactory.getPropertyHandler( RuleTerminalNodeLeftTuple.class );
            if ( handler == null ) {
                PropertyHandlerFactoryFixer.getPropertyHandlerClass().put( RuleTerminalNodeLeftTuple.class,
                                                                           new ActivationPropertyHandler() );
            }
        }

        // adding the newly created pattern to the build stack this is necessary in case of local declaration usage
        context.getBuildStack().push( pattern );

        if ( duplicateBindings ) {
            processDuplicateBindings( patternDescr.isUnification(), patternDescr, pattern, patternDescr, "this", patternDescr.getIdentifier(), context );
        }

        if ( objectType instanceof ClassObjectType ) {
            // make sure the Pattern is wired up to correct ClassObjectType and set as a target for rewiring
            context.getPkg().getClassFieldAccessorStore().getClassObjectType( ((ClassObjectType) objectType),
                                                                              pattern );
        }

        if ( pattern.getObjectType() instanceof ClassObjectType ) {
            Class< ? > cls = ((ClassObjectType) pattern.getObjectType()).getClassType();
            if ( cls.getPackage() != null && !cls.getPackage().getName().equals( "java.lang" ) ) {
                // register the class in its own package unless it is primitive or belongs to java.lang
                TypeDeclaration typeDeclr = context.getKnowledgeBuilder().getAndRegisterTypeDeclaration( cls,
                                                                                                       cls.getPackage().getName() );
                context.setTypesafe( typeDeclr == null || typeDeclr.isTypesafe() );
            } else {
                context.setTypesafe( true );
            }
        }

        processAnnotations( context, patternDescr, pattern );
        
        if ( patternDescr.getSource() != null ) {
            // we have a pattern source, so build it
            RuleConditionBuilder builder = (RuleConditionBuilder) context.getDialect().getBuilder( patternDescr.getSource().getClass() );

            PatternSource source = (PatternSource) builder.build( context,
                                                                  patternDescr.getSource() );
            if ( source instanceof From ) {
                ((From) source).setResultPattern( pattern );
            }
            pattern.setSource( source );
        }

        // Process all constraints
        processConstraintsAndBinds( context, patternDescr, pattern );

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

        // poping the pattern
        context.getBuildStack().pop();

        return pattern;
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
        Map<String, AnnotationDescr> annotationMap = patternDescr.getAnnotations();
        if (annotationMap == null) return;
        processListenedPropertiesAnnotation(context, patternDescr, pattern, annotationMap);
        processMetadataAnnotations( pattern, annotationMap );
    }

    protected void processMetadataAnnotations(Pattern pattern, Map<String, AnnotationDescr> annotationMap) {
        for ( String key : annotationMap.keySet() ) {
            if ( ! Pattern.ATTR_LISTENED_PROPS.equals( key ) ) {
                AnnotationDescr ann = annotationMap.get( key );
                AnnotationDefinition def = new AnnotationDefinition( key );
                for ( String propKey : ann.getValues().keySet() ) {
                    def.getValues().put( propKey, new AnnotationDefinition.AnnotationPropertyVal( propKey, null, ann.getValue( propKey ), null ) );
                }
                pattern.getAnnotations().put( key, def );
            }
        }        
    }

    protected void processListenedPropertiesAnnotation(RuleBuildContext context,
                                                     PatternDescr patternDescr,
                                                     Pattern pattern,
                                                     Map<String, AnnotationDescr> annotationMap) {
        AnnotationDescr listenedProps = annotationMap.get(Pattern.ATTR_LISTENED_PROPS);
        if (listenedProps != null) {
            List<String> settableProperties = getSettableProperties(context, patternDescr, pattern);

            List<String> listenedProperties = new ArrayList<String>();
            for (String propertyName : listenedProps.getValue().toString().split(",")) {
                propertyName = propertyName.trim();
                if (propertyName.equals("*") || propertyName.equals("!*")) {
                    if (listenedProperties.contains("*") || listenedProperties.contains("!*")) {
                        context.addError( new DescrBuildError( context.getParentDescr(),
                                patternDescr,
                                null,
                                "Duplicate usage of wildcard * in @" + Pattern.ATTR_LISTENED_PROPS + " annotation" ) );
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
                                                           "Unknown property " + propertyName + " in @" + Pattern.ATTR_LISTENED_PROPS + " annotation" ) );
                } else if (listenedProperties.contains(propertyName) || listenedProperties.contains("!" + propertyName)) {
                    context.addError( new DescrBuildError( context.getParentDescr(),
                                                           patternDescr,
                                                           null,
                                                           "Duplicate property " + propertyName + " in @" + Pattern.ATTR_LISTENED_PROPS + " annotation" ) );
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
                    "Wrong usage of @" + Pattern.ATTR_LISTENED_PROPS + " annotation on class " + patternClass.getName() + " that is not annotated as @PropertyReactive" ) );
        }
        typeDeclaration.setTypeClass(patternClass);
        return typeDeclaration.getSettableProperties();
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
     * 
     * @param context
     * @param patternDescr
     * @param pattern
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
                build( context,
                       patternDescr,
                       pattern,
                       result,
                       mvelCtx );
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
        Set<String> declarationNames = new HashSet<String>();

        boolean isFirst = true;
        String packageName = null;
        StringBuilder expressionBuilder = new StringBuilder(combinableConstraints.size() * 25);
        for (MvelConstraint constraint : combinableConstraints) {
            pattern.removeConstraint(constraint);
            if (isFirst) {
                packageName = constraint.getPackageName();
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
        }

        String expression = expressionBuilder.toString();
        MVELCompilationUnit compilationUnit = getConstraintBuilder( context ).buildCompilationUnit(context, pattern, expression, mvelCtx.getAliases());

        Constraint combinedConstraint = getConstraintBuilder( context ).buildMvelConstraint( packageName,
                                                                                             expression,
                                                                                             declarations.toArray(new Declaration[declarations.size()]),
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
                buildRuleBindings( context,
                                   patternDescr,
                                   pattern,
                                   binder );
            } else {
                // create a constraint
                build( context,
                       patternDescr,
                       pattern,
                       descr,
                       field.getName() + " == " + descr.getExpression() );
            }
        }
    }

    protected void build( final RuleBuildContext context,
                        final PatternDescr patternDescr,
                        final Pattern pattern,
                        final BaseDescr original,
                        final String expr ) {
        ConstraintConnectiveDescr result = parseExpression( context,
                                                            patternDescr,
                                                            original,
                                                            expr );
        if ( result == null ) {
            return;
        }
        result.copyLocation( original );
        MVELDumper.MVELDumperContext mvelCtx = new MVELDumper.MVELDumperContext().setRuleContext(context);
        build( context,
               patternDescr,
               pattern,
               result,
               mvelCtx );
    }

    protected void build( RuleBuildContext context,
                          PatternDescr patternDescr,
                          Pattern pattern,
                          ConstraintConnectiveDescr descr,
                          MVELDumper.MVELDumperContext mvelCtx ) {
        List<BaseDescr> initialDescrs = new ArrayList<BaseDescr>( descr.getDescrs() );
        for ( BaseDescr d : initialDescrs ) {
            buildCcdDescr(context, patternDescr, pattern, d, descr, mvelCtx);
        }

        if ( descr.getDescrs().size() > initialDescrs.size() ) {
            // The initial build process may have generated other constraint descrs.
            // This happens when null-safe references or inline-casts are used
            // These additional constraints must be built, and added as
            List<BaseDescr> additionalDescrs = new ArrayList<BaseDescr>( descr.getDescrs() );
            additionalDescrs.removeAll( initialDescrs );

            if ( ! additionalDescrs.isEmpty() ) {
                List<Constraint> constraints = new ArrayList<Constraint>( pattern.getConstraints() );
                for ( Constraint c : constraints ) {
                    pattern.removeConstraint( c );
                }
                for ( BaseDescr d : additionalDescrs ) {
                    buildCcdDescr(context, patternDescr, pattern, d, descr, mvelCtx);
                }
                for ( Constraint c : constraints ) {
                    pattern.addConstraint( c );
                }
            }
        }
    }

    protected void buildCcdDescr(RuleBuildContext context,
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

        // check if it is an atomic expression
        if ( expr.length() == 0 || processAtomicExpression( context, pattern, d, expr, aliases ) ) {
            // it is an atomic expression
            return;
        }

        // check if it is a simple expression or not
        buildExpression(context, pattern, d, expr, aliases );
    }


    protected void buildExpression( final RuleBuildContext context,
                                  final Pattern pattern,
                                  final BaseDescr d,
                                  final String expr,
                                  final Map<String, OperatorDescr> aliases ) {

        RelationalExprDescr relDescr = d instanceof RelationalExprDescr ? (RelationalExprDescr) d : null;
        boolean simple = isSimpleExpr( relDescr );

        if ( simple && // simple means also relDescr is != null
                !ClassObjectType.Map_ObjectType.isAssignableFrom( pattern.getObjectType() ) &&
                !ClassObjectType.Match_ObjectType.isAssignableFrom( pattern.getObjectType() ) ) {
            buildRelationalExpression( context, pattern, relDescr, expr, aliases );
        } else {
            // Either it's a complex expression, so do as predicate
            // Or it's a Map and we have to treat it as a special case
            createAndBuildPredicate( context, pattern, d, rewriteOrExpressions(context, pattern, d, expr), aliases );
        }
    }

    private String rewriteOrExpressions(RuleBuildContext context, Pattern pattern, BaseDescr d, String expr) {
        if (!(d instanceof ConstraintConnectiveDescr) || ((ConstraintConnectiveDescr) d).getConnective() != ConnectiveType.OR) {
            return expr;
        }
        List<BaseDescr> subDescrs = ((ConstraintConnectiveDescr) d).getDescrs();
        for (BaseDescr subDescr : subDescrs) {
            if (!(subDescr instanceof RelationalExprDescr && isSimpleExpr( (RelationalExprDescr)subDescr ))) {
                return expr;
            }
        }
        String[] subExprs = expr.split("\\Q||\\E");
        if (subExprs.length != subDescrs.size()) {
            return expr;
        }

        int i = 0;
        StringBuilder sb = new StringBuilder();
        for (BaseDescr subDescr : subDescrs) {
            RelationalExprDescr relDescr = (RelationalExprDescr)subDescr;
            String[] values = new String[2];
            findExpressionValues(relDescr, values);
            String normalizedExpr;

            if (!getExprBindings(context, pattern, values[1]).isConstant()) {
                normalizedExpr = subExprs[i++];
            } else {
                InternalReadAccessor extractor = getFieldReadAccessor( context, relDescr, pattern.getObjectType(), values[0], null, true );
                if (extractor == null) {
                    normalizedExpr = subExprs[i++];
                } else {
                    ValueType vtype = extractor.getValueType();
                    LiteralRestrictionDescr restrictionDescr = new LiteralRestrictionDescr( relDescr.getOperator(),
                                                                                            relDescr.isNegated(),
                                                                                            relDescr.getParameters(),
                                                                                            values[1],
                                                                                            LiteralRestrictionDescr.TYPE_STRING );
                    normalizedExpr = normalizeMVELLiteralExpression( vtype,
                                                                     getFieldValue(context, vtype, restrictionDescr),
                                                                     subExprs[i++],
                                                                     values[0],
                                                                     relDescr.getOperator(),
                                                                     values[1],
                                                                     restrictionDescr );
                }
            }

            sb.append(normalizedExpr);
            if (i < subExprs.length) {
                sb.append(" || ");
            }
        }

        return sb.toString();
    }

    protected void buildRelationalExpression( final RuleBuildContext context,
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
        if ( (!usesThisRef && value1Expr.isConstant()) ||
                !addConstraintToPattern( context, pattern, relDescr, expr, values[0], values[1], value2Expr.isConstant() )) {
            createAndBuildPredicate( context, pattern, relDescr, expr, aliases );
        }
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
        boolean usesThisRef;
        if ( relDescr.getRight() instanceof AtomicExprDescr ) {
            AtomicExprDescr rdescr = ((AtomicExprDescr) relDescr.getRight());
            values[1] = rdescr.getRewrittenExpression().trim();
            usesThisRef = "this".equals( values[1] ) || values[1].startsWith("this.");
        } else {
            BindingDescr rdescr = ((BindingDescr) relDescr.getRight());
            values[1] = rdescr.getExpression().trim();
            usesThisRef = "this".equals( values[1] ) || values[1].startsWith("this.");
        }
        if ( relDescr.getLeft() instanceof AtomicExprDescr ) {
            AtomicExprDescr ldescr = (AtomicExprDescr) relDescr.getLeft();
            values[0] = ldescr.getRewrittenExpression();
            usesThisRef = usesThisRef || "this".equals( values[0] ) || values[0].startsWith("this.");
        } else {
            values[0] = ((BindingDescr) relDescr.getLeft()).getExpression();
            usesThisRef = usesThisRef || "this".equals( values[0] ) || values[0].startsWith("this.");
        }
        return usesThisRef;
    }

    protected boolean addConstraintToPattern( final RuleBuildContext context,
                                            final Pattern pattern,
                                            final RelationalExprDescr relDescr,
                                            String expr,
                                            String value1,
                                            String value2,
                                            boolean isConstant) {

        final InternalReadAccessor extractor = getFieldReadAccessor( context, relDescr, pattern.getObjectType(), value1, null, true );
        return extractor != null && addConstraintToPattern(context, pattern, relDescr, expr, value1, value2, isConstant, extractor);
    }


    protected boolean addConstraintToPattern( final RuleBuildContext context,
                                                final Pattern pattern,
                                                final RelationalExprDescr relDescr,
                                                String expr,
                                                String value1,
                                                String value2,
                                                boolean isConstant,
                                                InternalReadAccessor extractor ) {

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
            FieldValue field = getFieldValue(context, vtype, restrictionDescr);
            if (field != null) {
                Constraint constraint = getConstraintBuilder( context ).buildLiteralConstraint(context, pattern, vtype, field, expr, value1, operator, value2, extractor, restrictionDescr);
                if (constraint != null) {
                    pattern.addConstraint(constraint);
                    return true;
                }
            }
        }

        Declaration declr = null;
        if ( value2.indexOf( '(' ) < 0 && value2.indexOf( '.' ) < 0 && value2.indexOf( '[' ) < 0 ) {
            declr = context.getDeclarationResolver().getDeclaration( context.getRule(), value2 );

            if ( declr == null ) {
                // trying to create implicit declaration
                final Pattern thisPattern = (Pattern) context.getBuildStack().peek();
                declr = createDeclarationObject( context, value2, thisPattern );
            }
        }

        Declaration[] declarations = null;
        if ( declr == null ) {
            String[] parts = value2.split( "\\." );
            if ( parts.length == 2 ) {
                if ( "this".equals( parts[0].trim() ) ) {
                    declr = createDeclarationObject(context, parts[1].trim(), (Pattern) context.getBuildStack().peek());
                    value2 = parts[1].trim();
                } else {
                    declr = context.getDeclarationResolver().getDeclaration( context.getRule(), parts[0].trim() );
                    // if a declaration exists, then it may be a variable direct property access
                    if ( declr != null ) {
                        if ( declr.isPatternDeclaration() ) {
                            // TODO: no need to extract inner declaration when using mvel constraint
                            declarations = new Declaration[] { declr };
                            declr = createDeclarationObject(context, parts[1].trim(), declr.getPattern());
                            value2 = parts[1].trim();

                        } else {
                            // we will later fallback to regular predicates, so don't raise error
                            return false;
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
                    return false;
                }
            }
        }

        pattern.addConstraint( getConstraintBuilder( context ).buildVariableConstraint(context, pattern, expr, declarations, value1, relDescr.getOperatorDescr(), value2, extractor, declr, relDescr) );
        return true;
    }

    private Declaration[] getDeclarationsForReturnValue(RuleBuildContext context, RelationalExprDescr relDescr, String operator, String value2) {
        Pattern pattern = (Pattern) context.getBuildStack().peek();
        ReturnValueRestrictionDescr returnValueRestrictionDescr = new ReturnValueRestrictionDescr( operator,
                                                                                                   relDescr.isNegated(),
                                                                                                   relDescr.getParametersText(),
                                                                                                   value2 );

        Map<String, Class< ? >> declarationsMap = getDeclarationsMap( returnValueRestrictionDescr,
                context,
                true );
        Class< ? > thisClass = null;
        if ( pattern.getObjectType() instanceof ClassObjectType ) {
            thisClass = ((ClassObjectType) pattern.getObjectType()).getClassType();
        }

        Map<String, Class< ? >> globals = context.getKnowledgeBuilder().getGlobals();
        AnalysisResult analysis = context.getDialect().analyzeExpression( context,
                returnValueRestrictionDescr,
                returnValueRestrictionDescr.getContent(),
                new BoundIdentifiers( declarationsMap,
                        globals,
                        null,
                        thisClass ) );
        if ( analysis == null ) {
            // something bad happened
            return null;
        }
        final BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();

        final List<Declaration> tupleDeclarations = new ArrayList<Declaration>();
        final List<Declaration> factDeclarations = new ArrayList<Declaration>();
        for ( String id : usedIdentifiers.getDeclrClasses().keySet() ) {
            final Declaration decl = context.getDeclarationResolver().getDeclaration( context.getRule(), id );
            if ( decl.getPattern() == pattern ) {
                factDeclarations.add( decl );
            } else {
                tupleDeclarations.add( decl );
            }
        }
        createImplicitBindings( context,
                pattern,
                analysis.getNotBoundedIdentifiers(),
                usedIdentifiers,
                factDeclarations );

        final Declaration[] previousDeclarations = tupleDeclarations.toArray( new Declaration[tupleDeclarations.size()] );
        final Declaration[] localDeclarations = factDeclarations.toArray( new Declaration[factDeclarations.size()] );

        Arrays.sort( previousDeclarations,
                SortDeclarations.instance );
        Arrays.sort( localDeclarations,
                SortDeclarations.instance );

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
            declarations[i++] = context.getDeclarationResolver().getDeclaration(context.getRule(), requiredGlobal);
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

    protected boolean processAtomicExpression( RuleBuildContext context,
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
                pdescr.copyLocation( d );
                buildEval( context, pattern, pdescr, aliases, expr, true );
                return true;
            }
        }
        return false;
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

    protected void createAndBuildPredicate( RuleBuildContext context,
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
        buildEval( context, pattern, pdescr, aliases, expr, false );

        // fall back to original dialect
        context.setDialect(dialect);
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
        pctx.addInput( "this",
                       thisClass );
        pctx.addInput( "empty",
                       boolean.class ); // overrides the mvel empty label
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
                                                                     pattern.getObjectType(),
                                                                     fieldBindingDescr.getBindingField(),
                                                                     declr,
                                                                     true );
        declr.setReadAccessor( extractor );

        if ( extractor instanceof ClassFieldReader ) {
            ObjectType patternType = pattern.getObjectType();
            if (patternType instanceof ClassObjectType) {
                Class<?> patternClass = ((ClassObjectType)patternType).getClassType();
                TypeDeclaration typeDeclaration = context.getKnowledgeBuilder().getTypeDeclaration(patternClass);

                String fieldName = (( ClassFieldReader) extractor ).getFieldName();
                if ( typeDeclaration.getSettableProperties().contains(fieldName) ) {
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
    protected void buildEval( final RuleBuildContext context,
                            final Pattern pattern,
                            final PredicateDescr predicateDescr,
                            final Map<String, OperatorDescr> aliases,
                            final String expr,
                            final boolean isEvalExpression) {

        AnalysisResult analysis = buildAnalysis(context, pattern, predicateDescr, aliases );

        if ( analysis == null ) {
            // something bad happened
            return;
        }

        Declaration[][] usedDeclarations = getUsedDeclarations(context, pattern, analysis);
        Declaration[] previousDeclarations = usedDeclarations[0];
        Declaration[] localDeclarations = usedDeclarations[1];

        BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();
        String[] requiredGlobals = usedIdentifiers.getGlobals().keySet().toArray( new String[usedIdentifiers.getGlobals().size()] );
        String[] requiredOperators = usedIdentifiers.getOperators().keySet().toArray( new String[usedIdentifiers.getOperators().size()] );

        Arrays.sort( previousDeclarations,
                     SortDeclarations.instance );
        Arrays.sort( localDeclarations,
                     SortDeclarations.instance );

        boolean isJavaEval = isEvalExpression && context.getDialect() instanceof JavaDialect;

        if (isJavaEval) {
            final PredicateConstraint predicateConstraint = new PredicateConstraint( null,
                    previousDeclarations,
                    localDeclarations,
                    requiredGlobals,
                    requiredOperators );

            final PredicateBuilder builder = context.getDialect().getPredicateBuilder();

            builder.build(context,
                    usedIdentifiers,
                    previousDeclarations,
                    localDeclarations,
                    predicateConstraint,
                    predicateDescr,
                    analysis);

            pattern.addConstraint( predicateConstraint );
        } else {
            MVELCompilationUnit compilationUnit = getConstraintBuilder( context ).buildCompilationUnit(context,
                    previousDeclarations,
                    localDeclarations,
                    predicateDescr,
                    analysis);

            Declaration[] mvelDeclarations = new Declaration[previousDeclarations.length + localDeclarations.length + requiredGlobals.length];
            int i = 0;
            for (Declaration d : previousDeclarations) {
                mvelDeclarations[i++] = d;
            }
            for (Declaration d : localDeclarations) {
                mvelDeclarations[i++] = d;
            }
            for (String global : requiredGlobals) {
                mvelDeclarations[i++] = context.getDeclarationResolver().getDeclaration(context.getRule(), global);
            }

            boolean isDynamic = requiredOperators.length > 0 ||
                    ClassObjectType.Match_ObjectType.isAssignableFrom( pattern.getObjectType()) ||
                    (!((ClassObjectType)pattern.getObjectType()).getClassType().isArray() &&
                    !context.getKnowledgeBuilder().getTypeDeclaration(((ClassObjectType)pattern.getObjectType()).getClassType()).isTypesafe());

            Constraint constraint = getConstraintBuilder( context ).buildMvelConstraint( context.getPkg().getName(), expr, mvelDeclarations, compilationUnit, isDynamic );
            pattern.addConstraint( constraint );
        }
    }

    public static Declaration[][] getUsedDeclarations(RuleBuildContext context, Pattern pattern, AnalysisResult analysis) {
        BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();
        final List<Declaration> tupleDeclarations = new ArrayList<Declaration>();
        final List<Declaration> factDeclarations = new ArrayList<Declaration>();

        for ( String id : usedIdentifiers.getDeclrClasses().keySet() ) {
            final Declaration decl = context.getDeclarationResolver().getDeclaration( context.getRule(), id );
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
        Map<String, Class< ? >> declarations = getDeclarationsMap( predicateDescr, context, true );
        Map<String, Class< ? >> globals = context.getKnowledgeBuilder().getGlobals();
        Map<String, EvaluatorWrapper> operators = aliases == null ? new HashMap<String, EvaluatorWrapper>() : buildOperators(context, pattern, predicateDescr, aliases);

        Class< ? > thisClass = null;
        if ( pattern.getObjectType() instanceof ClassObjectType) {
            thisClass = ((ClassObjectType) pattern.getObjectType()).getClassType();
        }

        return context.getDialect().analyzeExpression( context,
                                                        predicateDescr,
                                                        predicateDescr.getContent(),
                                                        new BoundIdentifiers( declarations,
                                                                              globals,
                                                                              operators,
                                                                              thisClass ) );
    }

    protected static Map<String, EvaluatorWrapper> buildOperators(RuleBuildContext context,
                                                                  Pattern pattern,
                                                                  PredicateDescr predicateDescr,
                                                                  Map<String, OperatorDescr> aliases) {
        Map<String, EvaluatorWrapper> operators = new HashMap<String, EvaluatorWrapper>();
        for ( Map.Entry<String, OperatorDescr> entry : aliases.entrySet() ) {
            OperatorDescr op = entry.getValue();

            Declaration leftDecl = createDeclarationForOperator(context, pattern, op.getLeftString());
            Declaration rightDecl = createDeclarationForOperator(context, pattern, op.getRightString());

            Target left = leftDecl != null && leftDecl.isPatternDeclaration() ? Target.HANDLE : Target.FACT;
            Target right = rightDecl != null && rightDecl.isPatternDeclaration() ? Target.HANDLE : Target.FACT;

            op.setLeftIsHandle( left == Target.HANDLE );
            op.setRightIsHandle( right == Target.HANDLE );

            Evaluator evaluator = getConstraintBuilder( context ).getEvaluator(
                    context,
                    predicateDescr,
                    ValueType.OBJECT_TYPE,
                    op.getOperator(),
                    false, // the rewrite takes care of negation
                    op.getParametersText(),
                    left,
                    right);
            EvaluatorWrapper wrapper = getConstraintBuilder( context ).wrapEvaluator( evaluator,
                                                                                      leftDecl,
                                                                                      rightDecl );
            operators.put( entry.getKey(),
                           wrapper );
        }
        return operators;
    }

    private static Declaration createDeclarationForOperator(RuleBuildContext context, Pattern pattern, String expr) {
        Declaration declaration = null;
        int dotPos = expr.indexOf('.');
        if (dotPos < 0) {
            if (!isIdentifier(expr)) {
                return null;
            }
            declaration = context.getDeclarationResolver().getDeclaration( context.getRule(), expr );
            if ( declaration == null) {
                if ( "this".equals( expr ) ) {
                    declaration = createDeclarationObject( context, "this", pattern );
                } else {
                    declaration = new Declaration("this", pattern);
                    try {
                        if ( context.getPkg().getTypeResolver().resolveType( expr ) != null ) {
                            return null;
                        }
                    } catch ( ClassNotFoundException e ) {}
                    context.getPkg().getClassFieldAccessorStore().getReader( ((ClassObjectType) pattern.getObjectType()).getClassName(), expr, declaration );
                }
            }
        } else {
            String part1 = expr.substring(0, dotPos).trim();
            String part2 = expr.substring(dotPos+1).trim();
            if ( "this".equals( part1 ) ) {
                declaration = createDeclarationObject(context, part2, (Pattern) context.getBuildStack().peek());
            } else {
                declaration = context.getDeclarationResolver().getDeclaration( context.getRule(), part1 );
                // if a declaration exists, then it may be a variable direct property access
                if ( declaration != null ) {
                    if ( declaration.isPatternDeclaration() ) {
                        declaration = createDeclarationObject(context, part2, declaration.getPattern());
                    } else {
                        declaration = null;
                    }
                }
            }
        }
        return declaration;
    }

    public static Map<String, Class< ? >> getDeclarationsMap( final BaseDescr baseDescr,
                                                                 final RuleBuildContext context,
                                                                 final boolean reportError ) {
        Map<String, Class< ? >> declarations = new HashMap<String, Class< ? >>();
        for ( Map.Entry<String, Declaration> entry : context.getDeclarationResolver().getDeclarations( context.getRule() ).entrySet() ) {
            if ( entry.getValue().getExtractor() == null ) {
                if ( reportError ) {
                    context.addError(new DescrBuildError(context.getParentDescr(),
                            baseDescr,
                            null,
                            "Field Reader does not exist for declaration '" + entry.getKey() + "' in '" + baseDescr + "' in the rule '" + context.getRule().getName() + "'"));
                }
                continue;
            }
            declarations.put( entry.getKey(),
                              entry.getValue().getExtractor().getExtractToClass() );
        }
        return declarations;
    }


    protected static ConstraintBuilder getConstraintBuilder( RuleBuildContext context ) {
        return context.getCompilerFactory().getConstraintBuilderFactoryService().newConstraintBuilder();
    }

    /**
     * @param context
     * @param pattern
     * @param unboundIdentifiers
     * @param factDeclarations
     */
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
                if ( boundIdentifiers.getDeclarations() == null ) {
                    boundIdentifiers.setDeclarations( new HashMap<String, Declaration>() );
                }
                boundIdentifiers.getDeclarations().put( identifier,
                                                        declaration );
                boundIdentifiers.getDeclrClasses().put( identifier,
                                                        declaration.getExtractor().getExtractToClass() );
                it.remove();
            }
        }
    }

    /**
     * Creates a declaration object for the field identified by the given identifier
     * on the give pattern object
     *
     * @param context
     * @param identifier
     * @param pattern
     * @return
     */
    protected static Declaration createDeclarationObject( final RuleBuildContext context,
                                                 final String identifier,
                                                 final Pattern pattern ) {
        return createDeclarationObject( context,
                                        identifier,
                                        identifier,
                                        pattern );

    }

    protected static Declaration createDeclarationObject( final RuleBuildContext context,
                                                 final String identifier,
                                                 final String expr,
                                                 final Pattern pattern ) {
        final BindingDescr implicitBinding = new BindingDescr( identifier,
                                                               expr );

        final Declaration declaration = new Declaration( identifier,
                                                         null,
                                                         pattern,
                                                         true );

        InternalReadAccessor extractor = getFieldReadAccessor( context,
                                                               implicitBinding,
                                                               pattern.getObjectType(),
                                                               implicitBinding.getExpression(),
                                                               declaration,
                                                               false );

        if ( extractor == null ) {
            return null;
        }

        declaration.setReadAccessor( extractor );

        return declaration;
    }

    protected FieldValue getFieldValue(RuleBuildContext context,
                                     ValueType vtype,
                                     LiteralRestrictionDescr literalRestrictionDescr) {
        FieldValue field = null;
        try {
            String value = literalRestrictionDescr.getText().trim();
            MVEL.COMPILER_OPT_ALLOW_NAKED_METH_CALL = true;
            MVEL.COMPILER_OPT_ALLOW_OVERRIDE_ALL_PROPHANDLING = true;
            MVEL.COMPILER_OPT_ALLOW_RESOLVE_INNERCLASSES_WITH_DOTNOTATION = true;
            MVEL.COMPILER_OPT_SUPPORT_JAVA_STYLE_CLASS_LITERALS = true;

            MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" );
            ParserConfiguration pconf = data.getParserConfiguration();
            ParserContext pctx = new ParserContext( pconf );

            Object o = MVELSafeHelper.getEvaluator().executeExpression( MVEL.compileExpression( value,
                    pctx ) );
            if ( o != null && vtype == null ) {
                // was a compilation problem else where, so guess valuetype so we can continue
                vtype = ValueType.determineValueType( o.getClass() );
            }

            field = context.getCompilerFactory().getFieldFactory().getFieldValue(o,
                                                                                 vtype,
                                                                                 context.getKnowledgeBuilder().getDateFormats());
        } catch ( final Exception e ) {
            // we will fallback to regular preducates, so don't raise an error
        }
        return field;
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
                                                             final ObjectType objectType,
                                                             final String fieldName,
                                                             final AcceptsReadAccessor target,
                                                             final boolean reportError ) {
        // reportError is needed as some times failure to build accessor is not a failure, just an indication that building is not possible so try something else.
        InternalReadAccessor reader;

        if ( ValueType.FACTTEMPLATE_TYPE.equals( objectType.getValueType() ) ) {
            //@todo use accessor cache            
            final FactTemplate factTemplate = ((FactTemplateObjectType) objectType).getFactTemplate();
            reader = new FactTemplateFieldExtractor( factTemplate,
                                                     factTemplate.getFieldTemplateIndex( fieldName ) );
            if ( target != null ) {
                target.setReadAccessor( reader );
            }
        } else if ( ! identifierRegexp.matcher( fieldName ).matches()
                    || ( fieldName.indexOf( '.' ) > -1 || fieldName.indexOf( '[' ) > -1 || fieldName.indexOf( '(' ) > -1 ) ) {
            // we need MVEL extractor for expressions
            Dialect dialect = context.getDialect();
            Map<String, Class< ? >> globals = context.getKnowledgeBuilder().getGlobals();

            try {
                MVELDialect mvelDialect = (MVELDialect) context.getDialect( "mvel" );
                context.setDialect( mvelDialect );

                Map<String, Class< ? >> declarations = getDeclarationsMap( descr,
                                                                           context,
                                                                           false );

                final AnalysisResult analysis = context.getDialect().analyzeExpression( context,
                                                                                        descr,
                                                                                        fieldName,
                                                                                        new BoundIdentifiers( declarations,
                                                                                                              globals,
                                                                                                              null,
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
                ((MVELCompileable) reader).compile( data );
                data.addCompileable( (MVELCompileable) reader );
            } catch ( final Exception e ) {
                int dotPos = fieldName.indexOf('.');
                String varName = dotPos > 0 ? fieldName.substring(0, dotPos).trim() : fieldName;
                if (globals.containsKey(varName)) {
                    return null;
                }

                if ( reportError ) {
                    DialectUtil.copyErrorLocation(e,
                            descr);
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
        } else {
            Declaration decl = context.getDeclarationResolver().getDeclarations( context.getRule() ).get(fieldName);
            if (decl != null && decl.getExtractor() instanceof ClassFieldReader && "this".equals(((ClassFieldReader)decl.getExtractor()).getFieldName())) {
                return decl.getExtractor();
            }

            boolean alternatives = false;
            try {
                Map<String, Class< ? >> declarations = getDeclarationsMap( descr, context, false );
                Map<String, Class< ? >> globals = context.getKnowledgeBuilder().getGlobals();
                alternatives = declarations.containsKey( fieldName ) || globals.containsKey( fieldName );

                reader = context.getPkg().getClassFieldAccessorStore().getReader( ((ClassObjectType) objectType).getClassName(),
                                                                                  fieldName,
                                                                                  target );
            } catch ( final Exception e ) {
                if ( reportError && ! alternatives && context.isTypesafe() ) {
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

                if ( reportError && ! alternatives ) {
                    Collection<KnowledgeBuilderResult> results = context.getPkg().getClassFieldAccessorStore().getWiringResults( ((ClassObjectType) objectType).getClassType(), fieldName );
                    if ( ! results.isEmpty() ) {
                        for ( KnowledgeBuilderResult res : results ) {
                            if ( res.getSeverity() == ResultSeverity.ERROR ) {
                                context.addError( new DroolsErrorWrapper( res ) );
                            } else {
                                context.addWarning( new DroolsWarningWrapper( res ) );
                            }
                        }
                    }
                }
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
        result.copyLocation( original );
        if ( parser.hasErrors() ) {
            for ( DroolsParserException error : parser.getErrors() ) {
                context.addError(new DescrBuildError(context.getParentDescr(),
                        patternDescr,
                        null,
                        "Unable to parser pattern expression:\n" + error.getMessage()));
            }
            return null;
        }
        return result;
    }
}
