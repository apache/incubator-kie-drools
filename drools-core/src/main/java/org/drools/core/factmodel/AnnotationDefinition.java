/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.factmodel;


import org.drools.core.base.TypeResolver;
import org.kie.api.definition.type.Annotation;
import org.kie.api.definition.type.Role;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AnnotationDefinition implements Externalizable, Annotation {

    private String name;

    private Map<String,AnnotationPropertyVal> values;

    public AnnotationDefinition() { }

    public AnnotationDefinition(String name) {
        this.name = name;
        this.values = new HashMap<String, AnnotationPropertyVal>();
    }


    public Map<String, AnnotationPropertyVal> getValues() {
        return values;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.name = (String) in.readObject();
        this.values = (Map<String,AnnotationPropertyVal>) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( this.name );
        out.writeObject( this.values );
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnnotationDefinition that = (AnnotationDefinition) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (values != null ? !values.equals(that.values) : that.values != null) return false;

        return true;
    }

    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (values != null ? values.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "@" + name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getPropertyValue( String key ) {
        if ( values.containsKey( key ) ) {
            return values.get( key ).getValue();
        } else {
            return null;
        }
    }

    public Class getPropertyType( String key ) {
        if ( values.containsKey( key ) ) {
            return values.get( key ).getType();
        } else {
            return null;
        }
    }

    public static AnnotationDefinition build(Class annotationClass, Map<String, Object> valueMap, TypeResolver resolver) throws NoSuchMethodException {
        AnnotationDefinition annotationDefinition = new AnnotationDefinition(annotationClass.getName());
        HashMap<String,AnnotationPropertyVal> values = new HashMap<String,AnnotationPropertyVal>();
        for (String key : valueMap.keySet()) {
            AnnotationPropertyVal value = rebuild(key, annotationClass, valueMap.get(key), resolver);
            if (value != null) {
                values.put(key,value);
            }
        }
        annotationDefinition.values = Collections.unmodifiableMap(values);
        return annotationDefinition;
    }

    private static AnnotationPropertyVal rebuild(String key, Class annotationClass, Object value, TypeResolver resolver) throws NoSuchMethodException {
        if (annotationClass == Role.class) {
            return new AnnotationPropertyVal(key,
                                             Role.Type.class,
                                             value != null && value.toString().equalsIgnoreCase("event") ? Role.Type.EVENT : Role.Type.FACT,
                                             AnnotationPropertyVal.ValType.ENUMERATION);
        }

        Method prop = annotationClass.getMethod(key);
        Class returnType = prop.getReturnType();
        Object val = decode(returnType, value, resolver);
        AnnotationPropertyVal.ValType valType;

        if (returnType.isPrimitive()) {
            valType = AnnotationPropertyVal.ValType.PRIMITIVE;
        } else if (returnType.isEnum()) {
            valType = AnnotationPropertyVal.ValType.ENUMERATION;
        } else if (returnType.isArray()) {

            if (returnType.getComponentType().isEnum()) {
                valType = AnnotationPropertyVal.ValType.ENUMARRAY;
            } else if (returnType.getComponentType().isPrimitive()) {
                valType = AnnotationPropertyVal.ValType.PRIMARRAY;
            } else if (String.class.equals(returnType.getComponentType())) {
                valType = AnnotationPropertyVal.ValType.STRINGARRAY;
            } else {
                valType = AnnotationPropertyVal.ValType.CLASSARRAY;
            }

        } else if (String.class.equals(returnType)) {
            valType = AnnotationPropertyVal.ValType.STRING;
        } else {
            valType = AnnotationPropertyVal.ValType.KLASS;
        }

        return new AnnotationPropertyVal(key, returnType, val, valType);
    }

    private static Object decode( Class returnType, Object value, TypeResolver resolver ) {
        if ( returnType.isArray() ) {
            int n = Array.getLength( value );
            Class targetType = returnType.getComponentType().isAnnotation() ? AnnotationDefinition.class : returnType.getComponentType();
            Object ar = java.lang.reflect.Array.newInstance( targetType, n );
            for ( int j = 0; j < n; j++ ) {
                java.lang.reflect.Array.set(ar, j, decode( returnType.getComponentType(), Array.get( value, j ), resolver) );
            }
            return ar;
        } else if (returnType.isEnum()) {
            try {
                String valueStr = value.toString().trim();
                if (valueStr.indexOf('.') > 0) {
                    value = valueStr.substring( valueStr.lastIndexOf(".")+1);
                }
                return returnType.getMethod("valueOf",String.class).invoke(null,value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (InvocationTargetException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (NoSuchMethodException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } else if (String.class.equals(returnType)) {
            return unquote( value.toString().trim() );
        } else if (boolean.class.equals(returnType)) {
            return Boolean.valueOf( value.toString() );
        } else if (int.class.equals(returnType)) {
            return Integer.valueOf( value.toString() );
        } else if (double.class.equals(returnType)) {
            return Double.valueOf( value.toString() );
        } else if (long.class.equals(returnType)) {
            return Long.valueOf( value.toString() );
        } else if (float.class.equals(returnType)) {
            return Float.valueOf( value.toString() );
        } else if (short.class.equals(returnType)) {
            return Short.valueOf( value.toString() );
        } else if (char.class.equals(returnType)) {
            return unquote( value.toString() ).charAt(0);
        } else if (Class.class.equals(returnType)) {
            try {
                String cName = value.toString().trim().replace(".class","");
                return resolver.resolveType(cName);
            } catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
                return Object.class;
            }
        } else if (returnType.isAnnotation()) {
            try {
                return build( returnType, ((PropertyMap) value).getValues(), resolver );
            } catch ( NoSuchMethodException e ) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }



    public static String unquote( String s ){
        if( s.startsWith( "\"" ) && s.endsWith( "\"" ) ||
            s.startsWith( "'" ) && s.endsWith( "'" ) ) {
            return s.substring( 1, s.length() - 1 );
        } else {
            return s;
        }
    }







    public static class AnnotationPropertyVal implements Externalizable {

        private String property;
        private Class type;
        private Object value;

        private ValType valType;

        public static enum ValType {
            PRIMITIVE, KLASS, STRING, ENUMERATION, STRINGARRAY, ENUMARRAY, PRIMARRAY, CLASSARRAY, ANNOTATION;
        }

        public AnnotationPropertyVal() { }

        public AnnotationPropertyVal(String property, Class type, Object value, ValType valType) {
            this.property = property;
            this.type = type;
            this.value = value;
            this.valType = valType;
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            this.property = (String) in.readObject();
            this.type = (Class) in.readObject();
            this.value = in.readObject();
            this.valType = (ValType) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( this.property );
            out.writeObject( this.type );
            out.writeObject( value );
            out.writeObject( valType );
        }

        @Override
        public String toString() {
            return "PropertyVal{" +
                    "property='" + property + '\'' +
                    ", type=" + type +
                    ", value=" + value +
                    ", valType=" + valType +
                    '}';

        }

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }

        public Class getType() {
            return type;
        }

        public void setType(Class type) {
            this.type = type;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public ValType getValType() {
            return valType;
        }

        public void setValType(ValType valType) {
            this.valType = valType;
        }
    }
}


