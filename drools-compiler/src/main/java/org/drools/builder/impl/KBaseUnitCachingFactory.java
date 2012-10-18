package org.drools.builder.impl;

import org.drools.KBaseUnit;
import org.drools.kproject.KProject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class KBaseUnitCachingFactory {

    private static final Map<String, KBaseUnit> cache = new ConcurrentHashMap<String, KBaseUnit>();

    static KBaseUnit getOrCreateKBaseUnit(KProject kProject, String kBaseName) {
        KBaseUnit unit = cache.get(kBaseName);
        if (unit == null) {
            unit = new KBaseUnitImpl(kProject, kBaseName);
            cache.put(kBaseName, unit);
        }
        return unit;
    }
}
