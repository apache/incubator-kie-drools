package org.drools.impact.analysis.graph;

import org.drools.impact.analysis.model.Rule;

import java.util.Set;

public interface Node {

    enum Status {
        NONE,
        CHANGED,
        IMPACTED
    }

    static void linkNodes(Node source, Node target, Link.Type type) {
        // TODO: We may omit a link to oneself (Or it may be decided when rendering)
        Link link = new Link(source, target, type);
        source.getOutgoingLinks().add(link);
        target.getIncomingLinks().add(link);
    }

    String getId();

    Status getStatus();

    void setStatus(Status status);

    String getFqdn();

    String getPackageName();

    String getRuleName();

    default String getDescription() {
        return "";
    }

    Rule getRule();

    Set<Link> getIncomingLinks();

    Set<Link> getOutgoingLinks();
}
