package org.drools.quarkus.quickstart.test.model;

import java.util.Objects;

public class Smartphone {
    private final String name;

    public Smartphone(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Smartphone other = (Smartphone) obj;
        return Objects.equals(name, other.name);
    }

    @Override
    public String toString() {
        return "Smartphone [name=" + name + "]";
    }
}
