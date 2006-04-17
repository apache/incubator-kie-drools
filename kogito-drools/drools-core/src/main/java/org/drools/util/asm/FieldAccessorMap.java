package org.drools.util.asm;

import java.util.Map;

/**
 * This class holds a dynamically generated instance of a FieldAccessor, 
 * and a map of the field names to index numbers that are used to access the fields. 
 * @deprecated use ClassFiledExtractor instead.
 * @author Michael Neale
 */
public class FieldAccessorMap {

    private final FieldAccessor accessor;
    private final Map           nameMap;

    /**
     * @param accessor
     * @param fieldAccessMethods Will be used to calculate the "field name"
     * which is really like bean property names.
     */
    FieldAccessorMap(FieldAccessor accessor,
                     Map nameMap) {
        this.accessor = accessor;
        this.nameMap = nameMap;
    }

    /**
     * @return A map of field names, to their index value, for use by the accessor. 
     */
    public Map getFieldNameMap() {
        return this.nameMap;
    }

    /**
     * @return The field index accessor itself.
     */
    public FieldAccessor getFieldAccessor() {
        return accessor;
    }

    public int getIndex(String name) {
        return ((Integer) this.nameMap.get( name )).intValue();
    }
}
