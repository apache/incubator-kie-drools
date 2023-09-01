package org.drools.drl.ast.descr;

import java.util.Collections;
import java.util.List;

public class FromDescr extends PatternSourceDescr
    implements
    ConditionalElementDescr
    {

    private static final long serialVersionUID = 510l;
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
    
    public void insertBeforeLast(final Class<?> clazz ,final BaseDescr baseDescr ) {
        throw new UnsupportedOperationException( "Can't add descriptors to " + this.getClass().getName() );
    }

    public List<BaseDescr> getDescrs() {
        return Collections.emptyList();
    }

    public void addOrMerge(BaseDescr baseDescr) {
        throw new UnsupportedOperationException("Can't add descriptors to "+this.getClass().getName());
    }

    public boolean removeDescr(BaseDescr baseDescr) {
        throw new UnsupportedOperationException("Can't remove descriptors from "+this.getClass().getName());
    }

    public String toString() {
        return "from "+this.dataSource.toString();
    }
    
    @Override
    public String getText() {
        return this.toString();
    }

    public String getExpression() {
        return getDataSource().getText();
    }

    public void accept(DescrVisitor visitor) {
        visitor.visit(this);
    }
}
