package org.drools.factmodel;

/*
 * Copyright 2008 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.drools.base.ClassFieldAccessor;
import org.drools.base.ClassFieldAccessorCache;
import org.drools.base.ClassFieldReader;
import org.drools.base.ClassFieldWriter;
import org.drools.rule.FactField;
import org.drools.rule.FactType;

/**
 * Declares a class to be dynamically created
 *
 * @author etirelli
 */
public class ClassDefinition
    implements
    FactType {

    private String                       className;
    private String                       superClass;
    private String[]                     interfaces;
    private Class< ? >                   definedClass;

    private Map<String, FieldDefinition> fields = new LinkedHashMap<String, FieldDefinition>();

    public ClassDefinition() {
        this( null,
              null,
              null );
    }

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

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.className = (String) in.readObject();
        this.superClass = (String) in.readObject();
        this.interfaces = (String[]) in.readObject();
        this.definedClass = (Class<?>) in.readObject();
        this.fields = (Map<String, FieldDefinition>) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( this.className );
        out.writeObject( this.superClass );
        out.writeObject( this.interfaces );
        out.writeObject( this.definedClass );
        out.writeObject( this.fields );
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
     * @return Returns the className.
     */
    public final Class< ? > getDefinedClass() {
        return definedClass;
    }

    /**
     * @param className The class to set.
     */
    public void setDefinedClass(final Class< ? > definedClass) {

        this.definedClass = definedClass;
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
    public final Collection<FieldDefinition> getFieldsDefinitions() {
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
        ClassFieldAccessorCache cache = ClassFieldAccessorCache.getInstance();

        for ( FieldDefinition attrDef : this.fields.values() ) {
            ClassFieldReader reader = cache.getReader( this.getDefinedClass(),
                                                       attrDef.getName(),
                                                       this.getClass().getClassLoader() );
            ClassFieldWriter writer = cache.getWriter( this.getDefinedClass(),
                                                       attrDef.getName(),
                                                       this.getClass().getClassLoader() );
            ClassFieldAccessor accessor = new ClassFieldAccessor( reader,
                                                                  writer );
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
     * @return Returns the superClass.
     */
    public final String getSuperClass() {
        return superClass;
    }

    /**
     * @param superClass The superClass to set.
     */
    public final void setSuperClass(final String superClass) {
        this.superClass = (superClass != null) ? superClass : "java.lang.Object";
    }

    public String getName() {
        return getClassName();
    }

    public Object newInstance() throws InstantiationException,
                               IllegalAccessException {
        return this.definedClass.newInstance();
    }

    public Class< ? > getFactClass() {
        return getDefinedClass();
    }

    public List<FactField> getFields() {
        return new ArrayList<FactField>( fields.values() );
    }

}