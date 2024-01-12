package org.drools.util;

public class IncompatibleGetterOverloadException extends RuntimeException {

    private static final long serialVersionUID = -2359365094676377091L;
    private Class<?> klass;
    private String oldName;
    private Class<?> oldType;
    private String newName;
    private Class<?> newType;

    public IncompatibleGetterOverloadException(Class<?> klass, String oldName, Class<?> oldType, String newName, Class<?> newType) {
        super(" Incompatible Getter overloading detected in class " + klass.getName() + " : " + oldName + " (" + oldType + ") vs " + newName + " (" + newType + ") ");
        this.klass = klass;
        this.oldName = oldName;
        this.oldType = oldType;
        this.newName = newName;
        this.newType = newType;
    }

    public Class<?> getKlass() {
        return klass;
    }

    public String getOldName() {
        return oldName;
    }

    public Class<?> getOldType() {
        return oldType;
    }

    public String getNewName() {
        return newName;
    }

    public Class<?> getNewType() {
        return newType;
    }

}
