package org.drools.drl.ast.descr;

import java.util.Collections;
import java.util.List;

public class EvalDescr extends BaseDescr
    implements
    ConditionalElementDescr {
    private static final long serialVersionUID = 510l;

    private Object            content;

    private String[]          declarations;

    private String            classMethodName;

    public EvalDescr() { }

    public EvalDescr(final Object content) {
        this.content = content;
    }

    public Object getContent() {
        return this.content;
    }

    public void setContent(final Object content) {
        this.content = content;
    }

    public String getClassMethodName() {
        return this.classMethodName;
    }

    public void setClassMethodName(final String classMethodName) {
        this.classMethodName = classMethodName;
    }

    public void setDeclarations(final String[] declarations) {
        this.declarations = declarations;
    }

    public String[] getDeclarations() {
        return this.declarations;
    }

    public List getDescrs() {
        return Collections.EMPTY_LIST;
    }

    public void addDescr(final BaseDescr baseDescr) {
        throw new UnsupportedOperationException("Can't add descriptors to "+this.getClass().getName());
    }
    
    public void insertBeforeLast(final Class clazz ,final BaseDescr baseDescr ) {
        throw new UnsupportedOperationException( "Can't add descriptors to " + this.getClass().getName() );
    }

    public void addOrMerge(BaseDescr baseDescr) {
        throw new UnsupportedOperationException("Can't add descriptors to "+this.getClass().getName());
    }

     public boolean removeDescr(BaseDescr baseDescr) {
        throw new UnsupportedOperationException("Can't remove descriptors from "+this.getClass().getName());
    }
    
    @Override
    public String toString() {
        return content.toString();
    }

    public static final EvalDescr TRUE = new DummyEvalDescr();

    private static class DummyEvalDescr extends EvalDescr {
        public DummyEvalDescr() {
            super("true");
        }
    }

    public void accept(DescrVisitor visitor) {
        visitor.visit(this);
    }
}
