/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.models.datamodel.rule.visitors;

import java.util.List;

import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.ExpressionCollection;
import org.drools.workbench.models.datamodel.rule.ExpressionCollectionIndex;
import org.drools.workbench.models.datamodel.rule.ExpressionField;
import org.drools.workbench.models.datamodel.rule.ExpressionFieldVariable;
import org.drools.workbench.models.datamodel.rule.ExpressionFormLine;
import org.drools.workbench.models.datamodel.rule.ExpressionGlobalVariable;
import org.drools.workbench.models.datamodel.rule.ExpressionMethod;
import org.drools.workbench.models.datamodel.rule.ExpressionMethodParameter;
import org.drools.workbench.models.datamodel.rule.ExpressionPart;
import org.drools.workbench.models.datamodel.rule.ExpressionText;
import org.drools.workbench.models.datamodel.rule.ExpressionUnboundFact;
import org.drools.workbench.models.datamodel.rule.ExpressionVariable;
import org.drools.workbench.models.datamodel.rule.ExpressionVisitor;
import org.drools.workbench.models.datamodel.rule.builder.DRLConstraintValueBuilder;

/**
 * A visitor that can emit a String representing the Expression
 */
public class ToStringExpressionVisitor implements
                                       ExpressionVisitor {

    private StringBuilder sb;
    private DRLConstraintValueBuilder constraintValueBuilder;
    private boolean first;

    public ToStringExpressionVisitor() {
        this( DRLConstraintValueBuilder.getBuilder( DRLConstraintValueBuilder.DEFAULT_DIALECT ) );
    }

    public ToStringExpressionVisitor( final DRLConstraintValueBuilder constraintValueBuilder ) {
        this.constraintValueBuilder = constraintValueBuilder;
    }

    public void visit( ExpressionPart part ) {
        if ( part == null ) {
            return;
        }
        sb = new StringBuilder();
        first = true;
        part.accept( this );
    }

    public void visit( ExpressionField part ) {
        if ( !first ) {
            sb.append( '.' );
        }
        sb.append( part.getName() );
        moveNext( part );
    }

    public void visit( ExpressionMethod part ) {
        if ( !first ) {
            sb.append( '.' );
        }
        sb.append( part.getName() )
                .append( '(' )
                .append( paramsToString( part.getOrderedParams() ) )
                .append( ')' );
        moveNext( part );
    }

    public void visit( ExpressionVariable part ) {
        if ( !first ) {
            sb.append( '.' );
        }
        sb.append( part.getName() );
        moveNext( part );
    }

    public void visit( ExpressionUnboundFact part ) {
        moveNext( part,
                  false );
    }

    public void visit( ExpressionGlobalVariable part ) {
        if ( !first ) {
            sb.append( '.' );
        }
        sb.append( part.getName() );
        moveNext( part );
    }

    public void visit( ExpressionCollection part ) {
        if ( !first ) {
            sb.append( '.' );
        }
        sb.append( part.getName() );
        moveNext( part );
    }

    public void visit( ExpressionCollectionIndex part ) {
        sb.append( '[' ).append( paramsToString( part.getOrderedParams() ) ).append( ']' );
        moveNext( part );
    }

    public void visit( ExpressionFieldVariable part ) {
        if ( !first ) {
            sb.append( '.' );
        }
        sb.append( part.getName() );
        moveNext( part );
    }

    public void visit( ExpressionText part ) {
        if ( !first ) {
            sb.append( '.' );
        }
        sb.append( part.getName() );
        moveNext( part );
    }

    public void visit( ExpressionMethodParameter part ) {
        if ( !first ) {
            sb.append( '.' );
        }
        constraintValueBuilder.buildLHSFieldValue( sb,
                                                   BaseSingleFieldConstraint.TYPE_LITERAL,
                                                   part.getClassType(),
                                                   part.getName() );
        moveNext( part );
    }

    public String getText() {
        return sb.toString();
    }

    private String paramsToString( List<ExpressionFormLine> params ) {
        if ( params.isEmpty() ) {
            return "";
        }
        StringBuilder strParams = new StringBuilder();
        for ( ExpressionFormLine param : params ) {
            ToStringExpressionVisitor visitor = new ToStringExpressionVisitor( constraintValueBuilder );
            visitor.visit( param.getRootExpression() );
            strParams.append( ", " ).append( visitor.getText() );
        }
        return strParams.substring( 2 );
    }

    private void moveNext( ExpressionPart exp ) {
        moveNext( exp,
                  true );
    }

    private void moveNext( ExpressionPart exp,
                           boolean resetFirst ) {
        if ( exp.getNext() != null ) {
            if ( resetFirst ) {
                first = false;
            }
            exp.getNext().accept( this );
        }
    }
}

