package org.drools.base.mvel;

import java.io.Serializable;

import org.drools.rule.Declaration;
import org.drools.spi.KnowledgeHelper;
import org.mvel.integration.VariableResolver;

public class DroolsMVELKnowledgeHelper
    implements
    VariableResolver,
    Serializable  {

    private DroolsMVELFactory factory;
    public static final String DROOLS = "drools";

    public DroolsMVELKnowledgeHelper(final DroolsMVELFactory factory) {
        this.factory = factory;
    }

    public String getName() {
        return DROOLS;
    }

    public Class getKnownType() {
        return KnowledgeHelper.class;
    }

    public Object getValue() {
        return this.factory.getKnowledgeHelper();
    }    

    public void setValue(final Object value) {
        throw new UnsupportedOperationException( "External Variable identifer='" + getName() + "' type='" + getKnownType() + "' is final, it cannot be set" );
    }

    public int getFlags() {
        return 0;
    }

    /**
     * Not used in drools.
     */
    public Class getType() {
        return KnowledgeHelper.class;
    }

    /**
     * Not used in drools.
     */
    public void setStaticType(Class arg0) {
    }

}
