package org.drools.rule;

import java.io.Serializable;
import java.util.Map;

import org.drools.spi.DataProvider;

public class From extends ConditionalElement
    implements
    Serializable {

    private static final long serialVersionUID = -2640290731776949513L;

    private Pattern            pattern;

    private DataProvider      dataProvider;

    public From(final Pattern pattern,
                final DataProvider dataProvider) {
        this.pattern = pattern;
        this.dataProvider = dataProvider;
    }

    public Pattern getPattern() {
        return this.pattern;
    }

    public DataProvider getDataProvider() {
        return this.dataProvider;
    }

    public Object clone() {
        // TODO Auto-generated method stub
        return null;
    }

    public Map getInnerDeclarations() {
        return this.pattern.getInnerDeclarations();
    }

    public Map getOuterDeclarations() {
        return this.pattern.getOuterDeclarations();
    }

    /**
     * @inheritDoc
     */
    public Declaration resolveDeclaration(final String identifier) {
        return (Declaration) this.pattern.getInnerDeclarations().get( identifier );
    }

}
