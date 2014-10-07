package org.drools.core.meta.org.test;

import org.drools.core.metadata.MetadataHolder;

import java.util.List;

public class SubKlassImpl
    extends KlassImpl
    implements SubKlass, MetadataHolder {

    protected Integer subProp;

    protected List<AnotherKlass> links;

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

    public List<AnotherKlass> getLinks() {
        return links;
    }

    public void setLinks( List<AnotherKlass> links ) {
        this.links = links;
    }
}

