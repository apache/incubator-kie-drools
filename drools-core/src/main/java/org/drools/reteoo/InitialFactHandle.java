package org.drools.reteoo;

public class InitialFactHandle extends FactHandleImpl {
    private final FactHandleImpl delegate;

    private Object               object;

    public InitialFactHandle(FactHandleImpl delegate) {
        super();
        this.delegate = delegate;
        this.object = InitialFactImpl.getInstance();
    }

    // ----------------------------------------------------------------------
    // Instance members
    // ----------------------------------------------------------------------

    /**
     * @see Object
     */
    public boolean equals(Object object) {
        return this.delegate.equals( object );
    }

    /**
     * @see Object
     */
    public int hashCode() {
        return this.delegate.hashCode();
    }

    /**
     * @see Object
     */
    public String toString() {
        return toExternalForm();
    }

    public long getRecency() {
        return this.delegate.getRecency();
    }

    public void setRecency(long recency) {
        this.delegate.setRecency( recency );
    }

    public long getId() {
        return this.delegate.getId();
    }

    void invalidate() {
        this.delegate.invalidate();
    }

    public Object getObject() {
        return this.object;
    }

    public void setObject(Object object) {
        // do nothign
    }

    public String toExternalForm() {
        return "InitialFact";
    }

}
