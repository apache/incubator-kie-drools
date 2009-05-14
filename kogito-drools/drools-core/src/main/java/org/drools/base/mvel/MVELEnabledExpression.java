package org.drools.base.mvel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.spi.Enabled;
import org.drools.spi.Tuple;
import org.mvel2.MVEL;

public class MVELEnabledExpression
    implements
    Enabled,
    MVELCompileable,
    Externalizable {

    private static final long   serialVersionUID = 400L;

    private MVELCompilationUnit unit;
    private String              id;

    private Serializable        expr;
    private DroolsMVELFactory   prototype;

    public MVELEnabledExpression() {
    }

    public MVELEnabledExpression(final MVELCompilationUnit unit,
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

    public boolean getValue(final Tuple tuple,
                            final Rule rule,
                            final WorkingMemory workingMemory) {
        // it must be cloned for multi-thread safety
        DroolsMVELFactory factory = (DroolsMVELFactory) this.prototype.clone();
        factory.setContext( tuple,
        		            null,
                            null,
                            workingMemory,
                            null );
        factory.createVariable( "rule", rule, rule.getClass() );

        // do we have any functions for this namespace?
        Package pkg = workingMemory.getRuleBase().getPackage( "MAIN" );
        if ( pkg != null ) {
            MVELDialectRuntimeData data = (MVELDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData( this.id );
            factory.setNextFactory( data.getFunctionFactory() );
        }

        return ((Boolean) MVEL.executeExpression( this.expr,
                                                  null,
                                                  factory )).booleanValue();
    }
    
    public String toString() {
        return this.unit.getExpression();
    }    

}
