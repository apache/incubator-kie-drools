/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.common;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.base.ArrayElements;
import org.drools.core.base.DroolsQuery;
import org.drools.core.factmodel.traits.TraitTypeEnum;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.Tuple;
import org.drools.core.xml.jaxb.util.JaxbUnknownAdapter;
import org.kie.api.runtime.rule.FactHandle;

@XmlRootElement(name="disconnected-fact-handle")
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({ArrayElements.class})
public class DisconnectedFactHandle
        implements
        InternalFactHandle,
        Externalizable {

    @XmlElement
    @XmlSchemaType(name="long")
    private long    id;

    @XmlElement
    @XmlSchemaType(name="int")
    private int    identityHashCode;

    @XmlElement
    @XmlSchemaType(name="int")
    private int    objectHashCode;

    @XmlElement
    @XmlSchemaType(name="long")
    private long   recency;

    /**
     *  This could be a {@link DroolsQuery} object or other almost-impossible-to-serialize class
     */
    @XmlElement
    @XmlJavaTypeAdapter(value=JaxbUnknownAdapter.class)
    private Object object;

    @XmlElement
    @XmlSchemaType(name="string")
    private String entryPointId;

    @XmlElement
    private TraitTypeEnum traitType;

    private boolean                 negated;

    public DisconnectedFactHandle() {
    }

    public DisconnectedFactHandle(long id,
                                  int identityHashCode,
                                  int objectHashCode,
                                  long recency,
                                  String entryPointId,
                                  Object object,
                                  boolean isTraitOrTraitable ) {
        this.id = id;
        this.identityHashCode = identityHashCode;
        this.objectHashCode = objectHashCode;
        this.recency = recency;
        this.entryPointId = entryPointId;
        this.object = object;
        this.traitType = TraitTypeEnum.NON_TRAIT; // Traits are not tested with DisconnectedFactHandle
    }

    public DisconnectedFactHandle(long id,
                                  int identityHashCode,
                                  int objectHashCode,
                                  long recency,
                                  Object object,
                                  boolean isTraitOrTraitable ) {
        this( id,
              identityHashCode,
              objectHashCode,
              recency,
              null,
              object,
              isTraitOrTraitable );
    }

    public DisconnectedFactHandle(int id,
                                  int identityHashCode,
                                  int objectHashCode,
                                  long recency,
                                  boolean isTraitOrTraitable ) {
        this( id,
              identityHashCode,
              objectHashCode,
              recency,
              null,
              null,
              isTraitOrTraitable );
    }

    public DisconnectedFactHandle(String externalFormat) {
        parseExternalForm( externalFormat );
    }

    private void parseExternalForm( String externalFormat ) {
        String[] elements = externalFormat.split( ":" );
        if (elements.length < 7) {
            throw new IllegalArgumentException( "externalFormat did not have enough elements ["+externalFormat+"]" );
        }

        this.id = Integer.parseInt( elements[1] );
        this.identityHashCode = Integer.parseInt( elements[2] );
        this.objectHashCode = Integer.parseInt(elements[3]);
        this.recency = Long.parseLong( elements[4] );
        this.entryPointId = elements[5].trim();
        this.traitType = TraitTypeEnum.valueOf( elements[6] );
    }

    @Override
    public boolean isNegated() {
        return negated;
    }

    @Override
    public void setNegated(boolean negated) {
        this.negated = negated;
    }

    @Override
    public <K> K as( Class<K> klass ) throws ClassCastException {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    @Override
    public boolean isPendingRemoveFromStore() {
        return false;
    }

    public void forEachRightTuple( Consumer<RightTuple> rightTupleConsumer ) { }

    @Override
    public void forEachLeftTuple( Consumer<LeftTuple> leftTupleConsumer ) { }

    @Override
    public RightTuple findFirstRightTuple( Predicate<RightTuple> rightTuplePredicate ) {
        return null;
    }

    @Override
    public LeftTuple findFirstLeftTuple( Predicate<LeftTuple> lefttTuplePredicate ) {
        return null;
    }

    public long getId() {
        return this.id;
    }

    public int getIdentityHashCode() {
        return this.identityHashCode;
    }

    public int getObjectHashCode() {
        return this.objectHashCode;
    }

    public long getRecency() {
        return this.recency;
    }

    public LeftTuple getLastLeftTuple() {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public String getObjectClassName() {
        return this.object != null ? object.getClass().getName() : null;
    }

    public Object getObject() {
        if ( this.object != null ) {
            return this.object;
        }
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public WorkingMemoryEntryPoint getEntryPoint() {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public EqualityKey getEqualityKey() {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public RightTuple getRightTuple() {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public void invalidate() {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public boolean isEvent() {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public boolean isTraitOrTraitable() {
        return traitType != TraitTypeEnum.NON_TRAIT;
    }

    public boolean isTraitable() {
        return traitType == TraitTypeEnum.TRAITABLE || traitType == TraitTypeEnum.WRAPPED_TRAITABLE;
    }

    public boolean isTraiting() {
        return traitType == TraitTypeEnum.TRAIT.TRAIT;
    }
    public boolean isValid() {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public void setEntryPoint(WorkingMemoryEntryPoint ep ) {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public void setEqualityKey(EqualityKey key) {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public void setFirstLeftTuple(LeftTuple leftTuple) {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    @Override
    public LinkedTuples getLinkedTuples() {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    @Override
    public LinkedTuples detachLinkedTuples() {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    @Override
    public LinkedTuples detachLinkedTuplesForPartition(int i) {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public void setLastLeftTuple(LeftTuple leftTuple) {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public void setObject(Object object) {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public void setRecency(long recency) {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public void setRightTuple(RightTuple rightTuple) {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public InternalFactHandle clone() {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public String toExternalForm() {
        return "0:" + this.id +
               ":" +
               getIdentityHashCode() +
               ":" +
               getObjectHashCode() +
               ":" +
               getRecency() +
               ":" +
               this.entryPointId +
               ":" +
               this.traitType.name() +
               ":" +
               getObjectClassName();
    }

    @XmlAttribute(name = "external-form")
    public String getExternalForm() {
        return toExternalForm();
    }

    public LeftTuple getFirstLeftTuple() {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public RightTuple getFirstRightTuple() {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public RightTuple getLastRightTuple() {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public String toTupleTree(int indent) {
        return null;
    }

    public boolean isDisconnected() {
        return true;
    }

    public void disconnect() {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    public void addFirstLeftTuple(LeftTuple leftTuple) {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public void addLastLeftTuple(LeftTuple leftTuple) {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public void removeLeftTuple(LeftTuple leftTuple) {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public void clearLeftTuples() {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public void clearRightTuples() {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public void addFirstRightTuple(RightTuple rightTuple) {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public void addLastRightTuple(RightTuple rightTuple) {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public void addTupleInPosition(Tuple rightTuple) {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public void removeRightTuple(RightTuple rightTuple) {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public EntryPointId getEntryPointId() {
        return new EntryPointId(entryPointId);
    }

    @Override
    public WorkingMemoryEntryPoint getEntryPoint( InternalWorkingMemory wm ) {
        throw new UnsupportedOperationException( "org.drools.core.common.DisconnectedFactHandle.getEntryPoint -> TODO" );

    }

    public static DisconnectedFactHandle newFrom( FactHandle handle ) {
        if( handle instanceof DisconnectedFactHandle ) {
            return (DisconnectedFactHandle) handle;
        } else {
            InternalFactHandle ifh = (InternalFactHandle) handle;
            return new DisconnectedFactHandle(ifh.getId(),
                                              ifh.getIdentityHashCode(),
                                              ifh.getObjectHashCode(),
                                              ifh.getRecency(),
                                              ifh.getEntryPointName(),
                                              ifh.getObject(),
                                              ifh.isTraitOrTraitable() );
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( toExternalForm() );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        String externalForm = (String) in.readObject();
        parseExternalForm( externalForm );
    }

    @Override
    public TraitTypeEnum getTraitType() {
        return traitType;
    }

    public String toString() {
        return "[disconnected fact " + toExternalForm() + ":" + this.object + "]";
    }

}
