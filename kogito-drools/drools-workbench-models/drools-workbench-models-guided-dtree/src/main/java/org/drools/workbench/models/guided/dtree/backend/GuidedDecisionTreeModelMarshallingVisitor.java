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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.drools.core.util.DateUtils;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.OperatorsOracle;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.builder.DRLConstraintValueBuilder;
import org.drools.workbench.models.datamodel.rule.builder.JavaDRLConstraintValueBuilder;
import org.drools.workbench.models.guided.dtree.shared.model.GuidedDecisionTree;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionFieldValue;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionInsertNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionRetractNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionUpdateNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ConstraintNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.HasFieldValues;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.Node;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.TypeNode;
import org.drools.workbench.models.guided.dtree.shared.model.parser.GuidedDecisionTreeParserError;
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

/**
 * Visitor that converts the GuidedDecisionTree into DRL
 */
public class GuidedDecisionTreeModelMarshallingVisitor {

    private static final String INDENTATION = "\t";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat( DateUtils.getDateFormatMask(),
                                                                              Locale.ENGLISH );

    private int ruleCount;
    private StringBuilder rules = new StringBuilder();
    private DRLConstraintValueBuilder constraintValueBuilder = new JavaDRLConstraintValueBuilder();
    private String baseRuleName;
    private int varCounter;

    public String visit( final GuidedDecisionTree model ) {
        if ( model == null ) {
            return "";
        }

        //Append the DRL generated from the model
        if ( model.getRoot() != null ) {
            baseRuleName = model.getTreeName();
            final List<Node> path = new ArrayList<Node>();

            visit( path,
                   model.getRoot() );
        }

        //Append the DRL stored as a result of parsing errors
        for ( GuidedDecisionTreeParserError error : model.getParserErrors() ) {
            rules.append( error.getOriginalDrl() ).append( "\n" );
        }

        return rules.toString();
    }

    private void visit( final List<Node> path,
                        final Node node ) {
        path.add( node );

        //Terminal node; generate the DRL for this path through the tree
        final Iterator<Node> itr = node.iterator();
        if ( !itr.hasNext() ) {
            generateRuleDRL( path );
        }

        //Process children. Each child creates a new path through the tree
        while ( itr.hasNext() ) {
            final Node child = itr.next();
            final List<Node> subPath = new ArrayList<Node>( path );
            visit( subPath,
                   child );
        }
    }

    protected void generateRuleDRL( final List<Node> path ) {
        Node context = null;
        final StringBuilder drl = new StringBuilder();
        final boolean hasDateFieldValue = hasDateFieldValue( path );
        this.varCounter = 0;

        drl.append( generateRuleHeaderDRL() );
        drl.append( INDENTATION ).append( "when \n" );

        for ( Node node : path ) {
            if ( node instanceof TypeNode ) {
                generateTypeNodeDRL( (TypeNode) node,
                                     context,
                                     drl );

            } else if ( node instanceof ConstraintNode ) {
                generateConstraintNodeDRL( (ConstraintNode) node,
                                           context,
                                           drl );

            } else if ( node instanceof ActionRetractNode ) {
                generateActionRetractNodeDRL( (ActionRetractNode) node,
                                              context,
                                              hasDateFieldValue,
                                              drl );

            } else if ( node instanceof ActionUpdateNode ) {
                generateActionUpdateNodeDRL( (ActionUpdateNode) node,
                                             context,
                                             hasDateFieldValue,
                                             drl );

            } else if ( node instanceof ActionInsertNode ) {
                generateActionInsertNodeDRL( (ActionInsertNode) node,
                                             context,
                                             hasDateFieldValue,
                                             drl );
            }
            context = node;
        }
        if ( context == null ) {
            //No previous context to close

        } else if ( context instanceof ConstraintNode ) {
            drl.append( ")\n" ).append( "then \n" ).append( "end\n" );

        } else if ( context instanceof TypeNode ) {
            drl.append( ")\n" ).append( "then \n" ).append( "end\n" );

        } else if ( context instanceof ActionRetractNode ) {
            drl.append( "end\n" );

        } else if ( context instanceof ActionUpdateNode ) {
            drl.append( "end\n" );

        } else if ( context instanceof ActionInsertNode ) {
            drl.append( "end\n" );
        }

        ruleCount++;
        rules.append( drl );
    }

    private boolean hasDateFieldValue( final List<Node> path ) {
        for ( Node node : path ) {
            if ( node instanceof HasFieldValues ) {
                final HasFieldValues hfv = (HasFieldValues) node;
                for ( ActionFieldValue afv : hfv.getFieldValues() ) {
                    if ( afv.getValue() instanceof DateValue ) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected StringBuilder generateRuleHeaderDRL() {
        final StringBuilder sb = new StringBuilder();
        sb.append( "rule \"" ).append( baseRuleName ).append( "_" ).append( new Integer( ruleCount ).toString() ).append( "\"\n" );
        return sb;
    }

    protected void generateTypeNodeDRL( final TypeNode tn,
                                        final Node context,
                                        final StringBuilder drl ) {
        if ( context == null ) {
            //No previous context to close

        } else if ( context instanceof ConstraintNode ) {
            drl.append( ")\n" );

        } else if ( context instanceof TypeNode ) {
            drl.append( ")\n" );
        }

        drl.append( INDENTATION ).append( INDENTATION );
        if ( tn.isBound() ) {
            drl.append( tn.getBinding() ).append( " : " );
        }
        drl.append( tn.getClassName() ).append( "(" );
    }

    protected void generateConstraintNodeDRL( final ConstraintNode cn,
                                              final Node context,
                                              final StringBuilder drl ) {
        if ( context instanceof ConstraintNode ) {
            drl.append( ", " );
        }
        if ( cn.getFieldName() != null ) {
            if ( cn.isBound() ) {
                drl.append( cn.getBinding() ).append( " : " );
            }
            drl.append( cn.getFieldName() );
            if ( cn.getOperator() != null ) {
                drl.append( " " ).append( cn.getOperator() );
                if ( cn.getValue() != null ) {
                    drl.append( " " ).append( generateLHSValueDRL( cn.getValue(),
                                                                   OperatorsOracle.operatorRequiresList( cn.getOperator() ) ) );
                }
            }
        }
    }

    protected void generateActionRetractNodeDRL( final ActionRetractNode an,
                                                 final Node context,
                                                 final boolean hasDateFieldValue,
                                                 final StringBuilder drl ) {
        if ( context == null ) {
            //No previous context to close

        } else if ( context instanceof ConstraintNode ) {
            drl.append( ")\n" ).append( INDENTATION ).append( "then \n" );
            if ( hasDateFieldValue ) {
                drl.append( INDENTATION ).append( INDENTATION ).append( "java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(\"" + DateUtils.getDateFormatMask() + "\");\n" );
            }

        } else if ( context instanceof TypeNode ) {
            drl.append( ")\n" ).append( INDENTATION ).append( "then \n" );
            if ( hasDateFieldValue ) {
                drl.append( INDENTATION ).append( INDENTATION ).append( "java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(\"" + DateUtils.getDateFormatMask() + "\");\n" );
            }
        }

        drl.append( INDENTATION ).append( INDENTATION ).append( "retract( " ).append( an.getBoundNode().getBinding() ).append( " );\n" );
    }

    protected void generateActionUpdateNodeDRL( final ActionUpdateNode an,
                                                final Node context,
                                                final boolean hasDateFieldValue,
                                                final StringBuilder drl ) {
        if ( context == null ) {
            //No previous context to close

        } else if ( context instanceof ConstraintNode ) {
            drl.append( ")\n" ).append( INDENTATION ).append( "then \n" );
            if ( hasDateFieldValue ) {
                drl.append( INDENTATION ).append( INDENTATION ).append( "java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(\"" + DateUtils.getDateFormatMask() + "\");\n" );
            }

        } else if ( context instanceof TypeNode ) {
            drl.append( ")\n" ).append( INDENTATION ).append( "then \n" );
            if ( hasDateFieldValue ) {
                drl.append( INDENTATION ).append( INDENTATION ).append( "java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(\"" + DateUtils.getDateFormatMask() + "\");\n" );
            }
        }

        if ( an.isModify() ) {
            generateActionModifyNodeDRL( an,
                                         drl );
        } else {
            generateActionSetNodeDRL( an,
                                      drl );
        }
    }

    protected void generateActionModifyNodeDRL( final ActionUpdateNode an,
                                                final StringBuilder drl ) {
        final Iterator<ActionFieldValue> itr = an.getFieldValues().iterator();
        if ( !itr.hasNext() ) {
            return;
        }
        drl.append( INDENTATION ).append( INDENTATION ).append( "modify( " ).append( an.getBoundNode().getBinding() ).append( " ) {\n" );
        while ( itr.hasNext() ) {
            final ActionFieldValue afv = itr.next();
            drl.append( INDENTATION ).append( INDENTATION ).append( INDENTATION );
            drl.append( "set" );
            drl.append( Character.toUpperCase( afv.getFieldName().charAt( 0 ) ) );
            drl.append( afv.getFieldName().substring( 1 ) );
            drl.append( "( " ).append( generateRHSValueDRL( afv.getValue() ) ).append( " )" );
            if ( itr.hasNext() ) {
                drl.append( ", " );
            }
            drl.append( "\n" );
        }
        drl.append( INDENTATION ).append( INDENTATION ).append( "}\n" );

    }

    protected void generateActionSetNodeDRL( final ActionUpdateNode an,
                                             final StringBuilder drl ) {
        final Iterator<ActionFieldValue> itr = an.getFieldValues().iterator();
        while ( itr.hasNext() ) {
            final ActionFieldValue afv = itr.next();
            drl.append( INDENTATION ).append( INDENTATION ).append( an.getBoundNode().getBinding() ).append( "." );
            drl.append( "set" );
            drl.append( Character.toUpperCase( afv.getFieldName().charAt( 0 ) ) );
            drl.append( afv.getFieldName().substring( 1 ) );
            drl.append( "( " ).append( generateRHSValueDRL( afv.getValue() ) ).append( " );\n" );
        }
    }

    protected void generateActionInsertNodeDRL( final ActionInsertNode an,
                                                final Node context,
                                                final boolean hasDateFieldValue,
                                                final StringBuilder drl ) {
        if ( context == null ) {
            //No previous context to close

        } else if ( context instanceof ConstraintNode ) {
            drl.append( ")\n" ).append( INDENTATION ).append( "then \n" );
            if ( hasDateFieldValue ) {
                drl.append( INDENTATION ).append( INDENTATION ).append( "java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(\"" + DateUtils.getDateFormatMask() + "\");\n" );
            }

        } else if ( context instanceof TypeNode ) {
            drl.append( ")\n" ).append( INDENTATION ).append( "then \n" );
            if ( hasDateFieldValue ) {
                drl.append( INDENTATION ).append( INDENTATION ).append( "java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(\"" + DateUtils.getDateFormatMask() + "\");\n" );
            }
        }

        final Iterator<ActionFieldValue> itr = an.getFieldValues().iterator();
        if ( !itr.hasNext() ) {
            return;
        }
        final String var = "$var" + ( varCounter++ );
        drl.append( INDENTATION ).append( INDENTATION ).append( an.getClassName() ).append( " " ).append( var ).append( " = new " ).append( an.getClassName() ).append( "();\n" );
        while ( itr.hasNext() ) {
            final ActionFieldValue afv = itr.next();
            drl.append( INDENTATION ).append( INDENTATION ).append( var ).append( "." );
            drl.append( "set" );
            drl.append( Character.toUpperCase( afv.getFieldName().charAt( 0 ) ) );
            drl.append( afv.getFieldName().substring( 1 ) );
            drl.append( "( " ).append( generateRHSValueDRL( afv.getValue() ) ).append( " );\n" );
        }

        if ( an.isLogicalInsertion() ) {
            drl.append( INDENTATION ).append( INDENTATION ).append( "insertLogical( " ).append( var ).append( " );\n" );
        } else {
            drl.append( INDENTATION ).append( INDENTATION ).append( "insert( " ).append( var ).append( " );\n" );
        }
    }

    protected StringBuilder generateRHSValueDRL( final Value value ) {
        final StringBuilder sb = new StringBuilder();
        final String dataType = getDataType( value );
        final String strValue = getStringValue( value );
        constraintValueBuilder.buildRHSFieldValue( sb,
                                                   dataType,
                                                   strValue );
        return sb;
    }

    protected StringBuilder generateLHSValueDRL( final Value value,
                                                 final boolean isMultiValue ) {
        final StringBuilder sb = new StringBuilder();
        final int constraintType = BaseSingleFieldConstraint.TYPE_LITERAL;
        final String dataType = getDataType( value );
        final String strValue = getStringValue( value );

        if ( isMultiValue ) {
            populateValueList( sb,
                               constraintType,
                               dataType,
                               strValue );
        } else {
            constraintValueBuilder.buildLHSFieldValue( sb,
                                                       constraintType,
                                                       dataType,
                                                       strValue );
        }
        return sb;
    }

    //Convert a typed Value into legacy DataType
    private String getDataType( final Value value ) {
        if ( value instanceof BigDecimalValue ) {
            return DataType.TYPE_NUMERIC_BIGDECIMAL;

        } else if ( value instanceof BigIntegerValue ) {
            return DataType.TYPE_NUMERIC_BIGINTEGER;

        } else if ( value instanceof BooleanValue ) {
            return DataType.TYPE_BOOLEAN;

        } else if ( value instanceof ByteValue ) {
            return DataType.TYPE_NUMERIC_BYTE;

        } else if ( value instanceof DateValue ) {
            return DataType.TYPE_DATE;

        } else if ( value instanceof DoubleValue ) {
            return DataType.TYPE_NUMERIC_DOUBLE;

        } else if ( value instanceof FloatValue ) {
            return DataType.TYPE_NUMERIC_FLOAT;

        } else if ( value instanceof IntegerValue ) {
            return DataType.TYPE_NUMERIC_INTEGER;

        } else if ( value instanceof LongValue ) {
            return DataType.TYPE_NUMERIC_LONG;

        } else if ( value instanceof ShortValue ) {
            return DataType.TYPE_NUMERIC_SHORT;
        }

        return DataType.TYPE_STRING;
    }

    private String getStringValue( final Value value ) {
        if ( value instanceof DateValue ) {
            final DateValue dv = (DateValue) value;
            return DATE_FORMAT.format( dv.getValue() );
        } else if ( value.getValue() != null ) {
            return value.getValue().toString();
        } else {
            return "";
        }
    }

    private void populateValueList( final StringBuilder buf,
                                    final int constraintType,
                                    final String dataType,
                                    final String strValue ) {
        //Remove braces and convert to an Array of individual values
        String workingValue = strValue.trim();
        if ( workingValue.startsWith( "(" ) ) {
            workingValue = workingValue.substring( 1 );
        }
        if ( workingValue.endsWith( ")" ) ) {
            workingValue = workingValue.substring( 0,
                                                   workingValue.length() - 1 );
        }
        final String[] values = workingValue.split( "," );

        //Construct list syntax
        buf.append( " ( " );
        for ( String v : values ) {
            v = v.trim();
            if ( v.startsWith( "\"" ) ) {
                v = v.substring( 1 );
            }
            if ( v.endsWith( "\"" ) ) {
                v = v.substring( 0,
                                 v.length() - 1 );
            }
            constraintValueBuilder.buildLHSFieldValue( buf,
                                                       constraintType,
                                                       dataType,
                                                       v );
            buf.append( ", " );
        }
        buf.delete( buf.length() - 2,
                    buf.length() );
        buf.append( " )" );
    }

}
