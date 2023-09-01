package org.drools.mvel.compiler.oopath.model;

public class TMFileWithParentObj extends TMFile {

    private long id;
    private Object parent;

    public TMFileWithParentObj(long id, String name, int size, Object parent) {
        super(name, size);
        this.id = id;
        this.parent = parent;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Object getParent() {
        return parent;
    }

    public void setParent(Object parent) {
        this.parent = parent;
    }
}
