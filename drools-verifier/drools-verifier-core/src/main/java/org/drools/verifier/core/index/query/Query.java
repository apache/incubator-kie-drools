package org.drools.verifier.core.index.query;

import org.drools.verifier.core.index.matchers.Matcher;

public class Query {

    private final Matcher matcher;
    private final String mapId;

    public Query(final String mapId,
                 final Matcher matcher) {
        this.mapId = mapId;
        this.matcher = matcher;
    }

    public Matcher getMatcher() {
        return matcher;
    }

    public String getMapId() {
        return mapId;
    }
}
