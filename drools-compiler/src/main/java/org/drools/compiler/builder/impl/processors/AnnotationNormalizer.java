/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.compiler.builder.impl.processors;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import org.drools.compiler.compiler.AnnotationDeclarationError;
import org.drools.drl.ast.descr.AnnotatedBaseDescr;
import org.drools.drl.ast.descr.AnnotationDescr;
import org.drools.util.TypeResolver;
import org.kie.internal.builder.KnowledgeBuilderResult;

import static org.drools.util.StringUtils.ucFirst;

public abstract class AnnotationNormalizer {
    protected final TypeResolver typeResolver;
    protected final Collection<KnowledgeBuilderResult> results;
    AnnotationNormalizer(TypeResolver typeResolver) {
        this.typeResolver = typeResolver;
        this.results = new ArrayList<>();
    }

    public static AnnotationNormalizer of(TypeResolver typeResolver, boolean isStrict) {
        if (isStrict) {
            return new AnnotationNormalizer.Strict(typeResolver);
        }
        else {
            return new AnnotationNormalizer.NonStrict(typeResolver);
        }
    }

    abstract boolean isStrict();

    abstract AnnotationDescr doNormalize(AnnotationDescr descr);

    public void normalize(AnnotatedBaseDescr annotationsContainer) {
        for (AnnotationDescr annotationDescr : annotationsContainer.getAnnotations()) {
            annotationDescr.setResource(annotationsContainer.getResource());
            annotationDescr.setStrict(isStrict());
            if (annotationDescr.isDuplicated()) {
                this.results.add(new AnnotationDeclarationError(annotationDescr,
                        "Duplicated annotation: " + annotationDescr.getName()));
            }
            doNormalize(annotationDescr);
        }
        annotationsContainer.indexByFQN(isStrict());
    }

    public Collection<KnowledgeBuilderResult> getResults() {
        return results;
    }

    static class Strict extends AnnotationNormalizer {
        public Strict(TypeResolver typeResolver) {
            super(typeResolver);
        }

        @Override
        boolean isStrict() {
            return true;
        }

        AnnotationDescr doNormalize(AnnotationDescr annotationDescr) {
            try {
                Class<?> annotationClass = typeResolver.resolveType(annotationDescr.getName(), TypeResolver.ONLY_ANNOTATION_CLASS_FILTER);
                annotationDescr.setFullyQualifiedName(annotationClass.getCanonicalName());
                return annotationDescr;
            } catch (ClassNotFoundException | NoClassDefFoundError e) {
                this.results.add(new AnnotationDeclarationError(annotationDescr,
                        "Unknown annotation: " + annotationDescr.getName()));
            }
            return null;
        }
    }

    static class NonStrict extends AnnotationNormalizer {

        public NonStrict(TypeResolver typeResolver) {
            super(typeResolver);
        }

        @Override
        boolean isStrict() {
            return false;
        }

        AnnotationDescr doNormalize(AnnotationDescr annotationDescr) {
            Class<?> annotationClass = null;
            try {
                annotationClass = typeResolver.resolveType(annotationDescr.getName(), TypeResolver.ONLY_ANNOTATION_CLASS_FILTER);
            } catch (ClassNotFoundException | NoClassDefFoundError e) {
                String className = normalizeAnnotationNonStrictName(annotationDescr.getName());
                try {
                    annotationClass = typeResolver.resolveType(className, TypeResolver.ONLY_ANNOTATION_CLASS_FILTER);
                } catch (ClassNotFoundException | NoClassDefFoundError e1) {
                    // non-strict annotation, ignore error
                }
            }
            if (annotationClass != null) {
                annotationDescr.setFullyQualifiedName(annotationClass.getCanonicalName());

                for (String key : annotationDescr.getValueMap().keySet()) {
                    try {
                        Method m = annotationClass.getMethod(key);
                        Object val = annotationDescr.getValue(key);
                        if (val instanceof Object[] && !m.getReturnType().isArray()) {
                            this.results.add(new AnnotationDeclarationError(annotationDescr,
                                    "Wrong cardinality on property " + key));
                            return annotationDescr;
                        }
                        if (m.getReturnType().isArray() && !(val instanceof Object[])) {
                            val = new Object[]{val};
                            annotationDescr.setKeyValue(key, val);
                        }

                        if (m.getReturnType().isArray()) {
                            int n = Array.getLength(val);
                            for (int j = 0; j < n; j++) {
                                if (Class.class.equals(m.getReturnType().getComponentType())) {
                                    String className = Array.get(val, j).toString().replace(".class", "");
                                    Array.set(val, j, typeResolver.resolveType(className).getName() + ".class");
                                } else if (m.getReturnType().getComponentType().isAnnotation()) {
                                    Array.set(val, j, doNormalize((AnnotationDescr) Array.get(val, j)));
                                }
                            }
                        } else {
                            if (Class.class.equals(m.getReturnType())) {
                                String className = annotationDescr.getValueAsString(key).replace(".class", "");
                                annotationDescr.setKeyValue(key, typeResolver.resolveType(className));
                            } else if (m.getReturnType().isAnnotation()) {
                                annotationDescr.setKeyValue(key,
                                        doNormalize((AnnotationDescr) annotationDescr.getValue(key)));
                            }
                        }
                    } catch (NoSuchMethodException e) {
                        this.results.add(new AnnotationDeclarationError(annotationDescr,
                                "Unknown annotation property " + key));
                    } catch (ClassNotFoundException | NoClassDefFoundError e) {
                        this.results.add(new AnnotationDeclarationError(annotationDescr,
                                "Unknown class " + annotationDescr.getValue(key) + " used in property " + key +
                                        " of annotation " + annotationDescr.getName()));
                    }
                }
            }
            return annotationDescr;
        }


        private String normalizeAnnotationNonStrictName(String name) {
            if ("typesafe".equalsIgnoreCase(name)) {
                return "TypeSafe";
            }
            return ucFirst(name);
        }
    }
}
