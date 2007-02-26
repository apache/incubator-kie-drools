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

package org.drools.semantics.java.builder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.antlr.stringtemplate.StringTemplate;
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
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.ColumnDescr;
import org.drools.lang.descr.FieldBindingDescr;
import org.drools.lang.descr.FieldConstraintDescr;
import org.drools.lang.descr.LiteralRestrictionDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.lang.descr.RestrictionConnectiveDescr;
import org.drools.lang.descr.RestrictionDescr;
import org.drools.lang.descr.ReturnValueRestrictionDescr;
import org.drools.lang.descr.VariableRestrictionDescr;
import org.drools.rule.AndCompositeRestriction;
import org.drools.rule.Column;
import org.drools.rule.Declaration;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.LiteralRestriction;
import org.drools.rule.MultiRestrictionFieldConstraint;
import org.drools.rule.OrCompositeRestriction;
import org.drools.rule.PredicateConstraint;
import org.drools.rule.ReturnValueConstraint;
import org.drools.rule.ReturnValueRestriction;
import org.drools.rule.VariableConstraint;
import org.drools.rule.VariableRestriction;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldExtractor;
import org.drools.spi.FieldValue;
import org.drools.spi.ObjectType;
import org.drools.spi.Restriction;

/**
 * A builder for columns
 * 
 * @author etirelli
 */
public class ColumnBuilder {

    /**
     * Build a column for the given descriptor in the current 
     * context and using the given utils object
     * 
     * @param context
     * @param utils
     * @param columnDescr
     * @return
     */
    public Column build(final BuildContext context,
                        final BuildUtils utils,
                        final ColumnDescr columnDescr) {

        if ( columnDescr.getObjectType() == null || columnDescr.getObjectType().equals( "" ) ) {
            context.getErrors().add( new RuleError( context.getRule(),
                                                    columnDescr,
                                                    null,
                                                    "ObjectType not correctly defined" ) );
            return null;
        }

        ObjectType objectType = null;

        final FactTemplate factTemplate = context.getPkg().getFactTemplate( columnDescr.getObjectType() );

        if ( factTemplate != null ) {
            objectType = new FactTemplateObjectType( factTemplate );
        } else {
            try {
                Class userProvidedClass = utils.getTypeResolver().resolveType( columnDescr.getObjectType() );
                String shadowProxyName = ShadowProxyFactory.getProxyClassNameForClass( userProvidedClass );
                Class shadowClass = null;
                try {
                    // if already loaded
                    shadowClass = context.getPkg().getPackageCompilationData().getClassLoader().loadClass( shadowProxyName );
                } catch ( ClassNotFoundException cnfe ) {
                    // otherwise, create and load
                    byte[] proxyBytes = ShadowProxyFactory.getProxyBytes( userProvidedClass );
                    if ( proxyBytes != null ) {
                        context.getPkg().getPackageCompilationData().write( shadowProxyName,
                                                                            proxyBytes );
                        shadowClass = context.getPkg().getPackageCompilationData().getClassLoader().loadClass( shadowProxyName );
                    }

                }
                objectType = new ClassObjectType( userProvidedClass,
                                                  shadowClass );
            } catch ( final ClassNotFoundException e ) {
                context.getErrors().add( new RuleError( context.getRule(),
                                                        columnDescr,
                                                        null,
                                                        "Unable to resolve ObjectType '" + columnDescr.getObjectType() + "'" ) );
                return null;
            }
        }

        Column column;
        if ( columnDescr.getIdentifier() != null && !columnDescr.getIdentifier().equals( "" ) ) {

            if ( context.getDeclarationResolver().isDuplicated( columnDescr.getIdentifier() ) ) {
                // This declaration already  exists, so throw an Exception
                context.getErrors().add( new RuleError( context.getRule(),
                                                        columnDescr,
                                                        null,
                                                        "Duplicate declaration for variable '" + columnDescr.getIdentifier() + "' in the rule '" + context.getRule().getName() + "'" ) );
            }

            column = new Column( context.getNextColumnId(),
                                 0, // offset is 0 by default
                                 objectType,
                                 columnDescr.getIdentifier() );
        } else {
            column = new Column( context.getNextColumnId(),
                                 0, // offset is 0 by default
                                 objectType,
                                 null );
        }
        // adding the newly created column to the build stack
        // this is necessary in case of local declaration usage
        context.getBuildStack().push( column );

        for ( final Iterator it = columnDescr.getDescrs().iterator(); it.hasNext(); ) {
            final Object object = it.next();
            if ( object instanceof FieldBindingDescr ) {
                build( context,
                       utils,
                       column,
                       (FieldBindingDescr) object );
            } else if ( object instanceof FieldConstraintDescr ) {
                build( context,
                       utils,
                       column,
                       (FieldConstraintDescr) object );
            } else if ( object instanceof PredicateDescr ) {
                build( context,
                       utils,
                       column,
                       (PredicateDescr) object );
            }
        }
        // poping the column
        context.getBuildStack().pop();
        return column;
    }

    private void build(final BuildContext context,
                       final BuildUtils utils,
                       final Column column,
                       final FieldConstraintDescr fieldConstraintDescr) {

        final FieldExtractor extractor = getFieldExtractor( context,
                                                            utils,
                                                            fieldConstraintDescr,
                                                            column.getObjectType(),
                                                            fieldConstraintDescr.getFieldName() );
        if ( extractor == null ) {
            // @todo log error
            return;
        }

        if ( fieldConstraintDescr.getRestrictions().size() == 1 ) {
            final Object object = fieldConstraintDescr.getRestrictions().get( 0 );

            final Restriction restriction = buildRestriction( context,
                                                              utils,
                                                              column,
                                                              extractor,
                                                              fieldConstraintDescr,
                                                              (RestrictionDescr) object );
            if ( restriction == null ) {
                // @todo log errors
                return;
            }

            if ( object instanceof LiteralRestrictionDescr ) {
                column.addConstraint( new LiteralConstraint( extractor,
                                                             (LiteralRestriction) restriction ) );
            } else if ( object instanceof VariableRestrictionDescr ) {
                column.addConstraint( new VariableConstraint( extractor,
                                                              (VariableRestriction) restriction ) );
            } else if ( object instanceof ReturnValueRestrictionDescr ) {
                column.addConstraint( new ReturnValueConstraint( extractor,
                                                                 (ReturnValueRestriction) restriction ) );
            }

            return;
        }

        final List orList = new ArrayList();
        List andList = null;

        RestrictionDescr currentRestriction = null;
        RestrictionDescr previousRestriction = null;

        List currentList = null;
        List previousList = null;

        for ( final Iterator it = fieldConstraintDescr.getRestrictions().iterator(); it.hasNext(); ) {
            final Object object = it.next();

            // Process an and/or connective 
            if ( object instanceof RestrictionConnectiveDescr ) {

                // is the connective an 'and'?
                if ( ((RestrictionConnectiveDescr) object).getConnective() == RestrictionConnectiveDescr.AND ) {
                    // if andList is null, then we know its the first
                    if ( andList == null ) {
                        andList = new ArrayList();
                    }
                    previousList = currentList;
                    currentList = andList;
                } else {
                    previousList = currentList;
                    currentList = orList;
                }
            } else {
                Restriction restriction = null;
                if ( currentList != null ) {
                    // Are we are at the first operator? if so treat differently
                    if ( previousList == null ) {
                        restriction = buildRestriction( context,
                                                        utils,
                                                        column,
                                                        extractor,
                                                        fieldConstraintDescr,
                                                        previousRestriction );
                        if ( currentList == andList ) {
                            andList.add( restriction );
                        } else {
                            orList.add( restriction );
                        }
                    } else {
                        restriction = buildRestriction( context,
                                                        utils,
                                                        column,
                                                        extractor,
                                                        fieldConstraintDescr,
                                                        previousRestriction );

                        if ( previousList == andList && currentList == orList ) {
                            andList.add( restriction );
                            if ( andList.size() == 1 ) {
                                // Can't have an 'and' connective with one child, so add directly to the or list
                                orList.add( andList.get( 0 ) );
                            } else {
                                final Restriction restrictions = new AndCompositeRestriction( (Restriction[]) andList.toArray( new Restriction[andList.size()] ) );
                                orList.add( restrictions );
                            }
                            andList = null;
                        } else if ( previousList == andList && currentList == andList ) {
                            andList.add( restriction );
                        } else if ( previousList == orList && currentList == andList ) {
                            andList.add( restriction );
                        } else if ( previousList == orList && currentList == orList ) {
                            orList.add( restriction );
                        }
                    }
                }
            }
            previousRestriction = currentRestriction;
            currentRestriction = (RestrictionDescr) object;
        }

        final Restriction restriction = buildRestriction( context,
                                                          utils,
                                                          column,
                                                          extractor,
                                                          fieldConstraintDescr,
                                                          currentRestriction );
        currentList.add( restriction );

        Restriction restrictions = null;
        if ( currentList == andList && !orList.isEmpty() ) {
            // Check if it finished with an and, and process it
            if ( andList != null ) {
                if ( andList.size() == 1 ) {
                    // Can't have an 'and' connective with one child, so add directly to the or list
                    orList.add( andList.get( 0 ) );
                } else {
                    orList.add( new AndCompositeRestriction( (Restriction[]) andList.toArray( new Restriction[andList.size()] ) ) );
                }
                andList = null;
            }
        }

        if ( !orList.isEmpty() ) {
            restrictions = new OrCompositeRestriction( (Restriction[]) orList.toArray( new Restriction[orList.size()] ) );
        } else if ( andList != null && !andList.isEmpty() ) {
            restrictions = new AndCompositeRestriction( (Restriction[]) andList.toArray( new Restriction[andList.size()] ) );
        } else {
            // @todo throw error
        }

        column.addConstraint( new MultiRestrictionFieldConstraint( extractor,
                                                                   restrictions ) );
    }

    private void build(final BuildContext context,
                       final BuildUtils utils,
                       final Column column,
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
                                                            utils,
                                                            fieldBindingDescr,
                                                            column.getObjectType(),
                                                            fieldBindingDescr.getFieldName() );
        if ( extractor == null ) {
            return;
        }

        column.addDeclaration( fieldBindingDescr.getIdentifier(),
                               extractor );
    }

    private void build(final BuildContext context,
                       final BuildUtils utils,
                       final Column column,
                       final PredicateDescr predicateDescr) {
        final List[] usedIdentifiers = utils.getUsedIdentifiers( context,
                                                                 predicateDescr,
                                                                 predicateDescr.getText() );

        final List tupleDeclarations = new ArrayList();
        final List factDeclarations = new ArrayList();
        for ( int i = 0, size = usedIdentifiers[0].size(); i < size; i++ ) {
            Declaration decl = (Declaration) context.getDeclarationResolver().getDeclaration( (String) usedIdentifiers[0].get( i ) );
            if ( decl.getColumn() == column ) {
                factDeclarations.add( decl );
            } else {
                tupleDeclarations.add( decl );
            }
        }
        Declaration[] previousDeclarations = (Declaration[]) tupleDeclarations.toArray( new Declaration[tupleDeclarations.size()] );
        Declaration[] localDeclarations = (Declaration[]) factDeclarations.toArray( new Declaration[factDeclarations.size()] );

        final PredicateConstraint predicateConstraint = new PredicateConstraint( previousDeclarations,
                                                                                 localDeclarations );
        column.addConstraint( predicateConstraint );

        JavaPredicateBuilder builder = new JavaPredicateBuilder();

        builder.build( context,
                       utils,
                       usedIdentifiers,
                       previousDeclarations,
                       localDeclarations,
                       predicateConstraint,
                       predicateDescr );

    }

    private Restriction buildRestriction(final BuildContext context,
                                         final BuildUtils utils,
                                         final Column column,
                                         final FieldExtractor extractor,
                                         final FieldConstraintDescr fieldConstraintDescr,
                                         final RestrictionDescr restrictionDescr) {
        Restriction restriction = null;
        if ( restrictionDescr instanceof LiteralRestrictionDescr ) {
            restriction = buildRestriction( context,
                                            utils,
                                            extractor,
                                            fieldConstraintDescr,
                                            (LiteralRestrictionDescr) restrictionDescr );
        } else if ( restrictionDescr instanceof VariableRestrictionDescr ) {
            restriction = buildRestriction( context,
                                            extractor,
                                            fieldConstraintDescr,
                                            (VariableRestrictionDescr) restrictionDescr );
        } else if ( restrictionDescr instanceof ReturnValueRestrictionDescr ) {
            restriction = buildRestriction( context,
                                            utils,
                                            column,
                                            extractor,
                                            fieldConstraintDescr,
                                            (ReturnValueRestrictionDescr) restrictionDescr );

        }

        return restriction;
    }

    private VariableRestriction buildRestriction(final BuildContext context,
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

        final Declaration declaration = (Declaration) context.getDeclarationResolver().getDeclaration( variableRestrictionDescr.getIdentifier() );

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

    private LiteralRestriction buildRestriction(final BuildContext context,
                                                final BuildUtils utils,
                                                final FieldExtractor extractor,
                                                final FieldConstraintDescr fieldConstraintDescr,
                                                final LiteralRestrictionDescr literalRestrictionDescr) {
        FieldValue field = null;
        if ( literalRestrictionDescr.isStaticFieldValue() ) {
            final int lastDot = literalRestrictionDescr.getText().lastIndexOf( '.' );
            final String className = literalRestrictionDescr.getText().substring( 0,
                                                                                  lastDot );
            final String fieldName = literalRestrictionDescr.getText().substring( lastDot + 1 );
            try {
                final Class staticClass = utils.getTypeResolver().resolveType( className );
                field = FieldFactory.getFieldValue( staticClass.getField( fieldName ).get( null ).toString(),
                                                    extractor.getValueType() );
            } catch ( final ClassNotFoundException e ) {
                context.getErrors().add( new RuleError( context.getRule(),
                                                        literalRestrictionDescr,
                                                        e,
                                                        e.getMessage() ) );
            } catch ( final Exception e ) {
                context.getErrors().add( new RuleError( context.getRule(),
                                                        literalRestrictionDescr,
                                                        e,
                                                        "Unable to create a Field value of type  '" + extractor.getValueType() + "' and value '" + literalRestrictionDescr.getText() + "'" ) );
            }

        } else {
            try {
                field = FieldFactory.getFieldValue( literalRestrictionDescr.getText(),
                                                    extractor.getValueType() );
            } catch ( final Exception e ) {
                context.getErrors().add( new RuleError( context.getRule(),
                                                        literalRestrictionDescr,
                                                        e,
                                                        "Unable to create a Field value of type  '" + extractor.getValueType() + "' and value '" + literalRestrictionDescr.getText() + "'" ) );
            }
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

    private ReturnValueRestriction buildRestriction(final BuildContext context,
                                                    final BuildUtils utils,
                                                    final Column column,
                                                    final FieldExtractor extractor,
                                                    final FieldConstraintDescr fieldConstraintDescr,
                                                    final ReturnValueRestrictionDescr returnValueRestrictionDescr) {
        final List[] usedIdentifiers = utils.getUsedIdentifiers( context,
                                                                 returnValueRestrictionDescr,
                                                                 returnValueRestrictionDescr.getText() );

        final List tupleDeclarations = new ArrayList();
        final List factDeclarations = new ArrayList();
        for ( int i = 0, size = usedIdentifiers[0].size(); i < size; i++ ) {
            Declaration declaration = (Declaration) context.getDeclarationResolver().getDeclaration( (String) usedIdentifiers[0].get( i ) );
            if ( declaration.getColumn() == column ) {
                factDeclarations.add( declaration );
            } else {
                tupleDeclarations.add( declaration );
            }
        }

        final Evaluator evaluator = getEvaluator( context,
                                                  returnValueRestrictionDescr,
                                                  extractor.getValueType(),
                                                  returnValueRestrictionDescr.getEvaluator() );
        if ( evaluator == null ) {
            return null;
        }

        Declaration[] previousDeclarations = (Declaration[]) tupleDeclarations.toArray( new Declaration[tupleDeclarations.size()] );
        Declaration[] localDeclarations = (Declaration[]) factDeclarations.toArray( new Declaration[factDeclarations.size()] );
        final ReturnValueRestriction returnValueRestriction = new ReturnValueRestriction( extractor,
                                                                                          previousDeclarations,
                                                                                          localDeclarations,
                                                                                          evaluator );

        JavaReturnValueBuilder builder = new JavaReturnValueBuilder();

        builder.build( context,
                       utils,
                       usedIdentifiers,
                       previousDeclarations,
                       localDeclarations,
                       returnValueRestriction,
                       returnValueRestrictionDescr );

        return returnValueRestriction;
    }

    private FieldExtractor getFieldExtractor(final BuildContext context,
                                             final BuildUtils utils,
                                             final BaseDescr descr,
                                             final ObjectType objectType,
                                             final String fieldName) {
        FieldExtractor extractor = null;

        if ( objectType.getValueType() == ValueType.FACTTEMPLATE_TYPE ) {
            //@todo use extractor cache            
            final FactTemplate factTemplate = ((FactTemplateObjectType) objectType).getFactTemplate();
            extractor = new FactTemplateFieldExtractor( factTemplate,
                                                        factTemplate.getFieldTemplateIndex( fieldName ) );
        } else {
            try {
                extractor = utils.getClassFieldExtractorCache().getExtractor( ((ClassObjectType) objectType).getClassType(),
                                                                              fieldName );
            } catch ( final RuntimeDroolsException e ) {
                context.getErrors().add( new RuleError( context.getRule(),
                                                        descr,
                                                        e,
                                                        "Unable to create Field Extractor for '" + fieldName + "'" ) );
            }
        }

        return extractor;
    }

    private Evaluator getEvaluator(final BuildContext context,
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
