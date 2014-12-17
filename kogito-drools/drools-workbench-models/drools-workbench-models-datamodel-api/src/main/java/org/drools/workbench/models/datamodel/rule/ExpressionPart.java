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

public abstract class ExpressionPart
        implements IPattern,
                   IAction,
                   ExpressionVisitable {

    private ExpressionPart prev;
    private ExpressionPart next;
    protected String name;
    private String classType;
    private String genericType;
    private String parametricType;

    public ExpressionPart() {
    }

    public ExpressionPart( String name,
                           String classType,
                           String genericType ) {
        this.name = name;
        this.classType = classType;
        this.genericType = genericType;
    }

    public ExpressionPart( String name,
                           String classType,
                           String genericType,
                           String parametricType ) {
        this( name, classType, genericType );
        this.parametricType = parametricType;
    }

    public String getName() {
        return name;
    }

    public final String getClassType() {
        return classType;
    }

    public final String getGenericType() {
        return genericType;
    }

    public String getParametricType() {
        return parametricType;
    }

    public ExpressionPart getPrevious() {
        return prev;
    }

    public void setPrevious( ExpressionPart prev ) {
        this.prev = prev;
        if ( prev != null ) {
            prev.next = this;
        }
    }

    public ExpressionPart getNext() {
        return next;
    }

    public void setNext( ExpressionPart next ) {
        this.next = next;
        if ( next != null ) {
            next.prev = this;
        }
    }

    @Override
    public String toString() {
        return getName();
    }

    public void accept( ExpressionVisitor visitor ) {
        visitor.visit( this );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpressionPart that = (ExpressionPart) o;

        if (classType != null ? !classType.equals(that.classType) : that.classType != null) return false;
        if (genericType != null ? !genericType.equals(that.genericType) : that.genericType != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (next != null ? !next.equals(that.next) : that.next != null) return false;
        if (parametricType != null ? !parametricType.equals(that.parametricType) : that.parametricType != null)
            return false;
        if (prev != null ? !prev.equals(that.prev) : that.prev != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = prev != null ? prev.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (classType != null ? classType.hashCode() : 0);
        result = 31 * result + (genericType != null ? genericType.hashCode() : 0);
        result = 31 * result + (parametricType != null ? parametricType.hashCode() : 0);
        return result;
    }
}
