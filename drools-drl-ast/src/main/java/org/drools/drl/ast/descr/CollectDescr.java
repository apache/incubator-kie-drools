package org.drools.drl.ast.descr;

import java.util.Collections;
import java.util.List;

/**
 * An AST class to describe "collect" conditional element
 */
public class CollectDescr extends PatternSourceDescr
    implements
    ConditionalElementDescr,
    PatternDestinationDescr
    {

    private static final long  serialVersionUID = 510l;

    private PatternDescr       inputPattern;
    private String             classMethodName;

    public int getLine() {
        return this.inputPattern.getLine();
    }

    public String getClassMethodName() {
        return this.classMethodName;
    }

    public void setClassMethodName(final String classMethodName) {
        this.classMethodName = classMethodName;
    }

    public String toString() {
        return "[Collect: input=" + this.inputPattern.getIdentifier() + "; objectType=" + this.inputPattern.getObjectType() + "]";
    }

    public void addDescr(final BaseDescr patternDescr) {
        throw new UnsupportedOperationException( "Can't add descriptors to " + this.getClass().getName() );
    }

    public boolean removeDescr(BaseDescr baseDescr) {
        throw new UnsupportedOperationException("Can't remove descriptors from "+this.getClass().getName());
    }
    
    public void insertBeforeLast(final Class<?> clazz ,final BaseDescr baseDescr ) {
        throw new UnsupportedOperationException( "Can't add descriptors to " + this.getClass().getName() );
    }

    public List<BaseDescr> getDescrs() {
        // nothing to do
        return Collections.emptyList();
    }

    public void addOrMerge(BaseDescr baseDescr) {
        throw new UnsupportedOperationException( "Can't add descriptors to " + this.getClass().getName() );
    }

    public PatternDescr getInputPattern() {
        return this.inputPattern;
    }

    public void setInputPattern(final PatternDescr inputPattern) {
        this.inputPattern = inputPattern;
    }

}
