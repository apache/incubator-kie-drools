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

import java.util.Map;

import org.drools.core.base.evaluators.TimeIntervalParser;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.model.AnnotationValue;
import org.drools.model.TypeMetaData;
import org.drools.modelcompiler.constraints.MvelReadAccessor;
import org.kie.api.definition.type.Duration;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

import static org.drools.core.rule.TypeDeclaration.createTypeDeclarationForBean;

public class TypeDeclarationUtil {

    public static TypeDeclaration createTypeDeclaration( KnowledgePackageImpl pkg, TypeMetaData metaType ) {
        try {
            Class<?> typeClass = pkg.getTypeResolver().resolveType( metaType.getPackage() + "." + metaType.getName() );
            TypeDeclaration typeDeclaration = createTypeDeclarationForBean( typeClass );
            for (Map.Entry<String, AnnotationValue[]> ann :  metaType.getAnnotations().entrySet()) {
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
                                wireDurationAccessor( annVal.getValue().toString(), typeDeclaration, pkg );
                            }
                        }
                        break;
                    case "timestamp":
                        for (AnnotationValue annVal : ann.getValue()) {
                            if (annVal.getKey().equals( "value" )) {
                                wireTimestampAccessor( annVal.getValue().toString(), typeDeclaration, pkg );
                            }
                        }
                        break;
                    case "expires":
                        for (AnnotationValue annVal : ann.getValue()) {
                            if (annVal.getKey().equals( "value" )) {
                                long offset = TimeIntervalParser.parseSingle( annVal.getValue().toString() );
                                typeDeclaration.setExpirationOffset(offset == -1L ? Long.MAX_VALUE : offset);
                            } else if (annVal.getKey().equals( "policy" )) {
                                typeDeclaration.setExpirationType( Enum.valueOf( Expires.Policy.class, annVal.getValue().toString() ) );
                            }
                        }
                        break;
                    default:
                        throw new UnsupportedOperationException("Unknown annotation: " + ann.getKey());
                }
            }
            return typeDeclaration;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException( e );
        }
    }

    public static TypeDeclaration createTypeDeclaration(InternalKnowledgePackage pkg, Class<?> cls) {
        TypeDeclaration typeDeclaration = createTypeDeclarationForBean(cls);

        Duration duration = cls.getAnnotation( Duration.class );
        if (duration != null) {
            wireDurationAccessor( duration.value(), typeDeclaration, pkg );
        }
        Timestamp timestamp = cls.getAnnotation( Timestamp.class );
        if (timestamp != null) {
            wireDurationAccessor( timestamp.value(), typeDeclaration, pkg );
        }

        return typeDeclaration;
    }

    private static void wireDurationAccessor( String durationField, TypeDeclaration type, InternalKnowledgePackage pkg ) {
        type.setDurationAttribute(durationField);
        type.setDurationExtractor(getFieldExtractor( type, durationField, pkg, long.class ));
    }

    private static void wireTimestampAccessor( String timestampField, TypeDeclaration type, InternalKnowledgePackage pkg ) {
        type.setTimestampAttribute(timestampField);
        type.setTimestampExtractor(getFieldExtractor( type, timestampField, pkg, long.class ));
    }

    private static InternalReadAccessor getFieldExtractor( TypeDeclaration type, String field, InternalKnowledgePackage pkg, Class<?> returnType ) {
        return new MvelReadAccessor( type.getTypeClass(), returnType, field );
    }
}
