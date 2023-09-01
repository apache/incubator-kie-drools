package org.drools.drl.ast.descr;


import java.util.ArrayList;
import java.util.List;

public class NotDescr extends AnnotatedBaseDescr
    implements
    ConditionalElementDescr {

    private static final long serialVersionUID = 510l;
    private final List<BaseDescr> descrs = new ArrayList<>( 1 );

    public NotDescr() { }

    public NotDescr(final BaseDescr descr) {
        addDescr( descr );
    }

    public void addDescr(final BaseDescr baseDescr) {
        this.descrs.add( baseDescr );
    }
    
    public void insertBeforeLast(final Class clazz ,final BaseDescr baseDescr ) {
        throw new UnsupportedOperationException( "Can't add descriptors to " + this.getClass().getName() );
    }

    public List<BaseDescr> getDescrs() {
        return this.descrs;
    }

     public boolean removeDescr(BaseDescr baseDescr) {
        return baseDescr == null ? false : descrs.remove(baseDescr);
    }

    public void addOrMerge(BaseDescr baseDescr) {
        if( baseDescr instanceof NotDescr ) {
            this.descrs.addAll( ((NotDescr)baseDescr).getDescrs() );
        } else {
            this.descrs.add( baseDescr );
        }
    }

    @Override
    public void accept(DescrVisitor visitor) {
        visitor.visit(this);
    }
}
