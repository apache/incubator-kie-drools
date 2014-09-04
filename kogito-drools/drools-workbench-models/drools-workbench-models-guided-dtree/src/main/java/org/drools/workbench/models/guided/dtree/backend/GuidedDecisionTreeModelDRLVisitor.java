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
import java.util.List;
import java.util.Locale;

import org.drools.core.util.DateUtils;
import org.drools.workbench.models.guided.dtree.shared.model.GuidedDecisionTree;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ConstraintNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.Node;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.TypeNode;
import org.drools.workbench.models.guided.dtree.shared.model.values.Value;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.DateValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.StringValue;

/**
 * Visitor that converts the GuidedDecisionTree into DRL
 */
public class GuidedDecisionTreeModelDRLVisitor {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat( DateUtils.getDateFormatMask(),
                                                                              Locale.ENGLISH );

    private int ruleCount;
    private StringBuilder rules = new StringBuilder();
    private String baseRuleName;

    public String visit( final GuidedDecisionTree model ) {
        if ( model == null ) {
            return "";
        }
        if ( model.getRoot() == null ) {
            return "";
        }

        baseRuleName = model.getTreeName();
        final List<Node> path = new ArrayList<Node>();

        visit( path,
               model.getRoot() );

        return rules.toString();
    }

    private void visit( final List<Node> path,
                        final Node node ) {
        path.add( node );

        //Terminal node; generate the DRL for this path through the tree
        if ( node.getChildren().size() == 0 ) {
            generateRuleDRL( path );
        }

        //Process children. Each child creates a new path through the tree
        for ( Node child : node.getChildren() ) {
            final List<Node> subPath = new ArrayList<Node>( path );
            visit( subPath,
                   child );
        }
    }

    protected void generateRuleDRL( final List<Node> path ) {
        Node context = null;
        final StringBuilder drl = new StringBuilder();
        drl.append( generateRuleHeaderDRL() );
        drl.append( "when\n" );
        for ( Node node : path ) {
            if ( node instanceof TypeNode ) {
                final TypeNode tn = (TypeNode) node;
                if ( context == null ) {
                    drl.append( tn.getClassName() ).append( "(" );
                } else if ( context instanceof ConstraintNode ) {
                    drl.append( ")\n" ).append( tn.getClassName() ).append( "(" );
                } else if ( context instanceof TypeNode ) {
                    drl.append( ")\n" ).append( tn.getClassName() ).append( "(" );
                }

            } else if ( node instanceof ConstraintNode ) {
                final ConstraintNode cn = (ConstraintNode) node;
                if ( context instanceof ConstraintNode ) {
                    drl.append( ", " );
                }
                drl.append( cn.getFieldName() ).append( " " ).append( cn.getOperator() ).append( " " ).append( generateValueDRL( cn.getValue() ) );

            }
            context = node;
        }
        drl.append( ")\n" ).append( "then\n" ).append( "end\n" );
        ruleCount++;
        rules.append( drl );
    }

    protected StringBuilder generateRuleHeaderDRL() {
        final StringBuilder sb = new StringBuilder();
        sb.append( "rule \"" ).append( baseRuleName ).append( "_" ).append( new Integer( ruleCount ).toString() ).append( "\"\n" );
        return sb;
    }

    protected StringBuilder generateValueDRL( final Value value ) {
        final StringBuilder sb = new StringBuilder();
        if ( value instanceof StringValue ) {
            final StringValue sv = (StringValue) value;
            return sb.append( "\"" ).append( sv.getValue() ).append( "\"" );
        } else if ( value instanceof DateValue ) {
            final DateValue dv = (DateValue) value;
            return sb.append( "\"" ).append( DATE_FORMAT.format( dv.getValue() ) ).append( "\"" );
        }
        return sb.append( value.getValue() );
    }

}
