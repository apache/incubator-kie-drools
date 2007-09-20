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

import org.drools.RuntimeDroolsException;
import org.drools.base.ClassObjectType;
import org.drools.base.FieldFactory;
import org.drools.base.ValueType;
import org.drools.base.evaluators.Operator;
import org.drools.compiler.Dialect;
import org.drools.compiler.RuleError;
import org.drools.facttemplates.FactTemplate;
import org.drools.facttemplates.FactTemplateFieldExtractor;
import org.drools.facttemplates.FactTemplateObjectType;
import org.drools.lang.MVELDumper;
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
import org.drools.rule.PatternSource;
import org.drools.rule.PredicateConstraint;
import org.drools.rule.ReturnValueConstraint;
import org.drools.rule.ReturnValueRestriction;
import org.drools.rule.RuleConditionElement;
import org.drools.rule.VariableConstraint;
import org.drools.rule.VariableRestriction;
import org.drools.rule.builder.dialect.mvel.MVELDialect;
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
public class PatternBuilder
    implements
    RuleConditionBuilder {

    public PatternBuilder() {
    }

    public RuleConditionElement build(RuleBuildContext context,
                                      BaseDescr descr) {
        return this.build( context,
                           descr,
                           null );
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
    public RuleConditionElement build(RuleBuildContext context,
                                      BaseDescr descr,
                                      Pattern prefixPattern) {

        final PatternDescr patternDescr = (PatternDescr) descr;

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
                objectType = new ClassObjectType( userProvidedClass );
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
                                   patternDescr.getIdentifier(),
                                   patternDescr.isInternalFact());
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

        if ( patternDescr.getSource() != null ) {
            // we have a pattern source, so build it
            RuleConditionBuilder builder = context.getDialect().getBuilder( patternDescr.getSource().getClass() );

            PatternSource source = (PatternSource) builder.build( context,
                                                                  patternDescr.getSource() );

            pattern.setSource( source );
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
            context.getErrors().add( new RuleError( context.getRule(),
                                                    (BaseDescr) constraint,
                                                    null,
                                                    "This is a bug: unable to build constraint descriptor: '" + constraint + "' in rule '" + context.getRule().getName() + "'" ) );
        }
    }

    private void build(final RuleBuildContext context,
                       final Pattern pattern,
                       final FieldConstraintDescr fieldConstraintDescr,
                       final AbstractCompositeConstraint container) {
        String fieldName = fieldConstraintDescr.getFieldName();

        if ( fieldName.indexOf( '[' ) > -1 ) {
            rewriteToEval( context,
                           pattern,
                           fieldConstraintDescr,
                           container );

            // after building the predicate, we are done, so return
            return;
        }

        if ( fieldName.indexOf( '.' ) > -1 ) {
            // we have a composite field name
            String[] identifiers = fieldName.split( "\\." );
            if ( identifiers.length == 2 && ((pattern.getDeclaration() != null && identifiers[0].equals( pattern.getDeclaration().getIdentifier() )) || ("this".equals( identifiers[0] ))) ) {
                // we have a self reference, so, it is fine to do direct access
                fieldName = identifiers[1];
            } else {
                rewriteToEval( context,
                               pattern,
                               fieldConstraintDescr,
                               container );

                // after building the predicate, we are done, so return
                return;
            }
        }

        // if it is not a complex expression, just build a simple field constraint
        final FieldExtractor extractor = getFieldExtractor( context,
                                                            fieldConstraintDescr,
                                                            pattern.getObjectType(),
                                                            fieldName,
                                                            false );
        if ( extractor == null ) {
            if( fieldConstraintDescr.getFieldName().startsWith( "this." ) ) {
                // it may still be MVEL special syntax, like map key syntax, so try eval
                rewriteToEval( context,
                               pattern,
                               fieldConstraintDescr,
                               container );

                // after building the predicate, we are done, so return
                return;
            } else {
                context.getErrors().add( new RuleError( context.getRule(),
                                                        fieldConstraintDescr,
                                                        null,
                                                        "Unable to create Field Extractor for '" + fieldName + "' of '"+pattern.getObjectType().toString()+"' in rule '"+context.getRule().getName()+"'" ) );
                return;
            }
        }

        Restriction restriction = createRestriction( context,
                                                     pattern,
                                                     fieldConstraintDescr,
                                                     fieldConstraintDescr.getRestriction(),
                                                     extractor );

        if ( restriction == null ) {
            // error was already logged during restriction creation failure
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
            context.getErrors().add( new RuleError( context.getRule(),
                                                    fieldConstraintDescr,
                                                    null,
                                                    "This is a bug: Unkown restriction type '" + restriction.getClass() + "' for pattern '"+pattern.getObjectType().toString()+"' in rule '"+context.getRule().getName()+"'" ) );
        }

        if ( container == null ) {
            pattern.addConstraint( constraint );
        } else {
            container.addConstraint( constraint );
        }
    }

    private void rewriteToEval(final RuleBuildContext context,
                               final Pattern pattern,
                               final FieldConstraintDescr fieldConstraintDescr,
                               final AbstractCompositeConstraint container) {
        // it is a complex expression, so we need to turn it into an MVEL predicate
        Dialect dialect = context.getDialect();
        // switch to MVEL dialect
        MVELDialect mvelDialect = (MVELDialect) context.getDialect( "mvel" );
        boolean strictMode = mvelDialect.isStrictMode();
        mvelDialect.setStrictMode( false );

        context.setDialect( mvelDialect );

        PredicateDescr predicateDescr = new PredicateDescr();
        MVELDumper dumper = new MVELDumper();
        predicateDescr.setContent( dumper.dump( fieldConstraintDescr ) );

        build( context,
               pattern,
               predicateDescr,
               container );

        mvelDialect.setStrictMode( strictMode );
        // fall back to original dialect
        context.setDialect( dialect );
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
                restrictions[index] = buildRestriction( context,
                                                          pattern,
                                                          extractor,
                                                          fieldConstraintDescr,
                                                          restrictionDescr );
                if( restrictions[index] == null ) {
                    context.getErrors().add( new RuleError( context.getRule(),
                                                            fieldConstraintDescr,
                                                            null,
                                                            "Unable to create restriction '" + restrictionDescr.toString() + "' for field '"+ fieldConstraintDescr.getFieldName() +"' in the rule '" + context.getRule().getName() + "'" ) );
                }
                index++;
            }
        }

        if ( restrictions.length > 1 ) {
            AbstractCompositeRestriction composite = null;
            if ( top.getConnective() == RestrictionConnectiveDescr.AND ) {
                composite = new AndCompositeRestriction( restrictions );
            } else if ( top.getConnective() == RestrictionConnectiveDescr.OR ) {
                composite = new OrCompositeRestriction( restrictions );
            } else {
                context.getErrors().add( new RuleError( context.getRule(),
                                                        fieldConstraintDescr,
                                                        null,
                                                        "This is a bug: Impossible to create a composite restriction for connective: " + top.getConnective()+ "' for field '"+ fieldConstraintDescr.getFieldName() +"' in the rule '" + context.getRule().getName() + "'" ) );
            }

            return composite;
        } else if ( restrictions.length == 1 ) {
            return restrictions[0];
        }
        context.getErrors().add( new RuleError( context.getRule(),
                                                fieldConstraintDescr,
                                                null,
                                                "This is a bug: trying to create a restriction for an empty restriction list for field '"+ fieldConstraintDescr.getFieldName() +"' in the rule '" + context.getRule().getName() + "'" ) );
        return null;
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

        final Dialect.AnalysisResult analysis = context.getDialect().analyzeExpression( context,
                                                                                        predicateDescr,
                                                                                        predicateDescr.getContent() );

        if ( analysis == null ) {
            // something bad happened
            return;
        }

        // this will return an array with 2 lists
        // where first list is from rule local variables
        // second list is from global variables
        final List[] usedIdentifiers = analysis.getBoundIdentifiers();

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
        this.createImplicitBindings( context,
                                     pattern,
                                     analysis.getNotBoundedIdentifiers(),
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

        final PredicateBuilder builder = context.getDialect().getPredicateBuilder();

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

            Declaration declaration = createDeclarationObject( context,
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

        Declaration declaration = context.getDeclarationResolver().getDeclaration( variableRestrictionDescr.getIdentifier() );

        if ( declaration == null ) {
            // trying to create implicit declaration
            final Pattern thisPattern = (Pattern) context.getBuildStack().peek();
            final Declaration implicit = this.createDeclarationObject( context,
                                                                       variableRestrictionDescr.getIdentifier(),
                                                                       thisPattern );
            if ( implicit != null ) {
                declaration = implicit;
            } else {
                context.getErrors().add( new RuleError( context.getRule(),
                                                        variableRestrictionDescr,
                                                        null,
                                                        "Unable to return Declaration for identifier '" + variableRestrictionDescr.getIdentifier() + "'" ) );
                return null;
            }
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
            Declaration implicit = null;
            if ( "this".equals( parts[0] ) ) {
                implicit = this.createDeclarationObject( context,
                                                         parts[1],
                                                         (Pattern) context.getBuildStack().peek() );
            } else {
                final Declaration decl = context.getDeclarationResolver().getDeclaration( parts[0] );
                // if a declaration exists, then it may be a variable direct property access, not an enum
                if ( decl != null ) {
                    if ( decl.isPatternDeclaration() ) {
                        implicit = this.createDeclarationObject( context,
                                                                 parts[1],
                                                                 decl.getPattern() );

                    } else {
                        context.getErrors().add( new RuleError( context.getRule(),
                                                                qiRestrictionDescr,
                                                                "",
                                                                "Not possible to directly access the property '" + parts[1] + "' of declaration '" + parts[0] + "' since it is not a pattern" ) );
                        return null;
                    }
                }
            }
            
            if( implicit != null ) {
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
            // nothing to do, as it is not a class name with static field
        } catch ( final Exception e ) {
            context.getErrors().add( new RuleError( context.getRule(),
                                                    qiRestrictionDescr,
                                                    e,
                                                    "Unable to create a Field value of type  '" + extractor.getValueType() + "' and value '" + qiRestrictionDescr.getText() + "'" ) );
        }

        if ( field == null ) {
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
        Dialect.AnalysisResult analysis = context.getDialect().analyzeExpression( context,
                                                                                  returnValueRestrictionDescr,
                                                                                  returnValueRestrictionDescr.getContent() );
        final List[] usedIdentifiers = analysis.getBoundIdentifiers();

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

        createImplicitBindings( context,
                                pattern,
                                analysis.getNotBoundedIdentifiers(),
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

        final ReturnValueBuilder builder = context.getDialect().getReturnValueBuilder();

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
