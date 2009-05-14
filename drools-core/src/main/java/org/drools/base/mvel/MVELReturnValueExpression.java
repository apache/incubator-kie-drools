package org.drools.base.mvel;


import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.Package;
import org.drools.spi.FieldValue;
import org.drools.spi.ReturnValueExpression;
import org.drools.spi.Tuple;
import org.mvel2.MVEL;

public class MVELReturnValueExpression
    implements
    ReturnValueExpression,
    MVELCompileable,
    Externalizable  {
    private static final long       serialVersionUID = 400L;

    private MVELCompilationUnit unit;
    private String id;
    
    private Serializable      expr;
    private DroolsMVELFactory prototype;
    
    public MVELReturnValueExpression() {
    }

    public MVELReturnValueExpression(final MVELCompilationUnit unit,
                              final String id) {
        this.unit = unit;
        this.id = id;
    }    

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        id = in.readUTF();
        unit = ( MVELCompilationUnit ) in.readObject();
//        expr    = (Serializable)in.readObject();
//        prototype   = (DroolsMVELFactory)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF( id );
        out.writeObject( unit );
//        out.writeObject(expr);
//        out.writeObject(prototype);
    }
    
    public void compile(ClassLoader classLoader) {
        expr = unit.getCompiledExpression( classLoader );
        prototype = unit.getFactory( );
    }     
      

    public FieldValue evaluate(final Object object,
                               final Tuple tuple,
                               final Declaration[] previousDeclarations,
                               final Declaration[] requiredDeclarations,
                               final WorkingMemory workingMemory,
                               final Object ctx ) throws Exception {
        DroolsMVELFactory factory = (DroolsMVELFactory) ctx;
        factory.setContext( tuple,
                                 null,
                                 object,
                                 workingMemory,
                                 null );

        // do we have any functions for this namespace?
        Package pkg = workingMemory.getRuleBase().getPackage( "MAIN" );
        if ( pkg != null ) {
            MVELDialectRuntimeData data = ( MVELDialectRuntimeData ) pkg.getDialectRuntimeRegistry().getDialectData( this.id );
            factory.setNextFactory( data.getFunctionFactory() );
        }

        return org.drools.base.FieldFactory.getFieldValue( MVEL.executeExpression( this.expr,
                                                                                   null,
                                                                                   factory ) );
    }

    public Object createContext() {
        return this.prototype.clone();
    }
    
    public String toString() {
        return this.unit.getExpression();
    }    

}
