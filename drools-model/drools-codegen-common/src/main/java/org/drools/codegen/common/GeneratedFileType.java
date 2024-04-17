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
package org.drools.codegen.common;

import java.util.Objects;

/**
 * Interface to represent a type of GeneratedFile and specify how the type should be handled. It allows the definition of custom
 * types using one of the factory method {@link GeneratedFileType#of(String, Category, boolean)}.
 * It also provides default reusable instances for generic types {@link Category#SOURCE}, {@link Category#INTERNAL_RESOURCE}
 * , {@link Category#STATIC_HTTP_RESOURCE} and {@link Category#COMPILED_CLASS}
 */
public interface GeneratedFileType {

    GeneratedFileType SOURCE = of(Category.SOURCE);
    GeneratedFileType INTERNAL_RESOURCE = of(Category.INTERNAL_RESOURCE);
    GeneratedFileType STATIC_HTTP_RESOURCE = of(Category.STATIC_HTTP_RESOURCE);
    GeneratedFileType COMPILED_CLASS = of(Category.COMPILED_CLASS);

    GeneratedFileType RULE = of("RULE", Category.SOURCE);

    GeneratedFileType CONFIG = of("CONFIG", Category.SOURCE);
    GeneratedFileType DECLARED_TYPE = of("DECLARED_TYPE", Category.SOURCE);

    GeneratedFileType REST = of("REST", Category.SOURCE, true, true);

    String name();

    Category category();

    boolean canHotReload();

    boolean isCustomizable();

    enum Category {

        /**
         * Represent a Java source file
         */
        SOURCE,
        /**
         * Represent a cp resource automatically generated during codegen, so after generate-resources maven phase.
         * This means to add it to target/classes both for Quarkus or using kogito-maven-plugin (SB). For additional
         * information see {@link org.drools.codegen.common.GeneratedFileWriter#write(GeneratedFile)}
         * For Quarkus it will be subject of GeneratedResourceBuildItem and NativeImageResourceBuildItem too
         */
        INTERNAL_RESOURCE,
        /**
         * a resource file to be published as a static file to a web server. It will be automatically placed under META-INF/resources/
         * so you don't need to prefix its path with "META-INF/resources/"
         * For Quarkus it will be subject of GeneratedResourceBuildItem, NativeImageResourceBuildItem and AdditionalStaticResourceBuildItem
         * so it could be served without Servlet dependency
         */
        STATIC_HTTP_RESOURCE,
        /**
         * Represent a class file (Java compiled file)
         */
        COMPILED_CLASS;
    }

    static GeneratedFileType of(Category category) {
        Objects.requireNonNull(category, "category cannot be null");
        return of(category.name(), category);
    }

    static GeneratedFileType of(String name, Category category) {
        return of(name, category, true);
    }

    static GeneratedFileType of(String name, Category category, boolean canHotReload) {
        return new StaticGeneratedFileType(name, category, canHotReload, false);
    }

    static GeneratedFileType of(String name, Category category, boolean canHotReload, boolean customizable) {
        return new StaticGeneratedFileType(name, category, canHotReload, customizable);
    }

    class StaticGeneratedFileType implements GeneratedFileType {

        private final String name;
        private final Category category;
        private final boolean canHotReload;
        private final boolean customizable;

        private StaticGeneratedFileType(String name, Category category, boolean canHotReload, boolean customizable) {
            Objects.requireNonNull(name, "name cannot be null");
            Objects.requireNonNull(category, "category cannot be null");
            this.customizable = customizable;
            this.name = name;
            this.category = category;
            this.canHotReload = canHotReload;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public Category category() {
            return category;
        }

        @Override
        public boolean canHotReload() {
            return canHotReload;
        }

        @Override
        public boolean isCustomizable() {
            return customizable;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof GeneratedFileType))
                return false;
            GeneratedFileType that = (GeneratedFileType) o;
            return canHotReload == that.canHotReload() && name.equals(that.name()) && category == that.category() && customizable == that.isCustomizable();
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, category, canHotReload, customizable);
        }

        @Override
        public String toString() {
            return "GeneratedFileType{" +
                    "name='" + name + '\'' +
                    ", category=" + category +
                    ", canHotReload=" + canHotReload +
                    '}';
        }
    }
}
