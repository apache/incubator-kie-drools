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

package org.drools.compiler.lang.descr;

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
    private boolean                      recursive;

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
        inherited = in.readBoolean();
        recursive = in.readBoolean();
    }
    
    @Override
    public void writeExternal( ObjectOutput out ) throws IOException {
        super.writeExternal( out );
        out.writeInt( index );
        out.writeObject( fieldName );
        out.writeObject( initExpr );
        out.writeObject( pattern );
        out.writeBoolean( inherited );
        out.writeBoolean( recursive );
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

    public TypeFieldDescr cloneAsInherited() {
        TypeFieldDescr clone = new TypeFieldDescr( fieldName, pattern );
        clone.index = this.index;
        clone.initExpr = this.initExpr;
        clone.inherited = true;
        clone.recursive = this.recursive;
        for ( AnnotationDescr ad : this.getAnnotations() ) {
            clone.addAnnotation( ad );
        }
        return clone;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive( boolean recursive ) {
        this.recursive = recursive;
    }
}
