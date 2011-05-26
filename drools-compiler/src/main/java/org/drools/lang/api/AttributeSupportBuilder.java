package org.drools.lang.api;

import org.drools.lang.descr.BaseDescr;

public interface AttributeSupportBuilder<T extends BaseDescr> extends DescrBuilder<T> {

    public AttributeDescrBuilder attribute( String name );

}