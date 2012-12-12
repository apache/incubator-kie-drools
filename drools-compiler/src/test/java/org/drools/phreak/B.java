package org.drools.phreak;

public class B {
    Object object;

    public B(Object object) {
        super();
        this.object = object;
    }
    
    public static B b(Object object) {
        return new B( object );
    }

    public static B[] b(Object... objects) {
        B[] bs = new B[objects.length];
        int i = 0;
        for ( Object object : objects ) {
            bs[i++] = new B( object );
        }
        return bs;
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
        B other = (B) obj;
        if ( object == null ) {
            if ( other.object != null ) return false;
        } else if ( !object.equals( other.object ) ) return false;
        return true;
    }

    @Override
    public String toString() {
        return "B [" + object + "]";
    }

}