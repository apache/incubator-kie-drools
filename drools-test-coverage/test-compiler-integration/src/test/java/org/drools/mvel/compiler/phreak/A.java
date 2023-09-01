package org.drools.mvel.compiler.phreak;

import org.kie.api.definition.type.Position;

public class A {

    @Position(0)
    Integer object;

    public A(Integer object) {
        super();
        this.object = object;
    }
    
    public static org.drools.mvel.integrationtests.phreak.A a(Integer object) {
        return new org.drools.mvel.integrationtests.phreak.A(object );
    }

    public static org.drools.mvel.integrationtests.phreak.A[] a(Integer... objects) {
        org.drools.mvel.integrationtests.phreak.A[] as = new org.drools.mvel.integrationtests.phreak.A[objects.length];
        int i = 0;
        for ( Integer object : objects ) {
            as[i++] = new org.drools.mvel.integrationtests.phreak.A(object );
        }
        return as;
    }        

    public Object getObject() {
        return object;
    }

    public void setObject(Integer object) {
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
        A other = (A) obj;
        if ( object == null ) {
            if ( other.getObject() != null ) return false;
        } else if ( !object.equals( other.getObject() ) ) return false;
        return true;
    }

    @Override
    public String toString() {
        return "A[" + object + "]";
    }

}
