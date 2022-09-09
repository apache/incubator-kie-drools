/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.addon.cloudevents.quarkus.deployment;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget.Kind;
import org.jboss.jandex.DotName;
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.Type;
import org.kie.kogito.event.EventEmitter;
import org.kie.kogito.event.EventReceiver;

import io.quarkus.arc.deployment.AnnotationsTransformerBuildItem;
import io.quarkus.arc.processor.AnnotationsTransformer;
import io.quarkus.arc.processor.DotNames;
import io.quarkus.deployment.annotations.BuildStep;

public class KogitoProcessMessagingProcessor {

    private static class MessagingAnnotationTransfomer implements AnnotationsTransformer {
        private Map<DotName, DotName> fieldMapping;
        private Map<DotName, DotName> classMapping;

        public MessagingAnnotationTransfomer(Map<DotName, DotName> fieldMapping, Map<DotName, DotName> classMapping) {
            this.fieldMapping = fieldMapping;
            this.classMapping = classMapping;
        }

        @Override
        public boolean appliesTo(Kind kind) {
            return kind == Kind.FIELD || kind == Kind.CLASS;
        }

        @Override
        public void transform(TransformationContext ctx) {
            if (ctx.isField()) {
                boolean found = false;
                for (AnnotationInstance annotation : ctx.getAnnotations()) {
                    if (annotation.name().equals(DotNames.INJECT)) {
                        found = true;
                        break;
                    }
                }
                FieldInfo field = ctx.getTarget().asField();
                Type type = field.type();
                if (found && type.kind() == Type.Kind.CLASS) {
                    String className = type.name().toString();
                    if (className.equals(EventEmitter.class.getName()) || className.equals(EventReceiver.class.getName())) {
                        addAnnotation(fieldMapping, field.declaringClass().name(), ctx);
                    }
                }
            } else if (ctx.isClass()) {
                addAnnotation(classMapping, ctx.getTarget().asClass().name(), ctx);
            }
        }

        private static void addAnnotation(Map<DotName, DotName> mapping, DotName name, TransformationContext ctx) {
            DotName annotation = mapping.get(name);
            if (annotation != null) {
                ctx.transform().add(annotation).done();
            }
        }
    }

    @BuildStep
    AnnotationsTransformerBuildItem annotate(KogitoMessagingMetadataBuildItem messagingMetadata) {

        Map<DotName, DotName> fieldMapping = new HashMap<>();
        Map<DotName, DotName> classMapping = new HashMap<>();
        for (Entry<DotName, EventGenerator> entry : messagingMetadata.generators().entrySet()) {
            DotName annotationName = DotNamesHelper.createAnnotationName(entry.getValue());
            fieldMapping.put(entry.getKey(), annotationName);
            classMapping.put(DotNamesHelper.createClassName(entry.getValue()), annotationName);
        }
        return new AnnotationsTransformerBuildItem(new MessagingAnnotationTransfomer(fieldMapping, classMapping));
    }
}
