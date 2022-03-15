package org.drools.compiler.xpath.tobeinstrumented.model;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class PojoWithCollections {

    private final Collection fieldCollection;
    private final List fieldList;
    private final Set fieldSet;

    public PojoWithCollections(Collection fieldCollection,
                               List fieldList,
                               Set fieldSet) {
        super();
        this.fieldCollection = fieldCollection;
        this.fieldList = fieldList;
        this.fieldSet = fieldSet;
    }

    public Collection getFieldCollection() {
        return fieldCollection;
    }

    public List getFieldList() {
        return fieldList;
    }

    public Set getFieldSet() {
        return fieldSet;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PojoWithCollections [fieldCollection=").append(fieldCollection).append(", fieldList=").append(fieldList).append(", fieldSet=").append(fieldSet).append("]");
        return builder.toString();
    }
}
