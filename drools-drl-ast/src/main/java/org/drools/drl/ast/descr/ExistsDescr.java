package org.drools.drl.ast.descr;

import java.util.ArrayList;
import java.util.List;

public class ExistsDescr extends AnnotatedBaseDescr
    implements
    ConditionalElementDescr {

    private static final long serialVersionUID = 510l;
    private final List        descrs           = new ArrayList( 1 );

    public ExistsDescr() { }

    public ExistsDescr(final BaseDescr baseDescr) {
        addDescr( baseDescr );
    }

    public void addDescr(final BaseDescr baseDescr) {
        this.descrs.add( baseDescr );
    }
    
    public void insertBeforeLast(final Class clazz ,final BaseDescr baseDescr ) {
        throw new UnsupportedOperationException( "Can't add descriptors to " + this.getClass().getName() );
    }

    public List getDescrs() {
        return this.descrs;
    }

    public boolean removeDescr(BaseDescr baseDescr) {
        return baseDescr == null ? false : descrs.remove(baseDescr);
    }

    public void addOrMerge(BaseDescr baseDescr) {
        if( baseDescr instanceof ExistsDescr ) {
            this.descrs.addAll( ((ExistsDescr)baseDescr).getDescrs() );
        } else {
            this.descrs.add( baseDescr );
        }
    }

    @Override
    public void accept(DescrVisitor visitor) {
        visitor.visit(this);
    }
}
