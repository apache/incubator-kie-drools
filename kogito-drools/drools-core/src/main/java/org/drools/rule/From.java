package org.drools.rule;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import org.drools.spi.DataProvider;

public class From extends ConditionalElement
    implements
    Serializable,
    PatternSource {

    private static final long serialVersionUID = -2640290731776949513L;

    private DataProvider      dataProvider;

    public From(final DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public DataProvider getDataProvider() {
        return this.dataProvider;
    }

    public Object clone() {
        // TODO Auto-generated method stub
        return null;
    }

    public Map getInnerDeclarations() {
        return Collections.EMPTY_MAP;
    }

    public Map getOuterDeclarations() {
        return Collections.EMPTY_MAP;
    }

    /**
     * @inheritDoc
     */
    public Declaration resolveDeclaration(final String identifier) {
        return null;
    }

}
