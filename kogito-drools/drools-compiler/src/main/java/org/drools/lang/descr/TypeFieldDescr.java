/*
 * Copyright 2008 Red Hat
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

package org.drools.lang.descr;

import org.drools.definition.type.FactField;
import org.drools.factmodel.FieldDefinition;
import org.drools.rule.TypeDeclaration;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


public class TypeFieldDescr extends AnnotatedBaseDescr
    implements
    Comparable<TypeFieldDescr> {

    private static final long            serialVersionUID = 510l;
    private int                          index            = -1;
    private String                       fieldName;
    private String                       initExpr;
    private PatternDescr                 pattern;
    private boolean                      inherited;

    public TypeFieldDescr() {
        this( null );
    }

    public TypeFieldDescr(final String fieldName) {
        this.fieldName = fieldName;
    }

    public TypeFieldDescr(final String fieldName,
                          final PatternDescr pat) {
        this( fieldName );
        this.pattern = pat;
    }
    
    @Override
    public void readExternal( ObjectInput in ) throws IOException,
                                              ClassNotFoundException {
        super.readExternal( in );
        index = in.readInt();
        fieldName = (String) in.readObject();
        initExpr = (String) in.readObject();
        pattern = (PatternDescr) in.readObject();
    }
    
    @Override
    public void writeExternal( ObjectOutput out ) throws IOException {
        super.writeExternal( out );
        out.writeInt( index );
        out.writeObject( fieldName );
        out.writeObject( initExpr );
        out.writeObject( pattern );
    }

    /**
     * @return the identifier
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @param fieldName the identifier to set
     */
    public void setFieldName( String fieldName ) {
        this.fieldName = fieldName;
    }

    /**
    * @return the initExpr
    */
    public String getInitExpr() {
        return initExpr;
    }

    /**
     * @param initExpr the initExpr to set
     */
    public void setInitExpr( String initExpr ) {
        this.initExpr = initExpr;
    }

    /**
     * @return the pattern
     */
    public PatternDescr getPattern() {
        return pattern;
    }

    /**
     * @param pattern the pattern to set
     */
    public void setPattern( PatternDescr pattern ) {
        this.pattern = pattern;
    }

    public String toString() {
        return "TypeField[ " + this.getFieldName() + " : " + this.pattern + " = " + this.initExpr +  " ]";
    }

    public int compareTo( TypeFieldDescr other ) {
        return (this.index - other.index);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex( int index ) {
        this.index = index;
    }


    public boolean isInherited() {
        return inherited;
    }

    public void setInherited(boolean inherited) {
        this.inherited = inherited;
    }

}
