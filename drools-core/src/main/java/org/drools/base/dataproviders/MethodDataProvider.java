package org.drools.base.dataproviders;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.RuntimeDroolsException;
import org.drools.WorkingMemory;
import org.drools.base.evaluators.DateFactory;
import org.drools.rule.Declaration;
import org.drools.spi.DataProvider;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;

public class MethodDataProvider
    implements
    DataProvider {

    private final Declaration[] requiredDeclarations;
    private final boolean variableIsDeclaration;
    private final Declaration variableDeclaration;
    private final String      variableName;
    
    private final ValueHandler[]      valueHandlers;

    private final Method        method;
    private final Class variableClass;

    public MethodDataProvider(String variableName,
                              String methodName,
                              List arguments,
                              Map declarations,
                              Map globals) {
        
        List requiredDecs = new ArrayList();
        
        //work out where variable comes from, is it a dec or a global
        this.variableName = variableName;        
        if (declarations.containsKey( variableName )) {
            variableDeclaration = (Declaration) declarations.get( variableName );
            requiredDecs.add( variableDeclaration );
            variableIsDeclaration = true;
            this.variableClass = variableDeclaration.getExtractor().getExtractToClass();
        } else if (globals.containsKey( variableName )) {
            variableIsDeclaration = false;
            this.variableClass = (Class) globals.get( variableName );
            variableDeclaration = null;
        } else {
            variableDeclaration = null;
            throw new IllegalArgumentException("The variable name [" + variableName + "] was not a global or declaration.");
        }

        //now handle arguments
        List argumentData = new ArrayList();        
        
        for ( Iterator iter = arguments.iterator(); iter.hasNext(); ) {
            ArgumentValueDescr desc = (ArgumentValueDescr) iter.next();
            if ( desc.getType() == ArgumentValueDescr.VARIABLE ) {
                if ( declarations.containsKey( desc.getValue() ) ) {
                    Declaration dec = (Declaration) declarations.get( desc.getValue() );
                    requiredDecs.add( dec );
                    argumentData.add( new DeclaredVariable(dec) );
                } else if ( globals.containsKey( desc.getValue() ) ) {
                    argumentData.add( new GlobalVariable(desc.getValue()) );
                } else {
                    throw new IllegalArgumentException( "Uknown variable: " + desc.getValue() );
                }
            } else {
                // handling a literal
                argumentData.add( new LiteralValue(desc.getValue()) );
            }
        }

        //now find the method
        method = configureMethod(methodName, variableClass, arguments.size());
        
        valueHandlers = (ValueHandler[]) argumentData.toArray(new ValueHandler[argumentData.size()]);
        requiredDeclarations = new Declaration[requiredDecs.size()];
        requiredDecs.toArray( requiredDeclarations );
    }

    /**
     * work out what method we will be calling at runtime, based on the name and number of parameters.
     */
    private Method configureMethod(String methodName, Class variableClass, int numOfArgs) {
        Method[] methods = this.variableClass.getMethods();
        for ( int i = 0; i < methods.length; i++ ) {
            if (methods[i].getName().equals(methodName)) {
                if (methods[i].getParameterTypes().length == numOfArgs) {
                    return methods[i];
                }
            }
        }
        return null;
    }

    public Declaration[] getRequiredDeclarations() {
        return requiredDeclarations;
    }

    public Iterator getResults(Tuple tuple,
                               WorkingMemory wm,
                               PropagationContext ctx) {
        //get the variable value that we are operating on
        Object variable = null;
        if (variableIsDeclaration) {   
            variable = tuple.get( this.variableDeclaration ).getObject();            
        } else {
            variable = wm.getGlobal( this.variableName );
        }
        
        
        //the types we have to convert the arguments to
        Class[] parameterTypes = this.method.getParameterTypes();
        
        //the args values that we will pass
        Object[] args = new Object[this.valueHandlers.length];
        
        
        //now we need to set all the values, convert if literal
        for ( int i = 0; i < this.valueHandlers.length; i++ ) {
            ValueHandler handler = valueHandlers[i];
            if (handler instanceof LiteralValue) {
                String text = (String) handler.getValue( tuple, wm );
                Class type = parameterTypes[i];
                if (type == String.class) {
                    args[i] = text;
                } else {
                    args[i] = convert(text, type);
                }
            } else {
                args[i] = handler.getValue( tuple, wm );
            }
        }
        
        //now the actual invoking of the method
        try {
            Object result = this.method.invoke( variable, args );
            if (result instanceof Collection) {
                return ((Collection) result).iterator();
            } else if (result instanceof Iterator) {
                return (Iterator) result;
            } else {
                List resultAsList = new ArrayList(1);
                resultAsList.add( result );
                return resultAsList.iterator();
            }
        } catch ( IllegalArgumentException e ) {
            throw new RuntimeDroolsException(e);
        } catch ( IllegalAccessException e ) {
            throw new RuntimeDroolsException(e);        
        } catch ( InvocationTargetException e ) {
            throw new RuntimeDroolsException(e);        
        }

    }
    
    
    /** Attempt to convert text to the target class type */
    private Object convert(String text,
                           Class type) {
        if ( type == Integer.class || type == int.class) {
            return new Integer( text ) ;
        } else if ( text == "null" ) {
            return null;
        } else if ( type == Character.class || type == char.class ) {
            return ( new Character( text.charAt( 0 ) ) );
        } else if ( type == Short.class || type == short.class) {
            return new Short( text );
        } else if ( type == Long.class || type == long.class ) {
            return new Long( text );
        } else if ( type == Float.class || type == float.class) {
            return new Float( text );
        } else if ( type == Double.class || type == double.class) {
            return new Double( text );
        } else if ( type == Boolean.class || type == boolean.class) {
            return new Boolean( text );
        } else if ( type == Date.class ) {            
            return DateFactory.parseDate( text );
        } else if ( type == BigDecimal.class ) {
            return new BigDecimal(text);
        } else if ( type == BigInteger.class ) {
            return new BigInteger(text);
        } else {
            throw new IllegalArgumentException("Unable to convert [" + text + "] to type: [" + type.getName() + "]");
        }
    }


    static interface ValueHandler {
        
        Object getValue(Tuple tuple, WorkingMemory wm);
    }
    
    static class GlobalVariable implements ValueHandler {
        public String globalName;
        public GlobalVariable(String name) {
            this.globalName = name;
        }
        public Object getValue(Tuple tuple,
                               WorkingMemory wm) {
            return wm.getGlobal( globalName );
            
        }
    }
    
    static class DeclaredVariable implements ValueHandler {

        private Declaration declaration;

        public DeclaredVariable(Declaration dec) {
            this.declaration = dec;
        }
        
        public Object getValue(Tuple tuple,
                               WorkingMemory wm) {
            return tuple.get( this.declaration ).getObject();
        }
        
    }
    
    static class LiteralValue implements ValueHandler {

        private String value;

        public LiteralValue(String value) {
            this.value = value;
        }
        
        public Object getValue(Tuple tuple,
                               WorkingMemory wm) {            
            return value;
        }
        
    }
    


}
