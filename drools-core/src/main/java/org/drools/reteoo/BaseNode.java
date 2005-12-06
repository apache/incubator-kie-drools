package org.drools.reteoo;

import org.drools.spi.ReteooNode;

abstract class BaseNode
    implements
    ReteooNode
{
    protected final int id;
        
    public BaseNode(int id)
    {
        super( );
        this.id = id;
    }

    public int getId()
    {
        return this.id;
    }
    
}
