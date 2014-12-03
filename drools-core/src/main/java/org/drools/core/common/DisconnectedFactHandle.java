package org.drools.core.common;

import org.drools.core.factmodel.traits.TraitFactory;
import org.drools.core.factmodel.traits.TraitTypeEnum;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RightTuple;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

@XmlRootElement(name="disconnected-fact-handle")
@XmlAccessorType(XmlAccessType.FIELD)
public class DisconnectedFactHandle
        implements
        InternalFactHandle,
        Externalizable {

    @XmlElement
    @XmlSchemaType(name="int")
    private int    id;
    
    @XmlElement
    @XmlSchemaType(name="int")
    private int    identityHashCode;
    
    @XmlElement
    @XmlSchemaType(name="int")
    private int    objectHashCode;
    
    @XmlElement
    @XmlSchemaType(name="long")
    private long   recency;
    
    @XmlElement
    private Object object;
    
    @XmlElement
    @XmlSchemaType(name="string")
    private String entryPointId;
    
    @XmlElement
    private TraitTypeEnum traitType;

    private boolean                 negated;

    public DisconnectedFactHandle() {
    }

    public DisconnectedFactHandle(int id,
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
        this.traitType = isTraitOrTraitable ? determineTraitType() : TraitTypeEnum.NON_TRAIT;
    }

    public DisconnectedFactHandle(int id,
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
        this.traitType = elements.length > 6 ? TraitTypeEnum.valueOf( elements[6] ) : TraitTypeEnum.NON_TRAIT;
    }

    @Override
    public boolean isNegated() {
        return negated;
    }

    @Override
    public void setNegated(boolean negated) {
        this.negated = negated;
    }

    public int getId() {
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

    public Object getObject() {
        if ( this.object != null ) {
            return this.object;
        }
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public EntryPoint getEntryPoint() {
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

    public void setEntryPoint(EntryPoint ep) {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public void setEqualityKey(EqualityKey key) {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public void setFirstLeftTuple(LeftTuple leftTuple) {
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
    
    public InternalFactHandle quickClone() {
        return new DisconnectedFactHandle(id, identityHashCode, objectHashCode, recency, entryPointId, object, traitType != TraitTypeEnum.NON_TRAIT );
    }

    public InternalFactHandle clone() {
        return clone();
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
               this.traitType.name();
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

    public void addLeftTupleInPosition(LeftTuple leftTuple) {
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

    public void addRightTupleInPosition(RightTuple rightTuple) {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public void removeRightTuple(RightTuple rightTuple) {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public String getEntryPointId() {
        return entryPointId;
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
                                              ifh.getEntryPoint() != null ? ifh.getEntryPoint().getEntryPointId() : null,
                                                  null,
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

    private TraitTypeEnum determineTraitType() {
        if ( isTraitOrTraitable() ) {
            return TraitFactory.determineTraitType( object );
        } else {
            return TraitTypeEnum.NON_TRAIT;
        }
    }

    @Override
    public TraitTypeEnum getTraitType() {
        return traitType;
    }
}
