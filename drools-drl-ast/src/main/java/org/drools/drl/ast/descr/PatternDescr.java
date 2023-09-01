/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.drl.ast.descr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kie.internal.definition.GenericTypeDefinition;

public class PatternDescr extends AnnotatedBaseDescr
    implements
    Cloneable {
    private static final long       serialVersionUID     = 510l;
    private String                  objectType;
    private String                  identifier;
    private boolean                 unification;
    private ConditionalElementDescr constraint           = new AndDescr();
    private int                     leftParentCharacter  = -1;
    private int                     rightParentCharacter = -1;
    private PatternSourceDescr      source;
    private List<BehaviorDescr>     behaviors;
    private boolean                 query;
    private GenericTypeDefinition   genericType;

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

    public PatternDescr(final String objectType,
                        final String identifier,
                        final boolean isQuery ) {
        this.objectType = objectType;
        this.identifier = identifier;
        this.query = isQuery; 
    }

    public void setIdentifier( final String identifier ) {
        this.identifier = identifier;
    }

    public void setObjectType( final String objectType ) {
        this.objectType = objectType;
    }

    public void setQuery( boolean query ) {
        this.query = query;
    }

    public String getObjectType() {
        return this.objectType;
    }

    public GenericTypeDefinition getGenericType() {
        return genericType == null ? new GenericTypeDefinition(objectType) : genericType;
    }

    public void setGenericType(GenericTypeDefinition genericType) {
        this.genericType = genericType;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public List<String> getAllBoundIdentifiers() {
        List<String> identifiers = new ArrayList<>();
        if (this.identifier != null) {
            identifiers.add( this.identifier );
        }
        for (BaseDescr descr : getDescrs()) {
            String descrText = descr.getText();
            int colonPos = descrText.indexOf( ':' );
            if (colonPos > 0) {
                identifiers.add(descrText.substring( 0, colonPos ).trim());
            }
        }
        return identifiers;
    }

    public boolean isQuery() {
        return query;
    }

    public List< ? extends BaseDescr> getDescrs() {
        return this.constraint.getDescrs();
    }

    public void addConstraint( BaseDescr base ) {
        this.constraint.addDescr( base );
    }

    public void removeAllConstraint() {
        constraint= new AndDescr();
    }

    public boolean removeConstraint( BaseDescr base ) {
        return this.constraint.removeDescr(base);
    }

    public ConditionalElementDescr getConstraint() {
        return this.constraint;
    }

    public PatternDescr negateConstraint() {
        this.constraint = (ConditionalElementDescr) ((BaseDescr)this.constraint).negate();
        return this;
    }

    public List< ? extends BaseDescr> getPositionalConstraints() {
        return this.doGetConstraints(ExprConstraintDescr.Type.POSITIONAL);
    }

    public List< ? extends BaseDescr> getSlottedConstraints() {
        return this.doGetConstraints(ExprConstraintDescr.Type.NAMED);
    }

    private List< ? extends BaseDescr> doGetConstraints(ExprConstraintDescr.Type type) {
        List<BaseDescr> returnList = new ArrayList<>();
        for(BaseDescr descr : this.constraint.getDescrs()) {

            // if it is a ExprConstraintDescr - check the type
            if(descr instanceof ExprConstraintDescr) {
                ExprConstraintDescr desc = (ExprConstraintDescr) descr;
                if(desc.getType().equals(type)) {
                    returnList.add(desc);
                }
            } else {
                // otherwise, assume 'NAMED'
                if(type.equals(ExprConstraintDescr.Type.NAMED)) {
                    returnList.add(descr);
                }
            }
        }

        return returnList;
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
    public void setLeftParentCharacter( final int leftParentCharacter ) {
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
    public void setRightParentCharacter( final int rightParentCharacter ) {
        this.rightParentCharacter = rightParentCharacter;
    }

    public PatternSourceDescr getSource() {
        return source;
    }

    public void setSource( PatternSourceDescr source ) {
        this.source = source;
    }

    @Override
    public void setResource(org.kie.api.io.Resource resource) {
        super.setResource(resource);
        ((BaseDescr) this.constraint).setResource(resource);
    };

    /**
     * @return the behaviors
     */
    public List<BehaviorDescr> getBehaviors() {
        if ( behaviors == null ) {
            return Collections.emptyList();
        }
        return behaviors;
    }

    /**
     * @param behaviors the behaviors to set
     */
    public void setBehaviors( List<BehaviorDescr> behaviors ) {
        this.behaviors = behaviors;
    }

    public void addBehavior( BehaviorDescr behavior ) {
        if ( this.behaviors == null ) {
            this.behaviors = new ArrayList<>();
        }
        this.behaviors.add( behavior );
    }

    /**
     * @return the unification
     */
    public boolean isUnification() {
        return unification;
    }

    /**
     * @param unification the unification to set
     */
    public void setUnification( boolean unification ) {
        this.unification = unification;
    }

    public PatternDescr clone() {
        PatternDescr clone = new PatternDescr( this.objectType,
                                               this.identifier );
        clone.setQuery( this.query );
        clone.setUnification( unification );
        clone.setLeftParentCharacter( this.leftParentCharacter );
        clone.setRightParentCharacter( this.rightParentCharacter );
        clone.setSource( this.source );
        clone.setStartCharacter( this.getStartCharacter() );
        clone.setEndCharacter( this.getEndCharacter() );
        clone.setLocation( this.getLine(),
                           this.getColumn() );
        clone.setEndLocation( this.getEndLine(),
                              this.getEndColumn() );
        clone.setText( this.getText() );
        for ( BaseDescr constraint : this.getDescrs() ) {
            clone.addConstraint( constraint );
        }
        if ( behaviors != null ) {
            for ( BehaviorDescr behavior : behaviors ) {
                clone.addBehavior( behavior );
            }
        }
        return clone;
    }

    public void accept(DescrVisitor visitor) {
        visitor.visit(this);
    }
}
