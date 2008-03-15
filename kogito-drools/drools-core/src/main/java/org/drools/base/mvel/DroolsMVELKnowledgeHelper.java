package org.drools.base.mvel;

import java.io.Serializable;
import java.io.Externalizable;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;

import org.drools.spi.KnowledgeHelper;
import org.mvel.integration.VariableResolver;

public class DroolsMVELKnowledgeHelper
    implements
    VariableResolver,
    Externalizable  {

    private static final long serialVersionUID = 9175428283083361478L;
    
    private DroolsMVELFactory factory;
    public static final String DROOLS = "drools";

    public DroolsMVELKnowledgeHelper() {
    }

    public DroolsMVELKnowledgeHelper(final DroolsMVELFactory factory) {
        this.factory = factory;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        factory = (DroolsMVELFactory)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(factory);
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
