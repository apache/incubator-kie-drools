package org.drools.clp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class FunctionRegistry {
    public Map              functions;

    public BuiltinFunctions builtin;

    //public Map variables;        

    public FunctionRegistry(BuiltinFunctions builtin) {
        this.functions = new HashMap();
        //this.variables = new HashMap();

        this.builtin = builtin;

        try {
            loadFunctions();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        } catch ( ClassNotFoundException e ) {
            throw new RuntimeException( e );
        }
    }

    public void loadFunctions() throws IOException,
                               ClassNotFoundException {
        BufferedReader reader = new BufferedReader( new InputStreamReader( getClass().getResourceAsStream( "functions.conf" ) ) );
        loadFunctions( reader );
    }

    public void loadFunctions(BufferedReader reader) throws IOException,
                                                    ClassNotFoundException {
        String line = null;

        try {
            while ( (line = reader.readLine()) != null ) {
                Class clazz = getClass().getClassLoader().loadClass( line );
                Function function = (Function) clazz.newInstance();
                addFunction( function );
            }
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException( e );
        } catch ( InstantiationException e ) {
            throw new RuntimeException( e );
        }
    }

    public Function getFunction(String name) {
        Function function = (Function) this.functions.get( name );

        if ( function == null ) {
            // Allows for late binidng FunctionDelegators
            function = new FunctionDelegator( name );
            this.functions.put( name,
                                function );
        }
        return function;
    }

    //    public Function createFunction(String name) {
    //        try {
    //            Class clazz = ( Class ) this.functions.get( name );
    //            if ( clazz == null ) {
    //                throw new RuntimeException( "Function '" + name + "' does not exist" );
    //            }
    //            return (Function) clazz.newInstance();
    //        } catch ( InstantiationException e ) {
    //            throw new RuntimeException( e );
    //        } catch ( IllegalAccessException e ) {
    //            throw new RuntimeException( e );
    //        }
    //    }        

    public void addFunction(Function function) {
        FunctionDelegator delegator = (FunctionDelegator) this.functions.get( function.getName() );

        if ( delegator == null ) {
            delegator = new FunctionDelegator( function.getName() );
            this.functions.put( function.getName(),
                                delegator );
        }
        delegator.setFunction( function );
    }

    //    public void init() {
    //        this.variables.clear();
    //    }
    //    
    //    public void setVariable(LocalVariableValue variable) {
    //        this.variables.put( variable.getIdentifier(), variable );
    //    }
    //    
    //    public VariableValueHandler getVariable(String identifier) {
    //        VariableValueHandler variable = ( VariableValueHandler ) this.variables.get( identifier );
    //        if ( identifier == null ) {
    //            variable = new TempTokenVariable( identifier );
    //            this.variables.put( identifier, variable );
    //        }
    //        return variable;
    //    }

    public int getFunctionSize() {
        return this.functions.size();
    }

    //    public int getVariablesSize() {
    //        return this.variables.size();
    //    }
}
