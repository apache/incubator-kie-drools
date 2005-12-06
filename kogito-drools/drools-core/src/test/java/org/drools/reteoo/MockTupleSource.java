package org.drools.reteoo;

public class MockTupleSource extends TupleSource
{

    private int attached;

    public MockTupleSource(int id)
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
