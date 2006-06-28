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
    
    public ClassFieldExtractorCache(  ) {
        this.cache = new HashMap();
    }
    
    public ClassFieldExtractor getExtractor(Class clazz, String fieldName) {
        String key = clazz.getName() + "|" + fieldName;
        if (this.cache.containsKey( key )) {
            return (ClassFieldExtractor) cache.get( key );
        } else {
            ClassFieldExtractor ex = new ClassFieldExtractor(clazz, fieldName);
            cache.put( key, ex );
            return ex;
        }        
    }
}
