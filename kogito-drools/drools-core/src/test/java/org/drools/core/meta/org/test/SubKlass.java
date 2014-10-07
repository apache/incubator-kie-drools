package org.drools.core.meta.org.test;

import java.util.List;

public interface SubKlass extends Klass {

    public Integer getSubProp();

    public void setSubProp( Integer value );

    public List<AnotherKlass> getLinks();

    public void setLinks( List<AnotherKlass> links );

}
