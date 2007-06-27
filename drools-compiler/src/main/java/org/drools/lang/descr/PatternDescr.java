package org.drools.lang.descr;

import java.util.List;

/*
 * Copyright 2005 JBoss Inc
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


public class PatternDescr extends BaseDescr {
    /**
     * 
     */
    private static final long       serialVersionUID     = 63959215220308107L;
    private String                  objectType;
    private String                  identifier;
    private ConditionalElementDescr constraint          = new AndDescr();
    private int                     leftParentCharacter  = -1;
    private int                     rightParentCharacter = -1;
    private PatternSourceDescr      source;

    public PatternDescr() {
        this( null,
              null );
    }

    public PatternDescr(final String objectType) {
        this( objectType,
              null );
    }

    public PatternDescr(final String objectType,
                        final String identifier) {
        this.objectType = objectType;
        this.identifier = identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public void setObjectType(final String objectType) {
        this.objectType = objectType;
    }

    public String getObjectType() {
        return this.objectType;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public List getDescrs() {
        return this.constraint.getDescrs();
    }
    
    public void addConstraint( BaseDescr base ) {
        this.constraint.addDescr( base );
    }
    
    public ConditionalElementDescr getConstraint() {
        return this.constraint;
    }

    public String toString() {
        return "[Pattern: id=" + this.identifier + "; objectType=" + this.objectType + "]";
    }

    /**
     * @return the leftParentCharacter
     */
    public int getLeftParentCharacter() {
        return this.leftParentCharacter;
    }

    /**
     * @param leftParentCharacter the leftParentCharacter to set
     */
    public void setLeftParentCharacter(final int leftParentCharacter) {
        this.leftParentCharacter = leftParentCharacter;
    }

    /**
     * @return the rightParentCharacter
     */
    public int getRightParentCharacter() {
        return this.rightParentCharacter;
    }

    /**
     * @param rightParentCharacter the rightParentCharacter to set
     */
    public void setRightParentCharacter(final int rightParentCharacter) {
        this.rightParentCharacter = rightParentCharacter;
    }

    public PatternSourceDescr getSource() {
        return source;
    }

    public void setSource(PatternSourceDescr source) {
        this.source = source;
    }
}