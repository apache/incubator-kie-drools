/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.feel.lang.runtime.functions;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Symbol;
import org.kie.dmn.feel.lang.runtime.FEELFunction;
import org.kie.dmn.feel.lang.runtime.NamedParameter;
import org.kie.dmn.feel.lang.types.FunctionSymbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BaseFEELFunction implements FEELFunction {

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private String name;
    private Symbol symbol;

    public BaseFEELFunction( String name ) {
        this.name = name;
        this.symbol = new FunctionSymbol( name, this );
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
        ((FunctionSymbol)this.symbol).setId( name );
    }

    @Override
    public Symbol getSymbol() {
        return symbol;
    }

    // TODO: can this method be improved somehow??
    @Override
    public Object applyReflectively(EvaluationContext ctx, Object[] params) {
        // use reflection to call the appropriate apply method
        try {
            if( params.length > 0 && params[0] instanceof NamedParameter ) {
                // using named parameters, so need to adjust
                List<List<String>> names = getParameterNames();
                names.sort( (o1, o2) -> o1.size() <= o2.size() ? -1 : +1 );

                List<String> available = Stream.of( params ).map( p -> ((NamedParameter)p).getName() ).collect( Collectors.toList() );

                boolean found = false;
                for( List<String> candidate : names ) {
                    if( candidate.containsAll( available ) ) {
                        Object[] newParams = new Object[candidate.size()];
                        for( Object o : params ) {
                            NamedParameter np = (NamedParameter) o;
                            newParams[ candidate.indexOf( np.getName() ) ] = np.getValue();
                        }
                        params = newParams;
                        found = true;
                        break;
                    }
                }
                if( !found ) {
                    logger.error( "Unable to find function "+getName()+"( "+available.stream().collect( Collectors.joining(", ") )+ " )" );
                    return null;
                }
            }
            if ( ! isCustomFunction() ) {
                Class[] classes = Stream.of( params ).map( p -> p != null ? p.getClass() : null ).toArray( Class[]::new );
                Method apply = null;
                // first, look for exact matches
                for( Method m : getClass().getDeclaredMethods() ) {
                    Class<?>[] parameterTypes = m.getParameterTypes();
                    if( !m.getName().equals( "apply" ) || parameterTypes.length != params.length  ) {
                        continue;
                    }
                    boolean found = true;
                    for( int i = 0; i < parameterTypes.length; i++ ) {
                        if ( classes[i] != null && ! parameterTypes[i].isAssignableFrom( classes[i] ) ) {
                            found = false;
                            break;
                        }
                    }
                    if( found ) {
                        apply = m;
                        break;
                    }
                }
                if( apply == null ) {
                    // if not found, look for a method with variable number of parameters that match
                    for( Method m : getClass().getDeclaredMethods() ) {
                        Class<?>[] parameterTypes = m.getParameterTypes();
                        if( !m.getName().equals( "apply" ) || ( parameterTypes.length > 0 && ! parameterTypes[parameterTypes.length-1].isArray() ) ) {
                            continue;
                        }
                        boolean found = true;
                        for( int i = 0; i < parameterTypes.length; i++ ) {
                            if ( i == parameterTypes.length-1 && parameterTypes[i].isArray() ) {
                                // last parameter is an array, so treat the method as variable number of parameters method
                                found = true;
                                Object[] newParams = new Object[i+1];
                                if( i > 0 ) {
                                    System.arraycopy( params, 0, newParams, 0, i );
                                }
                                Object[] remaining = new Object[params.length-i];
                                newParams[i] = remaining;
                                System.arraycopy( params, i, remaining, 0, remaining.length );
                                params = newParams;
                                break;
                            }
                            if ( classes[i] != null && ! parameterTypes[i].isAssignableFrom( classes[i] ) ) {
                                found = false;
                                break;
                            }
                        }
                        if( found ) {
                            apply = m;
                            break;
                        }
                    }
                }
                if( apply != null ) {
                    Object result = apply.invoke( this, params );
                    return result;
                } else {
                    String ps = Arrays.toString( classes );
                    logger.error( "Unable to find function '" + getName() + "( " + ps.substring( 1, ps.length()-1 ) +" )'" );
                }
            } else {
                Object result = null;
                if( this instanceof CustomFEELFunction ) {
                    result = ((CustomFEELFunction)this).apply( ctx, params );
                } else if( this instanceof JavaFunction ) {
                    result = ((JavaFunction)this).apply( ctx, params );
                } else {
                    logger.error( "Unable to find function '" + toString() +"'" );
                }
                return normalizeResult( result );
            }
        } catch ( Exception e ) {
            logger.error( "Error trying to call function "+getName()+".", e );
        }
        return null;
    }

    private Object normalizeResult(Object result) {
        // this is to normalize types returned by external functions
        return result != null && result instanceof Number && !(result instanceof BigDecimal) ? new BigDecimal( result.toString() ) : result;
    }

    protected boolean isCustomFunction() {
        return false;
    }

}
