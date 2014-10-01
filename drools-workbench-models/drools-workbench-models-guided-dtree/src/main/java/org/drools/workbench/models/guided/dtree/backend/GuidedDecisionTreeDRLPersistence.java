/*
 * Copyright 2014 JBoss Inc
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
import java.util.List;
import java.util.Map;

import org.drools.core.util.DateUtils;
import org.drools.workbench.models.commons.backend.imports.ImportsParser;
import org.drools.workbench.models.commons.backend.imports.ImportsWriter;
import org.drools.workbench.models.commons.backend.packages.PackageNameParser;
import org.drools.workbench.models.commons.backend.packages.PackageNameWriter;
import org.drools.workbench.models.commons.backend.rule.RuleModelDRLPersistenceImpl;
import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.imports.Imports;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.ActionInsertLogicalFact;
import org.drools.workbench.models.datamodel.rule.ActionRetractFact;
import org.drools.workbench.models.datamodel.rule.ActionSetField;
import org.drools.workbench.models.datamodel.rule.ActionUpdateField;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
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
import org.drools.workbench.models.guided.dtree.shared.model.values.Value;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.BigDecimalValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.BigIntegerValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.BooleanValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.ByteValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.DateValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.DoubleValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.FloatValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.IntegerValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.LongValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.ShortValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.StringValue;

/**
 * This takes care of converting GuidedDecisionTree object to DRL
 */
public class GuidedDecisionTreeDRLPersistence {

    public static GuidedDecisionTreeDRLPersistence getInstance() {
        return new GuidedDecisionTreeDRLPersistence();
    }

    public String marshal( final GuidedDecisionTree model ) {
        final StringBuilder sb = new StringBuilder();

        //Append package name and imports to DRL
        PackageNameWriter.write( sb,
                                 model );
        ImportsWriter.write( sb,
                             model );

        //Marshall model
        final GuidedDecisionTreeModelDRLVisitor visitor = new GuidedDecisionTreeModelDRLVisitor();
        sb.append( visitor.visit( model ) );

        return sb.toString();
    }

    public GuidedDecisionTree unmarshal( final String drl,
                                         final String baseFileName,
                                         final PackageDataModelOracle dmo ) {
        return unmarshal( drl,
                          baseFileName,
                          Collections.EMPTY_LIST,
                          dmo );
    }

    private GuidedDecisionTree unmarshal( final String drl,
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
        final List<List<Node>> paths = new ArrayList<List<Node>>();
        final String[] lines = drl.split( System.getProperty( "line.separator" ) );

        StringBuilder sb = null;
        for ( String line : lines ) {
            if ( line.toLowerCase().startsWith( "rule" ) ) {
                sb = new StringBuilder();
            }
            if ( sb != null ) {
                sb.append( line ).append( "\n" );
            }
            if ( line.toLowerCase().startsWith( "end" ) ) {
                rules.add( sb.toString() );
                sb = null;
            }
        }

        //Build a linear Path of Nodes for each rule
        for ( String rule : rules ) {
            final List<Node> nodes = new ArrayList<Node>();
            final RuleModel rm = RuleModelDRLPersistenceImpl.getInstance().unmarshal( rule,
                                                                                      globals,
                                                                                      dmo );
            try {
                for ( IPattern p : rm.lhs ) {
                    nodes.addAll( processIPattern( p,
                                                   model,
                                                   dmo ) );
                }
                for ( IAction a : rm.rhs ) {
                    nodes.addAll( processIAction( a,
                                                  getTypesOnPath( nodes ),
                                                  model,
                                                  dmo ) );

                }
            } catch ( GuidedDecisionTreeDRLParserException e ) {
                System.out.println( e.getMessage() );
                //TODO log DRL and reason
            }

            paths.add( nodes );
        }

        //Combine Paths into a single tree.
        for ( List<Node> path : paths ) {
            try {
                Node activeModelNode = null;
                for ( int index = 0; index < path.size(); index++ ) {
                    final Node node = path.get( index );
                    switch ( index ) {
                        case 0:
                            if ( !( node instanceof TypeNode ) ) {
                                throw new GuidedDecisionTreeDRLParserException( "Decision Tree Root is not a TypeNode" );
                            }
                            final TypeNode root = (TypeNode) node;
                            if ( model.getRoot() == null ) {
                                model.setRoot( root );
                            } else if ( !root.equals( model.getRoot() ) ) {
                                throw new GuidedDecisionTreeDRLParserException( "Root of Rule is not equal to the Decision Tree root." );
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
            } catch ( GuidedDecisionTreeDRLParserException e ) {
                System.out.println( e.getMessage() );
                //TODO log DRL and reason
            }
        }

        return model;
    }

    private List<Node> processIPattern( final IPattern p,
                                        final GuidedDecisionTree model,
                                        final PackageDataModelOracle dmo ) throws GuidedDecisionTreeDRLParserException {
        final List<Node> nodes = new ArrayList<Node>();
        if ( !( p instanceof FactPattern ) ) {
            throw new GuidedDecisionTreeDRLParserException( "Can only process FactPatterns" );
        }
        final FactPattern fp = (FactPattern) p;
        if ( fp.isNegated() ) {
            throw new GuidedDecisionTreeDRLParserException( "Cannot process negated FactPatterns" );
        }
        if ( fp.getWindow().getOperator() != null ) {
            throw new GuidedDecisionTreeDRLParserException( "Cannot process FactPatterns Windows" );
        }
        final TypeNode node = new TypeNodeImpl( fp.getFactType() );
        if ( fp.isBound() ) {
            node.setBinding( fp.getBoundName() );
        }
        nodes.add( node );
        for ( FieldConstraint fc : fp.getFieldConstraints() ) {
            nodes.addAll( processFieldConstraint( fc,
                                                  model,
                                                  dmo ) );
        }
        return nodes;
    }

    private List<Node> processFieldConstraint( final FieldConstraint fc,
                                               final GuidedDecisionTree model,
                                               final PackageDataModelOracle dmo ) throws GuidedDecisionTreeDRLParserException {
        final List<Node> nodes = new ArrayList<Node>();
        if ( fc instanceof SingleFieldConstraintEBLeftSide ) {
            throw new GuidedDecisionTreeDRLParserException( "Can only process SingleFieldConstraints" );
        }
        if ( !( fc instanceof SingleFieldConstraint ) ) {
            throw new GuidedDecisionTreeDRLParserException( "Can only process SingleFieldConstraints" );
        }
        final SingleFieldConstraint sfc = (SingleFieldConstraint) fc;
        if ( sfc.getConnectives() != null ) {
            throw new GuidedDecisionTreeDRLParserException( "Cannot process ConnectiveConstraints" );
        }

        ConstraintNode node = null;
        final String className = sfc.getFactType();
        final String fieldName = sfc.getFieldName();

        if ( sfc.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_LITERAL ) {
            final String operator = sfc.getOperator();
            final Value value = getValue( className,
                                          fieldName,
                                          model,
                                          dmo,
                                          sfc.getValue() );

            node = new ConstraintNodeImpl( className,
                                           fieldName,
                                           operator,
                                           value );

        } else if ( sfc.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_ENUM ) {
            final String operator = sfc.getOperator();
            final Value value = getValue( className,
                                          fieldName,
                                          model,
                                          dmo,
                                          sfc.getValue() );

            node = new ConstraintNodeImpl( className,
                                           fieldName,
                                           operator,
                                           value );

        } else if ( sfc.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_UNDEFINED ) {
            node = new ConstraintNodeImpl( className,
                                           fieldName );

        } else {
            throw new GuidedDecisionTreeDRLParserException( "Cannot process FieldConstraint: ConstraintValueType is unsupported." );
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
                            final String value ) throws GuidedDecisionTreeDRLParserException {
        final String dataType = getDataType( className,
                                             fieldName,
                                             model,
                                             dmo );
        if ( dataType == null ) {
            throw new GuidedDecisionTreeDRLParserException( "Unable to determine data-type for: " + className + "." + fieldName );
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
                throw new GuidedDecisionTreeDRLParserException( "Unable to convert '" + value + "' to a BigDecimal" );
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
                throw new GuidedDecisionTreeDRLParserException( "Unable to convert '" + value + "' to a BigDecimal" );
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
                throw new GuidedDecisionTreeDRLParserException( "Unable to convert '" + value + "' to a BigInteger" );
            }

        } else if ( DataType.TYPE_NUMERIC_BYTE.equals( dataType ) ) {
            try {
                return new ByteValue( new Byte( value ) );

            } catch ( NumberFormatException e ) {
                throw new GuidedDecisionTreeDRLParserException( "Unable to convert '" + value + "' to a Byte" );
            }

        } else if ( DataType.TYPE_NUMERIC_DOUBLE.equals( dataType ) ) {
            try {
                return new DoubleValue( new Double( value ) );

            } catch ( NumberFormatException e ) {
                throw new GuidedDecisionTreeDRLParserException( "Unable to convert '" + value + "' to a Double" );
            }

        } else if ( DataType.TYPE_NUMERIC_FLOAT.equals( dataType ) ) {
            try {
                return new FloatValue( new Float( value ) );

            } catch ( NumberFormatException e ) {
                throw new GuidedDecisionTreeDRLParserException( "Unable to convert '" + value + "' to a Float" );
            }

        } else if ( DataType.TYPE_NUMERIC_INTEGER.equals( dataType ) ) {
            try {
                return new IntegerValue( new Integer( value ) );

            } catch ( NumberFormatException e ) {
                throw new GuidedDecisionTreeDRLParserException( "Unable to convert '" + value + "' to a Integer" );
            }

        } else if ( DataType.TYPE_NUMERIC_LONG.equals( dataType ) ) {
            try {
                return new LongValue( new Long( value ) );

            } catch ( NumberFormatException e ) {
                throw new GuidedDecisionTreeDRLParserException( "Unable to convert '" + value + "' to a Long" );
            }

        } else if ( DataType.TYPE_NUMERIC_SHORT.equals( dataType ) ) {
            try {
                return new ShortValue( new Short( value ) );

            } catch ( NumberFormatException e ) {
                throw new GuidedDecisionTreeDRLParserException( "Unable to convert '" + value + "' to a Short" );
            }

        } else if ( DataType.TYPE_BOOLEAN.equals( dataType ) ) {
            try {
                return new BooleanValue( Boolean.parseBoolean( value ) );

            } catch ( NumberFormatException e ) {
                throw new GuidedDecisionTreeDRLParserException( "Unable to convert '" + value + "' to a Boolean" );
            }

        } else if ( DataType.TYPE_DATE.equals( dataType ) ) {
            try {
                String _value = value;
                if ( _value.startsWith( "\"" ) && _value.endsWith( "\"" ) ) {
                    _value = value.substring( 1,
                                              _value.length() - 1 );
                }
                return new DateValue( DateUtils.parseDate( _value ) );

            } catch ( IllegalArgumentException e ) {
                throw new GuidedDecisionTreeDRLParserException( "Unable to convert '" + value + "' to a Date" );
            }

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

    private List<Node> processIAction( final IAction a,
                                       final List<TypeNode> types,
                                       final GuidedDecisionTree model,
                                       final PackageDataModelOracle dmo ) throws GuidedDecisionTreeDRLParserException {
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
            throw new GuidedDecisionTreeDRLParserException( "Cannot find a TypeNode for binding '" + binding + "'." );

        } else if ( a instanceof ActionInsertLogicalFact ) {
            final ActionInsertLogicalFact aif = (ActionInsertLogicalFact) a;
            final ActionInsertNode aun = new ActionInsertNodeImpl( aif.getFactType() );
            aun.setLogicalInsertion( true );
            for ( org.drools.workbench.models.datamodel.rule.ActionFieldValue afv : aif.getFieldValues() ) {
                if ( afv.getNature() != FieldNatureType.TYPE_LITERAL ) {
                    throw new GuidedDecisionTreeDRLParserException( "Cannot process ActionFieldValue: FieldNatureType is unsupported." );
                }
                final String fieldName = afv.getField();
                final Value value = getValue( aif.getFactType(),
                                              afv.getField(),
                                              model,
                                              dmo,
                                              afv.getValue() );
                final ActionFieldValue _afv = new ActionFieldValueImpl( fieldName,
                                                                        value );
                aun.getFieldValues().add( _afv );
            }
            nodes.add( aun );
            return nodes;

        } else if ( a instanceof ActionInsertFact ) {
            final ActionInsertFact aif = (ActionInsertFact) a;
            final ActionInsertNode aun = new ActionInsertNodeImpl( aif.getFactType() );
            aun.setLogicalInsertion( false );
            for ( org.drools.workbench.models.datamodel.rule.ActionFieldValue afv : aif.getFieldValues() ) {
                if ( afv.getNature() != FieldNatureType.TYPE_LITERAL ) {
                    throw new GuidedDecisionTreeDRLParserException( "Cannot process ActionFieldValue: FieldNatureType is unsupported." );
                }
                final String fieldName = afv.getField();
                final Value value = getValue( aif.getFactType(),
                                              afv.getField(),
                                              model,
                                              dmo,
                                              afv.getValue() );
                final ActionFieldValue _afv = new ActionFieldValueImpl( fieldName,
                                                                        value );
                aun.getFieldValues().add( _afv );
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
                                throw new GuidedDecisionTreeDRLParserException( "Cannot process ActionFieldValue: FieldNatureType is unsupported." );
                            }
                            final String fieldName = afv.getField();
                            final Value value = getValue( tn.getClassName(),
                                                          afv.getField(),
                                                          model,
                                                          dmo,
                                                          afv.getValue() );
                            final ActionFieldValue _afv = new ActionFieldValueImpl( fieldName,
                                                                                    value );
                            aun.getFieldValues().add( _afv );
                        }
                        nodes.add( aun );
                        return nodes;
                    }
                }
            }
            throw new GuidedDecisionTreeDRLParserException( "Cannot find a TypeNode for binding '" + binding + "'." );

        } else if ( a instanceof ActionSetField ) {
            final ActionSetField asf = (ActionSetField) a;
            final String binding = asf.getVariable();
            for ( TypeNode tn : types ) {
                if ( tn.isBound() ) {
                    if ( tn.getBinding().equals( binding ) ) {
                        final ActionUpdateNode aun = new ActionUpdateNodeImpl( tn );
                        for ( org.drools.workbench.models.datamodel.rule.ActionFieldValue afv : asf.getFieldValues() ) {
                            if ( afv.getNature() != FieldNatureType.TYPE_LITERAL ) {
                                throw new GuidedDecisionTreeDRLParserException( "Cannot process ActionFieldValue: FieldNatureType is unsupported." );
                            }
                            final String fieldName = afv.getField();
                            final Value value = getValue( tn.getClassName(),
                                                          afv.getField(),
                                                          model,
                                                          dmo,
                                                          afv.getValue() );
                            final ActionFieldValue _afv = new ActionFieldValueImpl( fieldName,
                                                                                    value );
                            aun.getFieldValues().add( _afv );
                        }
                        nodes.add( aun );
                        return nodes;
                    }
                }
            }
            throw new GuidedDecisionTreeDRLParserException( "Cannot find a TypeNode for binding '" + binding + "'." );

        } else {
            throw new GuidedDecisionTreeDRLParserException( "Can only process FactPatterns" );
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

    private static class GuidedDecisionTreeDRLParserException extends Exception {

        public GuidedDecisionTreeDRLParserException( final String message ) {
            super( message );
        }

    }

}
