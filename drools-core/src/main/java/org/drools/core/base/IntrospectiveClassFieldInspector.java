/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.base;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.core.kie.impl.MessageImpl;
import org.kie.api.io.Resource;
import org.kie.internal.builder.InternalMessage;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.ResultSeverity;

import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isPublic;

/**
 * Visit a POJO user class, and extract the property getter methods that are public, in the 
 * order in which they are declared actually in the class itself (not using introspection).
 *
 * This may be enhanced in the future to allow annotations or perhaps external meta data
 * configure the order of the indexes, as this may provide fine tuning options in special cases.
 */
public class IntrospectiveClassFieldInspector implements ClassFieldInspector {

    private final Class<?>                classUnderInspection;

    private final Map<String, Integer>    fieldNames           = new HashMap<String, Integer>();
    private final Map<String, Class< ? >> fieldTypes           = new HashMap<String, Class< ? >>();
    private final Map<String, Field>      fieldTypesField      = new HashMap<String, Field>();
    private final Map<String, Method>     getterMethods        = new HashMap<String, Method>();
    private final Map<String, Method>     setterMethods        = new HashMap<String, Method>();
    private final Set<String>             nonGetters           = new HashSet<String>();
    private Map<String, Collection<KnowledgeBuilderResult>>
                                          results              = null;

    /**
     * @param classUnderInspection The class that the fields to be shadowed are extracted for.
     * @throws IOException
     */
    public IntrospectiveClassFieldInspector( final Class< ? > classUnderInspection) throws IOException {
        this( classUnderInspection, true );
    }

    public IntrospectiveClassFieldInspector( final Class< ? > classUnderInspection,
                                             final boolean includeFinalMethods) throws IOException {
        this.classUnderInspection = classUnderInspection;
        processClassWithoutByteCode( classUnderInspection, includeFinalMethods );
    }

    private void processClassWithoutByteCode( final Class< ? > clazz,
                                              final boolean includeFinalMethods ) {
        final List<Method> methods = Arrays.asList( clazz.getMethods() );
        // different JVMs might return the methods in different order, so has to be sorted in order 
        // to be compatible with all JVMs
        Collections.sort( methods,  new Comparator<Method>() {
            public int compare(Method m1,
                               Method m2) {
                String n1 = m1.getName();
                String n2 = m2.getName();
                if ( n1.equals( n2 ) && m1.getDeclaringClass() != m2.getDeclaringClass() ) {
                    return m1.getDeclaringClass().isAssignableFrom( m2.getDeclaringClass() ) ? -1 : 1;
                } else {
                    return n1.compareTo( n2 );
                }
            }
        });

        for ( Method method : methods ) {
            // modifiers mask
            if ( acceptMethod( method, includeFinalMethods ) ) {
                if ( method.getParameterTypes().length == 0 &&
                     !method.getName().equals( "<init>" ) && !method.getName().equals( "<clinit>" ) &&
                     method.getReturnType() != void.class && !method.isDefault() ) {

                    // want public methods that start with 'get' or 'is' and have no args, and return a value
                    final int fieldIndex = this.fieldNames.size();
                    addToMapping( method, fieldIndex );

                } else if ( method.getParameterTypes().length == 1 && method.getName().startsWith( "set" ) ) {

                    // want public methods that start with 'set' and have one arg
                    final int fieldIndex = this.fieldNames.size();
                    addToMapping( method, fieldIndex );
                }
            }
        }

        final List<Field> flds = Arrays.asList( clazz.getFields() );
        Collections.sort( flds, new Comparator<Field>() {
            public int compare( Field f1, Field f2 ) {
                return f1.getName().compareTo( f2.getName() );
            }
        } );

        for ( Field fld : flds ) {
            if ( ! Modifier.isStatic( fld.getModifiers() ) && ! fieldNames.containsKey( fld.getName() ) ) {
                final int fieldIndex = this.fieldNames.size();
                this.fieldNames.put( fld.getName(), fieldIndex );
                this.fieldTypes.put( fld.getName(), fld.getType() );
                this.fieldTypesField.put( fld.getName(), fld );
            }
        }
    }

    private boolean acceptMethod( Method method, boolean includeFinalMethods ) {
        int modifiers = method.getModifiers();
        if ( !isPublic(modifiers) ) {
            return false;
        }
        return includeFinalMethods || !isFinal(modifiers);
    }

    /**
     * Return a mapping of the field "names" (ie bean property name convention)
     * to the numerical index by which they can be accessed.
     */
    public Map<String, Integer> getFieldNames() {
        return this.fieldNames;
    }

    /**
     * sotty:
     * Checks whether a returned field is actually a getter or not
     *
     * @param name the field to test
     * @return true id the name does not correspond to a getter field
     */
    public boolean isNonGetter( String name ) {
        return nonGetters.contains( name );
    }

    /**
     * @return A mapping of field types (unboxed).
     */
    public Map<String, Field> getFieldTypesField() {
        return this.fieldTypesField;
    }

    /**
     * @return A mapping of field types (unboxed).
     */
    public Map<String, Class< ? >> getFieldTypes() {
        return this.fieldTypes;
    }

    public Class< ? > getFieldType(String name) {
        return this.fieldTypes.get(name);
    }

    /**
     * @return A mapping of methods for the getters. 
     */
    public Map<String, Method> getGetterMethods() {
        return this.getterMethods;
    }

    /**
     * @return A mapping of methods for the getters. 
     */
    public Map<String, Method> getSetterMethods() {
        return this.setterMethods;
    }

    private void addToMapping( final Method method,
                               final int index ) {
        final String name = method.getName();
        int offset;
        if ( name.startsWith( "is" ) ) {
            offset = 2;
        } else if ( name.startsWith( "get" ) || name.startsWith( "set" ) ) {
            offset = 3;
        } else {
            offset = 0;
        }
        final String fieldName = calcFieldName( name,
                                                offset );
        if ( this.fieldNames.containsKey( fieldName ) ) {
            //only want it once, the first one thats found
            if ( offset != 0 && this.nonGetters.contains( fieldName ) ) {
                //replace the non getter method with the getter one
                Integer oldIndex = removeOldField( fieldName );
                storeField( oldIndex,
                            fieldName );
                storeGetterSetter( method,
                                   fieldName );
                this.nonGetters.remove( fieldName );
            } else if ( offset != 0 ) {
                storeGetterSetter( method,
                                   fieldName );
            }
        } else {
            storeField( index,
                        fieldName );
            storeGetterSetter( method,
                               fieldName );

            if ( offset == 0 ) {
                // only if it is a non-standard getter method
                this.nonGetters.add( fieldName );
            }
        }
    }

    private Integer removeOldField( final String fieldName ) {
        Integer index = this.fieldNames.remove( fieldName );
        this.fieldTypes.remove( fieldName );
        this.getterMethods.remove( fieldName );
        return index;

    }

    private void storeField( final Integer index,
                             final String fieldName ) {
        this.fieldNames.put( fieldName,
                             index );
    }
    
    //class.getDeclaredField(String) doesn't walk the inheritance tree; this does
    private Map<String, Field> getAllFields(Class<?> type) {
        Map<String, Field> fields = new HashMap<String, Field>();
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            for(Field f : c.getDeclaredFields()) {
                fields.put(f.getName(), f);
            }
        }
        return fields;
    }

    private void storeGetterSetter( Method method,
                                    String fieldName ) {
        Field f =  getAllFields( classUnderInspection ).get( fieldName );
        if ( method.getName().startsWith( "set" ) && method.getParameterTypes().length == 1 ) {
            this.setterMethods.put( fieldName,
                                    method );
            if ( !fieldTypes.containsKey( fieldName ) ) {
                this.fieldTypes.put( fieldName,
                                     method.getParameterTypes()[0] );
            }
            if ( !fieldTypesField.containsKey( fieldName ) ) {
                this.fieldTypesField.put( fieldName,
                                          f );
            }
        } else if( ! void.class.isAssignableFrom( method.getReturnType() ) ) {
            Method existingMethod = getterMethods.get( fieldName );
            if ( existingMethod != null && !isOverride( existingMethod, method ) ) {
                if (method.getReturnType() != existingMethod.getReturnType() && method.getReturnType().isAssignableFrom(existingMethod.getReturnType())) {
                    // a more specialized getter (covariant overload) has been already indexed, so skip this one
                    return;
                } else {
                    addResult( fieldName, new GetterOverloadWarning( classUnderInspection,
                                                                     this.getterMethods.get( fieldName ).getName(), this.fieldTypes.get( fieldName ),
                                                                     method.getName(), method.getReturnType() ) );
                }
            }
            this.getterMethods.put( fieldName,
                                    method );
            this.fieldTypes.put( fieldName,
                                 method.getReturnType() );
            this.fieldTypesField.put( fieldName,
                                      f );
        }
    }

    private boolean isOverride( Method oldMethod, Method newMethod ) {
        return !oldMethod.getDeclaringClass().equals( newMethod.getDeclaringClass() ) &&
               oldMethod.getDeclaringClass().isAssignableFrom( newMethod.getDeclaringClass() );
    }

    private String calcFieldName( String name,
                                  final int offset ) {
        name = name.substring( offset );
        return ClassFieldReader.decapitalizeFieldName( name );
    }

    public Collection<KnowledgeBuilderResult> getInspectionResults( String fieldName ) {
        return results != null && results.containsKey( fieldName ) ? results.get( fieldName ) : Collections.EMPTY_LIST;
    }

    private void addResult( String fieldName, KnowledgeBuilderResult result ) {
        Map<String, Collection<KnowledgeBuilderResult>> results = getResults();
        Collection<KnowledgeBuilderResult> fieldResults = results.get( fieldName );
        if ( fieldResults == null ) {
            fieldResults = new ArrayList<KnowledgeBuilderResult>( 3 );
            results.put( fieldName, fieldResults );
        }
        fieldResults.add( result );
    }


    protected Map<String, Collection<KnowledgeBuilderResult>> getResults() {
        if ( results == null ) {
            results = new HashMap<String, Collection<KnowledgeBuilderResult>>( );
        }
        return results;
    }

    public static class GetterOverloadWarning implements KnowledgeBuilderResult {

        private Class klass;
        private String oldName;
        private Class oldType;
        private String newName;
        private Class newType;

        public GetterOverloadWarning( Class klass, String oldName, Class oldType, String newName, Class newType ) {
            this.klass = klass;
            this.oldName = oldName;
            this.oldType = oldType;
            this.newName = newName;
            this.newType = newType;
        }

        public ResultSeverity getSeverity() {
            return ResultSeverity.WARNING;
        }


        public String getMessage() {
            return " Getter overloading detected in class " + klass.getName() + " : " + oldName + " (" + oldType + ") vs " + newName + " (" + newType + ") ";
        }


        public int[] getLines() {
            return new int[ 0 ];
        }

        public Resource getResource() {
            return null;
        }

        @Override
        public InternalMessage asMessage(long id) {
            return new MessageImpl(id, this);
        }

    }


}
