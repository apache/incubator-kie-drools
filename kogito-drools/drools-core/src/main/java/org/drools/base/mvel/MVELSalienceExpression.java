package org.drools.base.mvel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.Package;
import org.drools.WorkingMemory;
import org.drools.spi.Salience;
import org.drools.spi.Tuple;
import org.mvel2.MVEL;

public class MVELSalienceExpression
    implements
    Salience,
    MVELCompileable,
    Externalizable {

    private static final long   serialVersionUID = 400L;

    private MVELCompilationUnit unit;
    private String              id;

    private Serializable        expr;
    private DroolsMVELFactory   prototype;

    public MVELSalienceExpression() {
    }

    public MVELSalienceExpression(final MVELCompilationUnit unit,
                                  final String id) {
        this.unit = unit;
        this.id = id;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        unit = (MVELCompilationUnit) in.readObject();
        id = in.readUTF();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( unit );
        out.writeUTF( id );
    }

    public void compile(ClassLoader classLoader) {
        expr = unit.getCompiledExpression( classLoader );
        prototype = unit.getFactory();
    }

    public int getValue(final Tuple tuple,
                        final WorkingMemory workingMemory) {
        DroolsMVELFactory factory = (DroolsMVELFactory) this.prototype.clone();
        factory.setContext( tuple,
                            null,
                            null,
                            workingMemory,
                            null );

        // do we have any functions for this namespace?
        Package pkg = workingMemory.getRuleBase().getPackage( "MAIN" );
        if ( pkg != null ) {
            MVELDialectRuntimeData data = (MVELDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData( this.id );
            factory.setNextFactory( data.getFunctionFactory() );
        }

        return ((Number) MVEL.executeExpression( this.expr,
                                                 factory )).intValue();
    }
    
    public String toString() {
        return this.unit.getExpression();
    }

}
