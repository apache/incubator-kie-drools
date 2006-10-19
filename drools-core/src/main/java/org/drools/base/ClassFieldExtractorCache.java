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
    private Map cache;

    public ClassFieldExtractorCache() {
        this.cache = new HashMap();
    }

    public ClassFieldExtractor getExtractor(final Class clazz,
                                            final String fieldName) {
        final String key = clazz.getName() + "|" + fieldName;
        if ( this.cache.containsKey( key ) ) {
            return (ClassFieldExtractor) this.cache.get( key );
        } else {
            final ClassFieldExtractor ex = new ClassFieldExtractor( clazz,
                                                              fieldName );
            this.cache.put( key,
                       ex );
            return ex;
        }
    }
}
