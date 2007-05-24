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

package org.drools.rule.builder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.jci.utils.ClassUtils;
import org.drools.RuntimeDroolsException;
import org.drools.base.ClassObjectType;
import org.drools.base.FieldFactory;
import org.drools.base.ShadowProxyFactory;
import org.drools.base.ValueType;
import org.drools.base.evaluators.Operator;
import org.drools.compiler.RuleError;
import org.drools.facttemplates.FactTemplate;
import org.drools.facttemplates.FactTemplateFieldExtractor;
import org.drools.facttemplates.FactTemplateObjectType;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.FieldBindingDescr;
import org.drools.lang.descr.FieldConstraintDescr;
import org.drools.lang.descr.LiteralRestrictionDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.lang.descr.QualifiedIdentifierRestrictionDescr;
import org.drools.lang.descr.RestrictionConnectiveDescr;
import org.drools.lang.descr.RestrictionDescr;
import org.drools.lang.descr.ReturnValueRestrictionDescr;
import org.drools.lang.descr.VariableRestrictionDescr;
import org.drools.rule.AbstractCompositeConstraint;
import org.drools.rule.AbstractCompositeRestriction;
import org.drools.rule.AndCompositeRestriction;
import org.drools.rule.AndConstraint;
import org.drools.rule.Declaration;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.LiteralRestriction;
import org.drools.rule.MultiRestrictionFieldConstraint;
import org.drools.rule.OrCompositeRestriction;
import org.drools.rule.OrConstraint;
import org.drools.rule.Pattern;
import org.drools.rule.PredicateConstraint;
import org.drools.rule.ReturnValueConstraint;
import org.drools.rule.ReturnValueRestriction;
import org.drools.rule.VariableConstraint;
import org.drools.rule.VariableRestriction;
import org.drools.spi.Constraint;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldExtractor;
import org.drools.spi.FieldValue;
import org.drools.spi.ObjectType;
import org.drools.spi.Restriction;

/**
 * A builder for patterns
 * 
 * @author etirelli
 */
public class PatternBuilder {

    private Dialect dialect;

    public PatternBuilder(final Dialect dialect) {
        this.dialect = dialect;
    }

    /**
     * Build a pattern for the given descriptor in the current 
     * context and using the given utils object
     * 
     * @param context
     * @param utils
     * @param patternDescr
     * @return
     */
    public Pattern build(final RuleBuildContext context,
                         final PatternDescr patternDescr) {

        if ( patternDescr.getObjectType() == null || patternDescr.getObjectType().equals( "" ) ) {
            context.getErrors().add( new RuleError( context.getRule(),
                                                    patternDescr,
                                                    null,
                                                    "ObjectType not correctly defined" ) );
            return null;
        }

        ObjectType objectType = null;

        final FactTemplate factTemplate = context.getPkg().getFactTemplate( patternDescr.getObjectType() );

        if ( factTemplate != null ) {
            objectType = new FactTemplateObjectType( factTemplate );
        } else {
            try {
                final Class userProvidedClass = context.getDialect().getTypeResolver().resolveType( patternDescr.getObjectType() );
                final String shadowProxyName = ShadowProxyFactory.getProxyClassNameForClass( userProvidedClass );
                Class shadowClass = null;
                try {
                    // if already loaded
                    shadowClass = context.getPkg().getPackageCompilationData().getClassLoader().loadClass( shadowProxyName );
                } catch ( final ClassNotFoundException cnfe ) {
                    // otherwise, create and load
                    final byte[] proxyBytes = ShadowProxyFactory.getProxyBytes( userProvidedClass );
                    if ( proxyBytes != null ) {
                        context.getPkg().getPackageCompilationData().write( ClassUtils.convertClassToResourcePath( shadowProxyName ),
                                                                            proxyBytes );
                        shadowClass = context.getPkg().getPackageCompilationData().getClassLoader().loadClass( shadowProxyName );
                    }

                }
                objectType = new ClassObjectType( userProvidedClass,
                                                  shadowClass );
            } catch ( final ClassNotFoundException e ) {
                context.getErrors().add( new RuleError( context.getRule(),
                                                        patternDescr,
                                                        null,
                                                        "Unable to resolve ObjectType '" + patternDescr.getObjectType() + "'" ) );
                return null;
            }
        }

        Pattern pattern;
        if ( patternDescr.getIdentifier() != null && !patternDescr.getIdentifier().equals( "" ) ) {

            if ( context.getDeclarationResolver().isDuplicated( patternDescr.getIdentifier() ) ) {
                // This declaration already  exists, so throw an Exception
                context.getErrors().add( new RuleError( context.getRule(),
                                                        patternDescr,
                                                        null,
                                                        "Duplicate declaration for variable '" + patternDescr.getIdentifier() + "' in the rule '" + context.getRule().getName() + "'" ) );
            }

            pattern = new Pattern( context.getNextPatternId(),
                                   0, // offset is 0 by default
                                   objectType,
                                   patternDescr.getIdentifier() );
        } else {
            pattern = new Pattern( context.getNextPatternId(),
                                   0, // offset is 0 by default
                                   objectType,
                                   null );
        }
        // adding the newly created pattern to the build stack
        // this is necessary in case of local declaration usage
        context.getBuildStack().push( pattern );

        for ( final Iterator it = patternDescr.getDescrs().iterator(); it.hasNext(); ) {
            final Object object = it.next();
            buildConstraint( context,
                             pattern,
                             object,
                             null );
        }
        // poping the pattern
        context.getBuildStack().pop();
        return pattern;
    }

    private void buildConstraint(final RuleBuildContext context,
                                 final Pattern pattern,
                                 final Object constraint,
                                 final AbstractCompositeConstraint container) {
        if ( constraint instanceof FieldBindingDescr ) {
            build( context,
                   pattern,
                   (FieldBindingDescr) constraint );
        } else if ( constraint instanceof FieldConstraintDescr ) {
            build( context,
                   pattern,
                   (FieldConstraintDescr) constraint,
                   container );
        } else if ( constraint instanceof PredicateDescr ) {
            build( context,
                   pattern,
                   (PredicateDescr) constraint,
                   container );
        } else if ( constraint instanceof AndDescr ) {
            AndConstraint and = new AndConstraint();
            for ( Iterator it = ((AndDescr) constraint).getDescrs().iterator(); it.hasNext(); ) {
                this.buildConstraint( context,
                                      pattern,
                                      it.next(),
                                      and );
            }
            if ( container == null ) {
                pattern.addConstraint( and );
            } else {
                container.addConstraint( and );
            }
        } else if ( constraint instanceof OrDescr ) {
            OrConstraint or = new OrConstraint();
            for ( Iterator it = ((OrDescr) constraint).getDescrs().iterator(); it.hasNext(); ) {
                this.buildConstraint( context,
                                      pattern,
                                      it.next(),
                                      or );
            }
            if ( container == null ) {
                pattern.addConstraint( or );
            } else {
                container.addConstraint( or );
            }
        } else {
            throw new UnsupportedOperationException( "This is a bug: unable to build constraint descriptor: " + constraint );
        }
    }

    private void build(final RuleBuildContext context,
                       final Pattern pattern,
                       final FieldConstraintDescr fieldConstraintDescr,
                       final AbstractCompositeConstraint container) {

        final FieldExtractor extractor = getFieldExtractor( context,
                                                            fieldConstraintDescr,
                                                            pattern.getObjectType(),
                                                            fieldConstraintDescr.getFieldName(),
                                                            true );
        if ( extractor == null ) {
            // @todo log error
            return;
        }

        Restriction restriction = createRestriction( context,
                                                     pattern,
                                                     fieldConstraintDescr,
                                                     fieldConstraintDescr.getRestriction(),
                                                     extractor );

        if ( restriction == null ) {
            // @todo log error
            return;
        }

        Constraint constraint = null;
        if ( restriction instanceof AbstractCompositeRestriction ) {
            constraint = new MultiRestrictionFieldConstraint( extractor,
                                                              restriction );
        } else if ( restriction instanceof LiteralRestriction ) {
            constraint = new LiteralConstraint( extractor,
                                                (LiteralRestriction) restriction );
        } else if ( restriction instanceof VariableRestriction ) {
            constraint = new VariableConstraint( extractor,
                                                 (VariableRestriction) restriction );
        } else if ( restriction instanceof ReturnValueRestriction ) {
            constraint = new ReturnValueConstraint( extractor,
                                                    (ReturnValueRestriction) restriction );
        } else {
            throw new UnsupportedOperationException( "Unknown restriction type: " + restriction.getClass() );

        }

        if ( container == null ) {
            pattern.addConstraint( constraint );
        } else {
            container.addConstraint( constraint );
        }
    }

    private Restriction createRestriction(final RuleBuildContext context,
                                          final Pattern pattern,
                                          final FieldConstraintDescr fieldConstraintDescr,
                                          final RestrictionConnectiveDescr top,
                                          final FieldExtractor extractor) {
        Restriction[] restrictions = new Restriction[top.getRestrictions().size()];
        int index = 0;

        for ( Iterator it = top.getRestrictions().iterator(); it.hasNext(); ) {
            RestrictionDescr restrictionDescr = (RestrictionDescr) it.next();

            if ( restrictionDescr instanceof RestrictionConnectiveDescr ) {
                restrictions[index++] = this.createRestriction( context,
                                                                pattern,
                                                                fieldConstraintDescr,
                                                                (RestrictionConnectiveDescr) restrictionDescr,
                                                                extractor );

            } else {
                restrictions[index++] = buildRestriction( context,
                                                          pattern,
                                                          extractor,
                                                          fieldConstraintDescr,
                                                          restrictionDescr );
            }
        }

        if ( restrictions.length > 1 ) {
            AbstractCompositeRestriction composite = null;
            if ( top.getConnective() == RestrictionConnectiveDescr.AND ) {
                composite = new AndCompositeRestriction( restrictions );
            } else if ( top.getConnective() == RestrictionConnectiveDescr.OR ) {
                composite = new OrCompositeRestriction( restrictions );
            } else {
                throw new UnsupportedOperationException( "Impossible to create a composite restriction for connective: " + top.getConnective() );
            }

            return composite;
        } else if ( restrictions.length == 1 ) {
            return restrictions[0];
        }
        throw new UnsupportedOperationException( "Trying to create a restriction for an empty restriction list" );
    }

    private void build(final RuleBuildContext context,
                       final Pattern pattern,
                       final FieldBindingDescr fieldBindingDescr) {

        if ( context.getDeclarationResolver().isDuplicated( fieldBindingDescr.getIdentifier() ) ) {
            // This declaration already  exists, so throw an Exception
            context.getErrors().add( new RuleError( context.getRule(),
                                                    fieldBindingDescr,
                                                    null,
                                                    "Duplicate declaration for variable '" + fieldBindingDescr.getIdentifier() + "' in the rule '" + context.getRule().getName() + "'" ) );
            return;
        }

        final FieldExtractor extractor = getFieldExtractor( context,
                                                            fieldBindingDescr,
                                                            pattern.getObjectType(),
                                                            fieldBindingDescr.getFieldName(),
                                                            true );
        if ( extractor == null ) {
            return;
        }

        pattern.addDeclaration( fieldBindingDescr.getIdentifier(),
                                extractor );
    }

    private void build(final RuleBuildContext context,
                       final Pattern pattern,
                       final PredicateDescr predicateDescr,
                       final AbstractCompositeConstraint container) {

        // this will return an array with 3 lists
        // where first list is from rule local variables
        // second list is from global variables
        // and third is for unbound variables
        final List[] usedIdentifiers = context.getDialect().getExpressionIdentifiers( context,
                                                                                      predicateDescr,
                                                                                      predicateDescr.getContent() );
        final List tupleDeclarations = new ArrayList();
        final List factDeclarations = new ArrayList();
        for ( int i = 0, size = usedIdentifiers[0].size(); i < size; i++ ) {
            final Declaration decl = context.getDeclarationResolver().getDeclaration( (String) usedIdentifiers[0].get( i ) );
            if ( decl.getPattern() == pattern ) {
                factDeclarations.add( decl );
            } else {
                tupleDeclarations.add( decl );
            }
        }
        final int NOT_BOUND_INDEX = usedIdentifiers.length - 1;
        this.createImplicitBindings( context,
                                     pattern,
                                     usedIdentifiers[NOT_BOUND_INDEX],
                                     factDeclarations );

        final Declaration[] previousDeclarations = (Declaration[]) tupleDeclarations.toArray( new Declaration[tupleDeclarations.size()] );
        final Declaration[] localDeclarations = (Declaration[]) factDeclarations.toArray( new Declaration[factDeclarations.size()] );
        final String[] requiredGlobals = (String[]) usedIdentifiers[1].toArray( new String[usedIdentifiers[1].size()] );

        final PredicateConstraint predicateConstraint = new PredicateConstraint( null,
                                                                                 previousDeclarations,
                                                                                 localDeclarations,
                                                                                 requiredGlobals );

        if ( container == null ) {
            pattern.addConstraint( predicateConstraint );
        } else {
            container.addConstraint( predicateConstraint );
        }

        final PredicateBuilder builder = this.dialect.getPredicateBuilder();

        builder.build( context,
                       usedIdentifiers,
                       previousDeclarations,
                       localDeclarations,
                       predicateConstraint,
                       predicateDescr );

    }

    /**
     * @param context
     * @param utils
     * @param pattern
     * @param usedIdentifiers
     * @param NOT_BOUND_INDEX
     * @param factDeclarations
     */
    private void createImplicitBindings(final RuleBuildContext context,
                                        final Pattern pattern,
                                        final List unboundIdentifiers,
                                        final List factDeclarations) {
        // the following will create the implicit bindings
        for ( int i = 0, size = unboundIdentifiers.size(); i < size; i++ ) {
            final String identifier = (String) unboundIdentifiers.get( i );

            Declaration declaration = this.createDeclarationObject( context,
                                                                    identifier,
                                                                    pattern );

            if ( declaration != null ) {
                factDeclarations.add( declaration );
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
    private Declaration createDeclarationObject(final RuleBuildContext context,
                                                final String identifier,
                                                final Pattern pattern) {
        final FieldBindingDescr implicitBinding = new FieldBindingDescr( identifier,
                                                                         identifier );

        final FieldExtractor extractor = getFieldExtractor( context,
                                                            implicitBinding,
                                                            pattern.getObjectType(),
                                                            implicitBinding.getFieldName(),
                                                            false );
        if ( extractor == null ) {
            return null;
        }

        final Declaration declaration = new Declaration( identifier,
                                                         extractor,
                                                         pattern );
        return declaration;
    }

    private Restriction buildRestriction(final RuleBuildContext context,
                                         final Pattern pattern,
                                         final FieldExtractor extractor,
                                         final FieldConstraintDescr fieldConstraintDescr,
                                         final RestrictionDescr restrictionDescr) {
        Restriction restriction = null;
        if ( restrictionDescr instanceof LiteralRestrictionDescr ) {
            restriction = buildRestriction( context,
                                            extractor,
                                            fieldConstraintDescr,
                                            (LiteralRestrictionDescr) restrictionDescr );
        } else if ( restrictionDescr instanceof QualifiedIdentifierRestrictionDescr ) {
            restriction = buildRestriction( context,
                                            extractor,
                                            fieldConstraintDescr,
                                            (QualifiedIdentifierRestrictionDescr) restrictionDescr );
        } else if ( restrictionDescr instanceof VariableRestrictionDescr ) {
            restriction = buildRestriction( context,
                                            extractor,
                                            fieldConstraintDescr,
                                            (VariableRestrictionDescr) restrictionDescr );
        } else if ( restrictionDescr instanceof ReturnValueRestrictionDescr ) {
            restriction = buildRestriction( context,
                                            pattern,
                                            extractor,
                                            fieldConstraintDescr,
                                            (ReturnValueRestrictionDescr) restrictionDescr );

        }

        return restriction;
    }

    private VariableRestriction buildRestriction(final RuleBuildContext context,
                                                 final FieldExtractor extractor,
                                                 final FieldConstraintDescr fieldConstraintDescr,
                                                 final VariableRestrictionDescr variableRestrictionDescr) {
        if ( variableRestrictionDescr.getIdentifier() == null || variableRestrictionDescr.getIdentifier().equals( "" ) ) {
            context.getErrors().add( new RuleError( context.getRule(),
                                                    variableRestrictionDescr,
                                                    null,
                                                    "Identifier not defined for binding field '" + fieldConstraintDescr.getFieldName() + "'" ) );
            return null;
        }

        final Declaration declaration = context.getDeclarationResolver().getDeclaration( variableRestrictionDescr.getIdentifier() );

        //        if ( declaration == null ) {
        //            build( context, th)
        //            // lazily create teh declaration
        //        }        

        if ( declaration == null ) {
            context.getErrors().add( new RuleError( context.getRule(),
                                                    variableRestrictionDescr,
                                                    null,
                                                    "Unable to return Declaration for identifier '" + variableRestrictionDescr.getIdentifier() + "'" ) );
            return null;
        }

        final Evaluator evaluator = getEvaluator( context,
                                                  variableRestrictionDescr,
                                                  extractor.getValueType(),
                                                  variableRestrictionDescr.getEvaluator() );
        if ( evaluator == null ) {
            return null;
        }

        return new VariableRestriction( extractor,
                                        declaration,
                                        evaluator );
    }

    private LiteralRestriction buildRestriction(final RuleBuildContext context,
                                                final FieldExtractor extractor,
                                                final FieldConstraintDescr fieldConstraintDescr,
                                                final LiteralRestrictionDescr literalRestrictionDescr) {
        FieldValue field = null;
        try {
            field = FieldFactory.getFieldValue( literalRestrictionDescr.getText(),
                                                extractor.getValueType() );
        } catch ( final Exception e ) {
            context.getErrors().add( new RuleError( context.getRule(),
                                                    literalRestrictionDescr,
                                                    e,
                                                    "Unable to create a Field value of type  '" + extractor.getValueType() + "' and value '" + literalRestrictionDescr.getText() + "'" ) );
        }

        if ( field == null ) {
            return null;
        }

        final Evaluator evaluator = getEvaluator( context,
                                                  literalRestrictionDescr,
                                                  extractor.getValueType(),
                                                  literalRestrictionDescr.getEvaluator() );
        if ( evaluator == null ) {
            return null;
        }

        return new LiteralRestriction( field,
                                       evaluator,
                                       extractor );
    }

    private Restriction buildRestriction(final RuleBuildContext context,
                                         final FieldExtractor extractor,
                                         final FieldConstraintDescr fieldConstraintDescr,
                                         final QualifiedIdentifierRestrictionDescr qiRestrictionDescr) {
        FieldValue field = null;
        final String[] parts = qiRestrictionDescr.getText().split( "\\." );

        // if only 2 parts, it may be a composed direct property access
        if ( parts.length == 2 ) {
            final Declaration decl = context.getDeclarationResolver().getDeclaration( parts[0] );
            // if a declaration exists, then it is a variable direct property access, not an enum
            if ( decl != null ) {
                if( decl.isPatternDeclaration() ) {
                    final Declaration implicit = this.createDeclarationObject( context,
                                                                               parts[1],
                                                                               decl.getPattern() );

                    final Evaluator evaluator = getEvaluator( context,
                                                              qiRestrictionDescr,
                                                              extractor.getValueType(),
                                                              qiRestrictionDescr.getEvaluator() );
                    if ( evaluator == null ) {
                        return null;
                    }

                    return new VariableRestriction( extractor,
                                                    implicit,
                                                    evaluator );
                } else {
                    context.getErrors().add( new RuleError( context.getRule(),
                                                            qiRestrictionDescr,
                                                            "",
                                                            "Not possible to directly access the property '"+parts[1]+"' of declaration '"+parts[0]+"' since it is not a pattern" ) );
                    return null;
                }
            }
        }

        final int lastDot = qiRestrictionDescr.getText().lastIndexOf( '.' );
        final String className = qiRestrictionDescr.getText().substring( 0,
                                                                         lastDot );
        final String fieldName = qiRestrictionDescr.getText().substring( lastDot + 1 );
        try {
            final Class staticClass = context.getDialect().getTypeResolver().resolveType( className );
            field = FieldFactory.getFieldValue( staticClass.getField( fieldName ).get( null ),
                                                extractor.getValueType() );
        } catch ( final ClassNotFoundException e ) {
            context.getErrors().add( new RuleError( context.getRule(),
                                                    qiRestrictionDescr,
                                                    e,
                                                    e.getMessage() ) );
        } catch ( final Exception e ) {
            context.getErrors().add( new RuleError( context.getRule(),
                                                    qiRestrictionDescr,
                                                    e,
                                                    "Unable to create a Field value of type  '" + extractor.getValueType() + "' and value '" + qiRestrictionDescr.getText() + "'" ) );
        }
        
        if( field == null ) {
            return null;
        }

        final Evaluator evaluator = getEvaluator( context,
                                                  qiRestrictionDescr,
                                                  extractor.getValueType(),
                                                  qiRestrictionDescr.getEvaluator() );
        if ( evaluator == null ) {
            return null;
        }

        return new LiteralRestriction( field,
                                       evaluator,
                                       extractor );
    }

    private ReturnValueRestriction buildRestriction(final RuleBuildContext context,
                                                    final Pattern pattern,
                                                    final FieldExtractor extractor,
                                                    final FieldConstraintDescr fieldConstraintDescr,
                                                    final ReturnValueRestrictionDescr returnValueRestrictionDescr) {
        final List[] usedIdentifiers = context.getDialect().getExpressionIdentifiers( context,
                                                                                      returnValueRestrictionDescr,
                                                                                      returnValueRestrictionDescr.getContent() );

        final List tupleDeclarations = new ArrayList();
        final List factDeclarations = new ArrayList();
        for ( int i = 0, size = usedIdentifiers[0].size(); i < size; i++ ) {
            final Declaration declaration = context.getDeclarationResolver().getDeclaration( (String) usedIdentifiers[0].get( i ) );
            if ( declaration.getPattern() == pattern ) {
                factDeclarations.add( declaration );
            } else {
                tupleDeclarations.add( declaration );
            }
        }

        final int NOT_BOUND_INDEX = usedIdentifiers.length - 1;
        createImplicitBindings( context,
                                pattern,
                                usedIdentifiers[NOT_BOUND_INDEX],
                                factDeclarations );

        final Evaluator evaluator = getEvaluator( context,
                                                  returnValueRestrictionDescr,
                                                  extractor.getValueType(),
                                                  returnValueRestrictionDescr.getEvaluator() );
        if ( evaluator == null ) {
            return null;
        }

        final Declaration[] previousDeclarations = (Declaration[]) tupleDeclarations.toArray( new Declaration[tupleDeclarations.size()] );
        final Declaration[] localDeclarations = (Declaration[]) factDeclarations.toArray( new Declaration[factDeclarations.size()] );
        final String[] requiredGlobals = (String[]) usedIdentifiers[1].toArray( new String[usedIdentifiers[1].size()] );
        final ReturnValueRestriction returnValueRestriction = new ReturnValueRestriction( extractor,
                                                                                          previousDeclarations,
                                                                                          localDeclarations,
                                                                                          requiredGlobals,
                                                                                          evaluator );

        final ReturnValueBuilder builder = this.dialect.getReturnValueBuilder();

        builder.build( context,
                       usedIdentifiers,
                       previousDeclarations,
                       localDeclarations,
                       returnValueRestriction,
                       returnValueRestrictionDescr );

        return returnValueRestriction;
    }

    private FieldExtractor getFieldExtractor(final RuleBuildContext context,
                                             final BaseDescr descr,
                                             final ObjectType objectType,
                                             final String fieldName,
                                             final boolean reportError) {
        FieldExtractor extractor = null;

        if ( objectType.getValueType() == ValueType.FACTTEMPLATE_TYPE ) {
            //@todo use extractor cache            
            final FactTemplate factTemplate = ((FactTemplateObjectType) objectType).getFactTemplate();
            extractor = new FactTemplateFieldExtractor( factTemplate,
                                                        factTemplate.getFieldTemplateIndex( fieldName ) );
        } else {
            try {
                ClassLoader classloader = context.getPkg().getPackageCompilationData().getClassLoader();
                extractor = context.getDialect().getClassFieldExtractorCache().getExtractor( ((ClassObjectType) objectType).getClassType(),
                                                                                             fieldName,
                                                                                             classloader );
            } catch ( final RuntimeDroolsException e ) {
                if ( reportError ) {
                    context.getErrors().add( new RuleError( context.getRule(),
                                                            descr,
                                                            e,
                                                            "Unable to create Field Extractor for '" + fieldName + "'" ) );
                }
            }
        }

        return extractor;
    }

    private Evaluator getEvaluator(final RuleBuildContext context,
                                   final BaseDescr descr,
                                   final ValueType valueType,
                                   final String evaluatorString) {

        final Evaluator evaluator = valueType.getEvaluator( Operator.determineOperator( evaluatorString ) );

        if ( evaluator == null ) {
            context.getErrors().add( new RuleError( context.getRule(),
                                                    descr,
                                                    null,
                                                    "Unable to determine the Evaluator for  '" + valueType + "' and '" + evaluatorString + "'" ) );
        }

        return evaluator;
    }

}
