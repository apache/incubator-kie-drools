package org.drools.drl.ast.descr;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class UnitDescr extends BaseDescr {

    private static final long serialVersionUID = 510l;
    private String            target;

    public UnitDescr() {
        this( null );
    }

    public UnitDescr( final String clazzName ) {
        this.target = clazzName;
    }

    public String getTarget() {
        return this.target;
    }

    public void setTarget(final String clazzName) {
        this.target = clazzName;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((this.target == null) ? 0 : this.target.hashCode());
        result = PRIME * result + this.getStartCharacter();
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(final Object obj) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final UnitDescr other = (UnitDescr) obj;
        if ( this.target == null ) {
            if ( other.target != null ) {
                return false;
            }
        } else if ( !this.target.equals( other.target ) ) {
            return false;
        }
        return this.getStartCharacter() == other.getStartCharacter();
    }

    public String toString() {
        return "unit " + this.target;
    }

    public void readExternal( ObjectInput in ) throws IOException,
            ClassNotFoundException {
        super.readExternal( in );
        target = (String) in.readObject();
    }

    public void writeExternal( ObjectOutput out ) throws IOException {
        super.writeExternal( out );
        out.writeObject( target );
    }
}
