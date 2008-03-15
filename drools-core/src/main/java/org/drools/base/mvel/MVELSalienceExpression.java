package org.drools.base.mvel;

import java.io.Serializable;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectInput;

import org.drools.WorkingMemory;
import org.drools.spi.Salience;
import org.drools.spi.Tuple;
import org.mvel.MVEL;

public class MVELSalienceExpression
    implements
    Salience,
    Externalizable {

    private static final long       serialVersionUID = 400L;

    private Serializable      expr;
    private DroolsMVELFactory factory;

    public MVELSalienceExpression() {
    }

    public MVELSalienceExpression(final Serializable expr,
                                  final DroolsMVELFactory factory) {
        this.expr = expr;
        this.factory = factory;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        expr    = (Serializable)in.readObject();
        factory = (DroolsMVELFactory)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(expr);
        out.writeObject(factory);
    }

    public int getValue(final Tuple tuple,
                        final WorkingMemory workingMemory) {
        this.factory.setContext( tuple,
                                 null,
                                 null,
                                 workingMemory,
                                 null );
        return ((Number) MVEL.executeExpression( this.expr,
                                                  this.factory )).intValue();
    }

}
