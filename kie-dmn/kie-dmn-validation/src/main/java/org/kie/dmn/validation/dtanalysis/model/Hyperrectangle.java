package org.kie.dmn.validation.dtanalysis.model;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Hyperrectangle {

    private final int dimensions;
    private final List<Interval> edges;

    public Hyperrectangle(int dimensions, List<Interval> edges) {
        super();
        this.dimensions = dimensions;
        this.edges = edges;
    }

    @Override
    public String toString() {
        return edges.stream().map(Interval::toString).collect(Collectors.joining(" "));
    }

    public int getDimensions() {
        return dimensions;
    }

    public List<Interval> getEdges() {
        return Collections.unmodifiableList(edges);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + dimensions;
        result = prime * result + ((edges == null) ? 0 : edges.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Hyperrectangle other = (Hyperrectangle) obj;
        if (dimensions != other.dimensions)
            return false;
        if (edges == null) {
            if (other.edges != null)
                return false;
        } else if (!edges.equals(other.edges))
            return false;
        return true;
    }

}
