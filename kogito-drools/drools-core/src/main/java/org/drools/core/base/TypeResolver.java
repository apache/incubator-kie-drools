/*
 * Copyright 2010 JBoss Inc
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

package org.drools.core.base;

import java.lang.annotation.Annotation;
import java.util.Set;

public interface TypeResolver {
    Set<String> getImports();

    void addImport(String importEntry);

    Class<?> resolveType(String className) throws ClassNotFoundException;

    Class<?> resolveType(String className, ClassFilter classFilter) throws ClassNotFoundException;

    /**
     * This will return the fully qualified type name (including the namespace).
     * Eg, if it was a pojo org.drools.core.test.model.Cheese, then if you passed in "Cheese" you should get back
     * "org.drools.core.test.model.Cheese"
     */
    String getFullTypeName(String shortName) throws ClassNotFoundException;

    ClassLoader getClassLoader();

    interface ClassFilter {
        boolean accept(Class<?> clazz);
    }

    public static AcceptAllClassFilter ACCEPT_ALL_CLASS_FILTER = new AcceptAllClassFilter();
    public static class AcceptAllClassFilter implements ClassFilter {
        @Override
        public boolean accept(Class<?> clazz) {
            return true;
        }
    }

    public static ExcludeAnnotationClassFilter EXCLUDE_ANNOTATION_CLASS_FILTER = new ExcludeAnnotationClassFilter();
    public static class ExcludeAnnotationClassFilter implements ClassFilter {
        @Override
        public boolean accept(Class<?> clazz) {
            return !Annotation.class.isAssignableFrom(clazz);
        }
    }

    public static OnlyAnnotationClassFilter ONLY_ANNOTATION_CLASS_FILTER = new OnlyAnnotationClassFilter();
    public static class OnlyAnnotationClassFilter implements ClassFilter {
        @Override
        public boolean accept(Class<?> clazz) {
            return Annotation.class.isAssignableFrom(clazz);
        }
    }
}
