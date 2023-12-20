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
package org.drools.base.rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.drools.base.RuleBase;
import org.drools.base.base.AcceptsClassObjectType;
import org.drools.base.base.ClassObjectType;
import org.drools.base.base.ObjectType;
import org.drools.base.factmodel.AnnotationDefinition;
import org.drools.base.reteoo.PropertySpecificUtil;
import org.drools.base.rule.accessor.PatternExtractor;
import org.drools.base.rule.constraint.Constraint;
import org.drools.base.rule.constraint.XpathConstraint;
import org.drools.util.bitmask.BitMask;
import org.drools.wiring.api.util.ClassUtils;

import static org.kie.internal.ruleunit.RuleUnitUtil.isDataSource;

public class Pattern implements RuleConditionElement, AcceptsClassObjectType, Externalizable {

    private static final long serialVersionUID = 510l;
    private ObjectType objectType;
    private List<Constraint> constraints = Collections.EMPTY_LIST;
    private Declaration declaration;
    private Map<String, Declaration> declarations = Collections.EMPTY_MAP;
    private int patternId;
    private PatternSource source;
    private List<Behavior> behaviors;
    private Collection<String> listenedProperties = new HashSet<>();
    private boolean hasNegativeConstraint;

    private transient XpathBackReference backRefDeclarations;

    private Map<String, AnnotationDefinition> annotations;

    // This is the index of the related fact inside a tuple chain. i.e:
    // the position of the fact inside the tuple;
    private int             tupleIndex;

    // This is the index of the related fact, relative to the other facts in the current Path.
    private int             objectIndex;

    private boolean         passive;
    
    private XpathConstraint xPath;

    private BitMask positiveWatchMask;
    private BitMask negativeWatchMask;

    public Pattern() {
        this(0, null);
    }

    public Pattern(final int patternId, final ObjectType objectType) {
        this(patternId, 0, 0, objectType, null);
    }

    public Pattern(final int patternId, final ObjectType objectType, final String identifier) {
        this(patternId, 0, 0, objectType, identifier);
    }

    public Pattern(final int patternId, final int tupleIndex, final int objectIndex, final ObjectType objectType, final String identifier) {
        this(patternId, tupleIndex, objectIndex, objectType, identifier, false);
    }

    public Pattern(final int patternId, final int tupleIndex, final int objectIndex, final ObjectType objectType, final String identifier, final boolean isInternalFact) {
        this.patternId = patternId;
        this.tupleIndex = tupleIndex;
        this.objectIndex = objectIndex;
        this.objectType = objectType;
        if (identifier != null && (!identifier.equals(""))) {
            this.declaration = new Declaration(identifier, new PatternExtractor(objectType), this, isInternalFact);
            this.declarations = new HashMap<>();
            this.declarations.put(this.declaration.getIdentifier(), this.declaration );
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
        patternId = in.readInt();
        source = (PatternSource) in.readObject();
        tupleIndex = in.readInt();
        objectIndex = in.readInt();
        listenedProperties = (Collection<String>) in.readObject();
        if ( source instanceof From from ) {
            from.setResultPattern( this );
        }
        annotations = (Map<String,AnnotationDefinition>) in.readObject();
        passive = in.readBoolean();
        hasNegativeConstraint = in.readBoolean();
        xPath = (XpathConstraint) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( objectType );
        out.writeObject( constraints );
        out.writeObject( declaration );
        out.writeObject( declarations );
        out.writeObject( behaviors );
        out.writeInt(patternId);
        out.writeObject( source );
        out.writeInt(tupleIndex);
        out.writeInt(objectIndex);
        out.writeObject(getListenedProperties());
        out.writeObject( annotations );
        out.writeBoolean( passive );
        out.writeBoolean(hasNegativeConstraint);
        out.writeObject(xPath);
    }
    
    public void setClassObjectType(ClassObjectType objectType) {
        this.objectType = objectType;
    }

    public Declaration[] getRequiredDeclarations() {
        Set<Declaration> decl = new HashSet<>();
        for( Constraint constr : this.constraints ) {
            Collections.addAll( decl, constr.getRequiredDeclarations() );
        }
        return decl.toArray( new Declaration[decl.size()] );
    }
    
    public Pattern clone() {
        final String identifier = (this.declaration != null) ? this.declaration.getIdentifier() : null;
        final Pattern clone = new Pattern( this.patternId,
                                           this.tupleIndex,
                                           this.objectIndex,
                                           this.objectType,
                                           identifier,
                                           this.declaration != null && this.declaration.isInternalFact());
        clone.listenedProperties = listenedProperties;
        if ( this.getSource() != null ) {
            clone.setSource( (PatternSource) this.getSource().clone() );
            if ( source instanceof From ) {
                ((From)clone.getSource()).setResultPattern( clone );
            }
        }

        for ( Declaration decl : this.declarations.values() ) {
            Declaration addedDeclaration = clone.addDeclaration( decl.getIdentifier() );
            addedDeclaration.setReadAccessor( decl.getExtractor() );
            addedDeclaration.setBindingName( decl.getBindingName() );
            addedDeclaration.setxPathOffset( decl.getxPathOffset());
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
        return this.constraints;
    }

    public void addConstraint(int index, Constraint constraint) {
        if ( this.constraints == Collections.EMPTY_LIST ) {
            this.constraints = new ArrayList<>( 1 );
        }
        if ( constraint.getType().equals( Constraint.ConstraintType.UNKNOWN ) ) {
            this.setConstraintType( (MutableTypeConstraint) constraint );
        } else if ( constraint.getType().equals( Constraint.ConstraintType.XPATH ) ) {
            this.xPath = (XpathConstraint) constraint;
        }
        this.constraints.add(index, constraint);
    }

    public void addConstraints(Collection<Constraint> constraints) {
        if ( this.constraints == Collections.EMPTY_LIST ) {
            this.constraints = new ArrayList<>( constraints.size() );
        }
        for (Constraint constraint : constraints) {
            if ( constraint.getType().equals( Constraint.ConstraintType.UNKNOWN ) ) {
                this.setConstraintType( (MutableTypeConstraint) constraint );
            } else if ( constraint.getType().equals( Constraint.ConstraintType.XPATH ) ) {
                this.xPath = (XpathConstraint) constraint;
            }
            this.constraints.add(constraint);
        }
    }

    public void addConstraint(Constraint constraint) {
        if ( this.constraints == Collections.EMPTY_LIST ) {
            this.constraints = new ArrayList<>( 1 );
        }
        if ( constraint.getType().equals( Constraint.ConstraintType.UNKNOWN ) ) {
            this.setConstraintType( (MutableTypeConstraint) constraint );
        }
        this.constraints.add(constraint);
    }

    public boolean hasXPath() {
        return xPath != null;
    }

    public XpathConstraint getXpathConstraint() {
        return xPath;
    }

    public Declaration getXPathDeclaration() {
        return xPath != null ? xPath.getDeclaration() : null;
    }

    public Declaration addDeclaration(final String identifier) {
        Declaration declaration = resolveDeclaration( identifier );
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
        if ( this.declarations == Collections.EMPTY_MAP ) {
            this.declarations = new HashMap<>();
        }        
        this.declarations.put( decl.getIdentifier(), decl );
    }

    public void resetDeclarations() {
        this.declarations = Collections.EMPTY_MAP;
    }

    public boolean isBound() {
        return (this.declaration != null);
    }

    public Declaration getDeclaration() {
        return this.declaration;
    }

    /**
     * The index of the Pattern, in the list of patterns.
     * @return the patternIndex
     */
    public int getPatternId() {
        return this.patternId;
    }

    public int getObjectIndex() {
        return objectIndex;
    }

    public void setObjectIndex(int objectIndex) {
        this.objectIndex = objectIndex;
    }

    /**
     * The index of pattern in the tuple chain.
     * @return the tupleIndex
     */
    public int getTupleIndex() {
        return this.tupleIndex;
    }

    public void setTupleIndex(final int tupleIndex) {
        this.tupleIndex = tupleIndex;
    }

    public Map<String, Declaration> getDeclarations() {
        return declarations;
    }

    public Map<String, Declaration> getInnerDeclarations() {
        return backRefDeclarations != null ? backRefDeclarations.getDeclarationMap() : this.declarations;
    }

    public Map<String, Declaration>  getOuterDeclarations() {
        return getInnerDeclarations();
    }

    public Declaration resolveDeclaration(final String identifier) {
        return backRefDeclarations != null ? backRefDeclarations.getDeclarationMap().get( identifier ) : this.declarations.get( identifier );
    }

    public String toString() {
        return "Pattern type='" + ((this.objectType == null) ? "null" : this.objectType.toString()) + "', patternId='" + this.patternId + "', objectIndex='" + this.getObjectIndex() + "', identifer='" + ((this.declaration == null) ? "" : this.declaration.toString())
               + "'";
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + this.constraints.hashCode();
        result = PRIME * result + ((this.declaration == null) ? 0 : this.declaration.hashCode());
        result = PRIME * result + this.patternId;
        result = PRIME * result + ((this.objectType == null) ? 0 : this.objectType.hashCode());
        result = PRIME * result + ((this.behaviors == null) ? 0 : this.behaviors.hashCode());
        result = PRIME * result + this.tupleIndex;
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

        if (this.patternId != other.patternId) {
            return false;
        }

        if ( !this.objectType.equals( other.objectType ) ) {
            return false;
        }
        if (this.tupleIndex != other.tupleIndex) {
            return false;
        }
        return Objects.equals(this.source, other.source);
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

        Constraint.ConstraintType type = isAlphaConstraint ? Constraint.ConstraintType.ALPHA : Constraint.ConstraintType.BETA;
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
            this.behaviors = new ArrayList<>();
        }
        this.behaviors.add( behavior );
    }

    public Collection<String> getListenedProperties() {
        return listenedProperties;
    }

    public void addBoundProperty(String boundProperty) {
        if ( !listenedProperties.contains( "!*" ) ) {
            this.listenedProperties.add( boundProperty );
        }
    }

    public void addWatchedProperty(String watchedProperty) {
        this.listenedProperties.add( watchedProperty );
    }

    public void addWatchedProperties(Collection<String> watchedProperties) {
        this.listenedProperties.addAll( watchedProperties );
    }

    public List<String> getAccessibleProperties(RuleBase ruleBase) {
        return PropertySpecificUtil.getAccessibleProperties( ruleBase, objectType );
    }

    public BitMask getPositiveWatchMask( List<String> accessibleProperties ) {
        if (positiveWatchMask == null) {
            positiveWatchMask = PropertySpecificUtil.calculatePositiveMask(objectType, listenedProperties, accessibleProperties);
        }
        return positiveWatchMask;
    }

    public void setPositiveWatchMask( BitMask positiveWatchMask ) {
        this.positiveWatchMask = positiveWatchMask;
    }

    public BitMask getNegativeWatchMask( List<String> accessibleProperties ) {
        if (negativeWatchMask == null) {
            negativeWatchMask = PropertySpecificUtil.calculateNegativeMask(objectType, listenedProperties, accessibleProperties);
        }
        return negativeWatchMask;
    }

    public void setNegativeWatchMask( BitMask negativeWatchMask ) {
        this.negativeWatchMask = negativeWatchMask;
    }

    public Map<String, AnnotationDefinition> getAnnotations() {
        if ( annotations == null ) {
            annotations = new HashMap<>();
        }
        return annotations;
    }

    public XpathBackReference getBackRefDeclarations() {
        return backRefDeclarations;
    }

    public void setBackRefDeclarations( XpathBackReference backRefDeclarations ) {
        this.backRefDeclarations = backRefDeclarations;
    }

    public List<Class<?>> getXpathBackReferenceClasses() {
        return backRefDeclarations != null ? backRefDeclarations.getBackReferenceClasses() : Collections.EMPTY_LIST;
    }

    public boolean isCompatibleWithAccumulateReturnType( Class<?> returnType ) {
        return isCompatibleWithAccumulateReturnType( getPatternType(), returnType );
    }

    public boolean isCompatibleWithFromReturnType( Class<?> returnType ) {
        return isCompatibleWithFromReturnType( getPatternType(), returnType );
    }

    public static boolean isCompatibleWithAccumulateReturnType( Class<?> patternType, Class<?> returnType ) {
        return returnType == null ||
                returnType == Object.class ||
                ( returnType == Number.class && patternType != null && Number.class.isAssignableFrom( patternType ) ||
                        (patternType != null && patternType.isAssignableFrom(ClassUtils.convertFromPrimitiveType(returnType))) );
    }

    public static boolean isCompatibleWithFromReturnType( Class<?> patternType, Class<?> returnType ) {
        return isCompatibleWithAccumulateReturnType( patternType, returnType ) ||
                isIterable( returnType ) ||
                isDataSource( returnType ) ||
                ( patternType != null &&
                        ( returnType.isAssignableFrom( patternType ) ||
                                (!ClassUtils.isFinal(returnType) && ClassUtils.isInterface(patternType))
                        ) );
    }

    private Class<?> getPatternType() {
        return objectType instanceof ClassObjectType cot ? cot.getClassType() : null;
    }

    private static boolean isIterable(Class<?> clazz) {
        return Iterable.class.isAssignableFrom( clazz ) || clazz.isArray();
    }
}
