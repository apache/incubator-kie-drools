package org.drools.quarkus.quickstart.test.model;

import java.util.Objects;

public class CCTV {
    private final String name;
    private Boolean powered;

    public CCTV(String name, Boolean powered) {
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
        CCTV other = (CCTV) obj;
        return Objects.equals(name, other.name) && Objects.equals(powered, other.powered);
    }

    @Override
    public String toString() {
        return "CCTV [name=" + name + ", powered=" + powered + "]";
    }
}
