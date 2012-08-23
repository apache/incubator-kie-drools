package org.drools.lang.descr;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

public class NamedConsequenceDescr extends BaseDescr {

    private boolean breaking;

    public NamedConsequenceDescr() { }

    public NamedConsequenceDescr( String id ) {
        this.setText( id );
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        super.readExternal( in );
        breaking = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeBoolean( breaking );
    }

    public String getName() {
        return getText();
    }

    public void setName( String name) {
        setText( name );
    }

    public boolean isBreaking() {
        return breaking;
    }

    public void setBreaking(boolean breaking) {
        this.breaking = breaking;
    }

    @Override
    public String toString() {
        return (isBreaking() ? " break" : "do") + "[" + getName() + "]";
    }
}
