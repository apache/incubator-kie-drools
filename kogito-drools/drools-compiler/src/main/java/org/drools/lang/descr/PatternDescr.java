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
package org.drools.lang.descr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class PatternDescr extends BaseDescr implements Cloneable {
    /**
     * 
     */
    private static final long       serialVersionUID = 510l;
    private String                  objectType;
    private String                  identifier;
    private ConditionalElementDescr constraint          = new AndDescr();
    private int                     leftParentCharacter  = -1;
    private int                     rightParentCharacter = -1;
    private PatternSourceDescr      source;
    private List<BehaviorDescr>     behaviors;

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

    public List<? extends BaseDescr> getDescrs() {
        return this.constraint.getDescrs();
    }
    
    public void addConstraint( BaseDescr base ) {
        this.constraint.addDescr( base );
    }
    
    public ConditionalElementDescr getConstraint() {
        return this.constraint;
    }
    
    public boolean isInternalFact() {
        return this.getSource() != null && this.getSource() instanceof CollectDescr;
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
    
    /**
     * @return the behaviors
     */
    public List<BehaviorDescr> getBehaviors() {
        if( behaviors == null ) {
            return Collections.emptyList();
        }
        return behaviors;
    }

    /**
     * @param behaviors the behaviors to set
     */
    public void setBehaviors(List<BehaviorDescr> behaviors) {
        this.behaviors = behaviors;
    }
    
    public void addBehavior( BehaviorDescr behavior ) {
        if( this.behaviors == null ) {
            this.behaviors = new ArrayList<BehaviorDescr>();
        }
        this.behaviors.add( behavior );
    }

    public Object clone() {
        PatternDescr clone = new PatternDescr( this.objectType, this.identifier );
        clone.setLeftParentCharacter( this.leftParentCharacter );
        clone.setRightParentCharacter( this.rightParentCharacter );
        clone.setSource( this.source );
        clone.setStartCharacter( this.getStartCharacter() );
        clone.setEndCharacter( this.getEndCharacter() );
        clone.setLocation( this.getLine(), this.getColumn() );
        clone.setEndLocation( this.getEndLine(), this.getEndColumn() );
        clone.setText( this.getText() );
        for( BaseDescr constraint : this.getDescrs() ) {
            clone.addConstraint( constraint );
        }
        if( behaviors != null ) {
            for( BehaviorDescr behavior : behaviors ) {
                clone.addBehavior( behavior );
            }
        }
        return clone;
    }

}
