package org.drools.core.test.model;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.kie.api.runtime.rule.FactHandle;

public class MockFactHandle
    implements
    FactHandle {
    private static final long serialVersionUID = 510l;
    private int               id;


    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(id);
    }
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        id  = in.readInt();
    }

    @Override
    public Object getObject() {
        return this;
    }

    @Override
    public boolean isNegated() {
        return false;
    }

    @Override
    public boolean isEvent() {
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    public MockFactHandle(final int id) {
        this.id = id;
    }

    public String toExternalForm() {
        return "[fact:" + this.id + "]";
    }

    public int hashCode() {
        return this.id;
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || getClass() != object.getClass() ) {
            return false;
        }

        return ((MockFactHandle) object).id == this.id;
    }

    public long getId() {
        return this.id;
    }

    public long getRecency() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Object as(Class klass) throws ClassCastException {
        throw new UnsupportedOperationException();
    }
}
