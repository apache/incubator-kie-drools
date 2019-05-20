package org.drools.mvelcompiler.context;

public class Declaration {
    private String name;
    private Class<?> clazz;

    private boolean created = false;

    public Declaration(String name, Class<?> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    public Declaration(String name, Class<?> clazz, boolean created) {
        this(name, clazz);
        this.created = created;
    }

    public String getName() {
        return name;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Boolean getCreated() {
        return created;
    }
}
