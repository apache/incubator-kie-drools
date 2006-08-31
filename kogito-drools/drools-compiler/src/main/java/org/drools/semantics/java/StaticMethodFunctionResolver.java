package org.drools.semantics.java;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;

import org.drools.RuntimeDroolsException;
import org.drools.spi.FunctionResolver;
import org.drools.spi.TypeResolver;

public class StaticMethodFunctionResolver
    implements
    FunctionResolver {

    private final List         functionImports;

    private final TypeResolver typeResolver;

    public StaticMethodFunctionResolver(final List imports,
                                        final TypeResolver typeResolver) {
        this.functionImports = imports;

        this.typeResolver = typeResolver;
    }

    public void addFunctionImport(String functionImport) {
        this.functionImports.add( functionImport );

    }

    public List getFunctionImports() {
        return this.functionImports;
    }

    public String resolveFunction(String functionName,
                                  int numberOfArgs) {
        for ( Iterator it = this.functionImports.iterator(); it.hasNext(); ) {
            String functionImport = (String) it.next();

            // find the last '.' so we can separte the method/* from the package and Class
            int lastDot = functionImport.lastIndexOf( '.' );
            String name = functionImport.substring( lastDot );

            // if a functionImport imports the function name directly, i.e not using *, then only go further if they match.
            if ( !name.endsWith( "*" ) && !!functionName.equals( name ) ) {
                continue;
            }

            Class clazz;
            try {
                clazz = typeResolver.resolveType( functionImport.substring( 0,
                                                                            lastDot ) );
            } catch ( ClassNotFoundException e ) {
                // todo : must be a better way so we don't have to try/catch each resolveType call
                throw new RuntimeDroolsException( e );
            }
            Method[] methods = clazz.getMethods();
            for ( int i = 0, length = methods.length; i < length; i++ ) {
                if ( (methods[i].getModifiers() & Modifier.STATIC) == Modifier.STATIC && (methods[i].getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC && methods[i].getName().equals( functionName )
                     && methods[i].getParameterTypes().length == numberOfArgs ) {
                    return clazz.getName().replace( '$',
                                                    '.' );
                }
            }
        }
        throw new RuntimeDroolsException( "unable to find the function '" + functionName + "'" );
    }

}
