package org.drools.mvel.integrationtests.phreak;

import org.kie.api.definition.type.Position;

public class C {

    @Position(0)
    Object object;

    public C(Object object) {
        super();
        this.object = object;
    }

    public static C c(Object object) {
        return new C( object );
    }

    public static C[] c(Object... objects) {
        C[] cs = new C[objects.length];
        int i = 0;
        for ( Object object : objects ) {
            cs[i++] = new C( object );
        }
        return cs;
    }        

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((object == null) ? 0 : object.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        C other = (C) obj;
        if ( object == null ) {
            if ( other.object != null ) return false;
        } else if ( !object.equals( other.object ) ) return false;
        return true;
    }

    @Override
    public String toString() {
        return "C [" + object + "]";
    }

}
