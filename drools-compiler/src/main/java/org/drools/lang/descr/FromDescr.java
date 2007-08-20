package org.drools.lang.descr;

import java.util.Collections;
import java.util.List;

public class FromDescr extends PatternSourceDescr
    implements
    ConditionalElementDescr
    {

    private static final long serialVersionUID = 400L;
    private DeclarativeInvokerDescr dataSource;

    public DeclarativeInvokerDescr getDataSource() {
        return this.dataSource;
    }

    public void setDataSource(final DeclarativeInvokerDescr dataSource) {
        this.dataSource = dataSource;
    }

    public void addDescr(final BaseDescr baseDescr) {
        throw new UnsupportedOperationException("Can't add descriptors to "+this.getClass().getName());        
    }
    
    public void insertBeforeLast(final Class clazz ,final BaseDescr baseDescr ) { 
        throw new UnsupportedOperationException( "Can't add descriptors to " + this.getClass().getName() );
    }    

    public List getDescrs() {
        return Collections.EMPTY_LIST;
    }

    public void addOrMerge(BaseDescr baseDescr) {
        throw new UnsupportedOperationException("Can't add descriptors to "+this.getClass().getName());        
    }


}
