package org.drools.reteoo;

public class MockObjectSource extends ObjectSource
{
    private int attached;

    public MockObjectSource(int id)
    {
        super( id );
    }

    public void attach()
    {
        this.attached++;

    }

    public int getAttached()
    {
        return this.attached;
    }

}
