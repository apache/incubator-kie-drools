package org.drools.builder.impl;

import org.drools.KBaseUnit;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.kproject.KProject;

import java.util.Map;

import static org.drools.builder.impl.EvictionCache.MINUTE;

public class KBaseUnitCachingFactory {

    private static final long EVICTION_TIME = 10 * MINUTE;

    private static final Map<String, KBaseUnit> cache = new EvictionCache<String, KBaseUnit>(EVICTION_TIME);

    static KBaseUnit getOrCreateKBaseUnit(KnowledgeBuilderConfiguration kConf, KProject kProject, String kBaseName) {
        KBaseUnit unit = cache.get(kBaseName);
        if (unit == null) {
            unit = new KBaseUnitImpl(kConf, kProject, kBaseName);
            cache.put(kBaseName, unit);
        }
        return unit;
    }
}
