package org.drools.factmodel;


import java.lang.reflect.InvocationTargetException;

/**
 * <p><b>Title:</b> FieldDefinition</p>
 * <p><b>Description:</b> Declares a field to be dynamically generated</p>
 * <p><b>Copyright:</b> Copyright (c) 2004-2006</p>
 * <p><b>Company:</b> Auster Solutions</p>
 *
 * @author etirelli
 * @version $Id: FieldDefinition.java 205 2006-03-21 18:27:52Z etirelli $
 */
public class FieldDefinition {
    private String             name         = null;
    private String             type         = null;
    private boolean            key          = false;
    
    private String             internalType = null;
    private String             boxTypeName  = null;
    private boolean            primitive    = false;
    private String             unboxMethod  = null;
    private FieldAccessor      accessor     = null;

    /**
     * Default constructor
     * 
     * @param name the field's name
     * @param type the fully qualified fields type
     */
    public FieldDefinition(String name,
                           String type) {
        this(name, type, false);
    }

    /**
     * Default constructor
     * 
     * @param name the field's name
     * @param type the fully qualified fields type
     */
    public FieldDefinition(String name,
                           String type, 
                           boolean key) {
        this.name = name;
        this.type = type;
        this.key  = key;
        this.setInternals();
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the fully qualified type.
     */
    public String getType() {
        return this.type;
    }

    /**
     * @param type The fully qualified type to set.
     */
    public void setType(String type) {
        this.type = type;
        this.setInternals();
    }

    /**
     * @return Returns the fully qualified internal type representation
     */
    String getInternalType() {
        return this.internalType;
    }

    String getBoxTypeName() {
        return this.boxTypeName;
    }

    /**
     * @return Returns the primitive.
     */
    boolean isPrimitive() {
        return primitive;
    }

    /**
     * @return Returns the unboxMethod.
     */
    String getUnboxMethod() {
        return unboxMethod;
    }

    /**
     * @return Returns the key.
     */
    public boolean isKey() {
        return key;
    }

    /**
     * @param key The key to set.
     */
    public void setKey(boolean key) {
        this.key = key;
    }

    /**
     * Creates the String name for the get method for a field with the given name and type
     * @param name
     * @param type
     * @return
     */
    public String getReadMethod() {
        String prefix = null;
        if ( "boolean".equals( this.type ) ) {
            prefix = "is";
        } else {
            prefix = "get";
        }
        return prefix + this.name.substring( 0,
                                             1 ).toUpperCase() + this.name.substring( 1 );
    }

    /**
     * Creates the String name for the set method for a field with the given name and type
     * 
     * @param name
     * @param type
     * @return
     */
    public String getWriteMethod() {
        return "set" + this.name.substring( 0,
                                            1 ).toUpperCase() + this.name.substring( 1 );
    }

    /**
     * @inheritDoc
     */
    public boolean equals(Object o) {
        return this.getName().equals( ((FieldDefinition) o).getName() );
    }

    /**
     * @return Returns the field accessor
     */
    FieldAccessor getFieldAccessor() {
        return this.accessor;
    }

    /**
     * @param property The property descriptor to set.
     */
    void setFieldAccessor(FieldAccessor accessor) {
        this.accessor = accessor;
    }

    /**
     * @inheritDoc
     */
    public int hashCode() {
        return this.getName().hashCode();
    }

    /**
     * Sets the value of this attribute in the target
     * bean instance
     * 
     * @param bean the target bean instance where the attribute shall be set
     * @param value the value to set the attribute to
     * 
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public void setValue(Object bean,
                         Object value) throws IllegalArgumentException,
                                      IllegalAccessException,
                                      InvocationTargetException {
        if(this.accessor == null) {
            System.out.println("ACCESSOR NULL");
        }
        this.accessor.setValue( bean, value );
    }

    /**
     * Returns the value of this attribute in the target bean instance
     * 
     * @param bean the target bean instance
     * 
     * @return target bean instance attribute value
     *  
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public Object getValue(Object bean) throws IllegalArgumentException,
                                       IllegalAccessException,
                                       InvocationTargetException {
        return this.accessor.getValue( bean );
    }

    /**
     * Creates the internal string representation of this attribute's type
     * 
     * @param type
     * @return
     */
    private void setInternals() {
        if ( "byte".equals( this.type ) ) {
            this.internalType = "B";
            this.boxTypeName = "java/lang/Byte";
            this.primitive = true;
            this.unboxMethod = "byteValue";
        } else if ( "char".equals( type ) ) {
            this.internalType = "C";
            this.boxTypeName = "java/lang/Character";
            this.primitive = true;
            this.unboxMethod = "charValue";
        } else if ( "double".equals( type ) ) {
            this.internalType = "D";
            this.boxTypeName = "java/lang/Double";
            this.primitive = true;
            this.unboxMethod = "doubleValue";
        } else if ( "float".equals( type ) ) {
            this.internalType = "F";
            this.boxTypeName = "java/lang/Float";
            this.primitive = true;
            this.unboxMethod = "floatValue";
        } else if ( "int".equals( type ) ) {
            this.internalType = "I";
            this.boxTypeName = "java/lang/Integer";
            this.primitive = true;
            this.unboxMethod = "intValue";
        } else if ( "long".equals( type ) ) {
            this.internalType = "J";
            this.boxTypeName = "java/lang/Long";
            this.primitive = true;
            this.unboxMethod = "longValue";
        } else if ( "short".equals( type ) ) {
            this.internalType = "S";
            this.boxTypeName = "java/lang/Short";
            this.primitive = true;
            this.unboxMethod = "shortValue";
        } else if ( "boolean".equals( type ) ) {
            this.internalType = "Z";
            this.boxTypeName = "java/lang/Boolean";
            this.primitive = true;
            this.unboxMethod = "booleanValue";
        } else if ( "void".equals( type ) ) {
            this.internalType = "V";
            this.boxTypeName = "java/lang/Void";
            this.primitive = true;
            this.unboxMethod = null;
        } else if ( type != null ) {
            this.internalType = "L" + type.replace( '.',
                                                    '/' ) + ";";
            this.boxTypeName = type.replace( '.',
                                             '/' );
            this.primitive = false;
            this.unboxMethod = null;
        }
    }

}