package org.drools.quarkus.quickstart.test.model;

import java.util.Objects;

public class Light {
    private final String name;
    private Boolean powered;

    public Light(String name, Boolean powered) {
        this.name = name;
        this.powered = powered;
    }

    public Boolean getPowered() {
        return powered;
    }

    public void setPowered(Boolean powered) {
        this.powered = powered;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, powered);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Light other = (Light) obj;
        return Objects.equals(name, other.name) && Objects.equals(powered, other.powered);
    }

    @Override
    public String toString() {
        return "Light [name=" + name + ", powered=" + powered + "]";
    }
}
