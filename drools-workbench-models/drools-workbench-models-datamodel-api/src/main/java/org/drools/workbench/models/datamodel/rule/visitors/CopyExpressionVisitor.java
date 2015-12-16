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

import java.util.HashMap;
import java.util.Map;

import org.drools.workbench.models.datamodel.rule.ExpressionCollection;
import org.drools.workbench.models.datamodel.rule.ExpressionCollectionIndex;
import org.drools.workbench.models.datamodel.rule.ExpressionField;
import org.drools.workbench.models.datamodel.rule.ExpressionFieldVariable;
import org.drools.workbench.models.datamodel.rule.ExpressionFormLine;
import org.drools.workbench.models.datamodel.rule.ExpressionGlobalVariable;
import org.drools.workbench.models.datamodel.rule.ExpressionMethod;
import org.drools.workbench.models.datamodel.rule.ExpressionMethodParameter;
import org.drools.workbench.models.datamodel.rule.ExpressionMethodParameterDefinition;
import org.drools.workbench.models.datamodel.rule.ExpressionPart;
import org.drools.workbench.models.datamodel.rule.ExpressionText;
import org.drools.workbench.models.datamodel.rule.ExpressionUnboundFact;
import org.drools.workbench.models.datamodel.rule.ExpressionVariable;
import org.drools.workbench.models.datamodel.rule.ExpressionVisitor;

public class CopyExpressionVisitor implements ExpressionVisitor {

    private ExpressionPart root;
    private ExpressionPart curr;

    public CopyExpressionVisitor() {

    }

    public ExpressionPart copy( ExpressionPart part ) {
        root = null;
        curr = null;
        part.accept( this );
        return root;
    }

    public void visit( ExpressionPart part ) {
        throw new RuntimeException( "Can't copy an abstract class: " + ExpressionPart.class.getName() );
    }

    public void visit( ExpressionField part ) {
        add( new ExpressionField( part.getName(),
                                  part.getClassType(),
                                  part.getGenericType(),
                                  part.getParametricType() ) );
        moveNext( part );
    }

    public void visit( ExpressionMethod part ) {
        ExpressionMethod method = new ExpressionMethod( part.getName(),
                                                        part.getClassType(),
                                                        part.getGenericType(),
                                                        part.getParametricType() );
        copyMethodParams( part,
                          method );
        add( method );
        moveNext( part );
    }

    public void visit( ExpressionVariable part ) {
        add( new ExpressionVariable( part.getName(),
                                     part.getFactType() ) );
        moveNext( part );
    }

    public void visit( ExpressionFieldVariable part ) {
        add( new ExpressionFieldVariable( part.getName(),
                                          part.getClassType() ) );
        moveNext( part );
    }

    public void visit( ExpressionUnboundFact part ) {
        add( new ExpressionUnboundFact( part.getFactType() ) );
        moveNext( part );
    }

    public void visit( ExpressionCollection part ) {
        add( new ExpressionCollection( part.getName(),
                                       part.getClassType(),
                                       part.getGenericType(),
                                       part.getParametricType() ) );
        moveNext( part );
    }

    public void visit( ExpressionCollectionIndex part ) {
        ExpressionCollectionIndex method = new ExpressionCollectionIndex( part.getName(),
                                                                          part.getClassType(),
                                                                          part.getGenericType(),
                                                                          part.getParametricType() );
        copyMethodParams( part,
                          method );
        add( method );
        moveNext( part );
    }

    public void visit( ExpressionText part ) {
        add( new ExpressionText( part.getName(),
                                 part.getClassType(),
                                 part.getGenericType() ) );
        moveNext( part );
    }

    @Override
    public void visit( ExpressionMethodParameter part ) {
        add( new ExpressionMethodParameter( part.getName(),
                                            part.getClassType(),
                                            part.getGenericType() ) );
        moveNext( part );
    }

    public void visit( ExpressionGlobalVariable part ) {
        add( new ExpressionGlobalVariable( part.getName(),
                                           part.getClassType(),
                                           part.getGenericType(),
                                           part.getParametricType() ) );
        moveNext( part );

    }

    private void copyMethodParams( ExpressionMethod part,
                                   ExpressionMethod method ) {
        Map<ExpressionMethodParameterDefinition, ExpressionFormLine> params = new HashMap<ExpressionMethodParameterDefinition, ExpressionFormLine>();
        for ( Map.Entry<ExpressionMethodParameterDefinition, ExpressionFormLine> entry : part.getParams().entrySet() ) {
            params.put( new ExpressionMethodParameterDefinition( entry.getKey().getIndex(),
                                                                 entry.getKey().getDataType() ),
                        new ExpressionFormLine( entry.getValue() ) );
        }
        method.setParams( params );
    }

    private void moveNext( ExpressionPart ep ) {
        if ( ep.getNext() != null ) {
            ep.getNext().accept( this );
        }
    }

    private void add( ExpressionPart p ) {
        if ( root == null ) {
            root = p;
            curr = p;
        } else {
            curr.setNext( p );
            curr = p;
        }
    }

}
