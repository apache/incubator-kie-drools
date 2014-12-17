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

public class ExpressionVariable extends ExpressionPart {

    private String factType;

    public ExpressionVariable() {
    }

    public ExpressionVariable( String fieldName,
                               String fieldClassType,
                               String fieldGenericType ) {
        super( fieldName, fieldClassType, fieldGenericType );
    }

    public ExpressionVariable( String binding,
                               String factType ) {
        super( binding,
               factType,
               factType );
        if ( binding == null || binding.isEmpty() ) {
            throw new RuntimeException( "the fact is not bounded: " + factType );
        }
        this.factType = factType;
    }

    public String getFactType() {
        return factType;
    }

    @Override
    public void accept( ExpressionVisitor visitor ) {
        visitor.visit( this );
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        if ( !super.equals( o ) ) {
            return false;
        }

        ExpressionVariable that = (ExpressionVariable) o;

        if ( factType != null ? !factType.equals( that.factType ) : that.factType != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + ( factType != null ? factType.hashCode() : 0 );
        return result;
    }
}
