package org.drools.base.mvel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.List;

import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.Package;
import org.drools.WorkingMemory;
import org.drools.spi.ProcessContext;
import org.drools.spi.ReturnValueEvaluator;
import org.mvel2.MVEL;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.debug.DebugTools;
import org.mvel2.integration.impl.SimpleValueResolver;

public class MVELReturnValueEvaluator
    implements
    ReturnValueEvaluator,
    MVELCompileable,
    Externalizable {
    private static final long   serialVersionUID = 400L;

    private MVELCompilationUnit unit;
    private String              id;

    private Serializable        expr;
    private DroolsMVELFactory   prototype;
    private List<String>        variableNames;

    public MVELReturnValueEvaluator() {
    }

    public MVELReturnValueEvaluator(final MVELCompilationUnit unit,
                                    final String id) {
        this.unit = unit;
        this.id = id;
    }

    public void setVariableNames(List<String> variableNames) {
    	this.variableNames = variableNames;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        id = in.readUTF();
        unit = (MVELCompilationUnit) in.readObject();
        variableNames = (List<String>) in.readObject();
        //        expr    = (Serializable)in.readObject();
        //        prototype   = (DroolsMVELFactory)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF( id );
        out.writeObject( unit );
        out.writeObject(variableNames);
        //        out.writeObject(expr);
        //        out.writeObject(prototype);
    }

    public void compile(ClassLoader classLoader) {
        expr = unit.getCompiledExpression( classLoader );
        prototype = unit.getFactory();
    }

    public String getDialect() {
        return this.id;
    }

    public Object evaluate(final WorkingMemory workingMemory,
                           ProcessContext context) throws Exception {
        DroolsMVELFactory factory = (DroolsMVELFactory) this.prototype.clone();
        
        factory.addResolver("context", new SimpleValueResolver(context));
        factory.addResolver("kcontext", new SimpleValueResolver(context));
        if (variableNames != null) {
        	for (String variableName: variableNames) {
        		factory.addResolver(
    				variableName, new SimpleValueResolver(context.getVariable(variableName)));
        	}
        }
        
        factory.setContext( null,
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

        CompiledExpression compexpr = (CompiledExpression) this.expr;

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
    
    public String toString() {
        return this.unit.getExpression();
    }    

}
