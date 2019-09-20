/*
 * Copyright 2005 JBoss Inc
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

package org.drools.modelcompiler.util;

import java.lang.reflect.Method;
import java.util.Map;

import org.drools.core.base.evaluators.TimeIntervalParser;
import org.drools.core.factmodel.ClassDefinition;
import org.drools.core.factmodel.FieldDefinition;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.util.ClassUtils;
import org.drools.model.AnnotationValue;
import org.drools.model.TypeMetaData;
import org.drools.modelcompiler.constraints.MvelReadAccessor;
import org.kie.api.definition.type.Duration;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

import static org.drools.core.rule.TypeDeclaration.createTypeDeclarationForBean;
import static org.drools.core.util.ClassUtils.getter2property;

public class TypeDeclarationUtil {

    public static TypeDeclaration createTypeDeclaration(TypeMetaData metaType) {
        Class<?> typeClass = metaType.getType();

        TypeDeclaration typeDeclaration = createTypeDeclarationForBean( typeClass );
        typeDeclaration.setTypeClassDef( new ClassDefinitionForModel( typeClass ) );

        wireClassAnnotations( typeClass, typeDeclaration );
        wireMetaTypeAnnotations( metaType, typeDeclaration );

        return typeDeclaration;
    }

    private static void wireMetaTypeAnnotations( TypeMetaData metaType, TypeDeclaration typeDeclaration ) {
        for (Map.Entry<String, AnnotationValue[]> ann : metaType.getAnnotations().entrySet()) {
            switch (ann.getKey()) {
                case "role":
                    for (AnnotationValue annVal : ann.getValue()) {
                        if (annVal.getKey().equals( "value" ) && annVal.getValue().equals( "event" )) {
                            typeDeclaration.setRole( Role.Type.EVENT );
                        }
                    }
                    break;
                case "duration":
                    for (AnnotationValue annVal : ann.getValue()) {
                        if (annVal.getKey().equals( "value" )) {
                            wireDurationAccessor( annVal.getValue().toString(), typeDeclaration );
                        }
                    }
                    break;
                case "timestamp":
                    for (AnnotationValue annVal : ann.getValue()) {
                        if (annVal.getKey().equals( "value" )) {
                            wireTimestampAccessor( annVal.getValue().toString(), typeDeclaration );
                        }
                    }
                    break;
                case "expires":
                    for (AnnotationValue annVal : ann.getValue()) {
                        if (annVal.getKey().equals( "value" )) {
                            long offset = TimeIntervalParser.parseSingle( annVal.getValue().toString() );
                            typeDeclaration.setExpirationOffset(offset == -1L ? Long.MAX_VALUE : offset);
                            typeDeclaration.setExpirationType( Expires.Policy.TIME_HARD );
                        } else if (annVal.getKey().equals( "policy" )) {
                            typeDeclaration.setExpirationType( Enum.valueOf( Expires.Policy.class, annVal.getValue().toString() ) );
                        }
                    }
                    break;
                case "propertyReactive":
                    typeDeclaration.setPropertyReactive( true );
                    break;
                case "classReactive":
                    typeDeclaration.setPropertyReactive( false );
                    break;
            }
        }
    }

    private static void wireClassAnnotations( Class<?> typeClass, TypeDeclaration typeDeclaration ) {
        Duration duration = typeClass.getAnnotation( Duration.class );
        if (duration != null) {
            wireDurationAccessor( duration.value(), typeDeclaration );
        }

        Timestamp timestamp = typeClass.getAnnotation( Timestamp.class );
        if (timestamp != null) {
            wireTimestampAccessor( timestamp.value(), typeDeclaration );
        }
    }

    public static TypeDeclaration createTypeDeclaration(Class<?> cls) {
        TypeDeclaration typeDeclaration = createTypeDeclarationForBean( cls );

        Duration duration = cls.getAnnotation( Duration.class );
        if (duration != null) {
            wireDurationAccessor( duration.value(), typeDeclaration );
        }
        Timestamp timestamp = cls.getAnnotation( Timestamp.class );
        if (timestamp != null) {
            wireTimestampAccessor( timestamp.value(), typeDeclaration );
        }

        return typeDeclaration;
    }

    private static void wireDurationAccessor( String durationField, TypeDeclaration type ) {
        type.setDurationAttribute(durationField);
        type.setDurationExtractor(getFieldExtractor( type, durationField, long.class ));
    }

    private static void wireTimestampAccessor( String timestampField, TypeDeclaration type ) {
        type.setTimestampAttribute(timestampField);
        type.setTimestampExtractor(getFieldExtractor( type, timestampField, long.class ));
    }

    private static InternalReadAccessor getFieldExtractor( TypeDeclaration type, String field, Class<?> returnType ) {
        return new MvelReadAccessor( type.getTypeClass(), returnType, field );
    }

    public static class ClassDefinitionForModel extends ClassDefinition {

        public ClassDefinitionForModel( Class<?> cls ) {
            super( cls );
            processFields();
        }

        /**
         * Do not use this constructor. It should be used just by deserialization.
         */
        public ClassDefinitionForModel() {
        }

        public void processFields() {
            for (Method m : getDefinedClass().getDeclaredMethods()) {
                if (m.getParameterCount() == 0) {
                    String fieldName = getter2property(m.getName());
                    if (fieldName != null) {
                        addField( new FieldDefinition( fieldName, m.getReturnType().getCanonicalName() ) );
                    }
                }
            }
        }

        @Override
        public Object get(Object bean, String field) {
            java.lang.reflect.Field f = ClassUtils.getField(getDefinedClass(), field );
            if (f != null) {
                f.setAccessible( true );
                try {
                    return f.get( bean );
                } catch (IllegalAccessException e) {
                    throw new RuntimeException( e );
                }
            }
            return null;
        }

        @Override
        public void set(Object bean, String field, Object value) {
            java.lang.reflect.Field f = ClassUtils.getField( getDefinedClass(), field );
            if (f != null) {
                f.setAccessible( true );
                try {
                    f.set( bean, value );
                } catch (IllegalAccessException e) {
                    throw new RuntimeException( e );
                }
            }
        }
    }

    private TypeDeclarationUtil() {
        // It si not allowed to create instances of util classes.
    }
}
