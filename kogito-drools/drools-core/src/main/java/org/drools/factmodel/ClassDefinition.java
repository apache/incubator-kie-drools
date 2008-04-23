package org.drools.factmodel;


import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p><b>Title:</b> ClassDefinition</p>
 * <p><b>Description:</b> Declares a class to be dinamically created</p>
 * <p><b>Copyright:</b> Copyright (c) 2004-2006</p>
 * <p><b>Company:</b> Auster Solutions</p>
 *
 * @author etirelli
 * @version $Id: ClassDefinition.java 216 2006-03-24 19:16:31Z etirelli $
 */
public class ClassDefinition {
    private String                       className;
    private String                       superClass;
    private String[]                     interfaces;
    private Class                        definedClass;

    private Map<String, FieldDefinition> fields = new LinkedHashMap<String, FieldDefinition>();

    public ClassDefinition(String className) {
        this( className,
              null,
              null );
    }

    public ClassDefinition(String className,
                           String superClass) {
        this( className,
              superClass,
              null );
    }

    public ClassDefinition(String className,
                           String[] interfaces) {
        this( className,
              null,
              interfaces );
    }

    public ClassDefinition(String className,
                           String superClass,
                           String[] interfaces) {
        this.setClassName( className );
        this.setSuperClass( superClass );
        this.setInterfaces( interfaces );
    }

    /**
     * @return Returns the name.
     */
    public final String getClassName() {
        return this.className;
    }

    /**
     * @param name The name to set.
     */
    public final void setClassName(final String className) {
        this.className = className;
    }

    /**
     * @return the class name replacing '.' by '/'
     */
    final String getClassNameAsInternal() {
        return this.getClassName().replace( '.',
                                            '/' );
    }

    /**
     * @return Returns the name.
     */
    public final String getInternalName() {
        return "L" + this.getClassNameAsInternal() + ";";
    }

    /**
     * @return Returns the className.
     */
    public final Class getDefinedClass() {
        return definedClass;
    }

    /**
     * @param className The className to set.
     * @throws IntrospectionException 
     * @throws NoSuchFieldException 
     * @throws InvocationTargetException 
     * @throws NoSuchMethodException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     * @throws IllegalArgumentException 
     * @throws SecurityException 
     */
    final void setDefinedClass(final Class definedClass) throws IntrospectionException,
                                                        SecurityException,
                                                        IllegalArgumentException,
                                                        InstantiationException,
                                                        IllegalAccessException,
                                                        IOException,
                                                        ClassNotFoundException,
                                                        NoSuchMethodException,
                                                        InvocationTargetException,
                                                        NoSuchFieldException {

        this.definedClass = definedClass;

        if ( this.definedClass != null ) {
            this.buildFieldAccessors();
        }
    }

    /**
     * Adds a field definition to this class
     * @param attr
     */
    public final void addField(FieldDefinition attr) {
        this.fields.put( attr.getName(),
                         attr );
    }

    /**
     * @return Returns an unmodifiable collection of field definitions
     */
    public final Collection<FieldDefinition> getFields() {
        return Collections.unmodifiableCollection( this.fields.values() );
    }

    /**
     * Returns the field definition object for the given field name
     * 
     * @param fieldName
     * @return
     */
    public final FieldDefinition getField(final String fieldName) {
        return this.fields.get( fieldName );
    }

    /**
     * @param beanInfo The beanInfo to set.
     * @throws NoSuchFieldException 
     * @throws InvocationTargetException 
     * @throws NoSuchMethodException 
     * @throws ClassNotFoundException 
     * @throws IntrospectionException 
     * @throws IOException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     * @throws IllegalArgumentException 
     * @throws SecurityException 
     * @throws IntrospectionException 
     */
    public final void buildFieldAccessors() throws SecurityException,
                                           IllegalArgumentException,
                                           InstantiationException,
                                           IllegalAccessException,
                                           IOException,
                                           IntrospectionException,
                                           ClassNotFoundException,
                                           NoSuchMethodException,
                                           InvocationTargetException,
                                           NoSuchFieldException {
        FieldAccessorBuilder builder = new FieldAccessorBuilder();

        for ( FieldDefinition attrDef : this.fields.values() ) {
            FieldAccessor accessor = (FieldAccessor) builder.buildAndLoadFieldAccessor( this.getDefinedClass(),
                                                                                        attrDef.getName() ).newInstance();
            attrDef.setFieldAccessor( accessor );
        }
    }

    /**
     * @return Returns the interfaces.
     */
    public final String[] getInterfaces() {
        return interfaces;
    }

    /**
     * @param interfaces The interfaces to set.
     */
    public final void setInterfaces(String[] interfaces) {
        this.interfaces = (interfaces != null) ? interfaces : new String[0];
    }

    /**
     * @return
     */
    final String[] getInterfacesAsInternal() {
        String[] interfaces = new String[this.interfaces.length];
        for ( int i = 0; i < interfaces.length; i++ ) {
            interfaces[i] = this.interfaces[i].replace( '.',
                                                        '/' );
        }
        return interfaces;
    }

    /**
     * @return Returns the superClass.
     */
    public final String getSuperClass() {
        return superClass;
    }

    /**
     * @param superClass The superClass to set.
     */
    public final void setSuperClass(final String superClass) {
        this.superClass = (superClass != null) ? superClass : "java/lang/Object";
    }

    /**
     * @return Returns superclass name in the internal String representation
     */
    final String getSuperClassAsInternal() {
        return this.superClass.replace( '.',
                                        '/' );
    }

}