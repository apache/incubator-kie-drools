package org.drools.core.meta.org.test;

import org.drools.core.metadata.MetadataHolder;

public class SubKlassImpl
    extends KlassImpl
    implements SubKlass, MetadataHolder {

    protected Integer subProp;

    public SubKlassImpl() {
        super();
    }

    public Integer getSubProp() {
        return subProp;
    }

    public void setSubProp(Integer value) {
        this.subProp = value;
    }

    private final SubKlass_ _ = new SubKlass_( this );

    public SubKlass_ get_() {
        return _;
    }

}

