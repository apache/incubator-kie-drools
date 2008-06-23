package org.drools.base.mvel;

import org.drools.WorkingMemory;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.Package;
import org.drools.spi.ReturnValueEvaluator;
import org.mvel.MVEL;
import org.mvel.compiler.CompiledExpression;
import org.mvel.debug.DebugTools;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

public class MVELReturnValueEvaluator
    implements
    ReturnValueEvaluator,
    Externalizable {
    private static final long       serialVersionUID = 400L;

    private Serializable      expr;
    private DroolsMVELFactory prototype;

    public MVELReturnValueEvaluator() {
    }

    public MVELReturnValueEvaluator(final Serializable expr,
                                    final DroolsMVELFactory factory) {
        this.expr = expr;
        this.prototype = factory;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        expr    = (Serializable)in.readObject();
        prototype   = (DroolsMVELFactory)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(expr);
        out.writeObject(prototype);
    }

    public String getDialect() {
        return "mvel";
    }

    public Object evaluate(final WorkingMemory workingMemory) throws Exception {
        DroolsMVELFactory factory = (DroolsMVELFactory) this.prototype.clone();
        factory.setContext( null,
                            null,
                            null,
                            workingMemory,
                            null );
        
        Package pkg = workingMemory.getRuleBase().getPackage( "MAIN" );
        if ( pkg != null ) {
            MVELDialectRuntimeData data = ( MVELDialectRuntimeData ) pkg.getDialectRuntimeRegistry().getDialectData( "mvel" );
            factory.setNextFactory( data.getFunctionFactory() );
        }
        
        CompiledExpression compexpr = (CompiledExpression) this.expr;

        //Receive breakpoints from debugger
        MVELDebugHandler.prepare();

        Object value;
        if ( MVELDebugHandler.isDebugMode() ) {
            if ( MVELDebugHandler.verbose ) {
                System.out.println( DebugTools.decompile( compexpr ) );
            }
            value = MVEL.executeDebugger( compexpr,
                                          null,
                                          factory );
        } else {
            value = MVEL.executeExpression( compexpr,
                                            null,
                                            factory );
        }

        if ( !(value instanceof Boolean) ) {
            throw new RuntimeException( "Constraints must return boolean values" );
        }
        return ((Boolean) value).booleanValue();

    }

    public Serializable getCompExpr() {
        return expr;
    }

}
