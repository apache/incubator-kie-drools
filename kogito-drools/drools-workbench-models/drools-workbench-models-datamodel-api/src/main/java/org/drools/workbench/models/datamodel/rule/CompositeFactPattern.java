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

import java.util.Arrays;

/**
 * Represents first order logic like Or, Not, Exists.
 */
public class CompositeFactPattern
        implements
        IPattern {

    public static final String COMPOSITE_TYPE_NOT = "not";
    public static final String COMPOSITE_TYPE_EXISTS = "exists";
    public static final String COMPOSITE_TYPE_OR = "or";

    /**
     * this will one of: [Not, Exist, Or]
     */
    private String type;

    /**
     * The patterns.
     */
    private IFactPattern[] patterns;

    /**
     * This type should be from the contants in this class of course.
     */
    public CompositeFactPattern( final String type ) {
        this.type = type;
    }

    public CompositeFactPattern() {
    }

    public void clearFactPatterns() {
        this.patterns = new FactPattern[ 0 ];
    }

    public void addFactPatterns( IFactPattern[] patterns ) {
        for ( int i = 0; i < patterns.length; i++ ) {
            IFactPattern iFactPattern = patterns[ i ];
            this.addFactPattern( iFactPattern );
        }
    }

    public void addFactPattern( final IFactPattern pat ) {
        if ( this.patterns == null ) {
            this.patterns = new FactPattern[ 0 ];
        }

        final IFactPattern[] list = this.patterns;
        final IFactPattern[] newList = new IFactPattern[ list.length + 1 ];
        System.arraycopy( list,
                          0,
                          newList,
                          0,
                          list.length );
        newList[ list.length ] = pat;

        this.patterns = newList;
    }

    public IFactPattern[] getPatterns() {
        return patterns;
    }

    /**
     * Remove a FactPattern at the provided index. If index is less than zero or
     * greater than or equal to the number of patterns the effect of this method
     * is "no operation".
     * @param index
     * @return true if the deletion was successful, i.e. within range
     */
    public boolean removeFactPattern( int index ) {
        final int newSize = ( ( index >= 0 && index < this.patterns.length ) ? this.patterns.length - 1 : this.patterns.length );
        final IFactPattern[] newList = new IFactPattern[ newSize ];
        boolean deleted = false;
        int newIdx = 0;
        for ( int i = 0; i < this.patterns.length; i++ ) {
            if ( i != index ) {
                newList[ newIdx ] = this.patterns[ i ];
                newIdx++;
            } else {
                deleted = true;
            }
        }
        this.patterns = newList;
        return deleted;
    }

    public String getType() {
        return type;
    }

    public void setType( String type ) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompositeFactPattern that = (CompositeFactPattern) o;

        if (!Arrays.equals(patterns, that.patterns)) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (patterns != null ? Arrays.hashCode(patterns) : 0);
        result = ~~result;
        return result;
    }
}
