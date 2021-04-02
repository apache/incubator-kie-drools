package org.drools.mvelcompiler.context;

public class Declaration {
    private String name;
    private Class<?> clazz;

    public Declaration(String name, Class<?> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    @Override
    public String toString() {
        return "Declaration{" +
                "name='" + name + '\'' +
                ", clazz=" + clazz +
                '}';
    }
}
