package org.drools.impact.analysis.graph;

public class Link {

    private Node source;
    private Node target;
    private ReactivityType reactivityType;

    public Link(Node source, Node target, ReactivityType reactivityType) {
        super();
        this.source = source;
        this.target = target;
        this.reactivityType = reactivityType;
    }

    public Node getSource() {
        return source;
    }

    public void setSource(Node source) {
        this.source = source;
    }

    public Node getTarget() {
        return target;
    }

    public void setTarget(Node target) {
        this.target = target;
    }

    public ReactivityType getReactivityType() {
        return reactivityType;
    }

    public void setReactivityType(ReactivityType reactivityType) {
        this.reactivityType = reactivityType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((reactivityType == null) ? 0 : reactivityType.hashCode());
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        result = prime * result + ((target == null) ? 0 : target.hashCode());
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
        Link other = (Link) obj;
        if (reactivityType != other.reactivityType)
            return false;
        if (source == null) {
            if (other.source != null)
                return false;
        } else if (!source.equals(other.source))
            return false;
        if (target == null) {
            if (other.target != null)
                return false;
        } else if (!target.equals(other.target))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Link [source=" + source.getRuleName() + ", target=" + target.getRuleName() + ", type=" + reactivityType + "]";
    }
}
