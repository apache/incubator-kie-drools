package org.drools.base;

import java.util.HashMap;
import java.util.Map;

/**
 * As class field Extractors have some cost to generate
 * (inspecting the class, and generating classes via ASM)
 * it makes sense to cache them.
 * This is that cache.
 * 
 * @author Michael Neale
 *
 */
public class ClassFieldExtractorCache {
    private static ClassFieldExtractorCache instance = new ClassFieldExtractorCache();
    private static Map                      cache;

    public static ClassFieldExtractorCache getInstance() {
        return instance;
    }

    private ClassFieldExtractorCache() {

    }

    public static ClassFieldExtractor getExtractor(final Class clazz,
                                                   final String fieldName) {
        return getExtractor( clazz,
                             fieldName,
                             null );
    }

    public static ClassFieldExtractor getExtractor(final Class clazz,
                                                   final String fieldName,
                                                   ClassLoader classLoader) {
        if ( cache == null ) {
            cache = new HashMap();
        }

        final String key = clazz.getName() + "|" + fieldName;

        if ( cache.containsKey( key ) ) {
            return (ClassFieldExtractor) cache.get( key );
        } else {
            final ClassFieldExtractor ex = new ClassFieldExtractor( clazz,
                                                                    fieldName,
                                                                    classLoader );
            cache.put( key,
                       ex );
            return ex;
        }
    }

}
