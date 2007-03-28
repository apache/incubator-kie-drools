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
        loadFunctions();
    }

    public void loadFunctions()  {
        BufferedReader reader = new BufferedReader( new InputStreamReader( getClass().getResourceAsStream( "functions.conf" ) ) );
        loadFunctions( reader );
    }

    public void loadFunctions(BufferedReader reader) {
        String line = null;

        try {
            while ( (line = reader.readLine()) != null ) {
                if ( line.startsWith( "#" ) || line.equals( "" )) {
                    continue;
                }
                Class clazz = getClass().getClassLoader().loadClass( line );
                Function function = (Function) clazz.newInstance();
                this.functions.put( function.getName(),
                                    function );
            }
        } catch ( Exception e ) {
            throw new RuntimeException( "unable to find function '" + line +"'" );
        }
    }

    public Function getFunction(String name) {
        return (Function) this.functions.get( name );
    }
}
