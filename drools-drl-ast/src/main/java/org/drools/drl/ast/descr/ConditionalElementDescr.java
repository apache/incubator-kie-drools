package org.drools.drl.ast.descr;

import java.util.List;

public interface ConditionalElementDescr {

    void insertBeforeLast(final Class<?> clazz ,final BaseDescr baseDescr );
    
    void addDescr(BaseDescr baseDescr);

    boolean removeDescr(BaseDescr baseDescr);
    
    void addOrMerge(final BaseDescr baseDescr);
    
    List<? extends BaseDescr> getDescrs();
}
