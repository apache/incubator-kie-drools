package org.codehaus.jfdi.interpreter;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * This creates value handers to attach to nodes in the AST.
 * This is an abstract factory, of which there will be a Drools implementation 
 * (but could also be a stand alone one).
 * 
 * @author Michael Neale
 */
public abstract class AbstractValueHandlerFactory
    implements
    Serializable,
    ValueHandlerFactory {

    private static final long    serialVersionUID = 320L;

    protected final TypeResolver typeResolver;    
    
    protected  String[] requiredVariables;

    public AbstractValueHandlerFactory(TypeResolver typeResolver) {
        this.typeResolver = typeResolver;
    }

    /* (non-Javadoc)
     * @see org.codehaus.jfdi.interpreter.ValueHandlerFactory#createLiteral(int, java.lang.String)
     */
    public ValueHandler createLiteral(Class cls,
                                      String val) {
        if ( cls.getClass() == String.class ) {
            return new LiteralValue( val );
        } else {
            Object objectValue = null;
            try {
                objectValue = cls.getDeclaredConstructor( new Class[]{String.class} ).newInstance( new Object[]{val} );
            } catch ( Exception e ) {
                throw new RuntimeException( e );
            }
            return new LiteralValue( objectValue );
        }
    }

    /* (non-Javadoc)
     * @see org.codehaus.jfdi.interpreter.ValueHandlerFactory#createLocalVariable(java.lang.String, java.lang.String, boolean)
     */
    public ValueHandler createLocalVariable(String identifier,
                                            String type,
                                            boolean isFinal) {
        Class clazz = null;
        try {
            clazz = this.typeResolver.resolveType( type );
        } catch ( ClassNotFoundException e ) {
            throw new RuntimeException( e );
        }

        return new LocalVariable( identifier,
                                  clazz,
                                  isFinal );
    }

    /* (non-Javadoc)
     * @see org.codehaus.jfdi.interpreter.ValueHandlerFactory#createExternalVariable(java.lang.String)
     */
    public abstract ValueHandler createExternalVariable(String identifier);
    
    protected void registerExternalVariable(String identifier) {
        // Increase the size of the array and add the new variable
        if ( this.requiredVariables == null ) {
            this.requiredVariables = new  String[] { identifier  };
        }  else {
            String[] newArray = new String[ this.requiredVariables.length + 1];
            System.arraycopy(this.requiredVariables, 
                             0, newArray, 0, this.requiredVariables.length );
            newArray[ newArray.length -1 ] = identifier;
            this.requiredVariables = newArray;
        }
    }    
        
    public String[] getRequiredVariables() {
        return this.requiredVariables;
    }    

    public static class MapValue {

        private List list;

        public MapValue() {
            list = new ArrayList();
        }

        public void add(KeyValuePair pair) {
            this.list.add( pair );
        }

        public KeyValuePair[] getKeyValuePairs() {
            return (KeyValuePair[]) this.list.toArray( new KeyValuePair[this.list.size()] );
        }
    }

    public static class KeyValuePair {
        private ValueHandler key;
        private ValueHandler value;

        public KeyValuePair(ValueHandler key,
                            ValueHandler value) {
            this.key = key;
            this.value = value;
        }

        public ValueHandler getKey() {
            return this.key;
        }

        public ValueHandler getValue() {
            return this.value;
        }
    }

}
