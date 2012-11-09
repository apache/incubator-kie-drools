package org.drools.builder.impl;

import org.drools.KBaseUnit;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.kproject.KBase;

import java.util.Map;

import static org.drools.builder.impl.EvictionCache.MINUTE;

public class KBaseUnitCachingFactory {

    private static final long EVICTION_TIME = 10 * MINUTE;

    private static final Map<String, KBaseUnitImpl> cache = new EvictionCache<String, KBaseUnitImpl>(EVICTION_TIME);

    static KBaseUnitImpl getOrCreateKBaseUnit(String url, KBase kBase) {
        String kBaseName = kBase.getQName();
        KBaseUnitImpl unit = cache.get(kBaseName);
        if (unit == null) {
            unit = new KBaseUnitImpl(url, kBase);
            cache.put(kBaseName, unit);
        }
        return unit;
    }

    static void evictKBaseUnit(String kBaseName) {
        cache.remove(kBaseName);
    }

    static void clear() {
        cache.clear();
    }
}
