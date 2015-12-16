/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.models.guided.dtree.backend;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.core.util.DateUtils;
import org.drools.workbench.models.commons.backend.imports.ImportsParser;
import org.drools.workbench.models.commons.backend.packages.PackageNameParser;
import org.drools.workbench.models.commons.backend.rule.RuleModelDRLPersistenceImpl;
import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.imports.Imports;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.oracle.OperatorsOracle;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.ActionInsertLogicalFact;
import org.drools.workbench.models.datamodel.rule.ActionRetractFact;
import org.drools.workbench.models.datamodel.rule.ActionSetField;
import org.drools.workbench.models.datamodel.rule.ActionUpdateField;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.CompositeFieldConstraint;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FieldConstraint;
import org.drools.workbench.models.datamodel.rule.FieldNatureType;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraintEBLeftSide;
import org.drools.workbench.models.datamodel.util.PortablePreconditions;
import org.drools.workbench.models.guided.dtree.shared.model.GuidedDecisionTree;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionFieldValue;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionInsertNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionRetractNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionUpdateNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ConstraintNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.Node;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.TypeNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.impl.ActionFieldValueImpl;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.impl.ActionInsertNodeImpl;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.impl.ActionRetractNodeImpl;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.impl.ActionUpdateNodeImpl;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.impl.ConstraintNodeImpl;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.impl.TypeNodeImpl;
import org.drools.workbench.models.guided.dtree.shared.model.parser.GuidedDecisionTreeParserError;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.AmbiguousRootParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.BindingNotFoundParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.DataTypeConversionErrorParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.DataTypeNotFoundParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.DefaultParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.InvalidRootParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.ParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.UnsupportedFieldConstraintParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.UnsupportedFieldConstraintTypeParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.UnsupportedFieldNatureTypeParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.UnsupportedIActionParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.parser.messages.UnsupportedIPatternParserMessage;
import org.drools.workbench.models.guided.dtree.shared.model.values.Value;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.BigDecimalValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.BigIntegerValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.BooleanValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.ByteValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.DateValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.DoubleValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.EnumValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.FloatValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.IntegerValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.LongValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.ShortValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.StringValue;

/**
 * Visitor that converts DRL into GuidedDecisionTree
 */
public class GuidedDecisionTreeModelUnmarshallingVisitor {

    public GuidedDecisionTree visit( final String drl,
                                     final String baseFileName,
                                     final PackageDataModelOracle dmo ) {
        return visit( drl,
                      baseFileName,
                      Collections.EMPTY_LIST,
                      dmo );
    }

    private GuidedDecisionTree visit( final String drl,
                                      final String baseFileName,
                                      final List<String> globals,
                                      final PackageDataModelOracle dmo ) {
        PortablePreconditions.checkNotNull( "drl",
                                            drl );
        PortablePreconditions.checkNotNull( "baseFileName",
                                            baseFileName );
        PortablePreconditions.checkNotNull( "globals",
                                            globals );
        PortablePreconditions.checkNotNull( "dmo",
                                            dmo );

        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName( baseFileName );

        //De-serialize Package name
        final String packageName = PackageNameParser.parsePackageName( drl );
        model.setPackageName( packageName );

        //De-serialize imports
        final Imports imports = ImportsParser.parseImports( drl );
        for ( Import item : imports.getImports() ) {
            model.getImports().addImport( item );
        }

        //Split DRL into separate rules
        final List<String> rules = new ArrayList<String>();
        final Pattern pattern = Pattern.compile( "\\s?rule\\s(.+?)\\send\\s?",
                                                 Pattern.DOTALL );
        final Matcher matcher = pattern.matcher( drl );
        while ( matcher.find() ) {
            rules.add( matcher.group() );
        }

        //Build a linear Path of Nodes for each rule
        final List<GuidedDecisionTreeParserExtendedError> rulesParserContent = new ArrayList<GuidedDecisionTreeParserExtendedError>();
        for ( String rule : rules ) {
            final GuidedDecisionTreeParserExtendedError ruleParserContent = new GuidedDecisionTreeParserExtendedError();
            rulesParserContent.add( ruleParserContent );

            try {

                final RuleModel rm = RuleModelDRLPersistenceImpl.getInstance().unmarshal( rule,
                                                                                          globals,
                                                                                          dmo );
                ruleParserContent.setOriginalRuleName( rm.name );
                ruleParserContent.setOriginalDrl( rule );

                for ( IPattern p : rm.lhs ) {
                    ruleParserContent.getNodes().addAll( visit( p,
                                                                model,
                                                                dmo,
                                                                ruleParserContent.getMessages() ) );
                }
                for ( IAction a : rm.rhs ) {
                    ruleParserContent.getNodes().addAll( visit( a,
                                                                getTypesOnPath( ruleParserContent.getNodes() ),
                                                                model,
                                                                dmo,
                                                                ruleParserContent.getMessages() ) );

                }

            } catch ( Exception e ) {
                ruleParserContent.getMessages().add( new DefaultParserMessage( e.getMessage() ) );

            }
        }

        //Combine Paths into a single tree.
        for ( GuidedDecisionTreeParserExtendedError ruleParserContent : rulesParserContent ) {
            Node activeModelNode = null;
            boolean error = !ruleParserContent.getMessages().isEmpty();
            for ( int index = 0; !error && index < ruleParserContent.getNodes().size(); index++ ) {
                final Node node = ruleParserContent.getNodes().get( index );
                switch ( index ) {
                    case 0:
                        if ( !( node instanceof TypeNode ) ) {
                            ruleParserContent.getMessages().add( new InvalidRootParserMessage() );
                            error = true;
                            break;
                        }
                        final TypeNode root = (TypeNode) node;
                        if ( model.getRoot() == null ) {
                            model.setRoot( root );
                        } else if ( !root.equals( model.getRoot() ) ) {
                            ruleParserContent.getMessages().add( new AmbiguousRootParserMessage( root.getClassName() ) );
                            error = true;
                            break;
                        }
                        activeModelNode = model.getRoot();
                        break;

                    default:
                        if ( !activeModelNode.getChildren().contains( node ) ) {
                            //If the active node in the Model doesn't contain the child add it as a new child
                            activeModelNode.addChild( node );
                            activeModelNode = node;
                        } else {
                            //Otherwise swap out the Node in the Path for the existing one in the Model
                            activeModelNode = activeModelNode.getChildren().get( activeModelNode.getChildren().indexOf( node ) );
                        }
                }
            }
            if ( !ruleParserContent.getMessages().isEmpty() ) {
                model.getParserErrors().add( new GuidedDecisionTreeParserError( ruleParserContent.getOriginalRuleName(),
                                                                                ruleParserContent.getOriginalDrl(),
                                                                                ruleParserContent.getMessages() ) );
            }
        }

        return model;
    }

    private List<Node> visit( final IPattern p,
                              final GuidedDecisionTree model,
                              final PackageDataModelOracle dmo,
                              final List<ParserMessage> messages ) {
        final List<Node> nodes = new ArrayList<Node>();
        if ( !( p instanceof FactPattern ) ) {
            messages.add( new UnsupportedIPatternParserMessage() );
            return nodes;
        }
        final FactPattern fp = (FactPattern) p;
        if ( fp.isNegated() ) {
            messages.add( new UnsupportedIPatternParserMessage() );
            return nodes;
        }
        if ( fp.getWindow().getOperator() != null ) {
            messages.add( new UnsupportedIPatternParserMessage() );
            return nodes;
        }
        final TypeNode node = new TypeNodeImpl( fp.getFactType() );
        if ( fp.isBound() ) {
            node.setBinding( fp.getBoundName() );
        }
        nodes.add( node );
        for ( FieldConstraint fc : fp.getFieldConstraints() ) {
            nodes.addAll( visit( fc,
                                 model,
                                 dmo,
                                 messages ) );
        }
        return nodes;
    }

    private List<Node> visit( final FieldConstraint fc,
                              final GuidedDecisionTree model,
                              final PackageDataModelOracle dmo,
                              final List<ParserMessage> messages ) {
        final List<Node> nodes = new ArrayList<Node>();
        if ( fc instanceof CompositeFieldConstraint ) {
            messages.add( new UnsupportedFieldConstraintParserMessage() );
            return nodes;
        } else if ( fc instanceof SingleFieldConstraintEBLeftSide ) {
            messages.add( new UnsupportedFieldConstraintParserMessage() );
            return nodes;
        }
        if ( !( fc instanceof SingleFieldConstraint ) ) {
            messages.add( new UnsupportedFieldConstraintParserMessage() );
            return nodes;
        }
        final SingleFieldConstraint sfc = (SingleFieldConstraint) fc;
        if ( sfc.getConnectives() != null ) {
            messages.add( new UnsupportedFieldConstraintParserMessage() );
            return nodes;
        }

        ConstraintNode node = null;
        final String className = sfc.getFactType();
        final String fieldName = sfc.getFieldName();

        if ( sfc.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_LITERAL ) {
            final String operator = sfc.getOperator();
            final boolean isValueRequired = OperatorsOracle.isValueRequired( operator );
            if ( isValueRequired ) {
                final Value value = getValue( className,
                                              fieldName,
                                              model,
                                              dmo,
                                              messages,
                                              sfc.getValue() );
                if ( value != null ) {
                    node = new ConstraintNodeImpl( className,
                                                   fieldName,
                                                   operator,
                                                   value );
                }
            } else {
                node = new ConstraintNodeImpl( className,
                                               fieldName );
            }

        } else if ( sfc.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_ENUM ) {
            final String operator = sfc.getOperator();
            final boolean isValueRequired = OperatorsOracle.isValueRequired( operator );
            if ( isValueRequired ) {
                final Value value = getValue( className,
                                              fieldName,
                                              model,
                                              dmo,
                                              messages,
                                              sfc.getValue() );
                if ( value != null ) {
                    node = new ConstraintNodeImpl( className,
                                                   fieldName,
                                                   operator,
                                                   value );
                }
            } else {
                node = new ConstraintNodeImpl( className,
                                               fieldName );
            }

        } else if ( sfc.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_UNDEFINED ) {
            final String operator = sfc.getOperator();
            final boolean isValueRequired = OperatorsOracle.isValueRequired( operator );
            if ( isValueRequired ) {
                node = new ConstraintNodeImpl( className,
                                               fieldName );
            } else {
                node = new ConstraintNodeImpl( className,
                                               fieldName,
                                               operator,
                                               null );
            }

        } else {
            messages.add( new UnsupportedFieldConstraintTypeParserMessage() );
            return nodes;
        }

        if ( node != null ) {
            if ( sfc.isBound() ) {
                node.setBinding( sfc.getFieldBinding() );
            }
            nodes.add( node );
        }

        return nodes;
    }

    private Value getValue( final String className,
                            final String fieldName,
                            final GuidedDecisionTree model,
                            final PackageDataModelOracle dmo,
                            final List<ParserMessage> messages,
                            final String value ) {
        final String dataType = getDataType( className,
                                             fieldName,
                                             model,
                                             dmo );
        if ( dataType == null ) {
            messages.add( new DataTypeNotFoundParserMessage( className,
                                                             fieldName ) );
            return null;
        }

        if ( DataType.TYPE_STRING.equals( dataType ) ) {
            String _value = value;
            if ( _value.startsWith( "\"" ) && _value.endsWith( "\"" ) ) {
                _value = value.substring( 1,
                                          _value.length() - 1 );
            }
            return new StringValue( new String( _value ) );

        } else if ( DataType.TYPE_NUMERIC.equals( dataType ) ) {
            try {
                String _value = value;
                if ( _value.endsWith( "B" ) ) {
                    _value = _value.substring( 0,
                                               _value.length() - 1 );
                }
                return new BigDecimalValue( new BigDecimal( _value ) );

            } catch ( NumberFormatException e ) {
                messages.add( new DataTypeConversionErrorParserMessage( value,
                                                                        BigDecimal.class.getName() ) );
                return null;
            }

        } else if ( DataType.TYPE_NUMERIC_BIGDECIMAL.equals( dataType ) ) {
            try {
                String _value = value;
                if ( _value.endsWith( "B" ) ) {
                    _value = _value.substring( 0,
                                               _value.length() - 1 );
                }
                return new BigDecimalValue( new BigDecimal( _value ) );

            } catch ( NumberFormatException e ) {
                messages.add( new DataTypeConversionErrorParserMessage( value,
                                                                        BigDecimal.class.getName() ) );
                return null;
            }

        } else if ( DataType.TYPE_NUMERIC_BIGINTEGER.equals( dataType ) ) {
            try {
                String _value = value;
                if ( _value.endsWith( "I" ) ) {
                    _value = _value.substring( 0,
                                               _value.length() - 1 );
                }
                return new BigIntegerValue( new BigInteger( _value ) );

            } catch ( NumberFormatException e ) {
                messages.add( new DataTypeConversionErrorParserMessage( value,
                                                                        BigInteger.class.getName() ) );
                return null;
            }

        } else if ( DataType.TYPE_NUMERIC_BYTE.equals( dataType ) ) {
            try {
                return new ByteValue( new Byte( value ) );

            } catch ( NumberFormatException e ) {
                messages.add( new DataTypeConversionErrorParserMessage( value,
                                                                        Byte.class.getName() ) );
                return null;
            }

        } else if ( DataType.TYPE_NUMERIC_DOUBLE.equals( dataType ) ) {
            try {
                return new DoubleValue( new Double( value ) );

            } catch ( NumberFormatException e ) {
                messages.add( new DataTypeConversionErrorParserMessage( value,
                                                                        Double.class.getName() ) );
                return null;
            }

        } else if ( DataType.TYPE_NUMERIC_FLOAT.equals( dataType ) ) {
            try {
                return new FloatValue( new Float( value ) );

            } catch ( NumberFormatException e ) {
                messages.add( new DataTypeConversionErrorParserMessage( value,
                                                                        Float.class.getName() ) );
                return null;
            }

        } else if ( DataType.TYPE_NUMERIC_INTEGER.equals( dataType ) ) {
            try {
                return new IntegerValue( new Integer( value ) );

            } catch ( NumberFormatException e ) {
                messages.add( new DataTypeConversionErrorParserMessage( value,
                                                                        Integer.class.getName() ) );
                return null;
            }

        } else if ( DataType.TYPE_NUMERIC_LONG.equals( dataType ) ) {
            try {
                return new LongValue( new Long( value ) );

            } catch ( NumberFormatException e ) {
                messages.add( new DataTypeConversionErrorParserMessage( value,
                                                                        Long.class.getName() ) );
                return null;
            }

        } else if ( DataType.TYPE_NUMERIC_SHORT.equals( dataType ) ) {
            try {
                return new ShortValue( new Short( value ) );

            } catch ( NumberFormatException e ) {
                messages.add( new DataTypeConversionErrorParserMessage( value,
                                                                        Short.class.getName() ) );
                return null;
            }

        } else if ( DataType.TYPE_BOOLEAN.equals( dataType ) ) {
            if ( value.equalsIgnoreCase( "true" ) || value.equalsIgnoreCase( "false" ) ) {
                return new BooleanValue( Boolean.parseBoolean( value ) );
            } else {
                messages.add( new DataTypeConversionErrorParserMessage( value,
                                                                        Boolean.class.getName() ) );
            }
            return null;

        } else if ( DataType.TYPE_DATE.equals( dataType ) ) {
            try {
                String _value = value;
                if ( _value.startsWith( "\"" ) && _value.endsWith( "\"" ) ) {
                    _value = value.substring( 1,
                                              _value.length() - 1 );
                }
                return new DateValue( DateUtils.parseDate( _value ) );

            } catch ( IllegalArgumentException e ) {
                messages.add( new DataTypeConversionErrorParserMessage( value,
                                                                        Date.class.getName() ) );
                return null;
            }

        } else if ( DataType.TYPE_COMPARABLE.equals( dataType ) ) {
            String _value = value;
            if ( _value.startsWith( "\"" ) && _value.endsWith( "\"" ) ) {
                _value = value.substring( 1,
                                          _value.length() - 1 );
            }
            return new EnumValue( new String( _value ) );
        }

        return null;
    }

    private String getDataType( final String className,
                                final String fieldName,
                                final GuidedDecisionTree model,
                                final PackageDataModelOracle dmo ) {
        //Assume className is within the same package as the decision tree
        String fqcn = ( model.getPackageName().isEmpty() ? className : model.getPackageName() + "." + className );

        //Check whether className is imported
        for ( Import i : model.getImports().getImports() ) {
            if ( i.getType().endsWith( className ) ) {
                fqcn = i.getType();
            }
        }
        for ( Map.Entry<String, ModelField[]> e : dmo.getProjectModelFields().entrySet() ) {
            if ( e.getKey().equals( fqcn ) ) {
                for ( ModelField mf : e.getValue() ) {
                    if ( mf.getName().equals( fieldName ) ) {
                        return mf.getType();
                    }
                }
            }
        }
        return null;
    }

    private List<Node> visit( final IAction a,
                              final List<TypeNode> types,
                              final GuidedDecisionTree model,
                              final PackageDataModelOracle dmo,
                              final List<ParserMessage> messages ) {
        final List<Node> nodes = new ArrayList<Node>();
        if ( a instanceof ActionRetractFact ) {
            final ActionRetractFact arf = (ActionRetractFact) a;
            final String binding = arf.getVariableName();
            for ( TypeNode tn : types ) {
                if ( tn.isBound() ) {
                    if ( tn.getBinding().equals( binding ) ) {
                        final ActionRetractNode arn = new ActionRetractNodeImpl( tn );
                        nodes.add( arn );
                        return nodes;
                    }
                }
            }
            messages.add( new BindingNotFoundParserMessage( binding ) );
            return nodes;

        } else if ( a instanceof ActionInsertLogicalFact ) {
            final ActionInsertLogicalFact aif = (ActionInsertLogicalFact) a;
            final ActionInsertNode aun = new ActionInsertNodeImpl( aif.getFactType() );
            aun.setLogicalInsertion( true );
            for ( org.drools.workbench.models.datamodel.rule.ActionFieldValue afv : aif.getFieldValues() ) {
                if ( afv.getNature() != FieldNatureType.TYPE_LITERAL ) {
                    messages.add( new UnsupportedFieldNatureTypeParserMessage() );
                    return nodes;
                }
                final String fieldName = afv.getField();
                final Value value = getValue( aif.getFactType(),
                                              afv.getField(),
                                              model,
                                              dmo,
                                              messages,
                                              afv.getValue() );
                if ( value != null ) {
                    final ActionFieldValue _afv = new ActionFieldValueImpl( fieldName,
                                                                            value );
                    aun.getFieldValues().add( _afv );
                }
            }
            nodes.add( aun );
            return nodes;

        } else if ( a instanceof ActionInsertFact ) {
            final ActionInsertFact aif = (ActionInsertFact) a;
            final ActionInsertNode aun = new ActionInsertNodeImpl( aif.getFactType() );
            aun.setLogicalInsertion( false );
            for ( org.drools.workbench.models.datamodel.rule.ActionFieldValue afv : aif.getFieldValues() ) {
                if ( afv.getNature() != FieldNatureType.TYPE_LITERAL ) {
                    messages.add( new UnsupportedFieldNatureTypeParserMessage() );
                    return nodes;
                }
                final String fieldName = afv.getField();
                final Value value = getValue( aif.getFactType(),
                                              afv.getField(),
                                              model,
                                              dmo,
                                              messages,
                                              afv.getValue() );
                if ( value != null ) {
                    final ActionFieldValue _afv = new ActionFieldValueImpl( fieldName,
                                                                            value );
                    aun.getFieldValues().add( _afv );
                }
            }
            nodes.add( aun );
            return nodes;

        } else if ( a instanceof ActionUpdateField ) {
            final ActionUpdateField auf = (ActionUpdateField) a;
            final String binding = auf.getVariable();
            for ( TypeNode tn : types ) {
                if ( tn.isBound() ) {
                    if ( tn.getBinding().equals( binding ) ) {
                        final ActionUpdateNode aun = new ActionUpdateNodeImpl( tn );
                        aun.setModify( true );
                        for ( org.drools.workbench.models.datamodel.rule.ActionFieldValue afv : auf.getFieldValues() ) {
                            if ( afv.getNature() != FieldNatureType.TYPE_LITERAL ) {
                                messages.add( new UnsupportedFieldNatureTypeParserMessage() );
                                return nodes;
                            }
                            final String fieldName = afv.getField();
                            final Value value = getValue( tn.getClassName(),
                                                          afv.getField(),
                                                          model,
                                                          dmo,
                                                          messages,
                                                          afv.getValue() );
                            if ( value != null ) {
                                final ActionFieldValue _afv = new ActionFieldValueImpl( fieldName,
                                                                                        value );
                                aun.getFieldValues().add( _afv );
                            }
                        }
                        nodes.add( aun );
                        return nodes;
                    }
                }
            }
            messages.add( new BindingNotFoundParserMessage( binding ) );
            return nodes;

        } else if ( a instanceof ActionSetField ) {
            final ActionSetField asf = (ActionSetField) a;
            final String binding = asf.getVariable();
            for ( TypeNode tn : types ) {
                if ( tn.isBound() ) {
                    if ( tn.getBinding().equals( binding ) ) {
                        final ActionUpdateNode aun = new ActionUpdateNodeImpl( tn );
                        for ( org.drools.workbench.models.datamodel.rule.ActionFieldValue afv : asf.getFieldValues() ) {
                            if ( afv.getNature() != FieldNatureType.TYPE_LITERAL ) {
                                messages.add( new UnsupportedFieldNatureTypeParserMessage() );
                                return nodes;
                            }
                            final String fieldName = afv.getField();
                            final Value value = getValue( tn.getClassName(),
                                                          afv.getField(),
                                                          model,
                                                          dmo,
                                                          messages,
                                                          afv.getValue() );
                            if ( value != null ) {
                                final ActionFieldValue _afv = new ActionFieldValueImpl( fieldName,
                                                                                        value );
                                aun.getFieldValues().add( _afv );
                            }
                        }
                        nodes.add( aun );
                        return nodes;
                    }
                }
            }
            messages.add( new BindingNotFoundParserMessage( binding ) );
            return nodes;

        } else {
            messages.add( new UnsupportedIActionParserMessage() );
            return nodes;
        }

    }

    private List<TypeNode> getTypesOnPath( final List<Node> nodes ) {
        final List<TypeNode> types = new ArrayList<TypeNode>();
        for ( Node node : nodes ) {
            if ( node instanceof TypeNode ) {
                final TypeNode tn = (TypeNode) node;
                types.add( tn );
            }
        }
        return types;
    }

    private static class GuidedDecisionTreeParserExtendedError extends GuidedDecisionTreeParserError {

        private List<Node> nodes = new ArrayList<Node>();

        private GuidedDecisionTreeParserExtendedError() {
            super( "",
                   "",
                   new ArrayList<ParserMessage>() );
        }

        private void setOriginalDrl( final String originalDrl ) {
            this.originalDrl = originalDrl;
        }

        private void setOriginalRuleName( final String originalRuleName ) {
            this.originalRuleName = originalRuleName;
        }

        private List<Node> getNodes() {
            return nodes;
        }
    }

}
