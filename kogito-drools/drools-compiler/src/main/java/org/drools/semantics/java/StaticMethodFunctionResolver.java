package org.drools.semantics.java;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jfdi.interpreter.TypeResolver;
import org.drools.RuntimeDroolsException;
import org.drools.spi.AvailableVariables;
import org.drools.spi.FunctionResolver;

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
                                  String params) {
        return resolveFunction(functionName,
                               params,
                               null );      
    }

    public String resolveFunction(String functionName,
                                  String params,
                                  AvailableVariables variables) {
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
            MethodResolver methodResolver = new MethodResolver( clazz,
                                                                functionName );
            
            
            Class[] classes = determineParameterTypes( params, variables );
            
            Method method = methodResolver.resolveMethod( classes );

            if ( method != null && methodResolver.isStaticMethod() ) {
                   return clazz.getName().replace( '$',
                                                   '.' );
            }            
        }
        throw new RuntimeDroolsException( "unable to find the function '" + functionName + "'" );
    }

    /**
     * pass a string representation of parameters and determine the used class types
     * @param paramString
     * @return
     */
    private Class[] determineParameterTypes(String paramString, AvailableVariables variables) {
        if ( paramString.trim().equals( "" ) ) {
            return new Class[0];
        }
        String[] params = paramString.split( "," );
        Class[] classes = new Class[params.length];

        for ( int i = 0, length = params.length; i < length; i++ ) {
            String param = params[i].trim();
            if ( param.charAt( 0 ) == '"' && param.charAt( param.length() - 1 ) == '"' ) {
                // we have a string literal
                classes[i] = String.class;
            } else if ( Character.getType( param.charAt( 0 ) ) == Character.DECIMAL_DIGIT_NUMBER ) {
                // we have a number

                char c = param.charAt( param.length() - 1 );
                if ( param.indexOf( '.' ) == -1 ) {
                    /// we have an integral
                    if ( Character.getType( c ) != Character.DECIMAL_DIGIT_NUMBER ) {
                        switch ( c ) {
                            case 'l' :
                            case 'L' :
                                classes[i] = Long.class;
                                break;
                            case 'f' :
                            case 'F' :
                                classes[i] = Float.class;
                                break;
                            case 'd' :
                            case 'D' :
                                classes[i] = Double.class;
                                break;
                            default :
                                throw new IllegalArgumentException( "invalid type identifier '" + c + "' used with number [" + param + "]" );
                        }
                    } else {
                        classes[i] = Integer.class;
                    }
                } else {
                    // we have a decimal
                    if ( Character.getType( c ) != Character.DECIMAL_DIGIT_NUMBER ) {
                        switch ( c ) {
                            case 'l' :
                            case 'L' :
                                throw new IllegalArgumentException( "invalid type identifier '" + c + "' used with number [" + param + "]" );
                            case 'f' :
                            case 'F' :
                                classes[i] = Float.class;
                                break;
                            case 'd' :
                            case 'D' :
                                classes[i] = Double.class;
                                break;
                            default :
                                throw new IllegalArgumentException( "invalid type identifier '" + c + "' used with number [" + param + "]" );
                        }
                    } else {
                        classes[i] = Float.class;
                    }
                }
            } else if ( param.startsWith( "new" ) ) {
                //We have a new instance, get its type
                int start = 3;
                int wordLength = param.length();
                // scan to start of first word
                for ( int j = start; j < wordLength; j++ ) {
                    if ( param.charAt( j ) != ' ') {
                        break;
                    }
                    start++;
                }
                
                int end = start;
                // now scan to end of the first word           
                for ( int j = start; j <= wordLength; j++ ) {
                    char c = param.charAt( j );
                    if (  c == ' ' || c == '(' ) {
                        break;
                    }
                    end++;
                }       
            
                char[] word = new char[end-start];
                // now copy the word         
                int k = 0;
                for ( int j = start; j < end; j++ ) {
                    word[k++]= param.charAt( j );
                }                 
            
                Class clazz = null;
                try {
                    clazz = this.typeResolver.resolveType( new String( word ) );
                } catch ( Exception e ) {
                    throw new IllegalArgumentException( "Unable to resovle type [" + new String( word )  + "]" );
                }
                classes[i] = clazz;
                
                
            } else {
                // we have a varable
                if ( variables != null ) {
                    classes[i] = variables.getType( param );
                }
            }

        }
        return classes;
    }
}
