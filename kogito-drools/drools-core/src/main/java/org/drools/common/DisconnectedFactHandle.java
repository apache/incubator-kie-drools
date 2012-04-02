package org.drools.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.RightTuple;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;

@XmlAccessorType(XmlAccessType.NONE)
public class DisconnectedFactHandle
        implements
        InternalFactHandle {

    private int    id;
    private int    identityHashCode;
    private int    objectHashCode;
    private long   recency;
    private Object object;
    private String entryPointId;

    protected DisconnectedFactHandle() {
    }

    public DisconnectedFactHandle(int id,
                                  int identityHashCode,
                                  int objectHashCode,
                                  long recency,
                                  String entryPointId,
                                  Object object) {
        this.id = id;
        this.identityHashCode = identityHashCode;
        this.objectHashCode = objectHashCode;
        this.recency = recency;
        this.entryPointId = entryPointId;
        this.object = object;
    }

    public DisconnectedFactHandle(int id,
                                  int identityHashCode,
                                  int objectHashCode,
                                  long recency,
                                  Object object) {
        this( id, 
              identityHashCode, 
              objectHashCode, 
              recency, 
              null, 
              object );
    }

    public DisconnectedFactHandle(int id,
                                  int identityHashCode,
                                  int objectHashCode,
                                  long recency) {
        this( id, 
              identityHashCode, 
              objectHashCode, 
              recency, 
              null, 
              null );
    }

    public DisconnectedFactHandle(String externalFormat) {
        String[] elements = externalFormat.split( ":" );
        if ( elements.length < 6 ) {
            throw new IllegalArgumentException( "externalFormat did not have enough elements" );
        }

        this.id = Integer.parseInt( elements[1] );
        this.identityHashCode = Integer.parseInt( elements[2] );
        this.objectHashCode = Integer.parseInt( elements[3] );
        this.recency = Long.parseLong( elements[4] );
        this.entryPointId = elements[5];
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

    public boolean isValid() {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public void setEntryPoint(WorkingMemoryEntryPoint ep) {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public void setEqualityKey(EqualityKey key) {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public void setFirstLeftTuple(LeftTuple leftTuple) {
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

    public DefaultFactHandle clone() {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public String toExternalForm() {
        return "0:" + this.id + ":" + this.identityHashCode + ":" + this.objectHashCode + ":" + this.recency + ":" + this.entryPointId;
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
                                                  null );
        }
    }

}
