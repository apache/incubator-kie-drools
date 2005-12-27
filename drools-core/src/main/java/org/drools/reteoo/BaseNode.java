package org.drools.reteoo;

import org.drools.FactException;
import org.drools.spi.PropagationContext;
import org.drools.spi.ReteooNode;

abstract class BaseNode
    implements
    ReteooNode {
    protected final int id;

    protected boolean   attachingNewNode = false;

    protected boolean   hasMemory        = false;

    protected int       sharedCount      = 0;

    public BaseNode(int id){
        super();
        this.id = id;
    }

    public int getId(){
        return this.id;
    }

    public boolean isAttachingNewNode(){
        return this.attachingNewNode;
    }

    protected void setHasMemory(boolean hasMemory){
        this.hasMemory = hasMemory;
    }

    public boolean hasMemory(){
        return this.hasMemory;
    }

    /**
     * Attaches this node into the network.
     */
    public abstract void attach();

    public abstract void remove();

    public abstract void updateNewNode(WorkingMemoryImpl workingMemory,
                                       PropagationContext context) throws FactException;

    public void addShare(){
        --this.sharedCount;
    }

    public void removeShare(){
        --this.sharedCount;

        if ( this.sharedCount < 0 ) {
            throw new RuntimeException( "Shared count for BaseNode should never be less than 0" );
        }
    }

    public boolean isShared(){
        return (this.sharedCount == 0) ? false : true;
    }

    public int getSharedCount(){
        return this.sharedCount;
    }

    public int hashCode(){
        return this.id;
    }

}
