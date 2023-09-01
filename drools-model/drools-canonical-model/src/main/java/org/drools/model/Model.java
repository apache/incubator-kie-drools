package org.drools.model;

import java.util.List;

public interface Model {

    String getName();

    default String getPackageName() {
        return getName();
    }

    List<Global> getGlobals();

    List<Rule> getRules();

    List<Query> getQueries();

    List<TypeMetaData> getTypeMetaDatas();

    List<EntryPoint> getEntryPoints();
}
