/*
 * Copyright 2012 JBoss Inc
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

package org.drools.workbench.models.datamodel.rule;

import java.util.LinkedList;
import java.util.List;

import org.drools.workbench.models.datamodel.rule.visitors.CopyExpressionVisitor;
import org.drools.workbench.models.datamodel.rule.visitors.ToStringExpressionVisitor;

public class ExpressionFormLine
        implements
        IAction,
        IPattern {

    private String binding = null;
    private LinkedList<ExpressionPart> parts = new LinkedList<ExpressionPart>();
    private int index = Integer.MAX_VALUE;

    public ExpressionFormLine() {
    }

    public ExpressionFormLine( final int index ) {
        this.index = index;
    }

    public ExpressionFormLine( ExpressionFormLine other ) {
        this.index = other.getIndex();
        CopyExpressionVisitor copier = new CopyExpressionVisitor();
        if ( other.getParts().size() == 0 ) {
            return;
        }
        for ( ExpressionPart exp = copier.copy( other.getRootExpression() ); exp != null; exp = exp.getNext() ) {
            parts.add( exp );
        }
    }

    public ExpressionFormLine( ExpressionPart part ) {
        appendPart( part );
    }

    public String getText( final ToStringExpressionVisitor visitor ) {
        visitor.visit( getRootExpression() );
        return visitor.getText();
    }

    public int getIndex() {
        return this.index;
    }

    public void appendPart( ExpressionPart part ) {
        if ( !parts.isEmpty() ) {
            parts.getLast().setNext( part );
        }
        parts.add( part );
    }

    public void removeLast() {
        if ( !parts.isEmpty() ) {
            ExpressionPart last = parts.removeLast();
            if ( last.getPrevious() != null ) {
                last.getPrevious().setNext( null );
                last.setPrevious( null );
            }
        }
    }

    private ExpressionPart getPreviousPart() {
        return parts.getLast();
    }

    public String getPreviousClassType() {
        ExpressionPart last = getPreviousPart();
        return last.getPrevious() == null ? null : last.getPrevious().getClassType();
    }

    public String getClassType() {
        return parts.getLast().getClassType();
    }

    public String getGenericType() {
        return parts.isEmpty() ? null : parts.getLast().getGenericType();
    }

    public String getFieldName() {
        return parts.isEmpty() ? null : parts.getLast().getName();
    }

    public String getPreviousGenericType() {
        ExpressionPart prev = getPreviousPart().getPrevious();
        return prev == null ? null : prev.getGenericType();
    }

    public String getParametricType() {
        return parts.getLast().getParametricType();
    }

    public boolean isEmpty() {
        return parts.isEmpty();
    }

    public String getCurrentName() {
        return parts.getLast().getName();
    }

    public String getPreviousName() {
        ExpressionPart previousPart = getPreviousPart();
        return previousPart == null ? null : previousPart.getName();
    }

    public ExpressionPart getRootExpression() {
        return parts.isEmpty() ? null : parts.getFirst();
    }

    public boolean isBound() {
        return binding != null;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding( String binding ) {
        this.binding = binding;
    }

    public List<ExpressionPart> getParts() {
        return this.parts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpressionFormLine that = (ExpressionFormLine) o;

        if (index != that.index) return false;
        if (binding != null ? !binding.equals(that.binding) : that.binding != null) return false;
        if (parts != null ? !parts.equals(that.parts) : that.parts != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = binding != null ? binding.hashCode() : 0;
        result = 31 * result + (parts != null ? parts.hashCode() : 0);
        result = 31 * result + index;
        return result;
    }
}
