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

package org.drools.core.rule;

import org.drools.core.base.ClassObjectType;
import org.drools.core.factmodel.AnnotationDefinition;
import org.drools.core.reteoo.NodeTypeEnums;
import org.drools.core.rule.constraint.MvelConstraint;
import org.drools.core.spi.AcceptsClassObjectType;
import org.drools.core.spi.Constraint;
import org.drools.core.spi.Constraint.ConstraintType;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.ObjectType;
import org.drools.core.spi.PatternExtractor;
import org.drools.core.spi.SelfDateExtractor;
import org.drools.core.spi.SelfNumberExtractor;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Pattern
    implements
    RuleConditionElement,
    AcceptsClassObjectType,
    Externalizable {
    private static final long serialVersionUID = 510l;
    private ObjectType objectType;
    private List<Constraint> constraints = Collections.EMPTY_LIST;
    private Declaration              declaration;
    private Map<String, Declaration> declarations;
    private int                      index;
    private PatternSource            source;
    private List<Behavior>           behaviors;
    private List<String>             listenedProperties;
    private boolean                  hasNegativeConstraint;

    private Map<String, AnnotationDefinition> annotations;

    // this is the offset of the related fact inside a tuple. i.e:
    // the position of the related fact inside the tuple;
    private int offset;

    private boolean           passive;

    public Pattern() {
        this(0,
             null);
    }

    public Pattern(final int index,
                   final ObjectType objectType) {
        this(index,
             index,
             objectType,
             null);
    }

    public Pattern(final int index,
                   final ObjectType objectType,
                   final String identifier) {
        this(index,
             index,
             objectType,
             identifier);
    }

    public Pattern(final int index,
                   final int offset,
                   final ObjectType objectType,
                   final String identifier) {
        this(index,
             offset,
             objectType,
             identifier,
             false);
    }

    public Pattern(final int index,
                   final int offset,
                   final ObjectType objectType,
                   final String identifier,
                   final boolean isInternalFact) {
        this.index = index;
        this.offset = offset;
        this.objectType = objectType;
        if (identifier != null && (!identifier.equals(""))) {
            this.declaration = new Declaration(identifier,
                                               getReadAcessor(objectType),
                                               this,
                                               isInternalFact);
            this.declarations = new HashMap<String, Declaration>(2); // default to avoid immediate resize
            this.declarations.put(this.declaration.getIdentifier(),
                                  this.declaration );
        } else {
            this.declaration = null;
        }        
    }

    public boolean hasNegativeConstraint() {
        return hasNegativeConstraint;
    }

    public void setHasNegativeConstraint(boolean negative) {
        this.hasNegativeConstraint = negative;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        objectType = (ObjectType) in.readObject();
        constraints = (List<Constraint>) in.readObject();
        declaration = (Declaration) in.readObject();
        declarations = (Map<String, Declaration>) in.readObject();
        behaviors = (List<Behavior>) in.readObject();
        index = in.readInt();
        source = (PatternSource) in.readObject();
        offset = in.readInt();
        listenedProperties = (List<String>) in.readObject();
        if ( source instanceof From ) {
            ((From)source).setResultPattern( this );
        }
        annotations = (Map<String,AnnotationDefinition>) in.readObject();
        passive = in.readBoolean();
        hasNegativeConstraint = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( objectType );
        out.writeObject( constraints );
        out.writeObject( declaration );
        out.writeObject( declarations );
        out.writeObject( behaviors );
        out.writeInt( index );
        out.writeObject( source );
        out.writeInt( offset );
        out.writeObject(getListenedProperties());
        out.writeObject( annotations );
        out.writeBoolean( passive );
        out.writeBoolean(hasNegativeConstraint);
    }
    
    public static InternalReadAccessor getReadAcessor(ObjectType objectType) {
        if ( !(objectType instanceof ClassObjectType) ) {
            return new PatternExtractor(objectType);
        }
        Class returnType = ((ClassObjectType) objectType).getClassType();
        
        if (Number.class.isAssignableFrom( returnType ) ||
                ( returnType == byte.class ||
                  returnType == short.class ||
                  returnType == int.class ||
                  returnType == long.class ||
                  returnType == float.class ||
                  returnType == double.class ) ) {            
            return new SelfNumberExtractor(objectType);            
         } else if (  Date.class.isAssignableFrom( returnType ) ) {
            return new SelfDateExtractor(objectType);
        } else {
            return new PatternExtractor(objectType);
        }        
    }
    
    public void setClassObjectType(ClassObjectType objectType) {
        this.objectType = objectType;
    }

    public Declaration[] getRequiredDeclarations() {
        Set<Declaration> decl = new HashSet<Declaration>();
        for( Constraint constr : this.constraints ) {
            Collections.addAll( decl, constr.getRequiredDeclarations() );
        }
        return decl.toArray( new Declaration[decl.size()] );
    }
    
    public Pattern clone() {
        final String identifier = (this.declaration != null) ? this.declaration.getIdentifier() : null;
        final Pattern clone = new Pattern( this.index,
                                           this.offset,
                                           this.objectType,
                                           identifier,
                                           this.declaration != null && this.declaration.isInternalFact());
        clone.setListenedProperties( getListenedProperties() );
        if ( this.getSource() != null ) {
            clone.setSource( (PatternSource) this.getSource().clone() );
            if ( source instanceof From ) {
                ((From)clone.getSource()).setResultPattern( this );
            }
        }

        if( this.declarations != null ) {
            for ( Declaration decl : this.declarations.values() ) {
                Declaration addedDeclaration = clone.addDeclaration( decl.getIdentifier() );
                addedDeclaration.setReadAccessor( decl.getExtractor() );
                addedDeclaration.setBindingName( decl.getBindingName() );
            }
        }

        for ( Constraint oldConstr : this.constraints ) {
            Constraint clonedConstr = oldConstr.clone();

            // we must update pattern references in cloned declarations
            Declaration[] oldDecl = oldConstr.getRequiredDeclarations();
            Declaration[] newDecl = clonedConstr.getRequiredDeclarations();
            for ( int i = 0; i < newDecl.length; i++ ) {
                if ( newDecl[i].getPattern() == this ) {
                    newDecl[i].setPattern( clone );
                    // we still need to call replace because there might be nested declarations to replace
                    clonedConstr.replaceDeclaration( oldDecl[i],
                                                     newDecl[i] );
                }
            }

            clone.addConstraint(clonedConstr);
        }
        
        if ( behaviors != null ) {
            for ( Behavior behavior : this.behaviors ) {
                clone.addBehavior( behavior );
            }
        }
        return clone;
    }

    public ObjectType getObjectType() {
        return this.objectType;
    }
    
    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    public PatternSource getSource() {
        return source;
    }

    public void setSource(PatternSource source) {
        this.source = source;
    }

    public boolean isPassive() {
        return passive;
    }

    public void setPassive(boolean passive) {
        this.passive = passive;
    }

    public List<Constraint> getConstraints() {
        return Collections.unmodifiableList( this.constraints );
    }

    public void addConstraint(int index, Constraint constraint) {
        if ( this.constraints == Collections.EMPTY_LIST ) {
            this.constraints = new ArrayList<Constraint>( 1 );
        }
        if ( constraint.getType().equals( Constraint.ConstraintType.UNKNOWN ) ) {
            this.setConstraintType( (MutableTypeConstraint) constraint );
        }
        this.constraints.add(index, constraint);
    }

    public void addConstraints(Collection<Constraint> constraints) {
        if ( this.constraints == Collections.EMPTY_LIST ) {
            this.constraints = new ArrayList<Constraint>( constraints.size() );
        }
        for (Constraint constraint : constraints) {
            if ( constraint.getType().equals( Constraint.ConstraintType.UNKNOWN ) ) {
                this.setConstraintType( (MutableTypeConstraint) constraint );
            }
            this.constraints.add(constraint);
        }
    }

    public void addConstraint(Constraint constraint) {
        if ( this.constraints == Collections.EMPTY_LIST ) {
            this.constraints = new ArrayList<Constraint>( 1 );
        }
        if ( constraint.getType().equals( Constraint.ConstraintType.UNKNOWN ) ) {
            this.setConstraintType( (MutableTypeConstraint) constraint );
        }
        this.constraints.add(constraint);
    }

    public void removeConstraint(Constraint constraint) {
        this.constraints.remove(constraint);
    }

    public List<MvelConstraint> getCombinableConstraints() {
        if (constraints.size() < 2) {
            return null;
        }
        List<MvelConstraint> combinableConstraints = new ArrayList<MvelConstraint>();
        for (Constraint constraint : constraints) {
            if (constraint instanceof MvelConstraint &&
                    !((MvelConstraint)constraint).isUnification() && !((MvelConstraint)constraint).isDynamic() &&
            // at the moment it is not possible to determine the exact type of node which this
                    // constraint belongs to so use ExistsNode being the less restrictive in terms of index usage
                    !((MvelConstraint)constraint).isIndexable(NodeTypeEnums.ExistsNode) &&
                    // don't combine alpha nodes to allow nodes sharing
                    constraint.getType() == ConstraintType.BETA) {
                combinableConstraints.add((MvelConstraint)constraint);
            }
        }
        return combinableConstraints;
    }

    public Declaration addDeclaration(final String identifier) {
        Declaration declaration = this.declarations != null ? this.declarations.get( identifier ) : null;
        if ( declaration == null ) {
            declaration = new Declaration( identifier,
                                           null,
                                           this,                                           
                                           true );
            addDeclaration(declaration);
        }
        return declaration;
    }
    
    public void addDeclaration(final Declaration decl) {
        if ( this.declarations == null ) {
            this.declarations = new HashMap<String, Declaration>( 2 ); // default to avoid immediate resize
        }        
        this.declarations.put( decl.getIdentifier(),
                               decl );        
    }

    public boolean isBound() {
        return (this.declaration != null);
    }

    public Declaration getDeclaration() {
        return this.declaration;
    }

    public Declaration getDeclaration(String identifier) {
        return this.declarations != null ? this.declarations.get(identifier) : null;
    }

    public int getIndex() {
        return this.index;
    }

    /**
     * The offset of the fact related to this pattern
     * inside the tuple
     *
     * @return the offset
     */
    public int getOffset() {
        return this.offset;
    }

    public void setOffset(final int offset) {
        this.offset = offset;
    }

    public Map<String, Declaration> getInnerDeclarations() {
        return (this.declarations != null) ? this.declarations : Collections.EMPTY_MAP;
    }

    public Map<String, Declaration>  getOuterDeclarations() {
        return (this.declarations != null) ? this.declarations : Collections.EMPTY_MAP;
    }

    public Declaration resolveDeclaration(final String identifier) {
        return (this.declarations != null) ? this.declarations.get( identifier ) : null;
    }

    public String toString() {
        return "Pattern type='" + ((this.objectType == null) ? "null" : this.objectType.toString()) + "', index='" + this.index + "', offset='" + this.getOffset() + "', identifer='" + ((this.declaration == null) ? "" : this.declaration.toString())
               + "'";
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + this.constraints.hashCode();
        result = PRIME * result + ((this.declaration == null) ? 0 : this.declaration.hashCode());
        result = PRIME * result + this.index;
        result = PRIME * result + ((this.objectType == null) ? 0 : this.objectType.hashCode());
        result = PRIME * result + ((this.behaviors == null) ? 0 : this.behaviors.hashCode());
        result = PRIME * result + this.offset;
        result = PRIME * result + ((this.source == null) ? 0 : this.source.hashCode());
        return result;
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || getClass() != object.getClass() ) {
            return false;
        }

        final Pattern other = (Pattern) object;

        if ( !this.constraints.equals( other.constraints ) ) {
            return false;
        }

        if ( this.behaviors != other.behaviors || (this.behaviors != null && !this.behaviors.equals( other.behaviors )) ) {
            return false;
        }

        if ( this.declaration == null ) {
            if ( other.declaration != null ) {
                return false;
            }
        } else if ( !this.declaration.equals( other.declaration ) ) {
            return false;
        }

        if ( this.index != other.index ) {
            return false;
        }

        if ( !this.objectType.equals( other.objectType ) ) {
            return false;
        }
        if ( this.offset != other.offset ) {
            return false;
        }
        return (this.source == null) ? other.source == null : this.source.equals( other.source );
    }

    public List<RuleConditionElement> getNestedElements() {
        return this.source != null ? Collections.singletonList( this.source ) : Collections.EMPTY_LIST;
    }

    public boolean isPatternScopeDelimiter() {
        return true;
    }

    private void setConstraintType(final MutableTypeConstraint constraint) {
        final Declaration[] declarations = constraint.getRequiredDeclarations();

        boolean isAlphaConstraint = true;
        for ( int i = 0; isAlphaConstraint && i < declarations.length; i++ ) {
            if ( !declarations[i].isGlobal() && declarations[i].getPattern() != this ) {
                isAlphaConstraint = false;
            }
        }

        ConstraintType type = isAlphaConstraint ? ConstraintType.ALPHA : ConstraintType.BETA;
        constraint.setType( type );
    }

    /**
     * @return the behaviors
     */
    public List<Behavior> getBehaviors() {
        if ( this.behaviors == null ) {
            return Collections.emptyList();
        }
        return this.behaviors;
    }

    /**
     * @param behaviors the behaviors to set
     */
    public void setBehaviors(List<Behavior> behaviors) {
        this.behaviors = behaviors;
    }

    public void addBehavior(Behavior behavior) {
        if ( this.behaviors == null ) {
            this.behaviors = new ArrayList<Behavior>();
        }
        this.behaviors.add( behavior );
    }

    public List<String> getListenedProperties() {
        return listenedProperties;
    }

    public void setListenedProperties(List<String> listenedProperties) {
        this.listenedProperties = listenedProperties;
    }

    public Map<String, AnnotationDefinition> getAnnotations() {
        if ( annotations == null ) {
            annotations = new HashMap<String, AnnotationDefinition>();
        }
        return annotations;
    }
}
