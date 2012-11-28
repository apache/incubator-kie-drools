package org.drools.builder.impl;

import org.kie.builder.KieBaseModel;

import java.net.URL;
import java.util.Map;

import static org.drools.builder.impl.EvictionCache.MINUTE;

public class KBaseUnitCachingFactory {

    private static final long EVICTION_TIME = 10 * MINUTE;

    private static final Map<String, KBaseUnitImpl> cache = new EvictionCache<String, KBaseUnitImpl>(EVICTION_TIME);

    static KBaseUnitImpl getOrCreateKBaseUnit(URL url, KieBaseModel kieBaseModel) {
        String kBaseName = kieBaseModel.getName();
        KBaseUnitImpl unit = cache.get(kBaseName);
        if (unit == null) {
            unit = new KBaseUnitImpl(url, kieBaseModel);
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
