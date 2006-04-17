package org.drools.spi;

public class ColumnExtractor
    implements
    Extractor {

    private ObjectType objectType;

    public ColumnExtractor(ObjectType objectType) {
        this.objectType = objectType;
    }

    public Object getValue(Object object) {
        return object;
    }

    public ObjectType getObjectType() {
        return this.objectType;
    }

}
