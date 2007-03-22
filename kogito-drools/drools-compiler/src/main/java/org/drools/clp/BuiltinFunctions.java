package org.drools.clp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class BuiltinFunctions {
    public static final BuiltinFunctions instance = new BuiltinFunctions();
    
    public static BuiltinFunctions getInstance() {
        return instance;
    }
    
    public Map functions;
    
    private BuiltinFunctions() {
        this.functions = new HashMap();
        
        try {
            loadFunctions();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        } catch ( ClassNotFoundException e ) {
            throw new RuntimeException( e );
        }
    }        
    
    public void loadFunctions() throws IOException, ClassNotFoundException {        
        BufferedReader reader = new BufferedReader( new InputStreamReader( getClass().getResourceAsStream( "functions.conf" ) ) );
        loadFunctions( reader );
    }
    
    public void loadFunctions(BufferedReader reader) throws IOException, ClassNotFoundException {
        String line = null;       
        
        try {
            while( (line = reader.readLine() ) != null ) {
                Class clazz = getClass().getClassLoader().loadClass( line );
                Function function = ( Function) clazz.newInstance();
                this.functions.put(  function.getName(), function );
            }
        } catch(IllegalAccessException e) {
            throw new RuntimeException( e );            
        } catch(InstantiationException e) {
            throw new RuntimeException( e );
        }
    }
    
    public Function getFunction(String name) {
        return ( Function ) this.functions.get( name );
    }
}
