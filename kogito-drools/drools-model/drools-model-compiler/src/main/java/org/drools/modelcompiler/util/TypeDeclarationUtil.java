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

import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.modelcompiler.constraints.MvelReadAccessor;
import org.kie.api.definition.type.Duration;
import org.kie.api.definition.type.Timestamp;

import static org.drools.core.rule.TypeDeclaration.createTypeDeclarationForBean;

public class TypeDeclarationUtil {

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

    public static void wireDurationAccessor( String durationField, TypeDeclaration type, InternalKnowledgePackage pkg ) {
        type.setDurationAttribute(durationField);
        type.setDurationExtractor(getFieldExtractor( type, durationField, pkg, long.class ));
    }

    public static void wireTimestampAccessor( String timestampField, TypeDeclaration type, InternalKnowledgePackage pkg ) {
        type.setTimestampAttribute(timestampField);
        type.setTimestampExtractor(getFieldExtractor( type, timestampField, pkg, long.class ));
    }

    private static InternalReadAccessor getFieldExtractor( TypeDeclaration type, String field, InternalKnowledgePackage pkg, Class<?> returnType ) {
        return new MvelReadAccessor( type.getTypeClass(), returnType, field );
    }
}
