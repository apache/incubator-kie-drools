package org.jbpm.task.test;

import java.io.Serializable;

public class MyObject implements Serializable {

    private String name;

    public MyObject(String name) {
        this.name = name;
    }

    public String getValue() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MyObject other = (MyObject) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "MyObject{" + "name=" + name + '}';
    }
}
