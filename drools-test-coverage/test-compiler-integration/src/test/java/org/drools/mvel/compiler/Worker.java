package org.drools.mvel.compiler;

public class Worker {
    
    private String id;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    
    @Override
    public int hashCode() {
        return 3;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof Worker)) return false;
        Worker other = (Worker) obj;
        return nullSafeEquals(this.id, other.id);
    }
    
    private boolean nullSafeEquals(Object obj1, Object obj2) {
        if (obj1 == obj2) return true;
        if (obj1 == null && obj2 != null) return false;
        return obj1.equals(obj2);
    }
}
